package guiGraderDashboard;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

import java.util.*;

/**
 * Title: GraderDashboardPage
 *
 * Description:
 * JavaFX view for the Grader Dashboard. Provides instructional staff with a
 * centralized interface for reviewing discussion activity trends, identifying
 * low-participation threads, and viewing individual student profiles.
 *
 * Layout:
 * The page is organized into a header, a left-side analytics panel, and a
 * center content area. The left panel displays summary statistics and an
 * activity trend chart. The center panel displays low-participation threads
 * and a searchable student roster with profile drill-down.
 *
 * Responsibilities:
 * Construct and display the dashboard user interface.
 * Load data from the controller into charts, tables, and profile panels.
 * Handle user interactions such as searching, student selection, profile
 * expansion, and return navigation.
 *
 * MVC Role:
 * This class serves as the View in the MVC architecture. It retrieves all
 * dashboard data through the GraderDashboardController and does not directly
 * manage underlying data storage.
 *
 * Testing:
 * Validated through manual dashboard execution, UI interaction, student
 * search/filter testing, and profile display verification.
 *
 * @author Diego Armenta
 */
public class GraderDashboardPage {

    /** Controller used to retrieve dashboard data. */
    private final GraderDashboardController controller;

    /** Root layout for the dashboard scene. */
    private BorderPane root;

    /** JavaFX scene used to display the dashboard. */
    private Scene scene;

    /** Left-side panel containing stat cards and trend chart. */
    private VBox leftPanel;

    /** Center panel containing tables and student profile area. */
    private VBox centerPanel;

    /** Label showing the total posts value. */
    private Label totalPostsValue;

    /** Label showing the active students value. */
    private Label activeStudentsValue;

    /** Label showing the low-participation threads value. */
    private Label lowThreadsValue;

    /** Pane used to draw the activity chart. */
    private Pane chartPane;

    /** Table showing low-participation thread data. */
    private TableView<String[]> threadsTable;

    /** Search field used to filter the student roster. */
    private TextField searchField;

    /** Table showing student summary data. */
    private TableView<String[]> studentTable;

    /** Profile panel shown when a student is selected. */
    private VBox profilePanel;

    /** Label showing the selected student's name. */
    private Label profileName;

    /** Label showing the selected student's email. */
    private Label profileEmail;

    /** Label showing the selected student's post count. */
    private Label profilePosts;

    /** Label showing the selected student's last active date. */
    private Label profileLastActive;

    /** Label showing the selected student's thread count. */
    private Label profileThreadsStarted;

    /** Container displaying recent posts for the selected student. */
    private VBox recentPostsList;

    /**
     * Constructs the Grader Dashboard page and initializes the user interface.
     */
    public GraderDashboardPage() {
        controller = GraderDashboardController.getInstance();
        buildUI();
        loadData();
    }

    /**
     * Builds the complete user interface layout for the dashboard.
     *
     * Description:
     * Creates the root layout, header, left analytics panel, and center
     * content panel, then assembles them into the main scene structure.
     */
    private void buildUI() {
        root = new BorderPane();
        root.getStyleClass().add("dashboard-root");

        root.setTop(buildHeader());

        HBox mainContent = new HBox(16);
        mainContent.setPadding(new Insets(16));
        mainContent.getStyleClass().add("main-content");

        leftPanel = buildLeftPanel();
        centerPanel = buildCenterPanel();

        HBox.setHgrow(centerPanel, Priority.ALWAYS);
        mainContent.getChildren().addAll(leftPanel, centerPanel);
        root.setCenter(mainContent);
    }

    /**
     * Builds the dashboard header bar.
     *
     * @return a configured header HBox
     */
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

    /**
     * Builds the left-side analytics panel.
     *
     * Description:
     * Creates summary statistic cards and the activity trend chart container.
     *
     * @return a configured VBox representing the left panel
     */
    private VBox buildLeftPanel() {
        VBox panel = new VBox(16);
        panel.getStyleClass().add("left-panel");
        panel.setPrefWidth(260);
        panel.setMinWidth(220);

        Label statsLabel = new Label("OVERVIEW");
        statsLabel.getStyleClass().add("section-label");

        totalPostsValue = new Label("—");
        activeStudentsValue = new Label("—");
        lowThreadsValue = new Label("—");

        VBox cards = new VBox(10,
            buildStatCard("Posts This Week", totalPostsValue, "#4A90D9"),
            buildStatCard("Active Students", activeStudentsValue, "#27AE60"),
            buildStatCard("Low-Activity Threads", lowThreadsValue, "#E67E22")
        );

        Label chartLabel = new Label("ACTIVITY — LAST 14 DAYS");
        chartLabel.getStyleClass().add("section-label");

        chartPane = new Pane();
        chartPane.getStyleClass().add("chart-pane");
        chartPane.setPrefHeight(130);

        panel.getChildren().addAll(statsLabel, cards, chartLabel, chartPane);
        return panel;
    }

    /**
     * Builds a summary statistic card.
     *
     * @param title the title displayed on the card
     * @param valueLabel the label used to display the current value
     * @param accentHex the accent color applied to the card
     * @return a configured VBox representing a stat card
     */
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

    /**
     * Builds the center content panel.
     *
     * Description:
     * Creates the low-participation threads section and the student roster
     * section, then combines them into a single vertical panel.
     *
     * @return a configured VBox representing the center panel
     */
    private VBox buildCenterPanel() {
        VBox panel = new VBox(20);
        panel.getStyleClass().add("center-panel");

        panel.getChildren().addAll(
            buildThreadsSection(),
            buildStudentRosterSection()
        );
        return panel;
    }

    /**
     * Builds the low-participation threads section.
     *
     * @return a configured VBox containing the threads table
     */
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

    /**
     * Builds the student roster section.
     *
     * Description:
     * Creates the roster header, search field, student summary table, and
     * expandable student profile panel.
     *
     * @return a configured VBox containing roster components
     */
    @SuppressWarnings("unchecked")
    private VBox buildStudentRosterSection() {
        VBox section = new VBox(8);
        VBox.setVgrow(section, Priority.ALWAYS);

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

        studentTable = new TableView<>();
        studentTable.getStyleClass().add("data-table");
        studentTable.setPrefHeight(200);
        studentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        studentTable.setPlaceholder(new Label("No students found."));

        TableColumn<String[], String> nameCol = arrayColumn("Name", 1);
        TableColumn<String[], String> emailCol = arrayColumn("Email", 2);
        TableColumn<String[], String> postsCol = arrayColumn("Posts", 3);
        TableColumn<String[], String> lastActCol = arrayColumn("Last Active", 4);
        postsCol.setMaxWidth(70);

        studentTable.getColumns().addAll(nameCol, emailCol, postsCol, lastActCol);
        studentTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldRow, newRow) -> { if (newRow != null) openStudentProfile(newRow[0]); }
        );

        profilePanel = buildProfilePanel();
        profilePanel.setVisible(false);
        profilePanel.setManaged(false);

        section.getChildren().addAll(rosterHeader, studentTable, profilePanel);
        return section;
    }

    /**
     * Builds the expandable student profile panel.
     *
     * @return a configured VBox representing the profile panel
     */
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

        profileEmail = new Label();
        profilePosts = new Label();
        profileLastActive = new Label();
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

    /**
     * Loads all dashboard data into the visible interface.
     */
    private void loadData() {
        loadStatCards();
        loadActivityChart();
        loadThreadsTable();
        loadStudentTable(null);
    }

    /**
     * Loads summary statistic values into the overview cards.
     */
    private void loadStatCards() {
        totalPostsValue.setText(String.valueOf(controller.getTotalPostsLastWeek()));
        activeStudentsValue.setText(String.valueOf(controller.getActiveStudentsLastWeek()));
        lowThreadsValue.setText(String.valueOf(controller.getLowParticipationThreads().size()));
    }

    /**
     * Draws the activity trend chart using JavaFX rectangles.
     *
     * Description:
     * Reads trend data from the controller and renders a simple bar chart inside
     * the chart pane. Each bar includes a tooltip showing the date and post count.
     */
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

            Tooltip tip = new Tooltip(entry.getKey() + ": " + entry.getValue() + " posts");
            Tooltip.install(bar, tip);

            chartPane.getChildren().add(bar);
            x += barWidth + 3;
        }
    }

    /**
     * Loads low-participation thread data into the threads table.
     */
    private void loadThreadsTable() {
        List<String[]> threads = controller.getLowParticipationThreads();
        threadsTable.getItems().setAll(threads);
    }

    /**
     * Loads student summary data into the roster table.
     *
     * @param query optional search text used to filter students
     */
    private void loadStudentTable(String query) {
        List<String[]> students = (query == null || query.isBlank())
            ? controller.getAllStudentSummaries()
            : controller.searchStudents(query);
        studentTable.getItems().setAll(students);
    }

    /**
     * Filters the student table based on a search query.
     *
     * @param query the current search text
     */
    private void filterStudents(String query) {
        loadStudentTable(query);
        closeProfile();
    }

    /**
     * Opens and populates the student profile panel for the selected student.
     *
     * @param userId unique identifier of the selected student
     */
    private void openStudentProfile(String userId) {
        String[] profile = controller.getStudentProfile(userId);
        if (profile == null) return;

        profileName.setText(profile[1]);
        profileEmail.setText("✉  " + profile[2]);
        profilePosts.setText("💬  Total posts: " + profile[3]);
        profileLastActive.setText("⏱  Last active: " + profile[4]);
        profileThreadsStarted.setText("📌  Threads started: " + profile[5]);

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

    /**
     * Closes the student profile panel and clears the current table selection.
     */
    private void closeProfile() {
        profilePanel.setVisible(false);
        profilePanel.setManaged(false);
        studentTable.getSelectionModel().clearSelection();
    }

    /**
     * Navigates back to the previous administrator page.
     *
     * Description:
     * This implementation currently closes the current window. It should be
     * replaced with the project's final back-navigation behavior if needed.
     */
    private void goBack() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

    /**
     * Creates a table column that reads a value from a String array by index.
     *
     * @param header the displayed column header
     * @param index the String[] index used for the column value
     * @return a configured table column
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

    /**
     * Truncates a string to a maximum display length.
     *
     * @param s the input string
     * @param max the maximum length allowed before truncation
     * @return the original string if short enough, otherwise a truncated version
     */
    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max) + "…";
    }

    /**
     * Displays the Grader Dashboard in the provided stage.
     *
     * @param stage the stage used to display the dashboard
     */
    public void show(Stage stage) {
        if (scene == null) {
            scene = new Scene(root, 1100, 720);
            scene.getStylesheets().add(
                getClass().getResource("/applicationMain/application.css").toExternalForm()
            );
        }
        stage.setScene(scene);
        stage.setTitle("Grader Dashboard");
        stage.show();

        loadData();
    }

    /**
     * Displays the Grader Dashboard in a newly created stage.
     */
    public void show() {
        show(new Stage());
    }

    /** Shared singleton-style view instance used by displayGraderDashboard. */
    private static GraderDashboardPage theView = null;

    /**
     * Displays the Grader Dashboard using the shared page instance.
     *
     * @param ps the stage used to display the dashboard
     * @param user the currently logged-in user
     */
    public static void displayGraderDashboard(Stage ps, entityClasses.User user) {
        if (theView == null) {
            theView = new GraderDashboardPage();
        }

        theView.show(ps);
    }
}