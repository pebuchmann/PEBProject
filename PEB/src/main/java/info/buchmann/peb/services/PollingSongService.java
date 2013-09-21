package info.buchmann.peb.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import info.buchmann.peb.domain.Song;
import info.buchmann.peb.helper.Accessor;

/**
 * Created by peter on 8/31/13.
 */
public class PollingSongService extends Service {
    private static String TAG = "PollingSongService";
    private boolean IS_RUNNING = false;
    private Context context;
    private int counter = 0;
    private Timer timer = new Timer();
    private Song lastSong = new Song("","");

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startService();
    }

    private void startService() {
        timer.scheduleAtFixedRate(new MainTask(), 0, 35000);
    }

    private class MainTask extends TimerTask {
        public void run() {
            String json = null;
            try {
                json = Accessor.querySRF3();
            } catch (Exception e) {
                Log.e(TAG, "inside Timer", e);
                return;
            }
            Song currentSong = Accessor.parseSRF3Json(json);
            if(lastSong!= null && !lastSong.equals(currentSong)){
                lastSong = currentSong;
                broadcastUpdate(currentSong);
            }else if(lastSong == null){
                lastSong = currentSong;
                broadcastUpdate(currentSong);
            }

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "PollingSongService:onStartCommand Service started.");

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();

    }


    private void broadcastUpdate(Song song) {
        Intent intent = new Intent("songUpdateBroadcast");
        intent.putExtra("counter", ++counter);
        intent.putExtra("song",song);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendToPebble(String artist, String song) {
        final Intent i = new Intent("com.getpebble.action.SEND_NOTIFICATION");

        final Map data = new HashMap();
        data.put("title", "Now On SRF3");
        data.put("body", artist + ": " + song);
        final JSONObject jsonData = new JSONObject(data);
        final String notificationData = new JSONArray().put(jsonData).toString();

        i.putExtra("messageType", "PEBBLE_ALERT");
        i.putExtra("sender", "PEB SRF3");
        i.putExtra("notificationData", notificationData);


        sendBroadcast(i);
    }
}
