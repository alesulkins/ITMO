<?php
session_start();

// Включаем отображение ошибок для отладки
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

// Подключение к базе данных
require_once '../php/db.php';

// Проверка роли пользователя
if (!isset($_SESSION['user_id'])) {
    header('Location: ../index.php');
    exit();
}

if ($_SESSION['user_role'] !== 'consultant') {
    header('Location: ../index.php');
    exit();
}

// Обработка формы назначения тестов
if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['respondent_id']) && isset($_POST['tests'])) {
    $expert_id = $_SESSION['user_id'];
    $respondent_id = $_POST['respondent_id'];
    $tests = $_POST['tests'];
    
    // Начинаем транзакцию
    $conn->begin_transaction();
    
    try {
        // Удаляем предыдущие назначения для этого пользователя
        $delete_sql = "DELETE FROM assigned_tests WHERE respondent_id = ?";
        $delete_stmt = $conn->prepare($delete_sql);
        $delete_stmt->bind_param("i", $respondent_id);
        $delete_stmt->execute();
        $delete_stmt->close();
        
        // Добавляем новые назначения с учетом порядка
        $order = 1;
        foreach ($tests as $test_name) {
            $insert_sql = "INSERT INTO assigned_tests (expert_id, respondent_id, test_name, test_order) VALUES (?, ?, ?, ?)";
            $insert_stmt = $conn->prepare($insert_sql);
            $insert_stmt->bind_param("iisi", $expert_id, $respondent_id, $test_name, $order);
            $insert_stmt->execute();
            $insert_stmt->close();
            
            // Добавляем уведомление для пользователя
            $message = "Вам назначен тест: " . htmlspecialchars($test_name);
            $notification_sql = "INSERT INTO notifications (user_id, message) VALUES (?, ?)";
            $notification_stmt = $conn->prepare($notification_sql);
            $notification_stmt->bind_param("is", $respondent_id, $message);
            $notification_stmt->execute();
            $notification_stmt->close();
            
            $order++;
        }
        
        $conn->commit();
        $_SESSION['success_message'] = "Тесты успешно назначены!";
    } catch (Exception $e) {
        $conn->rollback();
        $_SESSION['error_message'] = "Ошибка при назначении тестов: " . $e->getMessage();
    }
    
    header("Location: consultant_panel.php");
    exit();
}

// Обработка уведомлений о подключении к Zoom


// Получаем уведомления для текущего консультанта
$notifications_sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
$notifications_stmt = $conn->prepare($notifications_sql);
$notifications_stmt->bind_param("i", $_SESSION['user_id']);
$notifications_stmt->execute();
$notifications = $notifications_stmt->get_result()->fetch_all(MYSQLI_ASSOC);
$notifications_stmt->close();
?>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Панель консультанта</title>
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .chat-box {
            height: 300px;
            overflow-y: scroll;
            border: 1px solid #ccc;
            padding: 10px;
            margin-bottom: 20px;
        }
        .message {
            margin-bottom: 10px;
        }
        .message p {
            margin: 0;
        }
        .message small {
            color: #666;
        }
        .text-right {
            text-align: right;
        }
        .text-left {
            text-align: left;
        }
        .table-container {
            max-height: 400px;
            overflow-y: auto;
        }
        .test-item {
            margin-bottom: 10px;
        }
        .sortable-chosen {
            opacity: 0.8;
            background: #f8f9fa;
        }
        .notification-item.unread {
            background-color: #f0f8ff;
        }
        .notification-badge {
            position: absolute;
            top: -5px;
            right: -5px;
            font-size: 10px;
        }
    </style>
</head>
<body>
    <!-- Навигационная панель -->
    <nav class="navbar navbar-expand-lg navbar-light bg-light fixed-top">
        <a class="navbar-brand" href="../index.php">ITMO Portal</a>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ml-auto">
                <?php if (isset($_SESSION['username'])): ?>
                    <li class="nav-item">
                        <span class="navbar-text mr-2"><?php echo htmlspecialchars($_SESSION['username']); ?></span>
                        <a href="../pages/logout.php" class="btn btn-outline-dark">Выйти</a>
                    </li>
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" id="notificationsDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                            <i class="fas fa-bell"></i>
                            <?php 
                            $unread_count = 0;
                            foreach ($notifications as $notification) {
                                if (!$notification['is_read']) $unread_count++;
                            }
                            if ($unread_count > 0): ?>
                                <span class="badge badge-danger notification-badge"><?php echo $unread_count; ?></span>
                            <?php endif; ?>
                        </a>
                        <div class="dropdown-menu dropdown-menu-right" aria-labelledby="notificationsDropdown">
                            <h6 class="dropdown-header">Уведомления</h6>
                            <?php if (empty($notifications)): ?>
                                <a class="dropdown-item" href="#">Нет новых уведомлений</a>
                            <?php else: ?>
                                <?php foreach ($notifications as $notification): ?>
                                    <a class="dropdown-item notification-item <?php echo !$notification['is_read'] ? 'unread' : ''; ?>" href="#" data-id="<?php echo $notification['id']; ?>">
                                        <?php echo htmlspecialchars($notification['message']); ?>
                                        <small class="text-muted d-block"><?php echo $notification['created_at']; ?></small>
                                    </a>
                                <?php endforeach; ?>
                            <?php endif; ?>
                        </div>
                    </li>
                <?php endif; ?>
            </ul>
        </div>
    </nav>

    <!-- Основной контент -->
    <div class="container mt-5 pt-5">
        <h1 class="text-center">Панель консультанта</h1>
        
        <!-- Отображение сообщений об успехе/ошибке -->
        <?php if (isset($_SESSION['success_message'])): ?>
            <div class="alert alert-success"><?php echo $_SESSION['success_message']; unset($_SESSION['success_message']); ?></div>
        <?php endif; ?>
        
        <?php if (isset($_SESSION['error_message'])): ?>
            <div class="alert alert-danger"><?php echo $_SESSION['error_message']; unset($_SESSION['error_message']); ?></div>
        <?php endif; ?>

        <!-- Таблица с результатами -->
        <div class="table-container">
            <h3>Результаты тестов</h3>
            <table class="table table-bordered">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Пользователь</th>
                        <th>Тест</th>
                        <th>Время реакции</th>
                        <th>Правильные ответы</th>
                        <th>Дата</th>
                    </tr>
                </thead>
                <tbody>
                    <?php
                    $sql = "SELECT tr.*, u.username 
                            FROM test_results tr
                            JOIN users u ON tr.user_id = u.id
                            ORDER BY tr.created_at DESC";
                    $result = $conn->query($sql);
                    if ($result->num_rows > 0) {
                        while ($row = $result->fetch_assoc()): ?>
                            <tr>
                                <td><?php echo $row['id']; ?></td>
                                <td><?php echo htmlspecialchars($row['username']); ?></td>
                                <td><?php echo htmlspecialchars($row['test_name']); ?></td>
                                <td><?php echo $row['reaction_time']; ?></td>
                                <td><?php echo $row['correct_answers']; ?></td>
                                <td><?php echo $row['created_at']; ?></td>
                            </tr>
                        <?php endwhile;
                    } else {
                        echo "<tr><td colspan='6' class='text-center'>Нет данных о тестах.</td></tr>";
                    }
                    ?>
                </tbody>
            </table>
        </div>

        <!-- Назначить тест -->
        <div class="row mt-4">
            <div class="col-md-12">
                <h3>Назначить тесты</h3>
                <form action="consultant_panel.php" method="POST" id="assignTestsForm">
                    <div class="form-group">
                        <label for="respondent_id">Выберите респондента:</label>
                        <select name="respondent_id" id="respondent_id" class="form-control" required>
                            <?php
                            $sql = "SELECT id, username FROM users WHERE role = 'user'";
                            $result = $conn->query($sql);
                            while ($row = $result->fetch_assoc()) {
                                echo "<option value='{$row['id']}'>{$row['username']}</option>";
                            }
                            ?>
                        </select>
                    </div>
            
                    <div class="form-group">
                        <label>Выберите тесты и порядок:</label>
                        <div id="testsContainer">
                            <div class="test-item mb-2 d-flex align-items-center">
                                <select name="tests[]" class="form-control mr-2" required>
                                    <option value="simple_reaction">Простая сенсомоторная реакция</option>
                                    <option value="complex_reaction">Сложная сенсомоторная реакция</option>
                                    <option value="circle_reaction_test">Тест на круги</option>
                                    <option value="addition_parity_test">Тест на сложение и четность</option>
                                    <option value="visual_memory_test">Тест на зрительную память</option>
                                </select>
                                <button type="button" class="btn btn-sm btn-danger remove-test">×</button>
                            </div>
                        </div>
                        <button type="button" id="addTest" class="btn btn-sm btn-secondary mt-2">Добавить тест</button>
                    </div>
            
                    <button type="submit" class="btn btn-primary">Назначить тесты</button>
                </form>
            </div>
        </div>

        <!-- Чат -->
        <div class="row mt-4">
            <div class="col-md-4">
                <h3>Пользователи</h3>
                <ul class="list-group">
                    <?php
                    $sql = "SELECT id, username FROM users WHERE role = 'user'";
                    $result = $conn->query($sql);
                    while ($user = $result->fetch_assoc()): ?>
                        <li class="list-group-item">
                            <a href="?user_id=<?php echo $user['id']; ?>"><?php echo htmlspecialchars($user['username']); ?></a>
                        </li>
                    <?php endwhile; ?>
                </ul>
            </div>

            <div class="col-md-8">
                <?php if (isset($_GET['user_id'])): ?>
                    <h3>Чат с пользователем</h3>
                    <div class="chat-box">
                        <?php
                        $selected_user_id = $_GET['user_id'];
                        $sql = "SELECT m.*, u1.username as from_username, u2.username as to_username 
                                FROM messagescon m
                                JOIN users u1 ON m.from_user_id = u1.id
                                JOIN users u2 ON m.to_user_id = u2.id
                                WHERE (from_user_id = ? AND to_user_id = ?) 
                                OR (from_user_id = ? AND to_user_id = ?) 
                                ORDER BY created_at";
                        $stmt = $conn->prepare($sql);
                        if (!$stmt) {
                            die("Ошибка подготовки запроса: " . $conn->error);
                        }
                        $stmt->bind_param("iiii", $_SESSION['user_id'], $selected_user_id, $selected_user_id, $_SESSION['user_id']);
                        $stmt->execute();
                        $messages = $stmt->get_result()->fetch_all(MYSQLI_ASSOC);
                        $stmt->close();

                        foreach ($messages as $message): ?>
                            <div class="message <?php echo $message['from_user_id'] == $_SESSION['user_id'] ? 'text-right' : 'text-left'; ?>">
                                <p><strong><?php echo $message['from_user_id'] == $_SESSION['user_id'] ? 'Вы' : htmlspecialchars($message['from_username']); ?>:</strong></p>
                                <p><?php echo htmlspecialchars($message['message']); ?></p>
                                <small><?php echo $message['created_at']; ?></small>
                            </div>
                        <?php endforeach; ?>
                    </div>

                    <!-- Форма отправки сообщения -->
                    <form action="send_message.php" method="POST" class="mt-3">
                        <input type="hidden" name="to_user_id" value="<?php echo $selected_user_id; ?>">
                        <div class="form-group">
                            <textarea name="message" class="form-control" placeholder="Введите сообщение" required></textarea>
                        </div>
                        <button type="submit" class="btn btn-primary">Отправить</button>
                    </form>
                <?php else: ?>
                    <p>Выберите пользователя, чтобы начать чат.</p>
                <?php endif; ?>
            </div>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.3/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/sortablejs@1.14.0/Sortable.min.js"></script>
    <script src="https://kit.fontawesome.com/a076d05399.js" crossorigin="anonymous"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const container = document.getElementById('testsContainer');
            const addButton = document.getElementById('addTest');
            
            // Инициализация Sortable для перетаскивания
            new Sortable(container, {
                animation: 150,
                handle: '.form-control',
                chosenClass: 'sortable-chosen'
            });
            
            // Добавление нового теста
            addButton.addEventListener('click', function() {
                const newItem = document.createElement('div');
                newItem.className = 'test-item mb-2 d-flex align-items-center';
                newItem.innerHTML = `
                    <select name="tests[]" class="form-control mr-2" required>
                        <option value="simple_reaction">Простая сенсомоторная реакция</option>
                        <option value="complex_reaction">Сложная сенсомоторная реакция</option>
                        <option value="circle_reaction_test">Тест на круги</option>
                        <option value="addition_parity_test">Тест на сложение и четность</option>
                        <option value="visual_memory_test">Тест на зрительную память</option>
                    </select>
                    <button type="button" class="btn btn-sm btn-danger remove-test">×</button>
                `;
                container.appendChild(newItem);
            });
            
            // Удаление теста
            container.addEventListener('click', function(e) {
                if (e.target.classList.contains('remove-test')) {
                    if (container.querySelectorAll('.test-item').length > 1) {
                        e.target.closest('.test-item').remove();
                    } else {
                        alert('Должен остаться хотя бы один тест');
                    }
                }
            });

            // Пометить уведомление как прочитанное
            $('.notification-item').click(function() {
                const notificationId = $(this).data('id');
                const item = $(this);
                
                if (item.hasClass('unread')) {
                    $.post('mark_notification_read.php', {id: notificationId}, function() {
                        item.removeClass('unread');
                        updateNotificationBadge();
                    });
                }
            });

            // Обновление счетчика непрочитанных уведомлений
            function updateNotificationBadge() {
                $.get('get_unread_notifications.php', function(count) {
                    const badge = $('.notification-badge');
                    if (count > 0) {
                        badge.text(count).show();
                    } else {
                        badge.hide();
                    }
                });
            }

            // Проверка новых уведомлений каждые 30 секунд
            setInterval(updateNotificationBadge, 30000);
        });
    </script>
</body>
</html>