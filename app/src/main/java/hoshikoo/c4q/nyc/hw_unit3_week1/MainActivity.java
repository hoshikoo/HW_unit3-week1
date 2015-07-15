package hoshikoo.c4q.nyc.hw_unit3_week1;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class MainActivity extends ActionBarActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private CommunityData mCommunityData;
    private ArrayList<String> mCommunityList;
    private HashMap<String, Integer> mCommunityMap;

    TextView boroughName;
    TextView result70;
    TextView result80;
    TextView result90;
    TextView result00;
    TextView result10;
    Button submitButton;

    Spinner spinner;
    String communityChosen;
    int id;
    String bName;
    String population70;
    String population80;
    String population90;
    String population00;
    String population10;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        spinner = (Spinner)findViewById(R.id.spinner);
        boroughName = (TextView)findViewById(R.id.borough_name);
        result70 = (TextView)findViewById(R.id.result_1970);
        result80 = (TextView)findViewById(R.id.result_1980);
        result90 = (TextView)findViewById(R.id.result_1990);
        result00 = (TextView)findViewById(R.id.result_2000);
        result10 = (TextView)findViewById(R.id.result_2010);

        submitButton = (Button)findViewById(R.id.button);


        String url = "https://data.cityofnewyork.us/api/views/xi7c-iiu2/rows.json?accessType=DOWNLOAD";

        if (isNetworkAvailable()) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();

            Call call = client.newCall(request);

            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(Response response) throws IOException {

                    try {
                        final String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {

                            mCommunityMap = getCommunityMap(jsonData);
                            mCommunityList = getCommunityList(jsonData);

                            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                @Override
                                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                                           int arg2, long arg3) {
                                    communityChosen = spinner.getSelectedItem().toString();

                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> arg0) {
                                    // TODO Auto-generated method stub

                                }
                            });

                            id = mCommunityMap.get(communityChosen);

                            mCommunityData = getCommunityDetail(jsonData, id);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    updateDisplay();

                                    submitButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {


                                            bName = mCommunityData.getmBorough();
                                            boroughName.setText(bName);
                                            population70 = Long.toString(mCommunityData.getmPopulation1970());
                                            result70.setText(population70);
                                            population80 = Long.toString(mCommunityData.getmPopulation1980());
                                            result80.setText(population80);
                                            population90 = Long.toString(mCommunityData.getmPopulation1990());
                                            result90.setText(population90);
                                            population00 = Long.toString(mCommunityData.getmPopulation2000());
                                            result00.setText(population00);
                                            population10 = Long.toString(mCommunityData.getmPopulation2010());
                                            result10.setText(population10);


                                        }
                                    });
                                }
                            });

                        } else {
                            alertUserAboutError();
                        }
                    }
                    catch (IOException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    }
                    catch (JSONException e)
                    {Log.e(TAG, "Exception caught: ", e);
                    }

                }
            });
        } else{
            Toast.makeText(this, getString(R.string.network_unavailable_message), Toast.LENGTH_LONG).show();
        }
        Log.d(TAG, "Main UI code is running");





    }

    private void updateDisplay() {
        ArrayAdapter<String> adapter= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, mCommunityList);
        spinner.setAdapter(adapter);

    }



    private HashMap<String, Integer> getCommunityMap(String jsonData) throws JSONException{
        JSONObject comData = new JSONObject(jsonData);
        JSONArray dataArray = comData.getJSONArray("data");

        HashMap<String, Integer> communityNameList = new HashMap<String, Integer>();

        if (dataArray != null) {
            for (int i=0;i<dataArray.length();i++){
                JSONArray eachComArray = dataArray.getJSONArray(i);
                String communityName = eachComArray.getString(10);
                int communityId = eachComArray.getInt(0);
                int arrayid = communityId-1;
                communityNameList.put(communityName,arrayid);
            }
        }

        return communityNameList;
    }


    private ArrayList<String> getCommunityList(String jsonData) throws JSONException{
        JSONObject comData = new JSONObject(jsonData);
        JSONArray dataArray = comData.getJSONArray("data");

        ArrayList<String> communityNameList = new ArrayList<>();

        if (dataArray != null) {
            for (int i=0;i<dataArray.length();i++){
                JSONArray eachComArray = dataArray.getJSONArray(i);
                String communityName = eachComArray.getString(10);

                communityNameList.add(communityName);
            }
        }

        Collections.sort(communityNameList);

        return communityNameList;
    }

    private CommunityData getCommunityDetail(String jsonData, int idNum) throws JSONException {

        JSONObject comData = new JSONObject(jsonData);
        JSONArray dataArray = comData.getJSONArray("data");

        JSONArray eachComArray = dataArray.getJSONArray(idNum);

        CommunityData communitydata = new CommunityData();
        communitydata.setmCommunityName(eachComArray.getString(10));
        communitydata.setmBorough(eachComArray.getString(8));
        communitydata.setmPopulation1970(eachComArray.getLong(11));
        communitydata.setmPopulation1980(eachComArray.getLong(12));
        communitydata.setmPopulation1990(eachComArray.getLong(13));
        communitydata.setmPopulation2000(eachComArray.getLong(14));
        communitydata.setmPopulation2010(eachComArray.getLong(15));

        return communitydata;

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo  networkInfo = manager.getActiveNetworkInfo();
        boolean isAvalable = false;
        if(networkInfo != null && networkInfo.isConnected()){
            isAvalable = true;
        }
        return isAvalable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
