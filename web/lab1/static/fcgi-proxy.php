<?php
ini_set('display_errors', '1');
error_reporting(E_ALL);

header('Content-Type: application/json; charset=UTF-8');

$allowedX = ['-2','-1.5','-1','-0.5','0','0.5','1','1.5','2'];
$allowedR = ['1','2','3','4','5'];

$x = $_GET['x'] ?? null;
$y = $_GET['y'] ?? null;
$r = $_GET['r'] ?? null;

if ($x === null || !in_array($x, $allowedX, true)) {
    http_response_code(400);
    echo json_encode(['now' => date('c'), 'reason' => 'X - некорректное значение']);
    exit;
}
if ($y === null || !is_numeric($y) || (float)$y <= -3 || (float)$y >= 3) {
    http_response_code(400);
    echo json_encode(['now' => date('c'), 'reason' => 'Y - некорректное значение']);
    exit;
}
if ($r === null || !in_array($r, $allowedR, true)) {
    http_response_code(400);
    echo json_encode(['now' => date('c'), 'reason' => 'R - некорректное значение']);
    exit;
}

$target = 'http://helios.cs.ifmo.ru:24135/fcgi-bin/lab1.jar?' . http_build_query([
    'x' => $x,
    'y' => $y,
    'r' => $r,
]);

$ctx = stream_context_create([
    'http' => [
        'method' => 'GET',
        'timeout' => 5,
        'ignore_errors' => true,
        'header' => "Accept: application/json\r\n",
    ],
]);

$body = @file_get_contents($target, false, $ctx);
$code = 0;
if (isset($http_response_header) && is_array($http_response_header)) {
    foreach ($http_response_header as $h) {
        if (preg_match('#^HTTP/\S+\s+(\d{3})#', $h, $m)) {
            $code = (int)$m[1];
            break;
        }
    }
}

if ($body === false) {
    http_response_code(502);
    echo json_encode(['now' => date('c'), 'reason' => 'Proxy error: upstream unreachable']);
    exit;
}

http_response_code($code > 0 ? $code : 200);
echo $body;