package kr.co.switchnow.switch_now_client.Util;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import kr.co.switchnow.switch_now_client.Activity.ConversationActivity_;
import kr.co.switchnow.switch_now_client.Activity.InteractionActivity;
import kr.co.switchnow.switch_now_client.R;

import static kr.co.switchnow.switch_now_client.R.id.mapView;


/**
 * Created by ceo on 2017-03-24.
 */

public class CustomDialog extends AppCompatActivity implements View.OnClickListener {


    TextView text;
    TextView statusView;
    Button yes_exit;
    Button no_exit;
    Button send;
    Button cancel;
    CircleImageView image;

    EditText messageTo;
    Button get;
    Button do_not_get;
    Dialog dialog = null;
    Activity thisActivity;
    Context mContext;
    String recipient;
    String recipientName;
    String messageToSend;
    String myId;
    String my_name;

    ConversationActivity_ conversationActivity;
    InteractionActivity interactionActivity;

    TextView titleMidPoint;
    ImageView iconMidPoint;
    LocationManager locationManager;
    Criteria criteria;
    Location lastKnownLocation;
    ArrayList<LatLng> markerPoints;
    GoogleMap m_map;
    LatLng myPosition;
    LatLng oppPosition;
    MarkerOptions m_options;
    DownloadTask downloadTask;
    MapView mMapView;

    public void exitDialog(Activity activity, String msg) {

        dialog = new Dialog(activity);


        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_exit);
        text = (TextView) dialog.findViewById(R.id.text_dialog_exit);
        text.setText(msg);

        yes_exit = (Button) dialog.findViewById(R.id.btn_dialog_exit_yes);
        no_exit = (Button) dialog.findViewById(R.id.btn_dialog_exit_no);

        yes_exit.setOnClickListener(this);
        no_exit.setOnClickListener(this);

        thisActivity = activity;
        dialog.show();

    }


    public void warningAlert(Activity activity, String msg) {

        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        thisActivity = activity;
        dialog.show();

    }


    public void GPS_DIALOG(Context context, String msg) {

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dialog_gps_permission);
        text = (TextView) dialog.findViewById(R.id.text_get_location);
        text.setText(msg);
        get = (Button) dialog.findViewById(R.id.btn_dialog_get_yes);
        do_not_get = (Button) dialog.findViewById(R.id.btn_dialog_get_no);

        get.setOnClickListener(this);
        do_not_get.setOnClickListener(this);
        mContext = context;

        dialog.show();
    }


    public void showProfile(Context context, String name, String status, String img_URL) {

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_profile_pop_up);
        text = (TextView) dialog.findViewById(R.id.name_pop_up_profile);
        statusView = (TextView) dialog.findViewById(R.id.status_pop_up_proifle);
        image = (CircleImageView) dialog.findViewById(R.id.profile_image_pop_up);
        yes_exit = (Button) dialog.findViewById(R.id.btn_dialog_confirm);
        text.setText(name);
        statusView.setText(status);
        mContext = context;
        Glide.with(mContext).load(img_URL).into(image);
        yes_exit.setOnClickListener(this);
        mContext = context;
        dialog.show();
    }

    public void sendMessage(Context context, String recevierId, String recevierName, String MyId, String myName) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_message_send_pop_up);
        text = (TextView) dialog.findViewById(R.id.message_pop_up_text);
        messageTo = (EditText) dialog.findViewById(R.id.message_pop_up_content);
        mContext = context;
        recipient = recevierId;
        recipientName = recevierName;
        myId = MyId;
        send = (Button) dialog.findViewById(R.id.btn_dialog_send);
        cancel = (Button) dialog.findViewById(R.id.btn_dialog_cancel);

        my_name = myName;

        send.setOnClickListener(this);
        cancel.setOnClickListener(this);
        conversationActivity = new ConversationActivity_();
        interactionActivity = new InteractionActivity();
        dialog.show();
    }

    public void showMidSpotMap(final Context context, final double m_la, final double m_lo, final double o_la, final double o_lo, final String oppName) {
        dialog = new Dialog(context);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.setContentView(R.layout.custom_dialog_show_midspot);
        titleMidPoint = (TextView) dialog.findViewById(R.id.show_map_text);
        iconMidPoint = (ImageView) dialog.findViewById(R.id.show_map_icon);

        mMapView = (MapView) dialog.findViewById(mapView);
        MapsInitializer.initialize(context);
        mContext = context;

        mMapView.onCreate(dialog.onSaveInstanceState());
        mMapView.onResume();

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                m_map = googleMap;
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
//                m_map.getCameraPosition().target;
                m_options = new MarkerOptions();
                m_map.setMyLocationEnabled(true);
                myPosition = new LatLng(m_la, m_lo);
//                m_map.moveCamera(CameraUpdateFactory.newLatLng(myPosition));

                oppPosition = new LatLng(o_la, o_lo);
//                markerPoints.add(myPosition);
//                markerPoints.add(oppPosition);
                m_options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                m_options.title(oppName);
                m_options.isVisible();

                CameraUpdate center=
                        CameraUpdateFactory.newLatLng(new LatLng(m_la, m_lo));
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
                m_map.moveCamera(center);
                m_map.animateCamera(zoom);

                m_options.position(oppPosition);

                Marker marker = m_map.addMarker(m_options);
                marker.showInfoWindow();
                String url = getDirectionsUrl(myPosition, oppPosition);
                downloadTask = new DownloadTask();
                downloadTask.execute(url);
            }
        });


        dialog.show();
    }


    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        Log.d("TAG","myPosition----------------------------------->"+str_origin);
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        Log.d("TAG","oppPosition---------------------------------->"+str_dest);
        // Sensor enabled
//        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest+ "&";


        // Output format
        String output = "json";
        String API_KEY = "AIzaSyBNogwIGbkDl_WqcyzFpldZSPSAl0skKEU";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters+"mode=transit"+"&key="+API_KEY;
//                +"&key="+API_KEY;
        Log.d("TAG","URL------------------------------------->" + url);
        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("TAG", "Exception while downloading url" + e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("TAG","Background Task------------------->" +e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("TAG","get halfway URL--------------------------------->"+result);
            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }


    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.d("TAG","ROUTES------------------------------------------>"+routes.toString());
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";
            Log.d("TAG","RESUTL-------Parsing results................---->"+result);
            if (result.size() < 1) {
                Toast.makeText(mContext, "No Points", Toast.LENGTH_SHORT).show();
                return;
            }

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    if (j == 0) {    // Get distance from the list
                        distance = (String) point.get("distance");
                        continue;
                    } else if (j == 1) { // Get duration from the list
                        duration = (String) point.get("duration");
                        continue;
                    }




                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);


                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
            }

//            tvDistanceDuration.setText("Distance:"+distance + ", Duration:"+duration);

            // Drawing polyline in the Google Map for the i-th route
            m_map.addPolyline(lineOptions);
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

    public class DirectionsJSONParser {

        /**
         * Receives a JSONObject and returns a list of lists containing latitude and longitude
         */
        public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

            List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
            JSONArray jRoutes = null;
            JSONArray jLegs = null;
            JSONArray jSteps = null;
            JSONObject jDistance = null;
            JSONObject jDuration = null;

            try {

                jRoutes = jObject.getJSONArray("routes");

                /** Traversing all routes */
                for (int i = 0; i < jRoutes.length(); i++) {
                    jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");

                    List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();

                    /** Traversing all legs */
                    for (int j = 0; j < jLegs.length(); j++) {

                        /** Getting distance from the json data */
                        jDistance = ((JSONObject) jLegs.get(j)).getJSONObject("distance");
                        HashMap<String, String> hmDistance = new HashMap<String, String>();
                        hmDistance.put("distance", jDistance.getString("text"));

                        /** Getting duration from the json data */
                        jDuration = ((JSONObject) jLegs.get(j)).getJSONObject("duration");
                        HashMap<String, String> hmDuration = new HashMap<String, String>();
                        hmDuration.put("duration", jDuration.getString("text"));

                        /** Adding distance object to the path */
                        path.add(hmDistance);

                        /** Adding duration object to the path */
                        path.add(hmDuration);

                        jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                        /** Traversing all steps */
                        for (int k = 0; k < jSteps.length(); k++) {
                            String polyline = "";
                            polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                            List<LatLng> list = decodePoly(polyline);

                            /** Traversing all points */
                            for (int l = 0; l < list.size(); l++) {
                                HashMap<String, String> hm = new HashMap<String, String>();
                                hm.put("lat", Double.toString(((LatLng) list.get(l)).latitude));
                                hm.put("lng", Double.toString(((LatLng) list.get(l)).longitude));
                                path.add(hm);
                            }
                        }
                    }
                    routes.add(path);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
            }
            return routes;
        }


    }

    public void onBackPressed(){

        dialog.dismiss();

    }


    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_dialog_exit_yes: {

                thisActivity.finishAffinity();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                thisActivity.startActivity(intent);
                break;
            }

            case R.id.btn_dialog_exit_no: {
                dialog.dismiss();
                break;
            }

            case R.id.btn_dialog_get_yes: {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);

                break;
            }

            case R.id.btn_dialog_get_no: {
                dialog.dismiss();

                break;
            }

            case R.id.btn_dialog_confirm: {
                dialog.dismiss();

                break;
            }

            case R.id.btn_dialog_send: {

                messageToSend = messageTo.getText().toString();

                interactionActivity.HoldMessageToChat(messageToSend, recipient, recipientName, myId, my_name);
                dialog.dismiss();
                break;
            }

            case R.id.btn_dialog_cancel: {


                dialog.dismiss();
                break;
            }

        }

    }

    protected void onResume() {
        super.onResume();

    }
    @Override

    protected void onStop() {


        super.onStop();



    }
    protected void onDestroy() {
        super.onDestroy();
    }

}




