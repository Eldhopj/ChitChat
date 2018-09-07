package in.eldhopj.chitchat.others;

import android.app.Application;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Common extends Application {
    private static final String TAG = "Common";
    public static FirebaseAuth mAuth;
    public static FirebaseUser firebaseUser; // Gets all user associated info when signing
    public static  String uID;
    public static DatabaseReference rootReference;

    public static final String users = "users";
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
        Log.d(TAG, "onCreate: ");
    }

}
