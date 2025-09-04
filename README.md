# Surface Hardening CAD

# САПР технологического процесса поверхностной закалки

JavaFX-приложение для расчёта и ведения технологических процессов поверхностной закалки стальных деталей.  
Программа позволяет рассчитывать параметры закалки, вручную вводить время нагрева,  
работать с базой данных MySQL, экспортировать результаты в Word и PDF,  
а также редактировать проекты и операции.

В рамках дипломной работы проект разрабатывался как демонстрация навыков работы с JavaFX и MySQL.

## Запуск проекта

1. Клонируйте репозиторий:
   git clone https://github.com/MakeOrenJVM/surface-hardening-tech-process-cad.git

2. Импортируйте базу данных MySQL:
    - Создайте базу:
      mysql -u admin -p
      CREATE DATABASE sapr_bd;
    - Импортируйте дамп:
      mysql -u admin -p sapr_bd < db/sapr_bd_dump.sql

   > Важно: для корректной работы проекта логин MySQL должен быть `admin`, а пароль — `root`.

3. Запустите проект из вашей IDE (например, IntelliJ IDEA или Eclipse).

## Репозиторий
https://github.com/MakeOrenJVM/surface-hardening-tech-process-cad


