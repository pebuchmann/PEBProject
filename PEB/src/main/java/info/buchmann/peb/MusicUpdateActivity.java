package info.buchmann.peb;

import info.buchmann.peb.domain.Song;
import info.buchmann.peb.services.QuerySongService;
import info.buchmann.peb.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MusicUpdateActivity extends Activity {
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
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;
    private SongUpdateBroadcastReceiver songUpdateBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        final Button dummyButton = (Button) findViewById(R.id.dummy_button);
        dummyButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
               /*Log.d("Info","Dummy Button clicked" );
               QuerySongTask qst = new QuerySongTask();
               qst.execute("bla");*/
                Toast toast = Toast.makeText(getApplicationContext(), "Starting Download", Toast.LENGTH_SHORT);
                toast.show();
                //sendToPebble();

            }

        });

        final Button serviceButton = (Button) findViewById(R.id.startService_button);
        serviceButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
               /*Log.d("Info","Dummy Button clicked" );
               QuerySongTask qst = new QuerySongTask();
               qst.execute("bla");*/
                Log.d("Info", "startService Button clicked");
                Toast toast = Toast.makeText(getApplicationContext(), "Starting Service", Toast.LENGTH_SHORT);
                toast.show();
                startQuerySongService();
            }

        });

        this.songUpdateBroadcastReceiver = new SongUpdateBroadcastReceiver(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(this.songUpdateBroadcastReceiver, new IntentFilter("songUpdateBroadcast"));
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(this.songUpdateBroadcastReceiver);
    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private void startQuerySongService() {
        Intent i = new Intent(this, QuerySongService.class);


        this.startService(i);
    }

    private void sendToPebble(Song song) {

        final Intent i = new Intent("com.getpebble.action.SEND_NOTIFICATION");
        String bodyText = "Some Error occurrred";
        if(song != null){
            bodyText = song.getArtist() + ": " + song.getTitle();
        }
        final Map data = new HashMap();
        data.put("title", "On SRF3");
        data.put("body",bodyText );
        final JSONObject jsonData = new JSONObject(data);
        final String notificationData = new JSONArray().put(jsonData).toString();

        i.putExtra("messageType", "PEBBLE_ALERT");
        i.putExtra("sender", "PEB SRF3");
        i.putExtra("notificationData", notificationData);


        sendBroadcast(i);
    }

    private class SongUpdateBroadcastReceiver extends BroadcastReceiver {
        Activity activity;

        public SongUpdateBroadcastReceiver(Activity pActivity) {
            activity = pActivity;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("debug", "callback");
            Song song = (Song) intent.getExtras().get("song");
            String text = "Some error occurred.";
            if (song != null) {
                text = song.getTitle() + " from " + song.getArtist();
            }
            //Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
            sendToPebble(song);
        }
    }
}
