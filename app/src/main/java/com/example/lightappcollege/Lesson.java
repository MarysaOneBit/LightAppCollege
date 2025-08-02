package com.example.lightappcollege; // Убедитесь, что пакет правильный

public class Lesson {
    private String lessonNumber; // Номер пары (например, "1")
    private String lessonName;   // Название предмета (например, "Математика")
    private String teacher;      // Имя преподавателя (например, "Иванов И.И.")
    private String cabinet;      // Номер кабинета (например, "305")

    // Конструктор: используется для создания нового объекта Lesson
    public Lesson(String lessonNumber, String lessonName, String teacher, String cabinet) {
        this.lessonNumber = lessonNumber;
        this.lessonName = lessonName;
        this.teacher = teacher;
        this.cabinet = cabinet;
    }

    // --- Геттеры ---
    // Метод для получения номера пары
    public String getLessonNumber() {
        return lessonNumber;
    }

    // Метод для получения названия предмета
    public String getLessonName() {
        return lessonName;
    }

    // Метод для получения имени преподавателя
    public String getTeacher() {
        return teacher;
    }

    // Метод для получения номера кабинета
    public String getCabinet() {
        return cabinet;
    }

    // Опционально: метод для установки номера пары (сеттер)
    // public void setLessonNumber(String lessonNumber) {
    //     this.lessonNumber = lessonNumber;
    // }

    // Опционально: метод для установки названия предмета (сеттер)
    // public void setLessonName(String lessonName) {
    //     this.lessonName = lessonName;
    // }

    // ... и так далее для других полей
}