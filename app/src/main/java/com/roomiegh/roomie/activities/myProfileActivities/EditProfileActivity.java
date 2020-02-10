package com.roomiegh.roomie.activities.myProfileActivities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.roomiegh.roomie.R;
import com.roomiegh.roomie.models.User;
import com.roomiegh.roomie.util.CameraUtil;
import com.roomiegh.roomie.util.PushUserUtil;

public class EditProfileActivity extends AppCompatActivity {
    private Button btEditProfileSave;
    private ImageView ivEditProfilePic;
    private EditText etEditProfilePhone, etEditProfilePhone2, etEditProfileEmail,
            etEditProfileInstitution, etEditProfileProgramme, etEditProfileYear, etEditProfileMonth,
            etEditProfileDay, etEditProfileGuardName, etEditProfileGuardPhone;
    private TextView tvProfileRefNo, tvProfileName, tvProfileHostel, tvProfileRoomNum;
    private byte[] imageData = new byte[1000];
    private Bitmap photo;
    private User currentUser, displayedCurrentUser;
    private boolean isUpdated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        //receive pushed user info
        Bundle receivedInfo = getIntent().getBundleExtra(PushUserUtil.PUSH_INTENT_KEY);
        String currentUserEmail = receivedInfo.getString(PushUserUtil.USER_EMAIL);

        init();

        currentUser.setEmail(currentUserEmail);

        //setting profile text for the tenant
        if (displayedCurrentUser != null) {
            /*if (displayedCurrentUser.getPhoto() != null) {
                ivEditProfilePic.setImageBitmap(CameraUtil.convertByteArrayToPhoto(displayedCurrentUser.getPhoto()));
            }*/
            tvProfileName.setText(displayedCurrentUser.getfName() + " " + displayedCurrentUser.getlName());
            //tvProfileRefNo.setText(displayedCurrentUser.getRefNo() + "");
            etEditProfileEmail.setText(displayedCurrentUser.getEmail());
            etEditProfilePhone.setText(displayedCurrentUser.getPhone());

        } else
            Toast.makeText(getApplicationContext(), "Profile not found", Toast.LENGTH_SHORT).show();

        ivEditProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CameraUtil.CAMERA_REQUEST_CODE);

            }
        });
        btEditProfileSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //currentUser.setPhoto(imageData);
                currentUser.setfName(displayedCurrentUser.getfName());
                currentUser.setlName(displayedCurrentUser.getlName());
                //currentUser.setRefNo(displayedCurrentUser.getRefNo());

                currentUser.setPhone(etEditProfilePhone.getText() + "");
                currentUser.setEmail(etEditProfileEmail.getText().toString());

                Intent sendStatus = new Intent();
                sendStatus.putExtra("bool",isUpdated);
                setResult(RESULT_OK, sendStatus);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CameraUtil.CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ivEditProfilePic.setImageBitmap(photo);
            imageData = CameraUtil.convertPhotoToByteArray(photo);
        }
    }

    private void init() {
        ivEditProfilePic = (ImageView) findViewById(R.id.ivEditProfilePic);
        tvProfileName = (TextView) findViewById(R.id.tvProfileName);
        etEditProfilePhone = (EditText) findViewById(R.id.etEditProfilePhone);
        etEditProfileEmail = (EditText) findViewById(R.id.etEditProfileEmail);
        etEditProfileProgramme = (EditText) findViewById(R.id.etEditProfileProgramme);
        etEditProfileGuardName = (EditText) findViewById(R.id.etEditProfileGuardName);
        etEditProfileGuardPhone = (EditText) findViewById(R.id.etEditProfileGuardPhone);

        btEditProfileSave = (Button) findViewById(R.id.btEditProfileSave);

        currentUser = new User();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
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
