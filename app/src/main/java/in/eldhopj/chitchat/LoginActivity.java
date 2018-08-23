package in.eldhopj.chitchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private TextInputLayout phoneNumberTIL, codeTIL;
    String mPhoneNo,enteredCode;
    String mVerificationId; //The verification id that will be sent from Firebase to user
    Button mSend;
    FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks  mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phoneNumberTIL = findViewById(R.id.phoneNoTIL);
        codeTIL = findViewById(R.id.codeTIL);
        mSend = findViewById(R.id.send);
        mAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(getApplicationContext());

        //AutoLogin
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {  // Check if user is signed in (non-null) and update UI accordingly.
            mainActivityIntent();
        }

        /**Checks if the user enters correct code or not*/
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuth(phoneAuthCredential);
                //Getting the code sent by SMS
                codeTIL.getEditText().setText(phoneAuthCredential.getSmsCode());
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

            /*Sometime we have to do manual entry
            This onCodeSent() called when the code is sent successfully*/
            @Override
            public void onCodeSent(String code, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(code, forceResendingToken);
                mVerificationId= code;//storing the verification id that is sent to the user
                mSend.setText("Verify");
            }
        };

        phoneNumberTIL.getEditText().addTextChangedListener(watch);
    }

    //Text watcher starts here
    private final TextWatcher watch = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            Log.d("beforeTextChanged:", "");
            // removes the error message when starts typing
            phoneNumberTIL.setError("");
            codeTIL.setError("");
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
        @Override
        public void afterTextChanged(Editable s) {
        }
    };
    //Text watcher ends here

    //validations stars here
    private boolean validatePhoneNumber() {
        CountryCodePicker ccp;
        ccp = findViewById(R.id.ccpID);
        ccp.registerCarrierNumberEditText(phoneNumberTIL.getEditText());
        mPhoneNo = ccp.getFullNumberWithPlus().trim();
        if (!(ccp.isValidFullNumber())) {
            phoneNumberTIL.setError("Enter valid phone number");
            return false;
        } else {
            phoneNumberTIL.setError(null);
            return true;
        }
    }
    private boolean validateCode() {
        enteredCode = codeTIL.getEditText().getText().toString().trim();
        if (enteredCode.isEmpty() || enteredCode.length() != 6) {
            codeTIL.setError("Field can't be empty");
            return false;
        } else {
            codeTIL.setError(null);
            return true;
        }
    }

    //validations ends here
    /**
     * SignIn
     * */
    private void signInWithPhoneAuth(PhoneAuthCredential phoneAuthCredential) {
        // custom progress dialog
        final ProgressDialog progressDialog = new ProgressDialog(this,
                R.style.Theme_AppCompat_Light_Dialog_Alert);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Welcome...");
        progressDialog.show();

        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()){
                    mainActivityIntent();
                }
                else{
                    Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    /**
     * for sending verification code
     * */
    private void phoneVerification() {
        /**@param phone_number
         * @param Timeout
         * @param timeUnit
         * @param activity
         * @param callBack -> for handling failures or successes */
        Log.d(TAG, "phoneVerification: ");
        PhoneAuthProvider.getInstance().verifyPhoneNumber(mPhoneNo,60, TimeUnit.SECONDS,this,mCallbacks);
    }
    private void verifyCodeManually(){
        Log.d(TAG, "verifyCodeManually: ");
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,enteredCode); //creating credential
        signInWithPhoneAuth(credential); //signing user
    }

    private void mainActivityIntent(){
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);//We need to close all the existing activity because we don't want our user to navigate back on backButton press
        startActivity(intent);
        finish();
        return;
    }

    /**
     * In case of verifying manually
     * */


    public void submit(View view) {
        if (!(validatePhoneNumber())) {
            return;
        }
        if (mVerificationId != null){
            verifyCodeManually();
        }
        else {
            phoneVerification();
        }
    }
}
