<?php
header('Content-Type: application/json');
require_once '../config/database.php';
require_once '../middleware/jwt.php';

// Authenticate
authenticate();

// Check HTTP method
if ($_SERVER['REQUEST_METHOD'] !== 'PUT') {
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
if (!$input || !isset($input['penjualan_id']) || !isset($input['produk_id']) || !isset($input['jumlah'])) {
    http_response_code(400);
    echo json_encode([
        'status' => 'error',
        'message' => 'Invalid input: penjualan_id, produk_id, and jumlah are required',
        'data' => null
    ]);
    exit;
}

$penjualan_id = (int)$input['penjualan_id'];
$produk_id_baru = (int)$input['produk_id'];
$jumlah_baru = (int)$input['jumlah'];
$nama_pembeli_baru = isset($input['nama_pembeli']) ? trim($input['nama_pembeli']) : null;

if ($penjualan_id <= 0 || $produk_id_baru <= 0 || $jumlah_baru <= 0) {
    http_response_code(400);
    echo json_encode([
        'status' => 'error',
        'message' => 'Valid IDs and jumlah (>0) are required',
        'data' => null
    ]);
    exit;
}

try {
    // 1. Ambil data penjualan LAMA
    $stmt = $pdo->prepare("SELECT produk_id, jumlah FROM penjualan WHERE penjualan_id = ?");
    $stmt->execute([$penjualan_id]);
    $penjualan_lama = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$penjualan_lama) {
        http_response_code(404);
        echo json_encode(['status' => 'error', 'message' => 'Penjualan not found']);
        exit;
    }

    $produk_id_lama = (int)$penjualan_lama['produk_id'];
    $jumlah_lama = (int)$penjualan_lama['jumlah'];

    // 2. Ambil data produk BARU untuk cek harga dan stok
    $stmt = $pdo->prepare("SELECT harga, stok FROM produk WHERE produk_id = ?");
    $stmt->execute([$produk_id_baru]);
    $produk_baru = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$produk_baru) {
        http_response_code(404);
        echo json_encode(['status' => 'error', 'message' => 'New Product not found']);
        exit;
    }

    // 3. Validasi Stok
    if ($produk_id_baru === $produk_id_lama) {
        // Jika produk sama, cek apakah stok cukup untuk tambahan (selisih)
        $selisih = $jumlah_baru - $jumlah_lama;
        if ($selisih > 0 && $produk_baru['stok'] < $selisih) {
            http_response_code(400);
            echo json_encode(['status' => 'error', 'message' => 'Insufficient stock for this product']);
            exit;
        }
    } else {
        // Jika produk berubah, cek apakah stok produk baru cukup
        if ($produk_baru['stok'] < $jumlah_baru) {
            http_response_code(400);
            echo json_encode(['status' => 'error', 'message' => 'Insufficient stock for the new product']);
            exit;
        }
    }

    $total_harga_baru = $jumlah_baru * $produk_baru['harga'];

    // --- MULAI TRANSAKSI ---
    $pdo->beginTransaction();

    if ($produk_id_baru === $produk_id_lama) {
        // SKENARIO A: Produk Tetap Sama -> Hanya update selisih stok
        $selisih = $jumlah_baru - $jumlah_lama;
        $stmt = $pdo->prepare("UPDATE produk SET stok = stok - ? WHERE produk_id = ?");
        $stmt->execute([$selisih, $produk_id_lama]);
    } else {
        // SKENARIO B: Produk Berubah (Contoh: Sepeda A ke Sepeda B)
        // 1. Kembalikan stok produk LAMA (Sepeda A)
        $stmt = $pdo->prepare("UPDATE produk SET stok = stok + ? WHERE produk_id = ?");
        $stmt->execute([$jumlah_lama, $produk_id_lama]);

        // 2. Kurangi stok produk BARU (Sepeda B)
        $stmt = $pdo->prepare("UPDATE produk SET stok = stok - ? WHERE produk_id = ?");
        $stmt->execute([$jumlah_baru, $produk_id_baru]);
    }

    // Update data di tabel penjualan
    $stmt = $pdo->prepare("UPDATE penjualan SET produk_id = ?, nama_pembeli = ?, jumlah = ?, total_harga = ? WHERE penjualan_id = ?");
    $stmt->execute([$produk_id_baru, $nama_pembeli_baru, $jumlah_baru, $total_harga_baru, $penjualan_id]);

    $pdo->commit();
    // --- SELESAI TRANSAKSI ---

    echo json_encode([
        'status' => 'success',
        'message' => 'Penjualan updated and stock synchronized successfully',
        'data' => [
            'penjualan_id' => $penjualan_id,
            'produk_id' => $produk_id_baru,
            'jumlah' => $jumlah_baru,
            'total_harga' => $total_harga_baru
        ]
    ]);

} catch (Exception $e) {
    if ($pdo->inTransaction()) $pdo->rollBack();
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Internal server error: ' . $e->getMessage()
    ]);
}
?>