package com.roomiegh.roomie.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.roomiegh.roomie.R;
import com.roomiegh.roomie.activities.BookingSuccessActivity;
import com.roomiegh.roomie.activities.RegistrationActivity;
import com.roomiegh.roomie.activities.SignInActivity;
import com.roomiegh.roomie.models.User;
import com.roomiegh.roomie.util.CameraUtil;
import com.roomiegh.roomie.util.PreferenceData;
import com.roomiegh.roomie.util.PushUserUtil;
import com.squareup.picasso.Picasso;

/**
 * Created by Kwadwo Agyapon-Ntra on 06/10/2015.
 */

public class TabProfile extends Fragment {
    private Button btMyProfileEdit;
    private TextView tvProfileName /*concatenated fName and lName*/,
            tvProfilePhone, tvProfileEmail, tvProfileProgramme, tvProfileGuardName, tvProfileGuardPhone;
    private ImageView ivProfilePic, ivSignInUser;
    private User currentUser, displayedCurrentUser;
    private String currentUserEmail;
    LinearLayout llNoAccountView;
    NestedScrollView nsvProfile;
    FloatingActionButton fabEditProfile,fabViewYourHostel;


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 100) && (resultCode == Activity.RESULT_OK)) {
            //boolean receivedStatus = data.getBooleanExtra("bool", false);
            llNoAccountView.setVisibility(View.GONE);
            nsvProfile.setVisibility(View.VISIBLE);
            //fabEditProfile.setVisibility(View.VISIBLE);
            if(PreferenceData.getTenantExists(getActivity())){
                //tenant exists, show button to view room
                fabViewYourHostel.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the layout for this fragment/tab
        View view = inflater.inflate(R.layout.tab_profile, container, false);

        init(view);

        if (PreferenceData.getUserLoggedInStatus(getActivity())) {
            //show profile
            llNoAccountView.setVisibility(View.GONE);
            nsvProfile.setVisibility(View.VISIBLE);
            //fabEditProfile.setVisibility(View.VISIBLE);

            if(PreferenceData.getTenantExists(getActivity())){
                //tenant exists, show button to view room
                fabViewYourHostel.setVisibility(View.VISIBLE);
            }

            currentUser = PreferenceData.getLoggedInUser(getActivity());
            refreshProfile(currentUser);
        }


        ivSignInUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = new Intent(getActivity().getApplicationContext(),
                        SignInActivity.class);
                startActivityForResult(signInIntent, 100);//using 100 as requestCode
            }
        });

        fabViewYourHostel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), BookingSuccessActivity.class));
            }
        });

        fabEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to edit profile activity, populate fields from shared preference before allowing user to edit some
                //we need updae routes for this
            }
        });

        return view;
    }

    private void refreshProfile(User currentUser) {

        //setting profile text for the tenant
        if (currentUser != null) {
            /*if (displayedCurrentUser.getPhoto() != null) {
                ivProfilePic.setImageBitmap(CameraUtil.convertByteArrayToPhoto(displayedCurrentUser.getPhoto()));
            }*/
            tvProfileName.setText(currentUser.getfName() + " " + currentUser.getlName());
            tvProfileEmail.setText(currentUser.getEmail());
            tvProfilePhone.setText(currentUser.getPhone());
            tvProfileProgramme.setText(currentUser.getProgramme());
            tvProfileGuardName.setText(currentUser.getNok());
            tvProfileGuardPhone.setText(currentUser.getPhone());
            if(!(currentUser.getPicPath().equals("") || currentUser.getPicPath() == null)){
                Picasso.with(getActivity())
                        .load(currentUser.getPicPath())
                        .centerCrop()
                        .fit()
                        .placeholder(R.drawable.ic_account_circle_gray)
                        .error(R.drawable.ic_account_circle_gray)
                        .into(ivProfilePic);
            }

            //ivRegImage.setPadding(0, 0, 0, 0);
            //TODO add other fields for profile
            

        } else
            Toast.makeText(getActivity().getApplicationContext(), "Profile not found", Toast.LENGTH_SHORT).show();

    }

    private void init(View view) {
        tvProfileName = (TextView) view.findViewById(R.id.tvProfileName);
        tvProfilePhone = (TextView) view.findViewById(R.id.tvProfilePhone);
        tvProfileEmail = (TextView) view.findViewById(R.id.tvProfileEmail);
        tvProfileProgramme = (TextView) view.findViewById(R.id.tvProfileProgramme);
        tvProfileGuardName = (TextView) view.findViewById(R.id.tvProfileGuardName);
        tvProfileGuardPhone = (TextView) view.findViewById(R.id.tvProfileGuardPhone);
        currentUser = new User();

        llNoAccountView = (LinearLayout) view.findViewById(R.id.llNoAccountView);
        ivSignInUser = (ImageView) view.findViewById(R.id.ivSignInUser);
        nsvProfile = (NestedScrollView) view.findViewById(R.id.nsvProfile);
        ivProfilePic = (ImageView) view.findViewById(R.id.ivProfilePic);
        fabEditProfile = (FloatingActionButton) view.findViewById(R.id.fabEditProfile);
        fabViewYourHostel = (FloatingActionButton) view.findViewById(R.id.fabViewYourHostel);
    }
}
