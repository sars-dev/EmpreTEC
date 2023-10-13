package com.sarsdev.emprendetec.Custom;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sarsdev.emprendetec.R;

public class Detalles extends AppCompatActivity {

    TextView title, desc, cat;
    ImageView detailImage;
    String key = "";
    String imageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles);

        title = findViewById(R.id.Title);
        detailImage = findViewById(R.id.recImage);
        desc = findViewById(R.id.desc);
        cat = findViewById(R.id.cat);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            desc.setText(bundle.getString("Description"));
            title.setText(bundle.getString("Title"));
            cat.setText(bundle.getString("Category"));
            key = bundle.getString("Key");
            imageUrl = bundle.getString("Image");
            Glide.with(this).load(bundle.getString("Image")).into(detailImage);
        }

    }
}