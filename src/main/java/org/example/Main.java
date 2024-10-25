package org.example;

import org.example.model.LightTraffic;
import org.example.system.SystemTrafficControl;

/**
 * Главный класс для запуска системы управления светофорами.
 * Этот класс создает экземпляр системы управления, добавляет светофоры
 * и запускает имитацию движения.
 */
public class Main {
    public static void main(String[] args) {
        SystemTrafficControl system = new SystemTrafficControl();

        // Создание светофоров и добавление их в систему
        for (int i = 1; i <= 4; i++) {
            LightTraffic light = new LightTraffic("Автомобильный: " + i, true); // Автомобильный светофор
            system.addTrafficLight(light);
        }

        for (int i = 1; i <= 8; i++) {
            LightTraffic light = new LightTraffic("Пешеходный: " + i, false); // Пешеходный светофор
            system.addTrafficLight(light);
        }

        // Установим связи между светофорами
        system.connectLights();

        // Запуск симуляции
        system.simulateTraffic();
    }
}