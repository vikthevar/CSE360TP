package guiGraderDashboard;

import java.util.*;

/**
 * Title: GraderDashboardController
 *
 * Description:
 * MVC Controller for the Grader Dashboard. Acts as the intermediary between
 * the GraderDashboardPage (View) and GraderDashboardDataStore (Model).
 * Implements the Singleton pattern to ensure a single shared controller instance.
 *
 * Responsibilities:
 * - Retrieve activity analytics (trends, weekly stats)
 * - Identify low-participation threads
 * - Provide student roster and profile data
 * - Support search/filter functionality for student data
 *
 * Data Source:
 * All data is retrieved from GraderDashboardDataStore.
 *
 * Testing:
 * - Validated through dashboard UI interaction tests
 * - Verified via manual tests for filtering, trends, and student lookups
 *
 * @author Diego Armenta
 */
public class GraderDashboardController {

    /** Singleton instance of the controller */
    private static GraderDashboardController instance = null;

    /** Reference to the data store (Model layer) */
    private final GraderDashboardDataStore dataStore;

    /** Threshold for identifying low-participation threads */
    private static final int LOW_PARTICIPATION_THRESHOLD = 3;

    /** Maximum number of recent posts shown per student */
    private static final int RECENT_POSTS_LIMIT = 10;

    /** Number of days used for activity trend calculations */
    private static final int TREND_DAYS = 14;

    /**
     * Private constructor to enforce Singleton pattern.
     */
    private GraderDashboardController() {
        dataStore = GraderDashboardDataStore.getInstance();
    }

    /**
     * Returns the single instance of the controller.
     *
     * @return GraderDashboardController instance
     */
    public static GraderDashboardController getInstance() {
        if (instance == null) {
            instance = new GraderDashboardController();
        }
        return instance;
    }

    // -------------------------------------------------------------------------
    // Activity Trends
    // -------------------------------------------------------------------------

    /**
     * Retrieves activity trends over the last N days.
     *
     * @return Map where key = date string and value = post count
     */
    public Map<String, Integer> getActivityTrends() {
        return dataStore.getActivityTrends(TREND_DAYS);
    }

    /**
     * Retrieves total number of posts made in the last 7 days.
     *
     * @return total post count
     */
    public int getTotalPostsLastWeek() {
        return dataStore.getTotalPostsLastWeek();
    }

    /**
     * Retrieves number of unique active students in the last 7 days.
     *
     * @return number of active students
     */
    public int getActiveStudentsLastWeek() {
        return dataStore.getActiveStudentsLastWeek();
    }

    // -------------------------------------------------------------------------
    // Low Participation Threads
    // -------------------------------------------------------------------------

    /**
     * Retrieves threads that fall below the participation threshold.
     *
     * Each entry contains:
     * - threadId
     * - threadTitle
     * - replyCount
     * - createdAt timestamp
     *
     * @return list of thread data arrays
     */
    public List<String[]> getLowParticipationThreads() {
        return dataStore.getLowParticipationThreads(LOW_PARTICIPATION_THRESHOLD);
    }

    /**
     * Returns the configured threshold for low participation.
     *
     * @return reply count threshold
     */
    public int getLowParticipationThreshold() {
        return LOW_PARTICIPATION_THRESHOLD;
    }

    // -------------------------------------------------------------------------
    // Student Profiles
    // -------------------------------------------------------------------------

    /**
     * Retrieves summary data for all students.
     *
     * Each entry contains:
     * - userId
     * - userName
     * - email
     * - totalPosts
     * - lastActive timestamp
     *
     * @return list of student summary arrays
     */
    public List<String[]> getAllStudentSummaries() {
        return dataStore.getAllStudentSummaries();
    }

    /**
     * Retrieves detailed profile information for a specific student.
     *
     * Returned array contains:
     * - userId
     * - userName
     * - email
     * - totalPosts
     * - lastActive timestamp
     * - threadsStarted
     *
     * @param userId unique identifier of the student
     * @return student profile data array
     */
    public String[] getStudentProfile(String userId) {
        return dataStore.getStudentProfile(userId);
    }

    /**
     * Retrieves recent posts for a specific student.
     *
     * Each entry contains:
     * - postId
     * - threadTitle
     * - postContent
     * - createdAt timestamp
     *
     * @param userId unique identifier of the student
     * @return list of recent posts
     */
    public List<String[]> getStudentRecentPosts(String userId) {
        return dataStore.getStudentRecentPosts(userId, RECENT_POSTS_LIMIT);
    }

    /**
     * Filters student summaries based on a search query.
     * Matches against username and email fields.
     *
     * @param query search string
     * @return filtered list of student summaries
     */
    public List<String[]> searchStudents(String query) {
        List<String[]> all = getAllStudentSummaries();

        if (query == null || query.isBlank()) {
            return all;
        }

        String lower = query.toLowerCase();
        List<String[]> filtered = new ArrayList<>();

        for (String[] s : all) {
            if (s[1].toLowerCase().contains(lower) ||
                s[2].toLowerCase().contains(lower)) {
                filtered.add(s);
            }
        }

        return filtered;
    }
}