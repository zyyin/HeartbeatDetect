package peitumedia.com.heartbeatdetect;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import static android.content.ContentValues.TAG;
import static java.security.AccessController.getContext;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class HeartBeatActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;


    private View mControlsView;
    private boolean mVisible;

    private Camera mCamera;
    private peitumedia.com.heartbeatdetect.CameraPreview mPreview;
    private static final int PERMISSIONS_REQUEST_CAPTURE_IMAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_heart_beat);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // User may have declined earlier, ask Android if we should show him a reason

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                // show an explanation to the user
                // Good practise: don't block thread after the user sees the explanation, try again to request the permission.

                // CALLBACK_NUMBER is a integer constants
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAPTURE_IMAGE);
                // The callback method gets the result of the request.
            }
        } else {


        }

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
       // delayedHide(100);
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mPreview.setFlash();
            return false;
        }
    };

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCamera = getCameraInstance();

        try{
            Camera.Parameters parameters = mCamera.getParameters();
            if(this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE)

            {
                parameters.set("orientation", "portrait");
                mCamera.setDisplayOrientation(90);

            } else
            {
                parameters.set("orientation", "landscape");
                mCamera.setDisplayOrientation(0);

            }
            mCamera.setParameters(parameters);
        }catch(Exception ex ){
            mCamera.release();

        }

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

    }

    private void hide() {
        // Hide UI first
    }

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.

        }
    };

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar

        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };

    private final Handler mHideHandler = new Handler();
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {

    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance

        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
    public void updateButton(String str) {
        Log.e(TAG, str);
        Button btn = (Button)findViewById(R.id.dummy_button);
        btn.setText(str);
    }
}
