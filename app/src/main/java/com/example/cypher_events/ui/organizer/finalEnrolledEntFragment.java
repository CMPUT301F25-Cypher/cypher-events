package com.example.cypher_events.ui.organizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cypher_events.R;

import java.util.ArrayList;

public class finalEnrolledEntFragment extends Fragment {
    public finalEnrolledEntFragment(){

    }
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstances){
        return inflater.inflate(R.layout.view_enrolled_finalenterant,container,false);
    }

    @Nullable
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayList<String> test = new ArrayList<>();
        test.add("Peterparker:peterparker@123");
        test.add("Tony Stark:Ironman@3000");
        ArrayAdapter<String> adapter=new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, test);
        ListView finalenrolled = view.findViewById(R.id.finalenrolledTextListView);
        finalenrolled.setAdapter(adapter);
    }
    //still need to figure out where to send the back button
}
