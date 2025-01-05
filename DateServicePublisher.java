import jakarta.xml.ws.Endpoint;

public class DateServicePublisher {
    public static void main(String[] args) {
        try {
            Endpoint.publish("http://localhost:8081/DateService", new DateService());
            System.out.println("Servizio avviato su http://localhost:8081/DateService");
        } catch (Exception e) {
            System.err.println("Errore durante l'avvio del servizio: " + e.getMessage());
        }
    }
}
