
package com.snowhillapps.brainspire.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.snowhillapps.brainspire.R;
import com.snowhillapps.brainspire.Constant;
import com.snowhillapps.brainspire.helper.Session;
import com.snowhillapps.brainspire.helper.Utils;
import com.snowhillapps.brainspire.model.Level;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

public class LevelActivity extends AppCompatActivity {
    public Toolbar toolbar;
    LevelListAdapter adapter;
    public static String fromQue;
    private static int levelNo = 1;
    List<Level> levelList;
    RecyclerView recyclerView;
    public TextView tvAlert;
    public ProgressBar progressBar;
    public RelativeLayout layout;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        layout = findViewById(R.id.layout);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.select_level);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        AdView mAdView = findViewById(R.id.banner_AdView);
        mAdView.loadAd(new AdRequest.Builder().build());
        invalidateOptionsMenu();

        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);
        tvAlert = findViewById(R.id.tvAlert);

        recyclerView.setLayoutManager(new LinearLayoutManager(LevelActivity.this));
        levelNo = MainActivity.dbHelper.GetLevelById(Constant.CATE_ID, Constant.SUB_CAT_ID);

        fromQue = getIntent().getStringExtra("fromQue");
        levelList = new ArrayList<>();

        for (int i = 0; i < Constant.TotalLevel; i++) {
            Level level = new Level();
            level.setLevelNo(levelNo);
            level.setLevel("" + (i + 1));
            levelList.add(level);
        }
        if (levelList.size() == 0) {
            tvAlert.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        } else {
            getData();
        }
    }


    private void getData() {
        if (Utils.isNetworkAvailable(LevelActivity.this)) {
            adapter = new LevelListAdapter(LevelActivity.this, levelList);
            recyclerView.setAdapter(adapter);
        } else {
            setSnackBar();
        }
    }

    public void setSnackBar() {
        Snackbar snackbar = Snackbar
                .make(layout, getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.retry), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getData();
                    }
                });

        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }

    public class LevelListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public Activity activity;
        private List<Level> levelList;

        public LevelListAdapter(Activity activity, List<Level> levelList) {
            this.levelList = levelList;
            this.activity = activity;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.level_lyt, parent, false);
            return new LevelViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            LevelViewHolder viewHolder = (LevelViewHolder) holder;
            levelNo = MainActivity.dbHelper.GetLevelById(Constant.CATE_ID, Constant.SUB_CAT_ID);
            Level level = levelList.get(position);
            viewHolder.levelNo.setText(getString(R.string.level_txt) + level.getLevel());
            if (levelNo >= position + 1)
                viewHolder.lock.setImageResource(R.drawable.unlock);
            else
                viewHolder.lock.setImageResource(R.drawable.lock);

            if (position >= (levelNo - 1)) {
                viewHolder.lock.setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimaryDark));
            } else {
                viewHolder.lock.setColorFilter(ContextCompat.getColor(activity, R.color.lock_gray));
                viewHolder.levelNo.setTextColor(ContextCompat.getColor(activity, R.color.lock_gray));
            }
            viewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Session.getSoundEnableDisable(activity)) {
                        Utils.backSoundonclick(activity);
                    }
                    if (Session.getVibration(activity)) {
                        Utils.vibrate(activity, Utils.VIBRATION_DURATION);
                    }
                    Utils.RequestlevelNo = position + 1;
                    if (levelNo >= position + 1) {
                        Intent intent = new Intent(LevelActivity.this, PlayActivity.class);
                        intent.putExtra("fromQue", fromQue);
                        startActivity(intent);
                    } else {
                        Toast.makeText(activity, getString(R.string.level_locked), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return levelList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class LevelViewHolder extends RecyclerView.ViewHolder {

            ImageView lock;
            RelativeLayout relativeLayout;
            TextView levelNo;

            public LevelViewHolder(View itemView) {
                super(itemView);
                lock = itemView.findViewById(R.id.lock);
                levelNo = itemView.findViewById(R.id.level_no);

                relativeLayout = itemView.findViewById(R.id.relativeLayout);
            }
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.setting:
                Utils.CheckVibrateOrSound(LevelActivity.this);
                Intent playQuiz = new Intent(LevelActivity.this, SettingActivity.class);
                startActivity(playQuiz);
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}

