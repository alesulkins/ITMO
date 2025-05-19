<?php
session_start();
require_once '../php/db.php';

// Проверка авторизации
if (!isset($_SESSION['user_id'])) {
    header("Location: ../pages/login.php");
    exit();
}

// Определение текущего этапа тестирования
$current_test = $_GET['test'] ?? 'bourdon';
$user_id = $_SESSION['user_id'];

// Сохранение результатов
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $test_name = $_POST['test_name'];
    $score = $_POST['score'];
    
    $stmt = $conn->prepare("INSERT INTO test_results (user_id, test_name, result) VALUES (?, ?, ?)");
    $stmt->bind_param("iss", $user_id, $test_name, $score);
    $stmt->execute();
    $stmt->close();
}

// Определение следующего теста
$tests_order = ['bourdon', 'stroop', 'wcst'];
$current_index = array_search($current_test, $tests_order);
$next_test = $tests_order[$current_index + 1] ?? null;
?>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Тест на внимание</title>
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .test-container {
            max-width: 800px;
            margin: 20px auto;
            padding: 20px;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        .burdon-cell {
            cursor: pointer;
            padding: 8px;
            border: 1px solid #ddd;
        }
        .burdon-cell.selected {
            background: #4CAF50 !important;
            color: white;
        }
        #stroop-stimulus {
            font-size: 32px;
            margin: 20px 0;
            padding: 15px;
            border: 2px solid #ccc;
        }
        .stroop-btn {
            margin: 5px;
            padding: 10px 20px;
        }
        .test-card, .ref-card {
            width: 100px;
            height: 150px;
            border: 2px solid #333;
            margin: 10px;
            cursor: pointer;
            display: inline-block;
        }
    </style>
</head>
<body>
    <div class="container mt-5">
        <?php switch($current_test): 
            case 'bourdon': ?>
            <!-- Тест Бурдона -->
            <div class="test-container">
                <h2 class="text-center mb-4">Корректурная проба</h2>
                <div class="text-center">
                    <button id="startBtn" class="btn btn-primary mb-3">Начать тест</button>
                    <div id="timer" class="h4">03:00</div>
                </div>
                
                <table class="table-bordered text-center mx-auto" id="letterGrid"></table>
                
                <div id="results" class="mt-4" hidden>
                    <h4>Результаты:</h4>
                    <p>Правильных: <span id="correctCount">0</span></p>
                    <p>Ошибок: <span id="errorCount">0</span></p>
                </div>
            </div>

            <script>
                // Генерация сетки 20x20
                const letters = ['К', 'А', 'М', 'С', 'Т', 'В', 'Р'];
                let isTestActive = false;
                let correct = 0, errors = 0;
                const targetLetter = 'К';

                function generateGrid() {
                    let grid = '<tbody>';
                    for(let i=0; i<20; i++) {
                        grid += '<tr>';
                        for(let j=0; j<20; j++) {
                            const letter = letters[Math.floor(Math.random()*letters.length)];
                            grid += `<td class="burdon-cell" data-letter="${letter}">${letter}</td>`;
                        }
                        grid += '</tr>';
                    }
                    grid += '</tbody>';
                    document.getElementById('letterGrid').innerHTML = grid;
                }

                // Таймер
                function startTimer(duration) {
                    let timer = duration, minutes, seconds;
                    const interval = setInterval(() => {
                        minutes = parseInt(timer / 60, 10);
                        seconds = parseInt(timer % 60, 10);
                        document.getElementById('timer').textContent = 
                            `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
                        
                        if (--timer < 0) {
                            clearInterval(interval);
                            endTest();
                        }
                    }, 1000);
                }

                // Обработка кликов
                document.getElementById('letterGrid').addEventListener('click', (e) => {
                    if (!isTestActive || e.target.tagName !== 'TD') return;
                    
                    const cell = e.target;
                    if (cell.dataset.letter === targetLetter) {
                        correct++;
                        cell.classList.add('selected');
                    } else {
                        errors++;
                        cell.classList.add('bg-danger');
                    }
                    cell.style.pointerEvents = 'none';
                });

                // Запуск теста
                document.getElementById('startBtn').addEventListener('click', () => {
                    isTestActive = true;
                    generateGrid();
                    startTimer(180);
                    document.getElementById('results').hidden = false;
                });

                function endTest() {
                    isTestActive = false;
                    document.getElementById('correctCount').textContent = correct;
                    document.getElementById('errorCount').textContent = errors;
                    setTimeout(() => window.location.href = 'attention_tests.php?test=stroop', 3000);
                }
            </script>
            <?php break; ?>

            <?php case 'stroop': ?>
            <!-- Тест Струпа -->
            <div class="test-container">
                <h2 class="text-center mb-4">Тест Струпа</h2>
                <div id="stroopStimulus" class="text-center h2 py-3"></div>
                <div class="text-center" id="buttons">
                    <button class="btn btn-primary stroop-btn" data-color="red">Красный</button>
                    <button class="btn btn-primary stroop-btn" data-color="blue">Синий</button>
                    <button class="btn btn-primary stroop-btn" data-color="green">Зелёный</button>
                </div>
                <div id="stroopResults" class="mt-4" hidden>
                    <p>Среднее время: <span id="avgTime">0</span> мс</p>
                </div>
            </div>

            <script>
                const colors = ['red', 'blue', 'green'];
                const colorNames = ['красный', 'синий', 'зелёный'];
                let startTime, trials = 0, totalTime = 0;

                function showStimulus() {
                    const color = colors[Math.floor(Math.random()*colors.length)];
                    const text = colorNames[Math.floor(Math.random()*colorNames.length)];
                    document.getElementById('stroopStimulus').innerHTML = 
                        `<span style="color: ${color}">${text}</span>`;
                    startTime = Date.now();
                }

                document.querySelectorAll('.stroop-btn').forEach(btn => {
                    btn.addEventListener('click', () => {
                        const color = btn.dataset.color;
                        const stimulusColor = document.querySelector('#stroopStimulus span').style.color;
                        totalTime += Date.now() - startTime;
                        trials++;
                        
                        if (trials >= 10) {
                            document.getElementById('stroopResults').hidden = false;
                            document.getElementById('avgTime').textContent = (totalTime/10).toFixed(0);
                            setTimeout(() => window.location.href = 'attention_tests.php?test=wcst', 2000);
                        } else {
                            showStimulus();
                        }
                    });
                });

                showStimulus();
            </script>
            <?php break; ?>
			<?php case 'wcst': ?>
			<!-- Тест WCST -->
			<div class="test-container">
				<h2 class="text-center mb-4">Сортировка карточек</h2>
				
				<!-- Целевая карта -->
				<div class="text-center mb-4">
					<h4>Целевая карта:</h4>
					<div id="targetCard" class="test-card mx-auto"></div>
				</div>
				
				<!-- Текущее правило -->
				<div class="text-center mb-3">
					<h4>Текущее правило:</h4>
					<div id="currentRule" class="h5 text-primary"></div>
				</div>

				<!-- Карты для выбора -->
				<div class="text-center" id="choiceCards">
					<div class="d-flex justify-content-center gap-3">
						<div class="test-card choice-card"></div>
						<div class="test-card choice-card"></div>
						<div class="test-card choice-card"></div>
						<div class="test-card choice-card"></div>
					</div>
				</div>

				<!-- Результаты -->
				<div id="wcstResults" class="mt-4 text-center" hidden>
					<h4>Результаты:</h4>
					<p>Правильных ответов: <span id="correctCount">0</span></p>
					<p>Ошибок: <span id="errorCount">0</span></p>
					<p>Смен правил: <span id="ruleChanges">0</span></p>
				</div>
			</div>

			<script>
				const rules = ['color', 'shape', 'number'];
				let currentRuleIndex = 0;
				let correctCount = 0;
				let errorCount = 0;
				let ruleChanges = 0;
				let correctInRow = 0;

				// Генератор характеристик
				const colors = ['red', 'blue', 'green'];
				const shapes = ['circle', 'triangle', 'square'];
				const numbers = [1, 2, 3];

				function generateRandomCard() {
					return {
						color: colors[Math.floor(Math.random() * colors.length)],
						shape: shapes[Math.floor(Math.random() * shapes.length)],
						number: numbers[Math.floor(Math.random() * numbers.length)]
					};
				}

				function createCardElement(card) {
					const symbols = {
						circle: '●',
						triangle: '▲',
						square: '■'
					};
					
					const div = document.createElement('div');
					div.innerHTML = `
						<div style="color: ${card.color}; font-size: 24px">
							${Array(card.number).fill(symbols[card.shape]).join('')}
						</div>
					`;
					return div;
				}

				function updateGame() {
					// Генерация целевой карты
					const targetCard = generateRandomCard();
					document.getElementById('targetCard').innerHTML = '';
					document.getElementById('targetCard').appendChild(createCardElement(targetCard));

					// Генерация карт для выбора
					const choices = Array.from({length: 4}, () => generateRandomCard());
					
					// Создание хотя бы одного правильного ответа
					const correctIndex = Math.floor(Math.random() * 4);
					choices[correctIndex] = {...targetCard};
					switch(rules[currentRuleIndex]) {
						case 'color':
							choices[correctIndex].shape = generateRandomCard().shape;
							choices[correctIndex].number = generateRandomCard().number;
							break;
						case 'shape':
							choices[correctIndex].color = generateRandomCard().color;
							choices[correctIndex].number = generateRandomCard().number;
							break;
						case 'number':
							choices[correctIndex].color = generateRandomCard().color;
							choices[correctIndex].shape = generateRandomCard().shape;
							break;
					}

					// Отображение карт
					document.querySelectorAll('.choice-card').forEach((card, index) => {
						card.innerHTML = '';
						card.appendChild(createCardElement(choices[index]));
						card.dataset.match = JSON.stringify(choices[index]);
					});

					// Обновление отображения правила
					document.getElementById('currentRule').textContent = {
						color: 'Цвет',
						shape: 'Форма',
						number: 'Количество'
					}[rules[currentRuleIndex]];
				}

				// Обработка выбора карты
				document.querySelectorAll('.choice-card').forEach(card => {
					card.addEventListener('click', function() {
						if (this.classList.contains('disabled')) return;

						const selected = JSON.parse(this.dataset.match);
						const target = JSON.parse(document.getElementById('targetCard').firstChild.dataset.match);
						
						let isCorrect = false;
						switch(rules[currentRuleIndex]) {
							case 'color':
								isCorrect = selected.color === target.color;
								break;
							case 'shape':
								isCorrect = selected.shape === target.shape;
								break;
							case 'number':
								isCorrect = selected.number === target.number;
								break;
						}

						if (isCorrect) {
							correctCount++;
							correctInRow++;
							this.classList.add('border-success');
						} else {
							errorCount++;
							correctInRow = 0;
							this.classList.add('border-danger');
						}

						// Смена правила после 5 правильных ответов
						if (correctInRow >= 5) {
							currentRuleIndex = (currentRuleIndex + 1) % rules.length;
							ruleChanges++;
							correctInRow = 0;
						}

						// Обновление интерфейса
						document.getElementById('correctCount').textContent = correctCount;
						document.getElementById('errorCount').textContent = errorCount;
						document.getElementById('ruleChanges').textContent = ruleChanges;

						setTimeout(() => {
							this.classList.remove('border-success', 'border-danger');
							updateGame();
						}, 500);
					});
				});

				// Инициализация
				document.getElementById('targetCard').firstChild.dataset.match = JSON.stringify(generateRandomCard());
				updateGame();
			</script>
			<?php break; ?>
        <?php endswitch; ?>
    </div>
</body>
</html>