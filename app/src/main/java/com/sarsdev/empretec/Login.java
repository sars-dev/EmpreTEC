package com.sarsdev.empretec;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.Task;

public class Login extends AppCompatActivity {

    Button btnLogin;
    TextView tvRegister, lblForgot;
    EditText emailET, passET;
    private FirebaseAuth mAuth;
    private boolean isPasswordVisible = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btn_Login);
        tvRegister = findViewById(R.id.lblRegister);
        lblForgot = findViewById(R.id.lblForgot);
        emailET = findViewById(R.id.emailET);
        passET = findViewById(R.id.passET);

        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRegister();
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

        lblForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText resetMail = new EditText(v.getContext());
                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("¿Restablecer contraseña?");
                passwordResetDialog.setMessage("Ingrese su email para recibir el link de restablecimiento.");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // extract the email and send reset link
                        String mail = resetMail.getText().toString();
                        mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Login.this, "El link fue enviado a tu email.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Login.this, "Error ! El link no ha sido enviado." + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });

                passwordResetDialog.setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // close the dialog
                    }
                });

                passwordResetDialog.create().show();

            }
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

    public void openRegister(){
        Intent intent = new Intent(Login.this, Register.class);
        startActivity(intent);
    }

    public void userLogin(){
        String mail = emailET.getText().toString().trim();
        String password = passET.getText().toString().trim();

        if (TextUtils.isEmpty(mail)){
            emailET.setError("Ingrese un correo");
            emailET.requestFocus();
        }else if (TextUtils.isEmpty(password)){
            Toast.makeText(Login.this, "Ingrese una contraseña", Toast.LENGTH_SHORT).show();
            passET.requestFocus();
        }else{
            mAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(Login.this, "Bienvenid@", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Login.this, Dash.class));
                    }else {
                        Log.w("TAG", "Error:", task.getException());
                    }
                }
            });
        }
    }


}