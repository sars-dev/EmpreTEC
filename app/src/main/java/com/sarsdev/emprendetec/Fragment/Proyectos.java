package com.sarsdev.emprendetec.Fragment;

import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import android.app.AlertDialog;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sarsdev.emprendetec.Dash;
import com.sarsdev.emprendetec.Model.DataClass;
import com.sarsdev.emprendetec.R;

public class Proyectos extends Fragment {

    View view;
    ImageView uploadImage;
    Button saveButton;
    EditText uploadTopic, uploadDesc, uploadLang, uploadLang2;
    String imageURL;
    Uri uri;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_IMAGE_GALLERY = 3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_proyectos, container, false);

        uploadImage = view.findViewById(R.id.uploadImage);
        uploadDesc = view.findViewById(R.id.uploadDesc);
        uploadTopic = view.findViewById(R.id.uploadTopic);
        uploadLang = view.findViewById(R.id.uploadLang);
        uploadLang2 = view.findViewById(R.id.uploadLang2);
        saveButton = view.findViewById(R.id.saveButton);

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] options = {"Cámara", "Galería"};
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
                builder.setTitle("Seleccionar foto desde")
                        .setItems(options, (dialog, which) -> {
                            if (which == 0) {
                                openCam();
                            } else if (which == 1) {
                                openGal();
                            }
                        }).show();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });

        return view;

    }

    public void openCam(){
        camaraLauncher.launch(new Intent(MediaStore.ACTION_IMAGE_CAPTURE));
    }

    public void openGal(){
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
                        uploadImage.setImageURI(uri);
                    } else {
                        Toast.makeText(getActivity(), "Imagen No Seleccionada", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    ActivityResultLauncher<Intent> camaraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Bundle bundle = result.getData().getExtras();
                        Bitmap bitmap = (Bitmap) bundle.get("data");
                        uri = getImageUri(bitmap);
                        uploadImage.setImageBitmap(bitmap);
                    } else {
                        Toast.makeText(getActivity(), "Imagen No Seleccionada", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    public void saveData() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Images")
                .child(uri.getLastPathSegment());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();
        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete()) ;
                Uri urlImage = uriTask.getResult();
                imageURL = urlImage.toString();
                uploadData();
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
            }
        });
    }

    public void uploadData() {
        String title = uploadTopic.getText().toString();
        String desc = uploadDesc.getText().toString();
        String area = uploadLang.getText().toString();
        String area2 = uploadLang2.getText().toString();
        DataClass dataClass = new DataClass(title, desc, area, area2, imageURL);
        FirebaseDatabase.getInstance().getReference("Proyectos").child(title)
                .setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Creado", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), Dash.class);
                            startActivity(intent);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private Uri getImageUri(Bitmap bitmap) {
        String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, "Image Title", null);
        return Uri.parse(path);
    }

}