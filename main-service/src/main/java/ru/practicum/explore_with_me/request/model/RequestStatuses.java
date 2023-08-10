package ru.practicum.explore_with_me.request.model;

public enum RequestStatuses {
    PENDING,
    CONFIRMED,
    REJECTED, //Отклонено владельцем события
    CANCELED //Отменено пользователем
}