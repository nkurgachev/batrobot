# Batrobot

Приложение запускается как Spring Boot сервис и для первого релиза рассчитано на простой deployment без Kubernetes: один VPS, Docker, Docker Compose и локальный volume для данных.

## Что нужно для запуска

**На локальной машине (для сборки образа):**

- Docker Engine 24+
- Аккаунт в публичном реестре — Docker Hub или GitHub Container Registry

**На VPS:**

- Linux с доступом по SSH
- Docker Engine 24+
- Docker Compose Plugin 2+
- домен не обязателен, если бот используется только через Telegram

Рекомендуемый минимальный сервер для стабильной работы:

- 1 vCPU
- 2 GB RAM
- 10+ GB SSD
- Debian 12

## Подготовка VPS

### 1. Подключиться к серверу

```bash
ssh user@your-vps-ip
```

### 2. Обновить пакеты

```bash
sudo apt update
sudo apt upgrade -y
```

### 3. Установить Docker и Compose plugin

```bash
sudo apt install -y ca-certificates curl
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/debian/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg
echo \
	"deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/debian \
	$(. /etc/os-release && echo $VERSION_CODENAME) stable" | \
	sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin git
```

### 4. Добавить пользователя в группу docker

```bash
sudo usermod -aG docker $USER
newgrp docker
```

Проверка:

```bash
docker --version
docker compose version
```

## Сборка образа и публикация в реестр

Dockerfile использует многоэтапную сборку: Gradle компилирует проект внутри контейнера. На VPS с 1 GB RAM это занимает десятки минут и нередко падает по OOM. **Собирайте образ локально** на своей машине и публикуйте готовый в публичный реестр — на VPS останется только `docker pull`.

### Docker Hub

1. Создайте аккаунт на [hub.docker.com](https://hub.docker.com) и публичный репозиторий `batrobot`.
2. Авторизуйтесь локально (один раз):

```bash
docker login
```

3. Соберите и опубликуйте образ:

```bash
docker build -t your-dockerhub-username/batrobot:latest .
docker push your-dockerhub-username/batrobot:latest
```

Для тегирования конкретной версии:

```bash
docker build -t your-dockerhub-username/batrobot:1.0.0 .
docker push your-dockerhub-username/batrobot:1.0.0
# Обновить latest отдельно, если нужно
docker tag your-dockerhub-username/batrobot:1.0.0 your-dockerhub-username/batrobot:latest
docker push your-dockerhub-username/batrobot:latest
```

### GitHub Container Registry (альтернатива)

```bash
# Авторизация — нужен PAT с правом write:packages
echo YOUR_PAT | docker login ghcr.io -u YOUR_GITHUB_USERNAME --password-stdin

docker build -t ghcr.io/your-github-username/batrobot:latest .
docker push ghcr.io/your-github-username/batrobot:latest
```

Чтобы образ был публично доступен, откройте его настройки в разделе **Packages → Change visibility → Public**.

## Развертывание проекта

### 1. Клонировать репозиторий

```bash
git clone <REPOSITORY_URL>
cd batrobot
```

### 2. Создать файл `.env`

В корне проекта создайте `.env`:

```dotenv
BATROBOT_IMAGE=your-dockerhub-username/batrobot:latest

SPRING_PROFILES_ACTIVE=prod

TELEGRAM_BOT_TOKEN=your_telegram_bot_token
TELEGRAM_BOT_USERNAME=your_bot_username
STRATZ_API_TOKEN=your_stratz_api_token
STEAM_API_TOKEN=your_steam_api_token

DAY_START_HOUR=3
DAY_TIMEZONE=Europe/Moscow
INGESTION_STRATZ_HISTORICAL_START=1767225600
INGESTION_STRATZ_MATCHES_LIMIT=20

JAVA_OPTS=-XX:MaxRAMPercentage=50.0 -XX:InitialRAMPercentage=10.0 -Dfile.encoding=UTF-8
```

Для VPS с ограниченными ресурсами лучше начать с более консервативного `JAVA_OPTS`, чем desktop-дефолт. Если на сервере 1 GB RAM, можно попробовать:

```dotenv
JAVA_OPTS=-XX:MaxRAMPercentage=50.0 -XX:InitialRAMPercentage=10.0 -Dfile.encoding=UTF-8
```

### 3. Подготовить директорию с данными

```bash
mkdir -p data
```

База H2 будет храниться в локальном каталоге `./data`, который пробрасывается в контейнер как `/app/data`.

### 4. Собрать и запустить сервис

```bash
docker compose pull
docker compose up -d
```

### 5. Проверить состояние контейнера

```bash
docker compose ps
docker compose logs -f batrobot
```

Контейнер считается готовым, когда healthcheck переходит в `healthy`.

## Пошаговая схема первого запуска

### 1. Проверить, что контейнер поднялся

```bash
docker compose ps
```

### 2. Проверить health endpoint

```bash
docker compose exec batrobot wget -qO- http://localhost:8081/actuator/health
```

Ожидаемый ответ:

```json
{"status":"UP"}
```

### 3. Проверить логи Flyway и запуска Spring Boot

```bash
docker compose logs batrobot | tail -n 100
```

Нужно убедиться, что:

- миграции применились без ошибок
- токены API корректно прочитались
- приложение не уходит в restart loop

### 4. Проверить, что бот отвечает в Telegram

Отправьте боту команду, например `/help`.

Если ответа нет, сначала проверьте логи:

```bash
docker compose logs -f batrobot
```

## Обновление приложения на VPS

### 1. Собрать и опубликовать новый образ (локально)

```bash
docker build -t your-dockerhub-username/batrobot:latest .
docker push your-dockerhub-username/batrobot:latest
```

### 2. Забрать версию compose-файла на VPS

```bash
git pull
```

### 3. Обновить и перезапустить контейнер

```bash
docker compose pull
docker compose up -d
```

### 4. Удалить старые dangling images

```bash
docker image prune -f
```

На слабом VPS это особенно полезно, чтобы не забивать диск старыми слоями.

## Полезные команды эксплуатации

Просмотр логов:

```bash
docker compose logs -f batrobot
```

Перезапуск:

```bash
docker compose restart batrobot
```

Остановка:

```bash
docker compose down
```

Остановка без удаления контейнера:

```bash
docker compose stop
```

Проверка ресурсов:

```bash
docker stats
```

## Рекомендации для слабого VPS

- Не запускайте на VPS другие JVM-сервисы или лишние контейнеры рядом с ботом.
- Не ставьте слишком высокий `INGESTION_STRATZ_MATCHES_LIMIT`, если мало RAM и CPU.
- Оставляйте минимум 2-3 GB свободного места на диске под Docker layers и H2 database.
- Если сервер с 1 GB RAM, желательно включить swap на хосте.
- Периодически проверяйте размер папки `data` и вывод `docker system df`.

Пример создания swap-файла на 1 GB:

```bash
sudo fallocate -l 1G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
```

## Резервное копирование

Минимально достаточно сохранять:

- файл `.env`
- каталог `data`

Пример простого бэкапа:

```bash
tar -czf batrobot-backup-$(date +%F).tar.gz .env data
```

## Типичные проблемы

### Контейнер постоянно перезапускается

Проверьте:

- корректность значений в `.env`
- доступность `STRATZ_API_TOKEN` и `STEAM_API_TOKEN`
- логи через `docker compose logs batrobot`

### Бот не отвечает, но контейнер работает

Проверьте:

- правильность `TELEGRAM_BOT_TOKEN`
- правильность `TELEGRAM_BOT_USERNAME`
- нет ли ошибок в логах Telegram polling / outbound requests

## Замечания по безопасности

- Не коммитьте `.env` в репозиторий.
- Не публикуйте вывод `docker compose config`, если в окружении уже есть реальные токены.
- Ограничьте SSH-доступ по ключам и выключите парольную авторизацию на VPS.
- Если порт `8081` не нужен извне, ограничьте доступ через firewall или удалите публикацию порта совсем.

## Что можно улучшить позже

- автоматизация сборки и публикации образа через CI (например, GitHub Actions)
- переход с H2 на PostgreSQL для более предсказуемой прод-эксплуатации
- systemd unit для автоматического `docker compose up` после reboot
- внешний reverse proxy, если появится HTTP API для внешних клиентов