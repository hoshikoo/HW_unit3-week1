package hoshikoo.c4q.nyc.hw_unit3_week1;

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
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Hoshiko on 7/19/15.
 */
public class DataGetter {

    public static final String TAG = "DataGetter";
    public static final String DATA_API = "https://data.cityofnewyork.us/api/views/xi7c-iiu2/rows.json?accessType=DOWNLOAD";


    JSONObject comData;

    String jsonData;




    public String getJasonString() throws IOException{
        String result = "";
        URL url = new URL(DATA_API);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setConnectTimeout(0);
        connection.setReadTimeout(0);

        InputStream inputStream = connection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder builder = new StringBuilder();
        String line = "";
        while ((line = bufferedReader.readLine()) != null) {
            builder.append(line + "\n");
        }

        result = builder.toString();

        return result;
    }


    public HashMap<String, Integer> getCommunityMap() throws JSONException, IOException {
        jsonData = getJasonString();
        comData = new JSONObject(jsonData);
        JSONArray dataArray = comData.getJSONArray("data");

        HashMap<String, Integer> communityNameMap = new HashMap<String, Integer>();

        if (dataArray != null) {
            for (int i=0;i<dataArray.length();i++){
                JSONArray eachComArray = dataArray.getJSONArray(i);
                String communityName = eachComArray.getString(10);
                int communityId = eachComArray.getInt(0);

                communityNameMap.put(communityName,communityId);
            }
        }

        return communityNameMap;
    }

//    public Integer getid(String communityChosen) throws JSONException{
//        JSONArray dataArray = comData.getJSONArray("data");
//
//        if (dataArray != null) {
//            for (int i=0;i<dataArray.length();i++){
//                JSONArray eachComArray = dataArray.getJSONArray(i);
//                if (communityChosen.equals(eachComArray.getString(10))){
//                    int communityId = eachComArray.getInt(0);
//                    return communityId;
//                }
//
//            }
//        }
//
//        return null;
//    }


    public ArrayList<String> getCommunityList() throws JSONException, IOException {

        jsonData = getJasonString();
        comData = new JSONObject(jsonData);
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

    public CommunityData getCommunityDetail(int idNum) throws JSONException, IOException {
        jsonData = getJasonString();
        comData = new JSONObject(jsonData);

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
}
