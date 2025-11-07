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

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private final List<Notification> notifications;

    public NotificationAdapter(List<Notification> notifications) {
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification n = notifications.get(position);
        holder.title.setText(n.getTitle());
        holder.message.setText(n.getMessage());
        holder.timestamp.setText(
                new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                        .format(new Date(n.getTimestamp()))
        );
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, message, timestamp;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textTitle);
            message = itemView.findViewById(R.id.textMessage);
            timestamp = itemView.findViewById(R.id.textTimestamp);
        }
    }
}
