## Сетевое взаимодействие - клиент-серверная архитектура, основные протоколы, их сходства и отличия.

Клиент-серверная архитектура в Java — это подход к построению распределённых систем, где приложение разделяется на две основные части: клиентскую и серверную. Клиент запрашивает информацию или услуги у сервера, а сервер отвечает на запросы, предоставляя данные или функциональность. 

### Основные компоненты

- **Клиент** — устройство или приложение, которое обращается к серверу.
- **Сервер** — компьютер, предоставляющий информацию или услуги клиентам.
- **Протоколы обмена данными** — правила или наборы инструкций для взаимодействия между клиентами и серверами

## Протокол TCP. Классы `Socket` и `ServerSocket`.

**Сокет** — это программная (логическая) конечная точка, устанавливающая двунаправленную коммуникацию между сервером и одной или несколькими клиентскими программами.

**TCP (Transmission Control Protocol)** обеспечивает надёжную и упорядоченную доставку данных между устройствами в сети. В Java для реализации TCP-соединения используются классы Socket (на клиенте) и ServerSocket (на сервере):

- Socket() — на сервере создается конечная точка для коммуникации.
- Bind() — сокету присваивается уникальный номер и для него резервируется уникальная комбинации IP-адреса и порта.
- Listen() — после создания сокета сервер ожидает подключения клиента.
- Accept() — сервер получает запрос на подключение от клиентского сокета.
- Connect() — клиент и сервер соединены друг с другом.
- Send()/Recieve() — обмен данными между клиентом и сервером.
- Close() — после обмена данными сервер и клиент разрывают соединение

## Протокол UDP. Классы `DatagramSocket` и `DatagramPacket`.

UDP (User Datagram Protocol) обеспечивает ненадёжную, но быструю передачу данных без установления соединения. В Java для работы с UDP используются классы DatagramSocket и DatagramPacket:

- **DatagramSocket** — сокет для отправки и получения датаграмм (пакетов данных). Не устанавливает постоянное соединение, может отправлять и получать данные от нескольких источников.   
- **DatagramPacket** — класс, который представляет пакет датаграмм. Содержит данные, отступ данных пакета, длина меньше или равна буферу, Iадрес отправителя и получателя, номер порта.

Отличия блокирующего и неблокирующего ввода-вывода, их преимущества и недостатки. Работа с сетевыми каналами.
## Блокирующий ввод-вывод

**Поток блокируется до завершения операции**. Например, при чтении данных из файла или запросе к базе данных поток не может выполнять другие задачи, пока операция не завершится. 

**Преимущества**: 
- поток находится в ожидании, что может быть полезно, если программа не выполняет других задач во время операции. 
**Недостатки**: 
- поток не может использовать ресурсы процессора, пока ожидает завершения операции, что может быть неэффективным в высоконагруженных системах. 

`Чтение из TCP-сокета. Вызов read блокирует выполнение, пока другая сторона соединения не пришлёт ответные данные.`
 
### Неблокирующий ввод-вывод

**Поток не блокируется**. 
Система продолжает выполнять другие задачи, а результат операции обрабатывается позже, когда данные станут доступны.

**Преимущества**:
- **Эффективное использование ресурсов**. Потоки могут выполнять другие задачи, пока операции ввода-вывода ожидают завершения.
- **Масштабируемость**. Системы с неблокирующим вводом-выводом могут обрабатывать тысячи одновременных соединений.
- **Улучшенная отзывчивость**. Приложения могут обрабатывать запросы пользователей, пока выполняются длительные операции, например, запросы к базе данных или внешнему API.

**Недостатки**: 
- при неблокирующем вводе-выводе программе придётся позже проверять, завершён ли ввод-вывод, что может привести к бесконечному циклу опроса, если задачи, которые выполняет программа, сами по себе представляют другой ввод-вывод.

`Базы данных (Redis и MongoDB). Используют неблокирующий ввод-вывод для повышения производительности, что позволяет им обрабатывать большое количество запросов одновременно.`
`Сетевые приложения (чат-серверы, системы реального времени). Неблокирующий ввод-вывод позволяет быстро обрабатывать множество запросов.`

## Классы `SocketChannel` и `DatagramChannel`.

Каналы являются частью пакета `java.nio` 
предоставляют альтернативный набор классов для работы с вводом-выводом данных 
Классы каналов позволяют выполнять синхронное и асинхронное чтение и запись данных внутри NIO фреймворка.
Работают напрямую с буферами данных, уменьшая задержку и увеличивая производительность 
#### Основные классы каналов
- FileChannel: чтение и запись данных в файлы 
- SocketChannel: чтение и запись данных через TCP сетевые соединения 
- DatagramChannel: чтение и запись данных через UDP сетевые соединения 
-  ServerSocketChannel: создание серверов, принимающих входящие соединения через TCP 
#### Использование каналов
- Создание объекта канала через методы open() 
- Пример создания канала для чтения данных из файла 
- Пример записи данных в файловый канал 
#### Преимущества каналов
- Увеличение скорости ввода-вывода данных в приложении 
- Возможность работы с сетевыми соединениями через SocketChannel, ServerSocketChannel и DatagramChannel

## Передача данных по сети. Сериализация объектов.

**Сериализация** — процесс преобразования сложных структур данных, таких как объекты, массивы, словари и т. д., в более простой формат, который может быть сохранён в файле или передан через сеть. Результатом сериализации может быть текстовая строка или бинарное представление, которое легко восстановить обратно в исходные данные
#### передача по сети

1.  Объект сериализуется на одной стороне (переводится в удобный для передачи формат, например, JSON или бинарный поток).
2. Затем передаётся по сети.
3. На другой стороне объект десериализуется (восстанавливается в исходную сложную структуру данных) для дальнейшего использования.

#### преимущества сериализации:

- **Хранение данных**. Сериализация позволяет сохранять состояние объектов в файлы или базы данных.
- **Интеграция разных систем**. Сериализация позволяет преобразовывать данные в общие форматы, такие как JSON или XML, что упрощает интеграцию.
- **Производительность**. В некоторых случаях бинарные форматы сериализации могут быть более компактными и быстрыми для передачи по сети, чем текстовые форматы.
- **Сохранение совместимости**. При использовании версионирования данных можно сохранить совместимость между разными версиями программ, десериализуя старые данные в новой версии приложения.
- **Расширяемость**. Многие форматы сериализации позволяют встраивать пользовательские типы данных, что делает их более гибкими для разных приложений.

## Интерфейс `Serializable`. Объектный граф, сериализация и десериализация полей и методов.
`Serializable` - один из специальных информативных интерфейсов, которые просто помечают классы дополнительной информацией. 

`private static final long serialVersionUID` - уникальный идентификатор версии сериализованного класса, который присваивается каждому классу по его значениям полей и методов, который `implements Serializable`. 

Если при десериализации значения `serialVersionUID` не совпадают, будет выброшено исключение `java.io.InvalidClassException`.

При сериализации класса сериализуются все объекты, на которые он ссылается. Поэтому, если объекты, на которые ссылаются, не будут наследовать интерфейс сериализации, то эти объекты и собственно класс будет невозможно полностью сериализовать. будет выброшено исключение. 

не хочешь сериализовать поле без выброшенных исключений? 
сделай это поле `transient`. при сериализации ему присваивается значение по умолчанию. В случае с объектами это `null`
## Java Stream API. Создание конвейеров. Промежуточные и терминальные операции.

**Java Stream API** - интерфейс, позволяющий выполнять операции над последовательностями элементов (списки, массивы или другие источники данных) в декларативной манере, упрощая код и повышая его читаемость. Полезен для обработки больших объемов данных, поддерживая ленивые вычисления и параллельное выполнение.

**Stream** — это абстракция, представляющая последовательность элементов, над которыми можно выполнять операции. Потоки не хранят данные, а предоставляют способ их обработки. Они поддерживают функциональный подход, избегая изменения исходных данных (иммутабельность)

- **Декларативность**: Код описывает, что нужно сделать, а не как (в отличие от императивного подхода с циклами).
- **Ленивые вычисления**: Промежуточные операции не выполняются, пока не вызвана терминальная операция.
- **Однократное использование**: Поток можно использовать только один раз; после выполнения терминальной операции он считается "закрытым".
#### Принцип работы

- **Источник данных**: Поток создаётся из коллекции, массива, файла или другого источника.
- **Конвейер операций**: Состоит из цепочки промежуточных операций, которые преобразуют данные, и одной терминальной операции, которая возвращает результат.
- **Иммутабельность**: Потоки не изменяют исходную коллекцию.
#### Промежуточные операции(основные)

`filter(Predicate predicate)` 
- Отфильтровывает элементы, не соответствующие условию (предикату).
- Возвращает поток, содержащий только элементы, для которых предикат возвращает true.

 `map(Function<T, R> mapper)`
- Преобразует каждый элемент потока с помощью функции.
- Возвращает поток с преобразованными элементами.

`sorted() или sorted(Comparator<T> comparator)`
- **Описание**: Сортирует элементы потока (по естественному порядку или с использованием компаратора).
- Возвращает отсортированный поток.

`distinct()`
- **Описание**: Удаляет дубликаты из потока (использует equals()).
- **Возвращает**: Поток с уникальными элементами.

`limit(long maxSize)`

- **Описание**: Ограничивает количество элементов в потоке.
- Возвращает поток с не более чем maxSize элементами.
#### Терминальные операции
`collect(Collector collector)` 
- Собирает элементы потока в коллекцию или другой результат.
 `toList()` `toSet()` `toMap(keyMapper, valueMapper)` `joining(delimiter)`
 
 `findFirst(), findAny()`
 - Возвращает первый или любой элемент потока (в виде Optional).
`count(), sum(), average(), min(), max()` - агрегатные
`min(Comparator<T> comparator), max(Comparator<T> comparator)`

`forEach(Consumer action)`
- Выполняет действие для каждого элемента.

`reduce(BinaryOperator<T> accumulator)`
- Сводит элементы потока к одному значению.

## Шаблоны проектирования: Decorator, Iterator, Factory method, Command, Flyweight, Interpreter, Singleton, Strategy, Adapter, Facade, Proxy.

**Шаблоны проектирования** — это проверенные решения типичных задач проектирования программного обеспечения. Они помогают сделать код более гибким, масштабируемым и легким для поддержки.
#### Decorator (Декоратор)
Декоратор позволяет добавлять новую функциональность объекту, не изменяя его исходный код. Это как добавление начинки в кофе: эспрессо остаётся тем же, но вы добавляете молоко, сливки или сироп.

**Когда использовать?**

- Когда нужно расширить поведение объекта динамически.
- Когда изменение исходного класса нежелательно или невозможно.

кофе (базовый объект)
добавить молоко, сироп или взбитые сливки (декораторы), не меняя сам кофе

**Пример кода**:

```java
interface Coffee {
    String getDescription();
    double getCost();
}

class SimpleCoffee implements Coffee {
    public String getDescription() { return "Simple Coffee"; }
    public double getCost() { return 2.0; }
}

abstract class CoffeeDecorator implements Coffee {
    protected Coffee coffee;
    public CoffeeDecorator(Coffee coffee) { this.coffee = coffee; }
}

class MilkDecorator extends CoffeeDecorator {
    public MilkDecorator(Coffee coffee) { super(coffee); }
    public String getDescription() { return coffee.getDescription() + ", Milk"; }
    public double getCost() { return coffee.getCost() + 0.5; }
}

public class Main {
    public static void main(String[] args) {
        Coffee coffee = new SimpleCoffee();
        coffee = new MilkDecorator(coffee);
        System.out.println(coffee.getDescription() + " costs $" + coffee.getCost());
        // Вывод: Simple Coffee, Milk costs $2.5
    }
}
```

Декоратор
Шаблон Декоратор позволяет гибко и динамически изменять поведение объекта без изменения его исходного кода. Это достигается через композицию, где каждый декоратор оборачивает предыдущий и добавляет дополнительное поведение. В Java это очень удобно использовать, например, при логировании, проверках прав доступа или динамическом изменении визуального компонента.

#### Iterator (Итератор)

Итератор предоставляет способ последовательного доступа к элементам коллекции, не раскрывая её внутренней структуры. Это как листать страницы книги, не зная, как она устроена внутри.

**Когда использовать?**

- Когда нужно обойти элементы коллекции (список, массив, дерево) без привязки к их реализации.
- Когда коллекция сложная, и прямой доступ затруднён.

**Пример из жизни**:  
Пульт телевизора: вы переключаете каналы (итерируете), не зная, как телевизор их хранит.

**Пример кода**:

```java
import java.util.Arrays;
import java.util.Iterator;

class MyCollection<T> implements Iterable<T> {
    private T[] items;

    public MyCollection(T[] items) { this.items = items; }

    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int index = 0;
            public boolean hasNext() { return index < items.length; }
            public T next() { return items[index++]; }
        };
    }
}

public class Main {
    public static void main(String[] args) {
        MyCollection<String> collection = new MyCollection<>(new String[]{"a", "b", "c"});
        for (String item : collection) {
            System.out.println(item);
        }
        // Вывод: a, b, c
    }
}
```

---

#### Factory Method (Фабричный метод)

Фабричный метод определяет интерфейс для создания объектов, позволяя подклассам решать, какой класс создавать. 

**Когда использовать?**

- Когда заранее неизвестно, какой конкретный класс объекта нужен.
- Когда создание объекта требует сложной логики.

**Пример кода**:

```java
interface Pizza {
    String getName();
}

class Margherita implements Pizza {
    public String getName() { return "Margherita"; }
}

class Pepperoni implements Pizza {
    public String getName() { return "Pepperoni"; }
}

abstract class PizzaFactory {
    abstract Pizza createPizza();
}

class MargheritaFactory extends PizzaFactory {
    Pizza createPizza() { return new Margherita(); }
}

class PepperoniFactory extends PizzaFactory {
    Pizza createPizza() { return new Pepperoni(); }
}

public class Main {
    public static void main(String[] args) {
        PizzaFactory factory = new MargheritaFactory();
        Pizza pizza = factory.createPizza();
        System.out.println("Ordered: " + pizza.getName());
        // Вывод: Ordered: Margherita
    }
}
```

---

#### Command (Команда)

Команда инкапсулирует запрос как объект, позволяя передавать его, хранить или отменять. Это как пульт ДУ: каждая кнопка — команда, которая говорит устройству, что делать.

**Когда использовать?**

- Когда нужно отделить запрос от его выполнения.
- Когда требуется поддержка отмены операций или их очереди.

**Пример из жизни**:  
Пульт телевизора: нажатие кнопки "включить" — это команда, которая выполняется телевизором.

**Пример кода**:

```java
interface Command {
    void execute();
}

class Light {
    public void turnOn() { System.out.println("Light is ON"); }
}

class TurnOnLightCommand implements Command {
    private Light light;
    public TurnOnLightCommand(Light light) { this.light = light; }
    public void execute() { light.turnOn(); }
}

class RemoteControl {
    private Command command;
    public void setCommand(Command command) { this.command = command; }
    public void pressButton() { command.execute(); }
}

public class Main {
    public static void main(String[] args) {
        Light light = new Light();
        Command turnOn = new TurnOnLightCommand(light);
        RemoteControl remote = new RemoteControl();
        remote.setCommand(turnOn);
        remote.pressButton();
        // Вывод: Light is ON
    }
}
```
---

#### Flyweight (Легковес)

Легковес позволяет экономить память, разделяя общее состояние объектов. Это как библиотека книг: вместо хранения полной копии книги для каждого читателя, хранится одна копия, а читатели получают ссылки.

**Когда использовать?**

- Когда создаётся много объектов с одинаковыми данными.
- Когда можно вынести общее состояние в отдельный объект.

**Пример из жизни**:  
Шрифты в текстовом редакторе: вместо создания нового объекта для каждого символа, общие свойства шрифта (например, начертание) хранятся один раз.

```java
class Font {
    private String name; // Общее состояние
    public Font(String name) { this.name = name; }
    public String getName() { return name; }
}

class FontFactory {
    private Map<String, Font> fonts = new HashMap<>();
    public Font getFont(String name) {
        return fonts.computeIfAbsent(name, Font::new);
    }
}

class Character {
    private char symbol; // Уникальное состояние
    private Font font;   // Общее состояние
    public Character(char symbol, Font font) {
        this.symbol = symbol;
        this.font = font;
    }
    public String toString() { return symbol + " in " + font.getName(); }
}

public class Main {
    public static void main(String[] args) {
        FontFactory factory = new FontFactory();
        Character c1 = new Character('A', factory.getFont("Arial"));
        Character c2 = new Character('B', factory.getFont("Arial"));
        System.out.println(c1);
        System.out.println(c2);
        // Вывод: A in Arial, B in Arial
        // Один объект Font для Arial
    }
}
```
---

#### Interpreter (Интерпретатор)

Интерпретатор определяет представление грамматики языка и интерпретирует выражения на этом языке. Это как переводчик, который разбирает фразу и объясняет её смысл.

**Когда использовать?**

- Когда нужно интерпретировать выражения на простом языке (например, математические выражения, SQL).
- Когда язык имеет чёткую грамматику.

**Пример из жизни**:  
Калькулятор: вводите выражение вроде "2 + 3", и интерпретатор вычисляет результат.

**Пример кода**:

```java
interface Expression {
    int interpret();
}

class NumberExpression implements Expression {
    private int number;
    public NumberExpression(int number) { this.number = number; }
    public int interpret() { return number; }
}

class PlusExpression implements Expression {
    private Expression left, right;
    public PlusExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }
    public int interpret() { return left.interpret() + right.interpret(); }
}

public class Main {
    public static void main(String[] args) {
        Expression expr = new PlusExpression(new NumberExpression(2), new NumberExpression(3));
        System.out.println(expr.interpret());
        // Вывод: 5
    }
}
```
---

#### Singleton (Одиночка)


Одиночка гарантирует, что у класса будет только один экземпляр, и предоставляет глобальную точку доступа к нему. Это как один общий принтер в офисе.

**Когда использовать?**

- Когда нужен ровно один экземпляр класса (например, для логгера, конфигурации).
- Когда требуется глобальный доступ к этому экземпляру.

**Пример из жизни**:  
Единственный логгер в приложении, записывающий все события в один файл.

**Пример кода**:

```java
class Logger {
    private static Logger instance;
    private Logger() {} // Приватный конструктор
    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }
    public void log(String message) { System.out.println("Log: " + message); }
}

public class Main {
    public static void main(String[] args) {
        Logger logger = Logger.getInstance();
        logger.log("Test message");
        // Вывод: Log: Test message
    }
}
```
**Осторожно**:

- Может нарушать SOLID в сложных системах (например, затруднять тестирование).
- Используйте только при явной необходимости.

---

#### Strategy (Стратегия)

**Что это?**  
Стратегия позволяет определять семейство алгоритмов и выбирать один из них во время выполнения. Это как выбор способа оплаты: наличные, карта или PayPal.

**Когда использовать?**

- Когда нужно переключать алгоритмы динамически.
- Когда много условий в коде можно заменить стратегиями.

**Пример из жизни**:  
Оплата в интернет-магазине: выбор между оплатой картой или PayPal.

**Пример кода**:

```java
interface PaymentStrategy {
    void pay(double amount);
}

class CreditCardPayment implements PaymentStrategy {
    public void pay(double amount) { System.out.println("Paid $" + amount + " with Credit Card"); }
}

class PayPalPayment implements PaymentStrategy {
    public void pay(double amount) { System.out.println("Paid $" + amount + " with PayPal"); }
}

class ShoppingCart {
    private PaymentStrategy paymentStrategy;
    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }
    public void checkout(double amount) { paymentStrategy.pay(amount); }
}

public class Main {
    public static void main(String[] args) {
        ShoppingCart cart = new ShoppingCart();
        cart.setPaymentStrategy(new CreditCardPayment());
        cart.checkout(100.0);
        // Вывод: Paid $100.0 with Credit Card
    }
}
```
---

#### Adapter (Адаптер)

**Что это?**  
Адаптер позволяет объектам с несовместимыми интерфейсами работать вместе. Это как переходник для розетки, чтобы подключить устройство с другим типом вилки.

**Когда использовать?**

- Когда нужно интегрировать старый или сторонний код с новым интерфейсом.
- Когда интерфейсы несовместимы.

**Пример из жизни**:  
Переходник для зарядки телефона с USB-C на старый разъём.

**Пример кода**:

```java
interface NewPrinter {
    void print(String text);
}

class OldPrinter {
    public void printOld(String text) { System.out.println("Old Printer: " + text); }
}

class PrinterAdapter implements NewPrinter {
    private OldPrinter oldPrinter;
    public PrinterAdapter(OldPrinter oldPrinter) { this.oldPrinter = oldPrinter; }
    public void print(String text) { oldPrinter.printOld(text); }
}

public class Main {
    public static void main(String[] args) {
        NewPrinter printer = new PrinterAdapter(new OldPrinter());
        printer.print("Test");
        // Вывод: Old Printer: Test
    }
}
```

---

#### Facade (Фасад)

**Что это?**  
Фасад предоставляет упрощённый интерфейс для сложной подсистемы. Это как пульт управления умным домом, который скрывает сложность управления светом, климатом и т.д.

**Когда использовать?**

- Когда нужно упростить взаимодействие с сложной системой.
- Когда подсистема состоит из множества классов.

**Пример из жизни**:  
Пульт умного дома: одно нажатие включает свет, музыку и кондиционер.

**Пример кода**:

```java
class Light { public void turnOn() { System.out.println("Light ON"); } }
class Music { public void play() { System.out.println("Music playing"); } }
class AirConditioner { public void start() { System.out.println("AC ON"); } }

class SmartHomeFacade {
    private Light light = new Light();
    private Music music = new Music();
    private AirConditioner ac = new AirConditioner();
    public void startEveningMode() {
        light.turnOn();
        music.play();
        ac.start();
    }
}

public class Main {
    public static void main(String[] args) {
        SmartHomeFacade facade = new SmartHomeFacade();
        facade.startEveningMode();
        // Вывод: Light ON, Music playing, AC ON
    }
}
```
---

#### Proxy (Прокси)

**Что это?**  
Прокси управляет доступом к объекту, добавляя дополнительную логику (например, проверку прав, кэширование). Это как секретарь, который решает, пропустить ли вас к боссу.

**Когда использовать?**

- Когда нужно контролировать доступ, кэшировать данные или отложить создание объекта.
- Для защиты, логирования или удалённого доступа.

**Пример из жизни**:  
Секретарь, который проверяет ваш пропуск перед встречей с директором.

**Пример кода**:

```java
interface Image {
    void display();
}

class RealImage implements Image {
    private String filename;
    public RealImage(String filename) {
        this.filename = filename;
        System.out.println("Loading " + filename);
    }
    public void display() { System.out.println("Displaying " + filename); }
}

class ProxyImage implements Image {
    private RealImage realImage;
    private String filename;
    public ProxyImage(String filename) { this.filename = filename; }
    public void display() {
        if (realImage == null) {
            realImage = new RealImage(filename);
        }
        realImage.display();
    }
}

public class Main {
    public static void main(String[] args) {
        Image image = new ProxyImage("photo.jpg");
        image.display();
        // Вывод: Loading photo.jpg, Displaying photo.jpg
        image.display(); // Без повторной загрузки
        // Вывод: Displaying photo.jpg
    }
}
```
