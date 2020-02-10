package com.roomiegh.roomie.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.roomiegh.roomie.R;
import com.roomiegh.roomie.util.PushUserUtil;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "authentication_tag";
    private EditText etSignUpEmail, etSignUpPassword, etPasswordVerification;
    private Button btSignUp;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        init();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEmailValid(etSignUpEmail.getText())) {
                    //valid email address
                    if (etPasswordVerification.getText().toString().equals(etSignUpPassword.getText().toString())
                            && !(etSignUpPassword.getText().toString().equals(""))) {
                        // password match
                        if (etSignUpPassword.getText().toString().length() >= 6)
                            createAccount(etSignUpEmail.getText().toString(), etSignUpPassword.getText().toString());
                        else
                            Toast.makeText(SignUpActivity.this, "Password should be at least 6 Characters long.", Toast.LENGTH_SHORT).show();
                    } else {
                        //password mismatch
                        Toast.makeText(SignUpActivity.this, "Password Mismatch", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //invalid email address
                    Toast.makeText(SignUpActivity.this, "Invalid Email Address", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void createAccount(final String email, final String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "An error occurred. Try again.",
                                    Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onComplete: Failed=" + task.getException().getMessage());
                        } else {
                            //successful, sign user in
                            signIn(email, password);
                        }
                    }
                });
    }

    private void signIn(final String email, final String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(SignUpActivity.this, "Error signing in.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            //successfully signed in, move to registration screen
                            Bundle pushUser = new Bundle();
                            pushUser.putString(PushUserUtil.USER_EMAIL, email);
                            pushUser.putString("password", password);//we will remove the password later.
                            //for now I just do not want to break the SQL lite code
                            Intent registerIntent = new Intent(SignUpActivity.this, RegistrationActivity.class);
                            registerIntent.putExtra(PushUserUtil.PUSH_INTENT_KEY, pushUser);
                            startActivity(registerIntent);
                        }
                    }
                });
    }

    private void init() {
        etSignUpEmail = (EditText) findViewById(R.id.etSignUpEmail);
        etSignUpPassword = (EditText) findViewById(R.id.etSignUpPassword);
        etPasswordVerification = (EditText) findViewById(R.id.etPasswordVerification);
        btSignUp = (Button) findViewById(R.id.btSignUp);
        mAuth = FirebaseAuth.getInstance();
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
