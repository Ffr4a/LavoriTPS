import javax.xml.ws.Endpoint;

public class DateServicePublisher {
    public static void main(String[] args) {
        Endpoint.publish("http://localhost:8080/DateService", new DateService());
        System.out.println("Servizio avviato su http://localhost:8080/DateService");
    }
}
