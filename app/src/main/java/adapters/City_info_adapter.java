package adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import tie.hackathon.travelguide.R;

/**
 * Created by swati on 9/10/15.
 */


public class City_info_adapter extends BaseAdapter {

    Context context;
    JSONArray FeedItems;
    private static LayoutInflater inflater = null;

    public City_info_adapter(Context context, JSONArray FeedItems) {
        this.context = context;
        this.FeedItems = FeedItems;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return FeedItems.length();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        try {
            return FeedItems.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.city_infoitem, null);

        TextView Title = (TextView) vi.findViewById(R.id.item_name);
        TextView Description = (TextView) vi.findViewById(R.id.item_address);
        Button onmap = (Button) vi.findViewById(R.id.viewonmap);


        try {
            Title.setText(FeedItems.getJSONObject(position).getString("name"));
            Description.setText(FeedItems.getJSONObject(position).getString("address"));
          } catch (JSONException e) {
            e.printStackTrace();
            Log.e("eroro",e.getMessage()+" ");
        }


        onmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent browserIntent = null;
                try {
                    browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps?q=" +
                            FeedItems.getJSONObject(position).getString("name") +
                            "+(name)+@" +
                            FeedItems.getJSONObject(position).getString("lat") +
                            "," +
                                    FeedItems.getJSONObject(position).getString("lng")
                    ));
                    context.startActivity(browserIntent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        });
        return vi;
    }

}