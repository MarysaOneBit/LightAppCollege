package com.example.lightappcollege;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Today extends AppCompatActivity {
    private static final String TAG = "ScheduleParser";
    private TextView statusTextView;
    private TextView screenTitleTextView; // Объявляем TextView для заголовка
    private EditText groupEditText;
    private Button parseButton;
    private ProgressBar progressBar;
    private RecyclerView recyclerViewSchedule;
    private LessonAdapter lessonAdapter;
    private BottomNavigationView bottomNavigationView;

    private static final String PREFS_NAME = "SchedulePrefs";
    private static final String KEY_GROUP_NUMBER = "groupNumber";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);

        statusTextView = findViewById(R.id.statusTextView);
        screenTitleTextView = findViewById(R.id.screenTitleTextView); // Инициализируем TextView для заголовка
        groupEditText = findViewById(R.id.groupEditText);
        parseButton = findViewById(R.id.parseButton);
        progressBar = findViewById(R.id.progressBar);
        recyclerViewSchedule = findViewById(R.id.recyclerViewSchedule);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        recyclerViewSchedule.setLayoutManager(new LinearLayoutManager(this));
        lessonAdapter = new LessonAdapter(new ArrayList<>());
        recyclerViewSchedule.setAdapter(lessonAdapter);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_today) {


                    return true;
                } else if (itemId == R.id.nav_tomorrow) {
                    // TODO: Запустить новую Activity для расписания на завтра
                    Intent intent = new Intent( Today.this, NextDay.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();

                    return true;
                } else if (itemId == R.id.nav_info) {

                    Intent intent = new Intent(Today.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    // Устанавливаем правильный выбранный пункт в меню
                    return true;
                }
                return false;
            }
        });

        // Устанавливаем выбранным по умолчанию пункт "Сегодня" и соответствующий заголовок
        bottomNavigationView.setSelectedItemId(R.id.nav_today);
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        screenTitleTextView.setText("Расписание на "+currentDate); // Устанавливаем текст заголовка при старте

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        final String savedGroup = prefs.getString(KEY_GROUP_NUMBER, "");

        if (!savedGroup.isEmpty()) {
            groupEditText.setText(savedGroup);
            statusTextView.setText("Загрузка расписания...");
            lessonAdapter.setLessons(new ArrayList<>());
            new ParseScheduleTask().execute("https://volcollege.ru/rasp/today/today.html", savedGroup);
        } else {
            statusTextView.setText("Введите группу и нажмите кнопку");
        }

        parseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String group = groupEditText.getText().toString().trim();
                if (!group.isEmpty()) {
                    statusTextView.setText("Загрузка расписания...");
                    lessonAdapter.setLessons(new ArrayList<>());
                    new ParseScheduleTask().execute("https://volcollege.ru/rasp/today/today.html", group);
                } else {
                    statusTextView.setText("Пожалуйста, введите номер группы.");
                }
            }
        });
    }

    private class ParseScheduleTask extends AsyncTask<String, Void, List<Lesson>> {
        private String currentGroupNameDisplayed = "";
        private String targetGroupInput = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            parseButton.setEnabled(false);
        }

        @Override
        protected List<Lesson> doInBackground(String... params) {
            String url = params[0];
            targetGroupInput = params[1];
            List<Lesson> parsedLessons = new ArrayList<>();

            Log.d(TAG, "Начинаем doInBackground. URL: " + url + ", Группа: " + targetGroupInput);

            try {
                Log.d(TAG, "Попытка подключения к Jsoup...");
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/533.36")
                        .timeout(20000)
                        .get();
                Log.d(TAG, "Jsoup подключился успешно. Начинаем парсинг...");

                Elements allTables = doc.select("table");
                Log.d(TAG, "Найдено ВСЕХ таблиц: " + allTables.size());

                boolean groupFoundAndStartedParsing = false;

                for (Element table : allTables) {
                    Elements rows = table.select("tr");

                    for (Element row : rows) {
                        Elements groupHeaderTds = row.select("td.R6C0[colspan=5]");
                        if (!groupHeaderTds.isEmpty()) {
                            String currentGroupName = groupHeaderTds.first().text().trim();
                            Log.d(TAG, "Обнаружен заголовок группы: " + currentGroupName);

                            if (groupFoundAndStartedParsing) {
                                Log.d(TAG, "Обнаружен следующий заголовок группы. Завершаем парсинг для целевой группы.");
                                return parsedLessons;
                            }

                            if (currentGroupName.equalsIgnoreCase(targetGroupInput) ||
                                    currentGroupName.equalsIgnoreCase(targetGroupInput + "-то") ||
                                    currentGroupName.equalsIgnoreCase(targetGroupInput + "-тп") ||
                                    currentGroupName.equalsIgnoreCase(targetGroupInput + "-бд") ||
                                    currentGroupName.equalsIgnoreCase(targetGroupInput + "-пк") ||
                                    currentGroupName.equalsIgnoreCase(targetGroupInput + "-эл")) {

                                currentGroupNameDisplayed = currentGroupName;
                                groupFoundAndStartedParsing = true;
                                Log.d(TAG, "Целевая группа " + targetGroupInput + " найдена! Начинаем сбор пар.");
                            }
                            continue;
                        }

                        if (groupFoundAndStartedParsing && row.hasClass("R7")) {
                            Elements cols = row.select("td.R7C0, td.R7C1, td.R7C2");

                            if (cols.size() >= 5) {
                                String lessonNumber = cols.get(0).text().trim();
                                String subgroup = cols.get(1).text().trim();
                                String lessonName = cols.get(2).text().trim();
                                String teacher = cols.get(3).text().trim();
                                String cabinet = cols.get(4).text().trim();

                                if (lessonNumber.matches("\\d+")) {
                                    Lesson lesson = new Lesson(lessonNumber, lessonName, teacher, cabinet);
                                    parsedLessons.add(lesson);
                                    Log.d(TAG, "Добавлена пара: " + lessonName);
                                }
                            }
                        }
                    }
                }

                if (!groupFoundAndStartedParsing) {
                    Log.d(TAG, "Расписание для группы " + targetGroupInput + " не найдено.");
                    return new ArrayList<>();
                }

            } catch (IOException e) {
                Log.e(TAG, "Ошибка при парсинге (IOException): " + e.getMessage(), e);
                return null;
            } catch (Exception e) {
                Log.e(TAG, "Неожиданная ошибка: " + e.getMessage(), e);
                return null;
            }
            return parsedLessons;
        }

        @Override
        protected void onPostExecute(List<Lesson> result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            parseButton.setEnabled(true);

            if (result == null) {
                statusTextView.setText("Произошла ошибка при загрузке или парсинге расписания.");
            } else if (result.isEmpty()) {
                statusTextView.setText("Расписание для группы " + groupEditText.getText().toString().trim() + " не найдено.");
            } else {
                statusTextView.setText("Расписание для " + currentGroupNameDisplayed + ":");
                lessonAdapter.setLessons(result);
                recyclerViewSchedule.scrollToPosition(0);

                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(KEY_GROUP_NUMBER, targetGroupInput);
                editor.apply();
                Log.d(TAG, "Номер группы '" + targetGroupInput + "' сохранен.");
            }
        }
    }
}