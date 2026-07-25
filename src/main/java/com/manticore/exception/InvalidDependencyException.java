package com.manticore.exception;

/**
 * Исключение, которое сигнализирует о том, что зависимость нарушает правила графа:
 *
 * <ul>
 *      <li>Создание зависимости на тот же узел: A -> A</li>
 *      <li>Создание циклической зависимости: A -> B -> A</li>
 * </ul>
 *
 * @author Linempy
 * @since 24.07.2026
 */
public class InvalidDependencyException extends RuntimeException {

    public InvalidDependencyException(String message) {
        super(message);
    }
}
