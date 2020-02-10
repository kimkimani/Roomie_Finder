package com.roomiegh.roomie.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.roomiegh.roomie.R;
import com.roomiegh.roomie.models.Room;
import com.roomiegh.roomie.models.User;
import com.roomiegh.roomie.util.PreferenceData;
import com.roomiegh.roomie.util.PushUserUtil;
import com.roomiegh.roomie.volley.AppSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ViewRoomActivity extends AppCompatActivity {
    private static final String REQUEST_TAG = "RegisterRoomRequest";
    String booking_url = "http://roomiegh.herokuapp.com/tenant";
    Room thisRoom;
    TextView tvRoomDetailsType, tvRoomDetailsPrice, tvRoomHostel, tvRoomLocation;
    ListView lvRoomPics;
    ProgressBar pbRoomPics;
    FloatingActionButton fabBookRoom;
    Toolbar toolbar;
    ProgressDialog pdBookRoom;
    String hostelLocation = "";
    Bundle receivedBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_room);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        receivedBundle = getIntent().getBundleExtra("room_bundle");
        thisRoom = (Room) receivedBundle.getSerializable("this_room");
        String hostelName = receivedBundle.getString("hostel_name");

        if (receivedBundle.getString("hostel_location") != null)
            hostelLocation = receivedBundle.getString("hostel_location");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        init();

        if (thisRoom != null) {
            setTitle("Room " + thisRoom.getRoomNum());
            tvRoomDetailsType.setText(tvRoomDetailsType.getText().toString() + " " + thisRoom.getType() + " in a room");
            tvRoomDetailsPrice.setText(tvRoomDetailsPrice.getText().toString() + " " + thisRoom.getPrice());
            tvRoomHostel.setText(tvRoomHostel.getText().toString() + " " + hostelName);
            if (hostelLocation != null)
                tvRoomLocation.setText(tvRoomLocation.getText().toString() + " " + hostelLocation);
        }

        callForPics(thisRoom.getId());

        fabBookRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PreferenceData.getUserLoggedInStatus(ViewRoomActivity.this)) {
                    //user is signed in
                    final User thisUser = PreferenceData.getLoggedInUser(ViewRoomActivity.this);

                    if (PreferenceData.getTenantExists(ViewRoomActivity.this)) {
                        //user is already a tenant, warn that room will be changed
                        AlertDialog.Builder builder = new AlertDialog.Builder(ViewRoomActivity.this);
                        builder.setTitle("Attention!!");
                        builder.setMessage("You will lose your old room!!!");
                        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                registerUser(thisUser, thisRoom);
                            }
                        });
                        builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do nothing
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        //user does not already have a room... book this one
                        registerUser(thisUser, thisRoom);
                    }

                } else {
                    //tell user to log in to continue
                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewRoomActivity.this);

                    builder.setMessage("You must log in to continue");
                    builder.setPositiveButton("Log in", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //Take user to sign in screen
                            Intent signInIntent = new Intent(ViewRoomActivity.this,
                                    SignInActivity.class);
                            signInIntent.putExtra("booking_room", true);
                            startActivityForResult(signInIntent, 100);//using 100 as requestCode
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
// 3. Get the AlertDialog from create()
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

    }

    private void callForPics(int id) {
        //do call for room pics over here
    }

    private void registerUser(User thisUser, final Room thisRoom) {
        //register this user to this room using post request
        Map<String, String> params = new HashMap<String, String>();
        params.put("app_users_user_id", thisUser.getId() + "");
        params.put("app_users_user_gender", thisUser.getGender());
        params.put("rooms_room_id", thisRoom.getId() + "");
        params.put("rooms_room_type", thisRoom.getType() + "");
        params.put("hostels_hostel_id", thisRoom.getHostel_id() + "");

        pdBookRoom.setMessage("Booking your room...");
        pdBookRoom.show();
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, booking_url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pdBookRoom.dismiss();
                        // response
                        Log.d("Response", response.toString());
                        try {
                            if (response.has("Tenant_info")) {
                                //successful room registration
                                Toast.makeText(ViewRoomActivity.this, "Successfully booked Room " + thisRoom.getRoomNum(), Toast.LENGTH_SHORT).show();

                                PreferenceData.setTenantInfo(ViewRoomActivity.this,
                                        response.getJSONObject("Tenant_info").getInt("id"),
                                        response.getJSONObject("Tenant_info").getInt("hostels_hostel_id"),
                                        response.getJSONObject("Tenant_info").getInt("rooms_room_id"));

                                //TODO Move to new activity and show account details
                                Intent paymentDetailsIntent = new Intent(ViewRoomActivity.this, BookingSuccessActivity.class);
                                //TODO attach required data
                                receivedBundle.putInt("hostel_id",thisRoom.getHostel_id());
                                receivedBundle.putSerializable("this_room",thisRoom);
                                paymentDetailsIntent.putExtra("room_bundle", receivedBundle);
                                startActivity(paymentDetailsIntent);

                            } else if (response.has("updated")) {
                                Toast.makeText(ViewRoomActivity.this, "Updated your room booking", Toast.LENGTH_SHORT).show();
                                //TODO Move to new activity and show account details
                                Intent paymentDetailsIntent = new Intent(ViewRoomActivity.this, BookingSuccessActivity.class);
                                //TODO attach required data
                                receivedBundle.putInt("hostel_id",thisRoom.getHostel_id());
                                receivedBundle.putSerializable("this_room",thisRoom);
                                paymentDetailsIntent.putExtra("room_bundle", receivedBundle);
                                startActivity(paymentDetailsIntent);
                            } else if (response.has("alert")){
                                //TODO Incomplete registration, take user to registration screen
                                //tell user to log in to continue
                                AlertDialog.Builder builder = new AlertDialog.Builder(ViewRoomActivity.this);

                                builder.setTitle("Incomplete Registration");
                                builder.setMessage("Complete your registration to proceed?");
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //Take user to sign in screen
                                        Intent registerIntent = new Intent(ViewRoomActivity.this,
                                                RegistrationActivity.class);
                                        Bundle emailBundle = new Bundle();
                                        emailBundle.putString(PushUserUtil.USER_EMAIL,PreferenceData.getLoggedInEmailUser(ViewRoomActivity.this));
                                        emailBundle.putString("registering_now","Yes I am!");
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
                            }else {
                                Toast.makeText(ViewRoomActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //TODO make this a dialog for confirmation
                        pdBookRoom.dismiss();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        pdBookRoom.dismiss();
                        Log.d("Error.Response", error.toString());
                        error.printStackTrace();
                        Toast.makeText(ViewRoomActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
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

    private void init() {
        tvRoomDetailsPrice = (TextView) findViewById(R.id.tvRoomDetailsPrice);
        tvRoomDetailsType = (TextView) findViewById(R.id.tvRoomDetailsType);
        tvRoomHostel = (TextView) findViewById(R.id.tvRoomDetailsHostel);
        tvRoomLocation = (TextView) findViewById(R.id.tvRoomDetailsLocation);
        pbRoomPics = (ProgressBar) findViewById(R.id.pbRoomDetailsPics);
        fabBookRoom = (FloatingActionButton) findViewById(R.id.fabBookRoom);
        lvRoomPics = (ListView) findViewById(R.id.lvRoomImages);
        //TODO set adapter for lvRoomPics
        pdBookRoom = new ProgressDialog(ViewRoomActivity.this);
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
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
