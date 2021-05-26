package com.nudorm.crawler.tasks;

import com.nudorm.crawler.domain.Url;
import com.nudorm.crawler.domain.UrlType;
import com.nudorm.crawler.ui.Result;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class Coordinator {
    protected final ConcurrentLinkedQueue<Url> queueParsing = new ConcurrentLinkedQueue<>();
    protected final ConcurrentSkipListSet<Url> listParsing = new ConcurrentSkipListSet<>();
    protected final ExecutorService executorServiceParsing;

    protected final ConcurrentLinkedQueue<Url> queueTesting = new ConcurrentLinkedQueue<>();
    protected final ConcurrentSkipListSet<Url> listTesting = new ConcurrentSkipListSet<>();
    protected final ExecutorService executorServiceTesting;

    protected final Url startUrl;
    protected final String[] filterParsing;
    protected final String[] filterTesting;

    public SimpleIntegerProperty parsingThreads = new SimpleIntegerProperty();
    public SimpleIntegerProperty testingThreads = new SimpleIntegerProperty();

    public SimpleIntegerProperty parsingQueueCount = new SimpleIntegerProperty();
    public SimpleIntegerProperty testingQueueCount = new SimpleIntegerProperty();

    public SimpleLongProperty parsed = new SimpleLongProperty();
    public SimpleLongProperty tested = new SimpleLongProperty();

    protected Timer timerTaskProperties = new Timer();
    protected Timer timerTaskParser = new Timer();
    protected Timer timerTaskTester = new Timer();

    protected final ObservableList<Result> observableListResult = FXCollections.synchronizedObservableList(
            FXCollections.observableList(new LinkedList<Result>())
    );

    public Coordinator(int nThreadsParsing, int nThreadsTesting, String startUrl, String[] filterParsing, String[] filterTesting) throws URISyntaxException {
        executorServiceParsing = Executors.newFixedThreadPool(nThreadsParsing);
        executorServiceTesting = Executors.newFixedThreadPool(nThreadsTesting);
        URI uri = new URI(startUrl);
        this.startUrl = new Url(null, uri, UrlType.PARSING);
        this.filterParsing = filterParsing;
        this.filterTesting = filterTesting;
    }

    public void start() {
        timerTaskProperties.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    parsingThreads.set(((ThreadPoolExecutor)executorServiceParsing).getActiveCount());
                    testingThreads.set(((ThreadPoolExecutor)executorServiceTesting).getActiveCount());

                    parsed.set(((ThreadPoolExecutor)executorServiceParsing).getCompletedTaskCount());
                    tested.set(((ThreadPoolExecutor)executorServiceTesting).getCompletedTaskCount());
                    parsingQueueCount.set(queueParsing.size());
                    testingQueueCount.set(queueTesting.size());
                });
            }
        }, 1000, 1000);

        getQueueParsing().add(startUrl);

        executorServiceParsing.submit(new Parser(this));
        Coordinator self = this;

        timerTaskParser.schedule(new TimerTask() {
            @Override
            public void run() {
                for (int i = 0; i < queueParsing.size(); i++) {
                    executorServiceParsing.submit(new Parser(self));
                }
            }
        }, 3000, 1000);

        timerTaskTester.schedule(new TimerTask() {
            @Override
            public void run() {
                for (int i = 0; i < queueTesting.size(); i++) {
                    executorServiceTesting.submit(new Tester(self));
                }
            }
        }, 3000, 1000);
    }

    public ObservableList<Result> getResults() {
        return observableListResult;
    }

    public ConcurrentLinkedQueue<Url> getQueueParsing() {
        return queueParsing;
    }

    public ConcurrentSkipListSet<Url> getListParsing() {
        return listParsing;
    }

    public ConcurrentLinkedQueue<Url> getQueueTesting() {
        return queueTesting;
    }

    public ConcurrentSkipListSet<Url> getListTesting() {
        return listTesting;
    }

    public Url getStartUrl() {
        return startUrl;
    }

    public String[] getFilterParsing() {
        return filterParsing;
    }

    public String[] getFilterTesting() {
        return filterTesting;
    }

    public UrlType getUrlType(String url) {
        AtomicReference<UrlType> type = new AtomicReference<>(UrlType.SKIP);

        Arrays.stream(filterParsing).forEach(s -> {
            if(url.contains(s)) {
                type.set(UrlType.PARSING);
            }
        });

        if(type.get().equals(UrlType.SKIP)) {
            Arrays.stream(filterTesting).forEach(s -> {
                if (url.contains(s)) {
                    type.set(UrlType.TESTING);
                }
            });
        }

        return type.get();
    }

    public void stop() {
        timerTaskParser.cancel();
        timerTaskTester.cancel();
        executorServiceParsing.shutdown();
        executorServiceTesting.shutdown();
    }
    public void quit() {
        stop();
        timerTaskProperties.cancel();
    }
}
