package com.snowhillapps.brainspire.helper;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

public class ApiConfig {

    static DecimalFormat decimalFormat = new DecimalFormat("#.##");
    public static SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy hh:mm:ssaa");
    public static SimpleDateFormat format2 = new SimpleDateFormat("dd-MMM''yy");
    public static SimpleDateFormat format3 = new SimpleDateFormat("hh:mmaa\ndd-MMM''yy");

    public static String VolleyErrorMessage(VolleyError error) {
        String message = "";
        try {
            if (error instanceof NetworkError) {
                message = "Cannot connect to Internet...Please check your connection!";
            } else if (error instanceof ServerError) {
                message = "The server could not be found. Please try again after some time!!";
            } else if (error instanceof AuthFailureError) {
                message = "Cannot connect to Internet...Please check your connection!";
            } else if (error instanceof ParseError) {
                message = "Parsing error! Please try again after some time!!";
            } else if (error instanceof NoConnectionError) {
                message = "Cannot connect to Internet...Please check your connection!";
            } else if (error instanceof TimeoutError) {
                message = "Connection TimeOut! Please check your internet connection.";
            } else
                message = "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

    public static void RequestToVolley(final VolleyCallback callback, final Activity activity, final String url, final Map<String, String> params, final boolean isprogress) {
        final ProgressDisplay progressDisplay = new ProgressDisplay(activity);

        if (Utils.isNetworkAvailable(activity)) {
            if (isprogress)
                progressDisplay.showProgress();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    callback.onSuccess(true, response);
                    if (isprogress)
                        progressDisplay.hideProgress();
                }

            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (isprogress)
                                progressDisplay.hideProgress();
                            callback.onSuccess(false, "");
                            String message = VolleyErrorMessage(error);
                            if (!message.equals(""))
                                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();

                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    return params;
                }
            };
            AppController.getInstance().getRequestQueue().getCache().clear();
            AppController.getInstance().addToRequestQueue(stringRequest);
        }

    }

    public static int dpToPx(int dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    public static boolean CheckValidattion(String item, boolean isemailvalidation, boolean ismobvalidation) {
        if (item.length() == 0)
            return true;
        else if (isemailvalidation && (!android.util.Patterns.EMAIL_ADDRESS.matcher(item).matches()))
            return true;
        else if (ismobvalidation && (item.length() < 10 || item.length() > 12))
            return true;
        else
            return false;

    }


    public static String GetDiscount(String oldprice, String newprice) {
        double dold = Double.parseDouble(oldprice);
        double dnew = Double.parseDouble(newprice);

        //return String.valueOf(((dnew / dold) - 1) * 100);
        return String.format("%.2f", (((dnew / dold) - 1) * 100));
    }








}
