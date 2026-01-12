<?php
header('Content-Type: application/json');
require_once '../config/database.php';
require_once '../middleware/jwt.php';

// Authenticate
authenticate();

// Check HTTP method
if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    http_response_code(405);
    echo json_encode([
        'status' => 'error',
        'message' => 'Method not allowed',
        'data' => null
    ]);
    exit;
}

// Get id from query param
$id = isset($_GET['id']) ? (int)$_GET['id'] : 0;
if ($id <= 0) {
    http_response_code(400);
    echo json_encode([
        'status' => 'error',
        'message' => 'Valid id is required',
        'data' => null
    ]);
    exit;
}

try {
    $stmt = $pdo->prepare("SELECT p.penjualan_id, p.produk_id, pr.nama_produk, p.user_id, u.username, p.nama_pembeli, p.jumlah, p.total_harga, p.tanggal FROM penjualan p JOIN produk pr ON p.produk_id = pr.produk_id JOIN users u ON p.user_id = u.user_id WHERE p.penjualan_id = ?");
    $stmt->execute([$id]);
    $penjualan = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$penjualan) {
        http_response_code(404);
        echo json_encode([
            'status' => 'error',
            'message' => 'Penjualan not found',
            'data' => null
        ]);
        exit;
    }

    echo json_encode([
        'status' => 'success',
        'message' => 'Penjualan detail retrieved successfully',
        'data' => $penjualan
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