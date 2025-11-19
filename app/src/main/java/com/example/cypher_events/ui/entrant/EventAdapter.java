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
        if (event == null) {
            return;
        }

        // Title
        String title = event.getEvent_title();
        if (title == null || title.isEmpty()) {
            title = "Untitled Event";
        }
        holder.tvName.setText(title);

        // Location
        String loc = event.getEvent_location();
        if (loc == null || loc.isEmpty()) {
            holder.tvLocation.setText("📍 Unknown Location");
        } else {
            holder.tvLocation.setText("📍 " + loc);
        }

        // Date (from signup start time)
        long start = event.getEvent_signupStartUtc();
        String dateString;
        if (start > 0) {
            SimpleDateFormat fmt = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            dateString = fmt.format(new Date(start));
        } else {
            dateString = "N/A";
        }
        holder.tvDate.setText("🗓 " + dateString);

        // Click callback
        holder.itemView.setOnClickListener(v -> {
            if (listener != null && event.getEvent_id() != null) {
                listener.onEventClick(event.getEvent_id());
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void submit(List<Event> events) {
        if (events == null) {
            this.eventList = new ArrayList<>();
        } else {
            this.eventList = new ArrayList<>(events); // defensive copy
        }
        notifyDataSetChanged();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvLocation;
        TextView tvDate;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvEventName);
            tvLocation = itemView.findViewById(R.id.tvEventLocation);
            tvDate = itemView.findViewById(R.id.tvEventDate);
        }
    }
}
