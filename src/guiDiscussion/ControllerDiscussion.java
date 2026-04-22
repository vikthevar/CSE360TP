package guiDiscussion;

import java.sql.SQLException;
import java.util.List;

import discussionStore.PostStore;
import discussionStore.ReplyStore;
import entityClasses.Post;
import entityClasses.Reply;
import engagementAnalytics.EngagementAnalytics;
import database.Database;

/**
 * <p> Title: ControllerDiscussion Class. </p>
 *
 * <p> Description: Controller for the discussion feature. This class owns the
 * discussion stores and provides helper methods for the view.</p>
 *
 * @author Vikram Thevar
 */
public class ControllerDiscussion {

    /** Shared database object for discussion persistence */
    private static final Database database;

    /** Stores used by the discussion feature */
    private static final PostStore postStore;
    private static final ReplyStore replyStore;

    static {
        try {
            database = new Database();
            database.connectToDatabase();

            postStore = new PostStore(database);
            replyStore = new ReplyStore(database);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize discussion database.", e);
        }
    }

    /**
     * Default constructor.
     */
    public ControllerDiscussion() {
    }

    /**
     * Creates a reply for testing or external calls.
     *
     * @param postId post ID
     * @param body reply body
     * @param author reply author
     * @return null if successful, otherwise an error message
     */
    public static String testCreateReply(int postId, String body, String author) {
        Post p = postStore.getPostById(postId);
        if (p == null) {
            return "Post does not exist.";
        }
        return replyStore.createReply(postId, body, author);
    }

    /**
     * Returns the post store.
     *
     * @return post store
     */
    protected static PostStore getPostStore() {
        return postStore;
    }

    /**
     * Returns the reply store.
     *
     * @return reply store
     */
    protected static ReplyStore getReplyStore() {
        return replyStore;
    }

    /**
     * Returns the database object.
     *
     * @return database
     */
    protected static Database getDatabase() {
        return database;
    }

    /**
     * Builds formatted text for replies belonging to a post.
     *
     * @param postId post ID
     * @return formatted reply text
     */
    protected static String buildRepliesDisplayText(int postId) {
        Post p = postStore.getPostById(postId);
        List<Reply> replies = replyStore.getRepliesByPostId(postId);

        boolean postDeleted = (p != null && p.isDeleted());

        StringBuilder sb = new StringBuilder();

        if (replies.isEmpty()) {
            sb.append("No replies found.\n");
        } else {
            for (Reply r : replies) {
                sb.append("Reply ").append(r.getReplyId())
                  .append(" (Post ").append(r.getPostId()).append(")\n");
                sb.append("Author: ").append(r.getAuthor()).append("\n");
                if (postDeleted) {
                    sb.append("Original post has been deleted.\n");
                }
                sb.append(r.getBody()).append("\n");
                sb.append("----\n");
            }
        }

        return sb.toString();
    }

    /**
     * Builds a plain-text engagement statistics report for the current posts.
     *
     * @return analytics report text
     */
    protected static String buildEngagementStatisticsReport() {
        return EngagementAnalytics.buildReport(postStore.getAllPosts());
    }
}
