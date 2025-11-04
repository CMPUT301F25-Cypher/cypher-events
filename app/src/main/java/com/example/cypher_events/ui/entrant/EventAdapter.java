package com.example.cypher_events.ui.entrant;

import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cypher_events.R;
import com.example.cypher_events.domain.model.Event;
import java.util.*;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.VH> {
    public interface OnClick { void open(String eventId); }

    private final List<Event> data = new ArrayList<>();
    private final OnClick onClick;

    public EventAdapter(OnClick onClick) { this.onClick = onClick; }

    static class VH extends RecyclerView.ViewHolder {
        TextView title, subtitle;
        VH(@NonNull View v) {
            super(v);
            title = v.findViewById(R.id.itemTitle);
            subtitle = v.findViewById(R.id.itemSubtitle);
        }
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int vt) {
        View v = LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_event, p, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        Event e = data.get(pos);
        h.title.setText(e.title);
        h.subtitle.setText(e.location);
        h.itemView.setOnClickListener(view -> onClick.open(e.id));
    }

    @Override public int getItemCount() { return data.size(); }

    public void submit(List<Event> events) {
        data.clear();
        if (events != null) data.addAll(events);
        notifyDataSetChanged();
    }
}
