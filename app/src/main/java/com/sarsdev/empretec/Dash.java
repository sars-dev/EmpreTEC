package com.sarsdev.empretec;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.sarsdev.empretec.Fragment.Inicio;
import com.sarsdev.empretec.Fragment.Perfil;
import com.sarsdev.empretec.Fragment.Proyectos;
import com.sarsdev.empretec.Fragment.Recursos;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class Dash extends AppCompatActivity {

    AnimatedBottomBar bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomBar = findViewById(R.id.bottom_bar);

        replace(new Inicio());

        bottomBar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener(){

            @Override
            public void onTabSelected(int i, @Nullable AnimatedBottomBar.Tab tab, int i1, @NonNull AnimatedBottomBar.Tab tab1) {

                if (tab1.getId()==R.id.home){
                    replace(new Inicio());
                } else if (tab1.getId()==R.id.resources) {
                    replace(new Recursos());
                } else if (tab1.getId()==R.id.projects) {
                    replace(new Proyectos());
                } else if (tab1.getId()==R.id.profile) {
                    replace(new Perfil());
                }

            }

            @Override
            public void onTabReselected(int i, @NonNull AnimatedBottomBar.Tab tab) {

            }
        });

    }

    private void replace(Fragment fragment){

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout, fragment);
        transaction.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.about){
            Toast.makeText(this, "Proximamente", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}