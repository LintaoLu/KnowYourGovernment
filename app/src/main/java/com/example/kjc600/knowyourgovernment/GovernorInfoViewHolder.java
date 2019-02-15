package com.example.kjc600.knowyourgovernment;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class GovernorInfoViewHolder extends RecyclerView.ViewHolder
{
    public TextView governorPosition;
    public TextView governorName;

    public GovernorInfoViewHolder(View view)
    {
        super(view);
        governorPosition = view.findViewById(R.id.governor_position);
        governorName = view.findViewById(R.id.governor_name);
    }
}
