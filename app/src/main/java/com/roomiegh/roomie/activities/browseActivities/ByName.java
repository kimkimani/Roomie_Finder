package com.roomiegh.roomie.activities.browseActivities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
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

public class ByName extends AppCompatActivity {
    private static final String LOG_TAG = "ByNameLog";
    private static final String REQUEST_TAG = "hostels_by_name_request";
    private static final String BROWSE_TYPE = "name";

    Toolbar toolbar;
    String url = "http://roomiegh.herokuapp.com/hostel";
    ProgressBar pbByName;
    ArrayList<Hostel> allHostels;
    ListView lvHostelsByName;
    HostelListAdapter hostelListAdapter;
    TextView tvNothingToShow;

    public ProgressBar getPbByName() {
        return pbByName;
    }

    //Search variables
    private MenuItem mSearchAction;
    private boolean isSearchOpened = false;
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_by_name);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        init();

        lvHostelsByName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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

        callForDetails();
    }

    private void callForDetails() {
        pbByName.setVisibility(View.VISIBLE);
        tvNothingToShow.setVisibility(View.GONE);
        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(LOG_TAG, response.toString());

                        if (response.length() > 0) {
                            JSONObject jsonData;
                            JSONArray hostelPicsArray;
                            Hostel hostel;
                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    jsonData = response.getJSONObject(i);
                                    hostel = new Hostel();//make sure to redifine hostel inside loop to avoid filling the arraylist with the same elements
                                    hostel.setId(jsonData.getInt("id"));
                                    hostel.setName(jsonData.getString("name"));
                                    hostel.setLocationId(jsonData.getInt("locations_location_id"));
                                    hostel.setNoOfRooms(jsonData.getInt("noOfRooms"));
                                    hostel.setRating(jsonData.getDouble("rating"));

                                    //TODO parse JSON right to get images
                                    hostelPicsArray = jsonData.getJSONArray("hostel_pics_small");
                                    if (hostelPicsArray.length() > 0)
                                        hostel.setPhotopath(hostelPicsArray.getJSONObject(0).getString("image_url"));
                                    else
                                        hostel.setPhotopath("");

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
                                pbByName.setVisibility(View.GONE);
                                tvNothingToShow.setVisibility(View.GONE);
                            }
                        } else {
                            // TODO: 09/05/2017 Show that no response matches the request
                            pbByName.setVisibility(View.GONE);
                            tvNothingToShow.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(LOG_TAG, "onErrorResponse: Error listener fired: " + error.getMessage());
                Log.d(LOG_TAG, "onErrorResponse: "+error.toString());
                if(error.toString().contains("NoConnectionError")){
                    Toast.makeText(ByName.this, "Your internet connection might be down", Toast.LENGTH_LONG).show();
                    pbByName.setVisibility(View.GONE);
                    tvNothingToShow.setVisibility(View.VISIBLE);
                }
                VolleyLog.d(LOG_TAG, "Error: " + error.getMessage());
                error.printStackTrace();
                pbByName.setVisibility(View.GONE);
            }
        });
        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayReq, REQUEST_TAG);
    }

    private void init() {
        pbByName = (ProgressBar) findViewById(R.id.pbByName);
        lvHostelsByName = (ListView) findViewById(R.id.lvHostelNames);
        allHostels = new ArrayList<Hostel>();
        hostelListAdapter = new HostelListAdapter(getApplicationContext(), allHostels);
        lvHostelsByName.setAdapter(hostelListAdapter);
        tvNothingToShow = (TextView) findViewById(R.id.tvNothingToShow);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //TODO: change menu_announcements to menu_by_name when search functions are ready
        getMenuInflater().inflate(R.menu.menu_announcements, menu);
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
        }else if(id == R.id.action_search){
            handleMenuSearch();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchAction = menu.findItem(R.id.action_search);
        return super.onPrepareOptionsMenu(menu);
    }

    protected void handleMenuSearch(){
        ActionBar action = getSupportActionBar(); //get the actionbar

        if(isSearchOpened){ //test if the search is open

            action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
            action.setDisplayShowTitleEnabled(true); //show the title in the action bar

            //hides the keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);

            //add the search icon in the action bar
            mSearchAction.setIcon(getResources().getDrawable(android.R.drawable.ic_menu_search));

            isSearchOpened = false;
        } else { //open the search entry

            action.setDisplayShowCustomEnabled(true); //enable it to display a
            // custom view in the action bar.
            action.setCustomView(R.layout.search_bar);//add the custom view
            action.setDisplayShowTitleEnabled(false); //hide the title

            etSearch = (EditText)action.getCustomView().findViewById(R.id.edtSearch); //the text editor

            //this is a listener to do a search when the user clicks on search button
            etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        doSearch();
                        return true;
                    }
                    return false;
                }
            });


            etSearch.requestFocus();

            //open the keyboard focused in the edtSearch
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT);


            //add the close icon
            mSearchAction.setIcon(getResources().getDrawable(android.R.drawable.ic_menu_close_clear_cancel));

            isSearchOpened = true;
        }
    }

    private void doSearch() {

    }

    @Override
    public void onBackPressed() {
        if(isSearchOpened) {
            handleMenuSearch();
            return;
        }
        super.onBackPressed();
    }
}
