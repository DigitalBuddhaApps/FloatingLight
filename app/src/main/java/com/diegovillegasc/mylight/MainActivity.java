package com.diegovillegasc.mylight;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class MainActivity extends AppCompatActivity {
    private InterstitialAd mInterstitialAd;

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        if (!getApplicationContext().getPackageManager().hasSystemFeature("android.hardware.camera.front")) {
            showNoFlashError();
        }
        if (VERSION.SDK_INT < 23 || Settings.canDrawOverlays(this)) {
            initializeView();
            return;
        }
        String stringBuilder = "package:" +
                getPackageName();
        startActivityForResult(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse(stringBuilder)), 102);
    }

    private void initializeView() {
        MobileAds.initialize(this, initializationStatus -> {
        });
        this.mInterstitialAd = new InterstitialAd(this);
        this.mInterstitialAd.setAdUnitId("ca-app-pub-2472157035155965/9119920668");
        this.mInterstitialAd.loadAd(new Builder().build());
        this.mInterstitialAd.setAdListener(new AdListener() {
            public void onAdClosed() {
                MainActivity mainActivity = MainActivity.this;
                mainActivity.startActivity(new Intent(mainActivity, Design.class));
                MainActivity.this.finish();
            }

            public void onAdLoaded() {
                MainActivity.this.mInterstitialAd.show();
            }

            public void onAdFailedToLoad(int i) {
                MainActivity mainActivity = MainActivity.this;
                mainActivity.startActivity(new Intent(mainActivity, Design.class));
                MainActivity.this.finish();
            }
        });
    }

    /* Access modifiers changed, original: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 102 && i2 == -1) {
            initializeView();
        } else {
            Toast.makeText(this, "Please Re-Open the app.!", Toast.LENGTH_SHORT).show();
        }
    }

    public void showNoFlashError() {
        AlertDialog create = new AlertDialog.Builder(this).create();
        create.setTitle("Error!");
        create.setMessage("Flash not available in this device...");
        create.setButton(-1, "OK", new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                MainActivity.this.finish();
            }
        });
        create.show();
    }
}
