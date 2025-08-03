package com.example.lightappcollege;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private TextView screenTitleTextView; // Для заголовка

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        screenTitleTextView = findViewById(R.id.screenTitleTextView);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

// Устанавливаем заголовок для этой Activity
        if (screenTitleTextView != null) {
            screenTitleTextView.setText("Информация о разработчике");
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_today) {
                    Intent intent = new Intent(MainActivity.this, Today.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();

                    return true;
                } else if (itemId == R.id.nav_tomorrow) {

                    Intent intent = new Intent( MainActivity.this, NextDay.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();

                    return true;
                } else if (itemId == R.id.nav_info) {

                    // Устанавливаем правильный выбранный пункт в меню
                    return true;
                }else if (itemId == R.id.nav_alarms) {

                    Intent intent = new Intent(MainActivity.this, RaspAlarms.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }
                return false;

            }
        });

        // Устанавливаем выбранным по умолчанию пункт "Сегодня" и соответствующий заголовок
        bottomNavigationView.setSelectedItemId(R.id.nav_info);
// Устанавливаем выбранным по умолчанию пункт "Информация" в этой Activity
        bottomNavigationView.setSelectedItemId(R.id.nav_info);

        // --- Обработка кликов по ссылкам ---
        TextView telegramLink = findViewById(R.id.telegramLink);
        telegramLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTelegramProfile("@Marysa151");
            }
        });

        TextView vkLink = findViewById(R.id.vkLink);
        vkLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebPage("https://vk.com/dserdtsev1");
            }
        });
    }
    // Метод для открытия Telegram-профиля
    private void openTelegramProfile(String username) {
        try {
            Intent telegramIntent = new Intent(Intent.ACTION_VIEW);
            // Используем Uri для Telegram. "tg://resolve?domain=" или "tg://msg?text="
            telegramIntent.setData(Uri.parse("tg://resolve?domain=" + username.replace("@", "")));
            startActivity(telegramIntent);
        } catch (Exception e) {
            // Если Telegram не установлен или произошла ошибка, открываем в браузере
            Toast.makeText(this, "Не удалось открыть Telegram. Открываю ссылку в браузере.", Toast.LENGTH_LONG).show();
            openWebPage("https://t.me/" + username.replace("@", ""));
        }
    }

    // Метод для открытия веб-страницы
    private void openWebPage(String url) {
        try {
            Uri webpage = Uri.parse(url);
            Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
            startActivity(webIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Не удалось открыть ссылку: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}