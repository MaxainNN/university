package io.mkalugin.university.enums;

/**
 * Перечисление для статуса задачи.
 */
public enum TaskStatus {

    /**
     * В обработке
     */
    PENDING,

    /**
     * В процессе исполнения
     */
    IN_PROGRESS,

    /**
     * Завершена
     */
    COMPLETED,

    /**
     * Отменена
     */
    CANCELLED
}
