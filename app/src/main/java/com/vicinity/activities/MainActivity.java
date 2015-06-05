package com.vicinity.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.vicinity.utils.GPSTracker;
import com.vicinity.utils.HttpClientFactory;
import com.vicinity.utils.MyInfoAdapter;
import com.vicinity.utils.User;
import com.vicinity.utils.Utils;
import com.vicinity.utils.XMlParser;
import com.vicinity.R;


public class MainActivity extends FragmentActivity {

    private double lat=0;
    private double lng=0;
    private static final HttpClient httpClient = HttpClientFactory.getThreadSafeClient();
    private GPSTracker gps;
    private String deviceId;
    private Marker myMaker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getLocation();

        GoogleMap map = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();

        map.setInfoWindowAdapter(new MyInfoAdapter(getLayoutInflater()));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 13));
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        myMaker = map.addMarker(new MarkerOptions()
                .title("You are here.")
                .snippet("")
                .position(new LatLng(lat, lng))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                ));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
//		switch(item.getItemId()){
//		case R.id.action_settings:
//			startActivity(new Intent(this, Prefs.class));
//			return true;
//		}
        return false;

    }
    public void getNearby(View v){
        getLocation();

        EditText whatsup = (EditText)findViewById(R.id.whatsup);
        TelephonyManager tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        deviceId = tm.getDeviceId();
        User user = new User();
        user.setName(deviceId);
        user.setWhatsup(whatsup.getText().toString());
        user.setLat(String.valueOf(lat));
        user.setLng(String.valueOf(lng));

        PostLocationTask  plt = new PostLocationTask();
        plt.execute(user);
        try {
            plt.get();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //hide input method
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public class PostLocationTask extends AsyncTask<User, Void, List<User>> {

        @Override
        protected List<User> doInBackground(User... users) {

            XMlParser parser = new XMlParser();
            User user = users[0];
            List<User> userList = null;
            HttpPost httpPost = new HttpPost(Utils.BASEURL+"program2server");

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("name", user.getName()));
            nameValuePairs.add(new BasicNameValuePair("lat", user.getLat()));
            nameValuePairs.add(new BasicNameValuePair("lng", user.getLng()));
            nameValuePairs.add(new BasicNameValuePair("whatsup", user.getWhatsup()));

            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpClient.execute(httpPost);
                if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
                {
                    System.out.println("OK");
                }
                else System.out.println(response.getStatusLine().getStatusCode());

                InputStream in = response.getEntity().getContent();
                userList = parser.parse(in);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }

            return userList;
        }
        protected void onPostExecute(List<User> users){

            GoogleMap map = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
            if(users==null){
                Toast.makeText(getApplicationContext(), "No user recieved!", Toast.LENGTH_LONG).show();
                return;
            }
            for(User u:users){
                int d = Double.valueOf(u.getDistance()).intValue();
                if(u.getName().equals(deviceId)){
                    myMaker.setSnippet(u.getWhatsup());
                }else{
                    map.addMarker(new MarkerOptions()
                            .title(u.getName())
                            .snippet(d + " meters away.\n" + u.getWhatsup())
                            .position(new LatLng(Double.valueOf(u.getLat()), Double.valueOf(u.getLng())))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                }
            }

        }

    }

    public void getLocation(){
        gps = new GPSTracker(MainActivity.this);

        // check if GPS enabled
        if(gps.canGetLocation()){

            lat = gps.getLatitude();
            lng = gps.getLongitude();

            // \n is for new line
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + lat + "\nLong: " + lng, Toast.LENGTH_LONG).show();
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }

    }

    //	public Address getAddress(double lat, double lng){
//
//		    Geocoder geo = new Geocoder(MainActivity.this, Locale.getDefault());
//		    List<Address> addresses = null;
//			try {
//				addresses = geo.getFromLocation(lat,lng, 1);
//				if (addresses.isEmpty()) {
//			        Log.i("location", "addressed is Empty");
//			    }
//			    else {
//			        if (addresses.size() > 0) {
//			            Log.i("location", addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
//			        }
//			    }
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//		   return addresses.get(0);
//	}
    @Override
    protected void onStart(){
        super.onStart();


    }
    @Override
    protected void onStop(){
        super.onStop();
    }
    protected void onResume(){
        super.onResume();

    }
}
