package eventlogger.repository;

import eventlogger.model.Event;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Тут, мабуть, буде доречним пояснити що це за клас,
 * аби не виникало питань звідки я знаю про цей патерн...
 * <p>
 * Це клас для роботи з репозиторієм подій, що зберігаються у файлі.
 * Коротко кажучи, це DAO(Data Access Object) для подій.
 * <p>
 * Про патерн дізнався від claude.ai, коли він робив ревю мого коду,
 * річ здалася мені цікавою, тому і використав.
 * <p>
 * <a href="https://uk.wikipedia.org/wiki/Data_access_object">Data access object Wikipedia</a>
 * @param filePath Шлях до файлу з подіями.
 * */
public record EventRepository(String filePath) {

    /**
     * Зберігає подію у файл.
     * @param event Подія для збереження.
     * @return true, якщо збереження пройшло успішно, інакше false.
     */
    public boolean save(Event event) {
        try(BufferedWriter writer = new BufferedWriter(
                new FileWriter(filePath, true)
        )) {
            writer.write(event.toFileString());
            writer.newLine();

            return true;
        } catch(IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Зчитує всі події з файлу.
     * @return Список подій.
     */
    public List<Event> findAll() {
        List<Event> events = new ArrayList<>();

        try(BufferedReader reader = new BufferedReader(
                new FileReader(filePath)
        )) {
            String line;

            while((line = reader.readLine()) != null) {
                Event event = Event.fromFileString(line);

                if(event != null) {
                    events.add(event);
                }
            }

        } catch(FileNotFoundException e) {
            return events;
        } catch(IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

        return events;
    }

    /**
     * Знаходить події за вказаною датою.
     * @param date Дата для пошуку.
     * @return Список подій, що відбулися в цю дату.
     */
    public List<Event> findByDate(LocalDate date) {
        return findAll().stream()
                .filter(event -> event.getDateTime().toLocalDate().equals(date))
                .collect(Collectors.toList());
    }

    /**
     * Знаходить події, що відбулися сьогодні.
     * @return Список подій за сьогоднішню дату.
     */
    public List<Event> findToday() {
        return findByDate(LocalDate.now());
    }

    /**
     * Знаходить першу подію у файлі.
     * @return Перша подія або null, якщо файл порожній.
     */
    public Event findFirst() {
        List<Event> events = findAll();

        return events.isEmpty() ? null : events.getFirst();
    }

    /**
     * Знаходить останню подію у файлі.
     * @return Остання подія або null, якщо файл порожній.
     */
    public Event findLast() {
        List<Event> events = findAll();

        return events.isEmpty() ? null : events.getLast();
    }

    /**
     * Підраховує загальну кількість подій у файлі.
     * @return Кількість подій.
     */
    public int count() {
        return findAll().size();
    }

    /**
     * Перевіряє, чи є у файлі хоча б одна подія.
     * @return true, якщо є події, інакше false.
     */
    public boolean hasEvents() {
        return count() > 0;
    }
}
