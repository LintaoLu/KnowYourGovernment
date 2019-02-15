package com.example.kjc600.knowyourgovernment;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class GovernorInfoAdapter extends RecyclerView.Adapter<GovernorInfoViewHolder>
{
    private ArrayList<GovernorInfo> governorInfoList;
    private MainActivity ma;

    public GovernorInfoAdapter(ArrayList<GovernorInfo> list, MainActivity mainActivity)
    {
        governorInfoList = list;
        ma = mainActivity;
    }

    @NonNull
    @Override
    public GovernorInfoViewHolder onCreateViewHolder( @NonNull final ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.governor_info, parent, false);

        itemView.setOnClickListener(ma);

        return new GovernorInfoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GovernorInfoViewHolder governorInfoViewHolder, int position)
    {
        GovernorInfo governorInfo = governorInfoList.get(position);
        String governorPosition = governorInfo.getGovernorPosition();
        String governorName = governorInfo.getGovernorName();
        String party = governorInfo.getParty();

        governorInfoViewHolder.governorPosition.setText(governorPosition);
        governorInfoViewHolder.governorName.setText(governorName + "(" + party + ")");
    }

    @Override
    public int getItemCount() {
        return governorInfoList.size();
    }
}
