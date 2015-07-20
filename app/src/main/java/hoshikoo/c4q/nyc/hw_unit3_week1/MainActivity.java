package hoshikoo.c4q.nyc.hw_unit3_week1;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
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


    Spinner spinner;
    String communityChosen;
    int id;
    String bName;
    String population70;
    String population80;
    String population90;
    String population00;
    String population10;

    JSONObject comData;
    String url;

    String jsonData;

    private Menu optionsMenu;

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

        this.setRefreshActionButtonState(true);


//        url = "https://data.cityofnewyork.us/api/views/xi7c-iiu2/rows.json?accessType=DOWNLOAD";

        if (isNetworkAvailable()) {

//            updateDisplay();

            new AsyncLoading().execute();
            new AsyncLoading2().execute();



      }
        else{

                alertUserAboutError();

        }
        Log.d(TAG, "Main UI code is running");

    }



    private class AsyncLoading extends AsyncTask<Void, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            try {

                mCommunityList = new DataGetter().getCommunityList();

                return mCommunityList;

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final ArrayList<String>mCommunityList) {

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, mCommunityList);
            spinner.setAdapter(adapter);
        }


    }



    private class AsyncLoading2 extends AsyncTask<Void, Void, HashMap<String, Integer>> {

        @Override
        protected HashMap<String, Integer> doInBackground(Void... params) {


            try {
                mCommunityMap = new DataGetter().getCommunityMap();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return mCommunityMap;

        }

        @Override
        protected void onPostExecute(final HashMap<String, Integer> mCommunitymap) {
           spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
               public void onItemSelected(AdapterView<?> arg0, View arg1,
                                          int arg2, long arg3) {
                   communityChosen = spinner.getSelectedItem().toString();

                   Toast.makeText(getBaseContext(), communityChosen,
                           Toast.LENGTH_LONG).show();

                   showNotification();

                   id = mCommunityMap.get(communityChosen);

//                   int idForList = id - 1;
//                    String strI = Integer.toString(id-1);
//                    Toast.makeText(getBaseContext(), strI,
//                            Toast.LENGTH_LONG).show();
                   new AsyncLoading3().execute();
                   
               }

               @Override
               public void onNothingSelected(AdapterView<?> parent) {

               }
           });

        }


    }


    private class AsyncLoading3 extends AsyncTask<Void, Void, CommunityData> {

        @Override
        protected CommunityData doInBackground(Void... params) {

            int idForList = id-1;

            try {
                mCommunityData=new DataGetter().getCommunityDetail(idForList);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  mCommunityData;

        }

        @Override
        protected void onPostExecute(CommunityData  mCommunityDate) {
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
        this.optionsMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuRefresh:

                finish();
                startActivity(getIntent());

                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void setRefreshActionButtonState(final boolean refreshing) {
        if (optionsMenu != null) {
            final MenuItem refreshItem = optionsMenu
                    .findItem(R.id.menuRefresh);
            if (refreshItem != null) {
                if (refreshing) {
                    refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
                } else {
                    refreshItem.setActionView(null);
                }
            }
        }
    }

    public static final int NOTIFICATION_ID = 1234;

    private void showNotification() {

        updateNotification("You checked the info about ...", communityChosen);
    }

    private void updateNotification(String titletext, String contentText){

//        String titletext ="Title";
//        contentText = communityChosen;

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        //this here is mainactivity
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setAutoCancel(true);

        builder.setContentTitle(titletext);
        builder.setSmallIcon(R.drawable.ic_action_editor_insert_comment);

        builder.setContentText(contentText);


        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);


        Notification notification = builder.build();
        notificationManager.notify(NOTIFICATION_ID, notification);


    }
}
