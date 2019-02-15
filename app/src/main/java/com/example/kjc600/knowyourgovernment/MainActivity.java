package com.example.kjc600.knowyourgovernment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener
{
    private ArrayList<GovernorInfo> governorInfoList = new ArrayList<>();
    private RecyclerView recyclerView;
    private GovernorInfoAdapter mAdapter;
    private TextView locationTV;
    private int position;
    private String location;
    private Locator locator;
    private static final String TAG = "MainActivity";
    private TextView noNetworkLocationTV;
    private TextView noNetWorkWarningTv;
    private String lastLocation;
    int viewID;

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString("lastLocation", location);
    }

    @Override
    protected void onCreate(Bundle outState) {
        super.onCreate(outState);

        if(doNetCheck())
        {
            setContentView(R.layout.activity_main);
            viewID = 0;
            locationTV = findViewById(R.id.location_textView);
            recyclerView = findViewById(R.id.recyclerView);
            mAdapter = new GovernorInfoAdapter(governorInfoList,this);
            recyclerView.setAdapter(mAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            locator = new Locator(this);
            if(outState!=null)
                new DownloadAsyncTask(this, outState.getString("lastLocation"));
            else
                locator = new Locator(this);
        }
        else
            goToNoNetworkView();
    }

    @Override
    protected void onStart()
    {
        super.onStart();

    }

    @Override
    public void onClick(View v)
    {
        position = recyclerView.getChildAdapterPosition(v);
        GovernorInfo governor = governorInfoList.get(position);
        Intent intent = new Intent(MainActivity.this, GovernorInfoActivity.class);
        intent.putExtra("Governor", governor);
        intent.putExtra("Location", location);
        intent.putExtra("lastLocation", lastLocation);
        startActivity(intent);
    }

    public void updateLocation(String lo)
    {
        if(!(lo == null))
        {
            location = lo;
            locationTV.setText(location);
        }
    }

    public void updateList(ArrayList<GovernorInfo> info)
    {
        if(info != null)
        {
            governorInfoList.clear();
            governorInfoList.addAll(info);
            mAdapter.notifyDataSetChanged();
        }
        else
            {
                showWarning("Search Failed!", "Your location may not be correct!");
            }
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.mian, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.version_menu:
                Intent intent = new Intent(MainActivity.this, VersionActivity.class);
                startActivity(intent);
                break;
            case R.id.location_menu:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final EditText et = new EditText(this);
                et.setInputType(InputType.TYPE_CLASS_TEXT);
                et.setGravity(Gravity.CENTER_HORIZONTAL);
                builder.setView(et);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if(doNetCheck())
                        {
                            if(viewID != 0)
                                goToMainView();
                            String currentLocation = et.getText().toString().trim();
                            lastLocation = currentLocation;
                            new DownloadAsyncTask(MainActivity.this, currentLocation).execute();
                        }
                        else
                            goToNoNetworkView();
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "canceled", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setMessage("Please enter a location:");
                builder.setTitle("Change Location");

                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }
        return true;
    }

    private void showWarning(String title, String msg)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setIcon(R.drawable.warning);

        builder.setMessage(msg);
        builder.setTitle(title);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void setData(double Latitude, double Longitude)
    {
        String address = doAddress(Latitude, Longitude);
        Log.d(TAG, "setData: " + Latitude + " " + Longitude);
        new DownloadAsyncTask(this, address).execute();
    }

    public void noLocationAvailable() {
        Toast.makeText(this, "No location providers were available", Toast.LENGTH_LONG).show();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 5) {
            Log.d(TAG, "onRequestPermissionsResult: permissions.length: " + permissions.length);
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "onRequestPermissionsResult: HAS PERM");
                        locator.setUpLocationManager();
                        locator.determineLocation();
                    } else {
                        Toast.makeText(this, "Location permission was denied " +
                                "- " + "cannot determine address", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onRequestPermissionsResult: NO PERM");
                    }
                }
            }
        }
        Log.d(TAG, "onRequestPermissionsResult: Exiting onRequestPermissionsResult");
    }

    private String doAddress(double latitude, double longitude)
    {
        List<Address> addresses = null;

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try
        {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Address add = addresses.get(0);
            if(add.getPostalCode() != null)
            {
                return add.getPostalCode();
            }
        }
        catch (IOException e) { }
        return null;
    }

    private boolean doNetCheck()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm == null)
        {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;
        }
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnectedOrConnecting())
            return true;
        else
            return false;
    }

    protected void onDestroy()
    {
        if(locator != null)
            locator.shutDown();
        super.onDestroy();
    }

    private void goToNoNetworkView()
    {
        setContentView(R.layout.no_network);
        viewID = 1;
        noNetworkLocationTV = findViewById(R.id.no_network_location);
        noNetworkLocationTV.setText("No Data for Location");
        noNetWorkWarningTv = findViewById(R.id.no_network_title);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("No Network Connection! \n \n");
        stringBuilder.append("Data cannot be accessed/loaded without an internet connection.");
        noNetWorkWarningTv.setText(stringBuilder.toString());
    }

    private void goToMainView()
    {
        setContentView(R.layout.activity_main);
        viewID = 0;
        locationTV = findViewById(R.id.location_textView);
        recyclerView = findViewById(R.id.recyclerView);
        mAdapter = new GovernorInfoAdapter(governorInfoList,this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}