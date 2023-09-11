package com.sarsdev.empretec;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class Detalles extends AppCompatActivity {

    TextView title, desc, cat, detailLang2;
    ImageView detailImage;
    String key = "";
    String imageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles);

        title = findViewById(R.id.title);
        detailImage = findViewById(R.id.image);
        desc = findViewById(R.id.desc);
        cat = findViewById(R.id.cat);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            desc.setText(bundle.getString("Description"));
            title.setText(bundle.getString("Title"));
            cat.setText(bundle.getString("Language"));
            key = bundle.getString("Key");
            imageUrl = bundle.getString("Image");
            Glide.with(this).load(bundle.getString("Image")).into(detailImage);
        }

    }
}