import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.Calendar;

public class DateServicePublisher {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(8081);
        System.out.println("Servizio avviato su http://localhost:8081/DateService");
        
        while (true) {
            Socket socket = serverSocket.accept();
            InputStream inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            
            String inputLine;
            StringBuilder requestBuffer = new StringBuilder();
            
            // Leggi la richiesta
            while ((inputLine = bufferedReader.readLine()) != null && !inputLine.isEmpty()) {
                requestBuffer.append(inputLine + "\n");
            }

            String request = requestBuffer.toString();
            System.out.println("Ricevuto: " + request);
            
            // Controllo se la richiesta è di tipo OPTIONS
            if (request.startsWith("OPTIONS")) {
                String response = "HTTP/1.1 200 OK\r\n" +
                                  "Access-Control-Allow-Origin: *\r\n" +
                                  "Access-Control-Allow-Methods: POST, OPTIONS\r\n" +
                                  "Access-Control-Allow-Headers: Content-Type\r\n" +
                                  "Content-Length: 0\r\n" +
                                  "\r\n";
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(response.getBytes());
                outputStream.flush();
            } else if (request.startsWith("POST")) {
                // Leggi il corpo della richiesta
                StringBuilder xmlRequestBody = new StringBuilder();
                char[] buffer = new char[1024];
                int bytesRead;
                
                // Salta le righe di intestazione e leggi il corpo
                while ((bytesRead = bufferedReader.read(buffer)) != -1) {
                    xmlRequestBody.append(buffer, 0, bytesRead);
                }

                String xmlContent = xmlRequestBody.toString();
                System.out.println("Corpo della richiesta XML: " + xmlContent);

                // Estrai giorno, mese e anno dall'XML
                try {
                    String giorno = extractXmlValue(xmlContent, "<day>", "</day>");
                    String mese = extractXmlValue(xmlContent, "<month>", "</month>");
                    String anno = extractXmlValue(xmlContent, "<year>", "</year>");
                    
                    String outputMessage = "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/'>" +
                                           "<soapenv:Body>" +
                                           "<urn:return>" + 
                                           getDayOfWeek(Integer.parseInt(giorno), Integer.parseInt(mese), Integer.parseInt(anno)) + 
                                           "</urn:return></soapenv:Body></soapenv:Envelope>";

                    OutputStream outputStream = socket.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                    bufferedWriter.write(outputMessage);
                    bufferedWriter.flush();
                } catch (Exception e) {
                    String errorMessage = "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/'>" +
                                          "<soapenv:Body>" +
                                          "<urn:error>Richiesta non valida. Assicurati che il formato sia corretto.</urn:error>" +
                                          "</soapenv:Body></soapenv:Envelope>";
                    OutputStream outputStream = socket.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                    bufferedWriter.write(errorMessage);
                    bufferedWriter.flush();
                    e.printStackTrace(); // Stampa lo stack trace per il debug
                }
            } 
            socket.close();
        }
    }
    
    private static String extractXmlValue(String xml, String startTag, String endTag) {
        int startIndex = xml.indexOf(startTag) + startTag.length();
        int endIndex = xml.indexOf(endTag, startIndex);
        return xml.substring(startIndex, endIndex).trim();
    }

    public static String getDayOfWeek(int day, int month, int year) {
        // Validazione dell'input
        if (month < 1 || month > 12) {
            return "";
        }
        if (day < 1 || day > 31) {
            return "";
        }

        // Impostare la data
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day); // Mese è zero-indicizzato
        
        // Array per i giorni della settimana
        String[] days = { "Domenica", "Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato" };
        
        // Restituire il giorno della settimana
        return days[calendar.get(Calendar.DAY_OF_WEEK) - 1];
    }
}
