package org.example.model;

import java.util.*;

/**
 * Класс, представляющий светофор на перекрестке.
 * Управляет состоянием светофора и взаимодействует с другими светофорами.
 */
public class LightTraffic {
    private String id; // Уникальный идентификатор светофора
    private int car; // Количество ожидающих автомобилей
    private int pedestrian; // Количество ожидающих пешеходов
    private String state; // Текущий свет "Красный", "Зеленый (авто)", "Зеленый (пешеход)"
    private List<LightTraffic> otherLights = new ArrayList<>(); // Список других светофоров
    private boolean isCarLight; // true - автомобильный светофор, false - пешеходный светофор
    private Queue<Event> eventQueue = new LinkedList<>(); // Очередь событий

    // Максимальное количество автомобилей и пешеходов на светофоре
    private static final int MAX_CARS_ALLOWED = 20; // Максимум 20 автомобилей
    private static final int MAX_PEDESTRIANS_ALLOWED = 50; // Максимум 50 пешеходов

    /**
     * Конструктор для создания светофора.
     *
     * @param id Уникальный идентификатор светофора.
     * @param isCarLight true, если светофор автомобильный, false, если пешеходный.
     */
    public LightTraffic(String id, boolean isCarLight) {
        this.id = id;
        this.isCarLight = isCarLight;
        this.car = 0;
        this.pedestrian = 0;
        this.state = "Красный"; // Начальное состояние
    }

    /**
     * Добавляет другой светофор для связи.
     *
     * @param light Другой светофор для добавления.
     */
    public void addOtherLight(LightTraffic light) {
        otherLights.add(light);
    }

    /**
     * Добавляет 1 автомобиль к светофору, если не превышен лимит.
     */
    public void addCar() {
        if (isCarLight && car < MAX_CARS_ALLOWED) {
            car++;
            sendEvent(); // Отправляем событие другим светофорам
        }
    }

    /**
     * Добавляет 1 пешехода к светофору, если не превышен лимит.
     */
    public void addPedestrian() {
        if (!isCarLight && pedestrian < MAX_PEDESTRIANS_ALLOWED) {
            pedestrian++;
            sendEvent(); // Отправляем событие другим светофорам
        }
    }

    /**
     * Обрабатывает входящие события от других светофоров.
     *
     * @param event Событие, полученное от другого светофора.
     */
    public void receiveEvent(Event event) {
        if (event.senderId.equals(this.id)) return; // Игнорируем собственные события

        this.car = Math.min(MAX_CARS_ALLOWED, this.car + Math.max(0, event.carCount));
        this.pedestrian = Math.min(MAX_PEDESTRIANS_ALLOWED, this.pedestrian + Math.max(0, event.pedestrianCount));
        System.out.printf("Светофор [%s]: Получено событие. Авто: %d, Пешеходы: %d%n", id, this.car, this.pedestrian);

        eventQueue.add(event); // Помещаем событие в очередь
        processEvents(); // Обрабатываем события
    }

    /**
     * Обрабатывает очередь событий, переоценивает и переключает светофор.
     */
    private void processEvents() {
        while (!eventQueue.isEmpty()) {
            Event event = eventQueue.poll(); // Удаляем событие из очереди
            evaluateAndSwitch(); // Оцениваем состояние светофора
        }
    }

    /**
     * Оценивает текущее состояние светофора и при необходимости переключает его.
     */
    public synchronized void evaluateAndSwitch() {
        boolean needToChange = false;

        if (isCarLight && car > 0 && state.equals("Красный")) {
            int timeGreen = calculateAdaptiveGreenTime();
            switchToGreen(timeGreen, "авто");
            needToChange = true;
        } else if (!isCarLight && pedestrian > 0 && state.equals("Красный")) {
            switchToGreen(10, "пешеход");
            needToChange = true;
        }

        // Переключаемся на красный, если больше нет машин или пешеходов
        if (state.equals("Зеленый (авто)") && car == 0) {
            switchToRed();
            needToChange = true;
        } else if (state.equals("Зеленый (пешеход)") && pedestrian == 0) {
            switchToRed();
            needToChange = true;
        }

        if (needToChange) {
            informOtherLights(); // Уведомляем другие светофоры о изменении
        }
    }

    /**
     * Рассчитывает адаптивное время для зеленого света.
     *
     * @return Время зеленого света в секундах.
     */
    private int calculateAdaptiveGreenTime() {
        if (isCarLight) {
            return Math.min(60, 5 + (car / 2)); // Максимум 60 секунд
        } else {
            return 10; // Фиксированное время для пешеходов
        }
    }

    /**
     * Переключает светофор на зеленый свет.
     *
     * @param timeGreen Время для зеленого света.
     * @param type Тип светофора ("авто" или "пешеход").
     */
    private void switchToGreen(int timeGreen, String type) {
        if (type.equals("авто")) {
            state = "Зеленый (авто)";
        } else {
            state = "Зеленый (пешеход)";
        }
        System.out.printf("Светофор [%s]: Переключается на Зеленый (%s) на %d секунд. (Авто: %d, Пешеходы: %d)%n",
                id, type, timeGreen, car, pedestrian);

        // Таймер для автоматической смены состояния
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                switchToRed();
                clearQueues(); // Очищаем количество автомобилей и пешеходов
                sendEvent(); // Отправляем событие другим светофорам

                // Проверим остальные светофоры
                for (LightTraffic light : otherLights) {
                    new Thread(light::evaluateAndSwitch).start(); // Уведомляем другие светофоры
                }
            }
        }, timeGreen * 1000);
    }

    /**
     * Переключает светофор на красный свет.
     */
    private void switchToRed() {
        state = "Красный";
        System.out.printf("Светофор [%s]: Переключается на красный.%n", id);
    }

    /**
     * Очищает очереди, уменьшая количество автомобилей и пешеходов.
     */
    private void clearQueues() {
        if (isCarLight && car > 0) {
            car = Math.max(0, car - 5); // Уменьшаем количество автомобилей
        }
        if (!isCarLight && pedestrian > 0) {
            pedestrian = Math.max(0, pedestrian - 10); // Уменьшаем количество пешеходов
        }
        sendEvent(); // Отправляем обновленное состояние другим светофорам
    }

    /**
     * Отправляет событие об изменении состояния другим светофорам.
     */
    private void sendEvent() {
        Event event = new Event(this.id, car, pedestrian, state);
        for (LightTraffic light : otherLights) {
            light.receiveEvent(event); // Отправляем событие другим светофорам
        }
        System.out.printf("Светофор [%s]: Отправка события: %s%n", id, event);
    }

    /**
     * Уведомляет другие светофоры о необходимости переоценки состояния.
     */
    private void informOtherLights() {
        for (LightTraffic light : otherLights) {
            new Thread(light::evaluateAndSwitch).start();
        }
    }

    /**
     * Проверяет, является ли светофор автомобильным.
     *
     * @return true, если светофор автомобильный, false в противном случае.
     */
    public boolean isCarLight() {
        return isCarLight;
    }
}
