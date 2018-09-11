package in.eldhopj.chitchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import in.eldhopj.chitchat.Adapters.UserListAdapter;
import in.eldhopj.chitchat.ModelClass.ListUser;
import in.eldhopj.chitchat.others.CountryIso2Phone;

import static in.eldhopj.chitchat.others.Common.NAME;
import static in.eldhopj.chitchat.others.Common.PHONE_NUMBER;
import static in.eldhopj.chitchat.others.Common.PROFILE_PICS;
import static in.eldhopj.chitchat.others.Common.USERS;
import static in.eldhopj.chitchat.others.Common.mAuth;
import static in.eldhopj.chitchat.others.Common.rootReference;

public class FindUserActivity extends AppCompatActivity {
    private static final String TAG = "FindUserActivity";
    private Toolbar mainToolbar;

    private Cursor phones;
    private ProgressDialog progressDialog;
    private RecyclerView mRecyclerView;
    private List<ListUser> mUserList, mContactList; // mUserList -> ChitChat USERS , mContactList ->Contacts in your phone
    Set<ListUser> hashSet;
    private UserListAdapter mUserListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);

        mUserList = new ArrayList<>();
        mContactList = new ArrayList<>();
        hashSet = new HashSet<>();


        progressDialog = new ProgressDialog(this,
                R.style.Theme_AppCompat_Light_Dialog_Alert);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false); // Prevents cancelling of progress bar on touching outside
        progressDialog.setMessage("Loading Users...");

        mainToolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(mainToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Chit Chat");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        initRecyclerView();
        new GetContactList().execute();

    }

    // Menu Starts here
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){ // get the ID
            case R.id.action_logout_btn:
                mAuth.signOut();
                loginActivityIntent();
                return true;
            case R.id.action_settings_btn:
                Intent intent = new Intent(getApplicationContext(), AccountSettingsActivity.class);
                startActivity(intent);
            default:
                return false;

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    // Menu ends here

    /**Fetching all saved contacts from phone*/
    //TODO : Fix this memory leak using weak reference
    private class GetContactList extends AsyncTask<Void, Void, List<ListUser>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
            progressDialog.show();
        }

        @Override
        protected List<ListUser> doInBackground(Void... voids) {
            Log.d(TAG, "doInBackground: ");
            if (phones != null) {
                while (phones.moveToNext()){ //Get contacts until when cursor cant move to next

                    String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));//Get the name of current phone you are in
                    String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));//Get the number of current phone you are in

                    phone = normalizingNumber(phone);

                    ListUser list = new ListUser(name,phone);
                    mContactList.add(list);
                }
                phones.close();
            }
            return mContactList;
        }

        @Override
        protected void onPostExecute(List<ListUser> listUsers) {
            super.onPostExecute(listUsers);
            Log.d(TAG, "onPostExecute: ");
            fetchUsers(mContactList);
        }
    }

    /**Normalizing the number by removing zero, spaces, dashes and parenthesis*/
    private String normalizingNumber(String phoneNum) {

        String isoPrefix = getCountryISO();

        if (String.valueOf(phoneNum.charAt(0)).equals("0")){ // removing zero
            phoneNum = phoneNum.substring(1);
        }
        phoneNum = phoneNum.replace("(","");// removing parenthesis
        phoneNum = phoneNum.replace(")","");// removing parenthesis
        phoneNum = phoneNum.replace(" " ,"");//removing space
        phoneNum = phoneNum.replace("-","");// removing dashes
        if (!String.valueOf(phoneNum.charAt(0)).equals("+")){
            phoneNum = isoPrefix.concat(phoneNum);
        }
        return phoneNum;
    }

    /**Get USERS from Firebase who are in phones contact */
    private void fetchUsers(final List<ListUser> mContactList) {
        Log.d(TAG, "fetchUsers: ");

        rootReference.child(USERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                progressDialog.dismiss();

                if (dataSnapshot.exists()) { // checks whether the db exists

                    /*Runs in a background thread
                    Here we want to fetch the data's of numbers only on USERS contact*/
                   new Thread(new Runnable() {
                       @Override
                       public void run() {
                           String phone;
                           String name;
                           for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                               // Declare here to make profileImageUrl as null after every iteration
                               String profileImageUrl = null;
                               for (ListUser  phoneNum : mContactList) {

                                   phone = childSnapshot.child(PHONE_NUMBER).getValue().toString();

                                   // if the contact number equals to Firebase DB number
                                   if (phone.equals(phoneNum.getPhone())) {
                                           name = childSnapshot.child(NAME).getValue().toString();

                                       if (childSnapshot.child(PROFILE_PICS).getValue() != null) // check if it is null or not, if null when we convert into string it will crash
                                           profileImageUrl = childSnapshot.child(PROFILE_PICS).getValue().toString();


                                       ListUser  users = new ListUser(name, phone,profileImageUrl);

                                       // looks through the mContactList and find the name which have empty string
                                       // ie, if user didn't give name use the phone contacts name
                                           if (name.equals("")) {
                                               for (ListUser contact : mContactList) { // Iterate through every contact
                                                   if (contact.getPhone().equals(users.getPhone())) { // If phone number matches
                                                       users.setName(contact.getName());
                                                   }
                                               }
                                           }
                                           //TODO progress dialog for loading contacts
                                           mUserList.add(users);
                                           break;
                                   }
                               }
                           }
                           runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
                                   mUserListAdapter.notifyDataSetChanged(); //Telling adapter something has been changed and update it
                               }
                           });
                       }
                   }).start();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(FindUserActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }


        });
    }

            /**Initializing recycler view*/
            private void initRecyclerView() {
                mRecyclerView = findViewById(R.id.userList);
                mRecyclerView.setHasFixedSize(true); // setting it to true allows some optimization to our view , avoiding validations when mUserListAdapter content changes

                mRecyclerView.setLayoutManager(new LinearLayoutManager(this)); //it can be GridLayoutManager or StaggeredGridLayoutManager


                hashSet.addAll(mUserList);
                mUserList.clear();
                mUserList.addAll(hashSet);

                //set the mUserListAdapter to the recycler view
                mUserListAdapter = new UserListAdapter(mUserList, this);
                mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL)); // Divider decorations
                mRecyclerView.setAdapter(mUserListAdapter);
            }

            /**To fetch the country ISO
             * This ISO will add to all the PhoneNumbers which doesn't have an country code in it*/
            private String getCountryISO(){
                String iso = null;

                TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE); // This will contain info our ISO
                if (telephonyManager != null && telephonyManager.getNetworkCountryIso() != null)
                    if (!telephonyManager.getNetworkCountryIso().equals(""))
                        iso = telephonyManager.getNetworkCountryIso();

                return CountryIso2Phone.getPhone(iso);
            }

    private void loginActivityIntent(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
        startActivity(intent);
        finish();
        return;
    }
}
