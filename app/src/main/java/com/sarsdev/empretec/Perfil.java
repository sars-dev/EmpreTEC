package com.sarsdev.empretec;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class Perfil extends Fragment {

    View view;
    Button btnSalir;
    private FirebaseAuth mAuth;
    private FirebaseUser useract;
    private TextView occupationTxtView, nameTxtView, emailTxtView, phoneTxtView, nameTV;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_perfil, container, false);

        btnSalir = view.findViewById(R.id.btnLogout);
        mAuth = FirebaseAuth.getInstance();
        useract = FirebaseAuth.getInstance().getCurrentUser();
        btnSalir.setOnClickListener(view -> {
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), Login.class);
            startActivity(intent);
        });

        occupationTxtView = view.findViewById(R.id.occupation_textview);
        nameTxtView = view.findViewById(R.id.tvName);
        nameTV = view.findViewById(R.id.name_textview);
        emailTxtView = view.findViewById(R.id.email_textview);
        phoneTxtView = view.findViewById(R.id.phone_textview);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference loginLogsRef = db.collection("users");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            String usersId = firebaseUser.getUid();
            loginLogsRef.document(usersId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map<String, Object> data = document.getData();
                            String nombreUsuario = (String) data.get("Nombre");
                            String nombre = (String) data.get("Nombre");
                            String apellidoUsuario = (String) data.get("Apellido");
                            String correoUsuario = (String) data.get("Correo");
                            nameTxtView.setText("");
                            nameTV.setText(nombre);
                            occupationTxtView.setText(nombreUsuario +" "+ apellidoUsuario);
                            emailTxtView.setText(correoUsuario);
                        } else {
                            Toast.makeText(getActivity(), "No hay datos", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d("Firestore", "Error getting user data: " + task.getException());
                    }
                }
            });
        } else {
            Toast.makeText(getActivity(), "No hay usuario", Toast.LENGTH_SHORT).show();
        }

        return view;
    }
}