package com.example.cypher_events.ui.organizer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cypher_events.R;
import com.example.cypher_events.util.QRGenerator;

public class GenerateQRFragment extends Fragment {

    private static final String ARG_EVENT_ID = "EventId";
    private String eventId;
    private ImageView imgQRCode;
    private TextView tvEventId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_generate_qr, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        Bundle args = getArguments();
        eventId = (args != null) ? args.getString(ARG_EVENT_ID) : null;

        imgQRCode = view.findViewById(R.id.imgQRCode);
        tvEventId = view.findViewById(R.id.tvEventId);

        ImageButton btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> 
                requireActivity().getSupportFragmentManager().popBackStack()
            );
        }

        if (eventId != null && !eventId.isEmpty()) {
            generateQRCode();
        } else {
            Toast.makeText(getContext(), "No event ID provided", Toast.LENGTH_SHORT).show();
        }
    }

    private void generateQRCode() {
        String qrContent = "event_id=" + eventId;
        
        Bitmap qrBitmap = QRGenerator.generate(qrContent, 512);
        
        if (qrBitmap != null) {
            imgQRCode.setImageBitmap(qrBitmap);
            tvEventId.setText("Event ID: " + eventId);
        } else {
            Toast.makeText(getContext(), "Failed to generate QR code", Toast.LENGTH_SHORT).show();
        }
    }
}
