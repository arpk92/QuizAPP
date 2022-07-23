package com.snowhillapps.brainspire.helper;

import android.app.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.snowhillapps.brainspire.Constant;
import com.snowhillapps.brainspire.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.snowhillapps.brainspire.activity.LoginActivity;
import com.snowhillapps.brainspire.model.Language;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Utils {
    public static TextToSpeech textToSpeech;
    public static AdRequest adRequest;
    public static InterstitialAd interstitial;
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;
    public static AlertDialog alertDialog;
    private static Vibrator sVibrator;
    public static int TotalQuestion = 1;
    public static int CoreectQuetion = 1;
    public static int WrongQuation = 1;
    public static int level_coin = 1;
    public static int level_score = 0;
    public static final long VIBRATION_DURATION = 100;
    public final static double DEG2RAD = (Math.PI / 180.0);
    public final static float FDEG2RAD = ((float) Math.PI / 180.f);

    @SuppressWarnings("unused")
    public final static double DOUBLE_EPSILON = Double.longBitsToDouble(1);

    @SuppressWarnings("unused")
    public final static float FLOAT_EPSILON = Float.intBitsToFloat(1);
    public static int RequestlevelNo = 1;
    public static final boolean DEFAULT_SOUND_SETTING = true;
    public static final boolean DEFAULT_VIBRATION_SETTING = true;
    public static final boolean DEFAULT_MUSIC_SETTING = false;
    public static final boolean DEFAULT_LAN_SETTING = true;
    public static final String FONTS = "fonts/";

    public static void backSoundonclick(Context mContext) {
        try {
            int resourceId = R.raw.click2;
            MediaPlayer mediaplayer = MediaPlayer.create(mContext, resourceId);
            mediaplayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setrightAnssound(Context mContext) {
        try {
            int resourceId = R.raw.right;
            MediaPlayer mediaplayer = MediaPlayer.create(mContext, resourceId);
            mediaplayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setwronAnssound(Context mContext) {
        try {
            int resourceId = R.raw.wrong;
            MediaPlayer mediaplayer = MediaPlayer.create(mContext, resourceId);
            mediaplayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void vibrate(Context context, long duration) {
        if (sVibrator == null) {
            sVibrator = (Vibrator) context
                    .getSystemService(Context.VIBRATOR_SERVICE);
        }
        if (sVibrator != null) {
            if (duration == 0) {
                duration = 50;
            }
            sVibrator.vibrate(duration);
        }
    }


    public static boolean isNetworkAvailable(Activity activity) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity == null) {
                return false;
            } else {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null) {
                    for (int i = 0; i < info.length; i++) {
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return false;
    }

    public static void loadAd(Context context) {

        interstitial = new InterstitialAd(context);
        interstitial.setAdUnitId(context.getString(R.string.admob_interstitial_id));
        adRequest = new AdRequest.Builder().build();
        interstitial.loadAd(adRequest);
    }

    public static void displayInterstitial() {
        if (interstitial.isLoaded()) {
            interstitial.show();
        } else {
            adRequest = new AdRequest.Builder().build();
            interstitial.loadAd(adRequest);
            interstitial.show();
        }
    }

    public static void CheckVibrateOrSound(Context context) {

        if (Session.getSoundEnableDisable(context)) {
            backSoundonclick(context);
        }
        if (Session.getVibration(context)) {
            vibrate(context, Utils.VIBRATION_DURATION);
        }
    }

    public static void InitializeTTF(Context context){
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                    textToSpeech.setSpeechRate(1f);
                    textToSpeech.setPitch(1.1f);

                }
            }
        });
    }
    public static void btnClick(View view, Activity activity) {
        Animation myAnim = AnimationUtils.loadAnimation(activity, R.anim.bounce);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        view.startAnimation(myAnim);
        CheckVibrateOrSound(activity);
    }

    static class MyBounceInterpolator implements android.view.animation.Interpolator {
        private double mAmplitude = 1;
        private double mFrequency = 10;

        MyBounceInterpolator(double amplitude, double frequency) {
            mAmplitude = amplitude;
            mFrequency = frequency;
        }

        public float getInterpolation(float time) {
            return (float) (-1 * Math.pow(Math.E, -time / mAmplitude) *
                    Math.cos(mFrequency * time) + 1);
        }
    }

    public static Bitmap getBitmapFromView(View view, int height, int width) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return bitmap;
    }

    public static void saveImage(ScrollView scrollView, Activity activity) {
        try {

            Bitmap bitmap = getBitmapFromView(scrollView, scrollView.getChildAt(0).getHeight(), scrollView.getChildAt(0).getWidth());
            File cachePath = new File(activity.getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.PNG, 75, stream);
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void ShareImage(Activity activity, String shareMsg) {
        File imagePath = new File(activity.getCacheDir(), "images");

        File newFile = new File(imagePath, "image.png");
        Uri contentUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", newFile);

        if (contentUri != null) {
            String shareBody = shareMsg;
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, activity.getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            activity.startActivity(Intent.createChooser(shareIntent, "Share via"));
        }
    }

    public static void ShareInfo(ScrollView scrollView, Activity activity, String shareMsg) {
        ProgressDialog pDialog = new ProgressDialog(activity);
        pDialog.setMessage("Please wait...");
        pDialog.setIndeterminate(true);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(true);
        new DownloadFiles(scrollView, pDialog, activity, shareMsg).execute();
    }

    public static class DownloadFiles extends AsyncTask<String, Integer, String> {
        ScrollView scrollView;
        ProgressDialog pDialog;
        Activity activity;
        String shareMsg;

        public DownloadFiles(ScrollView linearLayout, ProgressDialog pDialog, Activity activity, String shareMsg) {
            this.scrollView = linearLayout;
            this.pDialog = pDialog;
            this.activity = activity;
            this.shareMsg = shareMsg;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            saveImage(scrollView, activity);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (pDialog != null)
                pDialog.show();
        }


        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pDialog != null)
                pDialog.dismiss();
            ShareImage(activity, shareMsg);
        }
    }

    public static void GetSystemConfig(final Context context) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            boolean error = obj.getBoolean("error");
                            if (!error) {
                                JSONObject jsonobj = obj.getJSONObject("data");
                                Constant.APP_LINK = jsonobj.getString(Constant.KEY_APP_LINK);
                                Constant.MORE_APP_URL = jsonobj.getString(Constant.KEY_MORE_APP);
                                Constant.LANGUAGE_MODE = jsonobj.getString(Constant.KEY_LANGUAGE_MODE);
                                Constant.OPTION_E_MODE = jsonobj.getString(Constant.KEY_OPTION_E_MODE);
                                Constant.SHARE_APP_TEXT = jsonobj.getString(Constant.KEY_SHARE_TEXT);
                                if (Constant.LANGUAGE_MODE.equals("1"))
                                    Session.setBoolean(Session.LANG_MODE, true, context);
                                else {
                                    Session.setBoolean(Session.LANG_MODE, false, context);
                                    //Session.setBoolean(Session.IS_FIRST_TIME, false, context);
                                    Session.setCurrentLanguage(Constant.D_LANG_ID, context);
                                }

                                if (Constant.OPTION_E_MODE.equals("1"))
                                    Session.setBoolean(Session.E_MODE, true, context);
                                else
                                    Session.setBoolean(Session.E_MODE, false, context);


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
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(Constant.accessKey, Constant.accessKeyValue);
                params.put(Constant.GET_SYSTEM_CONFIG, "1");
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public static void postTokenToServer(final Context context) {
        final String token = Session.getDeviceToken(context);
        final String oldToken = Session.getUserData(Session.FCM, context);
        if (oldToken != null)
            if (!token.equalsIgnoreCase(oldToken)) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    boolean error = obj.getBoolean("error");
                                    if (!error) {
                                        Session.setDeviceToken(token, context);
                                        Session.setUserData(Session.FCM, token, context);
                                        FirebaseDatabase.getInstance().getReference("user").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("fcm_id").setValue(token);
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
                        params.put(Constant.updateFcmId, "1");
                        params.put(Constant.userId, Session.getUserData(Session.USER_ID, context));
                        params.put(Constant.fcmId, token);
                        return params;
                    }
                };
                AppController.getInstance().getRequestQueue().getCache().clear();
                AppController.getInstance().addToRequestQueue(stringRequest);
            }
    }

    public static void UpdateCoin(final Context context, final String coins) {
        final String token = Session.getDeviceToken(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            boolean error = obj.getBoolean("error");
                            if (!error) {

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
                params.put(Constant.updateFcmId, "1");
                params.put(Constant.userId, Session.getUserData(Session.USER_ID, context));
                params.put(Constant.fcmId, token);
                params.put(Constant.COINS, coins);
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);

    }

    public static void SignOutWarningDialog(final Activity activity) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        // Setting Dialog Message
        alertDialog.setMessage(activity.getResources().getString(R.string.logout_warning));
        alertDialog.setCancelable(false);
        final AlertDialog alertDialog1 = alertDialog.create();

        // Setting OK Button
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                Session.clearUserSession(activity);
                LoginManager.getInstance().logOut();
                LoginActivity.mAuth.signOut();
                FirebaseAuth.getInstance().signOut();
                Intent intentLogin = new Intent(activity, LoginActivity.class);
                activity.startActivity(intentLogin);
                activity.finish();
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog1.dismiss();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    public static void transparentStatusAndNavigation(Activity context) {

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true, context);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false, context);
            context.getWindow().setStatusBarColor(Color.TRANSPARENT);
            //context.getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    public static void RemoveGameRoomId(final String userId) {
        final DatabaseReference gameRef = FirebaseDatabase.getInstance().getReference(Constant.DB_GAME_ROOM);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(Constant.DB_GAME_ROOM).removeValue();
        if (userId != null) {
            String roomKey = gameRef.child(userId).getKey();
            if (userId.equals(roomKey)) {
                gameRef.child(roomKey).removeValue();
                gameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {

                            for (DataSnapshot dataSnapshot1 : data.getChildren()) {
                                if (userId.equals(dataSnapshot1.getKey())) {
                                    gameRef.child(Objects.requireNonNull(dataSnapshot1.getRef().getParent().getKey())).removeValue();
                                }
                                if (Objects.requireNonNull(data.child(Constant.AVAILABILITY).getValue()).toString().equalsIgnoreCase("1")) {
                                    if (dataSnapshot1.getKey().length() == 8) {
                                        System.out.println("====key " + dataSnapshot1.getKey());
                                        gameRef.child(Objects.requireNonNull(dataSnapshot1.getRef().getParent().getKey())).removeValue();
                                    }
                                }
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        }
    }

    public static void GetLanguage(final RecyclerView languageView, final Context context, final AlertDialog alertDialog) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {


                            JSONObject jsonObject = new JSONObject(response);
                            boolean error = jsonObject.getBoolean(Constant.ERROR);
                            if (!error) {
                                JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                                ArrayList<Language> languageList = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    Language language = new Language();
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    language.setId(object.getString(Constant.ID));
                                    language.setLanguage(object.getString(Constant.LANGUAGE));
                                    languageList.add(language);
                                }
                                LanguageAdapter languageAdapter = new LanguageAdapter(context, languageList, alertDialog);
                                languageView.setAdapter(languageAdapter);
                                if (!Session.getBoolean(Session.IS_FIRST_TIME, context)) {
                                    alertDialog.show();
                                }
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
                params.put(Constant.GET_LANGUAGES, "1");
                return params;
            }
        };

        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public static class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.ItemRowHolder> {
        private ArrayList<Language> dataList;
        private Context mContext;
        AlertDialog alertDialog;

        public LanguageAdapter(Context context, ArrayList<Language> dataList, AlertDialog alertDialog) {
            this.dataList = dataList;
            this.mContext = context;
            this.alertDialog = alertDialog;
        }

        @Override
        public LanguageAdapter.ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.language_layout, parent, false);
            return new LanguageAdapter.ItemRowHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull LanguageAdapter.ItemRowHolder holder, final int position) {

            final Language language = dataList.get(position);
            final ItemRowHolder itemRowHolder = holder;
            itemRowHolder.tvLanguage.setText(language.getLanguage());
            if (Session.getCurrentLanguage(mContext).equals(language.getId())) {
                itemRowHolder.radio.setImageResource(R.drawable.ic_radio_check);
            } else {
                itemRowHolder.radio.setImageResource(R.drawable.ic_radio_unchecked);
            }
            itemRowHolder.radio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemRowHolder.radio.setImageResource(R.drawable.ic_radio_check);
                    Session.setCurrentLanguage(language.getId(), mContext);
                    Session.setBoolean(Session.IS_FIRST_TIME, true, mContext);
                    notifyDataSetChanged();
                    alertDialog.dismiss();

                }
            });

        }

        @Override
        public int getItemCount() {
            return (dataList.size());
        }

        public class ItemRowHolder extends RecyclerView.ViewHolder {
            public ImageView radio;
            public TextView tvLanguage;


            public ItemRowHolder(View itemView) {
                super(itemView);
                radio = itemView.findViewById(R.id.radio);
                tvLanguage = itemView.findViewById(R.id.tvLanguage);
            }

        }

    }

    public static void ForgotPasswordPopUp(final Activity activity, final FirebaseAuth firebaseAuth) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.lyt_forgot_password, null);
        dialog.setView(dialogView);
        TextView tvSubmit = dialogView.findViewById(R.id.tvSubmit);

        final EditText edtEmail = dialogView.findViewById(R.id.edtEmail);

        final AlertDialog alertDialog = dialog.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.setCancelable(true);
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtEmail.getText().toString().trim();
                if (email.isEmpty())
                    edtEmail.setError(activity.getResources().getString(R.string.email_alert_1));
                else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
                    edtEmail.setError(activity.getResources().getString(R.string.email_alert_2));
                else {
                    firebaseAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(activity, "Email sent", Toast.LENGTH_SHORT).show();
                                        alertDialog.dismiss();
                                    }
                                }
                            });
                }
            }
        });

        alertDialog.show();
    }

    public static void setWindowFlag(final int bits, boolean on, Activity context) {
        Window win = context.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}
