package com.example.cypher_events.ui.entrant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cypher_events.R;
import com.example.cypher_events.domain.model.Event;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    public interface OnClick {
        void open(String eventId);
    }

    private List<Event> events = new ArrayList<>();
    private final OnClick listener;

    public EventAdapter(OnClick listener) {
        this.listener = listener;
    }

    public void submit(List<Event> updatedEvents) {
        this.events = updatedEvents != null ? updatedEvents : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);

        holder.title.setText(event.getEvent_title());
        holder.location.setText(event.getEvent_location());
        holder.itemView.setOnClickListener(v -> listener.open(event.getEvent_id()));

        // Optional placeholder image
        holder.image.setImageResource(R.drawable.ic_launcher_background);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, location;
        Button accept, decline;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.eventImage);
            title = itemView.findViewById(R.id.itemTitle);
            location = itemView.findViewById(R.id.itemLocation);
            accept = itemView.findViewById(R.id.btnAccept);
            decline = itemView.findViewById(R.id.btnDecline);
        }
    }
}
