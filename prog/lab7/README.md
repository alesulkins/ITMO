## helios
- psql -h pg studs
- password -> .pgpass

## Алгоритм подключения к базе данных
- соединение с помощью DriverManager.getConnection(...); - возвращает объект класса Connection
- создание запроса через метод connection.createStatement() - возвращает Statement 
- statement.executeQuery("SELECT ...") - возвращает результат ResultSet
- проходим по result.next() - получение и обработка данных - в каждом цикле реализует одну строку запроса
- закрытие
## DriverManager

#### Управление списком драйверов

##### Загрузка драйвера:

* `Class.forName("org.postgresql.Driver");`
* `-Djdbc.drivers=org.postgresql.Driver` (указывается при запуске JVM)

---

#### Метод `Connection getConnection(String url, ...)`

##### Пример URL:

* `jdbc:protocol://host:port/database`
* `jdbc:postgresql://db:5432/studs`

---

#### Метод `getConnection(String url, Properties info)`

```java
Properties info = new Properties();
info.load(new FileInputStream("db.cfg"));

Connection conn = DriverManager.getConnection(
    "jdbc:postgresql://db:5432/studs", info);
```

---

#### Метод `getConnection(String url, String user, String password)`

```java
Connection conn = DriverManager.getConnection(
    "jdbc:postgresql://db:5432/studs", "5999999", "sss999");
```


## Connection-interface
методы
	Statement <- createStatement();
	PreparedStatement <- prepareStatement(String sql);
	CallableStatement <- prepareCall(String sql);
	DatabaseMetaData <- getMetaData();
## Семейство Statement-interface

`Statement` — это интерфейс в JDBC, с помощью которого ты отправляешь SQL-запросы в базу данных.
- ﻿﻿addBatch(String sql)
- ﻿﻿clearBatch()
- ﻿﻿executeBatch()
#### Statement 
статический sql-запрос
запрос неизменяемый и без параметров
```java
Statement st = connection.createStatement();
ResultSet rs = st.executeQuery("SELECT ...");
```
- **Уязвим для SQL-инъекций**
	    когда пользователь может в поле ввода написать не требуемое от него значение, а какой-то запрос на вывод каких-то значений из бд
- **Нельзя передавать параметры через `?`**
	    такое возможно в препеирд стэйтмент
- Повторное использование строки запроса = медленно и опасно 
#### PreparedStatement
динамический запрос с параметрами
предотвращает sql-инъекции - следит за типом данных и экранирует спецсимволы
```java 
PreparedStatement ps = connection.prepareStatement("SELECT * FROM table WHERE id = ?");
ps.setInt(1,15) // 1- param number; 15-value
ResultSet rs = pstmt.executeQuery();
```
- **Безопасность** (SQL-инъекции не работают)
	    так как вводим индекс для вставки и значение 
- **Переиспользование** (база может кэшировать план выполнения)
	    на этапе компиляции запоминает запрос 
- **Типизация параметров**
#### CallableStatement extends PreparedStatement
вызов хранимой процедуры
```java 
CallableStatement cstmt = connection.prepareCall("{call get_user(?)}");
cstmt.setInt(1, 5);
ResultSet rs = cstmt.executeQuery();
```
Чаще юзается в энтерпрайзе или сложной логике в БД
#### ResultSet
Получение данных из ResultSet

```java
ResultSet rs = preparedStatement.executeQuery();
while (rs.next()) {
String name = rs.getString(1); // по номеру столбца
int id = rs.getInt("id"); // по имени столбца
}
```
возвращается `ResultSet`, и можно **построчно** читать данные из таблицы.

По умолчанию — **однопроходный (`TYPE_FORWARD_ONLY`)**.
можно сделать вот так-с:
```java
Statement stmt = conn.createStatement(
    ResultSet.TYPE_SCROLL_INSENSITIVE, 
    ResultSet.CONCUR_READ_ONLY
);
```
### Типы прокрутки:

- `TYPE_FORWARD_ONLY` — только вперёд (по умолчанию)
    
- `TYPE_SCROLL_INSENSITIVE` — можно назад и вперёд, но **не чувствителен** к изменениям БД
    
- `TYPE_SCROLL_SENSITIVE` — чувствителен к изменениям БД
    

### Типы доступности:

- `CONCUR_READ_ONLY` — только читать
    
- `CONCUR_UPDATABLE` — можно менять данные прямо в `ResultSet`
### методы execute...();
`ResultSet executeQuery(String sql)`
	для исполнения команды `SELECT`
	Возвращает `ResultSet`
	String sql
		запрос для `Statement`
		отсутствует для `PreparedStatement` и `CallableStatement`
`int executeUpdate(String sql)`
	для выполнения запросов `INSERT, UPDATE, DELETE`
	возвращает количество измененных строк
	Для команд DDL возвращает 0
`boolean execute(String sal)`
	для выполнения любых запросов
	true, если результат — `ResultSet : ResultSet getResultSet()`
	false, если результат — `updateCount : int getUpdateCount)`

## Многопоточность. Класс `Thread`, интерфейс `Runnable`. Модификатор `synchronized`.
Многопоточность позволяет выполнять **несколько задач параллельно**.  
В Java это можно сделать двумя основными способами:

- Наследовать класс `Thread`
- Реализовать интерфейс `Runnable`
---

#### 🧵 Класс ~~нитки~~ `Thread`

Ты создаёшь **новый поток**, расширяя (`extends`) класс `Thread`, и переопределяешь метод `run()`.

```java
class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Поток работает: " + Thread.currentThread().getName());
    }
}

Thread t = new MyThread();
t.start();
```
start() запускает новый поток, run() — просто метод.
если сделать t.run(), то это будет в том же потоке, где ты находишься, то есть ран не создает новый поток в отличие от старта. 

`Thread`, если:

- надо **получить доступ к методам `Thread` напрямую** (например, `getId()`, `getState()`).
- **нет наследования для других целей**.

#### основные методы
| Метод             | Что делает                                                        | Где часто применяется                                    |
| ----------------- | ----------------------------------------------------------------- | -------------------------------------------------------- |
| `start()`         | Запускает поток (асинхронно вызывает `run()`)                     | Всегда при запуске нового потока                         |
| `run()`           | Метод, содержащий код потока (вызывается внутри `start()`)        | Переопределяется или реализуется в Runnable              |
| `sleep(ms)`       | Заставляет поток "уснуть" на X миллисекунд                        | Таймеры, задержки, симуляция ожидания                    |
| `join()`          | Ожидает завершения другого потока                                 | Когда нужно дождаться, пока другой поток завершит работу |
| `isAlive()`       | Проверяет, жив ли поток (ещё не завершился)                       | Проверка состояния потока                                |
| `interrupt()`     | Прерывает поток (в основном, если он в ожидании или сне)          | Корректная остановка потока                              |
| `setPriority()`   | Устанавливает приоритет потока (1 до 10)                          | Редко используется — JVM может игнорить                  |
| `currentThread()` | Возвращает текущий поток                                          | Полезно в логах, дебаге                                  |


---

#### Интерфейс `Runnable`

Лучший способ: реализовать `Runnable`, а потом передать объект `Runnable` в `Thread`.

```java
class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("Работает Runnable: " + Thread.currentThread().getName());
    }
}

Runnable r = new MyRunnable();
Thread t = new Thread(r);
t.start();
//или так
Thread t = new Thread(() -> System.out.println("Через лямбду!"));
t.start();
```

`Runnable`, когда:
- класс **уже наследует что-то другое** (в Java только одно наследование)
- разделение логики от запуска потока
- для переиспользования кода (передать `Runnable` в ExecutorService)

---

#### `synchronized`

`synchronized` — это **механизм синхронизации**, который **предотвращает одновременный доступ к критическому участку кода**.
монитор отдается потоку с какого-то участка кода. 
Если монитор занят — поток ждёт.
один встроенный монитор есть у каждого объекта. он нигде не прописан, но jvm с помощью него синхронизирует потоки. 

##### Пример с критическим участком:
```java
class Counter {
    private int count = 0;

    public synchronized void increment() {
        count++; // синхронизирован на уровне `this`, т.е. на объекте, на котором вызывается метод.
    }

    public synchronized int getCount() {
        return count;
    }
}
//или так
public void increment() {
	System.out.println("здесь могут быть несколько потоков");
    synchronized (this) { // явное указание, что синхронизация должна быть по монитору текущего объекта
        count++;
        System.out.println("здесь может быть один поток");
    }
}
```

если в классе несколько независимых операций. и всё синхронизировано на `this` — они друг друга тормозят.  
На `lock1` и `lock2` — **работают параллельно**.
```java
private final Object lock1 = new Object();
private final Object lock2 = new Object();

public void read() {
    synchronized (lock1) {
        // читает
    }
}

public void write() {
    synchronized (lock2) {
        // пишет
    }
}
```

---

#### вместе

```java
class ResponseSender implements Runnable {
    @Override
    public void run() {
        sendResponse();
    }

    private synchronized void sendResponse() {
        System.out.println("Ответ отправлен: " + Thread.currentThread().getName());
    }
}

Thread responseThread = new Thread(new ResponseSender());
responseThread.start();
```

- `Runnable` = логика
- `Thread` = механизм запуска
- `synchronized` = безопасность (один поток за раз)

---
## Методы `wait()`, `notify()` класса `Object`, интерфейсы `Lock` и `Condition`.

- работают **только внутри `synchronized` блока**.
- позволяют потоки **координировать**, **ждать друг друга**, **передавать данные**, и **работать в команде** без багов и гонок.
- Не просто “заблокировать доступ”, а **организовать взаимодействие по сложным правилам**.
#### wait()
```java
obj.wait();
```
- **Отпускает монитор obj**, и поток уходит в состояние ожидания
- Ждёт, пока кто-то **вызовет `obj.notify()` или `obj.notifyAll()`**
#### notify()` и `notifyAll()

```java
obj.notify();      // пробуждает один поток, который ждал на obj
obj.notifyAll();   // пробуждает всех ждущих на obj
```
- Потоки просыпаются, но **не сразу захватывают монитор** — они **ждут, пока он освободится**
## Классы-сихронизаторы из пакета `java.util.concurrent`.
| Класс                        | Назначение                                                             | Когда использовать                                                          | Пример использования                                          |
| ---------------------------- | ---------------------------------------------------------------------- | --------------------------------------------------------------------------- | ------------------------------------------------------------- |
| **`ReentrantLock`**          | Явная альтернатива `synchronized`, с возможностью прерывания, таймаута | Когда нужен контроль над блокировкой, `tryLock()`, `lockInterruptibly()`    | `lock.lock(); try { ... } finally { lock.unlock(); }`         |
| **`Semaphore`**              | Ограничивает количество одновременных потоков                          | Например, доступ к ограниченному числу ресурсов (N подключений, N парковок) | `semaphore.acquire(); ... semaphore.release();`               |
| **`CountDownLatch`**         | Ожидает, пока несколько потоков завершат работу                        | Когда главный поток должен дождаться завершения других                      | `latch.await();`<br>`latch.countDown();`                      |
| **`CyclicBarrier`**          | Ожидает, пока несколько потоков соберутся в одной точке                | Когда потоки должны синхронно стартовать (или продолжить)                   | `barrier.await();`                                            |
| **`Exchanger`**              | Два потока обмениваются данными                                        | Когда два потока должны передать друг другу объекты                         | `obj = exchanger.exchange(obj);`                              |
| **`Phaser`**                 | Расширенный `CyclicBarrier` с фазами                                   | Сложные циклические барьеры, динамическое количество потоков                | `phaser.arriveAndAwaitAdvance();`                             |
| **`StampedLock`**            | Блокировка с метками, безопасное и быстрое чтение                      | Если нужно быстрое чтение, но с возможностью отката при конфликте           | `long stamp = lock.readLock();`                               |


## Модификатор `volatile`. Атомарные типы данных и операции.

`volatile` — модификатор переменной, гарантирующий:
- **Всегда читать актуальное значение из памяти**, а не из кэша потока
- **Все изменения переменной видны всем потокам сразу**

**Атомарность** — это гарантия, что операция **выполнится целиком**, без возможности быть прервана другим потоком посередине.
- нарушается атомарность -> гонка данных (например в методе, в котором используется инкремент, так как инкремент это три действия)
- гонка данных 
	без атомарных типов два потока зайдут в метод инкрементирвания, в котором изначальное значение было 0. на выходе дадут 1, так как не соблюдена потокобезопасность и атомарность 

| Класс                | Что хранит       | Пример использования                           |
| -------------------- | ---------------- | ---------------------------------------------- |
| `AtomicInteger`      | `int`            | Счётчики, ID-шники, потокобезопасный инкремент |
| `AtomicLong`         | `long`           | Таймеры, генерация длинных ID                  |
| `AtomicBoolean`      | `boolean`        | Флаги остановки потока, состояние "вкл/выкл"   |
| `AtomicReference<T>` | Ссылка на объект | Обмен объектами между потоками (например, кэш) |
| `AtomicIntegerArray` | Массив `int`     | Безопасный доступ к массиву чисел из потоков   |

---

## Коллекции из пакета `java.util.concurrent`.
почему нужны такие коллекции?
- `ArrayList.add()` и `HashMap.put()` не синхронизированы
- может быть **гонка данных**, **сломанные ссылки**, **искажения состояния** (infinite loop, null)

| Коллекция               | Тип данных      | Особенности                                               | Когда использовать                                        |
| ----------------------- | --------------- | --------------------------------------------------------- | --------------------------------------------------------- |
| `ConcurrentHashMap`     | `Map<K, V>`     | Потокобезопасная `HashMap` с сегментированной блокировкой | Частый доступ к ключам и значениям, без полной блокировки |
| `CopyOnWriteArrayList`  | `List<E>`       | Копирует массив при каждом изменении                      | Частое чтение, редкое изменение                           |
| `CopyOnWriteArraySet`   | `Set<E>`        | Аналогично `CopyOnWriteArrayList`, но без дубликатов      | Те же кейсы, где нужен `Set`                              |
| `ConcurrentLinkedQueue` | `Queue<E>`      | Очередь на основе **связного списка**, неблокирующая      | Быстрые асинхронные очереди                               |
| `ConcurrentLinkedDeque` | `Deque<E>`      | Двусторонняя очередь, неблокирующая                       | Если нужно добавлять и забирать с двух сторон             |
| `LinkedBlockingQueue`   | `BlockingQueue` | Очередь с **блокировкой** и ограничением размера          | Продюсер–консюмер (producer-consumer)                     |
| `ArrayBlockingQueue`    | `BlockingQueue` | Очередь с фиксированной длиной, на массиве                | Контролируемый буфер                                      |
| `PriorityBlockingQueue` | `BlockingQueue` | Потокобезопасная очередь с приоритетом                    | Планировщики, очереди задач                               |
| `DelayQueue`            | `BlockingQueue` | Элементы появляются только после задержки                 | Таймеры, кеши с TTL                                       |
| `SynchronousQueue`      | `BlockingQueue` | Каждый `put()` ждёт `take()`, и наоборот                  | Прямой обмен между потоками                               |
| `LinkedTransferQueue`   | `TransferQueue` | Быстрая очередь для передачи элементов между потоками     | Высокая нагрузка, передача данных без очереди             |

## Пулы потоков

> **Thread Pool** — это коллекция заранее созданных потоков, которые ждут задач и исполняют их, не создаваясь заново.

создается через `Executors`
```java
ExecutorService pool = Executors.newFixedThreadPool(4);
```
Потоки внутри пула:
- **ждут** задачи,
- **выполняют**,
- **освобождаются**, но **не умирают**.

| Метод                                 | Описание                                                    | Когда использовать                              |
| ------------------------------------- | ----------------------------------------------------------- | ----------------------------------------------- |
| `Executors.newFixedThreadPool(n)`     | Фиксированное число потоков (`n`)                           | Несколько задач одновременно                    |
| `Executors.newSingleThreadExecutor()` | Один поток                                                  | Последовательная обработка задач                |
| `Executors.newCachedThreadPool()`     | Без ограничения, создаёт новые потоки по мере необходимости | Очень много коротких задач                      |
| `Executors.newScheduledThreadPool(n)` | Выполнение задач с задержкой или по расписанию              | Таймеры, повторяющиеся задачи                   |
| `Executors.newWorkStealingPool()`     | Для параллелизма, использует `ForkJoinPool`                 | Для рекурсивных/разветвляющихся задач (Java 8+) |

## Интерфейсы `Executor`, `ExecutorService`, `Callable`, `Future`
Класс `Thread` — это **низкоуровнево, вручную**.  
А `Executor` и компания — это **гибкая система для управления пулами потоков** и **асинхронных задач**.  
Ты больше **не управляешь потоками напрямую**, а просто отправляешь задачи на выполнение.

| Интерфейс / Класс | Назначение                                                                    | Возвращает результат? | Пример                       |
| ----------------- | ----------------------------------------------------------------------------- | --------------------- | ---------------------------- |
| `Executor`        | Базовый интерфейс для запуска задач (`Runnable`)                              | ❌ нет                 | `executor.execute(runnable)` |
| `ExecutorService` | Расширение `Executor` — умеет управлять жизненным циклом, возвращать `Future` | ✅ да (`Future`)       | `submit(callable)`           |
| `Callable<T>`     | Задача с **возвратом значения** и возможностью выброса исключения             | ✅ да                  | `call()` → `T`               |
| `Future<T>`       | Обёртка над асинхронным результатом                                           | ✅ да (позже)          | `future.get()`               |

---

#### `Executor`

```java
Executor executor = Executors.newSingleThreadExecutor();
executor.execute(() -> System.out.println("Задача выполнена"));
```

* Просто запускает `Runnable` без возврата результата
* Основа всей системы

---

#### `ExecutorService`

```java
ExecutorService service = Executors.newFixedThreadPool(3);
service.execute(() -> System.out.println("Поток из пула"));

service.shutdown(); // закрывает сервис после выполнения задач
```

Умеет:

* `submit(...)` — возвращает `Future`
* `shutdown()` — мягко завершает
* `shutdownNow()` — жёстко прерывает
* `awaitTermination()` — ждёт завершения

---

#### `Callable<T>`

```java
Callable<String> task = () -> "Привет, Алеся!";
```

* Как `Runnable`, но:

  * Возвращает значение (`T`)
  * Может кидать исключения

---

#### `Future<T>`

```java
ExecutorService service = Executors.newSingleThreadExecutor();

Future<String> future = service.submit(() -> "Алеся умничка");

String result = future.get(); // блокирует, пока не будет результата
System.out.println(result);

service.shutdown();
```

##### Методы `Future`:

| Метод           | Что делает                             |
| --------------- | -------------------------------------- |
| `get()`         | Блокируется, пока не получит результат |
| `get(timeout)`  | Блокируется до таймаута                |
| `cancel(true)`  | Прерывает выполнение                   |
| `isDone()`      | Проверяет, выполнена ли задача         |
| `isCancelled()` | Проверяет, отменена ли задача          |

---
