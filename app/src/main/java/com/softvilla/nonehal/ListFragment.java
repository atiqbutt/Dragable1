package com.softvilla.nonehal;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Hassan on 10/29/2017.
 */

public class ListFragment  extends Fragment{

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context mcontext, Intent intent) {
            if (intent != null ) {
                String str= intent.getStringExtra("des");
                String str1= intent.getStringExtra("title");
                String videoId= intent.getStringExtra("id");
                description.setText(str);
                title.setText(str1);
                description.setVisibility(View.GONE);
                Resources resources = context.getResources();
                Drawable img = resources.getDrawable(android.R.drawable.arrow_down_float);
                title.setCompoundDrawablesWithIntrinsicBounds(null,null,img,null);
                title.setTag("down");

                list = new ArrayList<>();

                for (videoInfo obj : data){
                    if(!obj.videoId.equalsIgnoreCase(videoId)){
                        list.add(obj);
                    }
                    else {
                        index = data.indexOf(obj);
                    }
                }

                PlayerPlayListAdapter adapter = new PlayerPlayListAdapter(context,list);
                listView.setAdapter(adapter);

                nextButton.setAlpha((float) 1.0);
                backButton.setAlpha((float) 1.0);

                if(index == 0){
                    //backButton.setAlpha((float) 0.5);
                }

                if(index == list.size()){
                    //nextButton.setAlpha((float) 0.5);
                }
                //Get all your data from intent and do what you want
            }
        }
    };

    ListView listView;
    ArrayList<videoInfo> data;
    Context context;
    private String pageToken = "";

    private AdView mAdView;
    InterstitialAd mInterstitialAd;
    TextView description,title;
    ArrayList<videoInfo> list;
    ExpandableRelativeLayout expandableLayout;
    String fullDescriptions;
    int scrolPosition = 0;
    int index = 0;
    ImageButton backButton;
    ImageButton nextButton;

    boolean isVisible = false;
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.list_fragment_layout, container, false);
        context = view.getContext();



        getActivity().getWindow().getDecorView().setSystemUiVisibility(//View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                //View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                //| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
        //| View.SYSTEM_UI_FLAG_FULLSCREEN
        //| View.SYSTEM_UI_FLAG_LOW_PROFILE);
        //| View.SYSTEM_UI_FLAG_IMMERSIVE);

        MobileAds.initialize(view.getContext(), "ca-app-pub-8535523132621797~5524459545");

        LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver,
                new IntentFilter("descriptionIntent"));
        data = new ArrayList<videoInfo>();
        listView = (ListView) view.findViewById(R.id.DragplayerListView);
        listView.setDividerHeight(0);
        //expandableLayout = (ExpandableRelativeLayout) view.findViewById(R.id.expandableLayout);
        description = (TextView) view.findViewById(R.id.videoDescription);
        title = (TextView) view.findViewById(R.id.title);
        title.setText(videoInfo.title);
        description.setText(videoInfo.des);
        index = videoInfo.index - 1;
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /* expandableLayout.toggle();
                expandableLayout.expand();
                expandableLayout.collapse();*/
                //getDescription();

                if(isVisible){
                    Resources resources = context.getResources();
                    Drawable img = resources.getDrawable(android.R.drawable.arrow_down_float);
                    title.setCompoundDrawablesWithIntrinsicBounds(null,null,img,null);
                    title.setTag("down");
                    description.setVisibility(View.GONE);

                   // title.setCompoundDrawables(R.drawable.arrows,null,null,null);
                    isVisible = false;
                }
                else {
                    description.setVisibility(View.VISIBLE);
                    Resources resources = context.getResources();
                    Drawable img = resources.getDrawable(android.R.drawable.arrow_up_float);
                    title.setCompoundDrawablesWithIntrinsicBounds(null,null,img,null);
                    title.setTag("up");
                    isVisible = true;
                }




            }
        });
       // title.setMovementMethod(new ScrollingMovementMethod());
        //((AppCompatActivity) getActivity()).getSupportActionBar().hide();

       // description.setText(videoInfo.des);
        final Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!pageToken.equalsIgnoreCase("")){
                    getListInBackground();
                }
                handler.postDelayed(this,20000);
            }
        },20000);
        /*handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                mInterstitialAd = new InterstitialAd(view.getContext());
                mInterstitialAd.setAdUnitId("ca-app-pub-4789001247034559/8354834764");
                AdRequest adRequest = new AdRequest.Builder().build();
                mInterstitialAd.loadAd(adRequest);
                mInterstitialAd.setAdListener(new AdListener() {
                    @Override

                    public void onAdLoaded() {
                        displayinterstitial();
                    }
                });
                handler.postDelayed(this,5000);
            }
        } , 5000);
*/
       /* if (!isNetworkAvailable()) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(R.string.alert_message)
                    .setTitle(R.string.alert_title)
                    .setCancelable(false)
                    .setPositiveButton(R.string.alert_positive,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    getActivity().getFragmentManager().beginTransaction().remove(getContext()).commit();//finish();
                                }
                            });

            AlertDialog alert = builder.create();
            alert.show();

        } else if (isNetworkAvailable())
*/
        getList();



        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //Toast.makeText(context, data.get(position).description, Toast.LENGTH_SHORT).show();

                        final ProgressDialog dialog = new ProgressDialog(context);
                        dialog.setMessage("Loading...");
                        dialog.show();

                        AndroidNetworking.get("https://www.googleapis.com/youtube/v3/videos")
                                .addQueryParameter("part", "snippet")
                                .addQueryParameter("id", list.get(position).videoId)
                                .addQueryParameter("key", "AIzaSyDbWDJqeZvJiUgo0TE7Qa2FgyadYDyqPzI")
                                .setTag("test")
                                .setPriority(Priority.LOW)
                                .build()
                                .getAsJSONObject(new JSONObjectRequestListener() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        try {
                                            JSONArray jsonArray = response.getJSONArray("items");
                                            JSONObject jsonObject = jsonArray.getJSONObject(0);

                                            //Toast.makeText(context, jsonObject.toString(), Toast.LENGTH_SHORT).show();
                                            JSONObject fullDescription = jsonObject.getJSONObject("snippet");

                                            fullDescriptions = fullDescription.getString("description");
                                            description.setText(fullDescriptions);


                                            //videoInfos.description = fullDescription;
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        JSONObject jsonObject = new JSONObject();

                                        //JSONObject sub = jsonObject.getJSONObject("snippet");


                                        dialog.dismiss();
                                        // do anything with response
                                    }
                                    @Override
                                    public void onError(ANError error) {
                                        dialog.dismiss();
                                        Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                                        // handle error
                                    }
                                });

                        title.setText(list.get(position).name);
                        Intent intent = new Intent("Your_IntentFilter_string");
                        intent.putExtra("id", list.get(position).videoId);
                        String Key = list.get(position).videoId;
                        //Put your all data using put extra
                        description.setVisibility(View.GONE);
                        Resources resources = context.getResources();
                        Drawable img = resources.getDrawable(android.R.drawable.arrow_down_float);
                        title.setCompoundDrawablesWithIntrinsicBounds(null,null,img,null);
                        title.setTag("down");

                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);



                        list = new ArrayList<>();

                        for (videoInfo obj : data){
                            if(!obj.videoId.equalsIgnoreCase(Key)){
                                list.add(obj);
                            }
                        }

                        index = position;

                        PlayerPlayListAdapter adapter = new PlayerPlayListAdapter(context,list);
                        listView.setAdapter(adapter);

                    }
                }
        );
        listView.setOnScrollListener(
                new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView absListView, int i) {
                        if(absListView.getLastVisiblePosition() == data.size() - 1){
                            scrolPosition = data.size() - 5;
                            if(!pageToken.equalsIgnoreCase("")){
                                Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
                                getList();
                            }

                        }
                    }

                    @Override
                    public void onScroll(AbsListView absListView, int i, int i1, int i2) {

                    }
                }
        );

        /*listView.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });*/

        backButton = (ImageButton) view.findViewById(R.id.backBtn);
        nextButton = (ImageButton) view.findViewById(R.id.nextBtn);

        if(index == 0){
            //backButton.setAlpha((float) 0.5);
            //index = data.size() - 1;
        }



        backButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(index > 0){
                            final ProgressDialog dialog = new ProgressDialog(context);
                            dialog.setMessage("Loading...");
                            dialog.show();

                            AndroidNetworking.get("https://www.googleapis.com/youtube/v3/videos")
                                    .addQueryParameter("part", "snippet")
                                    .addQueryParameter("id", list.get(index - 1).videoId)
                                    .addQueryParameter("key", "AIzaSyDbWDJqeZvJiUgo0TE7Qa2FgyadYDyqPzI")
                                    .setTag("test")
                                    .setPriority(Priority.LOW)
                                    .build()
                                    .getAsJSONObject(new JSONObjectRequestListener() {
                                        @Override
                                        public void onResponse(JSONObject response) {

                                            try {
                                                JSONArray jsonArray = response.getJSONArray("items");
                                                JSONObject jsonObject = jsonArray.getJSONObject(0);

                                                //Toast.makeText(context, jsonObject.toString(), Toast.LENGTH_SHORT).show();
                                                JSONObject fullDescription = jsonObject.getJSONObject("snippet");

                                                fullDescriptions = fullDescription.getString("description");
                                                description.setText(fullDescriptions);


                                                //videoInfos.description = fullDescription;
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            JSONObject jsonObject = new JSONObject();

                                            //JSONObject sub = jsonObject.getJSONObject("snippet");


                                            dialog.dismiss();
                                            // do anything with response
                                        }
                                        @Override
                                        public void onError(ANError error) {
                                            dialog.dismiss();
                                            Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                                            // handle error
                                        }
                                    });

                            title.setText(list.get(index - 1).name);
                            Intent intent = new Intent("Your_IntentFilter_string");
                            intent.putExtra("id", list.get(index - 1).videoId);
                            String Key = list.get(index - 1).videoId;
                            //Put your all data using put extra
                            description.setVisibility(View.GONE);
                            Resources resources = context.getResources();
                            Drawable img = resources.getDrawable(android.R.drawable.arrow_down_float);
                            title.setCompoundDrawablesWithIntrinsicBounds(null,null,img,null);
                            title.setTag("down");

                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);



                            list = new ArrayList<>();

                            for (videoInfo obj : data){
                                if(!obj.videoId.equalsIgnoreCase(Key)){
                                    list.add(obj);
                                }
                                else {
                                    index = data.indexOf(obj);
                                }
                            }

                            PlayerPlayListAdapter adapter = new PlayerPlayListAdapter(context,list);
                            listView.setAdapter(adapter);
                            if(index == 0){
                                index = data.size() - 1;
                                //backButton.setAlpha((float) 0.5);
                            }
                            nextButton.setAlpha((float) 1.0);
                        }
                    }
                }
        );
        nextButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(index < list.size()){
                            final ProgressDialog dialog = new ProgressDialog(context);
                            dialog.setMessage("Loading...");
                            dialog.show();

                            AndroidNetworking.get("https://www.googleapis.com/youtube/v3/videos")
                                    .addQueryParameter("part", "snippet")
                                    .addQueryParameter("id", list.get(index).videoId)
                                    .addQueryParameter("key", "AIzaSyDbWDJqeZvJiUgo0TE7Qa2FgyadYDyqPzI")
                                    .setTag("test")
                                    .setPriority(Priority.LOW)
                                    .build()
                                    .getAsJSONObject(new JSONObjectRequestListener() {
                                        @Override
                                        public void onResponse(JSONObject response) {

                                            try {
                                                JSONArray jsonArray = response.getJSONArray("items");
                                                JSONObject jsonObject = jsonArray.getJSONObject(0);

                                                //Toast.makeText(context, jsonObject.toString(), Toast.LENGTH_SHORT).show();
                                                JSONObject fullDescription = jsonObject.getJSONObject("snippet");

                                                fullDescriptions = fullDescription.getString("description");
                                                description.setText(fullDescriptions);


                                                //videoInfos.description = fullDescription;
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            JSONObject jsonObject = new JSONObject();

                                            //JSONObject sub = jsonObject.getJSONObject("snippet");


                                            dialog.dismiss();
                                            // do anything with response
                                        }
                                        @Override
                                        public void onError(ANError error) {
                                            dialog.dismiss();
                                            Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                                            // handle error
                                        }
                                    });

                            title.setText(list.get(index).name);
                            Intent intent = new Intent("Your_IntentFilter_string");
                            intent.putExtra("id", list.get(index).videoId);
                            String Key = list.get(index).videoId;
                            //Put your all data using put extra
                            description.setVisibility(View.GONE);
                            Resources resources = context.getResources();
                            Drawable img = resources.getDrawable(android.R.drawable.arrow_down_float);
                            title.setCompoundDrawablesWithIntrinsicBounds(null,null,img,null);
                            title.setTag("down");

                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);



                            list = new ArrayList<>();

                            for (videoInfo obj : data){
                                if(!obj.videoId.equalsIgnoreCase(Key)){
                                    list.add(obj);
                                }
                                else {
                                    index = data.indexOf(obj);
                                }
                            }

                            PlayerPlayListAdapter adapter = new PlayerPlayListAdapter(context,list);
                            listView.setAdapter(adapter);

                            if(index == list.size()){
                                index = 1;
                                //nextButton.setAlpha((float) 0.5);
                            }
                            backButton.setAlpha((float) 1.0);
                            //index++;
                        }
                    }
                }
        );
        return view;
    }

    public  void  getList(){
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Loading...");
        dialog.show();

        AndroidNetworking.get("https://www.googleapis.com/youtube/v3/playlistItems")
                .addQueryParameter("part", "snippet")
                .addQueryParameter("pageToken", pageToken)
                .addQueryParameter("playlistId", "PL1m7Ygg9vUeDh-iJLfUVFyL6hY4EVid-3")
                .addQueryParameter("maxResults", "20")
                .addQueryParameter("key", "AIzaSyDbWDJqeZvJiUgo0TE7Qa2FgyadYDyqPzI")
                .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            pageToken = response.getString("nextPageToken");
                        } catch (JSONException e) {
                            pageToken = "";
                            e.printStackTrace();
                        }

                        try {

                            //pageToken = response.getString("nextPageToken");
                            //Toast.makeText(MainActivity.this, pageToken, Toast.LENGTH_SHORT).show();
                            JSONArray jsonArray = response.getJSONArray("items");
                            for (int i = 0 ; i < jsonArray.length() ; i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                JSONObject sub = jsonObject.getJSONObject("snippet");

                                videoInfo videoInfos = new videoInfo();
                                videoInfos.name = String.valueOf(sub.get("title"));


                                videoInfos.videoId = sub.getJSONObject("resourceId").getString("videoId");

                                videoInfos.description = String.valueOf(sub.get("description"));

                                videoInfos.thumbnail = sub.getJSONObject("thumbnails").getJSONObject("medium").getString("url");


                                //JSONArray title = new JSONArray(sub);


                                data.add(videoInfos);
                            }

                            if(index == 0){
                                index = data.size() - 1;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //Toast.makeText(MainActivity.this, String.valueOf(response), Toast.LENGTH_SHORT).show();

                        list = new ArrayList<>();

                        for (videoInfo obj : data){
                            if(!obj.videoId.equalsIgnoreCase(videoInfo.id)){
                                list.add(obj);
                            }
                        }

                        if(index == list.size()){
                            nextButton.setAlpha((float) 0.5);
                        }


                        PlayerPlayListAdapter adapter = new PlayerPlayListAdapter(context,list);
                        listView.setAdapter(adapter);

                        listView.setSelection(scrolPosition);
                        dialog.dismiss();

                        // do anything with response
                    }
                    @Override
                    public void onError(ANError error) {
                        dialog.dismiss();
                        Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                        // handle error
                    }
                });
    }

    public  void  getListInBackground(){
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Loading...");
        //dialog.show();

        AndroidNetworking.get("https://www.googleapis.com/youtube/v3/playlistItems")
                .addQueryParameter("part", "snippet")
                .addQueryParameter("pageToken", pageToken)
                .addQueryParameter("playlistId", "PL1m7Ygg9vUeDh-iJLfUVFyL6hY4EVid-3")
                .addQueryParameter("maxResults", "20")
                .addQueryParameter("key", "AIzaSyDbWDJqeZvJiUgo0TE7Qa2FgyadYDyqPzI")
                .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            pageToken = response.getString("nextPageToken");
                        } catch (JSONException e) {
                            pageToken = "";
                            e.printStackTrace();
                        }
                        try {

                            //pageToken = response.getString("nextPageToken");
                            //Toast.makeText(MainActivity.this, pageToken, Toast.LENGTH_SHORT).show();
                            JSONArray jsonArray = response.getJSONArray("items");
                            for (int i = 0 ; i < jsonArray.length() ; i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                JSONObject sub = jsonObject.getJSONObject("snippet");

                                videoInfo videoInfos = new videoInfo();
                                videoInfos.name = String.valueOf(sub.get("title"));


                                videoInfos.videoId = sub.getJSONObject("resourceId").getString("videoId");

                                videoInfos.description = String.valueOf(sub.get("description"));

                                videoInfos.thumbnail = sub.getJSONObject("thumbnails").getJSONObject("medium").getString("url");


                                //JSONArray title = new JSONArray(sub);


                                data.add(videoInfos);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //Toast.makeText(MainActivity.this, String.valueOf(response), Toast.LENGTH_SHORT).show();

                        list = new ArrayList<>();

                        for (videoInfo obj : data){
                            if(!obj.videoId.equalsIgnoreCase(videoInfo.id)){
                                list.add(obj);
                            }
                        }


                        PlayerPlayListAdapter adapter = new PlayerPlayListAdapter(context,list);
                        listView.setAdapter(adapter);

                        // do anything with response
                    }
                    @Override
                    public void onError(ANError error) {
                        dialog.dismiss();
                        Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                        // handle error
                    }
                });
    }


    public void displayinterstitial()
    {
        if (mInterstitialAd.isLoaded())
        {mInterstitialAd.show();}
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();

        if (mAdView != null) {
            mAdView.resume();
        }

    }
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }



    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
