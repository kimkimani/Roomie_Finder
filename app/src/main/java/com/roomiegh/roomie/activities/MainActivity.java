package com.roomiegh.roomie.activities;

/*Host activity for home, browse and profile tabs*/

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.roomiegh.roomie.R;
import com.roomiegh.roomie.adapters.ViewPagerAdapter;
import com.roomiegh.roomie.fragments.SlidingTabLayout;
import com.roomiegh.roomie.util.PreferenceData;
import com.roomiegh.roomie.util.ReadRSS;

//import tabs.SlidingTabLayout;

public class MainActivity extends AppCompatActivity {
    private static final String HOME_TAG = "HomeAdsView";
    // Declaring View and Variables
    Toolbar toolbar;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[] = {"Home", "Browse", "Profile"};
    int NumbOftabs = 3;
    String currentUserEmail;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "authentication_tag";
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //receive current user information
        /*Bundle receivedInfoBundle = getIntent().getBundleExtra(PushUserUtil.PUSH_INTENT_KEY);
        currentUserEmail = receivedInfoBundle.getString(PushUserUtil.USER_EMAIL);*/
        //fragment.setArguments(receivedInfoBundle);

        // Creating Toolbar and setting it as Toolbar for the activity

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        // Creating ViewPagerAdapter and Passing Fragment Manager, Titles for the Tabs and Number Of Tabs.
        //adapter =  new ViewPagerAdapter(getSupportFragmentManager(),Titles,NumbOftabs,currentUserEmail);
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, NumbOftabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.removeItem(R.id.action_settings);
        //if (user != null)//signed in
        if(PreferenceData.getUserLoggedInStatus(MainActivity.this))//signed in
            menu.removeItem(R.id.mnSignIn);
        else
            menu.removeItem(R.id.mnSignOut);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.mnSignIn) {
            mAuth.signOut();
            PreferenceData.clearLoggedInUserData(MainActivity.this);
            PreferenceData.clearProfileData(MainActivity.this);
            PreferenceData.clearProfilePic(MainActivity.this);
            this.finishAffinity();
            Intent goHome = new Intent(this, SignInActivity.class);
            startActivity(goHome);
            return true;
        }else if (id == R.id.mnSignOut) {
            mAuth.signOut();
            PreferenceData.clearLoggedInUserData(MainActivity.this);
            PreferenceData.clearProfileData(MainActivity.this);
            PreferenceData.clearProfilePic(MainActivity.this);
            PreferenceData.clearTenantInfo(MainActivity.this);
            this.finishAffinity();
            Intent goHome = new Intent(this, SignInActivity.class);
            startActivity(goHome);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    public void doRSSRead(Context ctx, RecyclerView recyclerView, ScrollView svConnectionError, ProgressBar pbLoadAds) {
        ReadRSS readRSS = new ReadRSS(ctx, recyclerView, svConnectionError, pbLoadAds);
        Log.d(HOME_TAG, "onCreateView: about to call ReadRSS");
        readRSS.execute();
    }
}
