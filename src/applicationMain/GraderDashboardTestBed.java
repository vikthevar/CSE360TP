package applicationMain;

import guiGraderDashboard.GraderDashboardController;
import guiGraderDashboard.GraderDashboardPage;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

/**
 * Title: GraderDashboardTestBed Class.
 *
 * Description:
 * This class provides a semi-automated test bed for the Grader Dashboard
 * implementation. It validates that the controller and sample data store
 * return expected dashboard data and then launches the JavaFX dashboard
 * interface for visual inspection.
 *
 * This test bed is intended to support TP3 documentation, manual testing,
 * and screencast evidence for the Grader Dashboard feature.
 *
 * Functional Areas Covered:
 * - activity trends
 * - weekly summary statistics
 * - low-participation threads
 * - student roster retrieval
 * - student search filtering
 * - individual student profile lookup
 * - recent post retrieval
 * - dashboard UI launch
 *
 * Testing:
 * This is a semi-automated test. Console output is used to verify data
 * retrieval behavior, and the JavaFX window is used to verify visual
 * rendering and interaction behavior.
 *
 * @author Vikram Thevar
 */
public class GraderDashboardTestBed extends Application {

    /** Controller under test. */
    private static final GraderDashboardController controller =
            GraderDashboardController.getInstance();

    /**
     * Executes the JavaFX portion of the test bed.
     *
     * @param stage the primary JavaFX stage
     */
    @Override
    public void start(Stage stage) {
        System.out.println();
        System.out.println("--------------------------------------------------");
        System.out.println("Launching Grader Dashboard UI...");
        System.out.println("Visually verify:");
        System.out.println("1. Header appears correctly");
        System.out.println("2. Stat cards are populated");
        System.out.println("3. Activity bars render");
        System.out.println("4. Low-participation threads table is populated");
        System.out.println("5. Student roster table is populated");
        System.out.println("6. Search field filters students");
        System.out.println("7. Selecting a student opens the profile panel");
        System.out.println("--------------------------------------------------");
        System.out.println();

        GraderDashboardPage page = new GraderDashboardPage();
        page.show(stage);
    }

    /**
     * Main entry point for the test bed.
     *
     * Description:
     * Runs console-based validation checks first, then launches the JavaFX
     * dashboard window for manual inspection.
     *
     * @param args command-line arguments, not used directly
     */
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("GRADER DASHBOARD TEST BED");
        System.out.println("==================================================");

        testActivityTrends();
        testWeeklySummaryStats();
        testLowParticipationThreads();
        testStudentSummaries();
        testStudentSearch();
        testStudentProfile();
        testStudentRecentPosts();

        System.out.println("==================================================");
        System.out.println("Console validation complete.");
        System.out.println("Proceeding to UI launch.");
        System.out.println("==================================================");

        launch(args);
    }

    /**
     * Validates retrieval of activity trend data.
     */
    private static void testActivityTrends() {
        System.out.println();
        System.out.println("[GD-01] Activity Trends");

        Map<String, Integer> trends = controller.getActivityTrends();

        if (trends == null || trends.isEmpty()) {
            System.out.println("FAIL: No activity trend data returned.");
            return;
        }

        System.out.println("PASS: Activity trends returned successfully.");
        System.out.println("Trend count: " + trends.size());

        for (Map.Entry<String, Integer> entry : trends.entrySet()) {
            System.out.println("  " + entry.getKey() + " -> " + entry.getValue());
        }
    }

    /**
     * Validates retrieval of weekly summary statistic values.
     */
    private static void testWeeklySummaryStats() {
        System.out.println();
        System.out.println("[GD-02] Weekly Summary Statistics");

        int totalPosts = controller.getTotalPostsLastWeek();
        int activeStudents = controller.getActiveStudentsLastWeek();

        System.out.println("Total posts last week: " + totalPosts);
        System.out.println("Active students last week: " + activeStudents);

        if (totalPosts >= 0 && activeStudents >= 0) {
            System.out.println("PASS: Weekly summary statistics returned successfully.");
        } else {
            System.out.println("FAIL: Invalid weekly summary statistics.");
        }
    }

    /**
     * Validates retrieval of low-participation thread data.
     */
    private static void testLowParticipationThreads() {
        System.out.println();
        System.out.println("[GD-03] Low-Participation Threads");

        List<String[]> threads = controller.getLowParticipationThreads();

        if (threads == null) {
            System.out.println("FAIL: Low-participation thread list is null.");
            return;
        }

        System.out.println("PASS: Low-participation threads returned successfully.");
        System.out.println("Thread count: " + threads.size());

        for (String[] thread : threads) {
            System.out.println("  Thread ID: " + thread[0]
                    + ", Title: " + thread[1]
                    + ", Replies: " + thread[2]
                    + ", Created: " + thread[3]);
        }
    }

    /**
     * Validates retrieval of student roster summary data.
     */
    private static void testStudentSummaries() {
        System.out.println();
        System.out.println("[GD-04] Student Summaries");

        List<String[]> students = controller.getAllStudentSummaries();

        if (students == null || students.isEmpty()) {
            System.out.println("FAIL: No student summary data returned.");
            return;
        }

        System.out.println("PASS: Student summaries returned successfully.");
        System.out.println("Student count: " + students.size());

        for (String[] student : students) {
            System.out.println("  ID: " + student[0]
                    + ", Name: " + student[1]
                    + ", Email: " + student[2]
                    + ", Posts: " + student[3]
                    + ", Last Active: " + student[4]);
        }
    }

    /**
     * Validates student search/filter behavior.
     */
    private static void testStudentSearch() {
        System.out.println();
        System.out.println("[GD-05] Student Search");

        List<String[]> result = controller.searchStudents("alice");

        if (result == null) {
            System.out.println("FAIL: Search result is null.");
            return;
        }

        System.out.println("Search query: alice");
        System.out.println("Result count: " + result.size());

        if (!result.isEmpty()) {
            System.out.println("PASS: Search returned matching student data.");
            for (String[] student : result) {
                System.out.println("  " + student[1] + " - " + student[2]);
            }
        } else {
            System.out.println("FAIL: Search returned no results for expected query.");
        }
    }

    /**
     * Validates retrieval of a single student profile.
     */
    private static void testStudentProfile() {
        System.out.println();
        System.out.println("[GD-06] Student Profile");

        String[] profile = controller.getStudentProfile("S001");

        if (profile == null) {
            System.out.println("FAIL: Student profile not found.");
            return;
        }

        System.out.println("PASS: Student profile returned successfully.");
        System.out.println("  ID: " + profile[0]);
        System.out.println("  Name: " + profile[1]);
        System.out.println("  Email: " + profile[2]);
        System.out.println("  Total Posts: " + profile[3]);
        System.out.println("  Last Active: " + profile[4]);
        System.out.println("  Threads Started: " + profile[5]);
    }

    /**
     * Validates retrieval of recent posts for a student.
     */
    private static void testStudentRecentPosts() {
        System.out.println();
        System.out.println("[GD-07] Student Recent Posts");

        List<String[]> posts = controller.getStudentRecentPosts("S001");

        if (posts == null) {
            System.out.println("FAIL: Recent posts list is null.");
            return;
        }

        System.out.println("PASS: Recent posts returned successfully.");
        System.out.println("Recent post count: " + posts.size());

        for (String[] post : posts) {
            System.out.println("  Post ID: " + post[0]
                    + ", Thread: " + post[1]
                    + ", Content: " + post[2]
                    + ", Created: " + post[3]);
        }
    }
}