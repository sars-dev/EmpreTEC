package com.sarsdev.empretec.Custom;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.sarsdev.empretec.Dash;
import com.sarsdev.empretec.R;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Editprofile extends AppCompatActivity {

    CircleImageView imgProfile;
    Button saveEdit;
    private FirebaseAuth mAuth;
    private FirebaseUser useract;
    EditText nameET, lastET, emailET, phoneET, streetET;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference loginLogsRef = db.collection("users");
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    private DocumentReference userRef;
    Uri uri;
    private Uri imageURL;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_IMAGE_GALLERY = 3;

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

        saveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateInfo();
            }
        });

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

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
                            /*String profileImageUrl = document.getString("profileImageUrl");
                            if (profileImageUrl != null) {
                                Glide.with(Editprofile.this)
                                        .load(profileImageUrl)
                                        .placeholder(R.drawable.imausera)
                                        .into(imgProfile);
                            }*/
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

    private void openGallery() {
        // Abre la galería para permitir al usuario seleccionar una imagen
        Intent photoPicker = new Intent(Intent.ACTION_PICK);
        photoPicker.setType("image/*");
        activityResultLauncher.launch(photoPicker);
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        uri = data.getData();
                        imgProfile.setImageURI(uri);
                    } else {
                        Toast.makeText(Editprofile.this, "Imagen No Seleccionada", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void updateInfo(){

        String usersId = firebaseUser.getUid();

        userRef = db.collection("users").document(usersId);

        String nuevoNombre = nameET.getText().toString();
        String nuevoApellido = lastET.getText().toString();
        String nuevoTelefono = phoneET.getText().toString();
        String nuevoDireccion = streetET.getText().toString();

        // Crea un mapa con los datos actualizados
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("Nombre", nuevoNombre);
        updatedData.put("Apellido", nuevoApellido);
        updatedData.put("Telefono", nuevoTelefono);
        updatedData.put("Direccion", nuevoDireccion);

        // Actualiza los datos en Firestore
        userRef.set(updatedData, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //uploadImageToFirebaseStorage();
                            Toast.makeText(Editprofile.this, "Datos Actualizados", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Editprofile.this, Dash.class));
                        } else {
                            // Manejar el error en caso de que la actualización falle
                            Exception e = task.getException();
                            if (e != null) {
                                Toast.makeText(Editprofile.this, "Error: "+e, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

    }

    private Uri getImageUri(Bitmap bitmap) {
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Image Title", null);
        return Uri.parse(path);
    }

}