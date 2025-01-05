import java.util.Calendar;

public class DateService {
    public String getDayOfWeek(int day, int month, int year) {
        // Validazione dell'input
        if (month < 1 || month > 12) {
            return "Mese non valido! Deve essere tra 1 e 12.";
        }
        if (day < 1 || day > 31) {
            return "Giorno non valido! Deve essere tra 1 e 31.";
        }
        // (Implementazione semplificata; potresti voler gestire i giorni effettivi di ogni mese)
        if ((month == 2 && day > 29) || (day == 31 && (month == 4 || month == 6 || month == 9 || month == 11))) {
            return "Giorno non valido per il mese!";
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
