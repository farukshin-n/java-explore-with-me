# java-explore-with-me

## Дипломный проект курса Java developer Яндекс-Практикума (4 когорта, 2 группа)

**Студент:** Наиль Фарукшин

Приложение **ExploreWithMe** предназначено, чтобы делиться информацией об интересных событиях и помогать найти компанию для участия в них.

Приложение состоит из двух частей: основной части (ewm-service) и сервиса статистики (stats-server), выполнено на основе микросервисной архитектуры. 

Пул-реквест проекта можно найти [здесь](https://github.com/farukshin-n/java-explore-with-me/pull/2).

### Основная часть

API основной части приложения (ewm-service) состоит из трёх частей:
- публичной, то есть доступной всем без исключения; 
- приватной, то есть доступной только пользователям;
- административной, то есть доступной только администраторам сервиса.

Публичное API позволяет: 
- искать события, 
- фильтровать их по разным параметрам (дате, количеству просмотров, категориям и пр.)
- просматривать полную информацию о них.

Приватное API позволяет: 
- добавлять новые события, редактировать и просматривать их;
- подавать заявки на участие в событиях, добавленных другими пользователями;
- подтверждать заявки других пользователей на созданные пользователем события.

Административное API позволяет:
- добавлять, изменять и удалять категории для событий;
- добавлять, редактировать данные и удалять пользователей;
- публиковать и отклонять события, добавленные пользователями;
- добавлять, удалять и закреплять подборки событий.

Спецификация основного сервиса доступна [здесь](https://github.com/farukshin-n/java-explore-with-me/blob/main/ewm-main-service-spec.json). 
Схема базы данных для всех необходимых сущностей:
![main schema](https://github.com/farukshin-n/java-explore-with-me/blob/develop/schema-main-service.jpeg)


### Сервис статистики

Этот сервис позволяет: 
- собирать информацию о просмотрах событий из публичного API основной части приложения;
- получать эту информацию из основной части приложения.

Спецификация основного сервиса доступна [здесь](https://github.com/farukshin-n/java-explore-with-me/blob/main/ewm-stats-service-spec.json).

Схема базы данных для сервиса статистики:

![stats schema](https://github.com/farukshin-n/java-explore-with-me/blob/develop/schema-stats.jpeg)

### Feature 

В дополнение к техническому заданию в приложении реализована функция добавления комментариев к событиям. Пулл-реквест можно посмотреть [здесь](https://github.com/farukshin-n/java-explore-with-me/pull/5).

У этой функции есть приватное и административное API.

Приватное API позволяет:
- добавлять, получать, изменять и удалять комментарии.

Административное API позволяет:
- удалять комментарии, скрывать и раскрывать их.
