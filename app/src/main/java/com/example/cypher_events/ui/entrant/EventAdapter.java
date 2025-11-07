/**
 * EventAdapter.java
 *
 * Purpose:
 * RecyclerView adapter that binds Event objects to a list for entrant display.
 * Handles click events via OnClick interface callback.
 *
 * Outstanding Issues:
 * - Currently shows only title and location; additional fields may be added later.
 */

package com.example.cypher_events.ui.entrant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cypher_events.R;
import com.example.cypher_events.domain.model.Event;
import com.example.cypher_events.domain.model.Entrant;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying Event items in a RecyclerView.
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.VH> {

    /**Callback interface for clikc events on an Event item.*/
    public interface OnClick {
        void open(String eventId);
        void accept(String eventId);
        void decline(String eventId);
    }

    private final List<Event> data = new ArrayList<>();
    private final OnClick onClick;
    private Entrant currentEntrant;

    /**
     * Constructs an EventAdapter.
     * @param onClick callback invoked when an Event item is clicked.
     */
    public EventAdapter(OnClick onClick) {
        this.onClick = onClick;
    }

    public void setEntrant(Entrant entrant) {
        this.currentEntrant = entrant;
        notifyDataSetChanged();
    }

    /** ViewHolder class for event list items. */
    static class VH extends RecyclerView.ViewHolder {
        TextView title, location;
        Button btnAccept, btnDecline;
        View buttonContainer;

        VH(@NonNull View v) {
            super(v);
            title = v.findViewById(R.id.itemTitle);
            location = v.findViewById(R.id.itemLocation);
            btnAccept = v.findViewById(R.id.btnAccept);
            btnDecline = v.findViewById(R.id.btnDecline);
            buttonContainer = v.findViewById(R.id.itemButtonContainer);
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
        holder.title.setText(event.title);
        holder.location.setText(event.location);

        //show button container
        holder.buttonContainer.setVisibility(View.VISIBLE);

        //if entrant exists and has accepted/declined lists, reflect state
        boolean accepted = false;
        boolean declined = false;
        if (currentEntrant != null) {
            List<String> acceptedList = currentEntrant.getEntrant_acceptedEvents();
            List<String> declinedList = currentEntrant.getEntrant_declinedEvents();
            accepted = acceptedList != null && acceptedList.contains(event.id);
            declined = declinedList != null && declinedList.contains(event.id);
        }

        //update buttons UI
        holder.btnAccept.setEnabled(!accepted && !declined);
        holder.btnAccept.setText(accepted ? "Accepted" : "Accept");
        holder.btnDecline.setEnabled(!declined);
        holder.btnDecline.setText(declined ? "Declined" : "Decline");

        holder.itemView.setOnClickListener(view -> onClick.open(event.id));
        holder.btnAccept.setOnClickListener(view -> onClick.accept(event.id));
        holder.btnDecline.setOnClickListener(view -> onClick.decline(event.id));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * Replaces adapter's data with a new list of events and refreshes the view.
     * @param events new list of Event objects.
     */
    public void submit(List<Event> events) {
        data.clear();
        if (events != null) data.addAll(events);
        notifyDataSetChanged();
    }
}
