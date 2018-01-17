package com.softvilla.nonehal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.github.pedrovgs.DraggableListener;
import com.github.pedrovgs.DraggablePanel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.mancj.materialsearchbar.MaterialSearchBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DragableTest extends AppCompatActivity {


    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null ) {
                String str= intent.getStringExtra("id");
                try {
                    youtubePlayer.loadVideo(str);
                    getWindow().getDecorView().setSystemUiVisibility(//View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            //View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            //| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE);
                }
                catch (Exception e){
                    VIDEO_KEY = str;
                }
                //Get all your data from intent and do what you want
            }
        }
    };
    private static final String YOUTUBE_API_KEY = "AIzaSyDbWDJqeZvJiUgo0TE7Qa2FgyadYDyqPzI";
    private static String VIDEO_KEY = "";


    private List<String> lastSearches;
    private MaterialSearchBar searchBar;
    PlayerPlayListAdapter adapter;


    Toolbar toolbar;

    DraggablePanel draggablePanel;

    ArrayList<videoInfo> data;
    ArrayList<videoInfo> hideData;
    String pageToken = "";
    ListView listView;
    int scrolPosition = 0;
    private YouTubePlayer youtubePlayer;
    private YouTubePlayerSupportFragment youtubeFragment;
    DrawerLayout drawerlayoutLeft;
    boolean isFirst = false, isBackMinimize = false, isFull = false, isSearch = false, isPause = false;

    private SearchView searchView;
    private MenuItem searchMenuItem;

    private AdView mAdView;
    InterstitialAd mInterstitialAd;
    ArrayList<videoInfo> searchArray;
    String fullDescriptions;

    SearchView searchViewAndroidActionBar = null;

    int seekTime;

    TextView title;
    EditText search;
    ImageView backArrow, searchBtn;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dragable_test);



        getWindow().getDecorView().setSystemUiVisibility(//View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                 //View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                //| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                  View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                 | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                 | View.SYSTEM_UI_FLAG_IMMERSIVE);
                 //| View.SYSTEM_UI_FLAG_FULLSCREEN
                //| View.SYSTEM_UI_FLAG_LOW_PROFILE);
                //| View.SYSTEM_UI_FLAG_IMMERSIVE);

        MobileAds.initialize(this, "ca-app-pub-8535523132621797~5524459545");


        adapter = new PlayerPlayListAdapter(this,data);


        title = (TextView) findViewById(R.id.title);
        search = (EditText) findViewById(R.id.searchEdit);

        searchBtn = (ImageView) findViewById(R.id.searchBtn);
        backArrow = (ImageView) findViewById(R.id.backArrows);

        search.setVisibility(View.GONE);
        backArrow.setVisibility(View.GONE);

        searchBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        searchBtn.setVisibility(View.GONE);
                        title.setVisibility(View.GONE);

                        search.setVisibility(View.VISIBLE);
                        backArrow.setVisibility(View.VISIBLE);

                        search.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                    }
                }
        );

        backArrow.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        search.setText("");
                        searchBtn.setVisibility(View.VISIBLE);
                        title.setVisibility(View.VISIBLE);

                        search.setVisibility(View.GONE);
                        backArrow.setVisibility(View.GONE);

                        InputMethodManager imm = (InputMethodManager) DragableTest.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
        );

        search.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable newText) {
                        if(newText.toString().equalsIgnoreCase("")){
                            isSearch = false;
                        }
                        else {
                            isSearch = true;
                        }
                        if(isFirst){
                            if(draggablePanel.isMaximized() && !newText.toString().isEmpty()){
                                draggablePanel.minimize();
                            }
                        }

                        searchArray = new ArrayList<>();

                        for(videoInfo obj : data){
                            if(obj.name.toLowerCase().contains(newText.toString().toLowerCase())){
                                searchArray.add(obj);

                            }
                        }
                        if(newText.toString().length() == 0){
                            isSearch = false;
                        }
                        else {
                            isSearch = true;
                        }
                        PlayListAdapter adapter = new PlayListAdapter(DragableTest.this,searchArray);
                        listView.setAdapter(adapter);
                    }
                }
        );


        // Add Text Change Listener to EditText


       // searchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
        //searchBar.setHint("Search");
        //searchBar.setOnSearchActionListener(DragableTest.this);
        //searchBar.setCustomSuggestionAdapter();
        //searchBar.setSpeechMode(true);
        //enable searchbar callbacks
        //restore last queries from disk
        //lastSearches = loadSearchSuggestionFromDisk();
        //searchBar.setLastSuggestions(list);
        //Inflate menu and setup OnMenuItemClickListener
        //searchBar.inflateMenu(R.menu.main);
        //searchBar.getMenu().setOnMenuItemClickListener(DragableTest.this);

        final Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final Runnable runnable = this;
                mInterstitialAd = new InterstitialAd(DragableTest.this);
                mInterstitialAd.setAdUnitId("ca-app-pub-8535523132621797/9567770049");
                AdRequest adRequest = new AdRequest.Builder()
                        .build();
                mInterstitialAd.loadAd(adRequest);


                mInterstitialAd.setAdListener(new AdListener() {
                    @Override

                    public void onAdLoaded() {
                        displayinterstitial();
                    }

                    @Override
                    public void onAdOpened() {
                        mInterstitialAd.setAdListener(null);
                    }



                    @Override
                    public void onAdClosed() {
                        mInterstitialAd.setAdListener(this);
                        handler.postDelayed(runnable,60000);
                    }
                });

            }
        } , 60000);

        /*toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Children App");
        setActionBar(toolbar);*/


        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();

        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {

                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                //Toast.makeText(DragableTest.this, String.valueOf(errorCode), Toast.LENGTH_LONG).show();
                // Code to be executed when an ad request fails.

            }

            @Override
            public void onAdOpened() {

                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdLeftApplication() {

                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {

                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
            }
        });


        Bundle extras = getIntent().getExtras();

        if(extras != null){
            VIDEO_KEY = extras.getString("id");
        }

        LocalBroadcastManager.getInstance(DragableTest.this).registerReceiver(mMessageReceiver,
                new IntentFilter("Your_IntentFilter_string"));


        draggablePanel = (DraggablePanel) findViewById(R.id.draggable_panel);
        //drawerlayoutLeft = findViewById(R.id.drawer_left);






        AndroidNetworking.initialize(getApplicationContext());

        data = new ArrayList<videoInfo>();
        hideData = new ArrayList<videoInfo>();

        listView = (ListView) findViewById(R.id.videoList);

        listView.setDividerHeight(0);

        listView.setOnScrollListener(
                new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView absListView, int i) {
                        if(absListView.getLastVisiblePosition() == data.size() - 1){
                            scrolPosition = data.size() - 5;
                            if(!pageToken.equalsIgnoreCase("")){
                                getList();
                            }

                        }
                    }

                    @Override
                    public void onScroll(AbsListView absListView, int i, int i1, int i2) {

                    }
                }
        );

        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {

                        final Intent intent = new Intent("descriptionIntent");

                        final ProgressDialog dialog = new ProgressDialog(DragableTest.this);
                        dialog.setMessage("Loading...");
                        dialog.show();

                        AndroidNetworking.get("https://www.googleapis.com/youtube/v3/videos")
                                .addQueryParameter("part", "snippet")
                                .addQueryParameter("id", data.get(position).videoId)
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
                                           // description.setText(fullDescriptions);
                                            //data.get(position).description = fullDescriptions;

                                            videoInfo.des = fullDescriptions;
                                            //videoInfos.description = fullDescription;
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        JSONObject jsonObject = new JSONObject();

                                        //JSONObject sub = jsonObject.getJSONObject("snippet");


                                        videoInfo.id = data.get(position).videoId;
                                        if(!isFirst){
                                            if(isSearch){
                                                VIDEO_KEY = searchArray.get(position).videoId;
                                            }
                                            else {
                                                VIDEO_KEY = data.get(position).videoId;
                                                videoInfo.index = position + 1;
                                            }

                                            initializeYoutubeFragment();
                                            initializeDraggablePanel();
                                            isFirst = true;
                                        }
                                        else {
                                            draggablePanel.maximize();
                                            if(isSearch){
                                                VIDEO_KEY = searchArray.get(position).videoId;
                                                youtubePlayer.loadVideo(searchArray.get(position).videoId);
                                            }
                                            else {
                                                VIDEO_KEY = data.get(position).videoId;
                                                youtubePlayer.loadVideo(data.get(position).videoId);
                                                videoInfo.index = position + 1;
                                            }

                                        }
                                        InputMethodManager imm = (InputMethodManager) DragableTest.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);



                                        //Toast.makeText(DragableTest.this, fullDescriptions, Toast.LENGTH_SHORT).show();
                                        if (isSearch){

                                            videoInfo.title = searchArray.get(position).name;
                                            intent.putExtra("des", fullDescriptions);
                                            intent.putExtra("title", searchArray.get(position).name);
                                            intent.putExtra("id", searchArray.get(position).videoId);
                                        }
                                        else {
                                            //videoInfo.des = fullDescriptions;
                                            videoInfo.title = data.get(position).name;
                                            intent.putExtra("title", data.get(position).name);
                                            intent.putExtra("des", fullDescriptions);
                                            intent.putExtra("id", data.get(position).videoId);
                                        }

                                        //Put your all data using put extra
                                        //searchViewAndroidActionBar.clearFocus();

                                        LocalBroadcastManager.getInstance(DragableTest.this).sendBroadcast(intent);
                                        dialog.dismiss();
                                        // do anything with response
                                    }
                                    @Override
                                    public void onError(ANError error) {
                                        dialog.dismiss();
                                        Toast.makeText(DragableTest.this, error.toString(), Toast.LENGTH_LONG).show();
                                        // handle error
                                    }
                                });


                    }
                }
        );

        if (!isNetworkAvailable()) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.alert_message)
                    .setTitle(R.string.alert_title)
                    .setCancelable(false)
                    .setPositiveButton(R.string.alert_positive,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    finish();
                                }
                            });

            AlertDialog alert = builder.create();
            alert.show();

        } else if (isNetworkAvailable())
        getList();




        draggablePanel.setDraggableListener(
                new DraggableListener() {
                    @Override
                    public void onMaximized() {
                        //(DragableTest.this).getSupportActionBar().hide();
                    }

                    @Override
                    public void onMinimized() {
                        //(DragableTest.this).getSupportActionBar().show();

                    }

                    @Override
                    public void onClosedToLeft() {
                        if(!isBackMinimize){
                            draggablePanel.minimize();
                        }

                    }

                    @Override
                    public void onClosedToRight() {
                        if (!isBackMinimize){
                            draggablePanel.minimize();
                        }


                    }
                }
        );

        /*handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                draggablePanel.closeToLeft();
            }q
        },100);*/
        ///listView = findViewById(R.id.DragplayerListView);

        final Handler LoadingListHandler = new Handler();

        LoadingListHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!pageToken.equalsIgnoreCase("")){
                    getListInBackground();
                }
                LoadingListHandler.postDelayed(this,20000);
            }
        },20000);



    }


    private void initializeYoutubeFragment() {
        youtubeFragment = new YouTubePlayerSupportFragment();
        youtubeFragment.initialize(YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {

            @Override public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                          YouTubePlayer player, boolean wasRestored) {
                if (!wasRestored) {
                    youtubePlayer = player;


                    youtubePlayer.loadVideo(VIDEO_KEY);



                    youtubePlayer.setOnFullscreenListener(
                            new YouTubePlayer.OnFullscreenListener() {
                                @Override
                                public void onFullscreen(boolean b) {
                                    isFull = b;
                                }
                            }
                    );


                    youtubePlayer.setPlaylistEventListener(
                            new YouTubePlayer.PlaylistEventListener() {
                                @Override
                                public void onPrevious() {
                                    Toast.makeText(DragableTest.this, "Previous", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onNext() {
                                    Toast.makeText(DragableTest.this, "Next", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onPlaylistEnded() {

                                }
                            }
                    );

                    youtubePlayer.setPlayerStateChangeListener(
                            new YouTubePlayer.PlayerStateChangeListener() {
                                @Override
                                public void onLoading() {

                                }

                                @Override
                                public void onLoaded(String s) {

                                }

                                @Override
                                public void onAdStarted() {

                                }

                                @Override
                                public void onVideoStarted() {

                                }

                                @Override
                                public void onVideoEnded() {

                                    if(videoInfo.index <= data.size()){

                                        videoInfo.des = data.get(videoInfo.index).description;
                                        Intent intent = new Intent("descriptionIntent");
                                        intent.putExtra("des", data.get(videoInfo.index).description);
                                        intent.putExtra("title", data.get(videoInfo.index).name);
                                        intent.putExtra("id", data.get(videoInfo.index).videoId);
                                        LocalBroadcastManager.getInstance(DragableTest.this).sendBroadcast(intent);

                                        youtubePlayer.loadVideo(data.get(videoInfo.index).videoId);
                                        videoInfo.index++;
                                    }

                                }

                                @Override
                                public void onError(YouTubePlayer.ErrorReason errorReason) {

                                }
                            }
                    );
                }
            }

            @Override public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                          YouTubeInitializationResult error) {
            }
        });



    }

    private void initializeDraggablePanel() throws Resources.NotFoundException {
        draggablePanel.setFragmentManager(getSupportFragmentManager());
        draggablePanel.setTopFragment(youtubeFragment);
        //draggablePanel.setTopViewHeight(300);
        draggablePanel.setBottomFragment(new ListFragment());
        draggablePanel.setClickToMaximizeEnabled(true);


        draggablePanel.initializeView();
        //getDraggablePanel();
        //draggablePanel.maximize();

    }

    public DraggablePanel getDraggablePanel() {
        if(draggablePanel.isClosedAtLeft()){
            if(youtubePlayer.isPlaying()){
                pauseVideo();
            }
        }
        return draggablePanel;
    }


    /**
     * Hook the DraggableListener to DraggablePanel to pause or resume the video when the
     * DragglabePanel is maximized or closed.
     */



    public  void  getList(){
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.show();

        AndroidNetworking.get("https://www.googleapis.com/youtube/v3/playlistItems")
                .addQueryParameter("part", "snippet")
                .addQueryParameter("playlistId", "PL1m7Ygg9vUeDh-iJLfUVFyL6hY4EVid-3")
                .addQueryParameter("pageToken", pageToken)
                .addQueryParameter("maxResults", "20")
                .addQueryParameter("key", "AIzaSyDbWDJqeZvJiUgo0TE7Qa2FgyadYDyqPzI")
                .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        //Toast.makeText(DragableTest.this, response.toString(), Toast.LENGTH_SHORT).show();

                        try {
                            pageToken = response.getString("nextPageToken");
                        } catch (JSONException e) {
                            pageToken = "";
                            e.printStackTrace();
                        }

                        try {


                            //Toast.makeText(MainActivity.this, pageToken, Toast.LENGTH_SHORT).show();
                            JSONArray jsonArray = response.getJSONArray("items");
                            for (int i = 0 ; i < jsonArray.length() ; i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                JSONObject sub = jsonObject.getJSONObject("snippet");

                                videoInfo videoInfos = new videoInfo();
                                videoInfos.name = String.valueOf(sub.get("title"));

                                videoInfos.videoId = sub.getJSONObject("resourceId").getString("videoId");

                               // videoInfos.description = String.valueOf(sub.get("title")) +"\n\n"+String.valueOf(sub.get("description"));

                                videoInfos.thumbnail = sub.getJSONObject("thumbnails").getJSONObject("medium").getString("url");


                                //JSONArray title = new JSONArray(sub);


                                data.add(videoInfos);
                            }

                        } catch (JSONException e) {
                            Toast.makeText(DragableTest.this, e.toString(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                        //Toast.makeText(MainActivity.this, String.valueOf(response), Toast.LENGTH_SHORT).show();


                        PlayListAdapter adapter = new PlayListAdapter(DragableTest.this,data);
                        listView.setAdapter(adapter);

                        listView.setSelection(scrolPosition);;
                        dialog.dismiss();
                        // do anything with response
                    }
                    @Override
                    public void onError(ANError error) {
                        dialog.dismiss();
                        Toast.makeText(DragableTest.this, error.toString(), Toast.LENGTH_LONG).show();
                        // handle error
                    }
                });
    }

    public  void  getListInBackground(){
        /*final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.show();*/

        AndroidNetworking.get("https://www.googleapis.com/youtube/v3/playlistItems")
                .addQueryParameter("part", "snippet")
                .addQueryParameter("playlistId", "PL1m7Ygg9vUeDh-iJLfUVFyL6hY4EVid-3")
                .addQueryParameter("pageToken", pageToken)
                .addQueryParameter("maxResults", "20")
                .addQueryParameter("key", "AIzaSyDbWDJqeZvJiUgo0TE7Qa2FgyadYDyqPzI")
                .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        //Toast.makeText(DragableTest.this, response.toString(), Toast.LENGTH_SHORT).show();

                        try {
                            pageToken = response.getString("nextPageToken");
                        } catch (JSONException e) {
                            pageToken = "";
                            e.printStackTrace();
                        }

                        try {


                            //Toast.makeText(MainActivity.this, pageToken, Toast.LENGTH_SHORT).show();
                            JSONArray jsonArray = response.getJSONArray("items");
                            for (int i = 0 ; i < jsonArray.length() ; i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                JSONObject sub = jsonObject.getJSONObject("snippet");

                                videoInfo videoInfos = new videoInfo();
                                videoInfos.name = String.valueOf(sub.get("title"));

                                videoInfos.videoId = sub.getJSONObject("resourceId").getString("videoId");

                                // videoInfos.description = String.valueOf(sub.get("title")) +"\n\n"+String.valueOf(sub.get("description"));

                                videoInfos.thumbnail = sub.getJSONObject("thumbnails").getJSONObject("medium").getString("url");


                                //JSONArray title = new JSONArray(sub);


                                data.add(videoInfos);
                            }

                        } catch (JSONException e) {
                            Toast.makeText(DragableTest.this, e.toString(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                        //Toast.makeText(MainActivity.this, String.valueOf(response), Toast.LENGTH_SHORT).show();


                        PlayListAdapter adapter = new PlayListAdapter(DragableTest.this,data);
                        listView.setAdapter(adapter);

                        /*listView.setSelection(scrolPosition);;
                        dialog.dismiss();*/
                        // do anything with response
                    }
                    @Override
                    public void onError(ANError error) {
                        //dialog.dismiss();
                        //Toast.makeText(DragableTest.this, error.toString(), Toast.LENGTH_LONG).show();
                        // handle error
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchViewItem = menu.findItem(R.id.action_search);
        searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchViewAndroidActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchViewAndroidActionBar.clearFocus();
                searchArray = new ArrayList<>();

                for(videoInfo obj : data){
                    if(obj.name.toLowerCase().contains(query.toString().toLowerCase())){
                        searchArray.add(obj);

                    }
                }
                if(query.toString().length() == 0){
                    isSearch = false;
                }
                else {
                    isSearch = true;
                }
                PlayListAdapter adapter = new PlayListAdapter(DragableTest.this,searchArray);
                listView.setAdapter(adapter);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(isFirst){
                    if(draggablePanel.isMaximized() && !newText.isEmpty()){
                        draggablePanel.minimize();
                    }
                }

                searchArray = new ArrayList<>();

                for(videoInfo obj : data){
                    if(obj.name.toLowerCase().contains(newText.toString().toLowerCase())){
                        searchArray.add(obj);

                    }
                }
                if(newText.toString().length() == 0){
                    isSearch = false;
                }
                else {
                    isSearch = true;
                }
                PlayListAdapter adapter = new PlayListAdapter(DragableTest.this,searchArray);
                listView.setAdapter(adapter);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if(isFull && isFirst){
            youtubePlayer.setFullscreen(false);
        }
        else {

            if(isFirst){
                if(draggablePanel.isMinimized()){
                    pauseVideo();
                    isBackMinimize = true;
                    draggablePanel.closeToRight();
                    isBackMinimize = false;

                }
                else if(draggablePanel.isClosedAtLeft()|| draggablePanel.isClosedAtRight()){
                    finish();
                }
                else {

                    draggablePanel.minimize();
                }
            }
            else {
                finish();
            }
        }


    }

    private void pauseVideo() {
        if (youtubePlayer.isPlaying()) {
            youtubePlayer.pause();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        //Toast.makeText(this, "Stop", Toast.LENGTH_SHORT).show();
        if(isFirst){
            //Toast.makeText(this, "Pause Stop", Toast.LENGTH_SHORT).show();
            seekTime = youtubePlayer.getCurrentTimeMillis();
        }
        if (mAdView != null) {
            mAdView.pause();
        }
        if(mInterstitialAd != null){
            mInterstitialAd.setAdListener(null);
        }
        if(isFull){
            youtubePlayer.setFullscreen(false);
        }
        isPause = true;

    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        getWindow().getDecorView().setSystemUiVisibility(//View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                //View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                //| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
        if(isFirst){
            if(draggablePanel.isMaximized() || draggablePanel.isMinimized()){
                try {
                    youtubePlayer.loadVideo(VIDEO_KEY,seekTime);
                }
                catch (Exception e){
                    draggablePanel.minimize();
                    draggablePanel.closeToRight();
                    isFirst = false;
                    isPause = false;
                    isSearch = false;
                    isFull = false;
                    isBackMinimize = false;
                    finish();
                    startActivity(new Intent(this,DragableTest.class));
                }
            }
            else {
                try {
                    youtubePlayer.pause();
                }
                catch (Exception e){
                    draggablePanel.minimize();
                    draggablePanel.closeToRight();
                    isFirst = false;
                    isPause = false;
                    isSearch = false;
                    isFull = false;
                    isBackMinimize = false;
                    finish();
                    startActivity(new Intent(this,DragableTest.class));
                }
            }

        }

        if (mAdView != null) {
            mAdView.resume();
        }
        if(mInterstitialAd != null){
            displayinterstitial();
        }



        isPause = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
        if(mInterstitialAd != null){
            displayinterstitial();
        }
        if(isFirst){
            if(draggablePanel.isMaximized() || draggablePanel.isMinimized()){
                try {
                    youtubePlayer.loadVideo(VIDEO_KEY,seekTime);
                }
                catch (Exception e){
                    draggablePanel.minimize();
                    draggablePanel.closeToRight();
                    isFirst = false;
                    finish();
                    startActivity(new Intent(this,DragableTest.class));
                }
            }
            else {
                try {
                    youtubePlayer.pause();
                }
                catch (Exception e){
                    draggablePanel.minimize();
                    draggablePanel.closeToRight();
                    isFirst = false;
                    finish();
                    startActivity(new Intent(this,DragableTest.class));
                }
            }

        }


        isPause = false;
    }
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        if(mInterstitialAd != null){
            mInterstitialAd.setAdListener(null);
        }
        isPause = true;
        if(isFull){
            youtubePlayer.setFullscreen(false);
        }
        super.onDestroy();
    }

    /**
     * Resume the video reproduced in the YouTubePlayer.
     */
    private void playVideo() {
        if (!youtubePlayer.isPlaying()) {
            youtubePlayer.play();
        }
    }

    public void displayinterstitial()
    {
        if (mInterstitialAd.isLoaded() && !isPause)
        {mInterstitialAd.show();}
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
