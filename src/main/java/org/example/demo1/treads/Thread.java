package org.example.demo1.treads;

import javafx.application.Platform;
import javafx.scene.control.Slider;

public class Thread extends java.lang.Thread {
    private Slider slider;
    private int count = 0;
    private volatile boolean isStop = false;

    public Thread(Slider slider) {
        this.slider = slider;
    }

    public Thread(Slider slider, int count) {
        this.slider = slider;
        this.count = count;
    }

    @Override
    public void run() {
        while (isStop) {
            try {
                Platform.runLater(() -> slider.setValue(count++)); // Обновляем значение слайдера
                Thread.sleep(1000); // Задержка на 1 секунду
            } catch (InterruptedException e) {
                if (!isStop) {
                    break; // Поток завершает работу корректно при прерывании
                }
            }
        }
        System.out.println("Thread stopped.");
    }


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void disable() {
        isStop = false; // Останавливаем поток
        interrupt(); // Прерываем поток
    }

    public void enable() {
        if (!isAlive()) { // Проверяем, жив ли поток
            isStop = true;
            start(); // Запускаем поток заново, если он был прерван
        }
    }

}
