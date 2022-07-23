package com.snowhillapps.brainspire.activity;

import android.annotation.SuppressLint;


import android.app.ProgressDialog;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.snowhillapps.brainspire.helper.CircleImageView;
import com.snowhillapps.brainspire.helper.Session;
import com.snowhillapps.brainspire.model.User;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.snowhillapps.brainspire.Constant;

import com.snowhillapps.brainspire.R;
import com.snowhillapps.brainspire.helper.ApiClient;
import com.snowhillapps.brainspire.helper.ApiInterface;
import com.snowhillapps.brainspire.helper.AppController;

import com.snowhillapps.brainspire.helper.Utils;
import com.snowhillapps.brainspire.model.Question;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;

public class GetOpponentActivity extends AppCompatActivity {
    private static final String FORMAT = "%02d";
    public static String roomKey = "";
    public static ArrayList<Question> battleQuestionList, questionArrayList;
    private static CountDownTimer countDownTimer;
    public boolean exist = false;
    private Context mContext;
    private AdView adView;
    private TextView tvPlayer1, tvPlayer2, tvTimeLeft, tvSecond, tvSearch, tvSearchPlayer, tvStateTitle;
    private NetworkImageView imgPlayer1, imgPlayer2;
    private DatabaseReference database, myRef;
    private ValueEventListener valueEventListener;
    boolean isRunning = false;
    public ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    public LinearLayout alertLayout, contentLayout;
    public RelativeLayout timerLayout;
    boolean player1Status, player2Status;
    public ProgressBar progressBar;
    public AlertDialog quiteDialog;
    public RecyclerView recyclerView;
    public String battleStart = "false", pauseCheck = "regular", questionResponse = "true", profilePlayer2, player1Name, player2Name,
            userId1, userId2, fcm1, fcm2, player, opponentId = "";

    public Toolbar toolbar;
    AlertDialog leaveDialog, timeAlertDialog, battleDialog;
    boolean isPlayStarted;
    public ArrayList<User> battleList;
    public ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_opponent);


        mContext = GetOpponentActivity.this;
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.matching_opponent));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        contentLayout = findViewById(R.id.contentLayout);
        timerLayout = findViewById(R.id.timerLayout);
        alertLayout = findViewById(R.id.alertLayout);
        adView = findViewById(R.id.banner_AdView);
        tvPlayer1 = findViewById(R.id.tv_player1_name);
        imgPlayer1 = findViewById(R.id.imgPlayer1);
        tvPlayer2 = findViewById(R.id.tv_player2_name);
        imgPlayer2 = findViewById(R.id.imgPlayer2);
        tvTimeLeft = findViewById(R.id.tv_time_left);
        tvStateTitle = findViewById(R.id.tvStateTitle);
        tvSearchPlayer = findViewById(R.id.tvSearchPlayer);
        imgPlayer1.setDefaultImageResId(R.drawable.ic_profile);
        imgPlayer2.setDefaultImageResId(R.drawable.ic_profile);
        imgPlayer2.setDefaultImageResId(R.drawable.ic_profile);
        progressBar = findViewById(R.id.progressBar);
        tvPlayer1.setText(getString(R.string.player_1));
        tvPlayer2.setText(getString(R.string.player_2));
        tvSecond = findViewById(R.id.tvSec);
        tvSearch = findViewById(R.id.tvSearch);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setNestedScrollingEnabled(false);
        Utils.GetSystemConfig(getApplicationContext());
        GetBattleStatestics();
        bannerAdd();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        /*
         *  this interface can be used to receive events about data changes at a location
         */
        valueEventListener = new ValueEventListener() {
            /*
            This method will be called with a snapshot of the data at this location.
            */
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (ds.exists() && ds.child(Constant.AVAILABILITY).getValue() != null) {
                            if (ds.child(Constant.AVAILABILITY).getValue() != null) {

                                if (ds.child(Constant.AVAILABILITY).getValue().toString().equalsIgnoreCase("1")) {
                                    if (ds.child(player).exists()) {
                                        roomKey = ds.getKey();
                                        Constant.GameRoomKey = ds.getKey();
                                    }
                                }

                                if (ds.child(Constant.AVAILABILITY).getValue().toString().equalsIgnoreCase("2")) {
                                    if (ds.child(player).exists()) {
                                        roomKey = ds.getKey();
                                        Constant.GameRoomKey = ds.getKey();
                                        for (DataSnapshot data : ds.getChildren()) {

                                            if (!data.getKey().equalsIgnoreCase(player)) {
                                                if (!data.getKey().equalsIgnoreCase(Constant.AVAILABILITY)) {
                                                    if (!data.getKey().equalsIgnoreCase(Constant.LANGUAGE_ID)) {
                                                        opponentId = data.getKey();
                                                        setSecondPlayerData();

                                                        try {

                                                            if (ds.child(player).getValue() != null)
                                                                player1Status = (boolean) (ds.child(player).child(Constant.STATUS).getValue());
                                                            if (ds.child(opponentId).getValue() != null)
                                                                player2Status = (boolean) ds.child(opponentId).child(Constant.STATUS).getValue();

                                                            if (player1Status && player2Status) {
                                                                isPlayStarted = true;
                                                            }
                                                            if (isPlayStarted) {
                                                                if (!player1Status || !player2Status) {
                                                                    showOtherUserQuitDialog();
                                                                }


                                                            }

                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                            }
                                        }


                                    }
                                }
                            }
                        }
                    }

                }
            }

            /*
             *  This method will be triggered in the event that this listener either failed at the server
             */
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        getData();
    }


    public void getData() {

        if (Utils.isNetworkAvailable(GetOpponentActivity.this)) {
            progressBar.setVisibility(View.VISIBLE);
            player = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            database = FirebaseDatabase.getInstance().getReference();
            Utils.RemoveGameRoomId(player);
            //call listener
            alertLayout.setVisibility(View.GONE);
            contentLayout.setVisibility(View.VISIBLE);
            timerLayout.setVisibility(View.GONE);
            setFirstPlayerData();
            tvSearchPlayer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SearchPlayerClickMethod();
                }
            });
        } else {
            alertLayout.setVisibility(View.VISIBLE);
            contentLayout.setVisibility(View.GONE);
            setSnackBar();

        }

    }

    public void SearchPlayerClickMethod() {
        exist = true;
        timerLayout.setVisibility(View.VISIBLE);
        tvSearchPlayer.setVisibility(View.GONE);
        myRef = FirebaseDatabase.getInstance().getReference(Constant.DB_GAME_ROOM);
        getQuestionForComputer();
        callGetRoomFunction();
        startTimer();
        myRef.addValueEventListener(valueEventListener);
    }

    public void setSnackBar() {
        Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content), getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.retry), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getData();
                    }
                });

        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }


    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * Constant.ALPHA_NUMERIC_STRING.length());
            builder.append(Constant.ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    // use timer toh get opposite player in specific time
    private void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(Constant.OPPONENT_SEARCH_TIME, Constant.COUNT_DOWN_TIMER) {
            @SuppressLint({"DefaultLocale", "SetTextI18n"})
            public void onTick(long millisUntilFinished) {
                isRunning = true;
                int progress = (int) (millisUntilFinished / 1000);
                tvTimeLeft.setText("" + String.format(FORMAT, progress));

                if (questionResponse.equals("false")) {

                    if (battleStart.equals("false")) {
                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                        }
                        callGamePlayActivity();
                        battleStart = "true";

                    }
                }
            }

            public void onFinish() {
                isRunning = false;
                tvTimeLeft.setText("00");
                if (tvPlayer1.getText().toString().equals(getString(R.string.player_1))) {
                    noPlayerAvailableDialog();
                } else {

                    if (battleQuestionList != null) {

                        if (questionResponse.equals("false")) {
                            if (battleStart.equals("false")) {
                                callGamePlayActivity();
                                battleStart = "true";
                            }
                        } else {

                            BattleDialog();

                        }

                    } else {
                        callGetRoomFunction_Virtual();

                    }


                    //  }
                }
            }
        }.start();
    }

    /*
     * show alert dialog when current user not available for some reason
     */
    public void noPlayerAvailableDialog() {
        try {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(GetOpponentActivity.this);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View dialogView = inflater.inflate(R.layout.dialog_time_up, null);
            dialog.setView(dialogView);
            TextView tvExit = dialogView.findViewById(R.id.tvExit);
            TextView tvMessage = dialogView.findViewById(R.id.tvMessage);
            tvMessage.setText(getString(R.string.alert_msg));
            LinearLayout tryLayout = dialogView.findViewById(R.id.tryLayout);
            CardView imgcardView = dialogView.findViewById(R.id.imgcardView);
            tryLayout.setVisibility(View.GONE);

            imgcardView.setVisibility(View.GONE);
            final AlertDialog alertDialog = dialog.create();
            tvExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    alertDialog.dismiss();

                    if (!roomKey.isEmpty()) {
                        DestroyKey(roomKey);
                        myRef.child(roomKey).removeValue();
                    }
                    myRef.removeEventListener(valueEventListener);
                    finish();
                }
            });
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.setCancelable(false);
            alertDialog.show();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void BattleDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(GetOpponentActivity.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.progress_dialog_lyt, null);
        dialog.setView(dialogView);

        TextView tvMessage = dialogView.findViewById(R.id.tvMessage);
        Button btnok = dialogView.findViewById(R.id.btnExit);
        battleDialog = dialog.create();
        tvMessage.setText(getString(R.string.battle_start_message));
        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DestroyKey(roomKey);
                //do ur stuff
                myRef.child(roomKey).child(player).child(Constant.STATUS).setValue(false);
                if (!roomKey.equalsIgnoreCase("")) {
                    myRef.child(roomKey).removeValue();
                }
                myRef.removeEventListener(valueEventListener);
                finish();
            }
        });

        battleDialog.show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (!questionResponse.equals("false")) {
                    if (battleDialog != null)
                        if (battleDialog.isShowing())
                            battleDialog.dismiss();
                    callGetRoomFunction_Virtual();
                    //  showTimeUpAlert(tvPlayer2.getText().toString());

                } else {
                    if (battleStart.equals("false")) {
                        callGamePlayActivity();
                        battleStart = "true";
                    }
                }

            }

        }, 5000);

    }

    @SuppressLint("SetTextI18n")
    private void showTimeUpAlert(final String playWith) {

        try {
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            hideProgressDialog();
            final AlertDialog.Builder dialog = new AlertDialog.Builder(GetOpponentActivity.this);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View dialogView = inflater.inflate(R.layout.dialog_time_up, null);
            dialog.setView(dialogView);
            TextView tvExit = dialogView.findViewById(R.id.tvExit);

            CircleImageView playerImg = dialogView.findViewById(R.id.imgPlayer);
            LinearLayout tryLayout = dialogView.findViewById(R.id.tryLayout);
            TextView btnRobot = dialogView.findViewById(R.id.btnRobot);
            TextView btnTryAgain = dialogView.findViewById(R.id.btnTryAgain);


            tryLayout.setVisibility(View.VISIBLE);
            playerImg.setErrorImageResId(R.drawable.ic_android);
            playerImg.setDefaultImageResId(R.drawable.ic_android);
            //  }

            timeAlertDialog = dialog.create();
            tvExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DestroyKey(roomKey);
                    myRef.child(roomKey).child(player).child(Constant.STATUS).setValue(false);
                    if (!roomKey.equalsIgnoreCase("")) {
                        myRef.child(roomKey).removeValue();
                    }
                    myRef.removeEventListener(valueEventListener);

                    finish();
                    timeAlertDialog.dismiss();
                }
            });

            btnRobot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (playWith.equals(getString(R.string.player_2)) || playWith.equals(getString(R.string.robot))) {

                        if (questionArrayList.size() != 0) {
                            callGamePlayActivity();
                        } else {
                            Toast.makeText(GetOpponentActivity.this, getString(R.string.question_not_available), Toast.LENGTH_SHORT).show();
                        }
                    }
                    timeAlertDialog.dismiss();
                }
            });
            btnTryAgain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    timeAlertDialog.dismiss();
                    if (!roomKey.equalsIgnoreCase("")) {
                        myRef.child(roomKey).removeValue();
                    }
                    myRef.removeEventListener(valueEventListener);
                    ReloadUserForBattle();
                }
            });
            timeAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            timeAlertDialog.setCancelable(false);
            timeAlertDialog.show();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ReloadUserForBattle() {
        tvPlayer1.setText(getString(R.string.player_1));
        tvPlayer2.setText(getString(R.string.player_2));
        player = "";
        opponentId = "";
        questionResponse = "true";

        imgPlayer1.setImageUrl("removed", imageLoader);
        imgPlayer2.setImageUrl("removed", imageLoader);
        imgPlayer1.setDefaultImageResId(R.drawable.ic_profile);
        imgPlayer2.setDefaultImageResId(R.drawable.ic_profile);
        imgPlayer1.setErrorImageResId(R.drawable.ic_profile);
        imgPlayer2.setErrorImageResId(R.drawable.ic_profile);
        roomKey = "";
        if (battleQuestionList != null)
            battleQuestionList.clear();
        getData();
        SearchPlayerClickMethod();

    }


    private void showOtherUserQuitDialog() {

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        DatabaseReference databaseReference = myRef.child(roomKey);
        if (databaseReference != null) {
            databaseReference.removeValue();
        }
        try {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(GetOpponentActivity.this);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View dialogView = inflater.inflate(R.layout.dialog_re_search, null);
            dialog.setView(dialogView);
            dialog.setCancelable(false);
            quiteDialog = dialog.create();
            TextView tvMessage = dialogView.findViewById(R.id.tv_message);
            TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
            TextView tvSearch = dialogView.findViewById(R.id.tvSearch);
            tvTitle.setText(tvPlayer1.getText().toString());
            TextView btnok = dialogView.findViewById(R.id.btnExit);

            tvMessage.setText(player2Name + getString(R.string.leave_battle_txt));

            tvSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ReloadUserForBattle();
                    quiteDialog.dismiss();
                }
            });
            btnok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DestroyKey(roomKey);
                    myRef.removeEventListener(valueEventListener);
                    finish();
                    quiteDialog.dismiss();
                }
            });

            Objects.requireNonNull(quiteDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            if (timeAlertDialog != null)
                if (timeAlertDialog.isShowing()) {
                    timeAlertDialog.dismiss();
                }
            quiteDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void setFirstPlayerData() {

        String userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        database.child(Constant.DB_USER)
                .child(userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            player1Name = (String) dataSnapshot.child(Constant.USER_NAME).getValue();
                            userId1 = (String) dataSnapshot.child(Constant.USER_ID).getValue();
                            fcm1 = (String) dataSnapshot.child(Constant.FCM_ID).getValue();
                            tvPlayer1.setText(player1Name);
                            imgPlayer1.setImageUrl(dataSnapshot.child(Constant.PROFILE_PIC).getValue().toString(), imageLoader);
                            progressBar.setVisibility(View.GONE);
                            tvTimeLeft.setVisibility(View.VISIBLE);
                            tvSecond.setVisibility(View.VISIBLE);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        database.child(Constant.DB_USER).child(userID).child(Constant.ONLINE_STATUS).setValue(true);


    }


    private void setSecondPlayerData() {

        database.child(Constant.DB_USER).child(opponentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    player2Name = (String) dataSnapshot.child(Constant.USER_NAME).getValue();
                    userId2 = (String) dataSnapshot.child(Constant.USER_ID).getValue();
                    fcm2 = (String) dataSnapshot.child(Constant.FCM_ID).getValue();
                    profilePlayer2 = dataSnapshot.child(Constant.PROFILE_PIC).getValue().toString();
                    tvPlayer2.setText(player2Name);
                    imgPlayer2.setImageUrl(profilePlayer2, imageLoader);
                    tvSearch.setText(getString(R.string.battle_start_message));
                    if (questionResponse.equals("true")) {
                        getQuestionsFromJson(fcm1, fcm2, userId1, userId2);
                    }


                } else {
                    player2Name = getString(R.string.robot);
                    tvPlayer2.setText(getString(R.string.robot));
                    showTimeUpAlert(tvPlayer2.getText().toString());
                    imgPlayer2.setDefaultImageResId(R.drawable.ic_android);
                    imgPlayer2.setColorFilter(Color.WHITE);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public void DestroyKey(final String roomKey) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constant.accessKey, Constant.accessKeyValue);
                params.put(Constant.getRandomQuestion, "1");
                params.put(Constant.GAME_ROOM_KEY, roomKey);
                params.put(Constant.DESTROY_GAME_KEY, "1");
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    private void callGamePlayActivity() {
        if (!opponentId.equalsIgnoreCase("")) {
            exist = false;
            startActivity(new Intent(mContext, GamePlayActivity.class)
                    .putExtra("gameid", roomKey)
                    .putExtra("opponentId", opponentId)
                    .putExtra("battlePlayer", tvPlayer2.getText().toString())
                    .putExtra("user_id1", userId1)
                    .putExtra("user_id2", userId2)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

            if (valueEventListener != null)
                myRef.removeEventListener(valueEventListener);

            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            questionResponse = "true";
            battleStart = "true";
            finish();


        }
    }

    public void getQuestionForComputer() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            String error = jsonObject.getString(Constant.ERROR);

                            if (error.equalsIgnoreCase("false")) {
                                JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                                questionArrayList = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    Question question = new Question();
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    question.setId(Integer.parseInt(object.getString(Constant.ID)));
                                    question.setQuestion(object.getString(Constant.QUESTION));
                                    question.setImage(object.getString(Constant.IMAGE));
                                    question.addOption(object.getString(Constant.OPTION_A).trim());
                                    question.addOption(object.getString(Constant.OPTION_B).trim());
                                    question.addOption(object.getString(Constant.OPTION_C).trim());
                                    question.addOption(object.getString(Constant.OPTION_D).trim());


                                    if (Session.getBoolean(Session.E_MODE, getApplicationContext())) {
                                        if (!object.getString(Constant.OPTION_E).trim().isEmpty() || !object.getString(Constant.OPTION_E).trim().equals(""))
                                            question.addOption(object.getString(Constant.OPTION_E).trim());
                                    }

                                    String rightAns = object.getString("answer");
                                    question.setAnsOption(rightAns);
                                    if (rightAns.equalsIgnoreCase("A")) {
                                        question.setTrueAns(object.getString(Constant.OPTION_A).trim());
                                    } else if (rightAns.equalsIgnoreCase("B")) {
                                        question.setTrueAns(object.getString(Constant.OPTION_B).trim());
                                    } else if (rightAns.equalsIgnoreCase("C")) {
                                        question.setTrueAns(object.getString(Constant.OPTION_C).trim());
                                    } else if (rightAns.equalsIgnoreCase("D")) {
                                        question.setTrueAns(object.getString(Constant.OPTION_D).trim());
                                    } else if (rightAns.equalsIgnoreCase("E")) {
                                        question.setTrueAns(object.getString(Constant.OPTION_E).trim());
                                    }
                                    question.setLevel(object.getString(Constant.LEVEL));
                                    question.setNote(object.getString(Constant.NOTE));
                                    questionArrayList.add(question);


                                }
                                Constant.MAX_QUESTION_PER_BATTLE = questionArrayList.size();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constant.accessKey, Constant.accessKeyValue);
                params.put(Constant.getQuestionForRobot, "1");
                if (Session.getBoolean(Session.LANG_MODE, getApplicationContext()))
                    params.put(Constant.LANGUAGE_ID, Session.getCurrentLanguage(getApplicationContext()));

                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    public void getQuestionsFromJson(final String fcm1, final String fcm2, final String userId1, final String userId2) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        battleQuestionList = new ArrayList<>();
                        battleQuestionList.clear();
                        try {


                            JSONObject jsonObject = new JSONObject(response);
                            String error = jsonObject.getString(Constant.ERROR);

                            questionResponse = error;

                            JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                Question question = new Question();
                                JSONObject object = jsonArray.getJSONObject(i);
                                question.setId(Integer.parseInt(object.getString(Constant.ID)));
                                question.setQuestion(object.getString(Constant.QUESTION));
                                question.setImage(object.getString(Constant.IMAGE));
                                question.addOption(object.getString(Constant.OPTION_A).trim());
                                question.addOption(object.getString(Constant.OPTION_B).trim());
                                question.addOption(object.getString(Constant.OPTION_C).trim());
                                question.addOption(object.getString(Constant.OPTION_D).trim());

                                if (Session.getBoolean(Session.E_MODE, getApplicationContext())) {
                                    if (!object.getString(Constant.OPTION_E).trim().isEmpty() || !object.getString(Constant.OPTION_E).trim().equals(""))
                                        question.addOption(object.getString(Constant.OPTION_E).trim());
                                }
                                String rightAns = object.getString("answer");
                                question.setAnsOption(rightAns);
                                if (rightAns.equalsIgnoreCase("A")) {
                                    question.setTrueAns(object.getString(Constant.OPTION_A).trim());
                                } else if (rightAns.equalsIgnoreCase("B")) {
                                    question.setTrueAns(object.getString(Constant.OPTION_B).trim());
                                } else if (rightAns.equalsIgnoreCase("C")) {
                                    question.setTrueAns(object.getString(Constant.OPTION_C).trim());
                                } else if (rightAns.equalsIgnoreCase("D")) {
                                    question.setTrueAns(object.getString(Constant.OPTION_D).trim());
                                } else if (rightAns.equalsIgnoreCase("E")) {
                                    question.setTrueAns(object.getString(Constant.OPTION_E).trim());
                                }
                                question.setLevel(object.getString(Constant.LEVEL));
                                question.setNote(object.getString(Constant.NOTE));


                                battleQuestionList.add(question);


                            }
                            Constant.MAX_QUESTION_PER_BATTLE = battleQuestionList.size();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constant.accessKey, Constant.accessKeyValue);
                params.put(Constant.getRandomQuestion, "1");
                params.put(Constant.USER_ID_1, userId1);
                params.put(Constant.FCM_ID_1, fcm1);
                params.put(Constant.USER_ID_2, userId2);
                params.put(Constant.FCM_ID_2, fcm2);
                params.put(Constant.GAME_ROOM_KEY, roomKey);
                if (Session.getBoolean(Session.LANG_MODE, getApplicationContext()))
                    params.put(Constant.LANGUAGE_ID, Session.getCurrentLanguage(getApplicationContext()));

                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void callGetRoomFunction() {
        Map<String, String> registerMap = new HashMap<>();
        registerMap.put(Constant.USER_ID, player);
        registerMap.put(Constant.LANGUAGE_ID, Session.getCurrentLanguage(getApplicationContext()));
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<Map> call = apiService.create(registerMap);
        call.enqueue(new retrofit2.Callback<Map>() {
            @Override
            public void onResponse(Call<Map> call, Response<Map> response) {
                try {
                    if (response.body() != null) {
                        Log.e("game room response****", response.body().toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Map> call, Throwable t) {

            }
        });

    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void callGetRoomFunction_Virtual() {
        Map<String, String> registerMap = new HashMap<>();
        registerMap.put(Constant.USER_ID, randomAlphaNumeric(8));
        registerMap.put(Constant.LANGUAGE_ID, Session.getCurrentLanguage(getApplicationContext()));
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<Map> call = apiService.create(registerMap);
        call.enqueue(new retrofit2.Callback<Map>() {
            @Override
            public void onResponse(Call<Map> call, Response<Map> response) {
                try {

                    if (response.body() != null) {
                        assert response.body() != null;
                    }
                    showProgressDialog();

                } catch (Exception e) {

                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Map> call, Throwable t) {
            }
        });
    }

    public void GetBattleStatestics() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            String error = jsonObject.getString(Constant.ERROR);
                            if (error.equalsIgnoreCase("false")) {
                                JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                                battleList = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    User battle = new User();
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    battle.setOpponentName(object.getString(Constant.OPPONENT_NAME));
                                    battle.setOpponentProfile(object.getString(Constant.OPPONENT_PROFILE));
                                    battle.setResut(object.getString(Constant.MY_STATUS));
                                    battleList.add(battle);
                                }

                                BattleAdapter adapter = new BattleAdapter(getApplicationContext(), battleList);
                                recyclerView.setAdapter(adapter);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constant.accessKey, Constant.accessKeyValue);
                params.put(Constant.GET_BATTLE_STATISTICS, "1");
                params.put(Constant.USER_ID, Session.getUserData(Session.USER_ID, getApplicationContext()));
                params.put(Constant.OFFSET, "0");
                params.put(Constant.LIMIT, Constant.BATTLE_PAGE_LIMIT);
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public class BattleAdapter extends RecyclerView.Adapter<BattleAdapter.ItemRowHolder> {
        private ArrayList<User> battleList;
        private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        public Context context;

        public BattleAdapter(Context context, ArrayList<User> battleList) {
            this.battleList = battleList;
            this.context = context;
        }

        @Override
        public BattleAdapter.ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.battle_layout, parent, false);
            return new ItemRowHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemRowHolder holder, final int position) {
            User battle = battleList.get(position);
            holder.imgProfile.setDefaultImageResId(R.drawable.ic_profile1);
            holder.imgOpponent.setDefaultImageResId(R.drawable.ic_profile1);
            holder.imgProfile.setImageUrl(Session.getUserData(Session.PROFILE, context), imageLoader);
            holder.imgOpponent.setImageUrl(battle.getOpponentProfile(), imageLoader);
            holder.tvUserName.setText(Session.getUserData(Session.NAME, context));
            holder.tvOpponent.setText(battle.getOpponentName());
            holder.tvResult.setText(battle.getResut());
        }

        @Override
        public int getItemCount() {
            return battleList.size();
        }

        public class ItemRowHolder extends RecyclerView.ViewHolder {
            TextView tvUserName, tvOpponent, tvResult;
            CircleImageView imgProfile, imgOpponent;

            public ItemRowHolder(View itemView) {
                super(itemView);
                tvUserName = itemView.findViewById(R.id.tvUserName);
                tvOpponent = itemView.findViewById(R.id.tvOpponent);
                tvResult = itemView.findViewById(R.id.tvResult);
                imgProfile = itemView.findViewById(R.id.imgProfile);
                imgOpponent = itemView.findViewById(R.id.imgOpponent);

            }
        }
    }

    public void bannerAdd() {
        AdRequest adRequest1 = new AdRequest.Builder().build();
        adView.loadAd(adRequest1);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

    }


    public void BackDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(GetOpponentActivity.this);

        // Setting Dialog Message
        alertDialog.setMessage(getResources().getString(R.string.back_message));
        alertDialog.setCancelable(false);
        leaveDialog = alertDialog.create();
        // Setting OK Button
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (Utils.isNetworkAvailable(GetOpponentActivity.this)) {
                    if (countDownTimer != null)
                        countDownTimer.cancel();
                    if (myRef != null) {
                        myRef.child(roomKey).child(player).child(Constant.STATUS).setValue(false);
                        if (!roomKey.equalsIgnoreCase("")) {
                            myRef.child(roomKey).removeValue();
                        }
                        myRef.removeEventListener(valueEventListener);
                    }
                }
                leaveDialog.dismiss();
                finish();
                // onBackPressed();
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                leaveDialog.dismiss();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (countDownTimer != null)
            BackDialog();
        else
            super.onBackPressed();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Utils.isNetworkAvailable(GetOpponentActivity.this))
            if (pauseCheck.equals("regular")) {
                if (exist) {
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }
                    myRef.child(roomKey).child(player).child(Constant.STATUS).setValue(false);
                    if (!roomKey.equalsIgnoreCase("")) {
                        myRef.child(roomKey).removeValue();
                    }
                    myRef.removeEventListener(valueEventListener);
                    finish();
                }
            }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.bookmark).setVisible(false);
        menu.findItem(R.id.report).setVisible(false);
        //  menu.findItem(R.id.setting).setVisible(true);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.setting:
                pauseCheck = "setting";
                Intent playQuiz = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(playQuiz);
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
