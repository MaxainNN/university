package io.mkalugin.university.service.scheduler;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * Сервис для отслеживания активности пользователей в системе.
 * <p>
 * Хранит информацию о времени последней активности каждого пользователя
 * (например, при каждом запросе через {@link ActivityInterceptor}).
 * Используется для определения, какие пользователи были активны
 * за определённый период времени.
 * </p>
 */
@Service
public class UserActivityService {

    private final ConcurrentMap<String, Instant> lastActive = new ConcurrentHashMap<>();

    /**
     * Обновляет время последней активности пользователя.
     *
     * @param username имя пользователя
     * @param time     момент времени последней активности
     */
    public void updateLastActive(String username, Instant time) {
        lastActive.put(username, time);
    }

    /**
     * Возвращает список пользователей, которые были активны
     * после указанного момента времени.
     *
     * @param since момент времени, начиная с которого проверяется активность
     * @return список имён активных пользователей
     */
    public List<String> getUsersActiveSince(Instant since) {
        return lastActive.entrySet().stream()
                .filter(e -> e.getValue().isAfter(since))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
