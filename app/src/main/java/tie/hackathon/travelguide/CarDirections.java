package tie.hackathon.travelguide;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.ArrayAdapter;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import Util.Constants;
import Util.Utils;

public class CarDirections extends AppCompatActivity {
    com.google.android.gms.maps.MapFragment mapFragment;
    GoogleMap map;
    private ProgressDialog progressDialog;
    String sorcelat,deslat,sorcelon,deslon,surce,dest,distancetext;
    Integer distance;
    SharedPreferences s ;
    SharedPreferences.Editor e;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_directions);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        s = PreferenceManager.getDefaultSharedPreferences(this);
        e = s.edit();

        sorcelat = s.getString(Constants.SOURCE_CITY_LAT, Constants.DELHI_LAT);
        sorcelon  = s.getString(Constants.SOURCE_CITY_LON, Constants.DELHI_LON);
        deslat = s.getString(Constants.DESTINATION_CITY_LAT,Constants.MUMBAI_LAT);
        deslon  = s.getString(Constants.DESTINATION_CITY_LON,Constants.MUMBAI_LON);

        surce = s.getString(Constants.SOURCE_CITY, "Delhi");
        dest  = s.getString(Constants.DESTINATION_CITY, "MUmbai");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String shareBody = "Lets plan a journey from " + surce + " to " + dest + ". The distace between the two cities is "
                        + distancetext;

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Travel Guide");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Choose"));
            }
        });

        this.mapFragment = (com.google.android.gms.maps.MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        map = mapFragment.getMap();

        ShowMarker(Double.parseDouble(sorcelat),Double.parseDouble(sorcelon),"SOURCE",R.drawable.ic_pin_drop_black_24dp);
        ShowMarker(Double.parseDouble(deslat),Double.parseDouble(deslon),"DESTINATION",R.drawable.ic_pin_drop_black_24dp);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Car Directions");
        new getcitytask().execute();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if(item.getItemId() ==android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }


    public void ShowMarker(Double LocationLat, Double LocationLong, String LocationName, Integer LocationIcon){
        LatLng Coord = new LatLng(LocationLat, LocationLong);

        if(map!=null) {
            map.setMyLocationEnabled(true);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(Coord, 5));

            MarkerOptions abc = new MarkerOptions();
            MarkerOptions x = abc
                    .title(LocationName)
                    .position(Coord)
                    .icon(BitmapDescriptorFactory.fromResource(LocationIcon));
            map.addMarker(x);

        }
    }

    public class getcitytask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progressDialog = new ProgressDialog(CarDirections.this);
            progressDialog.setMessage("Fetching route, Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String uri = "https://maps.googleapis.com/maps/api/directions/json?origin=" +
                        sorcelat + "," + sorcelon + "&destination=" + deslat+ "," + deslon +
                        "&key=AIzaSyBgktirlOODUO9zWD-808D7zycmP7smp-Y&mode=driving\n";
                URL url = new URL(uri);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                String readStream = Utils.readStream(con.getInputStream());
                Log.e("here", readStream + " ");
                return readStream;
            } catch (Exception e) {
                Log.e("here", e.getMessage() + " ");
                e.printStackTrace();
                return null;
            }


        }

        @Override
        protected void onPostExecute(String result) {
            try {
                //Tranform the string into a json object

                final JSONObject json = new JSONObject(result);
                JSONArray routeArray = json.getJSONArray("routes");
                JSONObject routes = routeArray.getJSONObject(0);
                JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
                String encodedString = overviewPolylines.getString("points");
                List<LatLng> list = decodePoly(encodedString);
                Polyline line = map.addPolyline(new PolylineOptions()
                                .addAll(list)
                                .width(12)
                                .color(Color.parseColor("#05b1fb"))//Google maps blue color
                                .geodesic(true)
                );
                distance = routes.getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getInt("value");
                distancetext = routes.getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getString("text");

                for (int z = 0; z < list.size() - 1; z++) {
                    LatLng src = list.get(z);
                    LatLng dest = list.get(z + 1);
                    Polyline line2 = map.addPolyline(new PolylineOptions()
                            .add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude, dest.longitude))
                            .width(2)
                            .color(Color.BLUE).geodesic(true));
                }
                progressDialog.hide();
                LatLng coordinate = new LatLng(Double.parseDouble(sorcelat), Double.parseDouble(sorcelon));
                CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 5);
                map.animateCamera(yourLocation);

            } catch (JSONException e) {
                Log.e("here11", e.getMessage() + " ");

            }
        }

    }


    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

}
