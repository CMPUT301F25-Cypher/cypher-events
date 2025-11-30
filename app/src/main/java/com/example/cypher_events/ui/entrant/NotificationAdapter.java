package com.example.cypher_events.ui.entrant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cypher_events.R;
import com.example.cypher_events.domain.model.Notification;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.VH> {

    private final List<Notification> data;

    public NotificationAdapter(List<Notification> data) {
        this.data = data;
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView title, message, ts;
        VH(View v) {
            super(v);
            title = v.findViewById(R.id.textTitle);
            message = v.findViewById(R.id.textMessage);
            ts = v.findViewById(R.id.textTimestamp);
        }
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        Notification n = data.get(pos);
        h.title.setText(n.getTitle());
        h.message.setText(n.getMessage());
        String t = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
                .format(new Date(n.getTimestampUtc()));
        h.ts.setText(t);
    }

    @Override public int getItemCount() { return data.size(); }
}
