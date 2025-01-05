import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;

public class SimpleSoapServer {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(8081);
        System.out.println("Server avviato su http://localhost:8081");

        while (true) {
            Socket socket = serverSocket.accept();
            InputStream inputStream = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder request = new StringBuilder();
            String line;

            // Leggi la richiesta
            while (!(line = reader.readLine()).isEmpty()) {
                request.append(line).append("\n");
            }

            String requestBody = request.toString();
            System.out.println("Ricevuto: " + requestBody);

            // Estrai i numeri dall'XML ricevuto
            int number1 = Integer.parseInt(extractXmlValue(requestBody, "<number1>", "</number1>"));
            int number2 = Integer.parseInt(extractXmlValue(requestBody, "<number2>", "</number2>"));

            // Esegue la somma
            int result = number1 + number2;

            // Costruire la risposta SOAP
            String response = "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/'>" +
                    "<soapenv:Body>" +
                    "<result>" + result + "</result>" +
                    "</soapenv:Body></soapenv:Envelope>";

            // Invia la risposta
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(response.getBytes());
            outputStream.flush();
            socket.close();
        }
    }

    private static String extractXmlValue(String xml, String startTag, String endTag) {
        int startIndex = xml.indexOf(startTag) + startTag.length();
        int endIndex = xml.indexOf(endTag, startIndex);
        return xml.substring(startIndex, endIndex).trim();
    }
}

