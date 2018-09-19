package in.eldhopj.chitchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import in.eldhopj.chitchat.Adapters.ChatAdapter;
import in.eldhopj.chitchat.ModelClass.AccountSettings;
import in.eldhopj.chitchat.ModelClass.Conversations;
import in.eldhopj.chitchat.others.TimeAgo;

import static in.eldhopj.chitchat.others.Common.CHAT;
import static in.eldhopj.chitchat.others.Common.CONVERSATION;
import static in.eldhopj.chitchat.others.Common.FROM;
import static in.eldhopj.chitchat.others.Common.LAST_SEEN;
import static in.eldhopj.chitchat.others.Common.MESSAGE;
import static in.eldhopj.chitchat.others.Common.ONLINE;
import static in.eldhopj.chitchat.others.Common.SEEN;
import static in.eldhopj.chitchat.others.Common.TIMESTAMP;
import static in.eldhopj.chitchat.others.Common.TYPE;
import static in.eldhopj.chitchat.others.Common.USERS;
import static in.eldhopj.chitchat.others.Common.mAuth;
import static in.eldhopj.chitchat.others.Common.mUserDb;
import static in.eldhopj.chitchat.others.Common.rootReference;
import static in.eldhopj.chitchat.others.Common.uID;

public class ConversationActivity extends AppCompatActivity {
    private static final String TAG = "ConversationActivity";
    String chatUserID;
    Boolean online=false;
    String userName;
    String lastSeen;
    String thumbnailUrl;
    TextView onlineStatus;
    EditText messageBoxEt;

    private RecyclerView mRecyclerView;
    private List<Conversations> mListItems;
    private ChatAdapter mChatAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        onlineStatus = findViewById(R.id.lastSeenConvo);
        messageBoxEt = findViewById(R.id.messageBox);
        mListItems = new ArrayList<>();

        Intent intent = getIntent();
        AccountSettings clickedItem = intent.getParcelableExtra(CONVERSATION);
        userName = clickedItem.getName();
        thumbnailUrl = clickedItem.getThumbnail();

        chatUserID = clickedItem.getUserId();

        setToolbar(userName,thumbnailUrl);
        initRecyclerView();
        loadMessages();

        final DatabaseReference chatRef = rootReference.child(CHAT).child(uID);
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(chatUserID)){

                    HashMap<String,Object> chat = new HashMap<>();
                    chat.put(TIMESTAMP,ServerValue.TIMESTAMP);
                    chat.put(SEEN,false);


                    chatRef.child(chatUserID).setValue(chat);
                    rootReference.child(CHAT).child(chatUserID).child(uID).setValue(chat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ConversationActivity.this, "Error :"+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

    public void sendMessage(View view) {
        final EditText messageBoxEt = findViewById(R.id.messageBox);
        String message = messageBoxEt.getText().toString();
        Boolean seen = false;
        String type = "text";
        DatabaseReference messageReference = rootReference.child(MESSAGE).child(chatUserID).child(uID).push();
        String pushId = messageReference.getKey();
        if (message.isEmpty()) {
            messageBoxEt.setError("Empty Message");
            messageBoxEt.requestFocus();
            return;
        }
        HashMap<String,Object> conversation = new HashMap<>();
        conversation.put(MESSAGE,message);
        conversation.put(SEEN,seen);
        conversation.put(TYPE,type);
        conversation.put(TIMESTAMP,ServerValue.TIMESTAMP);
        conversation.put(FROM,uID);

        HashMap<String,Object> sendMessage = new HashMap<>();
        sendMessage.put("message/"+uID+"/"+chatUserID+"/"+pushId,conversation);
        sendMessage.put("message/"+chatUserID+"/"+uID+"/"+pushId,conversation);

        messageBoxEt.setText("");

        rootReference.updateChildren(sendMessage).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ConversationActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

   private void loadMessages(){
        rootReference.child(MESSAGE).child(uID).child(chatUserID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Conversations conversations = dataSnapshot.getValue(Conversations.class);
                mListItems.add(mListItems.size(),conversations);
                mChatAdapter.notifyItemInserted(mListItems.size());
                mRecyclerView.scrollToPosition(mListItems.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

   private void initRecyclerView(){

       LinearLayoutManager layoutManager;
       mRecyclerView = findViewById(R.id.recyclerView);
       mRecyclerView.setHasFixedSize(true); // setting it to true allows some optimization to our view , avoiding validations when mRecyclerAdapter content changes

       layoutManager  = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false); //it can be GridLayoutManager or StaggeredGridLayoutManager
       layoutManager.setStackFromEnd(true);
       mRecyclerView.setLayoutManager(layoutManager);

       mChatAdapter = new ChatAdapter(mListItems, this);
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL)); // Divider decorations
       mRecyclerView.setAdapter(mChatAdapter);
   }
}
