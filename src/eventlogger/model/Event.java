package eventlogger.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Event {
                                                    // Замість запропонованого "yyyy-MM-dd HH:mm"
                                                    // я використав формат як у Windows, просто так звичніше
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    private static final String SEPARATOR = " — ";

    private final LocalDateTime dateTime;
    private final String description;

    /**
     * Конструктор для створення події з поточним часом та описом.
     * @param description Опис події.
     */
    public Event(String description) {
        this.dateTime = LocalDateTime.now();
        this.description = description;
    }

    /**
     * Конструктор для створення події з вказаним часом та описом.
     * @param dateTime Час події.
     * @param description Опис події.
     */
    public Event(LocalDateTime dateTime, String description) {
        this.dateTime = dateTime;
        this.description = description;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }
    public String getDescription() {
        return description;
    }

    /**
     * Форматує подію у рядок для збереження у файл.
     * @return Відформатований рядок.
     */
    public String toFileString() {
        return dateTime.format(FORMATTER) + SEPARATOR + description;
    }

    /**
     * Створює подію з рядка, зчитаного з файлу.
     * @param line Рядок з файлу.
     * @return Об'єкт події або null, якщо рядок некоректний.
     */
    public static Event fromFileString(String line) {
        if(line == null || line.trim().isEmpty()) {
            return null;
        }

        try {
            String[] parts = line.split(SEPARATOR, 2);

            if(parts.length == 2) {
                LocalDateTime dateTime = LocalDateTime.parse(parts[0].trim(), FORMATTER);
                String description = parts[1].trim();

                return new Event(dateTime, description);
            }
        } catch(Exception e) {
            System.err.println("Could not parse event line: " + line);
        }

        return null;
    }

    @Override
    public String toString() {
        return toFileString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;
        return Objects.equals(dateTime, event.dateTime) && Objects.equals(description, event.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateTime, description);
    }
}
