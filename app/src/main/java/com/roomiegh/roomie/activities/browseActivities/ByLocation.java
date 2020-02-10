package com.roomiegh.roomie.activities.browseActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.roomiegh.roomie.R;
import com.roomiegh.roomie.activities.HostelDetailsActivity;
import com.roomiegh.roomie.adapters.HostelListAdapter;
import com.roomiegh.roomie.fragments.HorizontalListViewFragment;
import com.roomiegh.roomie.models.Hostel;
import com.roomiegh.roomie.volley.AppSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ByLocation extends AppCompatActivity {
    private static final String LOG_TAG = "ByLocationLog";
    private static final String REQUEST_TAG = "hostels_by_location_request";
    private static final String BROWSE_TYPE = "location";
    private Toolbar toolbar;
    FragmentManager fm;
    Fragment fragment;
    ProgressBar pbLocations, pbLocationHostels;
    ListView lvLocationHostels;
    String location_specific_url = "http://roomiegh.herokuapp.com/locationhostel/";
    ArrayList<Hostel> allHostels;
    HostelListAdapter hostelListAdapter;
    Hostel selectedHostel = new Hostel();
    TextView tvNothingToShow;
    private TextView tvLocationsUnavailable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_by_location);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        init();

        fm = getSupportFragmentManager();
        fragment = fm.findFragmentById(R.id.fragmentContainer);

        if (fragment == null) {
            fragment = new HorizontalListViewFragment();
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }

        lvLocationHostels.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedHostel = (Hostel) lvLocationHostels.getItemAtPosition(position);
                //Bundle category from which we are looking for hostel details... so we know what the user is looking for
                //i.e. By Location or Room Type or Price or Name
                Bundle pushBrowseTypeBundle = new Bundle();
                pushBrowseTypeBundle.putString("browse_type", BROWSE_TYPE);
                int hostelID = ((Hostel) hostelListAdapter.getItem(position)).getId();
                pushBrowseTypeBundle.putInt("hostel_id", hostelID);
                Intent hostelDetailsIntent =
                        new Intent(getApplicationContext(), HostelDetailsActivity.class);
                hostelDetailsIntent.putExtra("type_bundle", pushBrowseTypeBundle);

                //open HostelDetailsActivity using intent and pass selectedHostel to it
                startActivity(hostelDetailsIntent);
            }
        });

    }

    private void init() {
        pbLocations = (ProgressBar) findViewById(R.id.pbLocations);
        tvLocationsUnavailable = (TextView) findViewById(R.id.tvLocationsUnavailable);
        pbLocationHostels = (ProgressBar) findViewById(R.id.pbLocationHostels);
        lvLocationHostels = (ListView) findViewById(R.id.lvLocationHostels);
        allHostels = new ArrayList<Hostel>();
        hostelListAdapter = new HostelListAdapter(getApplicationContext(), allHostels);
        lvLocationHostels.setAdapter(hostelListAdapter);
        tvNothingToShow = (TextView) findViewById(R.id.tvNothingToShow);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_by_location, menu);
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
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public ProgressBar getPbLocations() {
        return pbLocations;
    }

    public TextView getTvLocationsUnavailable() {
        return tvLocationsUnavailable;
    }

    public void callForLocationHostels(int locationID) {
        allHostels.clear();
        pbLocationHostels.setVisibility(View.VISIBLE);
        tvNothingToShow.setVisibility(View.GONE);
        lvLocationHostels.setVisibility(View.GONE);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, location_specific_url + locationID, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(LOG_TAG, response.toString());

                        try {
                            if (response.has("location_hostels") && response.getJSONArray("location_hostels").length()>0) {
                                //JSONArray jsonData;
                                Hostel hostel;
                                JSONArray hostelsArray;
                                JSONObject jsonHostel;
                                try {
                                    // TODO: 08/05/2017 Change JSON parsing for this call

                                    //jsonData = response.getJSONArray("location_hostels");
                                    hostelsArray = response.getJSONArray("location_hostels");

                                    for (int j = 0; j < hostelsArray.length(); j++) {
                                        jsonHostel = hostelsArray.getJSONObject(j);
                                        hostel = new Hostel();//make sure to redefine hostel inside loop to avoid filling the arraylist with the same elements
                                        hostel.setId(jsonHostel.getInt("id"));
                                        hostel.setName(jsonHostel.getString("name"));
                                        hostel.setLocationId(jsonHostel.getInt("locations_location_id"));
                                        hostel.setNoOfRooms(jsonHostel.getInt("noOfRooms"));
                                        hostel.setRating(jsonHostel.getDouble("rating"));
                                        //if (jsonHostel.getString("photoPath") != null)
                                        if (jsonHostel.has("hostel_pics_small")) {
                                            if(jsonHostel.getJSONArray("hostel_pics_small").length()>0){
                                                hostel.setPhotopath(
                                                        jsonHostel.getJSONArray("hostel_pics_small")
                                                                .getJSONObject(0).getString("image_url"));
                                            }else{
                                                hostel.setPhotopath("");
                                            }
                                        }else{
                                            hostel.setPhotopath("");
                                        }

                                        //add hostel to list
                                        allHostels.add(hostel);
                                        Log.d(LOG_TAG, "Added: " + hostel.getName());
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.d(LOG_TAG, "Exception encountered: " + e.toString());
                                } finally {
                                    //Toast.makeText(ByName.this, ""+allHostels.toString(), Toast.LENGTH_LONG).show();
                                    hostelListAdapter.setAllHostels(allHostels);
                                    hostelListAdapter.notifyDataSetChanged();
                                    pbLocationHostels.setVisibility(View.GONE);
                                    tvNothingToShow.setVisibility(View.GONE);
                                    lvLocationHostels.setVisibility(View.VISIBLE);
                                }
                            } else {
                                // TODO: 09/05/2017 Show that no response matches the request
                                pbLocationHostels.setVisibility(View.GONE);
                                tvNothingToShow.setVisibility(View.VISIBLE);
                                lvLocationHostels.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(LOG_TAG, "onErrorResponse: Error listener fired: " + error.getMessage());
                Log.d(LOG_TAG, "onErrorResponse: "+error.toString());
                if(error.toString().contains("NoConnectionError")){
                    Toast.makeText(ByLocation.this, "Your internet connection might be down", Toast.LENGTH_LONG).show();
                    pbLocationHostels.setVisibility(View.GONE);
                    tvNothingToShow.setVisibility(View.VISIBLE);
                    lvLocationHostels.setVisibility(View.GONE);
                }
                VolleyLog.d(LOG_TAG, "Error: " + error.getMessage());
                error.printStackTrace();
                pbLocationHostels.setVisibility(View.GONE);
            }
        });
        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq, REQUEST_TAG);

    }

}
