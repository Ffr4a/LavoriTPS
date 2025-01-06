import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.Calendar;

public class DateServicePublisher {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(8081);
        System.out.println("Servizio avviato su http://localhost:8081/DateService");

        while (true) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("Connessione accettata da: " + socket.getInetAddress() + ":" + socket.getPort());

                InputStream inputStream = socket.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine;
                StringBuilder requestBuffer = new StringBuilder();

                // Leggi la richiesta
                while ((inputLine = bufferedReader.readLine()) != null && !inputLine.isEmpty()) {
                    requestBuffer.append(inputLine + "\n");
                }

                String request = requestBuffer.toString();
                System.out.println("Richiesta ricevuta.");

                // Controllo se la richiesta è di tipo OPTIONS
                if (request.startsWith("OPTIONS")) {
                    sendOptionsResponse(socket);
                } else if (request.startsWith("POST")) {
                    String xmlRequestBody = readRequestBody(bufferedReader);
                    System.out.println("Elaborazione risposta...");

                    String outputMessage = createResponse(xmlRequestBody);
                    sendResponse(socket, outputMessage);
                    System.out.println("Risposta inviata.");
                } else {
                    sendErrorResponse(socket, "Metodo non supportato.");
                }

                socket.close(); // Chiudi il socket dopo la risposta
            } catch (IOException e) {
                System.err.println("Errore nella gestione della connessione: " + e.getMessage());
            }
        }
    }

    private static String readRequestBody(BufferedReader bufferedReader) throws IOException {
        StringBuilder xmlRequestBody = new StringBuilder();
        char[] buffer = new char[1024];
        int bytesRead;

        // Limita la lettura a un massimo di 4096 caratteri (4 KB), ad esempio.
        int maxSize = 4096;
        int totalRead = 0;

        while (totalRead < maxSize && (bytesRead = bufferedReader.read(buffer)) != -1) {
            xmlRequestBody.append(buffer, 0, bytesRead);
            totalRead += bytesRead;
            if (bytesRead < buffer.length) break; // Nessun altro byte disponibile
        }
        return xmlRequestBody.toString();
    }

    private static void sendOptionsResponse(Socket socket) throws IOException {
        String response = "HTTP/1.1 200 OK\r\n" +
                          "Access-Control-Allow-Origin: *\r\n" +
                          "Access-Control-Allow-Methods: POST, OPTIONS\r\n" +
                          "Access-Control-Allow-Headers: Content-Type\r\n" +
                          "Content-Length: 0\r\n" +
                          "\r\n";
        sendRawResponse(socket, response);
    }

    private static void sendErrorResponse(Socket socket, String errorMessage) throws IOException {
        String response = "HTTP/1.1 400 Bad Request\r\n" +
                          "Content-Type: text/xml\r\n" +
                          "Content-Length: " + errorMessage.length() + "\r\n\r\n" +
                          "<error>" + errorMessage + "</error>";
        sendRawResponse(socket, response);
    }

    private static void sendRawResponse(Socket socket, String response) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private static String createResponse(String xmlContent) {
        try {
            String giorno = extractXmlValue(xmlContent, "<day>", "</day>");
            String mese = extractXmlValue(xmlContent, "<month>", "</month>");
            String anno = extractXmlValue(xmlContent, "<year>", "</year>");

            return "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/'>" +
                   "<soapenv:Body>" +
                   "<urn:return>" + 
                   getDayOfWeek(Integer.parseInt(giorno), Integer.parseInt(mese), Integer.parseInt(anno)) + 
                   "</urn:return></soapenv:Body></soapenv:Envelope>";
        } catch (Exception e) {
            return "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/'>" +
                   "<soapenv:Body>" +
                   "<urn:error>Richiesta non valida.</urn:error>" +
                   "</soapenv:Body></soapenv:Envelope>";
        }
    }

    private static void sendResponse(Socket socket, String response) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
        bufferedWriter.write("HTTP/1.1 200 OK\r\n");
        bufferedWriter.write("Content-Type: text/xml\r\n");
        bufferedWriter.write("Content-Length: " + response.length() + "\r\n");
        bufferedWriter.write("\r\n");
        bufferedWriter.write(response);
        bufferedWriter.flush();
    }

    private static String extractXmlValue(String xml, String startTag, String endTag) {
        int startIndex = xml.indexOf(startTag) + startTag.length();
        int endIndex = xml.indexOf(endTag, startIndex);
        return xml.substring(startIndex, endIndex).trim();
    }

    public static String getDayOfWeek(int day, int month, int year) {
        if (month < 1 || month > 12 || day < 1 || day > 31) {
            return "";
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);

        String[] days = {"Domenica", "Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato"};

        return days[calendar.get(Calendar.DAY_OF_WEEK) - 1];
    }
}
