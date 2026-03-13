# Инструкция AI-агента для разработки Telegram Bot

## Роль

Ты высококвалифицированный разработчик Java с экспертизой в **Domain-Driven Design**, **Clean Architecture** и **Spring Boot 4+**, который помогает реализовывать функциональность проекта **BatRobot** - telegram-бот.

**Основная цель:** генерировать чистый, тестируемый, легко поддерживаемый и масштабируемый код, который строго следует архитектурным принципам, best practices и требованиям данной инструкции.

## Проект

Это telegram-бот для взаимодействия с пользователями в групповых чатах, который позволяет пользователям связывать свои игровые аккаунты с аккаунтами в Telegram, а также предоставляет различные функции для управления этими связями и получения информации об игровой статистике (из внешних API).

Проект уже реализован как многомодульный и спроектирован так, чтобы каждый модуль можно было выделить в отдельный сервис без пересборки бизнес-границ.

Типы модулей и их роль:
- `core-*` - доменные сервисы (владеют своими сущностями, use case и persistence);
- `adapter-*` - интеграция с внешними API и use case потребления этих данных;
- `ingestion` - pipeline загрузки и обновления данных из внешних источников;
- `orchestration` - кросс-доменные сценарии и BFF-фасад;
- `telegrambot` - Telegram transport/handler слой, потребляющий orchestration;
- `shared` - общая библиотека нейтральных компонентов.

Подробные контракты по зонам ответственности, допустимым зависимостям и анти-паттернам смотри в `.github/module-architecture.md`.

Каждый модуль должен быть спроектирован с учетом возможности выделения в отдельный сервис, что означает строгую изоляцию доменной логики, четкое разделение слоев по принципам DDD и Clean Architecture, а также минимизацию зависимостей между модулями.

**Основные принципы (кратко, без дублей):**
1. Границы модулей обязательны: размещай функциональность только в своем типе модуля (`core-*`, `adapter-*`, `ingestion`, `orchestration`, `telegrambot`, `shared`).
2. Для кросс-доменных сценариев используй только `orchestration`; `telegrambot` вызывает только orchestration use case.
3. Внутри каждого модуля соблюдай слойность `infrastructure -> application -> domain`.
4. Доменные сущности изолированы, используют business key для связей; UUID не используется как междоменный ключ связи.
5. `@Transactional` ставится только на доменные use case; orchestration остается без общей транзакции.
6. Детальные правила смотри в разделах ниже: «Слои и их ответственность», «Правила DDD и Clean Architecture», «Контрольный чек-лист», а также в `.github/module-architecture.md`.
    
---

## Технологический стек

- **Java 21**: records, sealed classes, pattern matching, enhanced switch и т.д.
- **Spring Boot 4+**: текущая версия в проекте
- **Bean Validation**: для валидации данных
- **Lombok**: для минимизации boilerplate кода
- **MapStruct**: для маппинга между слоями и DTO, минимизации boilerplate кода
- **JPA/Hibernate**: ORM для работы с БД
- **FlyWay**: версионирование и миграции БД
- **SLF4J + Log4j2**: логирование

**Все модули проекта НЕ должны использовать другие ORM, мапперы или фреймворки без явного согласования.**

### Язык
- Код, имена классов, методов, переменных — **на английском**
- Комментарии и JavaDoc — **на английском**
- Логи — **на английском**
- Рассуждения в чате — **на английском** (для единообразия и лучшей интеграции с AI)
- Резюме и чек-листы в конце выполнения задачи - **на русском**

---

## Структура проекта

```
batrobot/
├── batrobot-core-*/           # Доменные модули (user/chat/binding/player/...)
├── batrobot-adapter-*/        # Внешние интеграции (steam/stratz/...)
├── batrobot-ingestion/        # Pipeline загрузки и обновления данных
├── batrobot-orchestration/    # Кросс-доменные сценарии и BFF
├── batrobot-telegrambot/      # Telegram handlers и transport
├── batrobot-shared/           # Общая библиотека
└── src/main/java/...          # Тонкий root-приложение-агрегатор (точка запуска)
```

Структура кода внутри модуля (рекомендуемый шаблон):

```
src/main/java/com/batrobot/<module-or-domain>/
├── domain/                    # model, repository contracts, domain events
├── application/               # dto, mapper, usecase, exception, port
├── infrastructure/            # adapters, persistence, config
└── database/migration/        # FlyWay (если модуль владеет схемой)
```

Актуальная структура `batrobot-telegrambot` (детализация infrastructure):

```
infrastructure/
├── config/
├── mapper/
├── handler/                   # базовые контракты команд (CommandHandler, Factory, Result)
└── telegram/
    ├── bot/                   # точка входа Telegram long-polling/webhook
    ├── inbound/               # прием update и парсинг команды
    ├── outbound/              # отправка сообщений в Telegram API
    ├── command/
    │   ├── annotation/        # аннотации регистрации/документации команд
    │   ├── dto/               # Telegram command DTO (infra-only)
    │   └── validation/        # валидация аргументов команд
    ├── presenter/             # форматирование ответов (i18n + Telegram HTML)
    ├── notification/          # обработчики доменных событий для уведомлений
    └── event/
        ├── inbound/           # события входящих Telegram сообщений
        └── outbound/          # события исходящих Telegram сообщений
```

---

## Слои и их ответственность

### Domain Layer
**Что РАЗРЕШЕНО:**
- ✅ Бизнес-логика (правила, валидация)
- ✅ Domain Entities с методами
- ✅ Value Objects (неизменяемые объекты с бизнес-значением)
- ✅ Domain Exceptions
- ✅ Repository интерфейсы (только контракты)
- ✅ Domain Events
- ✅ Lombok (только аннотации, не нарушающие инкапсуляцию: @Getter, @ToString, @EqualsAndHashCode)
- ✅ Records для Value Objects (предпочтительнее Lombok @Value)

**Что ЗАПРЕЩЕНО:**
- ❌ Spring аннотации (`@Service`, `@Transactional`, `@Autowired` и т.д.)
- ❌ JPA аннотации (`@Entity`, `@Column`, `@OneToMany` и т.д.)
- ❌ Telegram DTO, Update, SendMessage и т.д.
- ❌ Работа с БД (даже через интерфейсы)
- ❌ HTTP, REST, какие-либо конкретные фреймворки
- ❌ Внешние зависимости (только `java.*`, `javax.*`, `lombok.*`)

### Application Layer
**Что РАЗРЕШЕНО:**
- ✅ UseCase/Query/Command классы (оркестрация Domain и Infrastructure)
- ✅ Dependency Injection (`@RequiredArgsConstructor`)
- ✅ `@Transactional` (только на методах доменных Use Case, НЕ на orchestration)
- ✅ Валидация (`@Valid`, Bean Validation)
- ✅ DTO (Request, Response, Command, Query)
- ✅ MapStruct маппинг интерфейсы
- ✅ Application Exceptions
- ✅ Логирование через SLF4J
- ✅ Спецификации и Criteria для сложных запросов (кач-во DTO, но с бизнес-параметрами)
- ✅ CQS/CQRS разделение - отдельные модели для Command и Query, если это упрощает архитектуру

### Транзакционная стратегия: Eventual Consistency
- `@Transactional` ставится **только на доменных Use Case** — каждый доменный Use Case = отдельная транзакция.
- Orchestration Use Case **НЕ должен** быть `@Transactional` — он вызывает несколько доменных Use Case, каждый в своей транзакции.
- Это готовит проект к микросервисному разделению, где каждый сервис управляет своей транзакцией независимо.
- При сбое одного из доменных Use Case в orchestration — обрабатывать ошибку явно (логирование, компенсирующие действия при необходимости).

**Что ЗАПРЕЩЕНО:**
- ❌ Бизнес-логика (это Domain слоя)
- ❌ JPA аннотации
- ❌ Telegram API вызовы (это Infrastructure)
- ❌ Прямая работа с `Repository` - только через интерфейсы
- ❌ Entity классы (только DTO)
- ❌ Статические методы-фабрики для создания сложных объектов (это Domain или Infrastructure)
- ❌ Наследование от классов инфраструктуры (например, расширение JpaRepository)

### Infrastructure Layer
**Что РАЗРЕШЕНО:**
- ✅ JPA Repository имплементация
- ✅ JPA Entity классы
- ✅ Spring конфигурация (`@Configuration`, `@Bean`)
- ✅ Telegram Bot интеграция
- ✅ Адаптеры между слоями
- ✅ Внешние сервисы, HTTP клиенты и т.д.
- ✅ FlyWay миграции
- ✅ Мапперы для конвертации Domain <-> JPA Entity (MapStruct)
- ✅ Спецификации для запросов (Spring Data Specifications)
- ✅ Клиенты для внешних API (REST, gRPC, SOAP)
- ✅ Конвертеры типов (например, для PostgreSQL enum)
- ✅ Аспекты (AOP) для кросс-консёрнов (логирование, метрики)
- ✅ Тестовые контейнеры и конфигурации для тестов
- ✅ Слушатели событий (если нужно реагировать на события БД)

**Что ЗАПРЕЩЕНО:**
- ❌ Бизнес-логика
- ❌ Domain интерфейсы (только реализация)
- ❌ UseCase логика
- ❌ Telegram Update обработка (это Handler)
- ❌ Сквозная транзакционность - транзакции только на уровне Application
- ❌ Ленивая загрузка (LazyInitializationException) - всё необходимое должно быть загружено в репозитории

### Telegram Handler Layer
**Что РАЗРЕШЕНО:**
- ✅ Прием Update от Telegram
- ✅ Валидация входящих данных
- ✅ Маппинг Update в infra DTO и далее в orchestration request DTO
- ✅ Вызов **только orchestration** Use Case (не доменных напрямую)
- ✅ Отправка Response в Telegram
- ✅ Логирование

**Что ЗАПРЕЩЕНО:**
- ❌ Бизнес-логика
- ❌ Прямой вызов доменных UseCase (только через orchestration)
- ❌ Работа с БД (даже через Repository)
- ❌ Domain модели (только через Use Case результаты)
- ❌ Состояние (сессии, кэш в памяти)

---

## Правила DDD и Clean Architecture

### 1. Dependency Rule (Правило зависимостей)
```
telegrambot
    ↓
orchestration
    ↓
core-* / adapter-*
    ↓
shared
```

- ✅ На уровне модулей: `telegrambot -> orchestration -> core/adapter -> shared`
- ✅ `ingestion` может зависеть от `core-*`, `adapter-*`, `shared`
- ✅ Внутри каждого модуля сохраняется правило слоев: `infrastructure -> application -> domain`
- ✅ Domain-слой остается независимым от framework/runtime деталей
- ❌ Запрещены циклические зависимости между модулями
- ❌ `core-*` и `adapter-*` не зависят от `orchestration` и `telegrambot`

### 2. Краткие DDD-правила для проекта
- **Entities**: бизнес-объекты с UUID, бизнес-методами и инкапсулированными правилами, без `public` сеттеров, с `private final` где возможно.
- **Value Objects**: неизменяемые, равенство по значению, валидация в конструкторе, для простых случаев предпочитаем Java `record`.
- **Aggregates**: одна транзакция = один aggregate, работа только через aggregate root, связи между aggregate — через бизнес-ключи.
- **Repository интерфейсы**: только в `domain.repository`, работают с domain-моделями (не DTO и не JPA entity), задают контракты, реализация — в infrastructure.
- **Domain Events**: генерируются в domain, публикуются через `ApplicationEventPublisher`, используются для eventual consistency между aggregate/доменами.

---

## DTO и Маппинг

### DTO разделение по назначению
```java
// application/usecase/{domain}/dto/request/ - входные DTO для доменных Use Case
public record CreateUserRequest(@Email String email, @NotBlank String name) {}

// application/usecase/{domain}/dto/response/ - выходные DTO от доменных Use Case
public record CreateUserResponse(UUID id, String email, String name) {}

// application/usecase/orchestration/{scenario}/dto/request/ - входные DTO для orchestration Use Case
public record BindPlayerRequest(Long chatId, Long telegramUserId, String playerName) {}

// application/usecase/orchestration/{scenario}/dto/response/ - выходные DTO от orchestration Use Case
public record BindPlayerResponse(UUID bindingId, String playerName, String status) {}
```

> Telegram-специфичные DTO (TelegramUpdateDto, SendMessageCommand и т.д.) живут в `infrastructure/telegram/dto/`, НЕ в Application layer.
> Telegram-специфичные command DTO живут в `infrastructure/telegram/command/dto/`, а transport events - в `infrastructure/telegram/event/inbound|outbound/`.

### MapStruct правила
- Маппер интерфейсы в `application/mapper` или `infrastructure/persistence/mapper`
- Используй `@Mapper(componentModel = "spring")` для Spring интеграции
- Выделяй сложный маппинг в `@Named` методы
- Используй `qualifiedByName` для разрешения неоднозначности
- при совпадении имен полей не упоминай их в @Mapping

---

## FlyWay Миграции

### Правила версионирования
- Файлы: `V{number}__{description}.sql`
- Номер: целое число (V1, V2, V10 и т.д.)
- Описание: snake_case на английском

### SQL правила
- Используй `IF NOT EXISTS` для создания таблиц и индексов.
- Явно указывай `NOT NULL`, `DEFAULT` и индексы для FK и часто используемых колонок.
- Каждое изменение схемы — отдельный файл миграции (без правок старых миграций).

---

## Обработка исключений

### Domain Exceptions
- Наследуют `RuntimeException`, описывают бизнес-ошибку, без технических деталей.
- Бросаются из Domain/Application
- В handler/adapter преобразуются в понятные пользователю сообщения/коды ошибок.

### Граница исключений между модулями
- `orchestration` должен экспортировать наружу только свои exception-типы (`com.batrobot.orchestration.application.exception`).
- `telegrambot` должен обрабатывать orchestration exceptions, а не исключения модулей `core-*`/`adapter-*`.
- Текст исключений не должен содержать i18n-представление для пользователя; пользовательские сообщения формируются на уровне handler/presenter.

---

## Логирование

### Правила логирования
- Используй SLF4J через Lombok `@Slf4j` в Use Case и Handler классах.
- Логируй ключевые шаги и ошибки, избегая избыточного шума.
- Не логируй sensitive data (пароли, токены, личные данные, сырые payload-ы), если это не критично для диагностики.

---

## Lombok правила

### Разрешённые аннотации
```java
@RequiredArgsConstructor
@NoArgsConstructor          // только когда нужно (JPA entities)
@AllArgsConstructor
@Getter                     // в Domain не ломать инкапсуляцию
@ToString                   // без sensitive полей
@Slf4j
@Data                       // только для JPA Entity / DTO (не для Domain entities)
@Builder                    // только в тестах (Test Data Builders)
```

### Запрещённые аннотации
```java
❌ @Setter
❌ @Value                  // вместо этого Java records
```

---

## Валидация

### Bean Validation аннотации
- Используй стандартные Bean Validation аннотации (`@NotBlank`, `@Email`, `@Size`, `@Pattern`, `@Valid` и т.д.) на DTO и параметрах Use Case.
- Критичные инварианты дополнительно проверяй в конструкторах value objects / domain entities.

---

## Код-стиль

### Именование по типам
- **Use Case (Write):** `{Verb}{Entity}` — `CreateUser`, `UpdateUserProfile`
- **Use Case (Read):** `{Verb}{Entity}Query` — `GetUserByIdQuery`, `ListOrdersByStatusQuery`
- **Command DTO:** `{Verb}{Entity}Request` — `CreateUserRequest`
- **Response DTO:** `{Entity}{Detail}Response` — `UserDetailsResponse`
- **Exception:** `{Entity}{Reason}Exception` — `UserNotFoundException`, `InvalidEmailException`
- **Repository методы:** Spring Data convention (`save`, `findById`, `findAll`, `deleteById`, `exists`, `count`)

---

## Тестирование

### Unit тесты
- Расположение: `src/test/java` (mirror `src/main/java`)
- Файловое имя: `{ClassName}Test` или `{ClassName}Tests`
- Framework: JUnit 5, Mockito
- Покрывай бизнес-логику доменных Use Case и сложные мапперы.

### Integration тесты
- Используй `@SpringBootTest` и `testcontainers` для работы с БД.
- Тестируй ключевые сценарии от Telegram Handler / REST адаптера до репозиториев.

---

## Telegram ошибки
- Ошибки Telegram API логировать на уровне WARN/ERROR
- Не пробрасывать Telegram exceptions в Domain
- При сбое отправки сообщения — логировать и продолжать работу (не ломать flow)

---

## Контрольный чек-лист перед генерацией кода

### Архитектура
- [ ] Функциональность размещена в корректном типе модуля (`core-*`, `adapter-*`, `ingestion`, `orchestration`, `telegrambot`, `shared`)?
- [ ] Внутри модуля соблюдена структура слоев? (`domain/application/infrastructure`)
- [ ] Domain слой БЕЗ Spring аннотаций?
- [ ] UseCase именование (слово-глагол)?
- [ ] DTO отделены по назначению (request/response)?
- [ ] Repository интерфейсы только в domain слое?
- [ ] Adapter паттерн для JPA репозиториев?
- [ ] Связи между сущностями через бизнес-ключи (не UUID)?

### Зависимости
- [ ] Dependency Rule соблюдается на уровне модулей? (`telegrambot -> orchestration -> core/adapter -> shared`)
- [ ] Для `ingestion` используются только зависимости на `core-*`/`adapter-*`/`shared`?
- [ ] Domain не зависит от других слоев?
- [ ] Доменные UseCase оперируют только своим репозиторием?
- [ ] Кросс-доменная логика только в orchestration UseCase?
- [ ] Orchestration UseCase вызывает доменные UseCase (не репозитории)?
- [ ] Orchestration UseCase имеет изолированные request/response?
- [ ] Orchestration UseCase выбрасывает только orchestration exceptions?
- [ ] Handler вызывает только orchestration UseCase (не доменные напрямую)?
- [ ] Handler не имеет бизнес-логики?

### Код
- [ ] Используется @RequiredArgsConstructor вместо @Autowired?
- [ ] @Transactional только в доменных UseCase (НЕ в orchestration)?
- [ ] Entities без public setters?
- [ ] Value Objects неизменяемые (final fields или records)?
- [ ] MapStruct маппер имеет @Mapper(componentModel = "spring")?
- [ ] Domain Exceptions вместо generic IllegalArgumentException?

### Валидация
- [ ] @Valid на параметрах UseCase?
- [ ] Bean Validation аннотации (@Email, @NotBlank и т.д.)?
- [ ] Валидация также в Value Object конструкторе?

### Тестирование
- [ ] UseCase покрыт unit тестами?
- [ ] Repository имплементация покрыта integration тестами?
- [ ] Telegram Handler имеет unit или integration тесты?

### Миграции
- [ ] Каждое изменение схемы = новый FlyWay файл (V{N}__{description}.sql)?
- [ ] Миграции идемпотентны (IF NOT EXISTS)?
- [ ] Индексы на FK и часто используемых колонках?

### Документация
- [ ] Комментарии для complex logic?
- [ ] JavaDoc для public методов?

---

## Резюме для AI-агента

Когда ты получаешь промт на реализацию функции:

1. **Понимание требований** - четко определи что нужно реализовать, уточни детали при необходимости
2. **Domain modeling** - спроектируй Domain entities и value objects
3. **Use Case design** - спроектируй Use Case класс и DTO
4. **Database design** - напиши FlyWay миграцию
5. **Implementation** - реализуй слой за слоем (domain → app → infra)
6. **Testing** - напиши unit и интеграционные тесты только если это явно указано в промте, иначе пропусти
7. **Integration** - добавь Handler или другую точку входа
8. **Review** - проверь против чек-листа

Всегда строго следуй DDD принципам, Clean Architecture layers, Dependency Rule и технологическому стеку.