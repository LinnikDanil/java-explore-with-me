# java-explore-with-me

### Техническое задание для функционала комментариев

#### Цель

Цель данного раздела - внедрить систему комментирования событий в приложении Афиша, позволяющую пользователям,
участвовавшим в событии, оставлять комментарии, которые будут модерироваться администратором.

#### Основные требования

1. **Авторизация пользователя**: Комментарии могут оставлять только пользователи, участвовавшие в событии.

2. **Модерация**: Комментарии будут отображаться только после одобрения администратором.

3. **Редактирование и отмена**: Пользователь может изменить или отменить отправку комментария, если он ещё не
   опубликован.

4. **Жалобы на комментарии** Владелец события может пожаловаться на оставленный комментарий, если считает его
   некорректным или нарушающим правила.

#### Функциональные требования

##### Для пользователя

1. **Добавление комментария**: Возможность добавить комментарий к событию после участия в нём.
2. **Просмотр своих комментариев**: Возможность просмотра всех своих комментариев, включая статус (например, "На
   модерации", "Опубликован").
3. **Редактирование и удаление комментария**: Возможность редактировать или удалить комментарий до его публикации.

##### Для администратора

1. **Просмотр списка комментариев**: Возможность просмотра всех комментариев, включая статус (например, "На
   модерации", "Опубликован").
2. **Одобрение/отклонение комментария**: Возможность одобрить или отклонить комментарий.
3. **Удаление комментария**: Возможность удалить любой комментарий(включая после публикации. Т.К. пользователь может
   пожаловаться на комментарий, что пользователь был зарегистрирован на событие, но не явился).

#### API

API для функционала комментариев делится на следующие части:

1. **Публичная часть**: Доступ к опубликованным комментариям для всех пользователей при получении события.
2. **Закрытая часть**: Добавление, редактирование, удаление комментариев для авторизованных пользователей, участвовавших
   в событии.
3. **Административная часть**: Модерация и удаление комментариев администратором.

#### Модель данных

- **Комментарий**: ID пользователя, ID события, текст комментария, дата и время создания, статус (например, "На
  модерации", "Опубликован").

#### Безопасность

- Убедитесь, что только администраторы имеют доступ к административным функциям модерации.
- Убедитесь, что только пользователи, участвовавшие в событии, могут добавлять комментарии к нему.

#### Тестирование

Необходимо провести тестирование всех функций для обеспечения стабильности и безопасности функционала комментариев.

#### Документация

Предоставьте полную документацию API, а также руководство пользователя и администратора для работы с комментариями.

### Вывод

Эта функциональность позволит обогатить взаимодействие пользователей с событиями, предоставив им возможность высказывать
свое мнение и получать обратную связь от других участников и администраторов.

{
"openapi": "3.0.1",
"info": {
"description": "Documentation \"Explore With Me\" API v1.0",
"title": "\"Explore With Me\" API сервер",
"version": "1.0"
},
"servers": [
{
"description": "Generated server url",
"url": "http://localhost:8080"
}
],
"tags": [
{
"description": "Публичный API для работы с комментариями",
"name": "Public: Комментарии"
},
{
"description": "Приватный API для работы с комментариями",
"name": "Private: Комментарии"
},
{
"description": "Административный API для работы с комментариями",
"name": "Admin: Комментарии"
}
],
"paths": {
"/comments/user/{userId}": {
"get": {
"summary": "Получение списка всех опубликованных комментариев, которые оставили пользователю на все его события",
"tags": ["Public: Комментарии"],
"parameters": [
{
"name": "userId",
"in": "path",
"required": true,
"schema": { "type": "integer", "format": "int64" }
},
{
"name": "from",
"in": "query",
"required": false,
"schema": { "type": "integer", "format": "int32" }
},
{
"name": "size",
"in": "query",
"required": false,
"schema": { "type": "integer", "format": "int32" }
}
],
"responses": {
"200": {
"description": "Успешно",
"content": {
"application/json": {
"schema": {
"type": "array",
"items": {
"$ref": "#/components/schemas/CommentResponseDto"
}
}
}
}
},
"404": {
"description": "Пользователь не найден или ничего не найдено",
"content": {
"application/json": {
"schema": {
"$ref": "#/components/schemas/ApiError"
}
}
}
}
}
}
},
"/users/{userId}/events/{eventId}/comments": {
"post": {
"summary": "Публикация комментария",
"tags": ["Private: Комментарии"],
"parameters": [
{ "name": "userId", "in": "path", "required": true, "schema": { "type": "integer", "format": "int64" } },
{ "name": "eventId", "in": "path", "required": true, "schema": { "type": "integer", "format": "int64" } }
],
"requestBody": {
"content": {
"application/json": {
"schema": {
"$ref": "#/components/schemas/CommentRequestDto"
}
}
},
"required": true
},
"responses": {
"201": {
"description": "Комментарий создан",
"content": {
"application/json": {
"schema": {
"$ref": "#/components/schemas/CommentResponseDto"
}
}
}
},
"404": {
"description": "Пользователь или событие не найдены",
"content": {
"application/json": {
"schema": {
"$ref": "#/components/schemas/ApiError"
}
}
}
},
"409": {
"description": "Конфликт",
"content": {
"application/json": {
"schema": {
"$ref": "#/components/schemas/ApiError"
}
}
}
}
}
}
},
"/users/{userId}/events/{eventId}/comments/{commentId}": {
"patch": {
"summary": "Редактирование комментария пользователем",
"tags": ["Private: Комментарии"],
"parameters": [
{ "name": "userId", "in": "path", "required": true, "schema": { "type": "integer", "format": "int64" } },
{ "name": "eventId", "in": "path", "required": true, "schema": { "type": "integer", "format": "int64" } },
{ "name": "commentId", "in": "path", "required": true, "schema": { "type": "integer", "format": "int64" } }
],
"requestBody": {
"content": {
"application/json": {
"schema": {
"$ref": "#/components/schemas/CommentRequestDto"
}
}
},
"required": true
},
"responses": {
"200": {
"description": "Комментарий обновлен",
"content": {
"application/json": {
"schema": {
"$ref": "#/components/schemas/CommentResponseDto"
}
}
}
},
"404": {
"description": "Пользователь, событие или комментарий не найдены",
"content": {
"application/json": {
"schema": {
"$ref": "#/components/schemas/ApiError"
}
}
}
}
}
},
"delete": {
"summary": "Удаление комментария до публикации",
"tags": ["Private: Комментарии"],
"parameters": [
{ "name": "userId", "in": "path", "required": true, "schema": { "type": "integer", "format": "int64" } },
{ "name": "eventId", "in": "path", "required": true, "schema": { "type": "integer", "format": "int64" } },
{ "name": "commentId", "in": "path", "required": true, "schema": { "type": "integer", "format": "int64" } }
],
"responses": {
"204": {
"description": "Комментарий удален"
},
"404": {
"description": "Пользователь, событие или комментарий не найдены",
"content": {
"application/json": {
"schema": {
"$ref": "#/components/schemas/ApiError"
}
}
}
}
}
}
},
"/users/{userId}/events/{eventId}/comments/{commentId}/reports": {
"post": {
"summary": "Создание жалобы на комментарий владельцем события",
"tags": ["Private: Комментарии"],
"parameters": [
{ "name": "userId", "in": "path", "required": true, "schema": { "type": "integer", "format": "int64" } },
{ "name": "eventId", "in": "path", "required": true, "schema": { "type": "integer", "format": "int64" } },
{ "name": "commentId", "in": "path", "required": true, "schema": { "type": "integer", "format": "int64" } }
],
"requestBody": {
"content": {
"application/json": {
"schema": {
"$ref": "#/components/schemas/ReportRequestDto"
}
}
},
"required": true
},
"responses": {
"201": {
"description": "Жалоба создана",
"content": {
"application/json": {
"schema": {
"$ref": "#/components/schemas/ReportResponseDto"
}
}
}
},
"404": {
"description": "Пользователь, событие или комментарий не найдены",
"content": {
"application/json": {
"schema": {
"$ref": "#/components/schemas/ApiError"
}
}
}
},
"409": {
"description": "Такая жалоба уже существует",
"content": {
"application/json": {
"schema": {
"$ref": "#/components/schemas/ApiError"
}
}
}
}
}
}
},
"/comments": {
"get": {
"summary": "Получение списка всех комментариев",
"tags": ["Admin: Комментарии"],
"parameters": [
{
"name": "stateAction",
"in": "query",
"schema": {
"type": "string",
"enum": ["PENDING", "CONFIRMED", "REJECTED"]
},
"description": "Фильтр по статусу комментария"
}
],
"responses": {
"200": {
"description": "Список комментариев",
"content": {
"application/json": {
"schema": {
"type": "array",
"items": { "$ref": "#/components/schemas/CommentResponseDto" }
}
}
}
}
}
}
},
"/comments/{commentId}": {
"patch": {
"summary": "Одобрение/отклонение комментария",
"tags": ["Admin: Комментарии"],
"parameters": [
{
"name": "commentId",
"in": "path",
"required": true,
"schema": {
"type": "integer",
"format": "int64"
}
}
],
"requestBody": {
"content": {
"application/json": {
"schema": {
"type": "object",
"properties": {
"state": {
"type": "string",
"enum": ["PUBLISH_COMMENT", "REJECT_COMMENT"]
}
},
"required": ["state"]
}
}
},
"required": true
},
"responses": {
"200": {
"description": "Статус комментария обновлен"
},
"409": {
"description": "Изменять статус комментария можно только если его статус PENDING",
"content": {
"application/json": {
"schema": {
"$ref": "#/components/schemas/ApiError"
}
}
}
}
}
},
"delete": {
"summary": "Удаление комментария",
"tags": ["Admin: Комментарии"],
"parameters": [
{
"name": "commentId",
"in": "path",
"required": true,
"schema": {
"type": "integer",
"format": "int64"
}
}
],
"responses": {
"204": {
"description": "Комментарий удален"
},
"404": {
"description": "Комментарий не найден",
"content": {
"application/json": {
"schema": {
"$ref": "#/components/schemas/ApiError"
}
}
}
}
}
}
},
"/comments/reports": {
"get": {
"summary": "Получение списка всех жалоб",
"tags": ["Admin: Комментарии"],
"parameters": [
{ "name": "from", "in": "query", "schema": { "type": "integer" } },
{ "name": "size", "in": "query", "schema": { "type": "integer" } }
],
"responses": {
"200": {
"description": "Список жалоб",
"content": {
"application/json": {
"schema": {
"type": "array",
"items": { "$ref": "#/components/schemas/ReportResponseDto" }
}
}
}
}
}
}
}

},
"components": {
"schemas": {
"CommentRequestDto": {
"type": "object",
"properties": {
"text": {
"type": "string",
"minLength": 30,
"maxLength": 1000
}
},
"required": ["text"]
},
"CommentResponseDto": {
"type": "object",
"properties": {
"id": { "type": "integer", "format": "int64" },
"text": { "type": "string" },
"owner": { "$ref": "#/components/schemas/UserShortResponseDto" },
"event": { "$ref": "#/components/schemas/EventShortResponseDto" },
"publishedDate": { "type": "string" }
}
},
"ReportRequestDto": {
"type": "object",
"properties": {
"text": {
"type": "string",
"minLength": 30,
"maxLength": 500
}
},
"required": ["text"]
},
"ReportResponseDto": {
"type": "object",
"properties": {
"id": { "type": "integer", "format": "int64" },
"text": { "type": "string" },
"owner": { "$ref": "#/components/schemas/UserShortResponseDto" },
"event": { "$ref": "#/components/schemas/EventShortResponseDto" },
"comment": { "$ref": "#/components/schemas/CommentResponseDto" },
"createdDate": { "type": "string" }
}
},
"EventShortResponseDto": {
"type": "object",
"properties": {
"annotation": { "type": "string" },
"category": { "$ref": "#/components/schemas/CategoryResponseDto" },
"confirmedRequests": { "type": "integer", "format": "int64" },
"eventDate": { "type": "string", "pattern": "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$" },
"id": { "type": "integer", "format": "int64" },
"initiator": { "$ref": "#/components/schemas/UserShortResponseDto" },
"paid": { "type": "boolean" },
"title": { "type": "string" },
"views": { "type": "integer", "format": "int64" }
}
},
"CategoryResponseDto": {
"type": "object",
"properties": {
"id": { "type": "integer", "format": "int64" },
"name": { "type": "string" }
}
},
"UserShortResponseDto": {
"type": "object",
"properties": {
"id": { "type": "integer", "format": "int64" },
"name": { "type": "string" }
}
},
"ApiError": {
"type": "object",
"properties": {
"status": { "type": "integer" },
"message": { "type": "string" }
}
}
}
}
}