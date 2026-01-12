<?php
header('Content-Type: application/json');
require_once '../config/database.php';
require_once '../middleware/jwt.php';

// Authenticate
authenticate();

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
if (!$input || !isset($input['nama_merk'])) {
    http_response_code(400);
    echo json_encode([
        'status' => 'error',
        'message' => 'Invalid input',
        'data' => null
    ]);
    exit;
}

$nama_merk = trim($input['nama_merk']);
if (empty($nama_merk)) {
    http_response_code(400);
    echo json_encode([
        'status' => 'error',
        'message' => 'Nama merk is required',
        'data' => null
    ]);
    exit;
}

try {
    $stmt = $pdo->prepare("INSERT INTO merk (nama_merk) VALUES (?)");
    $stmt->execute([$nama_merk]);
    $merk_id = $pdo->lastInsertId();

    echo json_encode([
        'status' => 'success',
        'message' => 'Merk created successfully',
        'data' => ['merk_id' => $merk_id, 'nama_merk' => $nama_merk]
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