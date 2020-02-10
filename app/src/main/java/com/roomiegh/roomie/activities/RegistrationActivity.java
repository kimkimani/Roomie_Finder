package com.roomiegh.roomie.activities;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.roomiegh.roomie.R;
import com.roomiegh.roomie.models.Hostel;
import com.roomiegh.roomie.models.User;
import com.roomiegh.roomie.util.PreferenceData;
import com.roomiegh.roomie.util.PushUserUtil;
import com.roomiegh.roomie.volley.AppSingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;


public class RegistrationActivity extends AppCompatActivity {
    private static final int PICK_FROM_STORAGE = 100;
    private static final int TAKE_PICTURE = 200;
    private static final String LOG_TAG = "RegisterUserLog";
    private static final String REQUEST_TAG = "UserPostRequest";
    private static String picName = "";
    private EditText etRegFirstName, etRegLastName, etRegPhone, etRegEmail, etRegProgramme, etRegNOK, etRegNOKPhone;
    private Button btRegSave, btRegCancel;
    private CheckBox cbRegMale, cbRegFemale;
    private boolean requiredFilled;
    private String gender;
    private User user;
    //private SignInManager signInManager;
    private String email;
    private ImageView ivRegImage;
    private ProgressBar pbUploadingPic, pbUploadingData;
    private Spinner spYears;
    int year = 0;

    private StorageReference mStorageRef;
    Uri file;
    Uri downloadUrl = null;
    Uri profilePicUri = null;
    private String url = "http://roomiegh.herokuapp.com/roomieuser";
    private boolean currentlyBooking = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        init();

        //set spinner values
        Integer[] items = new Integer[]{1,2,3,4,5,6,7};
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(RegistrationActivity.this,android.R.layout.simple_spinner_item, items);
        spYears.setAdapter(adapter);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        Bundle receivedInfo = getIntent().getBundleExtra(PushUserUtil.PUSH_INTENT_KEY);
        email = receivedInfo.getString(PushUserUtil.USER_EMAIL);
        if(receivedInfo.getString("registering_now")==null){
            //not called from View room called
            currentlyBooking = false;
        }else{
            currentlyBooking = true;
        }

        if (!email.equals("")) {
            etRegEmail.setText(email);
            etRegEmail.setEnabled(false);
        }

        //making CheckBoxes mutually exclusive
        cbRegMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cbRegFemale.setSelected(false);
                cbRegFemale.setChecked(false);
            }
        });
        cbRegFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cbRegMale.setSelected(false);
                cbRegMale.setChecked(false);
            }
        });

        spYears.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                year = position+1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //cancel button
        btRegCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        //Save profile button
        btRegSave.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void onClick(View v) {

                if (checkRequiredFilled()) {

                    if (cbRegFemale.isChecked() || cbRegMale.isChecked()) {
                        //check gender
                        if (cbRegMale.isChecked())
                            gender = "M";
                        else
                            gender = "F";

                        //assigning data to required variables for user
                        user.setfName(etRegFirstName.getText().toString());
                        user.setlName(etRegLastName.getText().toString());// concatenate names
                        user.setProgramme(etRegProgramme.getText().toString());
                        user.setYear(year);
                        user.setGender(gender);
                        user.setPhone(etRegPhone.getText().toString());
                        user.setEmail(email);
                        if(downloadUrl != null)
                            user.setPicPath(downloadUrl.getPath());
                        else
                            user.setPicPath("");
                        user.setNok(etRegNOK.getText().toString());
                        user.setNokPhone(etRegNOKPhone.getText().toString());


                        //putting data into database at required tables
                        //TODO make POST request and save results to SPs on success
                        makeUserPostRequest(user);

                        //Send user email to necessary activities and fragments
                        //Intent proceedIntent = new Intent(getApplicationContext(), MainActivity.class);
                        /*Bundle pushUser = new Bundle();
                        pushUser.putString(PushUserUtil.USER_EMAIL, email);

                        proceedIntent.putExtra(PushUserUtil.PUSH_INTENT_KEY, pushUser);*/

                        //TODO move to next activity if successful
                        /*startActivity(proceedIntent);
                        finishAffinity();*/


                    } else
                        Toast.makeText(getApplicationContext(),
                                "Please select your gender", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please fill required fields, shown with '*'", Toast.LENGTH_LONG).show();
                }
            }
        });

        ivRegImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. Instantiate an AlertDialog.Builder with its constructor
                AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);

// 2. Chain together various setter methods to set the dialog characteristics
                builder.setMessage("Change Picture");
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Do nothing
                    }
                });
                builder.setPositiveButton("Upload", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO upload picture from storage
                        Intent gallery =
                                new Intent(Intent.ACTION_PICK,
                                        //android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(gallery, PICK_FROM_STORAGE);
                    }
                });
// 3. Get the AlertDialog from create()
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        btRegCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (downloadUrl != null)
                    PreferenceData.clearProfilePic(RegistrationActivity.this);
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finishAffinity();
            }
        });

        /*btRegSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO upload contact details to the server
                //on success, save them in the shared preferences
                makeUserPostRequest();
            }
        });*/


    }

    private void makeUserPostRequest(final User user) {
        //pbByName.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", user.getfName()+" "+user.getlName());
        params.put("course", user.getProgramme());
        params.put("year", user.getYear()+"");
        params.put("gender", user.getGender());
        params.put("phone", user.getPhone());
        params.put("email", user.getEmail());
        params.put("photoPath", user.getPicPath());
        params.put("guardian_name", user.getNok());
        params.put("guardian_phone", user.getNokPhone());

        pbUploadingData.setVisibility(View.VISIBLE);
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url,new JSONObject(params),
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        pbUploadingData.setVisibility(View.GONE);
                        // response
                        try {
                            user.setId(response.getInt("user_info"));
                        } catch (JSONException e) {
                            Log.d(LOG_TAG, "onResponse: "+e.toString());
                            e.printStackTrace();
                        }
                        Log.d("Response", response.toString());
                        Toast.makeText(RegistrationActivity.this, "Profile Saved", Toast.LENGTH_SHORT).show();
                        PreferenceData.setProfileData(RegistrationActivity.this,user);
                        PreferenceData.setLoggedInUserEmail(getApplicationContext(), user.getEmail());
                        PreferenceData.setUserLoggedInStatus(getApplicationContext(), true);

                        if(profilePicUri != null)
                            PreferenceData.setProfilePicPath(RegistrationActivity.this,profilePicUri.getPath());


                        setResult(RESULT_OK);
                        if(!currentlyBooking)
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        finish();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        pbUploadingData.setVisibility(View.GONE);
                        Log.d("Error.Response", error.toString());
                        //Log.d("Error.Response", error.getMessage());
                        error.printStackTrace();
                        Toast.makeText(RegistrationActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", user.getfName()+" "+user.getlName());
                params.put("course", user.getProgramme());
                params.put("year", user.getYear()+"");
                params.put("gender", user.getGender());
                params.put("phone", user.getPhone());
                params.put("email", user.getEmail());
                params.put("photoPath", user.getPicPath());
                params.put("guardian_name", user.getNok());
                params.put("guardian_phone", user.getNokPhone());

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Api-Token","RoomieAppGH");
                headers.put("Content-Type","application/json");
                return headers;
            }
        };
        // Adding string request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(postRequest, REQUEST_TAG);

    }

    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = Uri.fromFile(getOutputMediaFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file);

        startActivityForResult(intent, TAKE_PICTURE);
    }

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");

        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        picName = "IMG_" + timeStamp + ".jpg";
        return new File(mediaStorageDir.getPath() + File.separator +
                picName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            pbUploadingPic.setVisibility(View.VISIBLE);
            if (requestCode == PICK_FROM_STORAGE) {
                Uri fileFromStorage = data.getData();
                uploadAndSet(fileFromStorage);
            } else if (requestCode == TAKE_PICTURE) {
                uploadAndSet(file);
            }
        } else {
            Toast.makeText(RegistrationActivity.this, "Problem getting image", Toast.LENGTH_SHORT).show();
        }

    }

    private void uploadAndSet(final Uri fileUri) {
        profilePicUri = fileUri;
        StorageReference profileRef = mStorageRef.child("profile_pics/" + new File(String.valueOf(fileUri)).getName());

        profileRef.putFile(fileUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        //TODO upload the download booking_url as photopath for this user
                        downloadUrl = taskSnapshot.getUploadSessionUri();
                        Picasso.with(RegistrationActivity.this).load(fileUri).fit().centerCrop().into(ivRegImage);
                        ivRegImage.setPadding(0, 0, 0, 0);
                        pbUploadingPic.setVisibility(GONE);
                        PreferenceData.setProfilePicPath(RegistrationActivity.this, fileUri.getPath());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        Toast.makeText(RegistrationActivity.this, "Usuccessful upload", Toast.LENGTH_SHORT).show();
                        pbUploadingPic.setVisibility(GONE);
                    }
                });
    }

    private boolean checkRequiredFilled() {
        if ((etRegFirstName.getText().length() == 0) ||
                (etRegLastName.getText().length() == 0) ||
                (!cbRegFemale.isChecked() && !cbRegMale.isChecked()) ||
                (etRegPhone.getText().length() == 0) ||
                (etRegProgramme.getText().length() == 0)||
                year == 0) {
            requiredFilled = false;
        } else {
            requiredFilled = true;
        }
        return requiredFilled;
    }

    private void init() {
        etRegFirstName = (EditText) findViewById(R.id.etRegFirstName);
        etRegLastName = (EditText) findViewById(R.id.etRegLastName);
        etRegPhone = (EditText) findViewById(R.id.etEditProfilePhone);
        btRegSave = (Button) findViewById(R.id.btEditProfileSave);
        btRegCancel = (Button) findViewById(R.id.btEditProfileCancel);
        cbRegMale = (CheckBox) findViewById(R.id.cbRegMale);
        cbRegFemale = (CheckBox) findViewById(R.id.cbRegFemale);
        user = new User();
        etRegEmail = (EditText) findViewById(R.id.etEditProfileEmail);
        ivRegImage = (ImageView) findViewById(R.id.ivEditProfilePic);
        pbUploadingPic = (ProgressBar) findViewById(R.id.pbUploadingProfilePic);
        spYears = (Spinner) findViewById(R.id.spProfileYear);
        etRegProgramme = (EditText) findViewById(R.id.etEditProfileProgramme);
        etRegNOK = (EditText) findViewById(R.id.etEditProfileGuardName);
        etRegNOKPhone = (EditText) findViewById(R.id.etEditProfileGuardPhone);
        pbUploadingData = (ProgressBar) findViewById(R.id.pbUploadingProfileData);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reg_screen, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }
}
