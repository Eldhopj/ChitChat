package in.eldhopj.chitchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import in.eldhopj.chitchat.ModelClass.AccountSettings;
import in.eldhopj.chitchat.others.TimeAgo;

import static in.eldhopj.chitchat.others.Common.CONVERSATION;
import static in.eldhopj.chitchat.others.Common.LAST_SEEN;
import static in.eldhopj.chitchat.others.Common.ONLINE;
import static in.eldhopj.chitchat.others.Common.USERS;
import static in.eldhopj.chitchat.others.Common.mAuth;
import static in.eldhopj.chitchat.others.Common.mUserDb;
import static in.eldhopj.chitchat.others.Common.rootReference;

public class ConversationActivity extends AppCompatActivity {
    private static final String TAG = "ConversationActivity";
    String chatUserID;
    Boolean online=false;
    String userName;
    String lastSeen;
    String thumbnailUrl;
    TextView onlineStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        onlineStatus = findViewById(R.id.lastSeenConvo);

        Intent intent = getIntent();
        AccountSettings clickedItem = intent.getParcelableExtra(CONVERSATION);
        userName = clickedItem.getName();
        thumbnailUrl = clickedItem.getThumbnail();

        chatUserID = clickedItem.getUserId();

        setToolbar(userName,thumbnailUrl);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUserDb.child(ONLINE).setValue(true);
        rootReference.child(USERS).child(chatUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              lastSeen =  dataSnapshot.child(LAST_SEEN).getValue().toString();
              if (dataSnapshot.hasChild(ONLINE)) {
                  online = (Boolean) dataSnapshot.child(ONLINE).getValue();
              }
              if (online){
                    onlineStatus.setText("Online");
                }
                else
                {
                    long lastSeenTime = Long.parseLong(lastSeen);
                    String lastSeenStatus = TimeAgo.getTimeAgo(lastSeenTime,getApplicationContext());
                    onlineStatus.setText(lastSeenStatus);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**Setup the values in the Toolbar*/
    private void setToolbar(String userName, String thumbnailUrl) {
        CircleImageView profilePic = findViewById(R.id.iconPic);
        TextView chatName = findViewById(R.id.chatName);
        if (thumbnailUrl != null)
        { Picasso.get().load(thumbnailUrl).placeholder(R.drawable.profilepic).into(profilePic); }

        chatName.setText(userName);

        Toolbar mainToolbar = findViewById(R.id.toolbar_chat);
        setSupportActionBar(mainToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
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
