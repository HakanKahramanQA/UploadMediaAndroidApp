package com.android.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.Settings;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<Image> arrayList = new ArrayList<>();
    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
            result -> {
                if (result) {
                    getImages();
                    getDoc();
                    getMovies();
                }
            });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setHasFixedSize(true);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            check30AndAfter();
        }
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }else if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncher.launch(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
        }
        else{
            getImages();
            getDoc();
            getMovies();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void check30AndAfter() {
        if (!Environment.isExternalStorageManager()) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                startActivityForResult(intent, 200);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 200);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            if (30 >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // Permission for storage access successful!
                    // Read your files now
                    getImages();
                    getDoc();
                    getMovies();
                } else {
                    // Allow permission for storage access!
                }
            }
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        getImages();
        getDoc();
        getMovies();
    }

    private void getImages(){
        arrayList.clear();
        String filePath = "/storage/emulated/0/Pictures";
        File file = new File(filePath);
        File[] files = file.listFiles();
        if (files != null) {
            for (File file1 : files) {
                if (file1.getPath().toLowerCase().endsWith(".jpg") || file1.getPath().toLowerCase().endsWith(".png") || file1.getPath().toLowerCase().endsWith(".gif") || file1.getPath().toLowerCase().endsWith(".bmp") || file1.getPath().toLowerCase().endsWith(".jpeg")) {
                    arrayList.add(new Image(file1.getName(), file1.getPath(), file1.length()));
                }
            }
        }
    }

    private void getDoc(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            File  file1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString());
            File[] files= null;
            files = file1.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getPath().toLowerCase().endsWith(".pdf") || file.getPath().toLowerCase().endsWith(".doc") || file.getPath().toLowerCase().endsWith(".xls") || file.getPath().toLowerCase().endsWith(".xlsx") || file.getPath().toLowerCase().endsWith(".docx") || file.getPath().toLowerCase().endsWith(".csv") || file.getPath().toLowerCase().endsWith(".txt")) {
                        arrayList.add(new Image(file.getName(), file.getPath(), file.length()));
                    }
                }
            }
        }
        else{
            String filePath = "/storage/emulated/0/Download";
            File file = new File(filePath);
            File[] files = file.listFiles();
            if (files != null) {
                for (File file1 : files) {
                    if (file1.getPath().toLowerCase().endsWith(".pdf") || file1.getPath().toLowerCase().endsWith(".doc") || file1.getPath().toLowerCase().endsWith(".xls") || file1.getPath().toLowerCase().endsWith(".xlsx") || file1.getPath().toLowerCase().endsWith(".docx") || file1.getPath().toLowerCase().endsWith(".csv") || file1.getPath().toLowerCase().endsWith(".txt")) {
                        arrayList.add(new Image(file1.getName(), file1.getPath(), file1.length()));
                    }
                }
            }
        }

    }

    private void getMovies(){
        String filePath = "/storage/emulated/0/Movies";
        File file = new File(filePath);
        File[] files = file.listFiles();
        if (files != null) {
            for (File file1 : files) {
                if (file1.getPath().endsWith(".mp4") || file1.getPath().toLowerCase().endsWith(".mov") || file1.getPath().toLowerCase().endsWith(".3gp")){
                    arrayList.add(new Image(file1.getName(), file1.getPath(), file1.length()));
                }
            }
        }
        ImageAdapter adapter = new ImageAdapter(MainActivity.this, arrayList);
        recyclerView.setAdapter(adapter);

    }
}