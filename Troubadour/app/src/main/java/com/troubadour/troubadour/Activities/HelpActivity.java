package com.troubadour.troubadour.Activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.troubadour.troubadour.R;

public class HelpActivity extends AppCompatActivity {

    WebView helpWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        helpWebView = (WebView) findViewById(R.id.helpWebView);
        helpWebView.getSettings().setJavaScriptEnabled(true);
        //helpWebView.getSettings().setDomStorageEnabled(true);
        //helpWebView.getSettings().setUserAgentString("Android");
        helpWebView.loadUrl("file:///android_asset/troubadourHelp.html");
        helpWebView.getSettings().setUserAgentString("Mozilla/5.0 (iPhone; U; CPU like Mac OS X; en) AppleWebKit/420+ (KHTML, like Gecko) Version/3.0 Mobile/1A543a Safari/419.3");
        helpWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                view.loadUrl(url);
                return false;
            }
        });
    }

}
