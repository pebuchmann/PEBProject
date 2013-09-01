package info.buchmann.peb.tasks;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.getpebble.android.kit.PebbleKit.*;
import com.getpebble.android.kit.util.PebbleDictionary;

import org.json.JSONArray;
import org.json.JSONObject;

import info.buchmann.peb.helper.Accessor;


/**
 * Created by peter on 8/31/13.
 */
public class QuerySongTask extends AsyncTask<String, Void, String>{

    @Override
    protected String doInBackground(String... params) {
        Log.d("info", "IN Background");
        /*String result = Accessor.querySRF3();

*/

        return "";
    }

    protected void onPostExecute(Long result) {
        //showDialog("Downloaded " + result + " bytes");
    }


}
