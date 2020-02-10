package com.roomiegh.roomie.activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.roomiegh.roomie.R;
import com.roomiegh.roomie.models.Hostel;
import com.roomiegh.roomie.volley.AppSingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HostelDetailsActivity extends AppCompatActivity {
    private static final String LOG_TAG = "HostelDetailsLog";
    private static final String REQUEST_TAG = "hostel_details_request";
    String browseType = "";
    int hostelID = -1;
    NestedScrollView nSView;
    ImageView ivHostelDetailsImage;
    TextView tvHostelDescription, tvHostelName, tvFacilities, tvLocation;
    RatingBar rbHostelDetailsRating;
    ProgressBar pbHostelDetails;
    FloatingActionButton fabViewRooms;

    Toolbar toolbar;
    String url = "http://roomiegh.herokuapp.com/hostel/";
    String location = "";

    ArrayList<String> hostelFacilities;
    Bundle receivedInfoBundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hostel_details);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        receivedInfoBundle = getIntent().getBundleExtra("type_bundle");
        browseType = receivedInfoBundle.getString("browse_type");
        hostelID = receivedInfoBundle.getInt("hostel_id");

        init();

        fabViewRooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO go to view rooms list acivity with browse type, if browse type is price or room type, use those parameters
                Intent viewRoomIntent = new Intent(HostelDetailsActivity.this,RoomsListActivity.class);
                //forward type bundle to view rooms List page
                viewRoomIntent.putExtra("type_bundle", receivedInfoBundle);
                startActivity(viewRoomIntent);
            }
        });

        callForDetails(hostelID);
    }

    private void callForDetails(int hostelID) {
        pbHostelDetails.setVisibility(View.VISIBLE);
        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(Request.Method.GET, url + hostelID, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(LOG_TAG, response.toString());

                        if (response.length() != 0) {
                            JSONObject jsonData, hostelLocationObject;
                            JSONArray hostelPicsArray, hostelDescriptionArray, hostelFacilitiesArray;
                            Hostel hostel = null;
                            hostelFacilities = new ArrayList<>();
                            try {
                                //for (int i = 0; i < response.length(); i++) {
                                //we're expecting only one result at index 0
                                jsonData = response.getJSONObject(0);
                                hostel = new Hostel();//make sure to redefine hostel inside loop to avoid filling the arraylist with the same elements
                                hostel.setId(jsonData.getInt("id"));
                                hostel.setName(jsonData.getString("name"));
                                //send hostel name to next activity over here
                                receivedInfoBundle.putString("hostel_name",jsonData.getString("name"));
                                hostel.setLocationId(jsonData.getInt("locations_location_id"));
                                hostel.setNoOfRooms(jsonData.getInt("noOfRooms"));
                                hostel.setRating(jsonData.getDouble("rating"));

                                //parse JSON to extract pics and description
                                hostelPicsArray = jsonData.getJSONArray("hostel_pics");
                                hostelDescriptionArray = jsonData.getJSONArray("hostel_description");
                                hostelFacilitiesArray = jsonData.getJSONArray("hostel_facilities");

                                //TODO Deal with getting correct location, then pass that location to the bundle and receive it at room details
                                if (jsonData.has("hostel_location")){
                                    hostelLocationObject = jsonData.getJSONObject("hostel_location");
                                    if(hostelLocationObject.has("name")){
                                        location = hostelLocationObject.getString("name");
                                        receivedInfoBundle.putString("hostel_location",location);
                                    }else{
                                        Log.d(LOG_TAG, "onResponse: no name for location");
                                    }
                                }

                                //TODO use for loop to pick multiple pics for the hostel
                                if (hostelPicsArray.length() > 0)
                                    hostel.setPhotopath(hostelPicsArray.getJSONObject(0).getString("image_url"));
                                if (hostelDescriptionArray.length() > 0)
                                    hostel.setDescription(hostelDescriptionArray.getJSONObject(0).getString("description"));
                                if (hostelFacilitiesArray.length() > 0) {
                                    for (int i = 0; i < hostelFacilitiesArray.length(); i++) {
                                        hostelFacilities.add(hostelFacilitiesArray.getJSONObject(i).getString("facility"));
                                    }
                                    hostel.setAllFacilities(hostelFacilities);
                                }


                                //add hostel to list
                                //allHostels.add(hostel);
                                Log.d(LOG_TAG, "Received: " + hostel.getName());
                                // }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d(LOG_TAG, "Exception encountered: " + e.toString());
                            } finally {

                                pbHostelDetails.setVisibility(View.GONE);
                                nSView.setVisibility(View.VISIBLE);
                                fabViewRooms.setVisibility(View.VISIBLE);

                                //set views using hostel data
                                if (hostel != null) {
                                    rbHostelDetailsRating.setNumStars((int) hostel.getRating());
                                    tvHostelDescription.setText(hostel.getDescription());
                                    tvHostelName.setText(hostel.getName());
                                    receivedInfoBundle.putString("hostel_name",hostel.getName());
                                    tvLocation.setText(tvLocation.getText().toString()+location);
                                    //list facilitites
                                    String facilityList = "";
                                    for (String facility : hostelFacilities)
                                        if (facilityList.equals(""))//for the first element
                                            facilityList += facility;
                                        else
                                            facilityList += "\n" + facility;
                                    tvFacilities.setText(facilityList);
                                    Log.d(LOG_TAG, "Hostel Description: "
                                            + hostel.getDescription());
                                    Picasso.with(HostelDetailsActivity.this)
                                            .load(hostel.getPhotopath())
                                            .fit()
                                            .centerCrop()
                                            .error(R.drawable.ic_home_black)
                                            .placeholder(R.drawable.white_bkgrnd)
                                            .into(ivHostelDetailsImage);
                                    Log.d(LOG_TAG, "Hostel Image: " + hostel.getPhotopath());

                                } else {
                                    Log.d(LOG_TAG, "onResponse: hostel is null");
                                }

                            }
                        } else {
                            // TODO: 09/05/2017 Show that no response matches the request
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(LOG_TAG, "onErrorResponse: Error listener fired: " + error.getMessage());
                VolleyLog.d(LOG_TAG, "Error: " + error.getMessage());
                pbHostelDetails.setVisibility(View.GONE);
            }
        });
        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayReq, REQUEST_TAG);
    }

    private void init() {
        pbHostelDetails = (ProgressBar) findViewById(R.id.pbHostelDetails);
        ivHostelDetailsImage = (ImageView) findViewById(R.id.ivHostelDetailsImage);
        rbHostelDetailsRating = (RatingBar) findViewById(R.id.rbHostelDetailsRating);
        tvHostelDescription = (TextView) findViewById(R.id.tvHostelDetailsDescription);
        tvHostelName = (TextView) findViewById(R.id.tvHostelName);
        tvFacilities = (TextView) findViewById(R.id.tvFacilities);
        nSView = (NestedScrollView) findViewById(R.id.nsView);
        fabViewRooms = (FloatingActionButton) findViewById(R.id.fabViewRooms);
        tvLocation = (TextView) findViewById(R.id.tvHostelLocation);
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
        }else if(id == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
