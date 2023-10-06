package com.sarsdev.empretec;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class Register extends AppCompatActivity {

    Button btnLogin;
    CircleImageView imgProfile;
    TextView tvLogin;
    EditText nameET, lastET, emailET, passET;
    RadioGroup radioGroup;
    RadioButton selectedRadioButton;
    Uri uri;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private FirebaseFirestore db;
    private boolean isPasswordVisible = false;
    public static final String TAG = "TAG";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameET = findViewById(R.id.nameET);
        lastET = findViewById(R.id.lastET);
        emailET = findViewById(R.id.emailET);
        passET = findViewById(R.id.passET);
        radioGroup = findViewById(R.id.radioGroup);
        imgProfile = findViewById(R.id.imgProfile);
        tvLogin = findViewById(R.id.lblLogin);
        btnLogin = findViewById(R.id.btn_Login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        imgProfile.setOnClickListener(view -> openGallery());

        btnLogin.setOnClickListener(v -> createUser());

        tvLogin.setOnClickListener(view -> openLogin());

        //Metodo para mostrar y ocultar contraseña
        passET.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (passET.getRight() - passET.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    togglePasswordVisibility();
                    return true;
                }
            }
            return false;
        });

    }

    public void openLogin(){
        Intent intent = new Intent(Register.this, Login.class);
        startActivity(intent);
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Oculta la contraseña
            passET.setTransformationMethod(PasswordTransformationMethod.getInstance());
            isPasswordVisible = false;
        } else {
            // Muestra la contraseña
            passET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            isPasswordVisible = true;
        }
        // Mueve el cursor al final del texto
        passET.setSelection(passET.getText().length());
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
                        Toast.makeText(Register.this, "Imagen No Seleccionada", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    public void createUser() {
        String name = nameET.getText().toString();
        String last = lastET.getText().toString();
        String mail = emailET.getText().toString().trim();
        String password = passET.getText().toString().trim();
        String phone = "";
        String street = "";
        selectedRadioButton = findViewById(radioGroup.getCheckedRadioButtonId());
        String selectedUser = selectedRadioButton.getText().toString();

        if (TextUtils.isEmpty(name)){
            nameET.setError("Ingrese un Nombre");
            nameET.requestFocus();
            return;
        } else if (TextUtils.isEmpty(last)) {
            lastET.setError("Ingrese un Apellido");
            lastET.requestFocus();
            return;
        } else if (mail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            emailET.setError("Correo Inválido");
            emailET.requestFocus();
            return;
        } else if (password.isEmpty() || password.length() < 8 || !Pattern.compile("[0-9]").matcher(password).find()) {
            passET.setError("Contraseña inválida");
            passET.requestFocus();
            return;
        }

        // Crear el usuario en Firebase Authentication
        mAuth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // El usuario se ha registrado correctamente
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    String userID = user.getUid();

                    // Se Obtiene la referencia a la ubicación de Storage donde se guardara la imagen
                    StorageReference storageRef = storage.getReference().child("Profile_Images").child(Objects.requireNonNull(uri.getLastPathSegment()));

                    // Subir la imagen a Storage
                    storageRef.putFile(uri).addOnCompleteListener(storageTask -> {
                        if (storageTask.isSuccessful()) {
                            // Si la carga de la imagen es exitosa, se obtiene la URL de descarga de la imagen
                            storageRef.getDownloadUrl().addOnCompleteListener(uriTask -> {
                                if (uriTask.isSuccessful()) {
                                    Uri urlImage = uriTask.getResult();
                                    String imageURL = urlImage.toString();

                                    // Se crea un nuevo documento para el usuario recién registrado en Firestore
                                    Map<String, Object> userMap = new HashMap<>();
                                    userMap.put("Nombre", name);
                                    userMap.put("Apellido", last);
                                    userMap.put("Correo", mail);
                                    userMap.put("Contraseña", password);
                                    userMap.put("Usuario", selectedUser);
                                    userMap.put("Telefono", phone);
                                    userMap.put("Direccion", street);
                                    userMap.put("profileImageUrl", imageURL);

                                    db.collection("users").document(userID)
                                            .set(userMap)
                                            .addOnCompleteListener(firestoreTask -> {
                                                if (firestoreTask.isSuccessful()) {
                                                    // Verificación de Correo
                                                    user.sendEmailVerification().addOnSuccessListener(unused -> {
                                                        Toast.makeText(Register.this, "Verificación de Email Enviada", Toast.LENGTH_SHORT).show();
                                                    }).addOnFailureListener(e -> {
                                                        Log.d(TAG, "onFailure: Email no enviado " + e.getMessage());
                                                    });

                                                    Toast.makeText(Register.this, "Usuario Registrado", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(Register.this, Login.class));
                                                } else {
                                                    Toast.makeText(Register.this, "Error al guardar datos en Firestore", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    Toast.makeText(Register.this, "Error al obtener la URL de la imagen", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(Register.this, "Error al cargar la imagen a Storage", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(Register.this, "Error al crear el usuario: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}