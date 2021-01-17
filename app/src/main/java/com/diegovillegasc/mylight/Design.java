package com.diegovillegasc.mylight;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;

public class Design extends AppCompatActivity {
    public static final String ALPHA = "alpha";
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SIZE = "size";
    int altot;
    int anchot;
    private AdView mAdView;
    SeekBar sb_alpha;
    SeekBar sb_size;
    private int sp_alpha;
    private int sp_size;
    TextView tv_rate;
    TextView tv_share;
    TextView tv_sizeh;
    TextView tv_sizew;

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_design);
        setSupportActionBar((Toolbar) findViewById(R.id.barrasup));
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        this.anchot = point.x;
        this.altot = point.y;
        this.tv_sizeh = (TextView) findViewById(R.id.d_tv_sizeh);
        this.tv_sizew = (TextView) findViewById(R.id.d_tv_sizew);
        this.sb_size = (SeekBar) findViewById(R.id.d_sb_size);
        this.sb_alpha = (SeekBar) findViewById(R.id.d_sb_alpha);
        this.tv_rate = (TextView) findViewById(R.id.d_tv_rate);
        this.tv_share = (TextView) findViewById(R.id.d_tv_share);
        this.tv_sizeh.setText(String.valueOf(this.altot));
        this.tv_sizew.setText(String.valueOf(this.anchot));
        this.sb_alpha.setMax(255);
        this.sb_size.setMax(this.anchot);
        this.sb_alpha.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                Design.this.saveData();
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                Design.this.saveData();
            }
        });
        this.sb_size.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                Design.this.saveData2();
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                Design.this.saveData2();
                Design.this.tv_sizeh.setText(String.valueOf(Design.this.sb_size.getProgress()));
                Design.this.tv_sizew.setText(String.valueOf(Design.this.sb_size.getProgress()));
            }
        });
        loadData();
        updateViews();
        ContextCompat.startForegroundService(this, new Intent(this, FloatWidgetService.class));
        this.mAdView = (AdView) findViewById(R.id.adView);
        this.mAdView.loadAd(new Builder().build());
        ((Button) findViewById(R.id.createBtn)).setOnClickListener((OnClickListener) view -> {
        });
        this.tv_share.setOnClickListener(view -> {
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("text/plain");
            intent.putExtra("android.intent.extra.SUBJECT", "Link of app:");
            intent.putExtra("android.intent.extra.TEXT", "https://play.google.com/store/apps/details?id=com.diegovillegasc.mylight");
            Design.this.startActivity(Intent.createChooser(intent, "Share using"));
        });
        this.tv_rate.setOnClickListener(view -> {
            String str = "android.intent.action.VIEW";
            Design design;
            StringBuilder stringBuilder;
            try {
                design = Design.this;
                stringBuilder = new StringBuilder();
                stringBuilder.append("market://details?id=");
                stringBuilder.append(Design.this.getPackageName());
                design.startActivity(new Intent(str, Uri.parse(stringBuilder.toString())));
            } catch (ActivityNotFoundException unused) {
                design = Design.this;
                stringBuilder = new StringBuilder();
                stringBuilder.append("http://play.google.com/store/apps/details?id=");
                stringBuilder.append(Design.this.getPackageName());
                design.startActivity(new Intent(str, Uri.parse(stringBuilder.toString())));
            }
        });
    }

    public void saveData() {
        Editor edit = getSharedPreferences("sharedPrefs", 0).edit();
        edit.putInt("alpha", this.sb_alpha.getProgress());
        edit.apply();
    }

    public void saveData2() {
        Editor edit = getSharedPreferences("sharedPrefs", 0).edit();
        edit.putInt("size", this.sb_size.getProgress());
        edit.apply();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", 0);
        this.sp_alpha = sharedPreferences.getInt("alpha", 255);
        this.sp_size = sharedPreferences.getInt("size", 200);
    }

    public void updateViews() {
        this.sb_alpha.setProgress(this.sp_alpha);
        this.sb_size.setProgress(this.sp_size);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.item1 /*2131296346*/:
                Toast.makeText(this, "This is a Low Priority Background Service, it keep alive depending of your phone resources !", Toast.LENGTH_LONG).show();
                break;
            case R.id.item2 /*2131296347*/:
                Toast.makeText(this, "Coming Soon !", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
