package com.example.lightappcollege;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {

    private List<Lesson> lessonList;

    public LessonAdapter(List<Lesson> lessonList) {
        this.lessonList = lessonList;
    }

    // Обновляет данные в адаптере
    public void setLessons(List<Lesson> newLessonList) {
        this.lessonList = newLessonList;
        notifyDataSetChanged(); // Уведомляем RecyclerView об изменении данных
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Создаем и возвращаем новый ViewHolder, "надувая" макет item_lesson_card
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lesson_card, parent, false);
        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        // Привязываем данные из объекта Lesson к элементам View в ViewHolder
        Lesson lesson = lessonList.get(position);
        holder.lessonNumberTextView.setText("Пара №" + lesson.getLessonNumber());
        holder.lessonNameTextView.setText("Название: " + lesson.getLessonName());
        holder.teacherTextView.setText("Преподаватель: " + lesson.getTeacher());
        holder.cabinetTextView.setText("Кабинет: " + lesson.getCabinet());
    }

    @Override
    public int getItemCount() {
        // Возвращает общее количество элементов в списке
        return lessonList.size();
    }

    // ViewHolder - класс, который хранит ссылки на View-элементы одной плитки
    static class LessonViewHolder extends RecyclerView.ViewHolder {
        TextView lessonNumberTextView;
        TextView lessonNameTextView;
        TextView teacherTextView;
        TextView cabinetTextView;

        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            lessonNumberTextView = itemView.findViewById(R.id.lessonNumberTextView);
            lessonNameTextView = itemView.findViewById(R.id.lessonNameTextView);
            teacherTextView = itemView.findViewById(R.id.teacherTextView);
            cabinetTextView = itemView.findViewById(R.id.cabinetTextView);
        }
    }
}