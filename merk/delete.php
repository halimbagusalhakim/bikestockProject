<?php
header('Content-Type: application/json');
require_once '../config/database.php';
require_once '../middleware/jwt.php';

// Authenticate
authenticate();

// Check HTTP method
if ($_SERVER['REQUEST_METHOD'] !== 'DELETE') {
    http_response_code(405);
    echo json_encode([
        'status' => 'error',
        'message' => 'Method not allowed',
        'data' => null
    ]);
    exit;
}

// PERBAIKAN: Ambil input dari $_GET['id'] karena Android mengirim via Query Parameter (?id=...)
$merk_id = isset($_GET['id']) ? (int)$_GET['id'] : 0;

if ($merk_id <= 0) {
    http_response_code(400);
    echo json_encode([
        'status' => 'error',
        'message' => 'Invalid input: ID merk diperlukan dan harus valid',
        'data' => null
    ]);
    exit;
}

try {
    // Tambahan Keamanan: Cek apakah Merk masih digunakan oleh produk lain (Foreign Key check)
    // Jika masih ada produk dengan merk ini, penghapusan akan memicu exception di blok catch
    
    $stmt = $pdo->prepare("DELETE FROM merk WHERE merk_id = ?");
    $stmt->execute([$merk_id]);
    
    if ($stmt->rowCount() == 0) {
        http_response_code(404);
        echo json_encode([
            'status' => 'error',
            'message' => 'Merk tidak ditemukan',
            'data' => null
        ]);
        exit;
    }

    echo json_encode([
        'status' => 'success',
        'message' => 'Merk berhasil dihapus',
        'data' => null
    ]);
} catch (Exception $e) {
    // Menangani error jika merk gagal dihapus (misal: masih ada produk yang terkait)
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Gagal menghapus merk. Pastikan tidak ada produk yang menggunakan merk ini.',
        'data' => null
    ]);
}
?>