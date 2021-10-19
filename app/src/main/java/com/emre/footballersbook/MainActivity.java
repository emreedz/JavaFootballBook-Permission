package com.emre.footballersbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.emre.footballersbook.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    ArrayList<Football> footballArrayList;
    FootballAdapter footballAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        footballArrayList = new ArrayList<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        footballAdapter = new FootballAdapter(footballArrayList);
        binding.recyclerView.setAdapter(footballAdapter);

        getData();

    }


    private void getData() {
        try {
            SQLiteDatabase sqlDatabase = this.openOrCreateDatabase("Footballers", MODE_PRIVATE, null);
            Cursor cursor = sqlDatabase.rawQuery("SELECT * FROM gunners", null);
            int nameIndex = cursor.getColumnIndex("footballersname");
            int idIndex = cursor.getColumnIndex("id");

            while (cursor.moveToNext()) {
                String name = cursor.getString(nameIndex);
                int id = cursor.getInt(idIndex);
                Football footballers = new Football(name, id);
                footballArrayList.add(footballers);
            }

            footballAdapter.notifyDataSetChanged();
            cursor.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.footballers, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.add_footballers) {
            Intent intent = new Intent(MainActivity.this, FootballersDetail.class);
            intent.putExtra("info", "new");

            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }
}