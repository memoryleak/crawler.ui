package com.nudorm.crawler.tasks;

import com.nudorm.crawler.domain.Url;
import com.nudorm.crawler.domain.UrlType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Parser implements Runnable {
    private final Coordinator coordinator;

    public Parser(Coordinator coordinator) {
        this.coordinator = coordinator;
    }

    @Override
    public void run() {
        if(Thread.currentThread().isInterrupted()) return;
        Document doc = null;
        Url url = coordinator.getQueueParsing().poll();

        if (url == null || !url.getType().equals(UrlType.PARSING)) {
            return;
        }

        try {
            doc = Jsoup.connect(url.toString()).get();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            coordinator.listTesting.add(url);
        }

        if (doc == null) {
            return;
        }

        doc.getElementsByTag("a").eachAttr("href").forEach(href -> {
            if (!href.startsWith("/") && !href.startsWith("http")) {
                return;
            }

            if (href.startsWith("/")) {
                href = url.getSelf().getScheme() + "://" + url.getSelf().getHost() + href;
            }

            Url newUrl;

            try {
                newUrl = new Url(url.getSelf(), new URI(href), coordinator.getUrlType(href));
            } catch (URISyntaxException e) {
                e.printStackTrace();
                return;
            }

            if (newUrl.getType().equals(UrlType.PARSING) && !coordinator.getListParsing().contains(newUrl)) {
                coordinator.getListParsing().add(newUrl);
                coordinator.getQueueParsing().add(newUrl);
            }

            if (newUrl.getType().equals(UrlType.TESTING) && !coordinator.getListTesting().contains(newUrl)) {
                coordinator.getListTesting().add(newUrl);
                coordinator.getQueueTesting().add(newUrl);
            }
        });
    }
}
