package info.buchmann.peb.helper;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import info.buchmann.peb.domain.Song;

/**
 * Created by peter on 8/31/13.
 */
public class Accessor {
    private static String TAG = "peb:Accessor";

    public static Song querySRF3Song(int position) {
        String songJson = querySRF3();
        if (songJson != null && songJson.length() > 0) {

            return parseSRF3Json(songJson, position);
        }


        return null;
    }

    public static synchronized String querySRF3() {
        Date yesterday = new Date(new Date().getTime() - 24 * 3600 * 1000);
        Date nextWeek = new Date(yesterday.getTime() + 7 * 24 * 3600 * 1000);

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(yesterday);
        int fromYear = calendar.get(Calendar.YEAR);
        int fromMonth = calendar.get(Calendar.MONTH) + 1;
        int fromDay = calendar.get(Calendar.DATE);

        calendar.setTime(nextWeek);
        int toYear = calendar.get(Calendar.YEAR);
        int toMonth = calendar.get(Calendar.MONTH) + 1;
        int toDay = calendar.get(Calendar.DATE);

        String srfUrl = "http://www.srf.ch/webservice/songlog/log/channel/dd0fa1ba-4ff6-4e1a-ab74-d7e49057d96f.json?fromDate=" + fromYear + "-" + fromMonth + "-" + fromDay + "T00%3A00%3A00&toDate=" + toYear + "-" + toMonth + "-" + toDay + "T23%3A59%3A59&page.size=3&page.page=1&page.sort=playedDate&page.sort.dir=desc";
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(srfUrl);
        HttpResponse response;
        try {
            response = httpclient.execute(httpget);
            //Log.i(TAG,response.getStatusLine().toString());
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                String result = convertStreamToString(instream);
                instream.close();
                return result;
            }
        } catch (ClientProtocolException e) {
            Log.e(TAG, e.getStackTrace().toString());
        } catch (IOException e) {
            Log.e(TAG, e.getStackTrace().toString());
        }
        return null;

    }

    public static Song parseSRF3Json(String rawJson, int position) {
        String title = "-";
        String name = "-";
        String album = "-";
        String playedDate = "-";
        boolean isPlaying = false;

        if (rawJson == null || rawJson.equals("")) {
            Log.d(TAG, "-----------------------------------------");
            Log.d(TAG, "parseSRF3Json");
            Log.d(TAG, rawJson);
            Log.d(TAG, "------------------------------------------");
            return null;
        }


        try {
            JSONObject root = new JSONObject(rawJson);
            JSONArray songlogArray = root.getJSONArray("Songlog");
            JSONObject lastEntry = null;

            if (songlogArray != null && songlogArray.length() >= position + 1) {
                lastEntry = songlogArray.getJSONObject(position);
                playedDate = lastEntry.getString("playedDate");
                isPlaying = lastEntry.getBoolean("isPlaying");

                JSONObject lastSong = lastEntry.getJSONObject("Song");
                title = lastSong.getString("title");

                JSONObject artist = lastSong.getJSONObject("Artist");
                name = artist.getString("name");

            }

        } catch (JSONException e) {
            Log.e(TAG, "parsing JSON went south", e);
            return null;
        }
        return new Song(title, name, playedDate, isPlaying);
    }

    private static String convertStreamToString(InputStream is) {


        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            Log.e(TAG, e.getStackTrace().toString());
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e(TAG, e.getStackTrace().toString());
            }
        }
        return sb.toString();
    }

    public static String connect(String url) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url);
        HttpResponse response;
        try {
            response = httpclient.execute(httpget);
            //Log.i(TAG,response.getStatusLine().toString());
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                String result = convertStreamToString(instream);
                instream.close();
                return result;
            }
        } catch (ClientProtocolException e) {
            Log.e(TAG, e.getStackTrace().toString());
        } catch (IOException e) {
            Log.e(TAG, e.getStackTrace().toString());
        }
        return null;
    }

}
