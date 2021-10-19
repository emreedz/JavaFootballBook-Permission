package com.emre.footballersbook;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.ByteArrayOutputStream;

public class FootballersDetail extends AppCompatActivity {

    private ActivityFootballersDetailBinding binding;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;

    SQLiteDatabase database;
    Bitmap selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFootballersDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        registerLauncher();

        database = this.openOrCreateDatabase("Footballers", MODE_PRIVATE, null);

        Intent intent = getIntent();
        String info = intent.getStringExtra("info");

        if (info.equals("new")) {
            //new footballers
            binding.nameText.setText("");
            binding.countryText.setText("");
            binding.clubText.setText("");
            binding.imageView.setImageResource(R.drawable.select);
            binding.savebutton.setVisibility(View.VISIBLE);

        } else {
            //old footballers
            int footballersId = intent.getIntExtra("footballersid", 0);
            binding.savebutton.setVisibility(View.INVISIBLE);

            try {
                Cursor cursor = database.rawQuery("SELECT * FROM gunners WHERE id = ? ", new String[]
                        {String.valueOf(footballersId)});
                int NameIndex = cursor.getColumnIndex("footballersname");
                int CountryIndex = cursor.getColumnIndex("countryname");
                int ClubIndex = cursor.getColumnIndex("clubname");
                int imageIndex = cursor.getColumnIndex("image");


                while (cursor.moveToNext()) {
                    binding.nameText.setText(cursor.getString(NameIndex));
                    binding.countryText.setText(cursor.getString(CountryIndex));
                    binding.clubText.setText(cursor.getString(ClubIndex));

                    byte[] bytes = cursor.getBlob(imageIndex);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    binding.imageView.setImageBitmap(bitmap);
                }
                cursor.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

    public void save(View view) {
        String name = binding.nameText.getText().toString();
        String country = binding.countryText.getText().toString();
        String club = binding.clubText.getText().toString();

        Bitmap smallImage = makeSmallerImage(selectedImage, 300);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG, 50, baos);
        byte[] byteArray = baos.toByteArray();

        try {

            database.execSQL("CREATE TABLE IF NOT EXISTS gunners(id INTEGER PRIMARY KEY," +
                    "footballersname VARCHAR,countryname VARCHAR,clubname VARCHAR,image BLOB)");

            String sqlString = "INSERT INTO gunners(footballersname,countryname,clubname,image) VALUES(?,?,?,?)";

            SQLiteStatement sqlStatement = database.compileStatement(sqlString);
            sqlStatement.bindString(1, name);
            sqlStatement.bindString(2, country);
            sqlStatement.bindString(3, club);
            sqlStatement.bindBlob(4, byteArray);

            sqlStatement.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(FootballersDetail.this, MainActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }


    public Bitmap makeSmallerImage(Bitmap image, int maxSize) {

        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;

        if (bitmapRatio > 1) {
            //landscape Image
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            //portrait Image
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return image.createScaledBitmap(image, width, height, true);

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
                                    e.printStackTrace();
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