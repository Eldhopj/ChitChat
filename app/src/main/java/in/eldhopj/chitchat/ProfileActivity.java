package in.eldhopj.chitchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import in.eldhopj.chitchat.ModelClass.AccountSettings;

import static in.eldhopj.chitchat.others.Common.ONLINE;
import static in.eldhopj.chitchat.others.Common.PROFILE_ITEMS;
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
    }


}
