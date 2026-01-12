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
if (!$input || !isset($input['nama_produk']) || !isset($input['stok']) || !isset($input['harga']) || !isset($input['merk_id'])) {
    http_response_code(400);
    echo json_encode([
        'status' => 'error',
        'message' => 'Invalid input',
        'data' => null
    ]);
    exit;
}

$nama_produk = trim($input['nama_produk']);
$deskripsi = isset($input['deskripsi']) ? trim($input['deskripsi']) : null;
$stok = (int)$input['stok'];
$harga = (float)$input['harga'];
$merk_id = (int)$input['merk_id'];

if (empty($nama_produk) || $stok < 0 || $harga <= 0 || $merk_id <= 0) {
    http_response_code(400);
    echo json_encode([
        'status' => 'error',
        'message' => 'Valid nama_produk, stok (>=0), harga (>0), and merk_id are required',
        'data' => null
    ]);
    exit;
}

try {
    // Check if merk exists
    $stmt = $pdo->prepare("SELECT merk_id FROM merk WHERE merk_id = ?");
    $stmt->execute([$merk_id]);
    if (!$stmt->fetch()) {
        http_response_code(400);
        echo json_encode([
            'status' => 'error',
            'message' => 'Merk not found',
            'data' => null
        ]);
        exit;
    }

    $stmt = $pdo->prepare("INSERT INTO produk (nama_produk, deskripsi, stok, harga, merk_id) VALUES (?, ?, ?, ?, ?)");
    $stmt->execute([$nama_produk, $deskripsi, $stok, $harga, $merk_id]);
    $produk_id = $pdo->lastInsertId();

    echo json_encode([
        'status' => 'success',
        'message' => 'Produk created successfully',
        'data' => ['produk_id' => $produk_id, 'nama_produk' => $nama_produk, 'deskripsi' => $deskripsi, 'stok' => $stok, 'harga' => $harga, 'merk_id' => $merk_id]
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