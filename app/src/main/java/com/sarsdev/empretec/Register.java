package com.sarsdev.empretec;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    Button btnLogin;
    TextView tvLogin;
    EditText nameET, lastET, emailET, passET;
    RadioGroup radioGroup;
    RadioButton selectedRadioButton;
    private String userID;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private boolean isPasswordVisible = false;

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

        btnLogin = findViewById(R.id.btn_Login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });

        tvLogin = findViewById(R.id.lblLogin);

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLogin();
            }
        });

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

    public void openLogin(){
        Intent intent = new Intent(Register.this, Login.class);
        startActivity(intent);
    }

    public void createUser(){

        String name = nameET.getText().toString();
        String last = lastET.getText().toString();
        String mail = emailET.getText().toString().trim();
        String password = passET.getText().toString().trim();
        String phone = "";
        String street = "";
        String img_profile = "";
        selectedRadioButton = findViewById(radioGroup.getCheckedRadioButtonId());
        String selectedUser = selectedRadioButton.getText().toString();

        if (TextUtils.isEmpty(name)){
            nameET.setError("Ingrese un Nombre");
            nameET.requestFocus();
        } else if (mail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            emailET.setError("Correo Invalido");
            return;
        } else {
            emailET.setError(null);
        } if (password.isEmpty() || password.length() < 8) {
            passET.setError("Se necesitan más de 8 caracteres");
            return;
        } else if (!Pattern.compile("[0-9]").matcher(password).find()){
            passET.setError("Al menos un numero");
            return;
        } else {
            passET.setError(null);
        } if (TextUtils.isEmpty(last)){
            lastET.setError("Ingrese un Nombre");
            lastET.requestFocus();
        } else {
            mAuth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                    DocumentReference documentReference = db.collection("users").document(userID);

                    Map<String,Object> user=new HashMap<>();
                    user.put("Nombre", name);
                    user.put("Apellido", last);
                    user.put("Correo", mail);
                    user.put("Contraseña", password);
                    user.put("Usuario", selectedUser);
                    user.put("Telefono", phone);
                    user.put("Direccion", street);
                    user.put("profileImageUrl", img_profile);

                    documentReference.set(user).addOnSuccessListener(unused -> Log.d("TAG", "onSuccess: Datos registrados "+userID));
                    Toast.makeText(Register.this, "Usuario Registrado", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Register.this, Login.class));
                }else {
                    Toast.makeText(Register.this, "Usuario No Registrado "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


}