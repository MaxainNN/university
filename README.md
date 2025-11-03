## Digital calendar

## Description:
Цифровой календарь с функцией создания задач и заметок.
В проекте реализовано создание пользователя с шифрованием пароля и сохранением в БД.

## Stack:
- `Spring Boot` - Фреймворк для создания Java-приложений.
- `Hibernate` - ORM-фреймворк (Object-Relational Mapping), который связывает Java-классы с таблицами в базе данных.
- `Swagger` - Инструмент для документирования и тестирования REST API.
- `Thymeleaf` - Серверный шаблонизатор (HTML template engine) для Spring MVC.
- `Postgres` - Реляционная база данных проекта.
- `Elasticsearch` - Поисковый движок, используемый для быстрого поиска и фильтрации данных.
- `Redis` - Система кеширования в памяти, ускоряющая доступ к часто используемым данным.

## Run containers:
```bash
    docker-compose up -d
```

## Command to build:
```bash
  mvn clean install
```

## Links:

- http://localhost:8080/ - главная страница
- http://localhost:8080/swagger-ui/index.html - Swagger

## Example:

