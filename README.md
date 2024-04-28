<h1>Запуск програми</h1>
Метод <i><b>main</b></i> основного класу застосунку <i><b>com.streamlined.restapp.RestApplication</b></i>
<p>
<h1>Імпорт даних основної сутності</h1>
Вихідний файл для імпорту даних основної сутності <i><b>Person</b></i> розташований за посиланням
<p><i>https://github.com/streamlined2/REST-application/blob/main/src/main/resources/data/data0.json</i>
<p>
Файл створений за допомогою метода <i><b>main</b></i> класа генератора випадкових даних <i><b>com.streamlined.restapp.generator.PersonDataGenerator</b></i> із параметрами кількість файлів 1, кількість сутностей 1000.
<p>
<h1>Скріпти Liquibase</h1>
Скріпти Liquibase для створення таблиць, індексів сутностей <i><b>Person, Country</b></i> і заповнення таблиці допоміжної сутності <i><b>Country</b></i> розташовані в теці <i><b>src/main/resources/db</b></i>
<p>
<h1>Збереження даних</h1>
Для збереження і роботи із даними використовується офіційний імідж PostgreSQL 16.2
<p>
		<i>https://hub.docker.com/_/postgres</i>
<p>
        <i>https://hub.docker.com/layers/library/postgres/16.2/images/sha256-07572430dbcd821f9f978899c3ab3a727f5029be9298a41662e1b5404d5b73e0?context=explore</i>
<p>
Сервіс запускається за допомогою Docker Compose, файл налаштування якого <i><b>compose.yaml</b></i> розташований в корні проєкту.
<p>
<h1>Виконання запитів для ендпойнтів <i>_list, _report</i> основної сутності <i><b>Person</b></i></h1>
<h2>Приклад виконання запитів для ендпойнта <i>/api/person/_list</i></h2>
<p>
Запит для отримання першої сторінки розміром 10 сутностей зі значеннями статі <i>FEMALE</i>, кольору очей <i>RED</i> та волосся <i>YELLOW</i>
<p>
<i>
        {
            "sex":"FEMALE",
            "eyeColor":"RED",
            "hairColor":"YELLOW",
            "page":0,
            "size":10
        }
</i>
<p>
Для пошуку людини <i><b>Person</b></i> по допоміжній сутності країни <i><b>Country</b></i> можна зазначити одну із властивостей <i><b>id</b></i>, <i><b>name</b></i>, або <i><b>capital</b></i>
<p>
Наприклад, для пошуку людини за іменем країни походження та за іменем столиці країни громадянства слід вказати запит
<p>
<i>
        {
            "countryOfOrigin":{
                "name": "USA"
            },
            "citizenship":{
                "capital": "Washington"
            },
            "page":0,
            "size":10
        }
</i>
<p>
Оскільки основна сутність <i><b>Person</b></i> містить дві властивості допоміжної сутності <i><b>Country</b></i>, а саме <i><b>countryOfOrigin, citizenship</b></i>, посилання на первинний ключ допоміжної сутності <i><b>countryId</b></i> є двозначним і неможливим.
<p>
Запити для ендпойнта <i>/api/person/_report</i> ідентичні, але без використання параметрів <i><b>page, size</b></i>
<p>
<h1>Валідація даних</h1>
Валідація даних виконується компонентом <i>Validator</i> на рівні сервісу перевіркою сутностей, а не DTO на рівні контролера, що спрощує супровід проєкту.
<p>