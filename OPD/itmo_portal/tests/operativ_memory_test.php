<?php 
session_start();
if (!isset($_SESSION['user_id'])) {
    header("Location: ../pages/login.php");
    exit();
}
?>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Тест на оперативную память</title>
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .stimulus {
            width: 200px;
            height: 200px;
            background-color: #ccc;
            margin: 20px auto;
            border-radius: 10px;
            transition: all 0.3s ease;
            cursor: pointer;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 24px;
            font-weight: bold;
            color: white;
        }
        .options-container {
            display: flex;
            justify-content: center;
            gap: 15px;
            margin-top: 20px;
            flex-wrap: wrap;
        }
        .option {
            width: 60px;
            height: 60px;
            border-radius: 10px;
            cursor: pointer;
            border: 3px solid transparent;
            transition: transform 0.2s, border-color 0.2s;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: bold;
            background-color: #f8f9fa;
        }
        .option:hover {
            transform: scale(1.1);
        }
        .option.selected {
            border-color: #000;
        }
        .navbar {
            margin-bottom: 20px;
        }
        .container {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            min-height: 100vh;
            margin-top: -50px;
        }
        .btn-next {
            margin-top: 20px;
            display: none;
        }
        .instructions {
            max-width: 600px;
            margin: 0 auto 20px;
            text-align: center;
        }
        .sequence-info {
            margin-top: 15px;
            font-weight: bold;
        }
        .progress-container {
            width: 100%;
            max-width: 400px;
            margin: 20px auto;
        }
        .number-display {
            font-size: 48px;
            font-weight: bold;
            margin: 20px 0;
        }
        .hidden {
            display: none;
        }
        .difficulty-buttons {
            margin: 20px 0;
            display: flex;
            gap: 10px;
            justify-content: center;
        }
        .final-results {
            background-color: #f8f9fa;
            padding: 20px;
            border-radius: 10px;
            margin-top: 20px;
            max-width: 500px;
        }
        .zoom-modal {
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background-color: rgba(0,0,0,0.8);
            display: flex;
            justify-content: center;
            align-items: center;
            z-index: 1000;
        }
        .zoom-modal-content {
            background-color: white;
            padding: 30px;
            border-radius: 10px;
            max-width: 600px;
            text-align: center;
        }
        .zoom-info {
            background-color: #f0f7ff;
            padding: 15px;
            border-radius: 8px;
            margin: 20px 0;
            border-left: 5px solid #0066cc;
        }
        .zoom-code {
            font-size: 24px;
            font-weight: bold;
            color: #0066cc;
            margin: 10px 0;
        }
    </style>
</head>
<body>
    <!-- Навигационная панель -->
    <nav class="navbar navbar-expand-lg navbar-light bg-light fixed-top">
        <a class="navbar-brand" href="../index.php">ITMO Portal</a>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ml-auto">
                <li class="nav-item">
                    <span class="navbar-text mr-2"><?php echo htmlspecialchars($_SESSION['username']); ?></span>
                    <a href="../pages/logout.php" class="btn btn-outline-dark">Выйти</a>
                </li>
            </ul>
        </div>
    </nav>

    <!-- Модальное окно Zoom -->
    <div id="zoom-modal" class="zoom-modal">
        <div class="zoom-modal-content">
            <h2>Требование к прохождению теста</h2>
            <p>Для обеспечения честности тестирования вам необходимо подключиться к Zoom конференции.</p>
            <p>Пожалуйста, подключитесь к конференции перед началом теста. Ваше изображение и экран будут записываться.</p>
            
            <div class="zoom-info">
                <h4>Данные для подключения:</h4>
                <div class="zoom-code">
                    Код конференции: 123 456 7890
                </div>
                <div class="zoom-code">
                    Пароль: 987654
                </div>
                <p>Или перейдите по ссылке: <a href="https://zoom.us/j/1234567890?pwd=QWERTY123456" target="_blank">https://zoom.us/j/1234567890</a></p>
            </div>
            
            <p>После подключения нажмите кнопку "Я подключился", чтобы начать тест.</p>
            
            <div class="form-check mb-3">
                <input type="checkbox" class="form-check-input" id="agree-checkbox">
                <label class="form-check-label" for="agree-checkbox">Я подтверждаю, что подключился к Zoom конференции и готов начать тест</label>
            </div>
            
            <button id="start-test-btn" class="btn btn-primary" disabled>Я подключился</button>
        </div>
    </div>

    <!-- Основной контент -->
    <div class="container hidden" id="main-content">
        <h1 class="text-center">Тест на оперативную память</h1>
        <div class="instructions">
            <p>Вам будет показана последовательность чисел. Запомните их, а затем решите математические задачи, используя эти числа. В конце вам нужно будет вспомнить исходные числа в правильном порядке.</p>
        </div>
        
        <!-- Выбор уровня сложности -->
        <div id="difficulty-selection">
            <h3 class="text-center">Выберите уровень сложности:</h3>
            <div class="difficulty-buttons">
                <button class="btn btn-success difficulty-btn" data-level="easy">Лёгкий</button>
                <button class="btn btn-warning difficulty-btn" data-level="medium">Средний</button>
                <button class="btn btn-danger difficulty-btn" data-level="hard">Сложный</button>
            </div>
        </div>
        
        <div id="test-content" class="hidden">
            <div class="text-center">
                <button id="start-button" class="btn btn-primary">Начать тест</button>
                
                <!-- Этап запоминания -->
                <div id="memorization-stage" class="hidden">
                    <div id="number-display" class="number-display"></div>
                    <p>Запомните эти числа</p>
                </div>
                
                <!-- Этап математических задач -->
                <div id="math-stage" class="hidden">
                    <h3>Решите следующие задачи:</h3>
                    <div id="math-problems"></div>
                    <button id="submit-math" class="btn btn-secondary mt-3">Продолжить</button>
                </div>
                
                <!-- Этап воспроизведения -->
                <div id="recall-stage" class="hidden">
                    <p>Введите числа, которые вы запомнили, в исходном порядке:</p>
                    <div id="number-inputs" class="options-container"></div>
                    <button id="submit-recall" class="btn btn-primary mt-3">Проверить</button>
                </div>
                
                <div class="progress-container">
                    <div class="progress">
                        <div id="progress-bar" class="progress-bar" role="progressbar" style="width: 0%"></div>
                    </div>
                </div>
                
                <p id="sequence-info" class="sequence-info"></p>
                <p id="result" class="mt-3"></p>
                <div id="final-results" class="final-results hidden"></div>
                <button id="restart-button" class="btn btn-primary hidden">Пройти ещё раз</button>
                <a href="next_test.html" id="next-test-button" class="btn btn-success btn-next hidden">Следующий тест</a>
            </div>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.3/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>

    <script>
        // Ожидаем полной загрузки DOM
        document.addEventListener('DOMContentLoaded', function() {
            // Проверяем существование элементов перед добавлением обработчиков
            const agreeCheckbox = document.getElementById('agree-checkbox');
            const startTestBtn = document.getElementById('start-test-btn');
            
            if (agreeCheckbox && startTestBtn) {
                agreeCheckbox.addEventListener('change', function() {
                    startTestBtn.disabled = !this.checked;
                });
                
                startTestBtn.addEventListener('click', function() {
                    // Отправляем уведомление консультанту о подключении к Zoom
                    sendZoomConnectedNotification();
                    
                    // Показываем тест
                    document.getElementById('zoom-modal').style.display = 'none';
                    document.getElementById('main-content').classList.remove('hidden');
                });
            } else {
                console.error('Не удалось найти элементы для обработки Zoom модального окна');
            }

            // Пометить уведомление как прочитанное при загрузке теста
            const urlParams = new URLSearchParams(window.location.search);
            const notificationId = urlParams.get('notification_id');

            if (notificationId) {
                fetch(`index.php?mark_as_read=${notificationId}`)
                    .then(response => {
                        if (!response.ok) {
                            console.error('Ошибка при пометке уведомления как прочитанного');
                        }
                    })
                    .catch(error => {
                        console.error('Ошибка:', error);
                    });
            }

            // Конфигурация уровней сложности
            const difficultySettings = {
                easy: {
                    name: "Лёгкий",
                    startLength: 3,
                    maxLevel: 5,
                    displayTime: 3000,
                    mathTime: 15000,
                    numbersRange: { min: 1, max: 9 }
                },
                medium: {
                    name: "Средний",
                    startLength: 4,
                    maxLevel: 6,
                    displayTime: 2500,
                    mathTime: 12000,
                    numbersRange: { min: 1, max: 12 }
                },
                hard: {
                    name: "Сложный",
                    startLength: 5,
                    maxLevel: 7,
                    displayTime: 2000,
                    mathTime: 10000,
                    numbersRange: { min: 1, max: 15 }
                }
            };

            let currentDifficulty = null;
            let numbers = [];
            let userRecall = [];
            let sequenceLength = 0;
            let level = 1;
            let maxLevel = 0;
            let correctAnswers = 0;
            let totalScore = 0;
            let testStartTime;
            let displayTime = 0;
            let mathTime = 0;
            let numbersRange = { min: 1, max: 9 };
            let mathAnswers = [];

            // Инициализация теста при выборе уровня сложности
            document.querySelectorAll('.difficulty-btn').forEach(btn => {
                btn.addEventListener('click', function() {
                    currentDifficulty = this.getAttribute('data-level');
                    const settings = difficultySettings[currentDifficulty];
                    
                    // Устанавливаем параметры теста
                    sequenceLength = settings.startLength;
                    maxLevel = settings.maxLevel;
                    displayTime = settings.displayTime;
                    mathTime = settings.mathTime;
                    numbersRange = settings.numbersRange;
                    
                    // Показываем тестовый интерфейс
                    document.getElementById('difficulty-selection').classList.add('hidden');
                    document.getElementById('test-content').classList.remove('hidden');
                });
            });

            document.getElementById('start-button').addEventListener('click', startTest);
            document.getElementById('submit-math').addEventListener('click', proceedToRecall);
            document.getElementById('submit-recall').addEventListener('click', checkRecall);
            document.getElementById('restart-button').addEventListener('click', restartTest);

            function startTest() {
                console.log("Тест начат на уровне сложности: " + currentDifficulty);
                const result = document.getElementById('result');
                const nextButton = document.getElementById('next-test-button');
                const finalResults = document.getElementById('final-results');
                
                // Сброс предыдущих результатов
                result.textContent = '';
                finalResults.classList.add('hidden');
                document.getElementById('start-button').disabled = true;
                nextButton.classList.add('hidden');
                document.getElementById('restart-button').classList.add('hidden');
                
                // Скрыть все этапы
                document.getElementById('memorization-stage').classList.add('hidden');
                document.getElementById('math-stage').classList.add('hidden');
                document.getElementById('recall-stage').classList.add('hidden');
                
                testStartTime = Date.now();
                
                // Генерируем случайную последовательность чисел
                generateNumbers();
                
                // Показываем числа для запоминания
                showNumbers();
            }

            function generateNumbers() {
                numbers = [];
                for (let i = 0; i < sequenceLength; i++) {
                    // Генерируем числа в заданном диапазоне
                    numbers.push(Math.floor(Math.random() * (numbersRange.max - numbersRange.min + 1))) + numbersRange.min;
                }
                document.getElementById('sequence-info').textContent = 
                    `Уровень сложности: ${difficultySettings[currentDifficulty].name}. Уровень ${level} из ${maxLevel}. 
                    Запомните ${sequenceLength} чисел.`;
            }

            function showNumbers() {
                const numberDisplay = document.getElementById('number-display');
                const memorizationStage = document.getElementById('memorization-stage');
                
                memorizationStage.classList.remove('hidden');
                numberDisplay.textContent = numbers.join('   ');
                
                // Через заданное время переходим к математическим задачам
                setTimeout(() => {
                    memorizationStage.classList.add('hidden');
                    showMathProblems();
                }, displayTime);
            }

            function showMathProblems() {
                const mathStage = document.getElementById('math-stage');
                const mathProblemsDiv = document.getElementById('math-problems');
                
                mathStage.classList.remove('hidden');
                mathProblemsDiv.innerHTML = '';
                
                // Генерируем математические задачи с использованием запомненных чисел
                mathAnswers = [];
                numbers.forEach((num, index) => {
                    // Выбираем случайную операцию (сложение, вычитание или умножение)
                    const operations = ['+', '-', '*'];
                    const operation = operations[Math.floor(Math.random() * operations.length)];
                    
                    let operand, answer;
                    
                    // В зависимости от уровня сложности выбираем операнд
                    if (currentDifficulty === 'easy') {
                        if (operation === '+') {
                            operand = Math.floor(Math.random() * 3) + 1; // 1-3
                            answer = num + operand;
                        } else if (operation === '-') {
                            operand = Math.floor(Math.random() * (num - 1)) + 1; // 1-(num-1)
                            answer = num - operand;
                        } else {
                            operand = Math.floor(Math.random() * 3) + 1; // 1-3
                            answer = num * operand;
                        }
                    } else if (currentDifficulty === 'medium') {
                        if (operation === '+') {
                            operand = Math.floor(Math.random() * 5) + 1; // 1-5
                            answer = num + operand;
                        } else if (operation === '-') {
                            operand = Math.floor(Math.random() * (num - 1)) + 1; // 1-(num-1)
                            answer = num - operand;
                        } else {
                            operand = Math.floor(Math.random() * 4) + 1; // 1-4
                            answer = num * operand;
                        }
                    } else {
                        if (operation === '+') {
                            operand = Math.floor(Math.random() * 7) + 1; // 1-7
                            answer = num + operand;
                        } else if (operation === '-') {
                            operand = Math.floor(Math.random() * (num - 1)) + 1; // 1-(num-1)
                            answer = num - operand;
                        } else {
                            operand = Math.floor(Math.random() * 5) + 1; // 1-5
                            answer = num * operand;
                        }
                    }
                    
                    mathAnswers.push(answer);
                    
                    const problemDiv = document.createElement('div');
                    problemDiv.className = 'mb-2';
                    problemDiv.innerHTML = `
                        <span>Задача ${index + 1}: Если к запомненному числу ${operation} ${operand}, получится: </span>
                        <input type="number" id="math-answer-${index}" class="form-control d-inline-block" style="width: 80px;">
                    `;
                    mathProblemsDiv.appendChild(problemDiv);
                });
                
                // Автоматический переход через заданное время, если пользователь не нажал кнопку
                setTimeout(() => {
                    if (!document.getElementById('recall-stage').classList.contains('hidden')) return;
                    proceedToRecall();
                }, mathTime);
            }

            function proceedToRecall() {
                // Проверяем математические ответы (но не учитываем в оценке оперативной памяти)
                const mathStage = document.getElementById('math-stage');
                const recallStage = document.getElementById('recall-stage');
                const numberInputs = document.getElementById('number-inputs');
                
                mathStage.classList.add('hidden');
                recallStage.classList.remove('hidden');
                numberInputs.innerHTML = '';
                
                // Создаем поля для ввода чисел
                for (let i = 0; i < numbers.length; i++) {
                    const inputDiv = document.createElement('div');
                    inputDiv.className = 'option';
                    inputDiv.innerHTML = `
                        <input type="number" id="recall-${i}" class="form-control" 
                               min="${numbersRange.min}" max="${numbersRange.max}" 
                               style="width: 60px; height: 60px; text-align: center;">
                    `;
                    numberInputs.appendChild(inputDiv);
                }
            }

            function checkRecall() {
                const result = document.getElementById('result');
                const recallStage = document.getElementById('recall-stage');
                const finalResults = document.getElementById('final-results');
                
                userRecall = [];
                let recallCorrect = true;
                
                // Собираем введенные пользователем числа
                for (let i = 0; i < numbers.length; i++) {
                    const input = document.getElementById(`recall-${i}`);
                    const value = parseInt(input.value);
                    
                    if (isNaN(value) || value < numbersRange.min || value > numbersRange.max) {
                        result.textContent = `Пожалуйста, введите числа от ${numbersRange.min} до ${numbersRange.max}`;
                        return;
                    }
                    
                    userRecall.push(value);
                    
                    if (value !== numbers[i]) {
                        recallCorrect = false;
                    }
                }
                
                if (recallCorrect) {
                    correctAnswers++;
                    const levelScore = sequenceLength * 10; // Баллы за уровень
                    totalScore += levelScore;
                    
                    if (level < maxLevel) {
                        // Переход на следующий уровень
                        level++;
                        sequenceLength++;
                        result.textContent = `Правильно! Вы набрали ${levelScore} баллов. Переход на уровень ${level}.`;
                        recallStage.classList.add('hidden');
                        testStartTime = Date.now();
                        setTimeout(startTest, 1500);
                    } else {
                        // Тест завершен успешно
                        const averageTime = ((Date.now() - testStartTime) / 1000) / maxLevel;
                        result.textContent = `Тест завершён! Вы правильно воспроизвели все последовательности.`;
                        
                        // Показываем финальные результаты
                        finalResults.innerHTML = `
                            <h3>Результаты теста</h3>
                            <p><strong>Уровень сложности:</strong> ${difficultySettings[currentDifficulty].name}</p>
                            <p><strong>Пройдено уровней:</strong> ${maxLevel}</p>
                            <p><strong>Правильных ответов:</strong> ${correctAnswers} из ${maxLevel}</p>
                            <p><strong>Итоговый счет:</strong> ${totalScore} баллов</p>
                            <p><strong>Среднее время ответа:</strong> ${averageTime.toFixed(2)} сек.</p>
                        `;
                        finalResults.classList.remove('hidden');
                        
                        document.getElementById('start-button').disabled = false;
                        document.getElementById('next-test-button').classList.remove('hidden');
                        document.getElementById('restart-button').classList.remove('hidden');
                        
                        // Данные для отправки
                        const testData = {
                            user_id: <?php echo $_SESSION['user_id']; ?>,
                            test_name: 'Тест на оперативную память',
                            difficulty_level: currentDifficulty,
                            score: totalScore,
                            correct_answers: correctAnswers,
                            total_questions: maxLevel,
                            sequence_length: sequenceLength - 1,
                            average_time: averageTime
                        };

                        saveResult(testData);
                    }
                } else {
                    // Неправильная последовательность
                    const levelsCompleted = level - 1;
                    const averageTime = levelsCompleted > 0 ? (totalTime / levelsCompleted) : 0;
                    
                    result.textContent = `Неверно! Правильная последовательность: ${numbers.join(', ')}. Ваш ответ: ${userRecall.join(', ')}`;
                    
                    // Показываем финальные результаты
                    finalResults.innerHTML = `
                        <h3>Результаты теста</h3>
                        <p><strong>Уровень сложности:</strong> ${difficultySettings[currentDifficulty].name}</p>
                        <p><strong>Пройдено уровней:</strong> ${levelsCompleted} из ${maxLevel}</p>
                        <p><strong>Правильных ответов:</strong> ${correctAnswers} из ${levelsCompleted > 0 ? levelsCompleted : 1}</p>
                        <p><strong>Итоговый счет:</strong> ${totalScore} баллов</p>
                        <p><strong>Среднее время ответа:</strong> ${levelsCompleted > 0 ? averageTime.toFixed(2) : 0} сек.</p>
                    `;
                    finalResults.classList.remove('hidden');
                    
                    recallStage.classList.add('hidden');
                    document.getElementById('start-button').disabled = false;
                    document.getElementById('restart-button').classList.remove('hidden');
                    
                    // Данные для отправки (в случае ошибки)
                    const testData = {
                        user_id: <?php echo $_SESSION['user_id']; ?>,
                        test_name: 'Тест на оперативную память',
                        difficulty_level: currentDifficulty,
                        score: totalScore,
                        correct_answers: correctAnswers,
                        total_questions: maxLevel,
                        sequence_length: sequenceLength,
                        average_time: averageTime
                    };

                    saveResult(testData);
                }
            }

            function restartTest() {
                document.getElementById('final-results').classList.add('hidden');
                document.getElementById('restart-button').classList.add('hidden');
                document.getElementById('next-test-button').classList.add('hidden');
                document.getElementById('result').textContent = '';
                document.getElementById('difficulty-selection').classList.remove('hidden');
                document.getElementById('test-content').classList.add('hidden');
            }

            function saveResult(testData) {
                fetch('../php/save_test_result.php', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(testData)
                })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Ошибка сети: ' + response.statusText);
                    }
                    return response.json();
                })
                .then(data => {
                    if (data.status === 'success') {
                        console.log('Результат успешно сохранен в базе данных');
                    } else {
                        const errorMessage = data.message || 'Неизвестная ошибка';
                        console.error('Ошибка при сохранении результата:', errorMessage);
                    }
                })
                .catch(error => {
                    console.error('Ошибка при отправке данных:', error);
                });
            }

            function sendZoomConnectedNotification() {
                console.log("Sending Zoom notification...");
                const testName = "Тест на оперативную память";
                const respondentId = <?php echo $_SESSION['user_id']; ?>;
                
                fetch('../php/send_zoom_notification.php', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({
                        respondent_id: respondentId,
                        test_name: testName
                    })
                })
                .then(response => {
                    console.log("Response status:", response.status);
                    return response.json();
                })
                .then(data => {
                    console.log("Response data:", data);
                    if (data.status !== 'success') {
                        console.error('Ошибка при отправке уведомления');
                    }
                })
                .catch(error => {
                    console.error('Ошибка:', error);
                });
            }
        });
    </script>
</body>
</html>