package com.example.kjc600.knowyourgovernment;

/*
    Download all governors' information form online.
*/

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DownloadAsyncTask extends AsyncTask<Void, Void, String>
{
    private MainActivity mainActivity;
    private static final String DATA_URL =
            "https://www.googleapis.com/civicinfo/v2/representatives?key=AIzaSyAv_gYkyRmmwLSGR6xiVVRrZk6U3EXlxIE&address=";
    private String updateURL;
    private static final String TAG = "DownloadAsyncTask";

    public DownloadAsyncTask(MainActivity ma, String location)
    {
        Log.d(TAG, "DownloadAsyncTask: " + location);
        mainActivity = ma;
        updateURL = DATA_URL + location;
        Log.d(TAG, "DownloadAsyncTask: " + updateURL);
    }

    @Override
    protected void onPostExecute(String s)
    {
        Log.d(TAG, "onPostExecute: success");
        mainActivity.updateLocation(parseLocation(s));
        mainActivity.updateList(parseGovernor(s));
    }

    @Override
    protected String doInBackground(Void... voids)
    {
        Uri dataUri = Uri.parse(updateURL);
        String urlToUse = dataUri.toString();
        StringBuilder sb = new StringBuilder();

        try
        {
            URL url = new URL(urlToUse);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while((line = reader.readLine()) != null)
            {
                sb.append(line).append("\n");
            }
        }
        catch(Exception e)
        {
            return null;
        }

        return sb.toString();
    }

    private String parseLocation(String s)
    {
        try
        {
            //get location
            JSONObject jObjMain = new JSONObject(s);
            JSONObject normalizedInput = jObjMain.getJSONObject("normalizedInput");
            String city = normalizedInput.getString("city");
            String state = normalizedInput.getString("state");
            String zip = normalizedInput.getString("zip");
            return city + ", " + state + " " + zip;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private ArrayList<GovernorInfo> parseGovernor(String s)
    {
        ArrayList<GovernorInfo> governorInfoList = new ArrayList<>();
        try
        {
            JSONObject jObjMain = new JSONObject(s);
            ArrayList<String> positionArray = new ArrayList<>();

            JSONArray offices = jObjMain.getJSONArray("offices");
            for(int i = 0; i < offices.length(); i++)
            {
                JSONObject o = offices.getJSONObject(i);
                String position = o.getString("name");
                JSONArray officialIndices = o.getJSONArray("officialIndices");
                for(int j = 0; j < officialIndices.length(); j++)
                {
                    positionArray.add(position);
                }
            }

            JSONArray officials = jObjMain.getJSONArray("officials");
            for(int i = 0; i < officials.length(); i++)
            {
                JSONObject person = officials.getJSONObject(i);

                String governorName = person.getString("name");

                String position = positionArray.get(i);

                String party = "Unknown";
                if(person.has("party"))
                    party = person.getString("party");

                ArrayList<String> addressArray = new ArrayList<>();
                if(person.has("address"))
                {
                    JSONArray address = person.getJSONArray("address");
                    for(int k = 0; k < address.length(); k++)
                    {
                        JSONObject addressObj = address.getJSONObject(k);
                        String line1 = " ";
                        String line2 = " ";
                        String city = " ";
                        String state = " ";
                        String zip = " ";
                        StringBuilder addrs = new StringBuilder();
                        if(addressObj.has("line1"))
                        {
                            line1 = addressObj.getString("line1");
                            addrs.append(line1);
                            addrs.append("\n");
                        }

                        if(addressObj.has("line2"))
                        {
                            line2 = addressObj.getString("line2");
                            addrs.append(line2);
                            addrs.append("\n");
                        }
                        if(addressObj.has("city"))
                        {
                            city = addressObj.getString("city");
                            addrs.append(city);
                            addrs.append(", ");
                        }
                        if(addressObj.has("state"))
                        {
                            state = addressObj.getString("state");
                            addrs.append(state);
                            addrs.append(", ");
                        }
                        if(addressObj.has("zip"))
                        {
                            zip = addressObj.getString("zip");
                            addrs.append(zip);
                        }
                        addressArray.add(addrs.toString());
                    }
                }

                ArrayList<String> phoneArray = new ArrayList<>();
                if(person.has("phones"))
                {
                    JSONArray phonesJSONArray = person.getJSONArray("phones");
                    for(int k = 0; k < phonesJSONArray.length(); k++)
                    {
                        phoneArray.add(phonesJSONArray.getString(k));
                    }
                }

                ArrayList<String> websites = new ArrayList<>();
                if(person.has("urls"))
                {
                    JSONArray urlJSONArray = person.getJSONArray("urls");
                    for(int k = 0; k < urlJSONArray.length(); k++)
                    {
                        websites.add(urlJSONArray.getString(k));
                    }
                }

                ArrayList<String> emails = new ArrayList<>();
                if(person.has("emails"))
                {
                    JSONArray emailsJSONArray = person.getJSONArray("emails");
                    for(int k = 0; k < emailsJSONArray.length(); k++)
                    {
                        emails.add(emailsJSONArray.getString(k));
                    }
                }

                String photo = "";
                if(person.has("photoUrl"))
                    photo = person.getString("photoUrl");

                String twitter= "";
                String facebook = "";
                String googlePlus = "";
                String youtube = "";
                if(person.has("channels"))
                {
                    JSONArray channelsArray = person.getJSONArray("channels");
                    for(int k = 0; k < channelsArray.length(); k++)
                    {
                        JSONObject media = channelsArray.getJSONObject(k);
                        String type = media.getString("type");

                        if(type.equals("Facebook"))
                            facebook = media.getString("id");
                        if(type.equals("Twitter"))
                            twitter = media.getString("id");
                        if(type.equals("YouTube"))
                            youtube = media.getString("id");
                        if(type.equals("GooglePlus"))
                            googlePlus = media.getString("id");
                    }
                }
                Log.d(TAG, "Name " + governorName + " googlePlus= " + googlePlus +
                        " twitter= " + twitter + " Facebook= " + facebook + " youtube= " + youtube);

                governorInfoList.add(new GovernorInfo(governorName, position, party,
                        addressArray, phoneArray, emails, websites, photo, googlePlus, facebook,
                        twitter, youtube));
            }

            return governorInfoList;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
