package com.example.kjc600.knowyourgovernment;

import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class LargePhotoActivity extends AppCompatActivity {

    private TextView location3;
    ImageView image;
    private TextView name;
    private TextView position;
    private static final String FILE_NAME = "last_location.txt";
    private String lastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_large_photo);


        GovernorInfo governorInfo = (GovernorInfo) getIntent().getSerializableExtra("Governor");
        String location = getIntent().getStringExtra("Location");

        lastLocation = getIntent().getStringExtra("lastLocation");

        location3 = findViewById(R.id.location3);
        location3.setText(location);

        image = findViewById(R.id.imageView);

        Picasso picasso = new Picasso.Builder(this).
                listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        picasso.load(R.drawable.missingimage).into(image);
                    }
                })
                .build();

        picasso.load(governorInfo.getPhoto())
                .placeholder(R.drawable.placeholder)
                .into(image);


        name = findViewById(R.id.name2);
        position = findViewById(R.id.position2);

        name.setText(governorInfo.getGovernorName());
        position.setText(governorInfo.getGovernorPosition());

        if(governorInfo.getParty().equals("Democratic"))
            getWindow().getDecorView().setBackgroundColor(Color.BLUE);
        else if(governorInfo.getParty().equals("Republican"))
            getWindow().getDecorView().setBackgroundColor(Color.RED);
        else
            getWindow().getDecorView().setBackgroundColor(Color.BLACK);
    }

}
