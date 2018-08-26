package in.eldhopj.chitchat;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import in.eldhopj.chitchat.Adapters.UserListAdapter;
import in.eldhopj.chitchat.ModelClass.ListUserModelClass;

public class FindUserActivity extends AppCompatActivity {
    private static final String TAG = "FindUserActivity";
    private RecyclerView mRecyclerView;
    private List<ListUserModelClass> mUserList, mContactList; // mUserList -> ChitChat users , mContactList ->Contacts in your phone
    private UserListAdapter mUserListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);

        mUserList = new ArrayList<>();
        mContactList = new ArrayList<>();

        initRecyclerView();
        getContactList();
    }

    /**Fetching all saved contacts from phone*/
    private void getContactList(){

        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
        if (phones != null) {
            while (phones.moveToNext()){ //Get contacts until when cursor cant move to next

                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));//Get the name of current phone you are in
                String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));//Get the number of current phone you are in

                phone = normalizingNumber(phone);

                ListUserModelClass list = new ListUserModelClass(name,phone);
                mContactList.add(list);
                fetchUsers(list);
            }
            phones.close();
        }

    }

    /**Normalizing the number by removing zero, spaces, dashes and parenthesis*/
    private String normalizingNumber(String phoneNum) {

        String isoPrefix = getCountryISO();

        if (String.valueOf(phoneNum.charAt(0)).equals("0")){ // removing zero
            phoneNum = phoneNum.substring(1);
        }
        phoneNum = phoneNum.replace(" " ,"");//removing space
        phoneNum = phoneNum.replace("-","");// removing dashes
        phoneNum = phoneNum.replace("(","");// removing parenthesis
        phoneNum = phoneNum.replace(")","");// removing parenthesis
        if (!String.valueOf(phoneNum.charAt(0)).equals("+")){
            phoneNum = isoPrefix.concat(phoneNum);
        }
        return phoneNum;
    }

    /**Get users from Firebase who are in phones contact */
    private void fetchUsers(ListUserModelClass list) {
        final ProgressDialog progressDialog = new ProgressDialog(this,
                R.style.Theme_AppCompat_Light_Dialog_Alert);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading Users...");
        progressDialog.show();

        DatabaseReference mUserDb = FirebaseDatabase.getInstance().getReference().child("user");

        /*Query is a way of fetching data with certain parameters
         * Here we want to fetch the data's of numbers only on users contact*/
        Query query = mUserDb.orderByChild("phone").equalTo(list.getPhone());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                progressDialog.dismiss();

                if (dataSnapshot.exists()){ // checks whether the db exists
                    String phone = "";
                    String name = "";
                    for(DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        if (childSnapshot.child("phone").getValue() != null) // check if it is null or not, if null when we convert into string it will crash
                            phone = childSnapshot.child("phone").getValue().toString();
                        if (childSnapshot.child("name").getValue() != null)
                            name = childSnapshot.child("name").getValue().toString();

                        ListUserModelClass users = new ListUserModelClass(name,phone);

                        // looks through the mContactList and find the name of the specific phone number if the name is same as phone number
                        // ie, if user didn't give name use the phone contacts name
                        if (name.equals(phone)){
                            for (ListUserModelClass contact : mContactList){ // Iterate through every contact
                                if (contact.getPhone().equals(users.getPhone())) { // If phone number matches
                                    users.setName(contact.getName());
                                }
                            }
                        }

                        mUserList.add(users);
                        mUserListAdapter.notifyDataSetChanged(); //Telling adapter something has been changed and update it
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    /**Initializing recycler view*/
    private void initRecyclerView() {
        mRecyclerView = findViewById(R.id.userList);
        mRecyclerView.setHasFixedSize(true); // setting it to true allows some optimization to our view , avoiding validations when mUserListAdapter content changes

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this)); //it can be GridLayoutManager or StaggeredGridLayoutManager

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

}
