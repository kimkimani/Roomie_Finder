package com.roomiegh.roomie.activities.browseActivities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.roomiegh.roomie.R;
import com.roomiegh.roomie.activities.HostelDetailsActivity;
import com.roomiegh.roomie.adapters.HostelListAdapter;
import com.roomiegh.roomie.models.Hostel;
import com.roomiegh.roomie.volley.AppSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ByType extends AppCompatActivity {
    private static final String LOG_TAG = "ByTypeLog";
    private static final String REQUEST_TAG = "hostels_by_type_request";
    private static final String BROWSE_TYPE = "type";

    private Toolbar toolbar;
    private ImageView ivType1, ivType2, ivType3, ivType4;
    private LinearLayout llRoomTypes;
    private ListView lvHostelsByType;
    String type_specific_url = "http://roomiegh.herokuapp.com/roomtype/";
    ArrayList<Hostel> allHostels;
    HostelListAdapter hostelListAdapter;
    private ProgressBar pbByType;
    int roomType = -1;
    private TextView tvNothingToShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_by_type);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        init();

        lvHostelsByType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle pushBrowseTypeBundle = new Bundle();
                //edit the string that is pushed to reflect room type
                pushBrowseTypeBundle.putString("browse_type", BROWSE_TYPE);
                int hostelID = ((Hostel) hostelListAdapter.getItem(position)).getId();
                pushBrowseTypeBundle.putInt("hostel_id",hostelID);
                pushBrowseTypeBundle.putInt("room_type",roomType);
                Intent hostelDetailsIntent =
                        new Intent(getApplicationContext(), HostelDetailsActivity.class);
                hostelDetailsIntent.putExtra("type_bundle", pushBrowseTypeBundle);

                //open HostelDetailsActivity using intent and pass selectedHostel to it
                startActivity(hostelDetailsIntent);
            }
        });

        ivType1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ByType.this.setTitle("1-in-1");
                roomType = 1;
                callForHostelsByType(roomType);
            }
        });
        ivType2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ByType.this.setTitle("2-in-1");
                roomType = 2;
                callForHostelsByType(roomType);
            }
        });
        ivType3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ByType.this.setTitle("3-in-1");
                roomType = 3;
                callForHostelsByType(roomType);
            }
        });
        ivType4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ByType.this.setTitle("4-in-1");
                roomType = 4;
                callForHostelsByType(roomType);
            }
        });

    }

    private void callForHostelsByType(int type) {
        llRoomTypes.setVisibility(View.GONE);
        lvHostelsByType.setVisibility(View.VISIBLE);
        String queryURL = type_specific_url + type;

        allHostels.clear();
        pbByType.setVisibility(View.VISIBLE);
        tvNothingToShow.setVisibility(View.GONE);
        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(Request.Method.GET, queryURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(LOG_TAG, "response: " + response.toString());

                        if (response.length() > 0) {
                            JSONObject jsonData;
                            Hostel hostel;
                            JSONObject jsonHostel;
                            try {
                                for (int j = 0; j < response.length(); j++) {
                                    jsonData = response.getJSONObject(j);

                                    jsonHostel = jsonData.getJSONObject("roomhostel");
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
                                pbByType.setVisibility(View.GONE);
                                tvNothingToShow.setVisibility(View.GONE);
                            }
                        } else {
                            // TODO: 09/05/2017 Show that no response matches the request
                            pbByType.setVisibility(View.GONE);
                            tvNothingToShow.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(LOG_TAG, "onErrorResponse: Error listener fired: " + error.getMessage());
                Log.d(LOG_TAG, "onErrorResponse: "+error.toString());
                if(error.toString().contains("NoConnectionError")){
                    Toast.makeText(ByType.this, "Your internet connection might be down", Toast.LENGTH_LONG).show();
                    pbByType.setVisibility(View.GONE);
                    tvNothingToShow.setVisibility(View.VISIBLE);
                }
                VolleyLog.d(LOG_TAG, "Error: " + error.getMessage());
                error.printStackTrace();
                pbByType.setVisibility(View.GONE);
            }
        });
        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayReq, REQUEST_TAG);
    }

    private void init() {
        ivType1 = (ImageView) findViewById(R.id.ivType1);
        ivType2 = (ImageView) findViewById(R.id.ivType2);
        ivType3 = (ImageView) findViewById(R.id.ivType3);
        ivType4 = (ImageView) findViewById(R.id.ivType4);
        llRoomTypes = (LinearLayout) findViewById(R.id.llRoomTypes);
        lvHostelsByType = (ListView) findViewById(R.id.lvHostelsByType);
        allHostels = new ArrayList<Hostel>();
        hostelListAdapter = new HostelListAdapter(getApplicationContext(), allHostels);
        lvHostelsByType.setAdapter(hostelListAdapter);
        pbByType = (ProgressBar) findViewById(R.id.pbByType);
        tvNothingToShow = (TextView) findViewById(R.id.tvNothingToShow);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recent, menu);
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
            if (ByType.this.getTitle().toString().endsWith("in-1")) {
                //do not finish yet
                ByType.this.setTitle("Browse By Type");
                llRoomTypes.setVisibility(View.VISIBLE);
                lvHostelsByType.setVisibility(View.GONE);
                pbByType.setVisibility(View.GONE);
            }else{
                finish();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
