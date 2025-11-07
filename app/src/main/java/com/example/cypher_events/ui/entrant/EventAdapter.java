package com.example.cypher_events.ui.entrant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cypher_events.R;
import com.example.cypher_events.domain.model.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    public interface OnEventClickListener {
        void onEventClick(String eventId);
    }

    private List<Event> eventList = new ArrayList<>();
    private final OnEventClickListener listener;

    public EventAdapter(OnEventClickListener listener) {
        this.listener = listener;
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
        Event event = eventList.get(position);

        String title = (event.getEvent_title() == null || event.getEvent_title().isEmpty())
                ? "Untitled Event" : event.getEvent_title();
        String loc = (event.getEvent_location() == null || event.getEvent_location().isEmpty())
                ? "📍 Unknown Location" : "📍 " + event.getEvent_location();

        holder.tvName.setText(title);
        holder.tvLocation.setText(loc);

        long start = event.getEvent_signupStartUtc();
        String dateString = (start > 0)
                ? new java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
                .format(new java.util.Date(start))
                : "N/A";
        holder.tvDate.setText("🗓 " + dateString);

        holder.itemView.setOnClickListener(v -> listener.onEventClick(event.getEvent_id()));
    }


    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void submit(List<Event> events) {
        this.eventList = events;
        notifyDataSetChanged();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvLocation, tvDate;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvEventName);
            tvLocation = itemView.findViewById(R.id.tvEventLocation);
            tvDate = itemView.findViewById(R.id.tvEventDate);
        }
    }
}
