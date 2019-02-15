package com.example.kjc600.knowyourgovernment;

import java.io.Serializable;
import java.util.ArrayList;

public class GovernorInfo implements Serializable
{
    private String governorPosition = "";
    private String governorName = "";
    private String party = "";
    private ArrayList<String> address = new ArrayList<>();
    private ArrayList<String> phone = new ArrayList<>();
    private ArrayList<String> email = new ArrayList<>();
    private ArrayList<String> website = new ArrayList<>();
    private String photo = "";
    private String googlePlus = "";
    private String faceBook = "";
    private String twitter = "";
    private String youtube = "";

    public GovernorInfo(String governorName, String governorPosition, String party, ArrayList<String> address,
            ArrayList<String> phone, ArrayList<String> emails, ArrayList<String> website, String photo,
                       String googlePlus, String faceBook, String twitter, String youtube)
    {
        this.governorName = governorName;
        this.governorPosition = governorPosition;
        this.party = party;
        this.address = address;
        this.phone = phone;
        this.email = emails;
        this.website = website;
        this.photo = photo;
        this.googlePlus = googlePlus;
        this.faceBook = faceBook;
        this.twitter = twitter;
        this.youtube = youtube;
    }


    public String getGovernorPosition() {
        return governorPosition;
    }

    public String getGovernorName() {
        return governorName;
    }

    public String getParty() {
        return party;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getPhoto() {
        return photo;
    }

    public String getGooglePlus() {
        return googlePlus;
    }

    public String getFaceBook() {
        return faceBook;
    }

    public String getYoutube() {
        return youtube;
    }

    public ArrayList<String> getAddress() {
        return address;
    }

    public ArrayList<String> getEmail() {
        return email;
    }

    public ArrayList<String> getPhone() {
        return phone;
    }

    public ArrayList<String> getWebsite() {
        return website;
    }
}
