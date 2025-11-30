package simulator;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class SimulatorGUI extends Application {
    private TextField fileNameField;
    private Button runButton;
    private Button stepButton;
    private Button runAllButton;
    private Label cycleLabel;
    private TextArea logArea;
    private TableView<RSDisplay> rsTableView;
    private TableView<RegDisplay> regTableView;
    private Label cacheLabel;
    private Label instructionQueueLabel;
    private ObservableList<RSDisplay> rsData;
    private ObservableList<RegDisplay> regData;
    private SteppableSimulator steppableSimulator;
    private boolean simulationLoaded = false;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Tomasulo Algorithm Simulator - Advanced Visualization");
        primaryStage.setWidth(1400);
        primaryStage.setHeight(900);

        BorderPane root = new BorderPane();

        // Top: Controls
        root.setTop(createControlPanel());

        // Center: Split pane for tables and logs
        SplitPane centerSplit = new SplitPane();
        centerSplit.setPrefHeight(600);
        centerSplit.setDividerPositions(0.6);

        // Left: Visualization
        centerSplit.getItems().add(createVisualizationPanel());

        // Right: Log
        centerSplit.getItems().add(createLogPanel());

        root.setCenter(centerSplit);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createControlPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(15));
        panel.setStyle("-fx-border-color: #cccccc; -fx-border-width: 0 0 1 0; -fx-background-color: #f5f5f5;");

        HBox controlBox = new HBox(10);
        controlBox.setStyle("-fx-alignment: center-left;");

        Label fileLabel = new Label("Instruction File:");
        fileLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");

        fileNameField = new TextField();
        fileNameField.setText("instructions.txt");
        fileNameField.setPrefWidth(300);

        runButton = new Button("📂 Load File");
        runButton.setStyle("-fx-font-size: 12; -fx-padding: 10 25; -fx-font-weight: bold;");
        runButton.setOnAction(e -> loadSimulation());

        stepButton = new Button("⏭ Step Cycle");
        stepButton.setStyle("-fx-font-size: 12; -fx-padding: 10 25; -fx-font-weight: bold;");
        stepButton.setDisable(true);
        stepButton.setOnAction(e -> stepCycle());

        runAllButton = new Button("▶ Run All");
        runAllButton.setStyle("-fx-font-size: 12; -fx-padding: 10 25; -fx-font-weight: bold;");
        runAllButton.setDisable(true);
        runAllButton.setOnAction(e -> runAllCycles());

        Button clearButton = new Button("🔄 Clear");
        clearButton.setStyle("-fx-font-size: 12; -fx-padding: 10 20;");
        clearButton.setOnAction(e -> clearDisplay());

        cycleLabel = new Label("Cycle: 0");
        cycleLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #0066cc;");

        controlBox.getChildren().addAll(fileLabel, fileNameField, runButton, stepButton, runAllButton, clearButton,
                new Separator(javafx.geometry.Orientation.VERTICAL), cycleLabel);

        panel.getChildren().add(controlBox);
        return panel;
    }

    private VBox createVisualizationPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-border-color: #e0e0e0;");

        // Instruction Queue
        VBox queueBox = createTitledBox("Instruction Queue");
        instructionQueueLabel = new Label("Empty");
        instructionQueueLabel.setWrapText(true);
        instructionQueueLabel.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10;");
        queueBox.getChildren().add(instructionQueueLabel);

        // Reservation Stations Table
        VBox rsBox = createTitledBox("Reservation Stations");
        rsTableView = createRSTable();
        rsBox.getChildren().add(rsTableView);

        // Register File Table
        VBox regBox = createTitledBox("Register File (Floating Point)");
        regTableView = createRegTable();
        regBox.getChildren().add(regTableView);

        // Cache Info
        VBox cacheBox = createTitledBox("Cache & Memory");
        cacheLabel = new Label("Cache: 1024 bytes, Block: 4 bytes");
        cacheLabel.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11;");
        cacheBox.getChildren().add(cacheLabel);

        ScrollPane sp = new ScrollPane(new VBox(10, queueBox, rsBox, regBox, cacheBox));
        sp.setFitToWidth(true);

        panel.getChildren().add(sp);
        return panel;
    }

    private VBox createLogPanel() {
        VBox panel = new VBox(5);
        panel.setPadding(new Insets(10));

        Label title = new Label("Simulation Log");
        title.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10;");
        logArea.setPrefRowCount(30);

        VBox.setVgrow(logArea, Priority.ALWAYS);
        panel.getChildren().addAll(title, logArea);

        return panel;
    }

    private VBox createTitledBox(String title) {
        VBox box = new VBox(5);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-color: #fafafa;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 11; -fx-font-weight: bold; -fx-text-fill: #0066cc;");

        Separator sep = new Separator();
        box.getChildren().addAll(titleLabel, sep);

        return box;
    }

    private TableView<RSDisplay> createRSTable() {
        TableView<RSDisplay> table = new TableView<>();
        table.setPrefHeight(150);
        rsData = FXCollections.observableArrayList();
        table.setItems(rsData);

        TableColumn<RSDisplay, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().name));
        nameCol.setPrefWidth(60);

        TableColumn<RSDisplay, String> busyCol = new TableColumn<>("Busy");
        busyCol.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().busy));
        busyCol.setPrefWidth(40);

        TableColumn<RSDisplay, String> opCol = new TableColumn<>("Op");
        opCol.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().op));
        opCol.setPrefWidth(60);

        TableColumn<RSDisplay, String> destCol = new TableColumn<>("Dest");
        destCol.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().dest));
        destCol.setPrefWidth(50);

        TableColumn<RSDisplay, String> vjCol = new TableColumn<>("Vj");
        vjCol.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().vj));
        vjCol.setPrefWidth(50);

        TableColumn<RSDisplay, String> vkCol = new TableColumn<>("Vk");
        vkCol.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().vk));
        vkCol.setPrefWidth(50);

        TableColumn<RSDisplay, String> qjCol = new TableColumn<>("Qj");
        qjCol.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().qj));
        qjCol.setPrefWidth(60);

        TableColumn<RSDisplay, String> qkCol = new TableColumn<>("Qk");
        qkCol.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().qk));
        qkCol.setPrefWidth(60);

        table.getColumns().addAll(nameCol, busyCol, opCol, destCol, vjCol, vkCol, qjCol, qkCol);
        return table;
    }

    private TableView<RegDisplay> createRegTable() {
        TableView<RegDisplay> table = new TableView<>();
        table.setPrefHeight(120);
        regData = FXCollections.observableArrayList();
        table.setItems(regData);

        TableColumn<RegDisplay, String> nameCol = new TableColumn<>("Register");
        nameCol.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().name));
        nameCol.setPrefWidth(80);

        TableColumn<RegDisplay, String> valueCol = new TableColumn<>("Value");
        valueCol.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().value));
        valueCol.setPrefWidth(150);

        table.getColumns().addAll(nameCol, valueCol);
        return table;
    }

    private void clearDisplay() {
        logArea.clear();
        cycleLabel.setText("Cycle: 0");
        rsData.clear();
        regData.clear();
        instructionQueueLabel.setText("Empty");
        steppableSimulator = null;
        simulationLoaded = false;
        stepButton.setDisable(true);
        runAllButton.setDisable(true);
        runButton.setDisable(false);
        fileNameField.setDisable(false);
    }

    private void loadSimulation() {
        clearDisplay();
        String filename = fileNameField.getText().trim();

        if (filename.isEmpty()) {
            appendLog("❌ Error: Please enter a filename.\n");
            return;
        }

        try {
            appendLog("📂 Loading from: " + filename + "\n");

            steppableSimulator = new SteppableSimulator();
            steppableSimulator.loadInstructions(filename);
            steppableSimulator.initialize();
            simulationLoaded = true;

            int instrCount = steppableSimulator.getInstructionQueue().size();
            appendLog("✓ Loaded " + instrCount + " instructions\n");
            appendLog("✓ Ready to step through simulation\n\n");

            // Enable step buttons
            stepButton.setDisable(false);
            runAllButton.setDisable(false);
            fileNameField.setDisable(true);
            runButton.setDisable(true);

            updateTablesFromSimulator();

        } catch (Exception e) {
            appendLog("❌ Error: " + e.getMessage() + "\n");
            simulationLoaded = false;
        }
    }

    private void stepCycle() {
        if (!simulationLoaded || steppableSimulator == null) {
            appendLog("❌ Error: No simulation loaded\n");
            return;
        }

        try {
            steppableSimulator.stepCycle();
            updateTablesFromSimulator();

            if (steppableSimulator.isFinished()) {
                appendLog("\n✓ Simulation finished at cycle " + steppableSimulator.getCycle() + "\n");
                stepButton.setDisable(true);
                runAllButton.setDisable(true);
            }
        } catch (Exception e) {
            appendLog("❌ Error: " + e.getMessage() + "\n");
        }
    }

    private void runAllCycles() {
        if (!simulationLoaded || steppableSimulator == null) {
            appendLog("❌ Error: No simulation loaded\n");
            return;
        }

        stepButton.setDisable(true);
        runAllButton.setDisable(true);

        Thread runThread = new Thread(() -> {
            try {
                int startCycle = steppableSimulator.getCycle();
                while (!steppableSimulator.isFinished()) {
                    steppableSimulator.stepCycle();
                    Thread.sleep(50); // Small delay to show progress
                }

                javafx.application.Platform.runLater(() -> {
                    updateTablesFromSimulator();
                    appendLog("\n✓ Simulation finished at cycle " + steppableSimulator.getCycle() + "\n");
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> appendLog("❌ Error: " + e.getMessage() + "\n"));
            }
        });
        runThread.setDaemon(true);
        runThread.start();
    }

    private void updateTablesFromSimulator() {
        if (steppableSimulator == null)
            return;

        javafx.application.Platform.runLater(() -> {
            // Update cycle label
            cycleLabel.setText(String.format("Cycle: %d", steppableSimulator.getCycle()));

            // Update RS table
            rsData.clear();
            List<ReservationStation> stations = steppableSimulator.getReservationStations();
            for (ReservationStation rs : stations) {
                rsData.add(new RSDisplay(
                        rs.name,
                        rs.busy ? "Yes" : "No",
                        rs.op != null ? rs.op : "-",
                        rs.dest != null ? rs.dest : "-",
                        rs.vj != null ? String.format("%.2f", rs.vj) : "-",
                        rs.vk != null ? String.format("%.2f", rs.vk) : "-",
                        rs.qj != null ? rs.qj : "-",
                        rs.qk != null ? rs.qk : "-"));
            }

            // Update Register File table
            regData.clear();
            RegisterFile rf = steppableSimulator.getRegisterFile();
            if (rf != null) {
                for (int i = 0; i < 32; i++) {
                    String regName = "F" + i;
                    Object val = rf.getValue(regName);
                    if (val != null) {
                        double dval = ((Number) val).doubleValue();
                        if (dval != 0.0) {
                            regData.add(new RegDisplay(regName, String.format("%.2f", dval)));
                        }
                    }
                }
            }

            // Update instruction queue
            List<Instruction> queue = steppableSimulator.getInstructionQueue();
            StringBuilder queueStr = new StringBuilder();
            if (queue.isEmpty()) {
                queueStr.append("Empty (all instructions executed)");
            } else {
                for (Instruction instr : queue) {
                    queueStr.append(instr.toString()).append("\n");
                }
            }
            instructionQueueLabel.setText(queueStr.toString());
        });
    }

    public void appendLog(String text) {
        javafx.application.Platform.runLater(() -> logArea.appendText(text));
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Helper classes for table display
    public static class RSDisplay {
        public String name, busy, op, dest, vj, vk, qj, qk;

        public RSDisplay(String name, String busy, String op, String dest, String vj, String vk, String qj, String qk) {
            this.name = name;
            this.busy = busy;
            this.op = op;
            this.dest = dest;
            this.vj = vj;
            this.vk = vk;
            this.qj = qj;
            this.qk = qk;
        }
    }

    public static class RegDisplay {
        public String name, value;

        public RegDisplay(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }
}
