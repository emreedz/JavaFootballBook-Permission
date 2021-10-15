package com.emre.footballersbook;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.emre.footballersbook.databinding.ActivityFootballersDetailBinding;
import com.google.android.material.snackbar.Snackbar;

public class FootballersDetail extends AppCompatActivity {

    private ActivityFootballersDetailBinding binding;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;

    Bitmap selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFootballersDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        registerLauncher();
    }

    public void save(View view) {


    }

    public void select(View view) {

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(view, "Permission needed for Gallery", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Give Permission", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //request Permission
                                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                            }
                        }).show();
            } else {
                //request Permission
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }

        } else {
            //gallery
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);

        }

    }

    private void registerLauncher() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent intentFromResult = result.getData();
                            if (intentFromResult != null) {
                                Uri imageData = intentFromResult.getData();
                                //binding.imageView.setImageURI(imageData);
                                try {
                                    if (Build.VERSION.SDK_INT >= 28) {
                                        ImageDecoder.Source source = ImageDecoder.createSource(
                                                FootballersDetail.this.getContentResolver(), imageData);
                                        selectedImage = ImageDecoder.decodeBitmap(source);
                                        binding.imageView.setImageBitmap(selectedImage);
                                    } else {
                                        selectedImage = MediaStore.Images.Media.getBitmap(
                                                FootballersDetail.this.getContentResolver(), imageData);
                                        binding.imageView.setImageBitmap(selectedImage);
                                    }

                                } catch (Exception e) {

                                }
                            }
                        }

                    }
                });
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        if (result) {
                            //permission granted
                            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            activityResultLauncher.launch(intentToGallery);
                        } else {
                            //permission denied
                            Toast.makeText(FootballersDetail.this, "Permission Neaded", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}