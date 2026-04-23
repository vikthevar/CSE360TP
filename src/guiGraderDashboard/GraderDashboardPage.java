package guiGraderDashboard;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

import java.util.*;

/**
 * <p> Title: GraderDashboardPage </p>
 *
 * <p> Description: JavaFX View for the Grader Dashboard. Provides instructors
 * with a centralized interface to monitor real-time activity trends,
 * identify low-participation threads, and access individual student profiles.
 *
 * Layout:
 *   - Top: Header bar with title and Back button
 *   - Left panel: Summary stat cards + Activity trend (bar chart)
 *   - Center-top: Low-participation threads table
 *   - Center-bottom: Student roster with search + profile drill-down
 * </p>
 */
public class GraderDashboardPage {

    // --- MVC ---
    private final GraderDashboardController controller;

    // --- Top-level layout ---
    private BorderPane root;
    private Scene scene;

    // --- Panels ---
    private VBox leftPanel;
    private VBox centerPanel;

    // --- Stat cards ---
    private Label totalPostsValue;
    private Label activeStudentsValue;
    private Label lowThreadsValue;

    // --- Activity chart ---
    private Pane chartPane;

    // --- Low participation table ---
    private TableView<String[]> threadsTable;

    // --- Student roster ---
    private TextField searchField;
    private TableView<String[]> studentTable;

    // --- Student profile panel (shown on row click) ---
    private VBox profilePanel;
    private Label profileName;
    private Label profileEmail;
    private Label profilePosts;
    private Label profileLastActive;
    private Label profileThreadsStarted;
    private VBox recentPostsList;

    // -------------------------------------------------------------------------

    public GraderDashboardPage() {
        controller = GraderDashboardController.getInstance();
        buildUI();
        loadData();
    }

    // -------------------------------------------------------------------------
    // UI Construction
    // -------------------------------------------------------------------------

    private void buildUI() {
        root = new BorderPane();
        root.getStyleClass().add("dashboard-root");

        root.setTop(buildHeader());

        // Main content: left panel + center panel side by side
        HBox mainContent = new HBox(16);
        mainContent.setPadding(new Insets(16));
        mainContent.getStyleClass().add("main-content");

        leftPanel = buildLeftPanel();
        centerPanel = buildCenterPanel();

        HBox.setHgrow(centerPanel, Priority.ALWAYS);
        mainContent.getChildren().addAll(leftPanel, centerPanel);
        root.setCenter(mainContent);
    }

    /** Top header bar */
    private HBox buildHeader() {
        HBox header = new HBox();
        header.getStyleClass().add("dashboard-header");
        header.setPadding(new Insets(14, 20, 14, 20));
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Grader Dashboard");
        title.getStyleClass().add("header-title");
        HBox.setHgrow(title, Priority.ALWAYS);

        Button backBtn = new Button("← Back to Admin Home");
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> goBack());

        header.getChildren().addAll(title, backBtn);
        return header;
    }

    /** Left panel: stat cards + activity bar chart */
    private VBox buildLeftPanel() {
        VBox panel = new VBox(16);
        panel.getStyleClass().add("left-panel");
        panel.setPrefWidth(260);
        panel.setMinWidth(220);

        Label statsLabel = new Label("OVERVIEW");
        statsLabel.getStyleClass().add("section-label");

        // Stat cards
        totalPostsValue    = new Label("—");
        activeStudentsValue = new Label("—");
        lowThreadsValue    = new Label("—");

        VBox cards = new VBox(10,
            buildStatCard("Posts This Week",    totalPostsValue,     "#4A90D9"),
            buildStatCard("Active Students",    activeStudentsValue, "#27AE60"),
            buildStatCard("Low-Activity Threads", lowThreadsValue,  "#E67E22")
        );

        // Activity trend chart
        Label chartLabel = new Label("ACTIVITY — LAST 14 DAYS");
        chartLabel.getStyleClass().add("section-label");
        chartPane = new Pane();
        chartPane.getStyleClass().add("chart-pane");
        chartPane.setPrefHeight(130);

        panel.getChildren().addAll(statsLabel, cards, chartLabel, chartPane);
        return panel;
    }

    private VBox buildStatCard(String title, Label valueLabel, String accentHex) {
        VBox card = new VBox(4);
        card.getStyleClass().add("stat-card");
        card.setPadding(new Insets(12));
        card.setStyle("-fx-border-color: " + accentHex + "; -fx-border-width: 0 0 0 4;");

        Label titleLbl = new Label(title);
        titleLbl.getStyleClass().add("stat-card-title");
        valueLabel.getStyleClass().add("stat-card-value");
        valueLabel.setStyle("-fx-text-fill: " + accentHex + ";");

        card.getChildren().addAll(titleLbl, valueLabel);
        return card;
    }

    /** Center panel: low-participation threads + student roster */
    private VBox buildCenterPanel() {
        VBox panel = new VBox(20);
        panel.getStyleClass().add("center-panel");

        panel.getChildren().addAll(
            buildThreadsSection(),
            buildStudentRosterSection()
        );
        return panel;
    }

    // --- Low-participation threads ---

    @SuppressWarnings("unchecked")
    private VBox buildThreadsSection() {
        VBox section = new VBox(8);

        Label heading = new Label("Low-Participation Threads  (< " +
                controller.getLowParticipationThreshold() + " replies)");
        heading.getStyleClass().add("section-heading");

        threadsTable = new TableView<>();
        threadsTable.getStyleClass().add("data-table");
        threadsTable.setPrefHeight(180);
        threadsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        threadsTable.setPlaceholder(new Label("No low-participation threads found."));

        TableColumn<String[], String> titleCol = arrayColumn("Thread Title", 1);
        TableColumn<String[], String> repliesCol = arrayColumn("Replies", 2);
        TableColumn<String[], String> dateCol = arrayColumn("Created", 3);
        repliesCol.setMaxWidth(80);
        repliesCol.setMinWidth(60);
        dateCol.setMaxWidth(140);

        threadsTable.getColumns().addAll(titleCol, repliesCol, dateCol);

        section.getChildren().addAll(heading, threadsTable);
        return section;
    }

    // --- Student roster ---

    @SuppressWarnings("unchecked")
    private VBox buildStudentRosterSection() {
        VBox section = new VBox(8);
        VBox.setVgrow(section, Priority.ALWAYS);

        // Header row with search
        HBox rosterHeader = new HBox(12);
        rosterHeader.setAlignment(Pos.CENTER_LEFT);

        Label heading = new Label("Student Roster");
        heading.getStyleClass().add("section-heading");
        HBox.setHgrow(heading, Priority.ALWAYS);

        searchField = new TextField();
        searchField.setPromptText("Search by name or email…");
        searchField.getStyleClass().add("search-field");
        searchField.setPrefWidth(220);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterStudents(newVal));

        rosterHeader.getChildren().addAll(heading, searchField);

        // Table
        studentTable = new TableView<>();
        studentTable.getStyleClass().add("data-table");
        studentTable.setPrefHeight(200);
        studentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        studentTable.setPlaceholder(new Label("No students found."));

        TableColumn<String[], String> nameCol     = arrayColumn("Name",        1);
        TableColumn<String[], String> emailCol    = arrayColumn("Email",       2);
        TableColumn<String[], String> postsCol    = arrayColumn("Posts",       3);
        TableColumn<String[], String> lastActCol  = arrayColumn("Last Active", 4);
        postsCol.setMaxWidth(70);

        studentTable.getColumns().addAll(nameCol, emailCol, postsCol, lastActCol);
        studentTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldRow, newRow) -> { if (newRow != null) openStudentProfile(newRow[0]); }
        );

        // Profile panel (hidden until a student is selected)
        profilePanel = buildProfilePanel();
        profilePanel.setVisible(false);
        profilePanel.setManaged(false);

        section.getChildren().addAll(rosterHeader, studentTable, profilePanel);
        return section;
    }

    /** Expandable profile panel shown below the roster table */
    private VBox buildProfilePanel() {
        VBox panel = new VBox(10);
        panel.getStyleClass().add("profile-panel");
        panel.setPadding(new Insets(14));

        HBox topRow = new HBox(16);
        topRow.setAlignment(Pos.CENTER_LEFT);

        profileName = new Label();
        profileName.getStyleClass().add("profile-name");

        Button closeBtn = new Button("✕");
        closeBtn.getStyleClass().add("close-button");
        closeBtn.setOnAction(e -> closeProfile());
        HBox.setHgrow(profileName, Priority.ALWAYS);
        topRow.getChildren().addAll(profileName, closeBtn);

        profileEmail          = new Label();
        profilePosts          = new Label();
        profileLastActive     = new Label();
        profileThreadsStarted = new Label();

        for (Label l : new Label[]{profileEmail, profilePosts, profileLastActive, profileThreadsStarted}) {
            l.getStyleClass().add("profile-detail");
        }

        Label recentLabel = new Label("Recent Posts");
        recentLabel.getStyleClass().add("section-label");
        recentPostsList = new VBox(6);
        ScrollPane recentScroll = new ScrollPane(recentPostsList);
        recentScroll.setFitToWidth(true);
        recentScroll.setPrefHeight(120);
        recentScroll.getStyleClass().add("recent-scroll");

        panel.getChildren().addAll(
            topRow,
            profileEmail, profilePosts, profileLastActive, profileThreadsStarted,
            recentLabel, recentScroll
        );
        return panel;
    }

    // -------------------------------------------------------------------------
    // Data Loading
    // -------------------------------------------------------------------------

    private void loadData() {
        loadStatCards();
        loadActivityChart();
        loadThreadsTable();
        loadStudentTable(null);
    }

    private void loadStatCards() {
        totalPostsValue.setText(String.valueOf(controller.getTotalPostsLastWeek()));
        activeStudentsValue.setText(String.valueOf(controller.getActiveStudentsLastWeek()));
        lowThreadsValue.setText(String.valueOf(controller.getLowParticipationThreads().size()));
    }

    /** Draws a simple bar chart directly on a Pane using JavaFX rectangles */
    private void loadActivityChart() {
        Map<String, Integer> trends = controller.getActivityTrends();
        chartPane.getChildren().clear();

        if (trends.isEmpty()) return;

        int maxVal = trends.values().stream().mapToInt(Integer::intValue).max().orElse(1);
        double paneW = 240;
        double paneH = 120;
        int n = trends.size();
        double barWidth = Math.max(4, (paneW - 8) / n - 3);
        double x = 4;

        for (Map.Entry<String, Integer> entry : trends.entrySet()) {
            double ratio = maxVal == 0 ? 0 : (double) entry.getValue() / maxVal;
            double barH = Math.max(2, ratio * (paneH - 20));
            double y = paneH - 16 - barH;

            javafx.scene.shape.Rectangle bar = new javafx.scene.shape.Rectangle(x, y, barWidth, barH);
            bar.setFill(Color.web("#4A90D9", 0.85));
            bar.setArcWidth(3);
            bar.setArcHeight(3);

            // Tooltip with date + count
            Tooltip tip = new Tooltip(entry.getKey() + ": " + entry.getValue() + " posts");
            Tooltip.install(bar, tip);

            chartPane.getChildren().add(bar);
            x += barWidth + 3;
        }
    }

    private void loadThreadsTable() {
        List<String[]> threads = controller.getLowParticipationThreads();
        threadsTable.getItems().setAll(threads);
    }

    private void loadStudentTable(String query) {
        List<String[]> students = (query == null || query.isBlank())
            ? controller.getAllStudentSummaries()
            : controller.searchStudents(query);
        studentTable.getItems().setAll(students);
    }

    // -------------------------------------------------------------------------
    // Interactions
    // -------------------------------------------------------------------------

    private void filterStudents(String query) {
        loadStudentTable(query);
        closeProfile();
    }

    private void openStudentProfile(String userId) {
        String[] profile = controller.getStudentProfile(userId);
        if (profile == null) return;

        profileName.setText(profile[1]);
        profileEmail.setText("✉  " + profile[2]);
        profilePosts.setText("💬  Total posts: " + profile[3]);
        profileLastActive.setText("⏱  Last active: " + profile[4]);
        profileThreadsStarted.setText("📌  Threads started: " + profile[5]);

        // Recent posts
        recentPostsList.getChildren().clear();
        List<String[]> posts = controller.getStudentRecentPosts(userId);
        if (posts.isEmpty()) {
            recentPostsList.getChildren().add(new Label("No posts yet."));
        } else {
            for (String[] post : posts) {
                VBox postCard = new VBox(2);
                postCard.getStyleClass().add("post-card");
                postCard.setPadding(new Insets(6, 8, 6, 8));

                Label threadTitle = new Label("In: " + post[1]);
                threadTitle.getStyleClass().add("post-thread-title");
                Label content = new Label(truncate(post[2], 120));
                content.getStyleClass().add("post-content");
                content.setWrapText(true);
                Label date = new Label(post[3]);
                date.getStyleClass().add("post-date");

                postCard.getChildren().addAll(threadTitle, content, date);
                recentPostsList.getChildren().add(postCard);
            }
        }

        profilePanel.setVisible(true);
        profilePanel.setManaged(true);
    }

    private void closeProfile() {
        profilePanel.setVisible(false);
        profilePanel.setManaged(false);
        studentTable.getSelectionModel().clearSelection();
    }

    private void goBack() {
        // Navigate back to admin home — adjust to match your project's navigation pattern.
        // Example if your admin home uses a singleton show() method:
        // guiAdminHome.AdminHomePage.getInstance().show();
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close(); // Replace with your project's actual back-navigation call
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Creates a TableColumn that reads from a String[] by index.
     * Used because TableView items are raw String arrays (no model class needed).
     */
    private TableColumn<String[], String> arrayColumn(String header, int index) {
        TableColumn<String[], String> col = new TableColumn<>(header);
        col.setCellValueFactory(data -> {
            String[] row = data.getValue();
            String val = (row != null && index < row.length) ? row[index] : "";
            return new javafx.beans.property.SimpleStringProperty(val);
        });
        return col;
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max) + "…";
    }

    // -------------------------------------------------------------------------
    // Public API — called by AdminHome to show the dashboard
    // -------------------------------------------------------------------------

    /**
     * Shows the Grader Dashboard in the provided Stage (or a new one).
     * Call this from your AdminHome button handler.
     */
    public void show(Stage stage) {
        if (scene == null) {
            scene = new Scene(root, 1100, 720);
            // Link to your project's existing CSS file
            scene.getStylesheets().add(
                getClass().getResource("/applicationMain/application.css").toExternalForm()
            );
        }
        stage.setScene(scene);
        stage.setTitle("Grader Dashboard");
        stage.show();

        // Refresh data each time the dashboard is opened
        loadData();
    }

    /**
     * Convenience overload — opens the dashboard in a new Stage.
     */
    public void show() {
        show(new Stage());
    }
    
    private static GraderDashboardPage theView = null;

    public static void displayGraderDashboard(Stage ps, entityClasses.User user) {
        if (theView == null) {
            theView = new GraderDashboardPage();
        }

        theView.show(ps);
    }
}
