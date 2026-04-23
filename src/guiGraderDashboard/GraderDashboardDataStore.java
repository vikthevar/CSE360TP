package guiGraderDashboard;

import java.util.*;

/**
 * <p> Title: GraderDashboardDataStore </p>
 *
 * <p> Description: Provides hardcoded sample data for the Grader Dashboard.
 * This version does not connect to a database. It is intended for MVP/demo
 * use so the GUI can be tested without any backend dependency. </p>
 */
public class GraderDashboardDataStore {

    private static GraderDashboardDataStore instance = null;

    private Map<String, Integer> activityTrends;
    private List<String[]> lowParticipationThreads;
    private List<String[]> studentSummaries;
    private Map<String, String[]> studentProfiles;
    private Map<String, List<String[]>> studentRecentPosts;

    /** Private constructor for Singleton pattern */
    private GraderDashboardDataStore() {
        loadSampleData();
    }

    /** Returns the single instance of this store */
    public static GraderDashboardDataStore getInstance() {
        if (instance == null) {
            instance = new GraderDashboardDataStore();
        }
        return instance;
    }

    /**
     * Loads all hardcoded demo data.
     */
    private void loadSampleData() {
        activityTrends = new LinkedHashMap<>();
        activityTrends.put("2026-04-16", 5);
        activityTrends.put("2026-04-17", 8);
        activityTrends.put("2026-04-18", 4);
        activityTrends.put("2026-04-19", 10);
        activityTrends.put("2026-04-20", 6);
        activityTrends.put("2026-04-21", 9);
        activityTrends.put("2026-04-22", 7);

        lowParticipationThreads = new ArrayList<>();
        lowParticipationThreads.add(new String[] { "T101", "Question about Binary Trees", "1", "2026-04-20" });
        lowParticipationThreads.add(new String[] { "T102", "Help with Homework 7", "0", "2026-04-21" });
        lowParticipationThreads.add(new String[] { "T103", "Confused about MVC Pattern", "2", "2026-04-19" });

        studentSummaries = new ArrayList<>();
        studentSummaries.add(new String[] { "S001", "Alice Johnson", "alice@asu.edu", "12", "2026-04-22" });
        studentSummaries.add(new String[] { "S002", "Brian Lee", "brian@asu.edu", "7", "2026-04-21" });
        studentSummaries.add(new String[] { "S003", "Carlos Mendez", "carlos@asu.edu", "3", "2026-04-18" });
        studentSummaries.add(new String[] { "S004", "Diana Smith", "diana@asu.edu", "0", "Never" });

        studentProfiles = new HashMap<>();
        studentProfiles.put("S001", new String[] { "S001", "Alice Johnson", "alice@asu.edu", "12", "2026-04-22", "3" });
        studentProfiles.put("S002", new String[] { "S002", "Brian Lee", "brian@asu.edu", "7", "2026-04-21", "2" });
        studentProfiles.put("S003", new String[] { "S003", "Carlos Mendez", "carlos@asu.edu", "3", "2026-04-18", "1" });
        studentProfiles.put("S004", new String[] { "S004", "Diana Smith", "diana@asu.edu", "0", "Never", "0" });

        studentRecentPosts = new HashMap<>();

        List<String[]> alicePosts = new ArrayList<>();
        alicePosts.add(new String[] { "P201", "Question about Binary Trees", "I think the root should be visited first in preorder.", "2026-04-22" });
        alicePosts.add(new String[] { "P202", "Help with Homework 7", "I got 42 for problem 3, not sure if that is right.", "2026-04-21" });
        alicePosts.add(new String[] { "P203", "MVC Design Discussion", "The controller should handle button events.", "2026-04-20" });
        studentRecentPosts.put("S001", alicePosts);

        List<String[]> brianPosts = new ArrayList<>();
        brianPosts.add(new String[] { "P204", "Confused about MVC Pattern", "The model should not directly update the UI.", "2026-04-21" });
        brianPosts.add(new String[] { "P205", "Question about Binary Trees", "Could someone explain inorder traversal?", "2026-04-19" });
        studentRecentPosts.put("S002", brianPosts);

        List<String[]> carlosPosts = new ArrayList<>();
        carlosPosts.add(new String[] { "P206", "Midterm Review", "I am still confused about recursion base cases.", "2026-04-18" });
        studentRecentPosts.put("S003", carlosPosts);

        studentRecentPosts.put("S004", new ArrayList<String[]>());
    }

    // -------------------------------------------------------------------------
    // Activity Trends
    // -------------------------------------------------------------------------

    /**
     * Returns the number of discussion posts per day for the last N days.
     * Map key = date string, value = post count.
     */
    public Map<String, Integer> getActivityTrends(int lastNDays) {
        Map<String, Integer> result = new LinkedHashMap<>();
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(activityTrends.entrySet());

        int start = Math.max(0, entries.size() - lastNDays);
        for (int i = start; i < entries.size(); i++) {
            Map.Entry<String, Integer> entry = entries.get(i);
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    /**
     * Returns total posts in the last 7 days.
     */
    public int getTotalPostsLastWeek() {
        int total = 0;
        for (int count : activityTrends.values()) {
            total += count;
        }
        return total;
    }

    /**
     * Returns number of unique active students in the last 7 days.
     */
    public int getActiveStudentsLastWeek() {
        int activeCount = 0;
        for (String[] student : studentSummaries) {
            int totalPosts = Integer.parseInt(student[3]);
            if (totalPosts > 0) {
                activeCount++;
            }
        }
        return activeCount;
    }

    // -------------------------------------------------------------------------
    // Low-Participation Threads
    // -------------------------------------------------------------------------

    /**
     * Returns threads with fewer than the given reply threshold.
     * Each entry: [threadId, threadTitle, replyCount, createdAt]
     */
    public List<String[]> getLowParticipationThreads(int replyThreshold) {
        List<String[]> result = new ArrayList<>();
        for (String[] thread : lowParticipationThreads) {
            int replies = Integer.parseInt(thread[2]);
            if (replies < replyThreshold) {
                result.add(thread);
            }
        }
        return result;
    }

    // -------------------------------------------------------------------------
    // Student Profiles
    // -------------------------------------------------------------------------

    /**
     * Returns a summary list of all students.
     * Each entry: [userId, userName, email, totalPosts, lastActive]
     */
    public List<String[]> getAllStudentSummaries() {
        return new ArrayList<>(studentSummaries);
    }

    /**
     * Returns detailed profile data for a single student.
     * Returns: [userId, userName, email, totalPosts, lastActive, threadsStarted]
     */
    public String[] getStudentProfile(String userId) {
        return studentProfiles.get(userId);
    }

    /**
     * Returns recent posts by a specific student.
     * Each entry: [postId, threadTitle, postContent, createdAt]
     */
    public List<String[]> getStudentRecentPosts(String userId, int limit) {
        List<String[]> posts = studentRecentPosts.get(userId);
        List<String[]> result = new ArrayList<>();

        if (posts == null) {
            return result;
        }

        for (int i = 0; i < posts.size() && i < limit; i++) {
            result.add(posts.get(i));
        }

        return result;
    }
}
