<?php
require_once '../config/database.php';

class JWT {
    private static $secret = 'your_secret_key_here'; // Change this to a secure key

    public static function encode($payload) {
        $header = json_encode(['typ' => 'JWT', 'alg' => 'HS256']);
        $header_encoded = str_replace(['+', '/', '='], ['-', '_', ''], base64_encode($header));

        $payload_encoded = str_replace(['+', '/', '='], ['-', '_', ''], base64_encode(json_encode($payload)));

        $signature = hash_hmac('sha256', $header_encoded . "." . $payload_encoded, self::$secret, true);
        $signature_encoded = str_replace(['+', '/', '='], ['-', '_', ''], base64_encode($signature));

        return $header_encoded . "." . $payload_encoded . "." . $signature_encoded;
    }

    public static function decode($token) {
        $parts = explode('.', $token);
        if (count($parts) !== 3) {
            return false;
        }

        $header = $parts[0];
        $payload = $parts[1];
        $signature = $parts[2];

        $expected_signature = hash_hmac('sha256', $header . "." . $payload, self::$secret, true);
        $expected_signature_encoded = str_replace(['+', '/', '='], ['-', '_', ''], base64_encode($expected_signature));

        if ($signature !== $expected_signature_encoded) {
            return false;
        }

        $payload_decoded = json_decode(base64_decode(str_replace(['-', '_'], ['+', '/'], $payload)), true);
        if ($payload_decoded['exp'] < time()) {
            return false; // Expired
        }

        return $payload_decoded;
    }
}

function authenticate() {
    $headers = getallheaders();
    $auth_header = null;
    foreach ($headers as $key => $value) {
        if (strtolower($key) === 'authorization') {
            $auth_header = $value;
            break;
        }
    }
    if (!$auth_header) {
        http_response_code(401);
        echo json_encode([
            'status' => 'error',
            'message' => 'Authorization header missing',
            'data' => null
        ]);
        exit;
    }
    if (!preg_match('/Bearer\s+(.*)$/i', $auth_header, $matches)) {
        http_response_code(401);
        echo json_encode([
            'status' => 'error',
            'message' => 'Invalid authorization format',
            'data' => null
        ]);
        exit;
    }

    $token = $matches[1];
    $payload = JWT::decode($token);
    if (!$payload) {
        http_response_code(401);
        echo json_encode([
            'status' => 'error',
            'message' => 'Invalid or expired token',
            'data' => null
        ]);
        exit;
    }

    // Check if token exists in database and not expired
    global $pdo;
    $stmt = $pdo->prepare("SELECT token_id FROM tokens WHERE token = ? AND expires_at > NOW()");
    $stmt->execute([$token]);
    if (!$stmt->fetch()) {
        http_response_code(401);
        echo json_encode([
            'status' => 'error',
            'message' => 'Token not found or expired in database',
            'data' => null
        ]);
        exit;
    }

    return $payload;
}
?>