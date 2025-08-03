package com.example.lightappcollege;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class RaspAlarms extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rasp_alarms);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_today) {
                    Intent intent = new Intent(RaspAlarms.this, Today.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;

                } else if (itemId == R.id.nav_tomorrow) {
                    // TODO: Запустить новую Activity для расписания на завтра
                    Intent intent = new Intent( RaspAlarms.this, NextDay.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();

                    return true;
                } else if (itemId == R.id.nav_info) {

                    Intent intent = new Intent(RaspAlarms.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    // Устанавливаем правильный выбранный пункт в меню
                    return true;
                }else if (itemId == R.id.nav_alarms) {

                    return true;

                }
                return false;
            }
        });

        // Устанавливаем выбранным по умолчанию пункт "Сегодня" и соответствующий заголовок
        bottomNavigationView.setSelectedItemId(R.id.nav_alarms);





    }
}