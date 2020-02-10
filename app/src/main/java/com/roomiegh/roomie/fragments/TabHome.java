package com.roomiegh.roomie.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.roomiegh.roomie.R;
import com.roomiegh.roomie.activities.MainActivity;

/**
 * Created by Kwadwo Agyapon-Ntra on 06/10/2015.
 */
public class TabHome extends Fragment{
    private static final String HOME_TAG = "HomeAdsView";
    //private WebView wvHome;
    RecyclerView recyclerView;
    ScrollView svConnectionError;
    Context ctx;
    ProgressBar pbLoadAds;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_home,container,false);

        init(view);

        /*wvHome.setWebViewClient(new WebViewClient());
        wvHome.loadUrl("http://www.roomiegh.com");*/
        if (!connectedToNetwork()){
            //not connected to mobile data or WIFI
            Toast.makeText(getActivity(), R.string.check_connection, Toast.LENGTH_LONG).show();
            //fab.setVisibility(View.INVISIBLE);
        }
        /*ReadRSS readRSS =   new ReadRSS(getActivity(), recyclerView, svConnectionError);
        Log.d(HOME_TAG, "onCreateView: about to call ReadRSS");
        readRSS.execute();*/
        ((MainActivity)getActivity()).doRSSRead(getActivity(),recyclerView,svConnectionError,pbLoadAds);

        return view;
    }

    private void init(View view) {
        //wvHome = (WebView) view.findViewById(R.id.wvHome);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        svConnectionError = (ScrollView) view.findViewById(R.id.svConnectionError);
        pbLoadAds = (ProgressBar) view.findViewById(R.id.pbLoadAds);
    }

    private boolean connectedToNetwork() {
        /*//The code below will not work properly on the main thread
        InetAddress ipAddr = null;
        try{
            ipAddr = InetAddress.getByName("google.com");
            return !ipAddr.toString().equals("");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, ipAddr+"", Toast.LENGTH_LONG).show();
            return false;
        }*/
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo()!=null;
    }
}
