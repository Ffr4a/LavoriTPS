<?php
$geolocation = null;
$errorMessage = null;

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $ip = $_POST['ip'];
    try {
        $client = new SoapClient("http://127.0.0.1/soap/geolocation_service.wsdl");
        $geolocation = $client->getIpGeolocation($ip);
    } catch (SoapFault $fault) {
        $errorMessage = "Errore SOAP: " . $fault->getMessage();
    }
}
?>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Servizio di Geolocalizzazione IP</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f9;
            color: #333;
            margin: 0;
            padding: 0;
        }
        .container {
            width: 70%;
            margin: 50px auto;
            background-color: #fff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }
        h1 {
            text-align: center;
            color: #4CAF50;
        }
        form {
            margin-bottom: 20px;
            padding: 15px;
            background-color: #f9f9f9;
            border-radius: 8px;
        }
        label {
            font-size: 16px;
            margin-bottom: 10px;
            display: inline-block;
        }
        input {
            width: 100%;
            padding: 10px;
            margin: 10px 0;
            border: 1px solid #ccc;
            border-radius: 4px;
            font-size: 14px;
        }
        button {
            background-color: #4CAF50;
            color: white;
            padding: 10px 20px;
            font-size: 16px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            width: 100%;
        }
        button:hover {
            background-color: #45a049;
        }
        .results {
            margin-top: 30px;
            padding: 20px;
            background-color: #f9f9f9;
            border-radius: 8px;
            border: 1px solid #ddd;
        }
        .results h2 {
            color: #4CAF50;
        }
        .results p {
            margin: 8px 0;
        }
        .footer {
            text-align: center;
            margin-top: 40px;
            font-size: 14px;
            color: #777;
        }
        .ip-list p {
            font-size: 16px;
            color: #333;
            margin: 10px 0;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Servizio di Geolocalizzazione IP</h1>
        
        <form method="POST" action="">
            <label for="ip">Inserisci l'indirizzo IP:</label>
            <input type="text" id="ip" name="ip" required>
            <button type="submit">Ottieni Geolocalizzazione</button>
        </form>

        <h3>Oppure, puoi provare uno di questi IP predefiniti:</h3>
        <div class="ip-list">
            <p>8.8.8.8 (Google DNS)</p>
            <p>8.8.4.4 (Google DNS)</p>
            <p>1.1.1.1 (Cloudflare DNS)</p>
            <p>1.0.0.1 (Cloudflare DNS)</p>
            <p>93.184.216.34 (example.com)</p>
            <p>151.101.0.133 (fastly.com)</p>
            <p>128.101.101.101 (MIT)</p>
        </div>

        <?php if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($geolocation)): ?>
            <div class="results">
                <h2>Informazioni Geografiche</h2>
                <p><strong>Paese:</strong> <?php echo htmlspecialchars($geolocation['country']); ?></p>
                <p><strong>Regione:</strong> <?php echo htmlspecialchars($geolocation['region']); ?></p>
                <p><strong>Città:</strong> <?php echo htmlspecialchars($geolocation['city']); ?></p>
                <p><strong>Latitudine:</strong> <?php echo htmlspecialchars($geolocation['latitude']); ?></p>
                <p><strong>Longitudine:</strong> <?php echo htmlspecialchars($geolocation['longitude']); ?></p>
            </div>
        <?php elseif (isset($errorMessage)): ?>
            <div class="results">
                <p style="color: red;"><?php echo htmlspecialchars($errorMessage); ?></p>
            </div>
        <?php endif; ?>

        <div class="footer">
            <p>Servizio di geolocalizzazione IP</p>
        </div>
    </div>
</body>
</html>