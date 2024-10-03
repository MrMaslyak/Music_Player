package org.example.demo1.Treads;

import javafx.application.Platform;
import javafx.scene.control.Slider;
import org.example.demo1.HelloController;

public class Thread extends java.lang.Thread {
    private Slider slider;
    private int count = 0;
    private boolean isStop = true;


    public Thread(Slider slider ) {
        this.slider = slider;
    }

    public Thread(Slider slider, int count) {
        this.slider = slider;
        this.count = count;
    }

    @Override
    public void run() {
        while (isStop) {
            Platform.runLater(() -> slider.valueProperty().setValue(count++));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("stop this");

            }
        }
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isStop() {
        return isStop;
    }

    public void setStop(boolean stop) {
        isStop = stop;
    }
}
