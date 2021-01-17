package com.diegovillegasc.mylight;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat.Builder;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class FloatWidgetService extends Service {
    public static final String ALPHA = "alpha";
    public static final String BLOQUEO = "bloqueo";
    public static final String PUNTOX = "puntox";
    public static final String PUNTOY = "puntoy";
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SIZE = "size";
    boolean cerrar = false;
    ImageView closeButtonCollapsed;
    Handler handler;
    RelativeLayout ll;
    RelativeLayout ll_salir;
    ImageView loff;
    ImageView lon;
    boolean luz = false;
    String mCameraId;
    CameraManager mCameraManager;
    View mFloatingWidget;
    WindowManager mWindowManager;
    boolean mov = false;
    Notification notification;
    LayoutParams params;
    int sp_alpha;
    boolean sp_bloqueo;
    int sp_size;
    int sp_x;
    int sp_y;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        this.notification = new Builder(this, App.CHANNEL_ID).setContentTitle("Running widget in background").setSmallIcon(R.drawable.linterna1).setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0)).build();
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", 0);
        this.sp_alpha = sharedPreferences.getInt("alpha", 255);
        this.sp_size = sharedPreferences.getInt("size", 200);
        this.sp_x = sharedPreferences.getInt(PUNTOX, 1);
        this.sp_y = sharedPreferences.getInt(PUNTOY, 1);
        this.sp_bloqueo = sharedPreferences.getBoolean(BLOQUEO, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                this.mCameraId = this.mCameraManager.getCameraIdList()[0];
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        this.mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        this.mFloatingWidget = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);
        this.params = new LayoutParams(-2, -2, 2038, 8, -3);
        LayoutParams layoutParams = this.params;
        layoutParams.gravity = 17;
        layoutParams.x = 100;
        layoutParams.y = 100;
        this.mWindowManager.addView(this.mFloatingWidget, layoutParams);
        this.ll = (RelativeLayout) this.mFloatingWidget.findViewById(R.id.collapse_view);
        this.ll_salir = (RelativeLayout) this.mFloatingWidget.findViewById(R.id.d_ll_salir);
        this.closeButtonCollapsed = (ImageView) this.mFloatingWidget.findViewById(R.id.close_btn);
        this.lon = (ImageView) this.mFloatingWidget.findViewById(R.id.luz_on);
        this.loff = (ImageView) this.mFloatingWidget.findViewById(R.id.luz_off);
        this.lon.setImageAlpha(this.sp_alpha);
        this.loff.setImageAlpha(this.sp_alpha);
        this.closeButtonCollapsed.setImageAlpha(this.sp_alpha);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(100, 100);
        int i = this.sp_size;
        layoutParams2.width = i;
        layoutParams2.height = i;
        this.ll.setLayoutParams(layoutParams2);
        layoutParams2 = new RelativeLayout.LayoutParams(100, 100);
        int i2 = this.sp_size;
        layoutParams2.width = (int) (((double) i2) * 0.28d);
        layoutParams2.height = (int) (((double) i2) * 0.28d);
        layoutParams2.addRule(11, 10);
        this.ll_salir.setLayoutParams(layoutParams2);
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        startForeground(1824, this.notification);
        this.closeButtonCollapsed.setOnClickListener(view -> {
            FloatWidgetService.this.mWindowManager.removeView(FloatWidgetService.this.mFloatingWidget);
            FloatWidgetService.this.stopSelf();
            System.exit(0);
        });
        this.lon.setOnTouchListener(new OnTouchListener() {
            private static final int MAX_CLICK_DISTANCE = 15;
            private static final int MAX_CLICK_DURATION = 1000;
            private float initialTouchX;
            private float initialTouchY;
            private int initialX;
            private int initialY;
            private long pressStartTime;
            private boolean stayedWithinClickDistance;

            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == 0) {
                    this.pressStartTime = System.currentTimeMillis();
                    this.initialX = FloatWidgetService.this.params.x;
                    this.initialY = FloatWidgetService.this.params.y;
                    this.initialTouchX = motionEvent.getRawX();
                    this.initialTouchY = motionEvent.getRawY();
                    return true;
                } else if (action == 1) {
                    action = (int) (motionEvent.getRawX() - this.initialTouchX);
                    int rawY = (int) (motionEvent.getRawY() - this.initialTouchY);
                    float sqrt = ((float) Math.sqrt((double) ((action * action) + (rawY * rawY)))) / FloatWidgetService.this.getResources().getDisplayMetrics().density;
                    if (System.currentTimeMillis() - this.pressStartTime < 1000 && sqrt < 15.0f) {
                        FloatWidgetService.this.lon.setVisibility(View.GONE);
                        FloatWidgetService.this.loff.setVisibility(View.VISIBLE);
                        FloatWidgetService.this.turnOffFlash();
                    }
                    return true;
                } else if (action != 2) {
                    return false;
                } else {
                    FloatWidgetService.this.params.x = this.initialX + ((int) (motionEvent.getRawX() - this.initialTouchX));
                    FloatWidgetService.this.params.y = this.initialY + ((int) (motionEvent.getRawY() - this.initialTouchY));
                    FloatWidgetService.this.mWindowManager.updateViewLayout(FloatWidgetService.this.mFloatingWidget, FloatWidgetService.this.params);
                    return true;
                }
            }
        });
        this.loff.setOnTouchListener(new OnTouchListener() {
            private static final int MAX_CLICK_DISTANCE = 15;
            private static final int MAX_CLICK_DURATION = 1000;
            private float initialTouchX;
            private float initialTouchY;
            private int initialX;
            private int initialY;
            private long pressStartTime;

            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == 0) {
                    this.pressStartTime = System.currentTimeMillis();
                    this.initialX = FloatWidgetService.this.params.x;
                    this.initialY = FloatWidgetService.this.params.y;
                    this.initialTouchX = motionEvent.getRawX();
                    this.initialTouchY = motionEvent.getRawY();
                    FloatWidgetService.this.mov = false;
                    return true;
                } else if (action == 1) {
                    action = (int) (motionEvent.getRawX() - this.initialTouchX);
                    int rawY = (int) (motionEvent.getRawY() - this.initialTouchY);
                    float sqrt = ((float) Math.sqrt((double) ((action * action) + (rawY * rawY)))) / FloatWidgetService.this.getResources().getDisplayMetrics().density;
                    if (System.currentTimeMillis() - this.pressStartTime < 1000 && sqrt < 15.0f) {
                        FloatWidgetService.this.lon.setVisibility(View.VISIBLE);
                        FloatWidgetService.this.loff.setVisibility(View.GONE);
                        FloatWidgetService.this.turnOnFlash();
                    }
                    return true;
                } else if (action != 2) {
                    return false;
                } else {
                    FloatWidgetService floatWidgetService = FloatWidgetService.this;
                    floatWidgetService.mov = true;
                    floatWidgetService.params.x = this.initialX + ((int) (motionEvent.getRawX() - this.initialTouchX));
                    FloatWidgetService.this.params.y = this.initialY + ((int) (motionEvent.getRawY() - this.initialTouchY));
                    FloatWidgetService.this.mWindowManager.updateViewLayout(FloatWidgetService.this.mFloatingWidget, FloatWidgetService.this.params);
                    return true;
                }
            }
        });
        this.handler = new Handler();
        this.handler.postDelayed(new Runnable() {
            public void run() {
                SharedPreferences sharedPreferences = FloatWidgetService.this.getSharedPreferences("sharedPrefs", 0);
                FloatWidgetService.this.sp_alpha = sharedPreferences.getInt("alpha", 255);
                FloatWidgetService.this.sp_size = sharedPreferences.getInt("size", 200);
                FloatWidgetService.this.lon.setImageAlpha(FloatWidgetService.this.sp_alpha);
                FloatWidgetService.this.loff.setImageAlpha(FloatWidgetService.this.sp_alpha);
                FloatWidgetService.this.closeButtonCollapsed.setImageAlpha(FloatWidgetService.this.sp_alpha);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(100, 100);
                layoutParams.width = FloatWidgetService.this.sp_size;
                layoutParams.height = FloatWidgetService.this.sp_size;
                layoutParams.addRule(7);
                FloatWidgetService.this.ll.setLayoutParams(layoutParams);
                layoutParams = new RelativeLayout.LayoutParams(100, 100);
                layoutParams.width = (int) (((double) FloatWidgetService.this.sp_size) * 0.28d);
                layoutParams.height = (int) (((double) FloatWidgetService.this.sp_size) * 0.28d);
                layoutParams.addRule(11, 10);
                FloatWidgetService.this.ll_salir.setLayoutParams(layoutParams);
                FloatWidgetService.this.handler.postDelayed(this, 500);
            }
        }, 500);
        return Service.START_NOT_STICKY;
    }

    public void onDestroy() {
        View view = this.mFloatingWidget;
        if (view != null && view.isShown()) {
            this.mWindowManager.removeView(this.mFloatingWidget);
        }
        this.handler.removeCallbacks(null);
        super.onDestroy();
    }

    private void turnOnFlash() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.mCameraManager.setTorchMode(this.mCameraId, true);
            }
            this.luz = true;
        } catch (CameraAccessException e) {
            e.printStackTrace();
            this.luz = false;
        }
    }

    private void turnOffFlash() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.mCameraManager.setTorchMode(this.mCameraId, false);
            }
            this.luz = false;
        } catch (CameraAccessException e) {
            e.printStackTrace();
            this.luz = true;
        }
    }
}
