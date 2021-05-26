package com.nudorm.crawler.tasks;

import com.nudorm.crawler.domain.Url;
import com.nudorm.crawler.ui.Result;
import javafx.application.Platform;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Tester implements Runnable {
    private final Coordinator coordinator;

    public Tester(Coordinator coordinator) {
        this.coordinator = coordinator;
    }

    @Override
    public void run() {
        if(Thread.currentThread().isInterrupted()) return;
        Url url = coordinator.getQueueTesting().poll();
        HttpURLConnection connection = null;

        try {
            String modifiedUrl = url.toString().replace("store.nobelbiocare.com", "nobi-mg2.sta.platform.dental");

            connection = (HttpURLConnection) new URL(modifiedUrl).openConnection();
            connection.setConnectTimeout(1000 * 5); //wait 5 seconds the most
            connection.setReadTimeout(1000 * 5);
            url.setStatus(connection.getResponseCode());
            connection.disconnect();
        } catch (IOException e) {
            url.setStatus(-1);
        }
        finally {
            coordinator.getListTesting().add(url);
            Platform.runLater(() -> {
                coordinator.getResults().add(new Result(url));
            });

        }
    }
}
