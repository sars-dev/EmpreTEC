package com.sarsdev.empretec.Fragment;

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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sarsdev.empretec.Custom.Editprofile;
import com.sarsdev.empretec.Login;
import com.sarsdev.empretec.R;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Perfil extends Fragment {

    View view;
    CircleImageView imgProfile;
    Button btnSalir;
    ExtendedFloatingActionButton fabFA;
    private FirebaseAuth mAuth;
    private FirebaseUser useract;
    private TextView occupationTxtView, nameTxtView, emailTxtView, phoneTxtView, streetTxtView, nameTV;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_perfil, container, false);

        btnSalir = view.findViewById(R.id.btnLogout);
        fabFA = view.findViewById(R.id.fabFA);
        mAuth = FirebaseAuth.getInstance();
        useract = FirebaseAuth.getInstance().getCurrentUser();

        btnSalir.setOnClickListener(view -> {
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), Login.class);
            startActivity(intent);
        });

        fabFA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Editprofile.class));
            }
        });

        occupationTxtView = view.findViewById(R.id.occupation_textview);
        nameTxtView = view.findViewById(R.id.tvName);
        nameTV = view.findViewById(R.id.name_textview);
        emailTxtView = view.findViewById(R.id.email_textview);
        streetTxtView = view.findViewById(R.id.home_textview);
        phoneTxtView = view.findViewById(R.id.phone_textview);
        imgProfile = view.findViewById(R.id.user_imageview);

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
                            String telefonoUsuario = (String) data.get("Telefono");
                            String direccionUsuario = (String) data.get("Direccion");
                            String profileImageUrl = document.getString("profileImageUrl");
                            if (profileImageUrl != null) {
                                Glide.with(Perfil.this)
                                        .load(profileImageUrl)
                                        .placeholder(R.drawable.imausera)
                                        .into(imgProfile);
                            }
                            nameTxtView.setText("");
                            nameTV.setText(nombre);
                            occupationTxtView.setText(nombreUsuario +" "+ apellidoUsuario);
                            emailTxtView.setText(correoUsuario);
                            streetTxtView.setText(direccionUsuario);
                            phoneTxtView.setText(telefonoUsuario);
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