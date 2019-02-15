package com.example.kjc600.knowyourgovernment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class GovernorInfoActivity extends AppCompatActivity {

    private GovernorInfo governor;
    private TextView address;
    private TextView phone;
    private TextView email;
    private TextView website;
    private TextView location2;
    private TextView position;
    private TextView name;
    private TextView party;
    private ImageView faceBook;
    private ImageView googlePlus;
    private ImageView twitter;
    private ImageView youtube;
    private ImageView picture;
    private String location;
    private int failed = 0;
    private static final String FILE_NAME = "last_location.txt";
    private String lastLocation;
    private static final String TAG = "GovernorInfoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_governor_info);

        location2 = findViewById(R.id.location2);
        address = findViewById(R.id.address_info);
        phone = findViewById(R.id.phone_info);
        email = findViewById(R.id.email_info);
        website = findViewById(R.id.wensite_info);
        position = findViewById(R.id.governor_position);
        name = findViewById(R.id.governor_name);
        party = findViewById(R.id.governor_party);
        faceBook = findViewById(R.id.facebook_image);
        twitter = findViewById(R.id.twitter_image);
        youtube = findViewById(R.id.youtube_image);
        googlePlus = findViewById(R.id.googleplus_image);
        picture = findViewById(R.id.governor_picture);

        governor = (GovernorInfo) getIntent().getSerializableExtra("Governor");
        location = getIntent().getStringExtra("Location");

        lastLocation = getIntent().getStringExtra("lastLocation");

        location2.setText(location);
        name.setText(governor.getGovernorName());
        position.setText(governor.getGovernorPosition());
        party.setText("(" + governor.getParty() + ")");

        if(governor.getAddress().size() != 0)
            address.setText(governor.getAddress().get(0).trim());
        else
            address.setText("No address");

        if(governor.getPhone().size() != 0)
            phone.setText(governor.getPhone().get(0));
        else
            phone.setText("No phone");

        if(governor.getEmail().size() != 0)
            email.setText(governor.getEmail().get(0));
        else
            email.setText("No email");

        if(governor.getWebsite().size() != 0)
            website.setText(governor.getWebsite().get(0));
        else
            website.setText("No website");


        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String map = "http://maps.google.co.in/maps?q=" + address.getText().toString();
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                startActivity(i);
            }
        });

        if(! address.getText().toString().equals("No address"))
            address.setPaintFlags(address.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);

        Linkify.addLinks(phone, Linkify.PHONE_NUMBERS);
        phone.setLinkTextColor(Color.parseColor("#ffffff"));
        Linkify.addLinks(website, Linkify.WEB_URLS);
        website.setLinkTextColor(Color.parseColor("#ffffff"));
        Linkify.addLinks(email, Linkify.EMAIL_ADDRESSES);
        email.setLinkTextColor(Color.parseColor("#ffffff"));

        if(governor.getParty().equals("Democratic"))
            getWindow().getDecorView().setBackgroundColor(Color.BLUE);
        else if(governor.getParty().equals("Republican"))
            getWindow().getDecorView().setBackgroundColor(Color.RED);
        else
            getWindow().getDecorView().setBackgroundColor(Color.BLACK);

        if(governor.getPhoto().equals(""))
        {
            picture.setImageResource(R.drawable.missingimage);
            failed = 1;
        }

        else
            {
                Picasso picasso = new Picasso.Builder(this).
                        listener(new Picasso.Listener() {
                            @Override
                            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                                picasso.load(R.drawable.missingimage).into(picture);
                                failed = 1;
                            }
                        })
                        .build();

                picasso.load(governor.getPhoto())
                        .placeholder(R.drawable.placeholder)
                        .into(picture);
            }

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(failed == 0)
                {
                    Intent intent = new Intent(GovernorInfoActivity.this, LargePhotoActivity.class);
                    intent.putExtra("Governor", governor);
                    intent.putExtra("Location", location);
                    intent.putExtra("lastLocation", lastLocation);
                    startActivity(intent);
                }
            }
        });

        if(governor.getFaceBook().equals(""))
            faceBook.setVisibility(View.INVISIBLE);
        if(governor.getTwitter().equals(""))
            twitter.setVisibility(View.INVISIBLE);
        if(governor.getYoutube().equals(""))
            youtube.setVisibility(View.INVISIBLE);
        if(governor.getGooglePlus().equals(""))
            googlePlus.setVisibility(View.INVISIBLE);


        if(!governor.getFaceBook().equals(""))
        {
            faceBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String fbName = governor.getFaceBook();
                    String FACEBOOK_URL = "https://www.facebook.com/" + fbName;

                    Intent intent = null;
                    String urlToUse;
                    try {
                        getPackageManager().getPackageInfo("com.facebook.katana", 0);

                        int versionCode = getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
                        if (versionCode >= 3002850) { //newer versions of fb app
                            urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
                        } else { //older versions of fb app
                            urlToUse = "fb://page/" + fbName;
                        }
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlToUse));
                    } catch (Exception e) {
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse(FACEBOOK_URL));
                    }

                    startActivity(intent);
                }
            });
        }

        if(!governor.getTwitter().equals(""))
        {
            twitter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String user = governor.getTwitter();
                    String twitterAppUrl = "twitter://user?screen_name=" + user;
                    String twitterWebUrl = "https://twitter.com/" + user;

                    Intent intent = null;
                    try {
                        getPackageManager().getPackageInfo("com.twitter.android", 0);
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterAppUrl));
                    } catch (Exception e) {
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterWebUrl));
                    }
                    startActivity(intent);
                }
            });
        }

        if(!governor.getGooglePlus().equals(""))
        {
            googlePlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = governor.getGooglePlus();
                    Intent intent = null;
                    try
                    {
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.setClassName("com.google.android.apps.plus",
                                "com.google.android.apps.plus.phone.UrlGatewayActivity");
                        intent.putExtra("customAppUri", name); startActivity(intent);
                    }
                    catch (ActivityNotFoundException e)
                    {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/" + name)));
                    }
                }
            });
        }

        if(!governor.getYoutube().equals(""))
        {
            youtube.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = governor.getYoutube();
                    Intent intent = null;
                    try
                    {
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.setPackage("com.google.android.youtube");
                        intent.setData(Uri.parse("https://www.youtube.com/" + name)); startActivity(intent);
                    }
                    catch (ActivityNotFoundException e)
                    {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/" + name)));
                    }
                }
            });
        }
    }
}
