package io.mkalugin.university.exception;

import io.mkalugin.university.enums.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * Исключение, выбрасываемое, если задача не найдена.
 */
public class TaskNotFoundException extends BaseApiException {

    public TaskNotFoundException() {
        super(ErrorCode.TASK_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
}
