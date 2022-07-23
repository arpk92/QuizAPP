package com.snowhillapps.brainspire.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.snowhillapps.brainspire.Constant;
import com.snowhillapps.brainspire.R;
import com.snowhillapps.brainspire.helper.AppController;
import com.snowhillapps.brainspire.helper.ArcNavigationView;
import com.snowhillapps.brainspire.helper.CircleImageView;
import com.snowhillapps.brainspire.helper.Session;
import com.snowhillapps.brainspire.helper.Utils;
import com.google.android.material.navigation.NavigationView;

public class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public ArcNavigationView navigationView;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle drawerToggle;
    protected FrameLayout frameLayout;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    public static CircleImageView imgProfile;
    public TextView tvEmail;
    public static TextView tvName;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.transparentStatusAndNavigation(DrawerActivity.this);
        setContentView(R.layout.activity_drawer);
        frameLayout = findViewById(R.id.content_frame);
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView.setNavigationItemSelectedListener(this);
        View view = navigationView.getHeaderView(0);
        imgProfile = view.findViewById(R.id.imgProfile);
        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);

        imgProfile.setDefaultImageResId(R.drawable.user_icon);
        if (Session.isLogin(getApplicationContext())) {
            imgProfile.setImageUrl(Session.getUserData(Session.PROFILE, getApplicationContext()), imageLoader);
            tvName.setText(getString(R.string.hello) + Session.getUserData(Session.NAME, getApplicationContext()));
            tvEmail.setText(Session.getUserData(Session.EMAIL, getApplicationContext()));
        } else {
            tvName.setText(getString(R.string.login_not));
        }

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Session.isLogin(getApplicationContext())) {
                    LoginPopUp();
                } else {
                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    startActivity(intent);
                }
            }
        });
        Utils.loadAd(DrawerActivity.this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.leaderboard:
                if (!Session.isLogin(getApplicationContext())) {
                    LoginPopUp();
                } else {
                    Intent leaderBoard = new Intent(getApplicationContext(), LeaderBoardActivity.class);
                    startActivity(leaderBoard);
                }

                drawerLayout.closeDrawers();
                break;

            case R.id.statistic:
                if (!Session.isLogin(getApplicationContext())) {
                    LoginPopUp();
                } else {
                    Intent statistic = new Intent(getApplicationContext(), UserStatistics.class);
                    startActivity(statistic);
                }
                Utils.displayInterstitial();
                drawerLayout.closeDrawers();
                break;

            case R.id.setting:
                Intent setting = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(setting);
                break;

            case R.id.notification:
                Intent notification = new Intent(getApplicationContext(), NotificationList.class);
                startActivity(notification);
                drawerLayout.closeDrawers();
                Utils.displayInterstitial();
                break;
            case R.id.bookmark:

                Intent bookmark = new Intent(getApplicationContext(), BookmarkList.class);
                startActivity(bookmark);
                drawerLayout.closeDrawers();
                Utils.displayInterstitial();
                break;

            case R.id.invite:
                if (!Session.isLogin(getApplicationContext())) {
                    LoginPopUp();
                } else {
                    Intent invite = new Intent(getApplicationContext(), InviteFriendActivity.class);
                    startActivity(invite);
                }
                Utils.displayInterstitial();
                drawerLayout.closeDrawers();
                break;
            case R.id.instruction:
                Intent instruction = new Intent(getApplicationContext(), InstructionActivity.class);
                startActivity(instruction);
                drawerLayout.closeDrawers();
                Utils.displayInterstitial();
                break;
            case R.id.share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(android.content.Intent.EXTRA_TEXT, Constant.SHARE_APP_TEXT + " " + Constant.APP_LINK);
                intent.putExtra(Intent.EXTRA_SUBJECT, "");
                startActivity(Intent.createChooser(intent, getString(R.string.share_via)));
                break;
            case R.id.about:
                Intent about = new Intent(getApplicationContext(), PrivacyPolicy.class);
                about.putExtra("type", "about");
                startActivity(about);
                break;
            case R.id.terms:
                Intent terms = new Intent(getApplicationContext(), PrivacyPolicy.class);
                terms.putExtra("type", "terms");
                startActivity(terms);
                break;
            case R.id.privacy:
                Intent privacy = new Intent(getApplicationContext(), PrivacyPolicy.class);
                privacy.putExtra("type", "privacy");
                startActivity(privacy);
                drawerLayout.closeDrawers();
                break;
            default:
        }
        return false;
    }

    public void LoginPopUp() {
        Intent intent = new Intent(getApplicationContext(), LoginPopup.class);
        startActivity(intent);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }
}
