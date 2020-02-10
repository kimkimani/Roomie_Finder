package com.roomiegh.roomie.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.roomiegh.roomie.R;
import com.roomiegh.roomie.models.User;
import com.roomiegh.roomie.util.PreferenceData;
import com.roomiegh.roomie.util.PushUserUtil;
import com.roomiegh.roomie.volley.AppSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class SignInActivity extends AppCompatActivity {
    private static final String REQUEST_TAG = "get_signed_in_user_details_tag";
    private EditText etSignInEmail, etSignInPassword, etConfirmPassword;
    private Button btSignIn, btSignUpInstead;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "authentication_tag";
    private TextView tvForgotPassword, tvCreateAccountPrompt;
    boolean remember = false;
    ProgressDialog pdSignin;
    Toolbar toolbar;
    private String sign_in_url = "http://roomiegh.herokuapp.com/roomieuser/signin";
    boolean isBookingRoom = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        SignInActivity.this.setTitle("Sign In");

        if (getIntent().getBooleanExtra("booking_room", false)) {
            isBookingRoom = true;
        } else {
            isBookingRoom = false;
        }

        init();

        mAuth = FirebaseAuth.getInstance();

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

        btSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(etSignInEmail.getText().toString()).matches()) {
                    pdSignin.setMessage("Signing in...");
                    pdSignin.show();
                    final String userEmail = etSignInEmail.getText().toString();
                    mAuth.signInWithEmailAndPassword(etSignInEmail.getText().toString(), etSignInPassword.getText().toString())
                            .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        pdSignin.dismiss();
                                        Log.w(TAG, "signInWithEmail", task.getException());
                                        Toast.makeText(SignInActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    } else {

                                        setResult(RESULT_OK);
                                        Toast.makeText(SignInActivity.this, "Getting your details",
                                                Toast.LENGTH_SHORT).show();
                                        callForDetails(userEmail, pdSignin);
                                    }
                                }
                            });

                } else {
                    Toast.makeText(SignInActivity.this, "Invalid Email Address", Toast.LENGTH_SHORT).show();
                }

            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(etSignInEmail.getText().toString()).matches()) {
                    mAuth.sendPasswordResetEmail(etSignInEmail.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SignInActivity.this, "A reset email has been sent to " + etSignInEmail.getText().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(SignInActivity.this, "Enter your email address and hit 'Forgot Password' again", Toast.LENGTH_LONG).show();
                    etSignInEmail.requestFocus();
                }
            }
        });

        btSignUpInstead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btSignIn.getVisibility() == View.VISIBLE) {
                    //prepare screen to act as sign up page
                    btSignIn.setVisibility(View.GONE);
                    tvForgotPassword.setVisibility(View.GONE);
                    tvCreateAccountPrompt.setVisibility(View.GONE);
                    etConfirmPassword.setVisibility(View.VISIBLE);
                    etSignInEmail.requestFocus();
                    SignInActivity.this.setTitle("Sign Up");
                } else {
                    //validate password and email and attempt sign up
                    validateAndSignUp();
                }
                /*Intent registerInsteadIntent = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivityForResult(registerInsteadIntent, 200);*/
            }
        });
    }

    private void callForDetails(final String userEmail, final ProgressDialog pdSignin) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", userEmail);

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, sign_in_url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        User signedInUser = new User();
                        JSONObject jsonData;
                        JSONArray tenantData;
                        pdSignin.dismiss();
                        // response
                        Log.d("Response", response.toString());
                        try {
                            if (response.has("user_info")) {
                                Toast.makeText(SignInActivity.this, "Saving User Data", Toast.LENGTH_SHORT).show();
                                jsonData = response.getJSONArray("user_info").getJSONObject(0);
                                //store user info
                                signedInUser.setId(jsonData.getInt("id"));
                                String fullName = jsonData.getString("name");
                                signedInUser.setfName(fullName.substring(0, fullName.indexOf(" ")));
                                signedInUser.setlName(fullName.substring(fullName.indexOf(" ")));
                                signedInUser.setProgramme(jsonData.getString("course"));
                                signedInUser.setYear(jsonData.getInt("year"));
                                signedInUser.setGender(jsonData.getString("gender"));
                                signedInUser.setPicPath(jsonData.getString("photoPath"));
                                signedInUser.setNok(jsonData.getJSONArray("user_guardian").getJSONObject(0).getString("name"));
                                signedInUser.setEmail(userEmail);

                                if(jsonData.has("user_info")){
                                    tenantData = jsonData.getJSONArray("user_info");
                                    if (tenantData.length() > 0) {
                                        //tenant data exists, set in preferences
                                        PreferenceData.setTenantInfo(SignInActivity.this,
                                                tenantData.getJSONObject(0).getInt("id"),
                                                tenantData.getJSONObject(0).getInt("hostels_hostel_id"),
                                                tenantData.getJSONObject(0).getInt("rooms_room_id"));
                                    }
                                }

                                if (response.has("contact_info")) {
                                    jsonData = response.getJSONArray("contact_info").getJSONObject(0);
                                    signedInUser.setPhone(jsonData.getString("phone"));
                                }
                                if (response.has("guardian_contact_info")) {
                                    jsonData = response.getJSONArray("guardian_contact_info").getJSONObject(0);
                                    signedInUser.setNokPhone(jsonData.getString("phone"));
                                }
                                Toast.makeText(SignInActivity.this, "Saving Preference Data", Toast.LENGTH_SHORT).show();
                                PreferenceData.setLoggedInUserEmail(getApplicationContext(), userEmail);
                                PreferenceData.setUserLoggedInStatus(getApplicationContext(), true);
                                PreferenceData.setProfileData(SignInActivity.this, signedInUser);

                                /*PreferenceData.setTenantInfo(SignInActivity.this,
                                        response.getJSONObject("Tenant_info").getInt("id"),
                                        response.getJSONObject("Tenant_info").getInt("hostels_hostel_id"),
                                        response.getJSONObject("Tenant_info").getInt("rooms_room_id"));*/
                                //go to main activity

                                if (!isBookingRoom) {//take user to main, else finish and go back to room booking
                                    Intent goToApp = new Intent(SignInActivity.this, MainActivity.class);
                                    startActivity(goToApp);
                                }

                                SignInActivity.this.finish();


                            }else if(response.has("alert")){
                                //TODO Incomplete registration, take user to registration screen
                                //tell user to log in to continue
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);

                                builder.setTitle("Incomplete Registration");
                                builder.setMessage("Complete your registration to proceed?");
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //Take user to sign in screen
                                        Intent registerIntent = new Intent(SignInActivity.this,
                                                RegistrationActivity.class);
                                        Bundle emailBundle = new Bundle();
                                        emailBundle.putString(PushUserUtil.USER_EMAIL,etSignInEmail.getText().toString());
                                        //emailBundle.putString("registering_now","Yes I am!");
                                        registerIntent.putExtra(PushUserUtil.PUSH_INTENT_KEY, emailBundle);
                                        startActivityForResult(registerIntent, 100);//using 100 as requestCode
                                    }
                                });
                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });
// 3. Get the AlertDialog from create()
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "onResponse: Exception encountered " + e.toString());
                        }
                        pdSignin.dismiss();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        pdSignin.dismiss();
                        Log.d("Error.Response", error.toString());
                        error.printStackTrace();
                        Toast.makeText(SignInActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Api-Token", "RoomieAppGH");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        // Adding string request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(postRequest, REQUEST_TAG);

    }

    private void validateAndSignUp() {
        String pswd = etSignInPassword.getText().toString();
        if (pswd.equals(etConfirmPassword.getText().toString())) {
            if (pswd.length() >= 8) {
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(etSignInEmail.getText().toString()).matches()) {
                    pdSignin.setMessage("Signing Up...");
                    pdSignin.show();
                    mAuth.createUserWithEmailAndPassword(etSignInEmail.getText().toString(), pswd)
                            .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    pdSignin.dismiss();
                                    Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(SignInActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        PreferenceData.setLoggedInUserEmail(getApplicationContext(), etSignInEmail.getText().toString());
                                        PreferenceData.setUserLoggedInStatus(getApplicationContext(), true);

                                        setResult(RESULT_OK);
                                        Toast.makeText(SignInActivity.this, "Welcome",
                                                Toast.LENGTH_SHORT).show();
                                        Intent updateProfileIntent = new Intent(getApplicationContext(), RegistrationActivity.class);
                                        Bundle emailBundle = new Bundle();
                                        emailBundle.putString(PushUserUtil.USER_EMAIL, etSignInEmail.getText().toString());
                                        updateProfileIntent.putExtra(PushUserUtil.PUSH_INTENT_KEY, emailBundle);
                                        startActivity(updateProfileIntent);
                                        finish();
                                    }

                                    // ...
                                }
                            });
                }
            } else {
                Toast.makeText(SignInActivity.this, "Password should be over 8 characters long", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(SignInActivity.this, "'Password' should match 'Confirm Password'", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK) {
            //successful request, set result ok and finish activity
            setResult(RESULT_OK);
            finish();
        }
    }

    private void init() {
        etSignInEmail = (EditText) findViewById(R.id.etSignInEmail);
        etSignInPassword = (EditText) findViewById(R.id.etSignInPassword);
        btSignIn = (Button) findViewById(R.id.btSignIn);
        tvForgotPassword = (TextView) findViewById(R.id.tvForgotPassword);
        btSignUpInstead = (Button) findViewById(R.id.btSignUpInstead);
        pdSignin = new ProgressDialog(SignInActivity.this);

        tvCreateAccountPrompt = (TextView) findViewById(R.id.tvCreateAccountPrompt);
        etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_in_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            if (!(btSignIn.getVisibility() == View.VISIBLE)) {
                //prepare screen to act as sign up page
                btSignIn.setVisibility(View.VISIBLE);
                tvForgotPassword.setVisibility(View.VISIBLE);
                tvCreateAccountPrompt.setVisibility(View.VISIBLE);
                etConfirmPassword.setVisibility(View.GONE);
                etSignInEmail.setText("");
                etSignInPassword.setText("");
                SignInActivity.this.setTitle("Sign UI");
            } else {
                finish();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
