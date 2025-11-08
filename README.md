## Digital calendar

## Description:
Цифровой календарь с функцией создания задач и заметок.

## Stack:
- `Spring Boot` <img src="images/spring_icon.png" height=30 width=30> - Фреймворк для создания Java-приложений.
- `Hibernate` <img src="images/hiber_icon.png" height=30 width=30> - ORM-фреймворк (`Object-Relational Mapping`), который связывает Java-классы с таблицами в базе данных.
- `Swagger` <img src="images/swagger.svg" height=30 width=30> - Инструмент для документирования и тестирования `REST API`.
- `Thymeleaf` <img src="images/thymeleaf.png" height=30 width=30> - Серверный шаблонизатор (`HTML template engine`) для `Spring MVC`.
- `Postgres` <img src="images/postgr_icon.png" height=30 width=30> - Реляционная база данных проекта.
- `Elasticsearch` <img src="images/elastic_icon.png" height=30 width=30> - Поисковый движок, используемый для быстрого поиска и фильтрации данных.
- `Redis` <img src="images/redis_cion.svg" height=30 width=30> - Система кеширования в памяти, ускоряющая доступ к часто используемым данным.

## Key features:

В проекте реализованы авторизация и регистрация пользователя с шифрованием пароля и 
сохранением в БД. Реализованы html шаблоны для отображения страницы с логином, 
дашбордом, календарем. Реализован поиск событий пользователя с помощью `ElasticSearch`
и кэширование задач с помошью `Redis`. Реализован шедулер для отправки текущего списка задач
пользователя на его почту (Сейчас реализация - письмо сохраняется в папку emails).

## How to run app:

### Run containers:
```bash
  docker-compose up -d
```

### Command to build:
```bash
  mvn clean install
```

## Links:

- http://localhost:8080/ - главная страница
- http://localhost:8080/swagger-ui/index.html - Swagger

## Example:

### Страница авторизации:

<img src="images/08_11_sc_3.png">

### Дашборд:

<img src="images/08_11_sc_1.png">

### Календарь:

<img src="images/08_11_sc_7.png">

### Информация о текущем пользователе (профиль):

<img src="images/08_11_sc_2.png">

[//]: # (### Сообщение с задачами:)


### API методы в Swagger :

<img src="images/08_11_sc.png">