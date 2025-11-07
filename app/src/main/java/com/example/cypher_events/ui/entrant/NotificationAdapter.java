package com.example.cypher_events.ui.entrant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cypher_events.R;
import com.example.cypher_events.domain.model.Notification;
import com.example.cypher_events.domain.model.NotificationItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<Notification> notifications;

    public NotificationAdapter() {
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
        Notification item = notifications.get(position);
        holder.textTitle.setText(item.getTitle());
        holder.textMessage.setText(item.getMessage());
        holder.textTimestamp.setText(formatTimestamp(item.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    private String formatTimestamp(long timestamp) {
        return new SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
                .format(new Date(timestamp));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textMessage, textTimestamp;

        ViewHolder(View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textMessage = itemView.findViewById(R.id.textMessage);
            textTimestamp = itemView.findViewById(R.id.textTimestamp);
        }
    }

    public void updateList(List<Notification> newNotifications) {
        this.notifications = newNotifications;
        notifyDataSetChanged();
    }
}
