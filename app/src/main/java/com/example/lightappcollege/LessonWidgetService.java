package com.example.lightappcollege;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LessonWidgetService extends RemoteViewsService {

    private static final String PREFS_NAME = "SchedulePrefs";
    private static final String KEY_GROUP_NUMBER = "groupNumber";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new LessonWidgetFactory(this.getApplicationContext(), intent);
    }

    class LessonWidgetFactory implements RemoteViewsFactory {
        private Context mContext;
        private List<Lesson> mLessons = new ArrayList<>();
        private String targetGroup = "";

        public LessonWidgetFactory(Context context, Intent intent) {
            mContext = context;
        }

        @Override
        public void onCreate() {
            // Инициализация данных при создании фабрики.
            // Здесь мы будем загружать номер группы.
            SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            targetGroup = prefs.getString(KEY_GROUP_NUMBER, "");
            // Данные расписания будут загружены в onDataSetChanged()
        }

        @Override
        public void onDataSetChanged() {
            // Вызывается, когда данные виджета изменились (например, при обновлении).
            // Здесь мы парсим расписание в фоновом режиме.
            if (targetGroup.isEmpty()) {
                mLessons.clear(); // Если нет группы, очищаем список
                return;
            }

            // Выполняем парсинг в отдельном потоке, чтобы не блокировать UI.
            // Внимание: onDataSetChanged() вызывается в главном потоке,
            // поэтому здесь нельзя выполнять сетевые запросы напрямую!
            // Для реального приложения здесь нужно использовать WorkManager или Handler/AsyncTask.
            // В этом примере для простоты, если парсинг синхронный, он должен быть быстрым.
            // Для Production-приложения это нужно перенести в фоновую задачу.
            mLessons.clear(); // Очищаем старые данные
            try {
                // ВАЖНО: В реальном приложении этот парсинг должен выполняться асинхронно
                // (например, через AsyncTask, WorkManager или отдельный поток),
                // а затем notifyDataSetChanged() должен быть вызван для этой фабрики.
                // Здесь для примера он выполняется синхронно.
                Document doc = Jsoup.connect("https://volcollege.ru/rasp/today/today.html")
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/533.36")
                        .timeout(10000)
                        .get();

                Elements allTables = doc.select("table");
                boolean groupFoundAndStartedParsing = false;

                for (Element table : allTables) {
                    Elements rows = table.select("tr");

                    for (Element row : rows) {
                        Elements groupHeaderTds = row.select("td.R6C0[colspan=5]");
                        if (!groupHeaderTds.isEmpty()) {
                            String currentGroupName = groupHeaderTds.first().text().trim();
                            if (groupFoundAndStartedParsing) {
                                break; // Нашли следующую группу, выходим
                            }
                            if (currentGroupName.equalsIgnoreCase(targetGroup) ||
                                    currentGroupName.equalsIgnoreCase(targetGroup + "-то") ||
                                    currentGroupName.equalsIgnoreCase(targetGroup + "-тп") ||
                                    currentGroupName.equalsIgnoreCase(targetGroup + "-бд") ||
                                    currentGroupName.equalsIgnoreCase(targetGroup + "-пк") ||
                                    currentGroupName.equalsIgnoreCase(targetGroup + "-эл")) {
                                groupFoundAndStartedParsing = true;
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
                                    mLessons.add(lesson);
                                }
                            }
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                mLessons.clear(); // Очистить список при ошибке
            }
        }

        @Override
        public void onDestroy() {
            // Очистка ресурсов при уничтожении фабрики.
            mLessons.clear();
        }

        @Override
        public int getCount() {
            return mLessons.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            // Создает RemoteViews для каждого элемента списка.
            if (position < 0 || position >= mLessons.size()) {
                return null;
            }

            Lesson lesson = mLessons.get(position);
            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);

            // Заполняем элементы макета данными из объекта Lesson
            views.setTextViewText(R.id.item_lesson_number_name, lesson.getLessonNumber() + ". " + lesson.getLessonName());
            views.setTextViewText(R.id.item_lesson_teacher, "Преподаватель: " + lesson.getTeacher());
            views.setTextViewText(R.id.item_lesson_cabinet, "Кабинет: " + lesson.getCabinet());

            // Если вы хотите сделать каждый элемент списка кликабельным
            // Intent fillInIntent = new Intent();
            // views.setOnClickFillInIntent(R.id.root_layout_of_item, fillInIntent); // Нужен ID корневого layout в widget_list_item.xml

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            // Отображается, пока данные загружаются.
            return new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item_loading);
            // Создайте этот макет: просто TextView "Загрузка..."
        }

        @Override
        public int getViewTypeCount() {
            // Количество различных типов элементов списка (у нас один)
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}