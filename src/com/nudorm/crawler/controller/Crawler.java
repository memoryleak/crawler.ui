package com.nudorm.crawler.controller;

import com.nudorm.crawler.tasks.Coordinator;
import com.nudorm.crawler.tasks.CoordinatorBuilder;
import com.nudorm.crawler.ui.Result;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.net.URISyntaxException;

public class Crawler {
    @FXML
    public Slider sliderDesiredParsingThreads;
    @FXML
    public Label labelDesiredParsingThreads;
    @FXML
    public Slider sliderDesiredTestingThreads;
    @FXML
    public Label labelDesiredTestingThreads;
    @FXML
    public TableView<Result> tableViewResults;
    @FXML
    public TableColumn<String,String> tableColumnParent;
    @FXML
    public TableColumn<String,String> tableColumnSelf;
    @FXML
    public TableColumn<Integer,String> tableColumnStatus;
    @FXML
    public TextField textStartUrl;
    @FXML
    public TextArea textAreaFilterParsing;
    @FXML
    public TextArea textAreaFilterTesting;
    @FXML
    public Label labelCurrentParsers;
    @FXML
    public Label labelCurrentTesters;
    @FXML
    public Label labelParsed;
    @FXML
    public Label labelTested;
    @FXML
    public Label labelQueueParsers;
    @FXML
    public Label labelQueueTesters;

    @FXML
    public Button buttonExport;

    protected Coordinator coordinator;

    @FXML
    public void initialize() {
        sliderDesiredParsingThreads.valueProperty().addListener((ov, old_val, new_val) -> labelDesiredParsingThreads.setText(String.valueOf(new_val.intValue())));
        sliderDesiredTestingThreads.valueProperty().addListener((ov, old_val, new_val) -> labelDesiredTestingThreads.setText(String.valueOf(new_val.intValue())));

        tableColumnParent.setCellValueFactory(new PropertyValueFactory("parent"));
        tableColumnSelf.setCellValueFactory(new PropertyValueFactory("self"));
        tableColumnStatus.setCellValueFactory(new PropertyValueFactory("status"));
    }

    public void onToggleButtonRunAction(ActionEvent actionEvent) {
        ToggleButton toggleButton = (ToggleButton) actionEvent.getTarget();

        if(!toggleButton.isSelected()) {
            coordinator.stop();
            toggleButton.setText("Start");
            return;
        }
        try {
            coordinator = new CoordinatorBuilder()
                    .setParsingThreadsCount((int) sliderDesiredParsingThreads.getValue())
                    .setTestingThreadsCount((int) sliderDesiredTestingThreads.getValue())
                    .setStartUrl(textStartUrl.getText())
                    .setParsingFilter(textAreaFilterParsing.getText().split("\n"))
                    .setTestingFilter(textAreaFilterTesting.getText().split("\n"))
                    .build();
        } catch (URISyntaxException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("The provided start URL is invalid.");
            alert.show();
            return;
        }

        tableViewResults.setItems(coordinator.getResults());

        labelCurrentParsers.textProperty().bind(coordinator.parsingThreads.asString());
        labelCurrentTesters.textProperty().bind(coordinator.testingThreads.asString());
        labelQueueParsers.textProperty().bind(coordinator.parsingQueueCount.asString());
        labelQueueTesters.textProperty().bind(coordinator.testingQueueCount.asString());

        labelParsed.textProperty().bind(coordinator.parsed.asString());
        labelTested.textProperty().bind(coordinator.tested.asString());
        coordinator.start();
        toggleButton.setText("Stop");
    }

    public Coordinator getCoordinator() {
        return coordinator;
    }

    public void onButtonExportAction(ActionEvent actionEvent) {
        Writer writer = null;

        try {
            File file = new File("url.csv");
            writer = new BufferedWriter(new FileWriter(file));

            for (Result url : tableViewResults.getItems()) {
                String text = String.format("%s\t%s\t%s\n", url.getParent(), url.getSelf(), url.getStatus());
                writer.write(text);
            }

            writer.flush();
            writer.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {

        }
    }
}
