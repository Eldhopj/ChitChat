package in.eldhopj.chitchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.database.DatabaseReference;

import in.eldhopj.chitchat.ModelClass.AccountSettings;
import in.eldhopj.chitchat.others.SharedPrefsManager;

import static in.eldhopj.chitchat.others.Common.rootReference;
import static in.eldhopj.chitchat.others.Common.uID;
import static in.eldhopj.chitchat.others.Common.users;

/**For setting up name status and other user things*/
public class AccountSettingsActivity extends AppCompatActivity {
    private TextInputLayout nameTIL,statusTIL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        nameTIL = findViewById(R.id.name);
        statusTIL = findViewById(R.id.status);
    }

    // Saving into shared prefs
    public void saveSettings(View view) {
        String status = nameTIL.getEditText().getText().toString();
        String name = statusTIL.getEditText().getText().toString();

        AccountSettings settings = new AccountSettings(name,status);
        DatabaseReference mUserDb= rootReference.child(users).child(uID);
        mUserDb.setValue(settings);
        SharedPrefsManager.getInstance(getApplicationContext()).saveSettings(settings);

        mainActivityIntent();
    }


    public void skip(View view) {
        mainActivityIntent();
    }

    private void mainActivityIntent(){
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }

    //NOTE : Use below code only if the user get logout when login activity , usually its not necessary that's why its commented out

//    @Override
//    protected void onStart() {
//        if(firebaseUser == null){
//            loginActivityIntent();
//        }
//        super.onStart();
//    }
//
//    private void loginActivityIntent(){
//        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
//        startActivity(intent);
//        finish();
//        return;
//    }
}
