package engagementAnalytics;

/**
 * Simple metrics for student discussion engagement.
 *
 * @author Rohan Kshatriya
 */
public class EngagementMetrics {

    private final String studentName;
    private final int postCount;
    private final int totalWordCount;
    private final double averageWordsPerPost;
    private final double averagePostGap;

    public EngagementMetrics(
            String studentName,
            int postCount,
            int totalWordCount,
            double averageWordsPerPost,
            double averagePostGap) {
        this.studentName = studentName;
        this.postCount = postCount;
        this.totalWordCount = totalWordCount;
        this.averageWordsPerPost = averageWordsPerPost;
        this.averagePostGap = averagePostGap;
    }

    public String getStudentName() {
        return studentName;
    }

    public int getPostCount() {
        return postCount;
    }

    public int getTotalWordCount() {
        return totalWordCount;
    }

    public double getAverageWordsPerPost() {
        return averageWordsPerPost;
    }

    public double getAveragePostGap() {
        return averagePostGap;
    }
}
