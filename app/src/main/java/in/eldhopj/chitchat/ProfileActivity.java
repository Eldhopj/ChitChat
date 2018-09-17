package in.eldhopj.chitchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import in.eldhopj.chitchat.ModelClass.AccountSettings;

import static in.eldhopj.chitchat.others.Common.LAST_SEEN;
import static in.eldhopj.chitchat.others.Common.ONLINE;
import static in.eldhopj.chitchat.others.Common.PROFILE_ITEMS;
import static in.eldhopj.chitchat.others.Common.mAuth;
import static in.eldhopj.chitchat.others.Common.mUserDb;

/**For to view the profile of other USERS*/
public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    TextView displayNameTv,statusTv,lastSeenTv;
    CircleImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar mainToolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(mainToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        profilePic = findViewById(R.id.circleImageView);
        displayNameTv = findViewById(R.id.displayName);
        statusTv = findViewById(R.id.status);
        lastSeenTv = findViewById(R.id.lastSeen);

        //Receiving values from the intent
        Intent intent = getIntent();
        AccountSettings clickedItem = intent.getParcelableExtra(PROFILE_ITEMS);
        displayNameTv.setText(clickedItem.getName());
        statusTv.setText("Status : "+clickedItem.getStatus());
        lastSeenTv.setText("LastSeen : "+clickedItem.getLastSeen());

        if (clickedItem.getThumbnail() != null)
            Picasso.get().load(clickedItem.getThumbnail()).placeholder(R.drawable.profilepic)
                    .into(profilePic);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUserDb.child(ONLINE).setValue(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUserDb.child(ONLINE).setValue(false);
        mUserDb.child(LAST_SEEN).setValue(ServerValue.TIMESTAMP);
    }

    // Menu Starts here
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_accountsettings,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){ // get the ID
            case R.id.action_logout_btn:
                mUserDb.child(ONLINE).setValue(false);
                mAuth.signOut();
                loginActivityIntent();
                return true;
            default:
                return false;

        }
    }

    private void loginActivityIntent(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
        startActivity(intent);
        finish();
        return;
    }
}
