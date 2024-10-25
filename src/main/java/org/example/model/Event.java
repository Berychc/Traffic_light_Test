package org.example.model;
/**
 * Event представляет собой событие, которое передает информацию между светофорами.
 */
public class Event {
    protected final String senderId; // Идентификатор отправителя
    protected final int carCount; // Количество автомобилей
    protected final int pedestrianCount; // Количество пешеходов
    protected final String trafficLightState; // Текущее состояние светофора

    /**
     * Конструктор для создания нового события.
     *
     * @param senderId         Идентификатор отправителя.
     * @param carCount         Количество автомобилей.
     * @param pedestrianCount   Количество пешеходов.
     * @param trafficLightState Текущее состояние светофора.
     */
    public Event(String senderId, int carCount, int pedestrianCount, String trafficLightState) {
        this.senderId = senderId;
        this.carCount = carCount;
        this.pedestrianCount = pedestrianCount;
        this.trafficLightState = trafficLightState;
    }

    @Override
    public String toString() {
        return "Event{" +
                "senderId='" + senderId + '\'' +
                ", carCount=" + carCount +
                ", pedestrianCount=" + pedestrianCount +
                ", lightState='" + trafficLightState + '\'' +
                '}';
    }
}
