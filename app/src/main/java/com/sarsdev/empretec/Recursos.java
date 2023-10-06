package com.sarsdev.empretec;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Recursos extends Fragment {

    View view;
    CardView categoryCard1, categoryCard2, categoryCard3, categoryCard4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_recursos, container, false);

        categoryCard1 = view.findViewById(R.id.categoryCard);
        categoryCard2 = view.findViewById(R.id.categoryCard2);
        categoryCard3 = view.findViewById(R.id.categoryCard3);
        categoryCard4 = view.findViewById(R.id.categoryCard4);

        categoryCard1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Convocatorias.class);
                startActivity(intent);
            }
        });

        categoryCard2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Libros.class);
                startActivity(intent);
            }
        });

        categoryCard3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Libros.class);
                startActivity(intent);
            }
        });

        categoryCard4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Libros.class);
                startActivity(intent);
            }
        });

        return view;

    }
}