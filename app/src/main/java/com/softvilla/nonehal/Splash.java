package com.softvilla.nonehal;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class Splash extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        //Glide.with(this).load(R.drawable.splashfinal).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).crossFade().into(imageView);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splash.this, DragableTest.class);
                startActivity(intent);
                finish();
            }
        }, 3000);

        /*WebView webView = (WebView) findViewById(R.id.gifWebView);

        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("@drawable/loading");*/






      /*  new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animation .cancel();
                Intent intent = new Intent(Splash.this, DragableTest.class);
                startActivity(intent);

            }
        }, 3000);
*/


    }
}
