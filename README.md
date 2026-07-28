# Pipeline Service

REST-сервис на Java и Spring Boot для управления графами пайплайнов. Пайплайн содержит только узлы и направленные зависимости между ними; выполнение узлов не входит в задачу.

## Требования

- JDK 17 или новее

Docker и внешняя база данных не требуются.

## Запуск

Из корня проекта:

```bash
./gradlew bootRun
```

В PowerShell на Windows:

```powershell
.\gradlew.bat bootRun
```

После запуска сервис доступен по адресу `http://localhost:8080`. Swagger UI: `http://localhost:8080/swagger-ui.html`.

Запуск тестов:

```powershell
.\gradlew.bat test
```

## API

| Метод | Путь | Описание |
| --- | --- | --- |
| `POST` | `/pipelines` | Создать пайплайн |
| `POST` | `/pipelines/{pipelineId}/nodes` | Добавить узел |
| `POST` | `/pipelines/{pipelineId}/edges` | Добавить зависимость |
| `GET` | `/pipelines/{pipelineId}` | Получить весь пайплайн |
| `GET` | `/pipelines/{pipelineId}/execution-order` | Получить корректный топологический порядок |

### Пример работы через curl

Создать пайплайн:

```bash
curl -X POST http://localhost:8080/pipelines \
  -H "Content-Type: application/json" \
  -d '{"name":"demo"}'
```

Ответ содержит идентификатор созданного пайплайна:

```json
{
  "id": "f13c2f5e-0c69-4541-8d19-1c2b7ca3b4b5",
  "name": "demo",
  "nodes": [],
  "dependencies": []
}
```

Подставьте полученный `id` вместо `<pipelineId>` и добавьте узлы:

```bash
curl -X POST http://localhost:8080/pipelines/<pipelineId>/nodes \
  -H "Content-Type: application/json" \
  -d '{"nodeId":"input","name":"Input"}'

curl -X POST http://localhost:8080/pipelines/<pipelineId>/nodes \
  -H "Content-Type: application/json" \
  -d '{"nodeId":"output","name":"Output"}'
```

Добавить зависимость `input -> output`:

```bash
curl -X POST http://localhost:8080/pipelines/<pipelineId>/edges \
  -H "Content-Type: application/json" \
  -d '{"from":"input","to":"output"}'
```

Получить порядок выполнения:

```bash
curl http://localhost:8080/pipelines/<pipelineId>/execution-order
```

Пример ответа:

```json
{
  "pipelineId": "f13c2f5e-0c69-4541-8d19-1c2b7ca3b4b5",
  "order": ["input", "output"]
}
```

## Бизнес-правила

- Нельзя создать зависимость между несуществующими узлами.
- Нельзя создать зависимость узла на самого себя.
- Нельзя создать зависимость, которая образует цикл.
- Если есть зависимость A → B, то в возвращаемом порядке узел A должен находиться раньше узла B

Также было было принято решение, что узел уникален по `nodeId` внутри одного пайплайна. Добавление уже существующей зависимости обрабатывается идемпотентно (граф не меняется, ошибка не выбрасывается.

Проверка потенциального цикла реализована обходом графа в глубину (DFS). Порядок узлов строится алгоритмом Кана за `O(V + E)`, где `V` - количество узлов, а `E` - количество зависимостей.

## Хранение данных

Для этой задачи выбрано **in-memory хранилище** на основе `ConcurrentHashMap`.

Обоснование выбора: в ТЗ не заявлены требования к сохранности данных после перезапуска, совместной работе нескольких инстансов сервиса, истории изменений или сложным выборкам. Внешняя бд вместе с Docker Compose добавил бы миграции, конфигурацию и инфраструктурный код, но не улучшил бы эту центральную логику.

Ограничение: данные находятся в памяти процесса и очищаются после перезапуска приложения. Для production-версии, если появится требование персистентности или горизонтального масштабирования, реализацию `PipelineRepository` можно заменить на реализацию с обычной бд, не меняя контроллеры, сервисный слой и алгоритмы графа.

## Структура проекта

- `controller` - REST endpoints;
- `service` - бизнес-правил;
- `model` - сущности пайплайна, узлов и зависимостей;
- `repository` - репозиторий;
- `utils` - алгоритмы работы с графом;
- `dto` - запросы и ответы REST API;
- `exception` - исключения и единый обработчик ошибок.
