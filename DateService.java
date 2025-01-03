import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.Calendar;

@WebService(targetNamespace = "https://ffr4a.github.io/LavoriTPS")
public class DateService {

    @WebMethod
    public String getDayOfWeek(int day, int month, int year) {
        // Impostare la data
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day); // Mese è zero-indicizzato
        
        // Array per i giorni della settimana
        String[] days = { "Domenica", "Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato" };
        
        // Restituire il giorno della settimana
        return days[calendar.get(Calendar.DAY_OF_WEEK) - 1];
    }
}