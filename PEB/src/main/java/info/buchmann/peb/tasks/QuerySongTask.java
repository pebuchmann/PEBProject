package info.buchmann.peb.tasks;

import android.os.AsyncTask;
import android.util.Log;


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
