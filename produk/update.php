<?php
header('Content-Type: application/json');
require_once '../config/database.php';
require_once '../middleware/jwt.php';

// Authenticate
authenticate();

if ($_SERVER['REQUEST_METHOD'] !== 'PUT') {
    http_response_code(405);
    echo json_encode(['status' => 'error', 'message' => 'Method not allowed']);
    exit;
}

$input = json_decode(file_get_contents('php://input'), true);
if (!$input || !isset($input['produk_id'])) {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'Invalid input']);
    exit;
}

$produk_id = (int)$input['produk_id'];

// --- PERBAIKAN: Cek apakah produk benar-benar ada di database ---
$checkStmt = $pdo->prepare("SELECT produk_id FROM produk WHERE produk_id = ?");
$checkStmt->execute([$produk_id]);
if (!$checkStmt->fetch()) {
    http_response_code(404);
    echo json_encode(['status' => 'error', 'message' => 'Produk not found']);
    exit;
}

$update_fields = [];
$update_values = [];

// Mapping field
if (isset($input['nama_produk'])) { $update_fields[] = 'nama_produk = ?'; $update_values[] = trim($input['nama_produk']); }
if (isset($input['deskripsi'])) { $update_fields[] = 'deskripsi = ?'; $update_values[] = trim($input['deskripsi']); }
if (isset($input['stok'])) { $update_fields[] = 'stok = ?'; $update_values[] = (int)$input['stok']; }
if (isset($input['harga'])) { $update_fields[] = 'harga = ?'; $update_values[] = (float)$input['harga']; }
if (isset($input['merk_id'])) { $update_fields[] = 'merk_id = ?'; $update_values[] = (int)$input['merk_id']; }

if (empty($update_fields)) {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'No valid fields to update']);
    exit;
}

$update_values[] = $produk_id;

try {
    $stmt = $pdo->prepare("UPDATE produk SET " . implode(', ', $update_fields) . " WHERE produk_id = ?");
    $stmt->execute($update_values);

    // Sekalipun rowCount() adalah 0 (karena data sama), kita tetap kirim success 
    // karena kita sudah pastikan di atas bahwa produknya ada.
    echo json_encode([
        'status' => 'success',
        'message' => 'Produk updated successfully',
        'data' => ['produk_id' => $produk_id]
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => $e->getMessage()]);
}
?>