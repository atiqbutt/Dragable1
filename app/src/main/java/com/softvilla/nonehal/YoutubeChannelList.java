package com.softvilla.nonehal;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.prof.youtubeparser.Parser;
import com.prof.youtubeparser.models.videos.Video;

import java.util.ArrayList;

public class YoutubeChannelList extends AppCompatActivity {


    private RelativeLayout mLayoutManager;
    private Handler handler;
    private RecyclerView mRecyclerView;
    private VideoAdapter vAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar progressBar;
    private FloatingActionButton fab;
    private int totalElement;
    private String nextToken;
    private final String CHANNEL_ID = "UCLXo7UDZvByw2ixzpQCufnA";//"UCgj5NCVkJaGSxRpqkfdr0sA";//"UCVHFbqXqoYvEWM1Ddxl0QDg"; //"UCLXo7UDZvByw2ixzpQCufnA";
    //TODO: delete

    private AdView mAdView;
    InterstitialAd mInterstitialAd;


    private final String API_KEY = "AIzaSyAWPFkRUgJA4SvVgWx6xbByYJJVYz66VDc";//"AIzaSyDOG9uXoR8pG_m04a3-Q_0byh3_RXQ2kUM";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_channel_list);

        MobileAds.initialize(this, "ca-app-pub-4789001247034559~6430065921");


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                mInterstitialAd = new InterstitialAd(YoutubeChannelList.this);
                mInterstitialAd.setAdUnitId("ca-app-pub-4789001247034559/8354834764");
                AdRequest adRequest = new AdRequest.Builder().build();
                mInterstitialAd.loadAd(adRequest);
                mInterstitialAd.setAdListener(new AdListener() {
                    @Override

                    public void onAdLoaded() {
                        displayinterstitial();
                    }
                });

            }
        } , 10000);

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);


        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        //Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        mLayoutManager = new RelativeLayout(this);
        handler = new Handler();


        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.container);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
        mSwipeRefreshLayout.canChildScrollUp();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

               // vAdapter.setLoadMoreListener(VideoAdapter.LoadHolder);
                vAdapter.clearData();
                vAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(true);
                loadVideo();

            }
        });

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
            loadVideo();


        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                LinearLayoutManager layoutManager = LinearLayoutManager.class.cast(recyclerView.getLayoutManager());
                int lastVisible = layoutManager.findLastVisibleItemPosition();

                if (lastVisible == totalElement - 1){
                    Parser parser = new Parser();
                    if (nextToken != null) {
                        String url = parser.generateMoreDataRequest(CHANNEL_ID, 20, Parser.ORDER_DATE, API_KEY, nextToken);
                        parser.execute(url);
                        parser.onFinish(new Parser.OnTaskCompleted() {
                            @Override
                            public void onTaskCompleted(ArrayList<Video> list, String nextPageToken) {

                                //update the adapter with the new data
                                vAdapter.getList().addAll(list);
                                totalElement = vAdapter.getItemCount();
                                nextToken = nextPageToken;
                                vAdapter.notifyDataSetChanged();
                                Toast.makeText(YoutubeChannelList.this, "New video added!", Toast.LENGTH_SHORT).show();
                                fab.setVisibility(View.GONE);
                                mRecyclerView.scrollBy(0, 1000);
                            }

                            @Override
                            public void onError() {
                                Toast.makeText(YoutubeChannelList.this, "Error while loading data. Please retry", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        Toast.makeText(YoutubeChannelList.this, "Unable to load data. Please retry", Toast.LENGTH_SHORT).show();
                    }

                }

                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        //show the fab on the bottom of recycler view
       /* mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {


                LinearLayoutManager layoutManager = LinearLayoutManager.class.cast(recyclerView.getLayoutManager());
                int lastVisible = layoutManager.findLastVisibleItemPosition();

                if (lastVisible == totalElement - 1){
                    Parser parser = new Parser();
                    if (nextToken != null) {
                        String url = parser.generateMoreDataRequest(CHANNEL_ID, 20, Parser.ORDER_DATE, API_KEY, nextToken);
                        parser.execute(url);
                        parser.onFinish(new Parser.OnTaskCompleted() {
                            @Override
                            public void onTaskCompleted(ArrayList<Video> list, String nextPageToken) {

                                //update the adapter with the new data
                                vAdapter.getList().addAll(list);
                                totalElement = vAdapter.getItemCount();
                                nextToken = nextPageToken;
                                vAdapter.notifyDataSetChanged();
                                Toast.makeText(YoutubeChannelList.this, "New video added!", Toast.LENGTH_SHORT).show();
                                fab.setVisibility(View.GONE);
                                mRecyclerView.scrollBy(0, 1000);
                            }

                            @Override
                            public void onError() {
                                Toast.makeText(YoutubeChannelList.this, "Error while loading data. Please retry", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        Toast.makeText(YoutubeChannelList.this, "Unable to load data. Please retry", Toast.LENGTH_SHORT).show();
                    }

                }


                   *//* fab.setVisibility(View.VISIBLE);
                else
                    fab.setVisibility(View.GONE);*//*

                super.onScrolled(recyclerView, dx, dy);
            }
        });*/


        /*vAdapter.setLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {

                        Parser parser = new Parser();
                        if (nextToken != null) {
                            String url = parser.generateMoreDataRequest(CHANNEL_ID, 20, Parser.ORDER_DATE, API_KEY, nextToken);
                            parser.execute(url);
                            parser.onFinish(new Parser.OnTaskCompleted() {
                                @Override
                                public void onTaskCompleted(ArrayList<Video> list, String nextPageToken) {

                                    //update the adapter with the new data
                                    vAdapter.getList().addAll(list);
                                    totalElement = vAdapter.getItemCount();
                                    nextToken = nextPageToken;
                                    vAdapter.notifyDataSetChanged();
                                    Toast.makeText(YoutubeChannelList.this, "New video added!", Toast.LENGTH_SHORT).show();
                                    fab.setVisibility(View.GONE);
                                    mRecyclerView.scrollBy(0, 1000);
                                }

                                @Override
                                public void onError() {
                                    Toast.makeText(YoutubeChannelList.this, "Error while loading data. Please retry", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {
                            Toast.makeText(YoutubeChannelList.this, "Unable to load data. Please retry", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
//                        int index = movies.size() - 1;
//                        loadMore(index);// a method which requests remote data             }
            }


        });

        mRecyclerView.setAdapter(vAdapter);

*/
        /*mRecyclerView.setOnScrollListener(new YoutubeChannelList(mLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                //add progress item
//                myDataset.add(null);
//                mAdapter.notifyItemInserted(myDataset.size());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //remove progress item
                       *//* myDataset.remove(myDataset.size() - 1);
                        mAdapter.notifyItemRemoved(myDataset.size());*//*
                        //add items one by one
                        *//*for (int i = 0; i < 15; i++) {
                            myDataset.add("Item"+(myDataset.size()+1));
                            mAdapter.notifyItemInserted(myDataset.size());
                        }*//*
                        //or you can add all at once but do not forget to call mAdapter.notifyDataSetChanged();
                    }
                }, 2000);
               // System.out.println("load");
            }
        });*/

        //load more data
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Parser parser = new Parser();
                if (nextToken != null) {
                    String url = parser.generateMoreDataRequest(CHANNEL_ID, 20, Parser.ORDER_DATE, API_KEY, nextToken);
                    parser.execute(url);
                    parser.onFinish(new Parser.OnTaskCompleted() {
                        @Override
                        public void onTaskCompleted(ArrayList<Video> list, String nextPageToken) {

                            //update the adapter with the new data
                            vAdapter.getList().addAll(list);
                            totalElement = vAdapter.getItemCount();
                            nextToken = nextPageToken;
                            vAdapter.notifyDataSetChanged();
                            Toast.makeText(YoutubeChannelList.this, "New video added!", Toast.LENGTH_SHORT).show();
                            fab.setVisibility(View.GONE);
                            mRecyclerView.scrollBy(0, 1000);
                        }

                        @Override
                        public void onError() {
                            Toast.makeText(YoutubeChannelList.this, "Error while loading data. Please retry", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Toast.makeText(YoutubeChannelList.this, "Unable to load data. Please retry", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void displayinterstitial()
    {
        if (mInterstitialAd.isLoaded())
        {mInterstitialAd.show();}
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void loadVideo() {

        if (!mSwipeRefreshLayout.isRefreshing())
            progressBar.setVisibility(View.VISIBLE);

        Parser parser = new Parser();
        String url = parser.generateRequest(CHANNEL_ID, 20, Parser.ORDER_DATE, API_KEY);

        parser.execute(url);
        parser.onFinish(new Parser.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(ArrayList<com.prof.youtubeparser.models.videos.Video> list, String nextPageToken) {
                //list is an ArrayList with all video's item
                //set the adapter to recycler view
                vAdapter = new VideoAdapter(list, R.layout.yt_row, YoutubeChannelList.this);
                mRecyclerView.setAdapter(vAdapter);
                totalElement = vAdapter.getItemCount();
                nextToken = nextPageToken;
                vAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
                Toast.makeText(YoutubeChannelList.this, String.valueOf(list.size()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError() {
                Toast.makeText(YoutubeChannelList.this, "Error while loading data. Please retry", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {

        super.onResume();
        if (vAdapter != null)
            vAdapter.notifyDataSetChanged();

        if (mAdView != null) {
            mAdView.pause();
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (vAdapter != null)
            vAdapter.clearData();

        if (mAdView != null) {
            mAdView.pause();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {

            android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(YoutubeChannelList.this).create();
            alertDialog.setTitle(R.string.app_name);
            alertDialog.setMessage(Html.fromHtml(YoutubeChannelList.this.getString(R.string.info_text) +
                    " <a href='http://github.com/prof18/YoutubeParser'>GitHub.</a>" +
                    YoutubeChannelList.this.getString(R.string.author)));
            alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            ((TextView) alertDialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }



}
