package applicationMain;

import database.Database;
import discussionStore.PostStore;
import entityClasses.Post;

import java.sql.SQLException;
import java.util.List;

/**
 * <p> Title: PostModerationTestbed Class. </p>
 *
 * <p> Description:
 * This class provides a semi-automated testbed used to validate the TP3
 * post moderation functionality. It executes a sequence of operations
 * on a discussion post including creation, flagging, hiding, highlighting,
 * and reversing those actions, while printing the resulting state after
 * each step.</p>
 *
 * <p> This testbed supports validation of the following requirements:
 * <ul>
 *   <li>Staff can flag posts with a reason and reviewer identity</li>
 *   <li>Staff can hide and unhide posts</li>
 *   <li>Staff can highlight and remove highlight from posts</li>
 *   <li>System maintains correct post state transitions</li>
 * </ul>
 * </p>
 *
 * <p> This testbed is used as:
 * <ul>
 *   <li>a semi-automated test referenced in the Javadoc of PostStore methods</li>
 *   <li>a validation tool to confirm correct database persistence behavior</li>
 *   <li>supporting evidence in TP3 screencasts</li>
 * </ul>
 * </p>
 *
 * <p> This test does not replace JUnit tests. Instead, it complements them
 * by demonstrating realistic execution scenarios involving multiple
 * moderation operations.</p>
 *
 * @author Vikram Thevar
 */
public class PostModerationTestbed {

    /**
     * Executes the moderation test sequence.
     *
     * <p> This method performs the following steps:
     * <ol>
     *   <li>Connects to the database</li>
     *   <li>Creates a test post</li>
     *   <li>Applies moderation actions (flag, hide, highlight)</li>
     *   <li>Reverses moderation actions (unhide, remove highlight, unflag)</li>
     *   <li>Prints post state after each operation</li>
     * </ol>
     * </p>
     *
     * <p> Expected behavior:
     * <ul>
     *   <li>Each operation updates the post state correctly</li>
     *   <li>No exceptions occur during valid operations</li>
     *   <li>PostStore methods return null on success</li>
     * </ul>
     * </p>
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        Database database = new Database();

        try {
            database.connectToDatabase();
            PostStore postStore = new PostStore(database);

            System.out.println("==================================================");
            System.out.println("TP3 POST MODERATION TESTBED");
            System.out.println("==================================================");

            String createError = postStore.createPost(
                    "TP3 Moderation Test Post",
                    "This is a realistic post body used to test moderation features.",
                    "vikr",
                    "Assignment1"
            );

            if (createError != null) {
                System.out.println("Create failed: " + createError);
                return;
            }

            Post createdPost = findNewestPost(postStore.getAllPosts());
            if (createdPost == null) {
                System.out.println("Failed to locate created post.");
                return;
            }

            int postId = createdPost.getPostId();
            System.out.println("Created test post ID: " + postId);

            printPostState("Initial State", postStore.getPostById(postId));

            // FLAG
            String flagError = postStore.flagPost(postId, "Test flag reason", "staffTester");
            System.out.println("Flag result: " + (flagError == null ? "SUCCESS" : flagError));
            printPostState("After Flag", postStore.getPostById(postId));

            // HIDE
            String hideError = postStore.hidePost(postId);
            System.out.println("Hide result: " + (hideError == null ? "SUCCESS" : hideError));
            printPostState("After Hide", postStore.getPostById(postId));

            // HIGHLIGHT
            String highlightError = postStore.highlightPost(postId);
            System.out.println("Highlight result: " + (highlightError == null ? "SUCCESS" : highlightError));
            printPostState("After Highlight", postStore.getPostById(postId));

            // UNHIDE
            String unhideError = postStore.unhidePost(postId);
            System.out.println("Unhide result: " + (unhideError == null ? "SUCCESS" : unhideError));
            printPostState("After Unhide", postStore.getPostById(postId));

            // REMOVE HIGHLIGHT
            String removeHighlightError = postStore.removeHighlightPost(postId);
            System.out.println("Remove highlight result: " + (removeHighlightError == null ? "SUCCESS" : removeHighlightError));
            printPostState("After Remove Highlight", postStore.getPostById(postId));

            // UNFLAG
            String unflagError = postStore.unflagPost(postId);
            System.out.println("Unflag result: " + (unflagError == null ? "SUCCESS" : unflagError));
            printPostState("After Unflag", postStore.getPostById(postId));

            System.out.println("==================================================");
            System.out.println("TEST COMPLETE");
            System.out.println("==================================================");

        } catch (SQLException e) {
            System.out.println("Database connection failed.");
            e.printStackTrace();
        } finally {
            database.closeConnection();
        }
    }

    /**
     * Finds the most recently created post using the highest post ID.
     *
     * @param posts list of posts retrieved from the store
     * @return the post with the highest ID, or null if none exist
     */
    private static Post findNewestPost(List<Post> posts) {
        if (posts == null || posts.isEmpty()) return null;

        Post newest = posts.get(0);
        for (Post p : posts) {
            if (p.getPostId() > newest.getPostId()) {
                newest = p;
            }
        }
        return newest;
    }

    /**
     * Prints the current state of a post including moderation flags.
     *
     * @param label label describing the test step
     * @param post the post to inspect
     */
    private static void printPostState(String label, Post post) {
        System.out.println("--------------------------------------------------");
        System.out.println(label);

        if (post == null) {
            System.out.println("Post is null.");
            return;
        }

        System.out.println("Post ID: " + post.getPostId());
        System.out.println("Title: " + post.getTitle());
        System.out.println("Author: " + post.getAuthor());
        System.out.println("Thread: " + post.getThread());
        System.out.println("Deleted: " + post.isDeleted());
        System.out.println("Flagged: " + post.isFlagged());
        System.out.println("Hidden: " + post.isHidden());
        System.out.println("Highlighted: " + post.isHighlighted());
        System.out.println("Flag Reason: " + post.getFlagReason());
        System.out.println("Flagged By: " + post.getFlaggedBy());
    }
}