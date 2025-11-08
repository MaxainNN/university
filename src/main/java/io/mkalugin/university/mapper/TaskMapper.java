package io.mkalugin.university.mapper;

import io.mkalugin.university.dto.TaskCreateRequest;
import io.mkalugin.university.entity.Task;
import io.mkalugin.university.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Маппер для преобразования
 * DTO с запросом на создание задачи {@link TaskCreateRequest}
 * в сущность задачи {@link Task}.
 */
@Mapper(componentModel = "spring")
public interface TaskMapper {

    /**
     * Преобразование DTO модели
     * запроса на создание задачи в
     * сущность задачи.
     *
     * @param dto модель запроса
     * @param user сущность пользователя
     * @return сущность задачи
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "completedAt", ignore = true)
    @Mapping(target = "user", source = "user")
    Task toEntity(TaskCreateRequest dto, User user);
}
