package info.buchmann.peb.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;

import info.buchmann.peb.domain.Song;
import info.buchmann.peb.helper.Accessor;

/**
 * Created by peter on 9/21/13.
 */


public class MediaEventReceiver extends BroadcastReceiver {
    private final String TAG = "peb:MediaEventReceiver";
    private static int pointer = 0;

    @Override
    public void onReceive(final Context context, Intent intent) {

        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {

            KeyEvent event = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            Message msg = Message.obtain();
            Song song = null;
            if (event.getAction() == KeyEvent.ACTION_UP) {
                int code = event.getKeyCode();
                switch (code) {
                    case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                        //msg.obj = ClementineMessage.getMessage(MsgType.PLAYPAUSE);
                        pointer = 0;
                        new Thread(new SongQueryThread(context,pointer)).start();

                        Log.d(TAG, "+++++++++++++++++++++++++++++++++++PAUSE " + pointer);
                        break;
                    case KeyEvent.KEYCODE_MEDIA_NEXT:
                        if (pointer > 0) {
                            pointer--;
                        }

                        new Thread(new SongQueryThread(context, pointer)).start();

                        Log.d(TAG, "+++++++++++++++++++++++++++++++++++NEXT " + pointer);
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                        if (pointer < 2) {
                            pointer++;
                        }
                        new Thread(new SongQueryThread(context,pointer)).start();
                        Log.d(TAG, "+++++++++++++++++++++++++++++++++++PREVIOUS " + pointer);
                        break;
                    case KeyEvent.KEYCODE_VOLUME_DOWN:
                        // msg.obj = ClementineMessageFactory.buildVolumeMessage(App.mClementine.getVolume() - 10);
                        break;
                    case KeyEvent.KEYCODE_VOLUME_UP:
                        // msg.obj = ClementineMessageFactory.buildVolumeMessage(App.mClementine.getVolume() + 10);
                        break;
                    default:
                        msg = null;
                        break;
                }
                Log.d(TAG, "GOTCHA!!!!!!!!!!!!!!!!!");
                //Intent musicUpdateIntent = new Intent("musicUpdateBroadcast");
                //context.sendBroadcast(intent);

            }

        }
    }

    private class SongQueryThread implements Runnable {
        private Context context;
        private int position;

        public SongQueryThread(Context pContext, int pPosition) {
            context = pContext;
            position = pPosition;
        }

        @Override

        public void run() {
            //String rawJson = Accessor.querySRF3();
            Song song = Accessor.querySRF3Song(pointer);//Accessor.parseSRF3Json(rawJson,0);
            sendSongToPebbleMusicControl(song, context);

        }

        public void sendSongToPebbleMusicControl(Song song, Context context) {

            Intent i = new Intent("com.getpebble.action.NOW_PLAYING");
            if (song != null) {
                i.putExtra("artist", song.getArtist());
                i.putExtra("album", song.formatPlayStatus());
                i.putExtra("track", song.getTitle());
            } else {
                i.putExtra("album", "An error occurred.");
            }
            context.sendBroadcast(i);
        }

    }


    public void sendSongToPebbleMusicControl(Song song, Context context) {

        Intent i = new Intent("com.getpebble.action.NOW_PLAYING");
        if (song != null) {
            i.putExtra("artist", song.getArtist());
            i.putExtra("album", song.getPlayedDate());
            i.putExtra("track", song.getTitle());
        } else {
            i.putExtra("album", "An error occurred.");
        }
        context.sendBroadcast(i);
    }


}
