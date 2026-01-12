<?php
header('Content-Type: application/json');
require_once '../config/database.php';
require_once '../middleware/jwt.php';

// Authenticate
$payload = authenticate();
$user_id = $payload['user_id'];

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
$nama_pembeli = isset($input['nama_pembeli']) ? trim($input['nama_pembeli']) : null;
$jumlah = (int)$input['jumlah'];

if ($produk_id <= 0 || $jumlah <= 0) {
    http_response_code(400);
    echo json_encode([
        'status' => 'error',
        'message' => 'Valid produk_id and jumlah (>0) are required',
        'data' => null
    ]);
    exit;
}

try {
    // Get produk details
    $stmt = $pdo->prepare("SELECT stok, harga FROM produk WHERE produk_id = ?");
    $stmt->execute([$produk_id]);
    $produk = $stmt->fetch(PDO::FETCH_ASSOC);
    if (!$produk) {
        http_response_code(400);
        echo json_encode([
            'status' => 'error',
            'message' => 'Produk not found',
            'data' => null
        ]);
        exit;
    }

    if ($produk['stok'] < $jumlah) {
        http_response_code(400);
        echo json_encode([
            'status' => 'error',
            'message' => 'Insufficient stock',
            'data' => null
        ]);
        exit;
    }

    $total_harga = $jumlah * $produk['harga'];

    // Begin transaction
    $pdo->beginTransaction();

    // Update stok
    $stmt = $pdo->prepare("UPDATE produk SET stok = stok - ? WHERE produk_id = ?");
    $stmt->execute([$jumlah, $produk_id]);

    // Insert penjualan
    $stmt = $pdo->prepare("INSERT INTO penjualan (produk_id, user_id, nama_pembeli, jumlah, total_harga, tanggal) VALUES (?, ?, ?, ?, ?, NOW())");
    $stmt->execute([$produk_id, $user_id, $nama_pembeli, $jumlah, $total_harga]);
    $penjualan_id = $pdo->lastInsertId();

    $pdo->commit();

    echo json_encode([
        'status' => 'success',
        'message' => 'Penjualan created successfully',
        'data' => ['penjualan_id' => $penjualan_id, 'produk_id' => $produk_id, 'user_id' => $user_id, 'nama_pembeli' => $nama_pembeli, 'jumlah' => $jumlah, 'total_harga' => $total_harga]
    ]);
} catch (Exception $e) {
    $pdo->rollBack();
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Internal server error',
        'data' => null
    ]);
}
?>