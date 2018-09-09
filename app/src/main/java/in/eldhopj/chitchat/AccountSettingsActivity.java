package in.eldhopj.chitchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import in.eldhopj.chitchat.ModelClass.AccountSettingsSharedPrefs;
import in.eldhopj.chitchat.others.SharedPrefsManager;

import static in.eldhopj.chitchat.others.Common.profileImages;
import static in.eldhopj.chitchat.others.Common.rootReference;
import static in.eldhopj.chitchat.others.Common.storageRootReference;
import static in.eldhopj.chitchat.others.Common.uID;
import static in.eldhopj.chitchat.others.Common.users;

/**For setting up name status and other user things*/
public class AccountSettingsActivity extends AppCompatActivity {
    private TextInputLayout nameTIL,statusTIL;
    private ProgressDialog progressDialog;
    private DatabaseReference mUserDb;
    CircleImageView profilePic;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        nameTIL = findViewById(R.id.name);
        statusTIL = findViewById(R.id.status);
        profilePic = findViewById(R.id.profilePic);

        progressDialog = new ProgressDialog(this,
                R.style.Theme_AppCompat_Light_Dialog_Alert);
        progressDialog.setIndeterminate(true);


        mUserDb = rootReference.child(users).child(uID);

        mUserDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                    String name = dataSnapshot.child("name").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();

                    nameTIL.getEditText().setText(name);
                    statusTIL.getEditText().setText(status);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Saving into shared prefs and updating the values in Firebase
    public void saveSettings(View view) {
        String name = nameTIL.getEditText().getText().toString();
        String status = statusTIL.getEditText().getText().toString();

        HashMap<String,Object> settings = new HashMap<>();
        settings.put("name",name);
        settings.put("status",status);
        DatabaseReference mUserDb= rootReference.child(users).child(uID);
        progressDialog.setMessage("Saving...");
        progressDialog.show();
        //Update the fields
        mUserDb.updateChildren(settings).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    mainActivityIntent();
                }
            }
        });

        AccountSettingsSharedPrefs settingsSharedPrefs = new AccountSettingsSharedPrefs(name,status);
       SharedPrefsManager.getInstance(getApplicationContext()).saveSettings(settingsSharedPrefs);

    }


    public void skip(View view) {
        mainActivityIntent();
    }

    private void mainActivityIntent(){
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }

    public void uploadImage(View view) {

        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1) // Making image as square
                .start(this);
    }

    //To getting the cropped image  result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                profilePic.setImageURI(resultUri);

                //Uploading pic into firebase DB
                StorageReference profilePicUpload = storageRootReference.child(profileImages).child(uID+".jpg");
                progressDialog.setMessage("Uploading...");
                progressDialog.show();
                profilePicUpload.putFile(resultUri) //Give the image URI
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                if (task.isSuccessful()){
                                    progressDialog.dismiss();
                                    Toast.makeText(AccountSettingsActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
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
