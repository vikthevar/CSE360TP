package engagementAnalytics;

/**
 * Title: EngagementMetrics Class.
 *
 * <p>Description:</p>
 * <p>
 * Represents a collection of quantitative metrics used to evaluate a student's
 * engagement within discussion threads. These metrics are derived from post
 * activity and are used to support staff analysis of participation quality
 * and consistency.
 * </p>
 *
 * <p>Metrics Captured:</p>
 * <ul>
 *   <li><b>studentName</b> – Identifier of the student being evaluated</li>
 *   <li><b>postCount</b> – Total number of posts created by the student</li>
 *   <li><b>totalWordCount</b> – Aggregate number of words across all posts</li>
 *   <li><b>averageWordsPerPost</b> – Average number of words per post</li>
 *   <li><b>averagePostGap</b> – Average time gap between posts (used to assess consistency)</li>
 * </ul>
 *
 * <p>
 * This class is typically produced by analytics or reporting services and is
 * consumed by controllers or views to display engagement statistics to staff.
 * </p>
 *
 * <p>Validated by:</p>
 * <ul>
 *   <li>ControllerDiscussion.buildEngagementStatisticsReport</li>
 * </ul>
 *
 * @author Rohan Kshatriya
 */
public class EngagementMetrics {

    /** Name of the student associated with these metrics. */
    private final String studentName;

    /** Total number of posts created by the student. */
    private final int postCount;

    /** Total number of words across all posts. */
    private final int totalWordCount;

    /** Average number of words per post. */
    private final double averageWordsPerPost;

    /** Average time gap between posts (in system-defined units). */
    private final double averagePostGap;

    /**
     * Constructs an EngagementMetrics object with all computed values.
     *
     * <p>Description:</p>
     * <p>
     * Initializes a complete set of engagement metrics for a student.
     * These values are assumed to be precomputed by an analytics component.
     * </p>
     *
     * @param studentName the name or identifier of the student
     * @param postCount the total number of posts made by the student
     * @param totalWordCount the total word count across all posts
     * @param averageWordsPerPost the average words per post
     * @param averagePostGap the average time gap between posts
     */
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

    /**
     * Returns the name of the student.
     *
     * @return the student name
     */
    public String getStudentName() {
        return studentName;
    }

    /**
     * Returns the total number of posts created by the student.
     *
     * @return the post count
     */
    public int getPostCount() {
        return postCount;
    }

    /**
     * Returns the total number of words across all posts.
     *
     * @return the total word count
     */
    public int getTotalWordCount() {
        return totalWordCount;
    }

    /**
     * Returns the average number of words per post.
     *
     * @return the average words per post
     */
    public double getAverageWordsPerPost() {
        return averageWordsPerPost;
    }

    /**
     * Returns the average time gap between posts.
     *
     * @return the average post gap
     */
    public double getAveragePostGap() {
        return averagePostGap;
    }
}