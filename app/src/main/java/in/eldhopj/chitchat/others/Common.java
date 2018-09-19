package in.eldhopj.chitchat.others;

import android.app.Application;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

public class Common extends Application {
    private static final String TAG = "Common";
    public static FirebaseAuth mAuth;
    public static FirebaseUser firebaseUser; // Gets all user associated info when signing
    public static  String uID;
    public static DatabaseReference rootReference;
    public static StorageReference storageRootReference;
    public static DatabaseReference mUserDb;

    public static final String USERS = "users";
        public static final String PHONE_NUMBER = "phoneNum";
        public static final String NAME = "name";
        public static final String STATUS ="status";
        public static final String LAST_SEEN = "lastSeen";
        public static final String ONLINE ="online";
        //Common for both Storage and the DB for saving the download link in db
        public static final String PROFILE_PICS = "profile_pics";
        public static final String THUMBNAIL = "thumbnail";
    public static final String CHAT = "chat";
    public static final String MESSAGE = "message";
        public static final String SEEN = "seen";
        public static final String TYPE = "type";
        public static final String TIMESTAMP = "timeStamp";
        public static final String FROM = "from";

    //Intent
    public static final String PROFILE_ITEMS = "profile";
    public static final String CONVERSATION = "conversation";

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        rootReference = FirebaseDatabase.getInstance().getReference();
        storageRootReference = FirebaseStorage.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        firebaseUser =  mAuth.getCurrentUser();
        if (firebaseUser != null) {
            uID = firebaseUser.getUid();
            mUserDb = rootReference.child(USERS).child(uID);

            //--------------------Presence System-------------------------------
            final DatabaseReference mUserDb = rootReference.child(USERS).child(uID);
            mUserDb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    mUserDb.child(ONLINE).onDisconnect().setValue(false);
                    //         mUserDb.child(ONLINE).setValue(true);
                    mUserDb.child(LAST_SEEN).onDisconnect().setValue(ServerValue.TIMESTAMP);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }

        //-------------------Picasso offline capabilities------------------
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
    }
}
