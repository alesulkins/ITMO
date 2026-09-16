
3.1. Создать task, которая создаёт директорию
Решение 3.1
tasks.register("createDir") {
    def dir = layout.buildDirectory.dir("generated")
    outputs.dir(dir)

    doLast {
        dir.get().asFile.mkdirs()
    }
}
Лучше объявить outputs.dir, чем просто создать папку: Gradle получает информацию о результате task.

3.2. Три способа создания директории
Решение 3.2
1. File API
tasks.register("dir1") {
    doLast { file("$buildDir/a").mkdirs() }
}
2. Gradle mkdir
tasks.register("dir2") {
    doLast { mkdir("$buildDir/b") }
}
3. Provider API
tasks.register("dir3") {
    def target = layout.buildDirectory.dir("c")
    outputs.dir(target)
    doLast { target.get().asFile.mkdirs() }
}
Для современного Gradle третий вариант лучше сочетается с lazy configuration.

3.3. Вынести создание директории в отдельную task
Решение 3.3
def createGeneratedDir = tasks.register("createGeneratedDir") {
    def target = layout.buildDirectory.dir("generated")
    outputs.dir(target)
    doLast { target.get().asFile.mkdirs() }
}
3.4. Вторая task использует результат первой через inputs/outputs
Решение 3.4
def generatedFile = layout.buildDirectory.file("generated/input.txt")

def producer = tasks.register("producer") {
    outputs.file(generatedFile)
    doLast {
        def f = generatedFile.get().asFile
        f.parentFile.mkdirs()
        f.text = "hello"
    }
}

tasks.register("consumer") {
    inputs.file(producer.flatMap { generatedFile })
    doLast {
        println generatedFile.get().asFile.text
    }
}
3.5. dependsOn
Решение 3.5
def a = tasks.register("A") {
    doLast { println "A" }
}

tasks.register("B") {
    dependsOn(a)
    doLast { println "B" }
}
./gradlew B включает A.

3.6. mustRunAfter
Решение 3.6
def a = tasks.register("A") {
    doLast { println "A" }
}

tasks.register("B") {
    mustRunAfter(a)
    doLast { println "B" }
}
./gradlew B запускает только B. При совместном вызове A и B порядок будет A -> B.

3.7. shouldRunAfter
Решение 3.7
tasks.register("A")
tasks.register("B") {
    shouldRunAfter("A")
}
mustRunAfter — жёсткий порядок, shouldRunAfter — мягкое предпочтение.

3.8. dependsOn + mustRunAfter
Решение 3.8
def a = tasks.register("A")
def b = tasks.register("B") {
    mustRunAfter(a)
}

tasks.register("C") {
    dependsOn(a, b)
}
dependsOn включает A и B в graph, mustRunAfter задаёт порядок между ними.

3.9. Цепочка A -> B -> C
Решение 3.9
def a = tasks.register("A") { doLast { println "A" } }
def b = tasks.register("B") {
    dependsOn(a)
    doLast { println "B" }
}
tasks.register("C") {
    dependsOn(b)
    doLast { println "C" }
}
3.10. Input property
Решение 3.10
tasks.register("makeDir") {
    def dirName = providers.gradleProperty("dirName").orElse("default")
    inputs.property("dirName", dirName)
    doLast {
        mkdir(layout.buildDirectory.dir(dirName.get()).get().asFile)
    }
}
Запуск:

./gradlew makeDir -PdirName=test
3.11. Input file + output directory
Решение 3.11
def source = layout.projectDirectory.file("input.txt")
def out = layout.buildDirectory.dir("processed")

tasks.register("processInput") {
    inputs.file(source)
    outputs.dir(out)

    doLast {
        def target = out.get().asFile
        target.mkdirs()
        new File(target, "result.txt").text = source.asFile.text.toUpperCase()
    }
}
Первый запуск — execute.
Второй — UP-TO-DATE.
Изменить input.txt.
Task снова выполняется.
3.12. Неправильный input
Решение 3.12
Проблема:

def a = file("a.txt")
def b = file("b.txt")

tasks.register("broken") {
    inputs.file(a)
    outputs.file("$buildDir/result.txt")
    doLast {
        file("$buildDir/result.txt").text = a.text + b.text
    }
}
b.txt влияет на output, но не объявлен input. Изменение b.txt может не инвалидировать task.

Исправление:

inputs.files(a, b)
3.13. generated.txt -> consumer
Решение 3.13
def generated = layout.buildDirectory.file("generated.txt")

def generate = tasks.register("generateFile") {
    outputs.file(generated)
    doLast { generated.get().asFile.text = "generated content" }
}

tasks.register("readGenerated") {
    inputs.file(generate.flatMap { generated })
    doLast { println generated.get().asFile.text }
}
3.14. Cacheable custom task
Решение 3.14
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*

@CacheableTask
abstract class GenerateText extends DefaultTask {
    @Input
    abstract Property<String> getText()

    @OutputFile
    abstract RegularFileProperty getOutputFile()

    @TaskAction
    void generate() {
        def f = outputFile.get().asFile
        f.parentFile.mkdirs()
        f.text = text.get()
    }
}

tasks.register("generateText", GenerateText) {
    text.set("hello")
    outputFile.set(layout.buildDirectory.file("generated/text.txt"))
}
Лучший вариант из перечисленных для серьёзной custom task: контракт inputs/outputs выражен декларативно.

3.15. Почему dependsOn не передаёт результат
Ответ 3.15
dependsOn говорит только, что prerequisite task должна выполняться. Он не описывает, какой файл был произведён и что следующая task его читает. Data flow нужно описывать через outputs/inputs и Provider API.

3.16. C.dependsOn(A,B), но нужен A -> B -> C
Решение 3.16
def a = tasks.register("A")
def b = tasks.register("B") { mustRunAfter(a) }
tasks.register("C") { dependsOn(a, b) }
3.17. A/B независимы, но при совместном запуске A раньше B
Решение 3.17
tasks.register("A")
tasks.register("B") { mustRunAfter("A") }
3.18. finalizedBy
Решение 3.18
def cleanup = tasks.register("cleanupTemp") {
    doLast { delete(layout.buildDirectory.dir("tmp")) }
}

tasks.named("test") {
    finalizedBy(cleanup)
}
3.20. Custom task class
Решение 3.20
abstract class CreateDirectoryTask extends DefaultTask {
    @OutputDirectory
    abstract DirectoryProperty getTargetDir()

    @TaskAction
    void create() {
        targetDir.get().asFile.mkdirs()
    }
}
3.21. tasks.register
Ответ 3.21
register() создаёт lazy registration. Task object не нужно немедленно создавать/configure, если она не понадобится текущему build. Это уменьшает configuration time.

3.22. Абсолютный путь и cache
Решение 3.22
Плохо:

inputs.property("projectPath", projectDir.absolutePath)
На другой машине путь другой, даже если содержимое проекта одинаково. Если путь не является семантически значимым input, не включать его в contract.

3. Gradle Build Cache — задачи 3.23–3.32
3.1. Local и remote cache
Local build cache хранится на одной машине.

Remote build cache общий для нескольких developers/CI agents.

CI A -> cached output -> remote cache -> developer B
Это работает безопасно только для repeatable/relocatable tasks.

3.2. Build cache != dependency cache
Dependency cache хранит скачанные внешние artifacts. Build cache хранит outputs Gradle tasks.

3.3. Практический сценарий
Базовая task для всех экспериментов с build cache (тут для всех 3.23-3.32 - для каждой задачи свои блоки)
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.*

@CacheableTask
abstract class CachedUppercaseTask extends DefaultTask {

    @InputFile
    @PathSensitive(PathSensitivity.RELATIVE)
    abstract RegularFileProperty getInputFile()

    @OutputFile
    abstract RegularFileProperty getOutputFile()

    @TaskAction
    void transform() {
        def input = inputFile.get().asFile
        def output = outputFile.get().asFile

        output.parentFile.mkdirs()
        output.text = input.text.toUpperCase()
    }
}

tasks.register("cachedUppercase", CachedUppercaseTask) {
    inputFile.set(layout.projectDirectory.file("input.txt"))
    outputFile.set(layout.buildDirectory.file("cache-demo/result.txt"))
}
Решение 3.23–3.30
3.29. False cache hit
Решение 3.29
Если config.txt влияет на output, но не объявлен input, Gradle может восстановить результат, созданный для старой конфигурации. Это опаснее обычного cache miss, потому что build может формально быть successful, но output неверен.

3.31. Timestamp
Решение 3.31
Task, которая всегда пишет текущее время, не имеет repeatable output при тех же inputs. Лучше убрать timestamp либо вынести volatile metadata в отдельную дешёвую non-cacheable task.

3.32. Cache key
Ответ 3.32
Cache key — идентичность вычисления: implementation + declared inputs. Совпадение key позволяет восстановить outputs. Неполный input set создаёт риск false cache hit, лишний нестабильный input — лишний miss.

Официальные ресурсы:

https://docs.gradle.org/current/userguide/build_cache.html
https://docs.gradle.org/current/userguide/build_cache_concepts.html
https://docs.gradle.org/current/userguide/build_cache_debugging.html
4. JAR, Manifest и SHA-256
4.1. JAR
JAR — архив Java-приложения. Внутри него могут находиться:

скомпилированные .class-файлы;
ресурсы;
настройки;
служебные данные;
файл META-INF/MANIFEST.MF.
Пример:

app.jar
├── META-INF/
│   └── MANIFEST.MF
├── com/example/Main.class
└── application.properties
JAR физически является архивом. Поэтому изменение любого файла внутри JAR меняет содержимое самого JAR.

4.2. Manifest
Manifest — служебный файл внутри JAR:

META-INF/MANIFEST.MF
В нём можно хранить сведения о приложении:

Application-Name: Lab3
Implementation-Version: 1.0.0
Main-Class: com.example.Main
Manifest является частью JAR, поэтому изменение manifest изменяет и контрольную сумму JAR.

4.3. SHA-256
SHA-256 — алгоритм, который получает произвольный набор байтов и вычисляет для него значение фиксированной длины — контрольную сумму.

Логика:

байты JAR
    ↓
SHA-256
    ↓
контрольная сумма
Если внутри JAR изменится хотя бы один файл, manifest или ресурс, последовательность байтов JAR изменится, поэтому изменится и SHA-256.

3.33. Добавить в JAR manifest имя приложения
Решение 3.33
В build.gradle:

plugins {
    id "java"
}

tasks.named("jar") {
    manifest {
        attributes(
            "Application-Name": "Lab3"
        )
    }
}
Запуск:

./gradlew jar
После сборки внутри JAR появится:

META-INF/MANIFEST.MF
Содержимое будет примерно таким:

Manifest-Version: 1.0
Application-Name: Lab3
tasks.named("jar") получает уже существующую задачу jar, созданную Java-плагином.

manifest {} настраивает manifest будущего JAR.

attributes(...) добавляет в него атрибуты.

3.34. Добавить version и build timestamp в manifest
Решение 3.34
Полный вариант:

plugins {
    id "java"
}

group = "com.example"
version = "1.0.0"

tasks.named("jar") {
    manifest {
        attributes(
            "Application-Name": "Lab3",
            "Implementation-Version": project.version,
            "Build-Timestamp": new Date().format("yyyy-MM-dd HH:mm:ss")
        )
    }
}
После сборки:

Manifest-Version: 1.0
Application-Name: Lab3
Implementation-Version: 1.0.0
Build-Timestamp: 2026-09-08 01:30:15
project.version получает версию проекта.

new Date() получает текущие дату и время.

Важный недостаток: время сборки постоянно меняется.

Поэтому даже при неизменном исходном коде:

сборка №1 → один JAR
сборка №2 → другой timestamp → другие байты JAR
Контрольная сумма тоже будет различаться.

Для задания это допустимо, потому что timestamp прямо требуется добавить.

3.35. Добавить Main-Class
Решение 3.35
Полный вариант:

plugins {
    id "java"
}

group = "com.example"
version = "1.0.0"

tasks.named("jar") {
    manifest {
        attributes(
            "Application-Name": "Lab3",
            "Implementation-Version": project.version,
            "Build-Timestamp": new Date().format("yyyy-MM-dd HH:mm:ss"),
            "Main-Class": "com.example.Main"
        )
    }
}
Main-Class указывает класс, содержащий:

public static void main(String[] args)
Например:

package com.example;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello");
    }
}
После этого JAR можно запускать:

java -jar build/libs/<имя-файла>.jar
3.36. Добавить несколько произвольных атрибутов
Решение 3.36
plugins {
    id "java"
}

group = "com.example"
version = "1.0.0"

tasks.named("jar") {
    manifest {
        attributes(
            "Application-Name": "Lab3",
            "Implementation-Version": project.version,
            "Build-Timestamp": new Date().format("yyyy-MM-dd HH:mm:ss"),
            "Main-Class": "com.example.Main",
            "Built-By": System.getProperty("user.name"),
            "Project-Group": project.group.toString(),
            "Environment": "development",
            "Description": "OPI laboratory work 3"
        )
    }
}
Здесь дополнительно записываются:

пользователь, который выполнял сборку;
группа проекта;
окружение;
описание приложения.
3.37. Сделать manifest из Gradle
Решение 3.37
Итоговый вариант:

plugins {
    id "java"
}

group = "com.example"
version = "1.0.0"

tasks.named("jar") {
    manifest {
        attributes(
            "Application-Name": "Lab3",
            "Implementation-Version": project.version,
            "Build-Timestamp": new Date().format("yyyy-MM-dd HH:mm:ss"),
            "Main-Class": "com.example.Main",
            "Built-By": System.getProperty("user.name"),
            "Project-Group": project.group.toString(),
            "Environment": "development",
            "Description": "OPI laboratory work 3"
        )
    }
}
Сборка:

./gradlew jar
Посмотреть manifest на macOS:

unzip -p build/libs/*.jar META-INF/MANIFEST.MF
3.38. Сделать так, чтобы manifest влиял на hash JAR
Решение 3.38
Дополнительного специального механизма не требуется.

Manifest находится внутри JAR, поэтому автоматически входит в данные, для которых считается SHA-256.

Исходный вариант:

plugins {
    id "java"
}

group = "com.example"
version = "1.0.0"

tasks.named("jar") {
    manifest {
        attributes(
            "Application-Name": "Lab3",
            "Implementation-Version": project.version,
            "Main-Class": "com.example.Main",
            "Environment": "development"
        )
    }
}
Собрать:

./gradlew clean jar
На macOS:

shasum -a 256 build/libs/*.jar
Затем изменить manifest:

plugins {
    id "java"
}

group = "com.example"
version = "1.0.0"

tasks.named("jar") {
    manifest {
        attributes(
            "Application-Name": "Lab3",
            "Implementation-Version": project.version,
            "Main-Class": "com.example.Main",
            "Environment": "production"
        )
    }
}
Снова:

./gradlew clean jar
shasum -a 256 build/libs/*.jar
Полученная SHA-256 будет другой:

изменился MANIFEST.MF
        ↓
изменились байты JAR
        ↓
изменилась SHA-256
3.39. Посчитать SHA-256 для JAR после сборки
Решение 3.39
plugins {
    id "java"
}

group = "com.example"
version = "1.0.0"

tasks.named("jar") {
    manifest {
        attributes(
            "Application-Name": "Lab3",
            "Implementation-Version": project.version,
            "Main-Class": "com.example.Main"
        )
    }
}
Собрать:

./gradlew clean jar
На macOS:

shasum -a 256 build/libs/*.jar
На Linux обычно:

sha256sum build/libs/*.jar
Результат:

a2d7f3...91ce  build/libs/lab3-1.0.0.jar
3.40. Изменить один ресурс и показать изменение hash
Решение 3.40
build.gradle:

plugins {
    id "java"
}

group = "com.example"
version = "1.0.0"

tasks.named("jar") {
    manifest {
        attributes(
            "Application-Name": "Lab3",
            "Implementation-Version": project.version,
            "Main-Class": "com.example.Main"
        )
    }
}
Создать:

src/main/resources/demo.txt
Содержимое:

A
Выполнить:

./gradlew clean jar
shasum -a 256 build/libs/*.jar
Теперь заменить:

A
на:

B
И снова:

./gradlew clean jar
shasum -a 256 build/libs/*.jar
SHA-256 изменится, потому что изменилось содержимое JAR.

3.41. Создать отдельную Gradle-task для checksum
Решение 3.41
import java.security.MessageDigest

plugins {
    id "java"
}

group = "com.example"
version = "1.0.0"

tasks.named("jar") {
    manifest {
        attributes(
            "Application-Name": "Lab3",
            "Implementation-Version": project.version,
            "Main-Class": "com.example.Main"
        )
    }
}

def jarProvider = tasks.named("jar").flatMap {
    it.archiveFile
}

def checksumFile = layout.buildDirectory.file(
    "checksums/app.jar.sha256"
)

def checksumJar = tasks.register("checksumJar") {
    dependsOn(tasks.named("jar"))

    inputs.file(jarProvider)
    outputs.file(checksumFile)

    doLast {
        def jarFile = jarProvider.get().asFile

        byte[] bytes = jarFile.bytes

        byte[] digest = MessageDigest
            .getInstance("SHA-256")
            .digest(bytes)

        String hex = digest
            .collect {
                String.format("%02x", it)
            }
            .join()

        def output = checksumFile.get().asFile

        output.parentFile.mkdirs()

        output.text =
            hex + "  " + jarFile.name + "\n"
    }
}
Запуск:

./gradlew checksumJar
Сначала выполнится jar, потому что:

dependsOn(tasks.named("jar"))
Затем SHA-256 будет записана в:

build/checksums/app.jar.sha256
3.42. Сделать checksum частью build/package/publish
Решение 3.42
import java.security.MessageDigest

plugins {
    id "java"
    id "maven-publish"
}

group = "com.example"
version = "1.0.0"

tasks.named("jar") {
    manifest {
        attributes(
            "Application-Name": "Lab3",
            "Implementation-Version": project.version,
            "Main-Class": "com.example.Main"
        )
    }
}

def jarProvider = tasks.named("jar").flatMap {
    it.archiveFile
}

def checksumFile = layout.buildDirectory.file(
    "checksums/app.jar.sha256"
)

def checksumJar = tasks.register("checksumJar") {
    dependsOn(tasks.named("jar"))

    inputs.file(jarProvider)
    outputs.file(checksumFile)

    doLast {
        def jarFile = jarProvider.get().asFile

        byte[] bytes = jarFile.bytes

        byte[] digest = MessageDigest
            .getInstance("SHA-256")
            .digest(bytes)

        String hex = digest
            .collect {
                String.format("%02x", it)
            }
            .join()

        def output = checksumFile.get().asFile

        output.parentFile.mkdirs()

        output.text =
            hex + "  " + jarFile.name + "\n"
    }
}

tasks.named("build") {
    finalizedBy(checksumJar)
}

tasks.named("assemble") {
    finalizedBy(checksumJar)
}

tasks.named("publish") {
    dependsOn(checksumJar)
}

publishing {
    publications {
        mavenJava(MavenPublication) {
            from components.java

            artifact(checksumFile) {
                builtBy(checksumJar)
                extension = "sha256"
            }
        }
    }
}
Получается:

jar
 ↓
checksumJar
 ↓
build / assemble / publish
5. Docker
5.1. Зачем нужен Docker
Обычное приложение зависит не только от исходного кода.

Для его работы могут требоваться:

определённая версия Java;
системные библиотеки;
PostgreSQL;
переменные окружения;
файлы конфигурации;
конкретные сетевые настройки.
Например приложение работает на одном компьютере, потому что там установлена Java 21, а на другом установлена Java 17 и приложение уже не запускается.

Docker позволяет описать окружение приложения явно.

Идея:

исходный код
+
версия Java
+
системные зависимости
+
команда запуска
        ↓
      образ
        ↓
одинаковое окружение запуска
Это особенно важно для лабораторной, потому что сборка, тестирование и запуск должны быть воспроизводимыми.

5.2. Образ
Образ Docker (image) — подготовленный шаблон файловой системы и настроек, из которого затем запускаются контейнеры.

Образ может содержать:

Java;
JAR приложения;
системные библиотеки;
рабочую директорию;
команду запуска.
Образ сам по себе не является работающей программой.

Можно воспринимать его как неизменяемую заготовку.

5.3. Контейнер
Контейнер (container) — запущенный экземпляр образа.

Из одного образа можно создать несколько контейнеров:

             образ приложения
             /             \
            ↓               ↓
     контейнер №1      контейнер №2
Контейнер содержит работающий процесс приложения.

5.4. Почему контейнер не является полноценной виртуальной машиной
Виртуальная машина обычно имеет собственную гостевую операционную систему и собственное ядро.

Контейнер использует ядро основной системы.

Изоляцию обеспечивают механизмы ядра операционной системы.

Поэтому контейнеры обычно легче виртуальных машин.

5.5. Dockerfile
Dockerfile — текстовый файл, который описывает, как построить образ.

Основные команды:

FROM
Выбирает базовый образ.

Например:

FROM eclipse-temurin:21-jdk
означает, что внутри будущего образа будет Java 21 JDK.

WORKDIR
Устанавливает рабочую директорию.

WORKDIR /app
После этого следующие команды работают относительно /app.

COPY
Копирует файлы проекта внутрь образа.

RUN
Выполняет команду во время построения образа.

Например:

RUN ./gradlew build
CMD / ENTRYPOINT
Определяют, какую программу запускать при запуске контейнера.

Ключевая разница:

RUN
→ выполняется при docker build

ENTRYPOINT / CMD
→ выполняется при docker run
5.6. Контекст сборки
Когда выполняется:

docker build .
точка означает текущую директорию.

Docker получает её как контекст сборки.

Из этого контекста команды COPY могут брать файлы.

5.7. .dockerignore
.dockerignore позволяет не передавать Docker лишние файлы.

Например:

.git
.gradle
build
*.log
Это:

уменьшает контекст сборки;
ускоряет сборку;
уменьшает риск случайно положить лишние файлы в образ.
5.8. Слой образа
Образ состоит из слоёв (layers).

Каждый этап Dockerfile может создавать новый слой файловой системы.

Например:

базовая Java
      ↓
скопировали Gradle
      ↓
скопировали исходный код
      ↓
собрали приложение
Docker может повторно использовать неизменившиеся слои.

Это ускоряет повторные сборки.

5.9. Многоэтапная сборка
Часто для сборки приложения нужен полный JDK и Gradle.

Но после сборки для запуска нужен только JRE и готовый JAR.

Поэтому удобно использовать два этапа:

этап build
JDK + Gradle + исходный код
        ↓
      JAR
        ↓
этап runtime
JRE + JAR
Первый этап используется только для сборки.

Второй становится итоговым образом.

Преимущества:

итоговый образ меньше;
внутри нет исходников;
внутри нет Gradle;
меньше лишних инструментов.
5.10. Переменные окружения
Параметры приложения не обязательно записывать прямо в образ.

Например адрес базы данных можно передать при запуске:

DB_HOST=db
DB_PORT=5432
Один и тот же образ можно запускать с разными настройками.

5.11. Том
Том (volume) — хранилище данных, которым управляет Docker.

Он нужен, когда данные должны пережить удаление контейнера.

Типичный пример — PostgreSQL.

контейнер PostgreSQL
       ↓
      том
       ↓
данные сохраняются
Удалили контейнер — данные в томе остаются.

5.12. Привязка каталога
Привязка каталога (bind mount) — подключение конкретного файла или директории основной системы внутрь контейнера.

Например:

MacBook:
./config

        ↓

контейнер:
/app/config
Это удобно для:

конфигурации;
разработки;
наблюдения за изменениями файлов.
5.13. Сеть Docker
Каждый контейнер имеет своё сетевое окружение.

Если приложение и PostgreSQL находятся в одной Docker-сети, приложение может обращаться к базе по имени сервиса.

Например:

app → db:5432
Очень важно:

localhost внутри контейнера означает сам этот контейнер.

Если PostgreSQL находится в другом контейнере, использовать:

localhost:5432
обычно неправильно.

Нужно обращаться к имени сервиса:

db:5432
5.14. Docker Compose
Docker Compose позволяет описать несколько связанных контейнеров в одном файле.

Например:

приложение
     ↓
PostgreSQL
Можно в одном compose.yaml описать:

приложение;
базу;
сеть;
том;
переменные окружения;
зависимости между сервисами.
Практика Docker
3.43. Написать Dockerfile для проекта
Решение 3.43
Самый простой вариант:

FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN chmod +x gradlew

RUN ./gradlew clean build --no-daemon

ENTRYPOINT ["java", "-jar", "build/libs/app.jar"]
Что происходит:

FROM берёт базовый образ с Java.

WORKDIR /app создаёт рабочую директорию.

COPY . . копирует проект внутрь образа.

RUN chmod +x gradlew разрешает запуск Gradle Wrapper.

RUN ./gradlew clean build собирает проект внутри образа.

ENTRYPOINT определяет команду запуска контейнера.

Если твой JAR называется иначе, путь:

build/libs/app.jar
нужно заменить.

3.44. Собрать образ
Решение 3.44
Dockerfile:

FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN chmod +x gradlew

RUN ./gradlew clean build --no-daemon

ENTRYPOINT ["java", "-jar", "build/libs/app.jar"]
Сборка:

docker build -t lab3-app .
docker build запускает построение образа.

-t lab3-app задаёт имя образа.

. указывает текущую директорию как контекст сборки.

После этого проверить:

docker images
3.45. Запустить контейнер
Решение 3.45
Dockerfile:

FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN chmod +x gradlew

RUN ./gradlew clean build --no-daemon

ENTRYPOINT ["java", "-jar", "build/libs/app.jar"]
Сначала:

docker build -t lab3-app .
Затем:

docker run --rm lab3-app
Если приложение слушает порт 8080:

docker run --rm -p 8080:8080 lab3-app
Здесь:

8080 слева  → порт MacBook
8080 справа → порт контейнера
--rm удаляет контейнер после остановки.

3.46. Передать параметры через переменные окружения
Решение 3.46
Dockerfile:

FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN chmod +x gradlew

RUN ./gradlew clean build --no-daemon

ENTRYPOINT ["java", "-jar", "build/libs/app.jar"]
Запуск:

docker run --rm \
  -e DB_HOST=db \
  -e DB_PORT=5432 \
  -e APP_MODE=test \
  lab3-app
В контейнер передаются:

DB_HOST=db
DB_PORT=5432
APP_MODE=test
Java-приложение может прочитать их через:

System.getenv("DB_HOST")
Преимущество: параметры не записываются непосредственно в образ.

3.47. Передать файл конфигурации через bind mount
Решение 3.47
Пусть на Mac есть:

./config/application.properties
Dockerfile:

FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN chmod +x gradlew

RUN ./gradlew clean build --no-daemon

ENTRYPOINT ["java", "-jar", "build/libs/app.jar"]
Запуск:

docker run --rm \
  --mount type=bind,src="$PWD/config",dst=/app/config,readonly \
  lab3-app
Получается:

Mac:
./config

      ↓

контейнер:
/app/config
readonly запрещает контейнеру изменять файлы на Mac.

Образ при этом менять не нужно.

3.48. Сделать многоэтапную сборку
Решение 3.48
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY . .

RUN chmod +x gradlew

RUN ./gradlew clean build --no-daemon


FROM eclipse-temurin:21-jre AS runtime

WORKDIR /app

COPY --from=build /app/build/libs/app.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
Первый этап:

build
содержит:

JDK;
Gradle Wrapper;
исходный код.
Он собирает JAR.

Второй этап:

runtime
содержит только:

JRE;
готовый JAR.
В итоговый образ не попадают исходники и Gradle.

3.49. Сделать stage build и stage runtime
Решение 3.49
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN chmod +x gradlew
RUN ./gradlew clean build --no-daemon


FROM eclipse-temurin:21-jre AS runtime

WORKDIR /app

COPY --from=build /app/build/libs/app.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
AS build даёт имя первому этапу.

AS runtime даёт имя второму.

Строка:

COPY --from=build ...
копирует JAR из первого этапа во второй.

3.50. Запускать сборку проекта внутри контейнера
Решение 3.50
Dockerfile:

FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN chmod +x gradlew

RUN ./gradlew clean build --no-daemon
Выполнить:

docker build -t lab3-build .
Во время docker build выполняется:

./gradlew clean build
Причём Gradle запускается не на MacBook, а внутри временного контейнерного окружения сборки.

Поэтому версия Java и остальные условия сборки определяются Dockerfile.

3.51. Запускать unit tests внутри контейнера
Решение 3.51
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN chmod +x gradlew

RUN ./gradlew clean test --no-daemon
RUN ./gradlew build --no-daemon


FROM eclipse-temurin:21-jre AS runtime

WORKDIR /app

COPY --from=build /app/build/libs/app.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
Здесь unit tests выполняются командой:

./gradlew clean test
Если хотя бы один тест падает, команда возвращает ошибку.

Следовательно Docker-сборка тоже завершается ошибкой.

То есть невозможно получить итоговый образ через этот Dockerfile, если unit tests не прошли.

3.52. Запускать функциональные тесты в контейнеризированном окружении
Решение 3.52
Один из вариантов — поднять:

PostgreSQL
+
приложение
+
Playwright
через Docker Compose.

Dockerfile приложения:

FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY . .

RUN chmod +x gradlew
RUN ./gradlew clean build --no-daemon


FROM eclipse-temurin:21-jre AS runtime

WORKDIR /app

COPY --from=build /app/build/libs/app.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
compose.yaml:

services:

  db:
    image: postgres:18
    environment:
      POSTGRES_DB: app
      POSTGRES_USER: app
      POSTGRES_PASSWORD: test-password

  app:
    build: .
    environment:
      DB_HOST: db
      DB_PORT: 5432
      DB_NAME: app
      DB_USER: app
      DB_PASSWORD: test-password
    ports:
      - "8080:8080"
    depends_on:
      - db

  functional-tests:
    image: mcr.microsoft.com/playwright:<VERSION>-noble
    working_dir: /tests
    volumes:
      - ./functional-tests:/tests
    command: npx playwright test
    depends_on:
      - app
<VERSION> нужно заменить на версию Playwright, которая используется в проекте.

Запуск:

docker compose up --build --abort-on-container-exit
В результате функциональные тесты запускаются в том же контейнеризированном окружении, где находятся приложение и база данных.

На практике желательно дополнительно добавить проверку готовности приложения, потому что depends_on не означает автоматически, что приложение уже полностью готово принимать запросы.

3.53. Собрать две ревизии проекта в одной среде и показать воспроизводимость
Решение 3.53
Используем один и тот же Dockerfile:

FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY . .

RUN chmod +x gradlew
RUN ./gradlew clean test build --no-daemon


FROM eclipse-temurin:21-jre AS runtime

WORKDIR /app

COPY --from=build /app/build/libs/app.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
Сначала первая ревизия Git:

git checkout <REVISION_1>
docker build -t lab3:rev1 .
Затем вторая:

git checkout <REVISION_2>
docker build -t lab3:rev2 .
Обе ревизии собираются:

одной версией Java;
одним Dockerfile;
одинаковым набором системных библиотек;
одинаковой последовательностью команд.
Затем можно запустить обе:

docker run --rm lab3:rev1
docker run --rm lab3:rev2
Идея воспроизводимости здесь состоит не в том, что разные ревизии обязаны дать одинаковый JAR.

У них разный исходный код, поэтому артефакты могут различаться.

Воспроизводится окружение сборки.

Более строгий вариант — дополнительно фиксировать базовые Docker-образы по точной версии или digest.

3.54. Сделать Docker Compose для приложения и PostgreSQL
Решение 3.54
Dockerfile:

FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY . .

RUN chmod +x gradlew
RUN ./gradlew clean build --no-daemon


FROM eclipse-temurin:21-jre AS runtime

WORKDIR /app

COPY --from=build /app/build/libs/app.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
compose.yaml:

services:

  db:
    image: postgres:18
    environment:
      POSTGRES_DB: app
      POSTGRES_USER: app
      POSTGRES_PASSWORD: password
    volumes:
      - postgres-data:/var/lib/postgresql/data

  app:
    build: .
    environment:
      DB_HOST: db
      DB_PORT: 5432
      DB_NAME: app
      DB_USER: app
      DB_PASSWORD: password
    ports:
      - "8080:8080"
    depends_on:
      - db

volumes:
  postgres-data:
Запуск:

docker compose up --build
Здесь:

app
 ↓
db
Приложение обращается к PostgreSQL по имени:

db
а не localhost.

Том:

postgres-data
сохраняет данные PostgreSQL независимо от жизненного цикла контейнера.

3.55. Передать пароль, не записывая его внутрь image
Решение 3.55
Плохой вариант:

ENV DB_PASSWORD=my-secret-password
Пароль становится частью образа.

Лучше хранить пароль отдельно.

Например создать:

secrets/db_password.txt
В файле находится:

very-secret-password
compose.yaml:

services:

  db:
    image: postgres:18
    environment:
      POSTGRES_DB: app
      POSTGRES_USER: app
      POSTGRES_PASSWORD_FILE: /run/secrets/db_password
    secrets:
      - db_password
    volumes:
      - postgres-data:/var/lib/postgresql/data

  app:
    build: .
    environment:
      DB_HOST: db
      DB_PORT: 5432
      DB_USER: app
      DB_PASSWORD_FILE: /run/secrets/db_password
    secrets:
      - db_password
    depends_on:
      - db

secrets:
  db_password:
    file: ./secrets/db_password.txt

volumes:
  postgres-data:
Теперь пароль не записан в Dockerfile.

Он передаётся контейнеру отдельным файлом:

/run/secrets/db_password
PostgreSQL умеет использовать:

POSTGRES_PASSWORD_FILE
Для собственного Java-приложения необходимо самостоятельно реализовать чтение файла, путь к которому приходит через DB_PASSWORD_FILE.

Для простой учебной работы допустим и .env, но секреты нельзя коммитить в Git.

3.56. Объяснить image / container / layer / volume / bind mount / network
Решение 3.56
Образ (image)
Шаблон, из которого создаются контейнеры.

Он содержит файловую систему приложения и настройки запуска.

Сам по себе не является работающим процессом.

Контейнер (container)
Запущенный экземпляр образа.

image
  ↓
container
Один образ может использоваться для запуска нескольких контейнеров.

Слой (layer)
Часть файловой системы образа.

Образ состоит из последовательности слоёв.

Docker может повторно использовать неизменившиеся слои и тем самым ускорять сборку.

Том (volume)
Хранилище данных, которым управляет Docker.

Используется для данных, которые должны переживать удаление контейнера.

Типичный пример:

PostgreSQL
    ↓
volume
    ↓
постоянные данные
Bind mount
Подключение конкретного файла или директории основной системы внутрь контейнера.

MacBook
./config
    ↓
container
/app/config
Отличие от volume:

volume управляется Docker;
bind mount связан с конкретным путём на основной машине.
Сеть (network)
Виртуальная сеть, через которую контейнеры взаимодействуют между собой.

В Docker Compose сервисы обычно могут обращаться друг к другу по имени.

Например:

app → db:5432
Короткая формулировка для защиты:

Image — шаблон. Container — запущенный экземпляр image. Layer — слой образа. Volume — постоянное хранилище Docker. Bind mount — подключение конкретного пути основной системы. Network — сеть для взаимодействия контейнеров.

7. File watching и Compose Watch — 3.57–3.64
7.1. Bind mount не является watcher
Bind mount только делает host files видимыми в container. Он не задаёт реакцию «файл изменился -> выполнить command».

7.2. Polling
Polling — периодическая проверка состояния.

каждую секунду:
  проверить hash/mtime
  если изменилось -> action
Плюсы: просто, универсально. Минусы: latency и лишние проверки.

7.3. Event-based watch
ОС может генерировать filesystem events. Linux использует inotify, macOS — FSEvents и т.д.

7.4. Compose Watch
Compose Watch может реагировать на file changes через sync/rebuild/restart workflow.

Он полезен, когда нужно:

исключать build artifacts;
sync только source;
rebuild image при изменении dependency file;
restart service.
Ресурс:
https://docs.docker.com/compose/how-tos/file-watch/

3.57. Bind mount
Решение 3.57
docker run --rm --mount type=bind,src="$PWD/watched",dst=/watched alpine
3.59. Polling watcher
Решение 3.59
#!/bin/sh
last=""
while true; do
    current="$(sha256sum /watched/input.txt 2>/dev/null || true)"
    if [ "$current" != "$last" ]; then
        echo "$current" > /watched/output.txt
        last="$current"
    fi
    sleep 1
done
3.60–3.62. Compose Watch
Решение 3.60–3.62
services:
  app:
    build: .
    develop:
      watch:
        - action: sync
          path: ./src
          target: /app/src
          ignore:
            - build/
        - action: rebuild
          path: ./build.gradle
Конкретные поддерживаемые actions сверять с установленной версией Compose.

3.63. Bind mount vs volume
Ответ 3.63
Bind mount связан с конкретным host path. Named volume управляется Docker и лучше подходит для persistent service state вроде БД.

3.64. Watch vs bind mount
Ответ 3.64
Bind mount — storage mapping. Compose Watch — development workflow с семантикой file change -> sync/rebuild/restart.

8. PostgreSQL 16 -> 18 — 3.65–3.72
8.1. DBMS
DBMS/СУБД — программа управления базами данных. PostgreSQL — СУБД.

8.2. PostgreSQL cluster и data directory
PostgreSQL cluster — набор databases, обслуживаемых одним server instance.

Data directory хранит внутренние binary structures PostgreSQL: catalogs, relation files, transaction state, WAL-related data.

Это не «папка SQL-файлов».

8.3. Major version
16 и 18 — разные major versions. Внутренний data format может измениться. Поэтому один и тот же Docker volume нельзя автоматически считать совместимым после замены image.

Docker сохраняет bytes. Он не выполняет upgrade PostgreSQL format.

8.4. Logical dump/restore
PostgreSQL 16
   ↓ pg_dump
logical dump
   ↓ restore
PostgreSQL 18
Плюсы: понятность, переносимость. Минусы: время и дополнительное место.

8.5. pg_upgrade
pg_upgrade предназначен для major upgrade cluster без полного logical export/import.

Концептуальные modes:

copy — копирование files, безопаснее для rollback, но дороже по диску/времени;
link — hard links, быстро и экономно, но старый cluster перестаёт быть независимой rollback-копией;
clone — filesystem copy-on-write clone/reflink при поддержке.
Ресурс:
https://www.postgresql.org/docs/current/pgupgrade.html

8.6. WAL
WAL (Write-Ahead Log) — журнал предварительной записи PostgreSQL. Изменения сначала логируются, что используется для crash recovery и replication.

Главная мысль: data directory — согласованная внутренняя структура DBMS.

3.65–3.72. Безопасный upgrade
Решение 3.65–3.72
Dump/restore
Не удалять старый volume.
Поднять PostgreSQL 16.
Сделать backup.
Проверить backup.
Создать новый volume для PostgreSQL 18.
Поднять PostgreSQL 18.
Restore.
Проверить schema/data/application.
Сохранять старый volume до подтверждения успешной migration.
pg_dump -Fc -h old-db -U app app > app.dump
pg_restore -h new-db -U app -d app app.dump
pg_upgrade
Выбирать при большой БД/необходимости ускорения и наличии обеих версий binaries. До destructive actions выполнить compatibility checks и обеспечить rollback strategy.

9. Functional testing — 3.73–3.90
9.1. Unit, integration, functional, E2E
Unit test проверяет небольшую единицу кода.

Integration test проверяет взаимодействие компонентов.

Functional test проверяет функцию системы с позиции требований.

E2E test проверяет длинный путь через реальную систему целиком.

Functional и E2E пересекаются, но не являются строгими синонимами.

9.2. Test case vs automated test
Test case — спецификация проверки: ID, preconditions, steps, data, expected result.

Automated test — код, автоматически выполняющий проверку.

Test case может существовать до автоматизации.

9.3. Playwright, Selenium, Cypress
Selenium — зрелая WebDriver-экосистема, множество языков и browser drivers.

Cypress — JS/TS-oriented web testing framework с удобным developer workflow.

Playwright — современный browser automation framework с auto-waiting, multi-browser и сильным API для network/browser context.

Для тренировки ЛР удобно использовать Playwright.

9.4. DOM и locator
DOM — объектное представление HTML-документа браузером.

Locator — способ указать элемент DOM.

page.getByRole('button', { name: 'Login' })
Предпочтительнее role/name/test-id, чем хрупкий CSS path.

9.5. Assertion
Assertion — проверка ожидаемого состояния.

await expect(page).toHaveURL(/dashboard/)
9.6. Fixed sleep vs condition wait
Плохо:

await page.waitForTimeout(5000)
Если app готово за 100 ms — лишняя задержка. Если за 5.5 s — false failure.

Лучше ждать condition:

await expect(page.getByText('Welcome')).toBeVisible()
или конкретный network event.

Главное правило:

ждать состояние, а не приблизительное время.

9.7. Flaky test
Flaky test иногда проходит, иногда падает при одинаковом коде.

Причины:

race condition;
fixed sleeps;
shared mutable state;
нестабильный network;
порядок tests;
плохой readiness.
9.8. Setup/teardown
Setup — подготовка окружения. Teardown — очистка после test.

Teardown должен выполняться даже после failure.

3.73–3.83. Login tests
Решение 3.73–3.83
import { test, expect } from '@playwright/test';

test('successful login', async ({ page }) => {
  await page.goto('http://localhost:8080/login');
  await page.getByLabel('Login').fill('user');
  await page.getByLabel('Password').fill('password');
  await page.getByRole('button', { name: 'Sign in' }).click();
  await expect(page).toHaveURL(/dashboard/);
});

test('wrong password', async ({ page }) => {
  await page.goto('http://localhost:8080/login');
  await page.getByLabel('Login').fill('user');
  await page.getByLabel('Password').fill('wrong');
  await page.getByRole('button', { name: 'Sign in' }).click();
  await expect(page.getByText(/invalid/i)).toBeVisible();
});
3.80–3.81. Три ожидания
Решение 3.80–3.81
Fixed delay
await page.waitForTimeout(5000);
UI condition
await expect(page.getByText('Loaded')).toBeVisible();
Network/event
const response = page.waitForResponse(
  r => r.url().includes('/api/login') && r.status() === 200
);
await page.getByRole('button', { name: 'Sign in' }).click();
await response;
Лучший подход — ждать событие/condition, которое действительно означает готовность следующего шага.

3.84. Functional test в Gradle
Решение 3.84
tasks.register("functionalTest", Exec) {
    workingDir "frontend"
    commandLine "npx", "playwright", "test"
}

tasks.named("check") {
    dependsOn("functionalTest")
}
В реальном проекте нужно ещё обеспечить запуск и readiness приложения.

3.85–3.86. Start/readiness/teardown
Решение 3.85–3.86
Pipeline:

start test environment
       ↓
wait for readiness
       ↓
functional tests
       ↓
teardown
Gradle:

functionalTest.configure {
    dependsOn("startTestEnvironment")
    finalizedBy("stopTestEnvironment")
}
Но startTestEnvironment должен учитывать readiness, а не только факт running process/container.

3.87. 15 test cases
Решение 3.87
успешная авторизация;
неверный пароль;
несуществующий пользователь;
пустой login;
пустой password;
logout;
доступ к protected page без auth;
повторная авторизация;
expiration/reset session;
redirect после login;
refresh после login;
некорректный формат поля;
repeated submit;
backend unavailable;
после logout приватные данные не отображаются.
Для каждого описать preconditions, steps, data, expected result.

3.88. Test isolation
Ответ 3.88
Тест не должен зависеть от того, что предыдущий test оставил user/session/data. Preconditions должны создаваться явно, а teardown — очищать state.

3.89. Flaky test
Ответ 3.89
Воспроизвести многократными runs.
Найти race/shared state/fixed timeout/readiness problem.
Заменить sleeps на condition/event waits.
Изолировать test data.
Повторить много раз.
Не лечить flakiness увеличением sleep без выяснения причины.

3.90. Test case vs automated test
Ответ 3.90
Test case — спецификация проверки. Automated test — executable implementation этой проверки.