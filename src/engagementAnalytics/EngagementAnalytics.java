package engagementAnalytics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import entityClasses.Post;

/**
 * EngagementAnalytics computes simple discussion metrics for student participation.
 *
 * <p>This class is intentionally lightweight and built to support a plain-text
 * report showing how many posts each student created, the average word count of
 * their posts, and an approximate average post gap between their successive posts.</p>
 *
 * @author Rohan Kshatriya
 */
public class EngagementAnalytics {

    /**
     * Builds a simple engagement report for the discussion board.
     *
     * @param posts all posts to analyze
     * @return formatted report text
     */
    public static String buildReport(List<Post> posts) {
        if (posts == null || posts.isEmpty()) {
            return "No posts available for engagement analysis.";
        }

        Map<String, List<Post>> postsByAuthor = new HashMap<>();

        for (Post post : posts) {
            if (post == null || post.isDeleted()) {
                continue;
            }
            String author = normalizeAuthor(post.getAuthor());
            postsByAuthor.computeIfAbsent(author, key -> new ArrayList<>())
                    .add(post);
        }

        if (postsByAuthor.isEmpty()) {
            return "No active posts found for engagement analysis.";
        }

        List<EngagementMetrics> metrics = new ArrayList<>();
        int totalPosts = 0;
        int totalWords = 0;

        for (String author : new TreeSet<>(postsByAuthor.keySet())) {
            List<Post> authorPosts = postsByAuthor.get(author);
            authorPosts.sort(Comparator.comparingInt(Post::getPostId));

            int authorWordCount = 0;
            List<Integer> postIds = new ArrayList<>();
            for (Post p : authorPosts) {
                authorWordCount += countWords(p.getBody());
                postIds.add(p.getPostId());
            }

            double averageWords = authorPosts.isEmpty()
                    ? 0.0
                    : ((double) authorWordCount / authorPosts.size());
            double averageGap = computeAveragePostGap(postIds);

            metrics.add(new EngagementMetrics(author,
                    authorPosts.size(),
                    authorWordCount,
                    averageWords,
                    averageGap));
            totalPosts += authorPosts.size();
            totalWords += authorWordCount;
        }

        double overallAverageWords = totalPosts == 0 ? 0.0 : ((double) totalWords / totalPosts);

        StringBuilder report = new StringBuilder();
        report.append("Engagement Statistics\n");
        report.append("----------------------\n");
        report.append("Total active students: ").append(postsByAuthor.size()).append("\n");
        report.append("Total active posts: ").append(totalPosts).append("\n");
        report.append(String.format("Average words per post: %.1f\n", overallAverageWords));
        report.append("\n");
        report.append("Student metrics:\n");

        for (EngagementMetrics metric : metrics) {
            report.append(String.format(
                    "%s: %d posts, avg words %.1f, avg post gap %.1f posts\n",
                    metric.getStudentName(),
                    metric.getPostCount(),
                    metric.getAverageWordsPerPost(),
                    metric.getAveragePostGap()));
        }

        report.append("\n");
        report.append("Note: average post gap is measured as the average number of other posts\n");
        report.append("between a student\'s successive posts. It is a simple participation metric.\n");
        return report.toString();
    }

    private static String normalizeAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            return "Unknown student";
        }
        return author.trim();
    }

    private static int countWords(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        String[] tokens = text.trim().split("\\s+");
        return tokens.length;
    }

    private static double computeAveragePostGap(List<Integer> postIds) {
        if (postIds == null || postIds.size() < 2) {
            return 0.0;
        }
        double totalGap = 0.0;
        for (int i = 1; i < postIds.size(); i++) {
            totalGap += (postIds.get(i) - postIds.get(i - 1) - 1);
        }
        return totalGap / (postIds.size() - 1);
    }
}
