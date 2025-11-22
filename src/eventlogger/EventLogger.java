package eventlogger;

import eventlogger.model.Event;
import eventlogger.service.EventService;
import eventlogger.util.FileManager;
import eventlogger.repository.EventRepository;

import java.util.List;
import java.util.Scanner;

public class EventLogger {
    private static final Scanner scanner = new Scanner(System.in);
    private static EventService eventService;

    // Конфігурація
    private static final String DIRECTORY_PATH = "results";
    private static final String FILE_NAME = "events.txt";

    // UI
    private static final String PRESS_ENTER_MSG = "\n(Press Enter to return)";
    private static final String SEPARATOR_CHAR = "—";

    public static void main(String[] args) {
        {
            System.out.println("╔════════════════════════════════╗");
            System.out.println("║       Event Logger  v1.0       ║");
            System.out.println("║        Terminal Edition        ║");
            System.out.println("╚════════════════════════════════╝\n");
        }

        initialize();

        boolean continueRunning = true;
        while(continueRunning) continueRunning = showMainMenu();

        scanner.close();
        System.out.println("Exiting Event Logger. Goodbye!");
    }

    /**
     * Ініціалізація сервісів.
     * */
    private static void initialize() {
        try {
            String filePath = FileManager.fileExistenceChecker(DIRECTORY_PATH, FILE_NAME);

            EventRepository repository = new EventRepository(filePath);
            eventService = new EventService(repository);
        } catch(RuntimeException e) {
            System.err.println("Initialization error: " + e.getMessage());
            System.exit(1);
        }
    }

    /*
     * UI METHODS
     * */

    /**
     * Відображає головне меню та обробляє вибір користувача.
     * @return true, якщо потрібно продовжити роботу, інакше false.
     * */
    private static boolean showMainMenu() {
        printHeader("MAIN MENU");
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
            System.out.println();

            switch(choice) {
                case 1: showLogNewEvent(); break;
                case 2: showViewLoggedEvents(); break;
                case 3: showStatistics(); break;
                case 4: showSearchEventsByDate(); break;
                case 0: return false;
                default:
                    System.err.println("Invalid choice. Please enter a number from 0 to 4.");
                    System.out.println();
            }
        } catch(NumberFormatException e) {
            System.err.println("Invalid input. Please enter a number.");
            System.out.println();
        }

        return true;
    }

    /**
     * Відображає інтерфейс для логування нової події.
     * */
    private static void showLogNewEvent() {
        printHeader("LOG NEW EVENT");
        System.out.println("Enter an event description (or leave empty to cancel)");

        System.out.print("\n[terminal] log-new-event/> ");
        String input = scanner.nextLine().trim();

        if(!input.isEmpty()) {

            if(eventService.logNewEvent(input)) {
                System.out.println("Event logged successfully!");
            } else {
                System.err.println("Failed to log event. Please try again.");
            }
        } else {
            System.out.println("No event description provided. Cancelled.");
        }

        System.out.println();
    }

    /**
     * Відображає всі залоговані події.
     * */
    private static void showViewLoggedEvents() {
        printHeader("VIEW LOGGED EVENTS");

        List<Event> events = eventService.getAllEvents();

        if(!events.isEmpty()) {
            System.out.println("Total events: " + events.size());
            System.out.println();

            int index = 1;
            for(Event event : events) {
                System.out.println(index++ + ") " + event);
            }

            waitForEnter();
        } else {
            System.out.println("No events logged yet.");
            System.out.println();
        }
    }

    /**
     * Відображає статистику подій.
     * Включаючи загальну кількість, кількість сьогоднішніх подій, першу та останню подію.
     * */
    private static void showStatistics() {
        printHeader("STATISTICS");

        EventService.EventStatistics stats = eventService.getStatistics();

        System.out.println("Total events logged: " + stats.totalCount());
        System.out.println("Events today: " + stats.todayCount());

        System.out.print("First event: ");
        if(stats.firstEvent() != null) {
            System.out.println(stats.firstEvent());
        } else {
            System.out.println("N/A");
        }

        System.out.print("Last event: ");
        if(stats.lastEvent() != null) {
            System.out.println(stats.lastEvent());
        } else {
            System.out.println("N/A");
        }

        waitForEnter();
    }

    /**
     * Відображає інтерфейс для пошуку подій за датою.
     * */
    private static void showSearchEventsByDate() {
        printHeader("SEARCH EVENTS BY DATE");
        System.out.println("Enter a date to search for events (format: dd-MM-yyyy)");

        System.out.print("\n[terminal] search-events-by-date/> ");
        String inputDate = scanner.nextLine().trim();

        if(inputDate.isEmpty()) {
            System.out.println("No date provided. Cancelled.");
            System.out.println();
            return;
        }

        List<Event> matchedEvents = eventService.searchEventsByDate(inputDate);

        if(matchedEvents == null) {
            System.err.println("Invalid date format. Please use dd-MM-yyyy.");
            System.out.println();
            return;
        }

        System.out.println("\nSearch results for " + inputDate + ":");

        if(!matchedEvents.isEmpty()) {
            System.out.println("Found " + matchedEvents.size() + " event(s):\n");

            int index = 1;
            for(Event event : matchedEvents) {
                System.out.println(index++ + ") " + event);
            }
        } else {
            System.out.println("No events found for this date.");
        }

        waitForEnter();
    }

    /*
    * HELPER METHODS
    * */

    /**
     * Друкує заголовок секції з розділювачем.
     * @param title Текст заголовка.
     * */
    private static void printHeader(String title) {
        System.out.println(title);
        System.out.println(SEPARATOR_CHAR.repeat(title.length()));
    }

    /**
     * Очікує натискання клавіші Enter для продовження.
     * */
    private static void waitForEnter() {
        System.out.println(PRESS_ENTER_MSG);
        scanner.nextLine();
    }
}
