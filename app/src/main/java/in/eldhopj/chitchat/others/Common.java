package in.eldhopj.chitchat.others;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Common extends Application {
    private static final String TAG = "Common";
    public static FirebaseAuth mAuth;
    public static FirebaseUser firebaseUser; // Gets all user associated info when signing
    public static  String uID;
    public static DatabaseReference rootReference;
    public static StorageReference storageRootReference;

    public static final String USERS = "users";
    public static final String PROFILE_PICS = "profile_pics";
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser =  mAuth.getCurrentUser();
        if (firebaseUser != null) {
            uID = firebaseUser.getUid();
        }

        rootReference = FirebaseDatabase.getInstance().getReference();
        storageRootReference = FirebaseStorage.getInstance().getReference();
    }

}
