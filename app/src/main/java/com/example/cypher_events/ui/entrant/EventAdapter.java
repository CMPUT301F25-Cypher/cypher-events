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

import android.graphics.Bitmap;
import com.example.cypher_events.util.ImageProcessor;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {


    private static final int[] PLACEHOLDERS = new int[] {
            R.drawable.ic_placeholder_image1,
            R.drawable.ic_placeholder_image2,
            R.drawable.ic_placeholder_image3,
            R.drawable.ic_placeholder_image4,
            R.drawable.ic_placeholder_image5,
            R.drawable.ic_placeholder_image6,
            R.drawable.ic_placeholder_image7,
    };

    public static int getPlaceholderForId(String eventId) {
        if (eventId == null || eventId.isEmpty()) {
            return PLACEHOLDERS[0];
        }
        int idx = Math.abs(eventId.hashCode()) % PLACEHOLDERS.length;
        return PLACEHOLDERS[idx];
    }


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

        // Title
        String title = event.getEvent_title();
        if (title == null || title.isEmpty()) title = "Untitled Event";
        holder.tvName.setText(title);

        //  Location
        String loc = event.getEvent_location();
        if (loc == null || loc.isEmpty()) {
            holder.tvLocation.setText("📍 Unknown location");
        } else {
            holder.tvLocation.setText("📍 " + loc);
        }

        // Capacity
        int cap = event.getEvent_capacity();
        String capText;
        if (cap > 0) {

            capText = "Capacity: " + cap;
        } else {
            capText = "Capacity: N/A";
        }
        holder.tvCapacity.setText(capText);

        // Registration end date
        long end = event.getEvent_signupEndUtc();
        String endText;
        if (end > 0) {
            endText = "Ends: " + dateFormat.format(new Date(end));
        } else {
            endText = "Ends: N/A";
        }
        holder.tvEnd.setText(endText);

        // Image
        String b64 = event.getPosterBase64();
        if (b64 != null && !b64.trim().isEmpty()) {
            Bitmap bmp = ImageProcessor.base64ToBitmap(b64);
            if (bmp != null) {
                holder.imgEvent.setImageBitmap(bmp);
            } else {
                holder.imgEvent.setImageResource(R.mipmap.ic_launcher); // fallback
            }
        } else {
            holder.imgEvent.setImageResource(EventAdapter.getPlaceholderForId(event.getEvent_id()));
        }

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
