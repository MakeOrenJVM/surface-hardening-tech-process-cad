# surface-hardening-tech-process-cad

A Java-based CAD system for designing and analyzing technological processes of surface hardening.

## Technologies
- Java 17
- JavaFX
- PostgreSQL
- FXML
- JUnit 5, Mockito
- PDF Export (iText or similar)

## Features
- Designing and editing surface hardening process parameters
- Simulation of heat distribution and thermal analysis
- Visualization of charts and hardening process diagrams
- Export of process data and results to PDF
- JavaFX-based user interface

Note: User authentication and registration are not implemented (single-user mode)

## Project Structure
- `domain` — data models
- `service` — business logic
- `infrastructure` — data access layer
- `ui` — JavaFX user interface

## Testing
- Unit testing with JUnit 5
- Mocking with Mockito
- Fluent assertions with AssertJ

## How to Run
1. Clone the repository
2. Configure PostgreSQL in `application.properties`
3. Run the application from IntelliJ IDEA or as a `.jar` / `.exe` file

---

# САПР Технологического Процесса Поверхностной Закалки

Программная система на Java для проектирования и анализа технологических процессов поверхностной закалки.

## Технологии
- Java 17
- JavaFX
- PostgreSQL
- FXML
- JUnit 5, Mockito
- Экспорт в PDF (iText или аналогичная библиотека)

## Функциональность
- Проектирование и редактирование параметров закалки
- Моделирование распределения температуры и тепловой анализ
- Построение графиков и схем процесса
- Экспорт данных и результатов в PDF
- Интерфейс на JavaFX

Примечание: регистрация и авторизация пользователей не реализованы (однопользовательский режим)

## Структура проекта
- `domain` — модели данных
- `service` — бизнес-логика
- `infrastructure` — доступ к данным
- `ui` — пользовательский интерфейс на JavaFX

## Тестирование
- Unit-тесты с использованием JUnit 5
- Подмена зависимостей через Mockito
- Утверждения с использованием AssertJ

## Запуск проекта
1. Клонировать репозиторий
2. Настроить подключение к PostgreSQL в `application.properties`
3. Запустить приложение через IntelliJ IDEA или как `.jar` / `.exe` файл
