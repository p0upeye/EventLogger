import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EventLogger {
    private static final Scanner scanner = new Scanner(System.in);

    private static final String DIRECTORY_PATH = "results";
    private static final String FILE_NAME = "events.txt";
    private static final String FULL_FILE_PATH = DIRECTORY_PATH + File.separator + FILE_NAME;

                                                    // Замість запропонованого "yyyy-MM-dd HH:mm"
                                                    // я використав формат як у Windows, просто так звичніше
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    private static final String SEPARATOR = " — ";
    private static final String PRESS_ENTER_MSG = "\n(Press Enter to return)";

    public static void main(String[] args) {
        {
            System.out.println("╔════════════════════════════════╗");
            System.out.println("║     Event Logger v0.4-beta     ║");
            System.out.println("║        Terminal Edition        ║");
            System.out.println("╚════════════════════════════════╝\n");
        }

        // Перевіряє наявність директорії та файлу
        fileExistenceChecker();

        boolean continueRunning = true;
        while(continueRunning) continueRunning = showMainMenu();

        scanner.close();
        System.out.println("Exiting Event Logger. Goodbye!");
    }

    /*
    * UI METHODS
    * */

    /**
     * Показує головне меню та обробляє вибір користувача.
     * @return true якщо потрібно продовжувати роботу, false для виходу
     * */
    public static boolean showMainMenu() {
        System.out.println("MAIN MENU");
        System.out.println("—".repeat(18));
        System.out.println("[KEY] ACTION");
        System.out.println("[ 1 ] Log a new event");
        System.out.println("[ 2 ] View logged events");
        System.out.println("[ 3 ] Show statistics");
        System.out.println("[ 4 ] Search events by date");
        System.out.println("[ 0 ] Exit");

        System.out.print("\n[terminal] main-menu/> ");
        String input = scanner.nextLine().trim();

        try {
            int choice = Integer.parseInt(input);

            switch(choice) {
                case 1:
                    showLogNewEvent();
                    break;
                case 2:
                    showViewLoggedEvents();
                    break;
                case 3:
                    showStatistics();
                    break;
                case 4:
                    showSearchEventsByDate();
                    break;
                case 0:
                    return false;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    System.out.println();
            }
        } catch(NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            System.out.println();
        }

        return true;
    }

    /**
     * Показує інтерфейс для логування нової події.
     * Викликає метод запису події у файл.
     * */
    public static void showLogNewEvent() {
        System.out.println();
        System.out.println("LOG NEW EVENT");
        System.out.println("—".repeat(20));
        System.out.println("Enter an event description (or leave empty to cancel)");

        System.out.print("\n[terminal] log-new-event/> ");
        String input = scanner.nextLine().trim();

        // Якщо є відповідь записує подію у файл
        if(!input.isEmpty()) {
            writeEvent(input);

            System.out.println("Event logged successfully!");
            System.out.println();
        }
        else {
            System.out.println("No event description provided. Exiting.");
            System.out.println();
        }
    }

    /**
     * Показує всі записані події.
     * Якщо подій немає, повідомляє про це користувача.
     * */
    public static void showViewLoggedEvents() {
        System.out.println();
        System.out.println("VIEW LOGGED EVENTS");
        System.out.println("—".repeat(24));

        List<String> events = readEvents();

        if(!events.isEmpty()) {
            System.out.println("Total events: " + events.size());
            System.out.println();

            for(String event : events) {
                System.out.println(event);
            }

            System.out.println(PRESS_ENTER_MSG);
            scanner.nextLine();
        } else {
            System.out.println("No events logged yet.");
            System.out.println();
        }
    }

    /**
     * Показує статистику по записаних подіях.
     * Включаючи загальну кількість, кількість сьогоднішніх та першу подію.
     * */
    public static void showStatistics() {
        System.out.println();
        System.out.println("STATISTICS");
        System.out.println("—".repeat(18));

        List<String> events = readEvents();
        System.out.println("Total events logged: " + events.size());

        System.out.print("Events today: ");
        LocalDateTime now = LocalDateTime.now();
        int todayCount = 0;

        for(String event : events) {
            LocalDateTime eventDateTime = parseEventDateTime(event);

            if(eventDateTime != null &&
                    eventDateTime.toLocalDate().isEqual(now.toLocalDate())) {
                todayCount++;
            }
        }
        System.out.println(todayCount);

        System.out.print("First event:");
        if(!events.isEmpty()) {
            String firstEvent = events.getFirst();
            System.out.println(" " + firstEvent);
        } else {
            System.out.println(" N/A");
        }

        System.out.println(PRESS_ENTER_MSG);
        scanner.nextLine();
    }

    /**
     * Показує інтерфейс для пошуку подій за датою.
     * Виводить знайдені події або повідомлення, якщо подій немає.
     * */
    public static void showSearchEventsByDate() {
        System.out.println();
        System.out.println("SEARCH EVENTS BY DATE");
        System.out.println("—".repeat(26));
        System.out.println("Enter a date to search for events (format: dd-MM-yyyy)");

        System.out.print("\n[terminal] search-events-by-date/> ");
        String inputDate = scanner.nextLine().trim();

        if(inputDate.isEmpty()) {
            System.out.println("No date provided. Exiting.");
            System.out.println();
            return;
        }

        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate searchDate = LocalDate.parse(inputDate, dateFormatter);

            List<String> events = readEvents();
            List<String> matchedEvents = new ArrayList<>();

            for(String event : events) {
                LocalDateTime eventDateTime = parseEventDateTime(event);

                if(eventDateTime != null &&
                        eventDateTime.toLocalDate().isEqual(searchDate)) {
                    matchedEvents.add(event);
                }
            }

            System.out.println("\nSearch results for " + inputDate + ":");
            if(!matchedEvents.isEmpty()) {
                for(String event : matchedEvents) {
                    System.out.println(event);
                }
            } else {
                System.out.println("No events found for this date.");
            }

            System.out.println(PRESS_ENTER_MSG);
            scanner.nextLine();
        } catch(DateTimeParseException e) {
            System.out.println("Invalid date format. Please use dd-MM-yyyy.");
            System.out.println();
        }
    }

    /*
    * FILE METHODS
    * */

    /**
     * Парсить дату та час події з рядка.
     * @param eventLine рядок з подією
     * @return LocalDateTime об'єкт або null у випадку помилки
     * */
    private static LocalDateTime parseEventDateTime(String eventLine) {
        try {
            String[] parts = eventLine.split(SEPARATOR, 2);

            if(parts.length == 2) {
                return LocalDateTime.parse(parts[0], FORMATTER);
            }
        } catch(DateTimeParseException e) {
            System.err.println("Error parsing event line: " + eventLine);
        }

        return null;
    }

    /**
     * Перевіряє наявність директорії та файлу.
     * Якщо їх немає, створює їх.
     * @throws RuntimeException якщо не вдалося створити директорію або файл
     * */
    public static void fileExistenceChecker() {
        try {
            File directory = new File(DIRECTORY_PATH);
            File file = new File(FULL_FILE_PATH);

            if(!directory.exists()) {

                if(directory.mkdirs()) {
                    System.out.println("[+] Directory created: " + DIRECTORY_PATH);
                } else {
                    throw new RuntimeException("Could not create directory: " + DIRECTORY_PATH);
                }
            }

            if(!file.exists()) {
                if(file.createNewFile()) {
                    System.out.println("[+] File created: " + FILE_NAME);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create file " + FULL_FILE_PATH, e);
        }
    }

    /**
     * Записує подію з описом та поточним часом у файл.
     * @param description опис події
     * @throws RuntimeException якщо не вдалося записати у файл
     * */
    public static void writeEvent(String description) {
        try (BufferedWriter bWriter = new BufferedWriter(
                new FileWriter(FULL_FILE_PATH, true)
        )) {
            LocalDateTime now = LocalDateTime.now();
            String formatted = now.format(FORMATTER);

            String eventLine = formatted + SEPARATOR + description;

            bWriter.write(eventLine);
            bWriter.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
            System.out.println();
        }
    }

    /**
     * Зчитує всі події з файлу та повертає їх у вигляді списку рядків.
     * @return список подій
     * */
    private static List<String> readEvents() {
        List<String> events = new ArrayList<>();

        try(BufferedReader reader = new BufferedReader(
                new FileReader(FULL_FILE_PATH)
        )) {

            String line;
            while((line = reader.readLine()) != null) {

                if(!line.trim().isEmpty()) {
                    events.add(line);
                }
            }

        } catch(FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
            System.out.println();
        } catch(IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.out.println();
        }

        return events;
    }
}
