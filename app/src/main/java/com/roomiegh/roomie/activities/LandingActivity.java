package com.roomiegh.roomie.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.roomiegh.roomie.R;


public class LandingActivity extends AppCompatActivity {
    private Button btLandSignUp, btLandSingIn;
    private ImageView ivLandRoomie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        init();
        ivLandRoomie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        btLandSingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        btLandSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private void init() {
        btLandSignUp = (Button) findViewById(R.id.btLandSignUp);
        btLandSingIn = (Button) findViewById(R.id.btLandSignIn);
        ivLandRoomie = (ImageView) findViewById(R.id.ivLandRoomie);
    }

    public void signUp(){
        Intent signUpIntent = new Intent(LandingActivity.this, SignUpActivity.class);
        startActivity(signUpIntent);
    }

    public void signIn(){
        Intent signInIntent = new Intent(LandingActivity.this, SignInActivity.class);
        startActivity(signInIntent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_landing_screen, menu);
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
