package com.sarsdev.empretec;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Editprofile extends AppCompatActivity {

    CircleImageView imgProfile;
    Button saveEdit;
    private FirebaseAuth mAuth;
    private FirebaseUser useract;
    private Uri imageURL;
    EditText nameET, lastET, emailET, phoneET, streetET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        mAuth = FirebaseAuth.getInstance();
        useract = FirebaseAuth.getInstance().getCurrentUser();

        nameET = findViewById(R.id.nameET);
        lastET = findViewById(R.id.lastET);
        emailET = findViewById(R.id.emailET);
        phoneET = findViewById(R.id.phoneET);
        streetET = findViewById(R.id.streetET);
        saveEdit = findViewById(R.id.btn_Edit);
        imgProfile = findViewById(R.id.imgProfile);

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
                            nameET.setText(nombre);
                            lastET.setText(apellidoUsuario);
                            emailET.setText(correoUsuario);
                            phoneET.setText(telefonoUsuario);
                            streetET.setText(direccionUsuario);
                        } else {
                            Toast.makeText(Editprofile.this, "No Hay Datos", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d("Firestore", "Error getting user data: " + task.getException());
                    }
                }
            });
        } else {
            Toast.makeText(this, "No Hay Usuario", Toast.LENGTH_SHORT).show();
        }

    }
}