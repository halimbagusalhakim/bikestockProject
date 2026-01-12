<?php
header('Content-Type: application/json');
require_once '../config/database.php';
require_once '../middleware/jwt.php';

// Authenticate first to ensure token is valid
authenticate();

// Get token from header
$headers = getallheaders();
$auth_header = $headers['Authorization'];
preg_match('/Bearer\s+(.*)$/i', $auth_header, $matches);
$token = $matches[1];

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

try {
    // Delete token from database
    $stmt = $pdo->prepare("DELETE FROM tokens WHERE token = ?");
    $stmt->execute([$token]);

    echo json_encode([
        'status' => 'success',
        'message' => 'Logout successful',
        'data' => null
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