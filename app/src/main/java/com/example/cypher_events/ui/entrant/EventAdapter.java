package com.example.cypher_events.ui.entrant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    public EventAdapter(OnEventClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_home, parent, false);  // 👈 NEW LAYOUT
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        if (event == null) return;

        // --- Title ---
        String title = event.getEvent_title();
        if (title == null || title.isEmpty()) title = "Untitled Event";
        holder.tvName.setText(title);

        // --- Location ---
        String loc = event.getEvent_location();
        if (loc == null || loc.isEmpty()) {
            holder.tvLocation.setText("📍 Unknown location");
        } else {
            holder.tvLocation.setText("📍 " + loc);
        }

        // --- Capacity ---
        int cap = event.getEvent_capacity();
        String capText;
        if (cap > 0) {
            // if you later track remaining spots, you can plug it here
            capText = "Capacity: " + cap;
        } else {
            capText = "Capacity: N/A";
        }
        holder.tvCapacity.setText(capText);

        // --- Registration end date ---
        long end = event.getEvent_signupEndUtc();
        String endText;
        if (end > 0) {
            endText = "Ends: " + dateFormat.format(new Date(end));
        } else {
            endText = "Ends: N/A";
        }
        holder.tvEnd.setText(endText);

        // --- Image ---
        // For performance we keep this as a grey placeholder.
        // (You already show the real image in the detail screen.)
        // If you later want thumbnails, you can load them here.

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
            this.eventList = new ArrayList<>(events);
        }
        notifyDataSetChanged();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView imgEvent;
        TextView tvName;
        TextView tvLocation;
        TextView tvCapacity;
        TextView tvEnd;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);
            imgEvent = itemView.findViewById(R.id.imgEvent);
            tvName = itemView.findViewById(R.id.tvEventName);
            tvLocation = itemView.findViewById(R.id.tvEventLocation);
            tvCapacity = itemView.findViewById(R.id.tvEventCapacity);
            tvEnd = itemView.findViewById(R.id.tvEventEnd);
        }
    }
}
