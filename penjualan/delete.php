<?php
header('Content-Type: application/json');
require_once '../config/database.php';
require_once '../middleware/jwt.php';

// 1. Authenticate Token
authenticate();

// 2. Cek HTTP method
if ($_SERVER['REQUEST_METHOD'] !== 'DELETE') {
    http_response_code(405);
    echo json_encode([
        'status' => 'error',
        'message' => 'Method not allowed',
        'data' => null
    ]);
    exit;
}

/**
 * 3. Ambil ID dari Query Parameter ($_GET)
 * Karena di ApiService.kt menggunakan @Query("id"), data ditangkap di $_GET['id']
 */
$penjualan_id = isset($_GET['id']) ? (int)$_GET['id'] : 0;

if ($penjualan_id <= 0) {
    http_response_code(400);
    echo json_encode([
        'status' => 'error',
        'message' => 'Invalid input: ID penjualan diperlukan',
        'data' => null
    ]);
    exit;
}

try {
    // 4. Cek keberadaan data
    $check = $pdo->prepare("SELECT penjualan_id FROM penjualan WHERE penjualan_id = ?");
    $check->execute([$penjualan_id]);
    
    if ($check->rowCount() == 0) {
        http_response_code(404);
        echo json_encode([
            'status' => 'error',
            'message' => 'Data penjualan tidak ditemukan',
            'data' => null
        ]);
        exit;
    }

    // 5. Eksekusi hapus
    $stmt = $pdo->prepare("DELETE FROM penjualan WHERE penjualan_id = ?");
    $stmt->execute([$penjualan_id]);

    echo json_encode([
        'status' => 'success',
        'message' => 'Catatan penjualan berhasil dihapus',
        'data' => null
    ]);

} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Gagal menghapus data: ' . $e->getMessage(),
        'data' => null
    ]);
}
?>