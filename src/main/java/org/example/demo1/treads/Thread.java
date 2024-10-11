package org.example.demo1.treads;

import javafx.application.Platform;
import javafx.scene.control.Slider;

public class Thread extends java.lang.Thread {
    private Slider slider;
    private int count = 0;
    private boolean isStop;


    public Thread(Slider slider ) {
        this.slider = slider;
    }

    public Thread(Slider slider, int count, boolean isStop) {
        this.slider = slider;
        this.count = count;
        this.isStop = isStop;
    }

    @Override
    public void run() {
        while (isStop) {
            try {
                Platform.runLater(() -> slider.setValue(count++));
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                java.lang.System.out.println("Thread interrupted.");
                isStop = false;
                break;
            }
        }
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


}
