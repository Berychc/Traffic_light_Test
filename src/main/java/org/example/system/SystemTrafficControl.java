package org.example.system;

import org.example.model.LightTraffic;
import java.util.*;

/**
 * SystemTrafficControl управляет всеми светофорами на перекрестке.
 * Он отвечает за добавление светофоров, их связывание и моделирование движения на перекрестке.
 */
public class SystemTrafficControl {

    private final List<LightTraffic> lightsTraffic = new ArrayList<>();

    /**
     * Добавляет светофор в систему управления движением.
     *
     * @param light светофор, который будет добавлен
     */
    public void addTrafficLight(LightTraffic light) {
        lightsTraffic.add(light);
    }

    /**
     * Запускает бесконечную симуляцию движения.
     * Каждые 2 секунды добавляются автомобили и пешеходы.
     * Также переоцениваются состояния всех светофоров.
     */
    public void simulateTraffic() {
        // Бесконечная симуляция добавления машин и пешеходов
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (LightTraffic light : lightsTraffic) {
                    if (light.isCarLight()) {
                        light.addCar(); // Добавляем 1 автомобиль
                    } else {
                        light.addPedestrian(); // Добавляем 1 пешехода
                    }
                }

                // Перепроверяем состояния светофоров
                for (LightTraffic light : lightsTraffic) {
                    light.evaluateAndSwitch(); // Оцениваем состояние
                }
            }
        }, 0, 2000); // Каждые 2 секунды
    }

    /**
     * Устанавливает связи между всеми светофорами, чтобы они могли обмениваться событиями.
     */
    public void connectLights() {
        for (int i = 0; i < lightsTraffic.size(); i++) {
            LightTraffic light = lightsTraffic.get(i);
            for (int j = 0; j < lightsTraffic.size(); j++) {
                if (i != j) {
                    light.addOtherLight(lightsTraffic.get(j)); // Добавляем связь с другими светофорами
                }
            }
        }
    }
}
