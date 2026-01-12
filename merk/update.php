<?php
header('Content-Type: application/json');
require_once '../config/database.php';
require_once '../middleware/jwt.php';

// Authenticate
authenticate();

// Check HTTP method
if ($_SERVER['REQUEST_METHOD'] !== 'PUT') {
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
if (!$input || !isset($input['merk_id']) || !isset($input['nama_merk'])) {
    http_response_code(400);
    echo json_encode([
        'status' => 'error',
        'message' => 'Invalid input',
        'data' => null
    ]);
    exit;
}

$merk_id = (int)$input['merk_id'];
$nama_merk = trim($input['nama_merk']);
if ($merk_id <= 0 || empty($nama_merk)) {
    http_response_code(400);
    echo json_encode([
        'status' => 'error',
        'message' => 'Valid merk_id and nama_merk are required',
        'data' => null
    ]);
    exit;
}

try {
    $stmt = $pdo->prepare("UPDATE merk SET nama_merk = ? WHERE merk_id = ?");
    $stmt->execute([$nama_merk, $merk_id]);
    if ($stmt->rowCount() == 0) {
        http_response_code(404);
        echo json_encode([
            'status' => 'error',
            'message' => 'Merk not found',
            'data' => null
        ]);
        exit;
    }

    echo json_encode([
        'status' => 'success',
        'message' => 'Merk updated successfully',
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