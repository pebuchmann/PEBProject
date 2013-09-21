package info.buchmann.peb.helper;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
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

    public static synchronized String querySRF3() {
        StringBuffer result = new StringBuffer();
        HttpURLConnection urlConnection = null;


        Date yesterday = new Date(new Date().getTime()-24*3600*1000);
        Date nextWeek = new Date(yesterday.getTime() + 7 * 24 * 3600 * 1000);

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(yesterday);
        int fromYear = calendar.get(Calendar.YEAR);
        int fromMonth = calendar.get(Calendar.MONTH)+1;
        int fromDay =  calendar.get(Calendar.DATE);

        calendar.setTime(nextWeek);
        int toYear = calendar.get(Calendar.YEAR);
        int toMonth = calendar.get(Calendar.MONTH)+1;
        int toDay =  calendar.get(Calendar.DATE);

        String srfUrl = "http://www.srf.ch/webservice/songlog/log/channel/dd0fa1ba-4ff6-4e1a-ab74-d7e49057d96f.json?fromDate="+fromYear+"-"+fromMonth+"-"+fromDay+"T00%3A00%3A00&toDate="+toYear+"-"+toMonth+"-"+toDay+"T23%3A59%3A59&page.size=1&page.page=1&page.sort=playedDate&page.sort.dir=desc";
        try {
            URL url = new URL(srfUrl);


            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(25000);
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            while (in.available() > 0) {
                result.append((char) in.read());
            }
            Log.d(TAG, "-----------------------------------------");
            Log.d(TAG, "parsed InputStream");
            Log.d(TAG, result.toString());
            Log.d(TAG, "------------------------------------------");
        } catch (Exception e) {
            Log.e(TAG, "---------------------------------------------------");
            Log.e(TAG, "Exception",e);
            Log.e(TAG, "---------------------------------------------------");

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return result.toString();
    }

    public static Song parseSRF3Json(String rawJson) {
        String title = "-";
        String name = "-";
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

            if (songlogArray != null && songlogArray.length() > 0) {
                lastEntry = songlogArray.getJSONObject(0);
                JSONObject lastSong = lastEntry.getJSONObject("Song");
                title = lastSong.getString("title");
                JSONObject artist = lastSong.getJSONObject("Artist");
                name = artist.getString("name");
            }

        } catch (JSONException e) {
            Log.e(TAG, "parsing JSON went south",e);
            return null;
        }
        return new Song(title, name);
    }

}
