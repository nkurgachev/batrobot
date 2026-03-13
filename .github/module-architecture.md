# Модульная архитектура BatRobot

## Цель документа

Документ фиксирует ответственность модулей, допустимые зависимости и границы, чтобы каждый модуль оставался готовым к выделению в отдельный сервис.

## Типы модулей

### 1) core-*
Примеры: `batrobot-core-user`, `batrobot-core-chat`, `batrobot-core-binding`, `batrobot-core-player`.

Ответственность:
- владение доменной моделью конкретного bounded context;
- реализация доменных use case (command/query) только для своего домена;
- инкапсуляция доступа к своей БД (репозитории, сущности, миграции);
- публикация доменных событий и обработка инвариантов.

Ограничения:
- не содержит кросс-доменной оркестрации;
- не зависит от `orchestration` и `telegrambot`;
- не знает про Telegram API;
- не содержит клиентов внешних API, если это не его домен и не его граница ответственности.

### 2) adapter-*
Примеры: `batrobot-adapter-steam`, `batrobot-adapter-stratz`.

Ответственность:
- интеграция с внешними API (клиенты, ретраи, таймауты, rate limit handling, маппинг ответов);
- предоставление application use case для потребления данных адаптера;
- изоляция протокольных и vendor-специфичных DTO/ошибок.

Ограничения:
- не содержит бизнес-правил доменов `core-*`;
- не зависит от `orchestration` и `telegrambot`;
- не экспортирует наружу vendor DTO как публичный контракт use case.

### 3) ingestion
Пример: `batrobot-ingestion`.

Ответственность:
- загрузка и обновление данных через pipeline/джобы;
- координация шагов загрузки: external fetch -> transform -> save/update;
- планировщики и batch-процессы;
- идемпотентность и повторяемость pipeline.

Ограничения:
- не является пользовательским BFF;
- не содержит telegram-обработчиков;
- не содержит доменной логики вне вызова use case модулей-владельцев данных.

### 4) orchestration
Пример: `batrobot-orchestration`.

Ответственность:
- кросс-доменные сценарии и BFF-фасад для клиентских каналов;
- композиция use case из `core-*` и `adapter-*`;
- изолированные orchestration request/response и orchestration exceptions;
- явная обработка ошибок в стиле eventual consistency.

Ограничения:
- не обращается напрямую к репозиториям;
- не содержит JPA entity/репозиториев;
- не прокидывает наружу исключения core/adapter (только свои).

### 5) telegrambot
Пример: `batrobot-telegrambot`.

Ответственность:
- прием Telegram Update;
- mapping Telegram transport/update в infra DTO и orchestration DTO;
- вызов только orchestration use case;
- отправка сообщений/клавиатур/медиа в Telegram.
- форматирование пользовательских ответов (presenter);
- обработка domain/orchestration событий для Telegram-уведомлений.

Ограничения:
- не содержит бизнес-логики;
- не вызывает `core-*` и `adapter-*` напрямую;
- не работает с БД напрямую.

Рекомендуемые подпакеты внутри `infrastructure`:
- `telegram.bot` - точка входа Telegram клиента;
- `telegram.inbound` / `telegram.outbound` - транспортный поток входа/выхода;
- `telegram.command` - command handlers, validation, command dto;
- `telegram.presenter` - форматирование ответа (i18n/markup);
- `telegram.notification` - реакция на события для нотификаций;
- `telegram.event.inbound|outbound` - внутренние transport events.

### 6) shared
Пример: `batrobot-shared`.

Ответственность:
- общие технические компоненты: утилиты, базовые исключения, общие конфиги, кросс-секционные аспекты;
- повторно используемые типы, не содержащие доменной специфики.

Ограничения:
- не содержит бизнес-логики конкретного домена;
- не должен становиться «god-module»;
- не должен импортировать `core-*`, `adapter-*`, `ingestion`, `orchestration`, `telegrambot`.

## Допустимые зависимости между типами модулей

Разрешенная направленность зависимостей:
- `shared` -> (ни от кого)
- `core-*` -> `shared`
- `adapter-*` -> `shared`
- `ingestion` -> `core-*`, `adapter-*`, `shared`
- `orchestration` -> `core-*`, `adapter-*`, `shared`
- `telegrambot` -> `orchestration`, `shared`

Запрещенные направления:
- `core-*` -> `orchestration|telegrambot|ingestion`
- `adapter-*` -> `orchestration|telegrambot|ingestion`
- `orchestration` -> репозитории/сущности `core-*` напрямую, минуя use case
- `telegrambot` -> `core-*|adapter-*`
- любой модуль -> циклические зависимости

## Правила API/контрактов между модулями

- Межмодульный контракт задается через `application` DTO/use case и порты.
- Внешние DTO/клиенты (Steam/Stratz) не выходят за границы `adapter-*` как публичная доменная модель.
- `orchestration` использует собственные request/response и собственные exception-типы.
- `telegrambot` ловит orchestration exceptions, а не core/adapter exceptions.

## Транзакционная стратегия

- Транзакции ставятся на доменных use case в модулях-владельцах данных (`core-*`, при необходимости `adapter-*` где есть локальное хранилище).
- `orchestration` не помечается `@Transactional`.
- Межмодульные сценарии строятся с явной обработкой частичных отказов.

## Чек-лист при добавлении новой функциональности

- Определен модуль-владелец бизнес-правила.
- Не нарушены разрешенные направления зависимостей.
- Кросс-доменная координация размещена в `orchestration`, а не в `core-*`.
- Для внешнего API используется `adapter-*`, а не прямой HTTP-клиент из `core-*`/`telegrambot`.
- Telegram-слой вызывает только orchestration use case.
- Общий код помещен в `shared` только если он действительно нейтрален к домену.
