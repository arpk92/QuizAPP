package com.snowhillapps.brainspire.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.snowhillapps.brainspire.Constant;
import com.snowhillapps.brainspire.R;
import com.snowhillapps.brainspire.helper.AppController;
import com.snowhillapps.brainspire.helper.CircleImageView;
import com.snowhillapps.brainspire.helper.Session;
import com.snowhillapps.brainspire.helper.Utils;
import com.snowhillapps.brainspire.model.LeaderBoard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.snowhillapps.brainspire.Constant.PAGE_LIMIT;

public class LeaderBoardActivity extends AppCompatActivity {
    Toolbar toolbar;
    private RecyclerView recyclerView;
    public ArrayList<LeaderBoard> lbList, topList;
    private ProgressBar progressBar;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private TextView tvTitle, tvRank, tvScore, tvName, tvAlert;
    private CircleImageView imgProfile;
    private String USER_RANK;
    private RelativeLayout rankLyt;
    AppCompatSpinner spinner;
    public String SCORE, USER_ID;
    String[] filterType = {"All", "Monthly", "Daily"};
    String formattedDate;
    int PAGE_START = 0;
    int offset = 0;

    LeaderboardAdapter adapter;
    protected Handler handler;
    int total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        spinner = findViewById(R.id.spinner);
        tvTitle = findViewById(R.id.tvTitle);
        tvAlert = findViewById(R.id.tvAlert);
        tvRank = findViewById(R.id.tvRank);
        tvName = findViewById(R.id.tvName);
        tvScore = findViewById(R.id.tvScore);

        imgProfile = findViewById(R.id.imgProfile);

        rankLyt = findViewById(R.id.rankLyt);

        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        tvTitle.setText(getString(R.string.leaderboard));
        handler = new Handler();
        // Spinner on item click listener

        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        formattedDate = df.format(c);
        spinner.setAdapter(new ArrayAdapter<>(LeaderBoardActivity.this, R.layout.spinner_item, filterType));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                rankLyt.setVisibility(View.GONE);
                AppController.getInstance().getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
                    @Override
                    public boolean apply(Request<?> request) {
                        return true;
                    }
                });
                if (lbList != null)
                    lbList.clear();
                if (topList != null)
                    topList.clear();

                PAGE_START = 0;
                offset = 0;
                total = 0;
                LeaderBoardData(formattedDate, spinner.getSelectedItemPosition(), 0);

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    public void LeaderBoardData(final String date, final int position, final int startoffset) {
        if (Utils.isNetworkAvailable(LeaderBoardActivity.this)) {
            progressBar.setVisibility(View.VISIBLE);
            StringRequest strReq = new StringRequest(Request.Method.POST, Constant.QUIZ_URL, new Response.Listener<String>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(String response) {
                    try {
                     //   System.out.println("=====lb res " + response);
                        lbList = new ArrayList<>();
                        topList = new ArrayList<>();
                        JSONObject obj = new JSONObject(response);
                        tvAlert.setVisibility(View.GONE);
                        JSONArray jsonArray = obj.getJSONArray("data");

                        JSONObject userObject = jsonArray.getJSONObject(0).getJSONObject("my_rank");
                        if (userObject.length() > 0) {
                            if (!userObject.getString(Constant.RANK).equals("0")) {
                                if (userObject.getString(Constant.USER_ID).equals(Session.getUserData(Session.USER_ID, getApplicationContext()))) {
                                    rankLyt.setVisibility(View.VISIBLE);
                                    imgProfile.setImageUrl(Session.getUserData(Constant.PROFILE, getApplicationContext()), imageLoader);
                                    tvScore.setText("" + userObject.getString(Constant.SCORE));
                                    tvName.setText(Session.getUserData(Constant.USER_NAME, getApplicationContext()));
                                    tvRank.setText("" + userObject.getString(Constant.RANK));
                                }
                            }
                        } else {
                            rankLyt.setVisibility(View.GONE);
                        }
                        progressBar.setVisibility(View.GONE);
                        if (jsonArray.length() > 1) {
                            total = Integer.parseInt(obj.getString(Constant.TOTAL));
                            for (int i = 1; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                USER_RANK = object.getString(Constant.RANK);

                                switch (USER_RANK) {
                                    case "1":
                                        topList.add(new LeaderBoard(object.getString(Constant.RANK),
                                                object.getString(Constant.name), object.getString(Constant.SCORE),
                                                object.getString(Constant.USER_ID), object.getString(Constant.PROFILE)));
                                        break;

                                    case "2":

                                        topList.add(new LeaderBoard(object.getString(Constant.RANK),
                                                object.getString(Constant.name), object.getString(Constant.SCORE),
                                                object.getString(Constant.USER_ID), object.getString(Constant.PROFILE)));
                                        break;

                                    case "3":

                                        topList.add(new LeaderBoard(object.getString(Constant.RANK),
                                                object.getString(Constant.name), object.getString(Constant.SCORE),
                                                object.getString(Constant.USER_ID), object.getString(Constant.PROFILE)));
                                        break;

                                    default:
                                        //lbList.add(0, new LeaderBoard(topList));
                                        LeaderBoard leaderBoard = new LeaderBoard(object.getString(Constant.RANK),
                                                object.getString(Constant.name), object.getString(Constant.SCORE),
                                                object.getString(Constant.USER_ID), object.getString(Constant.PROFILE));
                                        lbList.add(leaderBoard);
                                        break;
                                }
                            }

                            if (jsonArray.length() == 2)
                                lbList.add(0, new LeaderBoard(topList));
                            else if (jsonArray.length() == 3)
                                lbList.add(0, new LeaderBoard(topList));
                            else if (jsonArray.length() == 4)
                                lbList.add(0, new LeaderBoard(topList));
                            else
                                lbList.add(0, new LeaderBoard(topList));


                            if (startoffset == 0) {
                                adapter = new LeaderboardAdapter(LeaderBoardActivity.this, lbList, recyclerView);
                                recyclerView.setAdapter(adapter);
                                adapter.setOnLoadMoreListener(new LeaderboardAdapter.OnLoadMoreListener() {
                                    @Override
                                    public void onLoadMore() {
                                        //add null , so the adapter will check view_type and show progress bar at bottom
                                        if (lbList.size() < total) {
                                            lbList.add(null);
                                            adapter.notifyItemInserted(lbList.size() - 1);

                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    //   remove progress item
                                                    lbList.remove(lbList.size() - 1);
                                                    adapter.notifyItemRemoved(lbList.size());
                                                    if (lbList.contains(null)) {
                                                        for (int i = 0; i < lbList.size(); i++) {
                                                            if (lbList.get(i) == null) {
                                                                lbList.remove(i);
                                                                break;
                                                            }
                                                        }
                                                    }

                                                    offset = offset + PAGE_LIMIT;
                                                    StringRequest strReq = new StringRequest(Request.Method.POST, Constant.QUIZ_URL, new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                            try {

                                                                SCORE = Constant.SCORE;
                                                                USER_ID = Constant.USER_ID;

                                                                JSONObject obj = new JSONObject(response);

                                                                JSONArray jsonArray = obj.getJSONArray("data");
                                                                if (jsonArray.length() > 1) {
                                                                    for (int i = 1; i < jsonArray.length(); i++) {
                                                                        JSONObject object = jsonArray.getJSONObject(i);
                                                                        USER_RANK = object.getString(Constant.RANK);
                                                                        LeaderBoard leaderBoard = new LeaderBoard(object.getString(Constant.RANK),
                                                                                object.getString(Constant.name), object.getString(SCORE),
                                                                                object.getString(USER_ID), object.getString(Constant.PROFILE));
                                                                        lbList.add(leaderBoard);
                                                                        //adapter.notifyItemInserted(lbList.size());
                                                                    }
                                                                    adapter.notifyDataSetChanged();
                                                                    adapter.setLoaded();

                                                                } else {
                                                                    progressBar.setVisibility(View.GONE);

                                                                }
                                                            } catch (
                                                                    JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }, new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                            progressBar.setVisibility(View.GONE);
                                                        }
                                                    }) {
                                                        @Override
                                                        protected Map<String, String> getParams() {

                                                            Map<String, String> params = new HashMap<String, String>();
                                                            params.put(Constant.accessKey, Constant.accessKeyValue);
                                                            if (position == 0) {
                                                                params.put(Constant.GET_GLOBAL_LB, "1");

                                                            } else if (position == 1) {
                                                                params.put(Constant.getMontlyLeaderboard, "1");
                                                                params.put(Constant.DATE, date);

                                                            } else if (position == 2) {
                                                                params.put(Constant.GET_TODAYS_LB, "1");
                                                                params.put(Constant.FROM, date);
                                                                params.put(Constant.TO, date);

                                                            }
                                                            params.put(Constant.OFFSET, String.valueOf(offset));
                                                            params.put(Constant.LIMIT, String.valueOf(PAGE_LIMIT));
                                                            params.put(Constant.USER_ID, Session.getUserData(Session.USER_ID, getApplicationContext()));
                                                            return params;
                                                        }
                                                    };
                                                    AppController.getInstance().getRequestQueue().getCache().clear();
                                                    AppController.getInstance().addToRequestQueue(strReq, "next");
                                                }
                                            }, 1000);
                                        }
                                    }
                                });

                            }
                        } else {

                            progressBar.setVisibility(View.GONE);
                            tvAlert.setText(getString(R.string.no_data));
                            tvAlert.setVisibility(View.VISIBLE);
                            if (adapter != null)
                                adapter.notifyDataSetChanged();

                        }
                    } catch (
                            JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(Constant.accessKey, Constant.accessKeyValue);
                    if (position == 0) {
                        params.put(Constant.GET_GLOBAL_LB, "1");
                    } else if (position == 1) {
                        params.put(Constant.getMontlyLeaderboard, "1");
                        params.put(Constant.DATE, date);

                    } else if (position == 2) {

                        params.put(Constant.GET_TODAYS_LB, "1");
                        params.put(Constant.FROM, date);
                        params.put(Constant.TO, date);
                    }
                    params.put(Constant.OFFSET, String.valueOf(startoffset));
                    params.put(Constant.LIMIT, String.valueOf(PAGE_LIMIT));
                    params.put(Constant.USER_ID, Session.getUserData(Session.USER_ID, getApplicationContext()));
                    return params;
                }
            };
            AppController.getInstance().getRequestQueue().getCache().clear();
            AppController.getInstance().addToRequestQueue(strReq);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
