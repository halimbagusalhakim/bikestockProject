<?php
header('Content-Type: application/json');
require_once '../config/database.php';
require_once '../middleware/jwt.php';

// 1. Verifikasi Token JWT
authenticate();

// 2. Pastikan Method adalah DELETE (Sesuai dengan ApiService.kt)
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
 * 3. Ambil ID dari Query String ($_GET)
 * Karena di Android menggunakan @Query("id"), maka data dikirim di URL: delete.php?id=8
 * Variabel $_GET otomatis menangkap data tersebut meskipun method-nya DELETE.
 */
$produk_id = isset($_GET['id']) ? (int)$_GET['id'] : 0;

// 4. Validasi ID
if ($produk_id <= 0) {
    http_response_code(400);
    echo json_encode([
        'status' => 'error',
        'message' => 'Invalid input: ID produk diperlukan dan harus valid',
        'data' => null
    ]);
    exit;
}

try {
    // 5. Cek apakah produk ada sebelum dihapus
    $check_stmt = $pdo->prepare("SELECT produk_id FROM produk WHERE produk_id = ?");
    $check_stmt->execute([$produk_id]);
    
    if ($check_stmt->rowCount() == 0) {
        http_response_code(404);
        echo json_encode([
            'status' => 'error',
            'message' => 'Produk tidak ditemukan',
            'data' => null
        ]);
        exit;
    }

    // 6. Eksekusi Penghapusan
    $stmt = $pdo->prepare("DELETE FROM produk WHERE produk_id = ?");
    $stmt->execute([$produk_id]);

    echo json_encode([
        'status' => 'success',
        'message' => 'Produk berhasil dihapus',
        'data' => null
    ]);

} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Internal server error: ' . $e->getMessage(),
        'data' => null
    ]);
}
?>