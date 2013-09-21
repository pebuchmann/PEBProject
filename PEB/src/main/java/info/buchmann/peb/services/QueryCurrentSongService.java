package info.buchmann.peb.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.TimerTask;

import info.buchmann.peb.domain.Song;
import info.buchmann.peb.helper.Accessor;

/**
 * Created by peter on 9/3/13.
 */
public class QueryCurrentSongService extends Service {
    public static final String TAG = "QueryCurrentSongService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "QueryCurrentSongService:onStartCommand Service started.");
        new Thread() {
            public void run() {
                String rawJson = Accessor.querySRF3();
                Song song = Accessor.parseSRF3Json(rawJson);
                broadcastUpdate(song);
            }
        }.start();
        return Service.START_NOT_STICKY;
    }

    private void broadcastUpdate(Song song) {
        Intent intent = new Intent("songUpdateBroadcast");
        intent.putExtra("song", song);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
