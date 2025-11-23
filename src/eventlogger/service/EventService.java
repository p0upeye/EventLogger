package eventlogger.service;

import eventlogger.repository.EventRepository;
import eventlogger.model.Event;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class EventService {
    private final EventRepository repository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public EventService(EventRepository repository) {
        this.repository = repository;
    }

    /**
     * Логує нову подію з вказаним описом.
     * @param description Опис події.
     * @return true, якщо подія успішно збережена, інакше false.
     */
    public boolean logNewEvent(String description) {
        if(description == null || description.trim().isEmpty()) {
            return false;
        }
        Event event = new Event(description.trim());

        return repository.save(event);
    }

    public List<Event> getAllEvents() {
        return repository.findAll();
    }

    /**
     * Шукає події за вказаною датою.
     * @param dateString Дата у форматі що вказується вище.
     * @return Список подій на вказану дату або null, якщо формат дати некоректний.
     */
    public List<Event> searchEventsByDate(String dateString) {
        try {
            LocalDate date = LocalDate.parse(dateString, DATE_FORMATTER);

            return repository.findByDate(date);
        } catch(DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Генерує статистику подій.
     * @return Об'єкт з статистикою подій.
     */
    public EventStatistics getStatistics() {
        int totalCount = repository.count();
        int todayCount = repository.findToday().size();
        Event firstEvent = repository.findFirst();
        Event lastEvent = repository.findLast();

        return new EventStatistics(totalCount, todayCount, firstEvent, lastEvent);
    }

    /**
     * Видаляє подію за номером (не індексом в лісті, а саме номером події).
     * @param eventIndex номер події.
     * @return true, якщо успішно видалено, інакше false.
     */
    public boolean deleteEvent(int eventIndex) {
        if(eventIndex < 1) return false;

        return repository.deleteByIndex(eventIndex - 1);
    }

    /**
     * Видаляє всі події
     * @return true якщо успішно
     */
    public boolean deleteAllEvents() {
        return repository.deleteAll();
    }

    public boolean hasEvents() {
        return repository.hasEvents();
    }

    /**
     * Внутрішній клас для зберігання статистики подій.
     */
    public record EventStatistics(int totalCount, int todayCount, Event firstEvent, Event lastEvent) {

        public boolean hasEvents() {
            return totalCount > 0;
        }
    }
}
