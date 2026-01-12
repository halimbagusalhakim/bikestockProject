<?php
header('Content-Type: application/json');
require_once '../config/database.php';
require_once '../middleware/jwt.php';

// Check HTTP method
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode([
        'status' => 'error',
        'message' => 'Method not allowed',
        'data' => null
    ]);
    exit;
}

// Get input
$input = json_decode(file_get_contents('php://input'), true);
if (!$input || !isset($input['username']) || !isset($input['password'])) {
    http_response_code(400);
    echo json_encode([
        'status' => 'error',
        'message' => 'Invalid input',
        'data' => null
    ]);
    exit;
}

$username = trim($input['username']);
$password = $input['password'];

if (empty($username) || empty($password)) {
    http_response_code(400);
    echo json_encode([
        'status' => 'error',
        'message' => 'Username and password are required',
        'data' => null
    ]);
    exit;
}

try {
    // Check user
    $stmt = $pdo->prepare("SELECT user_id, password, role FROM users WHERE username = ?");
    $stmt->execute([$username]);
    $user = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$user || !password_verify($password, $user['password'])) {
        http_response_code(401);
        echo json_encode([
            'status' => 'error',
            'message' => 'Invalid credentials',
            'data' => null
        ]);
        exit;
    }

    // Generate token
    $payload = [
        'user_id' => $user['user_id'],
        'role' => $user['role'],
        'exp' => time() + 3600 // 1 hour
    ];
    $token = JWT::encode($payload);

    // Store token in database
    $expires_at = date('Y-m-d H:i:s', $payload['exp']);
    $stmt = $pdo->prepare("INSERT INTO tokens (user_id, token, expires_at) VALUES (?, ?, ?)");
    $stmt->execute([$user['user_id'], $token, $expires_at]);

    echo json_encode([
        'status' => 'success',
        'message' => 'Login successful',
        'data' => ['token' => $token]
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Internal server error',
        'data' => null
    ]);
}
?>