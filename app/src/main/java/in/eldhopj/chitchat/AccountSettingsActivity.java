package in.eldhopj.chitchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static in.eldhopj.chitchat.others.Common.NAME;
import static in.eldhopj.chitchat.others.Common.PROFILE_PICS;
import static in.eldhopj.chitchat.others.Common.STATUS;
import static in.eldhopj.chitchat.others.Common.THUMBNAIL;
import static in.eldhopj.chitchat.others.Common.USERS;
import static in.eldhopj.chitchat.others.Common.mAuth;
import static in.eldhopj.chitchat.others.Common.rootReference;
import static in.eldhopj.chitchat.others.Common.storageRootReference;
import static in.eldhopj.chitchat.others.Common.uID;

/**For setting up name status and other user things*/
public class AccountSettingsActivity extends AppCompatActivity {
    private static final String TAG = "AccountSettingsActivity";
    boolean enableToolbar=true;
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
        Button skipBtn = findViewById(R.id.skip);

        enableToolbar = getIntent().getBooleanExtra("FromLoginActivity",true);

        if (enableToolbar) {
            skipBtn.setVisibility(View.GONE);
            Toolbar mainToolbar = findViewById(R.id.toolbar_main);
            mainToolbar.setVisibility(View.VISIBLE);
            setSupportActionBar(mainToolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Account Settings");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
        }

        progressDialog = new ProgressDialog(this,
                R.style.Theme_AppCompat_Light_Dialog_Alert);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false); // Prevents cancelling of progress bar on touching outside


        mUserDb = rootReference.child(USERS).child(uID);

        mUserDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                    String name = dataSnapshot.child(NAME).getValue().toString();
                    String status = dataSnapshot.child(STATUS).getValue().toString();
                    String profileImage;
                    if (dataSnapshot.child(THUMBNAIL).getValue() != null) {// If the Db contains a URL load that url into imageView
                        profileImage = dataSnapshot.child(THUMBNAIL).getValue().toString();
                        Picasso.get().load(profileImage).placeholder(R.drawable.profilepic).into(profilePic);
                    }
                    nameTIL.getEditText().setText(name);
                    statusTIL.getEditText().setText(status);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AccountSettingsActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
            case android.R.id.home:  // For Back Navigation
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_logout_btn:
                mAuth.signOut();
                loginActivityIntent();
                return true;
            default:
                return false;

        }
    }
    // Menu ends here

    /** Saving and updating the values in Firebase*/
    public void saveSettings(View view) {
        String name = nameTIL.getEditText().getText().toString();
        String status = statusTIL.getEditText().getText().toString();

        HashMap<String,Object> settings = new HashMap<>();
        settings.put(NAME,name);
        settings.put(STATUS,status);
        DatabaseReference mUserDb= rootReference.child(USERS).child(uID);
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
    }

    public void skip(View view) {
        mainActivityIntent();
    }

    private void mainActivityIntent(){
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }

    public void uploadImage(View view) {

        /**start picker to get image for cropping and then use the image in cropping activity*/
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1) // Making image as square
                .start(this);
    }

    /**To getting the cropped image  result*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                final Uri resultUri = result.getUri();

                profilePic.setImageURI(resultUri);

                /**Uploading pics into firebase DB*/
                //Uploading original pic
                final StorageReference profilePicUpload = storageRootReference.child(PROFILE_PICS).child(uID+".jpg");

                //Uploading Thumbnail
                final StorageReference thumbnailUpload = storageRootReference.child(THUMBNAIL).child(uID+".webp");

                progressDialog.setMessage("Uploading...");
                progressDialog.show();
                profilePicUpload.putFile(resultUri) //Give the image URI
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                if (task.isSuccessful()){

                                    //If the upload is successful getting URL of the profile pic
                                    profilePicUpload.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Uri> task) {

                                           if (task.isSuccessful()) {
                                               String profileImageUrl = task.getResult().toString();

                                               //After getting the URL update the URL in firebase DB
                                               HashMap<String, Object> profileImage = new HashMap<>();
                                               profileImage.put(PROFILE_PICS, profileImageUrl);

                                               mUserDb.updateChildren(profileImage).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                   @Override
                                                   public void onComplete(@NonNull Task<Void> task) {
                                                       progressDialog.dismiss();
                                                       Toast.makeText(AccountSettingsActivity.this, "Profile Pic Updated", Toast.LENGTH_SHORT).show();
                                                   }
                                               });

                                               /*Thumbnail upload to Db*/

                                               thumbnailUpload.putBytes(convertingBitmapArray(resultUri)) //passing Bitmap byte array
                                                       .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                   @Override
                                                   public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                       if (task.isSuccessful()){
                                                           thumbnailUpload.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                               @Override
                                                               public void onComplete(@NonNull Task<Uri> task) {
                                                                   if (task.isSuccessful()){
                                                                       String thumbnailUrl = task.getResult().toString(); // URL of thumb image

                                                                       //After getting the URL update the URL in firebase DB
                                                                       HashMap<String, Object> thumbnailImage = new HashMap<>();
                                                                       thumbnailImage.put(THUMBNAIL, thumbnailUrl);

                                                                       mUserDb.updateChildren(thumbnailImage).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                           @Override
                                                                           public void onComplete(@NonNull Task<Void> task) {
                                                                               progressDialog.dismiss();
                                                                               Toast.makeText(AccountSettingsActivity.this, "Profile Pic Updated", Toast.LENGTH_SHORT).show();
                                                                           }
                                                                       });
                                                                   }
                                                               }
                                                           });
                                                       }
                                                   }
                                               });
                                           }
                                           else {
                                               Toast.makeText(AccountSettingsActivity.this, "Please Try Again Later", Toast.LENGTH_SHORT).show();
                                               progressDialog.dismiss();
                                           }
                                       }
                                   });
                                }else{
                                    Toast.makeText(AccountSettingsActivity.this, "Please Try Again Later", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            }
                        });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                progressDialog.dismiss();
                Exception error = result.getError();
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**For compressing the image*/
    private Bitmap creatingThumbnail(Uri actualImageUri){
        File actualImage = new File(actualImageUri.getPath()); // getting the file path from Uri
        Bitmap compressedImage = null;
        try {
            compressedImage = new Compressor(this)
                    .setMaxWidth(140)
                    .setMaxHeight(140)
                    .setQuality(60)
                    .setCompressFormat(Bitmap.CompressFormat.WEBP)
                    .compressToBitmap(actualImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return compressedImage;
    }

    private  byte[] convertingBitmapArray(Uri resultUri){
        Bitmap thumbnail = creatingThumbnail(resultUri);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.WEBP, 100, baos);
        byte[] thumbnailByte = baos.toByteArray();
        return thumbnailByte;
    }

    private void loginActivityIntent(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
        startActivity(intent);
        finish();
        return;
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
