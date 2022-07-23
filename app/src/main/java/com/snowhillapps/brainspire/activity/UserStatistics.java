package com.snowhillapps.brainspire.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.snowhillapps.brainspire.Constant;
import com.snowhillapps.brainspire.R;
import com.snowhillapps.brainspire.helper.AppController;
import com.snowhillapps.brainspire.helper.AudienceProgress;
import com.snowhillapps.brainspire.helper.CircleImageView;
import com.snowhillapps.brainspire.helper.Session;
import com.snowhillapps.brainspire.helper.Utils;
import com.snowhillapps.brainspire.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserStatistics extends AppCompatActivity {


    Toolbar toolbar;
    TextView tvName, tvRank, tvScore, tvCoin, tvStrongCate, tvStrongRatio, tvWeakCate, tvWeakRatio, tvTotalQue, tvCorrect, tvInCorrect, tvCorrectP, tvInCorrectP;
    CircleImageView imgProfile;
    public ProgressBar strongProgress, weakProgress;
    public AudienceProgress progress;
    String totalQues, correctQues, inCorrectQues, strongCate, weakCate, strongRatio, weakRatio;
    public ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    public ArrayList<User> battleList;
    public RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_statistics);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.user_statistics));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        AdView mAdView = findViewById(R.id.banner_AdView);
        mAdView.loadAd(new AdRequest.Builder().build());
        imgProfile = findViewById(R.id.imgProfile);
        tvName = findViewById(R.id.tvName);
        tvRank = findViewById(R.id.tvRank);
        tvScore = findViewById(R.id.tvScore);
        tvCoin = findViewById(R.id.tvCoin);
        tvStrongCate = findViewById(R.id.tvStrongCate);
        tvStrongRatio = findViewById(R.id.tvStrongRatio);
        tvWeakCate = findViewById(R.id.tvWeakCate);
        tvWeakRatio = findViewById(R.id.tvWeakRatio);
        tvTotalQue = findViewById(R.id.tvAttended);
        tvCorrect = findViewById(R.id.tvCorrect);
        tvInCorrect = findViewById(R.id.tvInCorrect);
        tvCorrectP = findViewById(R.id.tvCorrectP);
        tvInCorrectP = findViewById(R.id.tvInCorrectP);
        strongProgress = findViewById(R.id.strongProgress);
        weakProgress = findViewById(R.id.weakProgress);
        progress = findViewById(R.id.progress);
        strongProgress.setMax(100);
        weakProgress.setMax(100);

        imgProfile.setDefaultImageResId(R.drawable.ic_account);
        imgProfile.setImageUrl(Session.getUserData(Session.PROFILE, UserStatistics.this), imageLoader);
        tvName.setText(getString(R.string.hello) + Session.getUserData(Session.NAME, UserStatistics.this));
        if (Utils.isNetworkAvailable(UserStatistics.this)) {
            GetUserData();
            GetUserStatistics();
        }

    }


    public void GetUserStatistics() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new Response.Listener<String>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);

                            boolean error = obj.getBoolean("error");
                            if (!error) {
                                JSONObject object = obj.getJSONObject(Constant.DATA);
                                totalQues = object.getString(Constant.QUESTION_ANSWERED);
                                correctQues = object.getString(Constant.CORRECT_ANSWERS);
                                inCorrectQues = String.valueOf(Integer.parseInt(totalQues) - Integer.parseInt(correctQues));
                                strongCate = object.getString(Constant.STRONG_CATE);
                                weakCate = object.getString(Constant.WEAK_CATE);
                                strongRatio = object.getString(Constant.RATIO_1);
                                weakRatio = object.getString(Constant.RATIO_2);

                                tvStrongCate.setText(strongCate);
                                tvWeakCate.setText(weakCate);
                                tvTotalQue.setText(totalQues);
                                tvCorrect.setText(correctQues);
                                tvInCorrect.setText(inCorrectQues);
                                tvStrongRatio.setText(strongRatio + getString(R.string.modulo_sign));
                                tvWeakRatio.setText(weakRatio + getString(R.string.modulo_sign));
                                int percentCorrect = (Integer.parseInt(correctQues) * 100) / Integer.parseInt(totalQues);
                                int percentInCorrect = (Integer.parseInt(inCorrectQues) * 100) / Integer.parseInt(totalQues);
                                tvCorrectP.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_green, 0, 0, 0);
                                tvInCorrectP.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_red, 0, 0, 0);
                                tvCorrectP.setText(percentCorrect + getString(R.string.modulo_sign));
                                tvInCorrectP.setText(percentInCorrect + getString(R.string.modulo_sign));
                                strongProgress.setProgress(Integer.parseInt(strongRatio));
                                weakProgress.setProgress(Integer.parseInt(weakRatio));
                                progress.SetAttributesForStatistics(getApplicationContext());
                                progress.setMaxProgress(Integer.parseInt(totalQues));
                                progress.setCurrentProgress(Integer.parseInt(correctQues));


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constant.accessKey, Constant.accessKeyValue);
                params.put(Constant.GET_USER_STATISTICS, "1");
                params.put(Constant.USER_ID, Session.getUserData(Session.USER_ID, UserStatistics.this));
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);

    }

    public void GetUserData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject obj = new JSONObject(response);
                            boolean error = obj.getBoolean("error");
                            if (!error) {
                                JSONObject jsonobj = obj.getJSONObject("data");
                                Constant.TOTAL_COINS = Integer.parseInt(jsonobj.getString(Constant.COINS));
                                tvCoin.setText("" + Constant.TOTAL_COINS);
                                tvRank.setText("" + jsonobj.getString(Constant.GLOBAL_RANK));
                                tvScore.setText(jsonobj.getString(Constant.GLOBAL_SCORE));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constant.accessKey, Constant.accessKeyValue);
                params.put(Constant.GET_USER_BY_ID, "1");
                params.put(Constant.ID, Session.getUserData(Session.USER_ID, getApplicationContext()));

                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
