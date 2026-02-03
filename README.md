
## 1. Посилання на ресурси
- Deployed Service (Live Demo): http://34.79.215.93.nip.io/movies.

- GitHub Repository: https://github.com/1vanytska/movie-app-cloud (Репозиторій структуровано як Monorepo, де знаходяться всі сервіси та конфігурації інфраструктури).

## 2. Архітектура та опис реалізації
Систему розгорнуто в Google Cloud Platform (GCP) на базі кластера Google Kubernetes Engine (GKE). Архітектура включає наступні компоненти:

### А. API Gateway (Entry Point)

- Технологія: Spring Cloud Gateway.
- Реалізація: Виступає єдиною точкою входу в систему. Маршрутизує запити до відповідних мікросервісів.
- Безпека: Налаштовано CORS для дозволу запитів з фронтенду.

### Б. Security & Authentication (OAuth2)

Інтеграція: Реалізовано вхід через Google (OAuth 2.0).

Flow:
- Користувач натискає "Login" на фронтенді.
- Відбувається редірект на сервер авторизації Google.
- Після успішного входу Google повертає ID-токен.
- Backend валідує токен та створює сесію користувача.

Захист API: Реалізовано на рівні SecurityConfig у Spring Boot. Ендпоінти закриті для анонімних користувачів (повертають 401 Unauthorized), доступ дозволено лише аутентифікованим запитам з валідним токеном.

### В. Frontend (ui-base-app-next)

- Технологія: React.js (Single Page Application).
- Deployment: Запакований у Docker-контейнер разом з Nginx, який роздає статику.

User Flow: 
- Реалізовано перехоплення 401 помилок. 
- Якщо API повертає 401 при запиті на /profile, інтерфейс показує кнопку логіну. 
- Після авторизації дані профілю (ім'я, email) підтягуються автоматично.

### Г. Backend Service (spring-boot-rest-api)

- Movie Service: Java 17 + Spring Boot. Відповідає за бізнес-логіку фільмів.

- Dockerization: Використано Multi-stage builds для Java (Maven) та Node.js сервісів, що дозволило зменшити розмір фінальних образів і не тягнути вихідний код у продакшн.

## 3. Інфраструктура та CI/CD
Процес розгортання повністю автоматизовано за допомогою інструментів Google Cloud.

Cloud Infrastructure:
- GKE (Kubernetes): Керує життєвим циклом подів. Використовується об'єкт Deployment для забезпечення відмовостійкості (автоматичний перезапуск при падінні).
- Artifact Registry: Приватний реєстр для зберігання зібраних Docker-образів.
- CI/CD Pipeline (Google Cloud Build): Налаштовано пайплайн безперервної доставки (Continuous Deployment), який спрацьовує при події git push у гілку main.
- Build: Cloud Build автоматично збирає Docker-образ з оновленого коду (використовуючи відповідний Dockerfile у папці сервісу).
- Push: Образ тегується та завантажується в Google Artifact Registry.
- Deploy: Виконується команда kubectl rollout restart deployment/.... Kubernetes плавно замінює старі контейнери на нові без простою системи (Zero-downtime deployment).
- Секрети: Токени доступу (Google Client ID/Secret) та параметри підключення до БД винесені у змінні оточення (Environment Variables) і не зберігаються у відкритому вигляді в коді репозиторію.