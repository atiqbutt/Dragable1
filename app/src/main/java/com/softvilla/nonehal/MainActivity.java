/*
package com.softvilla.babyrhymes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.prof.youtubeparser.Parser;
import com.prof.youtubeparser.models.videos.Video;

import java.util.ArrayList;
import java.util.LinkedList;

public class MainActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private ListView listView;
    private ImageView image;
    private RatingBar ratingBar;
    private TextView name, age, salary;
    DraggableView draggableView;

    private LinkedList<Employee> employees;
    private ListViewAdapter adapter;
TextView title;

    private ArrayList<Video> videos;
    private RelativeLayout mLayoutManager;
    private Handler handler;
    private RecyclerView mRecyclerView;
    private VideoAdapter vAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar progressBar;
    private FloatingActionButton fab;
    private int totalElement;
    private String nextToken;
    private final String CHANNEL_ID = "UCLXo7UDZvByw2ixzpQCufnA";
    //TODO: delete
    private final String API_KEY = "AIzaSyAWPFkRUgJA4SvVgWx6xbByYJJVYz66VDc";

    private AdView mAdView;
    InterstitialAd mInterstitialAd;

    ///////////////////////////////////////////////////////////////////////////////////

    private YouTubePlayer YPlayer;
    private static final String YoutubeDeveloperKey = "AIzaSyAWPFkRUgJA4SvVgWx6xbByYJJVYz66VDc";//"AIzaSyDOG9uXoR8pG_m04a3-Q_0byh3_RXQ2kUM";
    private static final int RECOVERY_DIALOG_REQUEST = 1;
    String videoKey;
    String videotitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                mInterstitialAd = new InterstitialAd(MainActivity.this);
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
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.initialize(YoutubeDeveloperKey, this);


        //final Video currentVideo = videos.getTitle();
        //title = (TextView) findViewById(R.id.title);


        draggableView = (DraggableView) findViewById(R.id.draggable_view);

        //set some feature for draggable panel
        //draggableView.setVisibility(View.GONE);
        draggableView.isClickToMaximizeEnabled();
        draggableView.isClickToMinimizeEnabled();
        draggableView.setClickToMaximizeEnabled(true);
        //draggableView.setClickToMinimizeEnabled(true);


        Bundle bundle = getIntent().getExtras();
        videoKey = bundle.getString("vid");
        videotitle = bundle.getString("title");



         */
/*locateView();

        setListViewHeader();
        setListViewAdapter();
        listView.setOnItemClickListener(onItemClickListener());*//*



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

        //show the fab on the bottom of recycler view
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
                                Toast.makeText(MainActivity.this, "New video added!", Toast.LENGTH_SHORT).show();
                                fab.setVisibility(View.GONE);
                                mRecyclerView.scrollBy(0, 1000);
                            }

                            @Override
                            public void onError() {
                                Toast.makeText(MainActivity.this, "Error while loading data. Please retry", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        Toast.makeText(MainActivity.this, "Unable to load data. Please retry", Toast.LENGTH_SHORT).show();
                    }

                }

                super.onScrollStateChanged(recyclerView, newState);
            }
        });






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
                vAdapter = new VideoAdapter(list, R.layout.yt_row, MainActivity.this);
                mRecyclerView.setAdapter(vAdapter);
                totalElement = vAdapter.getItemCount();
                nextToken = nextPageToken;
                vAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
                Toast.makeText(MainActivity.this, String.valueOf(list.size()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError() {
                Toast.makeText(MainActivity.this, "Error while loading data. Please retry", Toast.LENGTH_SHORT).show();
            }
        });
    }


    */
/*
*//*






    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format(
                    "There was an error initializing the YouTubePlayer",
                    errorReason.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(YoutubeDeveloperKey, this);
        }
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.youtube_view);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                        YouTubePlayer player, boolean wasRestored) {
        YPlayer = player;

 */
/* Now that this variable YPlayer is global you can access it
 * throughout the activity, and perform all the player actions like
 * play, pause and seeking to a position by code.*//*




        draggableView.setVisibility(View.VISIBLE);
        if (!wasRestored) {
            YPlayer.loadVideo(videoKey);//"2zNSgSzhBfM"
//            title.setText(videotitle);
        }
        //restore draggable view height
        draggableView.maximize();


    }


    */
/*private void setListViewAdapter() {
        //set listview data
        employees = new LinkedList<Employee>();
        employees.add(new Employee("Hoang Ngan", 25, 3000, false, 3.5f));
        employees.add(new Employee("Hong Thai", 22, 1000, true, 4));
        employees.add(new Employee("Quoc Cuong", 24, 1300, true, 4.5f));
        employees.add(new Employee("Tran Phuc", 21, 1400, true, 4));
        employees.add(new Employee("Duc Tuan", 29, 1500, true, 3.5f));
        employees.add(new Employee("Ngo Trang", 25, 1800, false, 3));
        employees.add(new Employee("Quynh Trang", 26, 2200, false, 4));
        employees.add(new Employee("Tuan Tinh", 28, 2600, true, 4.5f));
        employees.add(new Employee("Hoang Bien", 28, 1000, false, 3));
        employees.add(new Employee("Tran Ha", 27, 1200, false, 4));
        employees.add(new Employee("Dang Chuyen", 30, 1700, true, 5));
        employees.add(new Employee("Kim Kien", 22, 1500, true, 4));
        employees.add(new Employee("Thuy Linh", 24, 2500, false, 3.5f));

        //create - attach adapter to listview
        adapter = new ListViewAdapter(this, R.layout.item_list_employee, employees);
        listView.setAdapter(adapter);

    }*//*


    private void locateView() {
        */
/*listView = (ListView) findViewById(R.id.list_view);
        image = (ImageView) findViewById(R.id.image);
        ratingBar = (RatingBar) findViewById(R.id.rate);
        age = (TextView) findViewById(R.id.age);
        name = (TextView) findViewById(R.id.name);
        salary = (TextView) findViewById(R.id.salary);

        draggableView = (DraggableView) findViewById(R.id.draggable_view);

        //set some feature for draggable panel
        draggableView.setVisibility(View.GONE);
        draggableView.setClickToMaximizeEnabled(true);
        draggableView.setClickToMinimizeEnabled(true);*//*

       // draggableView.setClickable(true);
    }



    private void setListViewHeader() {
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.layout_header, listView, false);
        listView.addHeaderView(header, null, false);
    }


    private AdapterView.OnItemClickListener onItemClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Employee employee = (Employee) parent.getAdapter().getItem(position);

                    image.setImageResource(R.drawable.finallogo);


                name.setText("Name: " + employee.getName());
                age.setText("Age: " + employee.getAge());
                ratingBar.setRating(employee.getRating());
                draggableView.setVisibility(View.VISIBLE);
                salary.setText("Salary: " + employee.getSalary());

                //restore draggable view height
                draggableView.maximize();
            }
        };

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
    public void displayinterstitial()
    {
        if (mInterstitialAd.isLoaded())
        {mInterstitialAd.show();}
    }


}*/
