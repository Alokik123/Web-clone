package com.example.hornpub;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.viewpager2.widget.ViewPager2;
import android.webkit.ValueCallback;


import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    EditText urlInput;
    ImageView clearUrl;
    WebView webView;
    ProgressBar progressBar;
    ImageView webBack, webForward, webRefresh, webShare;
    LinearLayout searchEngineOptions;
    WebView webView2;
    WebView readerView;
    Button activateReaderModeButton;
    boolean searchEngineOptionsVisible = false;
     boolean isReaderModeActivated = false;
    ValueCallback<String> contentValueCallback;

    String readerModeHtmlContent = "<html><body><h1>Reader Mode Content</h1><p>This is the content of the reader mode.</p></body></html>";


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        urlInput = findViewById(R.id.url_input);
        clearUrl = findViewById(R.id.clear_icon);
        progressBar = findViewById(R.id.progress_bar);
        webView = findViewById(R.id.web_view);

        webBack = findViewById(R.id.web_back);
        webForward = findViewById(R.id.web_forward);
        webRefresh = findViewById(R.id.web_refresh);
        webShare = findViewById(R.id.web_share);
        searchEngineOptions = findViewById(R.id.search_engine_options);
        webView2 = findViewById(R.id.web_view);
        readerView = findViewById(R.id.reader_view);
        readerView.setVisibility(View.GONE);


        FloatingActionButton searchEngineFab = findViewById(R.id.search_engine_fab);
        ImageView googleSearchEngine = findViewById(R.id.google_search_engine);
        ImageView bingSearchEngine = findViewById(R.id.bing_search_engine);
        ImageView duckduckgoSearchEngine = findViewById(R.id.duckduckgo_search_engine);
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        Button activateReaderModeButton = findViewById(R.id.activate_reader_mode_button);
        EditText rci = findViewById(R.id.reader_content_input);
        String readerModeContent = rci.getText().toString();
        if (!readerModeContent.trim().isEmpty()) {
            String htmlContent = "<html><body>" + readerModeContent + "</body></html>";
            readerView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);
        } else {
            String defaultContent = "<html><body><h1>No content to display in Reader Mode</h1></body></html>";
            readerView.loadDataWithBaseURL(null, defaultContent, "text/html", "UTF-8", null);
        }

        activateReaderModeButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick (View view){
                toggleReaderMode();
            }
        });



        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
            }
        });
        viewPager.setAdapter(new WebBrowserPagerAdapter(this));
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Browser");
                            break;
                        case 1:
                            tab.setText("Reader Mode");
                            break;
                    }
                }).attach();


        loadMyUrl("google.com");
        readerView.getSettings().setJavaScriptEnabled(true);
        readerView.setWebChromeClient(new WebChromeClient());
        readerView.setWebViewClient(new WebViewClient());


        urlInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_GO || i == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(urlInput.getWindowToken(), 0);
                    loadMyUrl(urlInput.getText().toString());
                    return true;
                }
                return false;
            }
        });

        clearUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                urlInput.setText("");
            }
        });

        webBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (webView.canGoBack()) {
                    webView.goBack();
                }
            }
        });

        webForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (webView.canGoForward()) {
                    webView.goForward();
                }
            }
        });

        webRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.reload();
            }
        });

        webShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, webView.getUrl());
                intent.setType("text/plain");
                startActivity(intent);
            }
        });

        searchEngineFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchEngineOptionsVisible = !searchEngineOptionsVisible;
                searchEngineOptions.setVisibility(searchEngineOptionsVisible ? View.VISIBLE : View.GONE);
            }
        });

        googleSearchEngine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String googleUrl = "https://www.google.com/search?q=";
                String searchTerm = webView.getUrl();
                loadWebPage(googleUrl + searchTerm);
                searchEngineOptions.setVisibility(View.GONE);
            }
        });


        bingSearchEngine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bingUrl = "https://www.bing.com/search?q=";
                String searchTerm = webView.getUrl();
                loadWebPage(bingUrl + searchTerm);
                searchEngineOptions.setVisibility(View.GONE);
            }
        });

        duckduckgoSearchEngine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String duckduckgoUrl = "https://duckduckgo.com/?q=";
                String searchTerm = webView.getUrl();
                loadWebPage(duckduckgoUrl + searchTerm);
                searchEngineOptions.setVisibility(View.GONE);
            }
        });


    }

    void loadMyUrl(String url) {
        boolean matchUrl = Patterns.WEB_URL.matcher(url).matches();
        if (matchUrl) {
            webView.loadUrl(url);
        } else {
            webView.loadUrl("https://www.google.com/search?q=" + url);
        }
    }

    @Override
    public void onBackPressed() {

        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }

    }

    class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            urlInput.setText(webView.getUrl());
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void loadWebPage(String url) {
        boolean matchUrl = Patterns.WEB_URL.matcher(url).matches();
        if (matchUrl) {
            webView.loadUrl(url);
            updateWebViewBackground(url);
        } else if (url.startsWith("https://www.google.com")) {
            webView.loadUrl(url);
            updateWebViewBackground("https://www.google.com");
        } else if (url.startsWith("https://www.bing.com")) {
            webView.loadUrl(url);
            updateWebViewBackground("https://www.bing.com");
        } else if (url.startsWith("https://duckduckgo.com")) {
            webView.loadUrl(url);
            updateWebViewBackground("https://duckduckgo.com");
        } else {
            webView.loadUrl("https://www.google.com/search?q=" + url);
            updateWebViewBackground("https://www.google.com");
        }
    }


    private void updateWebViewBackground(String url) {
        if (url.contains("google.com")) {
            webView.setBackgroundColor(getResources().getColor(R.color.google_background));
        } else if (url.contains("bing.com")) {
            webView.setBackgroundColor(getResources().getColor(R.color.bing_background));
        } else if (url.contains("yahoo.com")) {
            webView.setBackgroundColor(getResources().getColor(R.color.yahoo_background));
        } else {
            webView.setBackgroundColor(getResources().getColor(android.R.color.white));
        }
    }



    private void toggleReaderMode() {
        if (isReaderModeActivated) {
            loadWebView();
        } else {
            loadReaderMode();
        }
    }

    private void loadReaderMode() {
        String currentContent = extractCurrentWebViewContent();
        if (!currentContent.trim().isEmpty()) {
            String htmlContent = "<html><body>" + currentContent + "</body></html>";
            readerView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);
        } else {
            String defaultContent = "<html><body><h1>No content to display in Reader Mode</h1></body></html>";
            readerView.loadDataWithBaseURL(null, defaultContent, "text/html", "UTF-8", null);
        }
        readerView.loadDataWithBaseURL(null, readerModeHtmlContent, "text/html", "UTF-8", null);
        webView.setVisibility(View.GONE);
        readerView.setVisibility(View.VISIBLE);
        isReaderModeActivated = true;

    }


    private void loadWebView() {
        webView.setVisibility(View.VISIBLE);
        readerView.setVisibility(View.GONE);
        isReaderModeActivated = false;
    }
    private String extractCurrentWebViewContent() {
        webView.evaluateJavascript("document.body.innerText", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
           }



        });
        return "";

}}


