<?php
function getIpGeolocation($ip) {
    $url = "http://ip-api.com/xml/$ip";
    $xml = @simplexml_load_file($url);
    if ($xml === false) {
        error_log("Errore nel caricamento del file XML.");
        throw new SoapFault("Server", "Errore nel caricamento delle informazioni di geolocalizzazione.");
    } else {
        error_log("Geolocalizzazione IP caricata con successo.");
    }
    $geolocation = [
        'country' => (string) $xml->country,
        'region' => (string) $xml->regionName,
        'city' => (string) $xml->city,
        'latitude' => (string) $xml->lat,
        'longitude' => (string) $xml->lon
    ];
    return $geolocation;
}
$options = ['uri' => 'http://127.0.0.1/soap/'];  
$server = new SoapServer("geolocation_service.wsdl", $options);
error_log("SOAP server inizializzato");
$server->addFunction("getIpGeolocation");
$server->handle();
?>