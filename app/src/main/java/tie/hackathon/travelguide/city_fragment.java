package tie.hackathon.travelguide;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import Util.Utils;
import adapters.Cities_adapter;


public class city_fragment extends Fragment {


    List<String> id = new ArrayList<>();
    List<String> names = new ArrayList<>();
    static Activity activity;
    ProgressBar pb;

    public city_fragment() {
        // Required empty public constructor
    }
    ListView lv;
    LinearLayout vehicle, acc, shop, city, check;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View v = inflater.inflate(R.layout.content_jokes, container, false);

        lv = (ListView) v.findViewById(R.id.music_list);
        pb = (ProgressBar) v.findViewById(R.id.pb);

        try {
            new getcitytask().execute();

        } catch (Exception e) {
            AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
            alertDialog.setTitle("Can't connect.");
            alertDialog.setMessage("We cannot connect to the internet right now. Please try again later. Exception e: " + e.toString());
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            Log.e("YouTube:", "Cannot fetch " + e.toString());
        }




        return v;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }
    public class getcitytask extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... params) {
            //this is where you should write your authentication code
            // or call external service
            // following try-catch just simulates network access


            try {
                String uri = "http://csinsit.org/prabhakar/tie/all-cities.php";
                URL url = new URL(uri);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                String readStream = Utils.readStream(con.getInputStream());
                Log.e("here",readStream+" ");
                return readStream;

//                return readStream;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }




        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject ob  = new JSONObject(result);
                JSONArray ar = ob.getJSONArray("cities");
                pb.setVisibility(View.GONE);
                lv.setAdapter(new Cities_adapter(activity,ar));

            } catch (JSONException e1) {
                e1.printStackTrace();
                Log.e("heer",e1.getMessage()+" ");
            }
        }

    }


}
