package tie.hackathon.travelguide;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lucasr.twowayview.TwoWayView;

import Util.Constants;
import Util.Utils;
import adapters.Videos_adapter;
import adapters.mood_adapter;


import java.net.HttpURLConnection;
import java.net.URL;

public class Videos extends AppCompatActivity {



    SharedPreferences s ;
    SharedPreferences.Editor e;
    TwoWayView lv2;
    ProgressBar pb;
    String playid;
    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        playid = Constants.PLAY_LIST_HAPPY;
        pb = (ProgressBar) findViewById(R.id.pb);
        s = PreferenceManager.getDefaultSharedPreferences(this);
        e = s.edit();
        Integer moods = Integer.parseInt(s.getString(Constants.CURRENT_SCORE,"2"));
        if(moods>10)
            playid = Constants.PLAY_LIST_VERYHAPPY;
        else if(moods>2)
            playid = Constants.PLAY_LIST_HAPPY;
        else if(moods>-2)
            playid = Constants.PLAY_LIST_NORMAL;
        else if(moods>-10)
            playid = Constants.PLAY_LIST_SAD;
        else
            playid = Constants.PLAY_LIST_VERYSAD;

        lv = (ListView) findViewById(R.id.music_list);
        lv2 = (TwoWayView) findViewById(R.id.lvmoods);
        try {
            new Video_RetrieveFeed().execute();


        } catch (Exception e) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Can't connect.");
            alertDialog.setMessage("We cannot connect to the internet right now. Please try again later.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            Log.e("YouTube:", "Cannot fetch " + e.toString());
        }

        lv2.setAdapter(new mood_adapter(this));
        setTitle("Videos");

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                switch(i){

                    case 0 : playid = Constants.PLAY_LIST_VERYHAPPY;break;
                    case 1 : playid = Constants.PLAY_LIST_HAPPY;break;
                    case 2 : playid = Constants.PLAY_LIST_NORMAL;break;
                    case 3 : playid = Constants.PLAY_LIST_SAD;break;
                    case 4 : playid = Constants.PLAY_LIST_VERYSAD;break;
                }

                try {
                    new Video_RetrieveFeed().execute();


                } catch (Exception e) {
                    AlertDialog alertDialog = new AlertDialog.Builder(Videos.this).create();
                    alertDialog.setTitle("Can't connect.");
                    alertDialog.setMessage("We cannot connect to the internet right now. Please try again later.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    Log.e("YouTube:", "Cannot fetch " + e.toString());
                }

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if(item.getItemId() ==android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }

    public class Video_RetrieveFeed extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... urls) {
            try {
                String uri = "https://www.googleapis.com/youtube/v3/playlists?part=snippet&channelId=" +
                        playid +
                        "&key=AIzaSyBgktirlOODUO9zWD-808D7zycmP7smp-Y";
                URL url = new URL(uri);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                String readStream = Utils.readStream(con.getInputStream());
                Log.e("out",readStream+" ");
                return readStream;
            } catch (Exception e) {
                Log.e("excepit",e.getMessage()+" ");
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String Result) {
            try {

                JSONObject YTFeed = new JSONObject(Result);
                JSONArray YTFeedItems = YTFeed.getJSONArray("items");
             lv.setAdapter(new Videos_adapter(Videos.this , YTFeedItems) );
                pb.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("here","reeefd" + e.getMessage() + Result);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


}
