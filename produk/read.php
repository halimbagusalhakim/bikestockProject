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

try {
    $stmt = $pdo->query("SELECT p.produk_id, p.nama_produk, p.deskripsi, p.stok, p.harga, p.merk_id, m.nama_merk FROM produk p JOIN merk m ON p.merk_id = m.merk_id");
    $produk = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode([
        'status' => 'success',
        'message' => 'Produk retrieved successfully',
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