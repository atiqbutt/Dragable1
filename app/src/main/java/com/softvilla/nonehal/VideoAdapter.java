package com.softvilla.nonehal;

/**
 * Created by Malik on 23/10/2017.
 */


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.prof.youtubeparser.models.videos.Video;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {




    private final int VIEW_ITEM = 0;
    private final int VIEW_LOADING = 1;
    private RelativeLayout mManager;
    OnLoadMoreListener loadMoreListener;
    boolean isLoading = false, isMoreDataAvailable = true;
    public final int TYPE_MOVIE = 0;
    public final int TYPE_LOAD = 1;

    public static final String YOUTUBE_VIDEO_KEY = "vid";

    private ArrayList<Video> videos;
    private int rowLayout;
    private Context mContext;
    //TODO: delete
    private final String API_KEY = "AIzaSyAWPFkRUgJA4SvVgWx6xbByYJJVYz66VDc";


    public VideoAdapter(ArrayList<Video> list, int rowLayout, Context context) {
        this.videos = list;
        this.rowLayout = rowLayout;
        this.mContext = context;
        //this.mManager = (RelativeLayout) context.getLayoutManager();
        //Toast.makeText(context, videos.size(), Toast.LENGTH_SHORT).show();
    }

    public void clearData(){
        if (videos != null)
            videos.clear();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(v);


    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position)  {

        final Video currentVideo = videos.get(position);
        

        String pubDateString = currentVideo.getDate();
        final String videoTitle = currentVideo.getTitle();

        //retrieve video link
        final String videoId = currentVideo.getVideoId();
        final String link = "https://www.youtube.com/watch?v=" + videoId;

        viewHolder.title.setText(currentVideo.getTitle());
//        viewHolder.pubDate.setText(pubDateString);

        Picasso.with(mContext)
                .load(currentVideo.getCoverLink())
                .placeholder(R.drawable.placeholder)
                .fit().centerCrop()
                .into(viewHolder.image);

        //show statistic of the selected video
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext,DragableTest.class);
                intent.putExtra("vid",videoId);
                intent.putExtra("title",videoTitle);
                mContext.startActivity(intent);
                ((Activity)mContext).finish();


                /*Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                mContext.startActivity(intent1);*/


               /* VideoStats videoStats = new VideoStats();
                String url = videoStats.generateStatsRequest(videoId, API_KEY);
                videoStats.execute(url);
                videoStats.onFinish(new VideoStats.OnTaskCompleted() {
                    @Override
                    public void onTaskCompleted(Statistics stats) {

                        String body = "Views: " + stats.getViewCount() + "\n" +
                                "Like: " + stats.getLikeCount() + "\n" +
                                "Dislike: " + stats.getDislikeCount() + "\n" +
                                "Number of comment: " + stats.getCommentCount() + "\n" +
                                "Number of favourite: " + stats.getFavoriteCount();

                        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
                        dialogBuilder.setTitle("Stats for: \"" + videoTitle + "\"");
                        dialogBuilder.setMessage(body);
                        dialogBuilder.setCancelable(false);
                        dialogBuilder.setNegativeButton(android.R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        dialogBuilder.show();
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(mContext, "Unable to get statistic for this video. Please try again", Toast.LENGTH_SHORT).show();
                    }
                });*/
            }
        });

        //open the video on Youtube
       /* viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

            }
        });*/



        if(position>=getItemCount()-1 && isMoreDataAvailable && !isLoading && loadMoreListener!=null){
            isLoading = true;
            loadMoreListener.onLoadMore();
        }
    }


    @Override
    public int getItemCount() {
       // Toast.makeText(mContext, videos.size() , Toast.LENGTH_SHORT).show();
        return videos == null ? 0 : videos.size();

    }

    public ArrayList<Video> getList() {
        return videos;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView pubDate;
        ImageView image;

        public ViewHolder(View itemView) {

            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            //pubDate = (TextView) itemView.findViewById(R.id.pubDate);
            image = (ImageView)itemView.findViewById(R.id.image);
        }
    }


    @Override
    public int getItemViewType(int position) {
        if(videos.get(position).equals("videos")){
            return TYPE_MOVIE;
        }else{
            return TYPE_LOAD;
        }
    }
    static class LoadHolder extends RecyclerView.ViewHolder{
        public LoadHolder(View itemView) {
            super(itemView);
        }
    }
    public void setMoreDataAvailable(boolean moreDataAvailable) {
        isMoreDataAvailable = moreDataAvailable;
    }
    interface OnLoadMoreListener{
        void onLoadMore();
    }

    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }
}
