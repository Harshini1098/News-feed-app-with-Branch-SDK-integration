package com.app.captureandupload;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.captureandupload.Adapter.MyAdapter;
import com.app.captureandupload.model.List_Data;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.SharingHelper;
import io.branch.referral.util.ContentMetadata;
import io.branch.referral.util.LinkProperties;
import io.branch.referral.util.ShareSheetStyle;

public class MainActivity extends AppCompatActivity {

    private static String JSON_URL = "https://www.reddit.com/r/health/hot.json?limit=10&after=t3_t3rfqv";
    RecyclerView recyclerView;
    private List<List_Data> dataList;
    private MyAdapter adapter;
    ProgressBar progressBar;
    ImageView backarrow;

    //pagination adding
    int page = 0, limit = 1;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerview);
        progressBar = findViewById(R.id.progress_bar);
        backarrow = findViewById(R.id.backarrow);
        getSupportActionBar().hide();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataList = new ArrayList<>();

        //method to call API
        loadJson();

        //pagination
        getDataFromAPI(page, limit);

        //method to finishing app on backpress arrow
        backpress();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                BranchUniversalObject buo = new BranchUniversalObject()
                        .setCanonicalIdentifier("content/12345")
                        .setTitle("My Content Title")
                        .setContentDescription("My Content Description")
                        .setContentImageUrl("https//branch.io logo")
                        .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                        .setLocalIndexMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                        .setContentMetadata(new ContentMetadata().addCustomMetadata("key1", "value1"));

/*
                BranchUniversalObject branchUniversalObject = new BranchUniversalObject()
                        // The identifier is what Branch will use to de-dupe the content across many different Universal Objects
                        .setCanonicalIdentifier("item/12345")
                                // This is where you define the open graph structure and how the object will appear on Facebook or in a deepview
                        .setTitle("Suits")
                        .setContentDescription("Great suits here")
                        .setContentImageUrl("http://steezo.com/wp-content/uploads/2012/12/man-in-suit.jpg")
                                // You use this to specify whether this content can be discovered publicly - default is public
                        .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                                // Here is where you can add custom keys/values to the deep link data
                        .addContentMetadata("picurl", "http://steezo.com/wp-content/uploads/2012/12/man-in-suit.jpg");
*/

                LinkProperties linkProperties = new LinkProperties()
                        .setChannel("facebook")
                        .setFeature("sharing")
                        .addControlParameter("$desktop_url", "https://www.spotify.com/in-en/")
                        .addControlParameter("$android_url","https://07pjy.app.link?%24identity_id=1032517619981920650https://07pjy.app.link?%24identity_id=1032517619981920650")
                        .addControlParameter("$ios_url", "https://timesofindia.indiatimes.com/world/europe/russia-ukraine-war-live-updates-16-march-2022/liveblog/90277814.cms");


                buo.generateShortUrl(MainActivity.this, linkProperties, new Branch.BranchLinkCreateListener() {
                    @Override
                    public void onLinkCreate(String url, BranchError error) {
                        if (error == null) {
                            Log.i("MyApp", "got my Branch link to share: " + url);
                        }
                        else {
                            Log.e("error", error.toString());
                        }
                    }
                });

                ShareSheetStyle shareSheetStyle = new ShareSheetStyle(MainActivity.this, "Check this out!", "This stuff is awesome: ")
                        .setMoreOptionStyle(getResources().getDrawable(android.R.drawable.ic_menu_search), "Show more")
                        .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK)
                        .addPreferredSharingOption(SharingHelper.SHARE_WITH.EMAIL);


                buo.showShareSheet(MainActivity.this,
                        linkProperties,
                        shareSheetStyle,
                        new Branch.BranchLinkShareListener() {
                            @Override
                            public void onShareLinkDialogLaunched() {
                            }

                            @Override
                            public void onShareLinkDialogDismissed() {
                            }

                            @Override
                            public void onLinkShareResponse(String sharedLink, String sharedChannel, BranchError error) {
                                Log.e("LinkShared", "success");
                            }

                            @Override
                            public void onChannelSelected(String channelName) {
                            }

                        });
            }
        });
    }

    private void backpress() {
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private void getDataFromAPI(int page, int limit) {
        if (page > limit) {
            Toast.makeText(this, "That's all the data..", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }
    }

    private void loadJson() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String Kind = jsonObject.getString("kind");
                            Log.e("the_res", "==>" + response);
                            Log.e("the_kind", "==>" + Kind);
                            JSONObject data = jsonObject.getJSONObject("data");
                            Log.e("the_data", "==>" + data);
                            JSONArray jsonArray = data.getJSONArray("children");
                            for (int i = 0; i <= jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                JSONObject datajsonobject = jsonObject1.getJSONObject("data");

                                String ups = datajsonobject.getString("ups");
                                Log.e("ups", "==>" + ups);

                                String selftext = datajsonobject.getString("selftext");
                                Log.e("selftext", "==>" + selftext);

                                String title = datajsonobject.getString("title");
                                Log.e("title", "==>" + title);

                                String created = datajsonobject.getString("created");
                                Log.e("created", "==>" + created);

                                String thumbnail = datajsonobject.getString("thumbnail");
                                Log.e("thumbnail", "==>" + thumbnail);

                                List_Data list_data = new List_Data(selftext, title, Integer.parseInt(ups), thumbnail, true, 500.0);
                                dataList.add(list_data);

                                // MyAdapter adapter = new MyAdapter(list_data, getApplicationContext());
                                adapter = new MyAdapter(dataList, MainActivity.this);
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", error.getMessage());
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);
        requestQueue.getCache().clear();
    }

    @Override
    public void onStart() {
        super.onStart();
        Branch.enableLogging();
        Branch branch = Branch.getInstance(getApplicationContext());
        branch.sessionBuilder(this).withCallback(branchReferralInitListener).withData(getIntent() != null ? getIntent().getData() : null).init();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
// if activity is in foreground (or in backstack but partially visible) launching the same
// activity will skip onStart, handle this case with reInitSession
        Branch.sessionBuilder(this).withCallback(branchReferralInitListener).reInit();
    }
    private Branch.BranchReferralInitListener branchReferralInitListener = new Branch.BranchReferralInitListener() {
        @Override
        public void onInitFinished(JSONObject linkProperties, BranchError error) {
            if (error == null) {
                Log.i("BranchDeepLink", "" + linkProperties.toString());
/*
                try {
                    Gson gson = new Gson();
                    JsonParser parser = new JsonParser();
                    JsonElement json = parser.parse(linkProperties.toString());
                  //  ShareScreenDataModel shareScreenDataModel = gson.fromJson(json, MainActivity.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
*/
            }
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Checking if the previous activity is launched on Branch Auto deep link.
        if(requestCode == getResources().getInteger(R.integer.AutoDeeplinkRequestCode)){
            //Decide here where  to navigate  when an auto deep linked activity finishes.For e.g. go to HomeActivity or a  SignUp Activity.
            finish();
        }
    }
}
