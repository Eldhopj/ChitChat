package in.eldhopj.chitchat;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
/**Commit 1:
 *          Phone auth using Firebase
 * Commit 2:
 *          Setup RecyclerView
 *          Permissions
 * Commit 3:
 *          Display contacts
 * Commit 4:
 *          Save users data's into Database while login
 *          Display only the users in ChitChat on findUser
 * Commit 5:
 *          Fix UI freeze bug while loading contacts makes code more snappy (FindUserActivity)
 * */
public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{
    private FirebaseAuth mAuth;
    private final int READ_WRITE_CONTACTS = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();  //firebase
        FirebaseUser user = mAuth.getCurrentUser();

        if(user == null){
          loginActivityIntent();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            reqContactsPerms();
        }
    }
    private void logout(){
        mAuth.signOut();
        loginActivityIntent();

    }
    private void loginActivityIntent(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
        startActivity(intent);
        finish();
        return;
    }

    public void findUser(View view) {
        Intent intent = new Intent(getApplicationContext(), FindUserActivity.class);
        startActivity(intent);
    }

    //Permissions
    @AfterPermissionGranted(READ_WRITE_CONTACTS)
    private void reqContactsPerms() { //Note : This method must be void and cant able to take any arguments
        String[] perms = {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS}; //Array of permission
        if (EasyPermissions.hasPermissions(this, perms)) { //check permission is granted or not

            //code if permission is granted goes in here
            Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show();

        } else {
            EasyPermissions.requestPermissions(this, "Needed permission for to access contacts",
                    READ_WRITE_CONTACTS, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

        // This will forcefully ask permission again and again if permission denied, Cant able to use the activity unless the permission given
        // Do it in emergency situations only
        reqContactsPerms();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

        // if some permissions denys the user will SEND TO SETTINGS of the app to manually grand the permission

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    // Code when user came back from the settings <Optional/>
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE){
            // Do something after user returned from app settings screen, like showing a Toast.
            Toast.makeText(this,"Welcome Back", Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
