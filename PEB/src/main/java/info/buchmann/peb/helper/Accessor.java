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

import info.buchmann.peb.domain.Song;

/**
 * Created by peter on 8/31/13.
 */
public class Accessor {

    public static String querySRF3() throws MalformedURLException, IOException {
        String result = "";
        HttpURLConnection urlConnection = null;
        Date today = new Date();
        Date nextWeek = new Date(today.getTime()+7*24*3600*1000);
        URL url = new URL("http://www.srf.ch/webservice/songlog/log/channel/dd0fa1ba-4ff6-4e1a-ab74-d7e49057d96f.json?fromDate=2013-08-22T00%3A00%3A00&toDate=2013-09-06T23%3A59%3A59&page.size=1&page.page=1&page.sort=playedDate&page.sort.dir=desc");
        urlConnection = (HttpURLConnection) url.openConnection();
        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
        while (in.available() > 0) {
            result += (char) in.read();
        }
        Log.d("info", in.toString());


        if (urlConnection != null) {
            urlConnection.disconnect();
        }

        return result;
    }

    public static Song parseSRF3Json(String rawJson) {
        String title = "-";
        String name = "-";
        if (rawJson == null || rawJson.equals("")) {
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
            Log.e("debug", e.getStackTrace().toString());
            return null;
        }
        return new Song(title, name);
    }

}
