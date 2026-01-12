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
    $stmt = $pdo->prepare("SELECT p.produk_id, p.nama_produk, p.deskripsi, p.stok, p.harga, p.merk_id, m.nama_merk FROM produk p JOIN merk m ON p.merk_id = m.merk_id WHERE p.produk_id = ?");
    $stmt->execute([$id]);
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

    echo json_encode([
        'status' => 'success',
        'message' => 'Produk detail retrieved successfully',
        'data' => $produk
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