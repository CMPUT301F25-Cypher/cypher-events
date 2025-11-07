package com.example.cypher_events.ui.entrant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cypher_events.R;
import com.example.cypher_events.domain.model.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter to display a list of events for entrants.
 * Updated to match the new Event model with getters.
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.VH> {

    public interface OnClick {
        void open(String eventId);
    }

    private final List<Event> data = new ArrayList<>();
    private final OnClick onClick;

    public EventAdapter(OnClick onClick) {
        this.onClick = onClick;
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView title, subtitle;

        VH(@NonNull View v) {
            super(v);
            title = v.findViewById(R.id.itemTitle);
            subtitle = v.findViewById(R.id.itemSubtitle);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Event event = data.get(position);

        // Use the getters from your Event model
        holder.title.setText(event.getEvent_title());
        holder.subtitle.setText(event.getEvent_location());

        holder.itemView.setOnClickListener(view -> onClick.open(event.getEvent_id()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void submit(List<Event> events) {
        data.clear();
        if (events != null) data.addAll(events);
        notifyDataSetChanged();
    }
}
