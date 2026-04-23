package guiGraderDashboard;

import java.util.*;

/**
 * <p> Title: GraderDashboardController </p>
 *
 * <p> Description: MVC Controller for the Grader Dashboard. Sits between
 * the GraderDashboardPage (View) and GraderDashboardDataStore (Model).
 * Follows the Singleton pattern consistent with the rest of the project. </p>
 */
public class GraderDashboardController {

    private static GraderDashboardController instance = null;
    private final GraderDashboardDataStore dataStore;

    /** Low-participation threshold: threads with fewer than this many replies are flagged */
    private static final int LOW_PARTICIPATION_THRESHOLD = 3;
    /** Number of recent posts to show per student profile */
    private static final int RECENT_POSTS_LIMIT = 10;
    /** Number of days for activity trend chart */
    private static final int TREND_DAYS = 14;

    private GraderDashboardController() {
        dataStore = GraderDashboardDataStore.getInstance();
    }

    /** Returns the single instance of this controller */
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
     * Returns activity trends for the last 14 days.
     * Map key = date string, value = post count.
     */
    public Map<String, Integer> getActivityTrends() {
        return dataStore.getActivityTrends(TREND_DAYS);
    }

    /** Returns total posts in the last 7 days for the summary stat card. */
    public int getTotalPostsLastWeek() {
        return dataStore.getTotalPostsLastWeek();
    }

    /** Returns number of unique active students in the last 7 days. */
    public int getActiveStudentsLastWeek() {
        return dataStore.getActiveStudentsLastWeek();
    }

    // -------------------------------------------------------------------------
    // Low-Participation Threads
    // -------------------------------------------------------------------------

    /**
     * Returns threads flagged as low-participation.
     * Each entry: [threadId, threadTitle, replyCount, createdAt]
     */
    public List<String[]> getLowParticipationThreads() {
        return dataStore.getLowParticipationThreads(LOW_PARTICIPATION_THRESHOLD);
    }

    /** Returns the current low-participation reply threshold. */
    public int getLowParticipationThreshold() {
        return LOW_PARTICIPATION_THRESHOLD;
    }

    // -------------------------------------------------------------------------
    // Student Profiles
    // -------------------------------------------------------------------------

    /**
     * Returns summary list of all students for the roster table.
     * Each entry: [userId, userName, email, totalPosts, lastActive]
     */
    public List<String[]> getAllStudentSummaries() {
        return dataStore.getAllStudentSummaries();
    }

    /**
     * Returns full profile data for a single student.
     * Returns: [userId, userName, email, totalPosts, lastActive, threadsStarted]
     */
    public String[] getStudentProfile(String userId) {
        return dataStore.getStudentProfile(userId);
    }

    /**
     * Returns recent posts by a specific student.
     * Each entry: [postId, threadTitle, postContent, createdAt]
     */
    public List<String[]> getStudentRecentPosts(String userId) {
        return dataStore.getStudentRecentPosts(userId, RECENT_POSTS_LIMIT);
    }

    /**
     * Filters student summaries by a search string (matches username or email).
     */
    public List<String[]> searchStudents(String query) {
        List<String[]> all = getAllStudentSummaries();
        if (query == null || query.isBlank()) return all;
        String lower = query.toLowerCase();
        List<String[]> filtered = new ArrayList<>();
        for (String[] s : all) {
            if (s[1].toLowerCase().contains(lower) || s[2].toLowerCase().contains(lower)) {
                filtered.add(s);
            }
        }
        return filtered;
    }
}
