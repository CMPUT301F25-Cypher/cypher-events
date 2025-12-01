package com.example.cypher_events.ui.organizer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cypher_events.R;

import java.util.ArrayList;
import java.util.List;

public class WaitingListAdapter extends RecyclerView.Adapter<WaitingListAdapter.ViewHolder> {

    private final List<EntrantItem> entrants = new ArrayList<>();

    public void setData(List<EntrantItem> list) {
        entrants.clear();
        if (list != null) entrants.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_waitlist_user, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        EntrantItem e = entrants.get(position);
        h.tvName.setText(e.name);
        h.tvEmail.setText(e.email);
    }

    @Override
    public int getItemCount() {
        return entrants.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail;

        public ViewHolder(@NonNull View v) {
            super(v);
            tvName = v.findViewById(R.id.tvUserName);
            tvEmail = v.findViewById(R.id.tvUserEmail);
        }
    }

    public static class EntrantItem {
        public String name;
        public String email;
    }
}