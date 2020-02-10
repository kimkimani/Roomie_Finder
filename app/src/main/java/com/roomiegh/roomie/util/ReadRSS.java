package com.roomiegh.roomie.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;


import com.roomiegh.roomie.R;
import com.roomiegh.roomie.adapters.AdsAdapter;
import com.roomiegh.roomie.models.FeedItem;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static android.text.Html.FROM_HTML_MODE_LEGACY;

/**
 * Created by KayO on 14/08/2016.
 */
public class ReadRSS extends AsyncTask<Void, Void, Void> {
    Context context;
    ProgressDialog progressDialog;
    String address = "http://hostelads.blogspot.com/feeds/posts/default?alt=rss";
    URL url;
    ArrayList<FeedItem> feedItems;
    RecyclerView recyclerView;
    ScrollView svConnectionError;
    ProgressBar pbLoadAds;
    //final int N_WORDS = 40;

    public ReadRSS(Context context, RecyclerView recyclerView, ScrollView svConnectionError) {
        this.recyclerView = recyclerView;
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
    }

    public ReadRSS(Context context, RecyclerView recyclerView, ScrollView svConnectionError, ProgressBar pbLoadAds) {
        this.recyclerView = recyclerView;
        this.context = context;
        this.svConnectionError = svConnectionError;
        this.pbLoadAds = pbLoadAds;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
    }

    @Override
    protected void onPreExecute() {
        //progressDialog.show();
        pbLoadAds.setVisibility(View.VISIBLE);
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //progressDialog.dismiss();
        pbLoadAds.setVisibility(View.INVISIBLE);
        if (feedItems!=null){//Internet connection was established
            svConnectionError.setVisibility(View.GONE);
            AdsAdapter adapter  = new AdsAdapter(context, feedItems);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.addItemDecoration(new VerticalSpace(30));
            recyclerView.setAdapter(adapter);
        }else{
            recyclerView.setVisibility(View.GONE);
            svConnectionError.setVisibility(View.VISIBLE);
            svConnectionError.bringToFront();
            Toast.makeText(context, R.string.inactive_connection, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        processXml(getData());
        return null;
    }

    private void processXml(Document data) {
        if (data != null) {
            feedItems = new ArrayList<>();
            Element root = data.getDocumentElement();//root is rss
            Node channel = root.getChildNodes().item(0);
            NodeList items = channel.getChildNodes();
            for (int i = 0; i < items.getLength(); i++) {
                Node currentChild = items.item(i);
                if (currentChild.getNodeName().equalsIgnoreCase("item")) {
                    FeedItem item = new FeedItem();
                    NodeList itemChildren = currentChild.getChildNodes();
                    for (int j = 0; j < itemChildren.getLength(); j++) {
                        Node current = itemChildren.item(j);
                        if (current.getNodeName().equalsIgnoreCase("title")) {
                            item.setTitle(current.getTextContent());
                        }  else if (current.getNodeName().equalsIgnoreCase("description")) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                //item.setDescription(Html.fromHtml(current.getTextContent(),FROM_HTML_MODE_LEGACY));
                                item.setDescription(Html.fromHtml(current.getTextContent(), FROM_HTML_MODE_LEGACY, new Html.ImageGetter() {
                                    @Override
                                    public Drawable getDrawable(String s) {
                                        //Toast.makeText(context, s, Toast.LENGTH_LONG).show();

                                        Drawable drawable = null;
                                        if (s.startsWith("http")) {
                                            // load from internet

                                            URL sourceURL;
                                            try {
                                                sourceURL = new URL(s);
                                                URLConnection urlConnection = sourceURL.openConnection();
                                                urlConnection.connect();
                                                InputStream inputStream = urlConnection.getInputStream();
                                                BufferedInputStream bufferedInputStream =
                                                        new BufferedInputStream(inputStream);
                                                Bitmap bm = BitmapFactory.decodeStream(bufferedInputStream);

                                                //convert bitmap to drawable
                                                drawable = new BitmapDrawable(context.getResources(), bm);

                                                drawable.setBounds(0, 0, bm.getWidth(), bm.getHeight());
                                            } catch (MalformedURLException e) {
                                                e.printStackTrace();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        return drawable;
                                    }
                                }, null));
                            } else {
                                item.setDescription(Html.fromHtml(current.getTextContent(), new Html.ImageGetter() {
                                    @Override
                                    public Drawable getDrawable(String s) {
                                        //Toast.makeText(context, s, Toast.LENGTH_LONG).show();

                                        Drawable drawable = null;
                                        if (s.startsWith("http")) {
                                            // load from internet

                                            URL sourceURL;
                                            try {
                                                sourceURL = new URL(s);
                                                URLConnection urlConnection = sourceURL.openConnection();
                                                urlConnection.connect();
                                                InputStream inputStream = urlConnection.getInputStream();
                                                BufferedInputStream bufferedInputStream =
                                                        new BufferedInputStream(inputStream);
                                                Bitmap bm = BitmapFactory.decodeStream(bufferedInputStream);

                                                //convert bitmap to drawable
                                                drawable = new BitmapDrawable(context.getResources(), bm);

                                                drawable.setBounds(0, 0, bm.getWidth(), bm.getHeight());
                                            } catch (MalformedURLException e) {
                                                e.printStackTrace();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        return drawable;
                                    }
                                }, null));
                            }
                            //need a better method to filter out all html
                        } else if (current.getNodeName().equalsIgnoreCase("pubDate")) {
                            item.setPubDate(current.getTextContent().substring(0, current.getTextContent().length() - 5));
                        } else if (current.getNodeName().equalsIgnoreCase("link")) {
                            item.setLink(current.getTextContent());
                        } else if (current.getNodeName().equalsIgnoreCase("media:thumbnail")) {
                            String url = current.getAttributes().item(0).getTextContent();
                            item.setThumbnailUrl(url);
                        }
                    }
                    feedItems.add(item);
                    /*Log.d("itemTitle", item.getTitle());
                    Log.d("itemDescription", item.getDescription());
                    Log.d("itemLink", item.getLink());
                    Log.d("itemPubDate", item.getPubDate());
                    Log.d("itemThumbnailUrl", item.getThumbnailUrl());*/
                }
            }
        }
    }


    public Document getData() {
        try {
            url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            InputStream inputStream = connection.getInputStream();
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document xmlDoc = builder.parse(inputStream);
            return xmlDoc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
