package com.roomiegh.roomie.activities.browseActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
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

/*Prototyping by price page - Friday, 05/05/2017*/
public class ByPrice extends AppCompatActivity {
    private static final String LOG_TAG = "ByPriceLog";
    private static final String REQUEST_TAG = "hostels_by_price_request";
    private static final String BROWSE_TYPE = "price";

    private Toolbar toolbar;
    private SeekBar sbMax, sbMin;
    private EditText etMaxPrice, etMinPrice;
    private FloatingActionButton fabSearchByPrice;
    private ListView lvHostelsByPrice;
    private ProgressBar pbHostelsByPrice;
    private LinearLayout llShowOrHidePrices;
    String price_specific_url = "http://roomiegh.herokuapp.com/roomprice/";
    ArrayList<Hostel> allHostels;
    HostelListAdapter hostelListAdapter;
    int max = -1,min = -1;
    private TextView tvNothingToShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_by_price);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        init();

        //max price seekbar
        sbMax.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                etMaxPrice.setText(progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //min price seekbar
        sbMin.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                etMinPrice.setText(progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        etMaxPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!(etMaxPrice.getText().toString().equals("")))
                    sbMax.setProgress(Integer.valueOf(etMaxPrice.getText().toString()));
                else
                    etMaxPrice.setText(0 + "");
                etMaxPrice.setSelection(etMaxPrice.getText().length());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etMinPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!(etMinPrice.getText().toString().equals("")))
                    sbMin.setProgress(Integer.valueOf(etMinPrice.getText().toString()));
                else
                    etMinPrice.setText(0 + "");
                etMinPrice.setSelection(etMinPrice.getText().length());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        fabSearchByPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llShowOrHidePrices.getVisibility() == View.VISIBLE) {
                    //hide view and try to search
                    llShowOrHidePrices.setVisibility(View.GONE);
                    if (pricesCheckOut(sbMax.getProgress(), sbMin.getProgress())) {
                        max = sbMax.getProgress();
                        min = sbMin.getProgress();
                        //prices are alright, search hostels
                        queryHostelsByPrice(sbMax.getProgress(), sbMin.getProgress());
                        hideSoftKeyboard();
                    } else {
                        //problem with prices. Show view
                        Toast.makeText(ByPrice.this, "Check your prices", Toast.LENGTH_SHORT).show();
                        llShowOrHidePrices.setVisibility(View.VISIBLE);
                    }

                } else {
                    //open up view to edit prices again
                    llShowOrHidePrices.setVisibility(View.VISIBLE);
                }

            }
        });

        lvHostelsByPrice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle pushBrowseTypeBundle = new Bundle();
                pushBrowseTypeBundle.putString("browse_type", BROWSE_TYPE);
                int hostelID = ((Hostel) hostelListAdapter.getItem(position)).getId();
                pushBrowseTypeBundle.putInt("hostel_id",hostelID);
                pushBrowseTypeBundle.putInt("max_price",max);
                pushBrowseTypeBundle.putInt("min_price",min);
                Intent hostelDetailsIntent =
                        new Intent(getApplicationContext(), HostelDetailsActivity.class);
                hostelDetailsIntent.putExtra("type_bundle", pushBrowseTypeBundle);

                //open HostelDetailsActivity using intent and pass selectedHostel to it
                startActivity(hostelDetailsIntent);
            }
        });


    }

    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(fabSearchByPrice.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private boolean pricesCheckOut(int max, int min) {
        return max >= min;
    }

    private void queryHostelsByPrice(int maxPrice, int minPrice) {
        allHostels.clear();
        pbHostelsByPrice.setVisibility(View.VISIBLE);
        tvNothingToShow.setVisibility(View.GONE);
        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(Request.Method.GET, price_specific_url + minPrice + "/" + maxPrice, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(LOG_TAG, response.toString());

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
                                pbHostelsByPrice.setVisibility(View.GONE);
                                tvNothingToShow.setVisibility(View.GONE);
                            }
                        }else{
                            // TODO: 09/05/2017 Show that no response matches the request
                            pbHostelsByPrice.setVisibility(View.GONE);
                            tvNothingToShow.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(LOG_TAG, "onErrorResponse: Error listener fired: " + error.getMessage());
                Log.d(LOG_TAG, "onErrorResponse: "+error.toString());
                if(error.toString().contains("NoConnectionError")){
                    Toast.makeText(ByPrice.this, "Your internet connection might be down", Toast.LENGTH_LONG).show();
                    pbHostelsByPrice.setVisibility(View.GONE);
                    tvNothingToShow.setVisibility(View.VISIBLE);
                }
                VolleyLog.d(LOG_TAG, "Error: " + error.getMessage());
                error.printStackTrace();
            }
        });
        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayReq, REQUEST_TAG);

    }

    private void init() {
        pbHostelsByPrice = (ProgressBar) findViewById(R.id.pbByPrice);
        llShowOrHidePrices = (LinearLayout) findViewById(R.id.llShowOrHidePrices);
        sbMax = (SeekBar) findViewById(R.id.sbMax);
        sbMin = (SeekBar) findViewById(R.id.sbMin);
        etMaxPrice = (EditText) findViewById(R.id.etMaxPrice);
        etMinPrice = (EditText) findViewById(R.id.etMinPrice);
        fabSearchByPrice = (FloatingActionButton) findViewById(R.id.fabSearchByPrice);
        lvHostelsByPrice = (ListView) findViewById(R.id.lvHostelsByPrice);
        allHostels = new ArrayList<Hostel>();
        hostelListAdapter = new HostelListAdapter(getApplicationContext(), allHostels);
        lvHostelsByPrice.setAdapter(hostelListAdapter);
        tvNothingToShow = (TextView) findViewById(R.id.tvNothingToShow);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_news, menu);
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
        }else if(id == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
