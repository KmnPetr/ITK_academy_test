# Тестовое задание для компании ITK Academy

Запуск проекта осуществляется командой 
```bash
docker compose up -d
```
Далее дождитесь запуска БД Postgres, окончания сборки maven проекта, запуска приложения

Все необходимые переменные (пароли, порты и др.) есть возможность поменять из файла .env (в корне проекта)

Для проверки эндпоинтов использовать следующие пути:

- Создание нового wallet:
```http
POST /api/v1/wallet/create (без тела и доп.параметров)
Вернет json c полем uuid созданного wallet
```

- Операции пополнения, списания:
```http
POST /api/v1/wallet
{
    valletId: UUID,
    operationType: DEPOSIT or WITHDRAW,
    amount: 1000
}
```

- Запрос баланса:
```http
GET /api/v1/wallet/{WALLET_UUID}
```

