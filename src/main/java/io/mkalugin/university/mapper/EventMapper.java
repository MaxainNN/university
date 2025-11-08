package io.mkalugin.university.mapper;

import io.mkalugin.university.dto.EventCreateRequest;
import io.mkalugin.university.entity.Event;
import io.mkalugin.university.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Маппер для преобразования
 * DTO с запросом на создание события {@link EventCreateRequest}
 * в сущность события {@link Event}.
 */
@Mapper(componentModel = "spring")
public interface EventMapper {

    /**
     * Преобразование DTO модели
     * запроса на создание события в
     * сущность события.
     *
     * @param dto модель запроса
     * @param user сущность пользователя
     * @return сущность события
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "reminderSent", ignore = true)
    Event toEntity(EventCreateRequest dto, User user);
}
