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
if (!$input || !isset($input['produk_id']) || !isset($input['jumlah'])) {
    http_response_code(400);
    echo json_encode([
        'status' => 'error',
        'message' => 'Invalid input',
        'data' => null
    ]);
    exit;
}

$produk_id = (int)$input['produk_id'];
$jumlah = (int)$input['jumlah'];

if ($produk_id <= 0) {
    http_response_code(400);
    echo json_encode([
        'status' => 'error',
        'message' => 'Valid produk_id is required',
        'data' => null
    ]);
    exit;
}

try {
    // Get current stok
    $stmt = $pdo->prepare("SELECT stok FROM produk WHERE produk_id = ?");
    $stmt->execute([$produk_id]);
    $produk = $stmt->fetch(PDO::FETCH_ASSOC);
    if (!$produk) {
        http_response_code(404);
        echo json_encode([
            'status' => 'error',
            'message' => 'Produk not found',
            'data' => null
        ]);
        exit;
    }

    $stok_baru = $produk['stok'] + $jumlah;
    if ($stok_baru < 0) {
        http_response_code(400);
        echo json_encode([
            'status' => 'error',
            'message' => 'Insufficient stock, cannot reduce below 0',
            'data' => null
        ]);
        exit;
    }

    // Update stok
    $stmt = $pdo->prepare("UPDATE produk SET stok = ? WHERE produk_id = ?");
    $stmt->execute([$stok_baru, $produk_id]);

    echo json_encode([
        'status' => 'success',
        'message' => 'Stock adjusted successfully',
        'data' => ['produk_id' => $produk_id, 'stok_baru' => $stok_baru]
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