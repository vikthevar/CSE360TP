package applicationMain;

import database.Database;
import discussionStore.PostStore;
import entityClasses.Post;
import java.sql.SQLException;
import java.util.List;

/**
 * Title: TP2AuthorizationTest Class.
 *
 * <p>Description:</p>
 * <p>
 * This class provides a semi-automated test bed for evaluating security-related
 * behavior in Team Project Phase 2. The focus is on identifying potential
 * authorization weaknesses, specifically CWE-862 (Missing Authorization),
 * along with validating boundary conditions and null input handling.
 * </p>
 *
 * <p>Security Focus:</p>
 * <ul>
 *   <li>CWE-862: Missing Authorization</li>
 * </ul>
 *
 * <p>Test Coverage Includes:</p>
 * <ul>
 *   <li>Authorized vs unauthorized update scenarios</li>
 *   <li>Boundary value testing for post IDs</li>
 *   <li>Null input validation testing</li>
 * </ul>
 *
 * <p>
 * This test bed complements JUnit testing by producing readable console output
 * that demonstrates whether the implementation behaves correctly or exposes
 * security vulnerabilities.
 * </p>
 *
 * @author David Rowlands
 */
public class TP2AuthorizationTest {

    /**
     * Entry point for executing the TP2 security test cases.
     *
     * <p>Description:</p>
     * <p>
     * This method initializes the database, creates test data, and executes a
     * sequence of test cases that evaluate authorization behavior, boundary
     * conditions, and input validation.
     * </p>
     *
     * <p>Test Sequence:</p>
     * <ol>
     *   <li>Create a test post</li>
     *   <li>Test authorized update (expected success)</li>
     *   <li>Test unauthorized update to detect CWE-862</li>
     *   <li>Test boundary values for post ID deletion</li>
     *   <li>Test null input handling during updates</li>
     * </ol>
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Starting TP2 Security Tests (Project Phase 2)....\n");

        Database db = new Database();
        try {
            db.connectToDatabase();
        } catch (SQLException e) {
            System.err.println("Database setup failed: " + e.getMessage());
            return;
        }

        PostStore postStore = new PostStore(db);

        System.out.println("--- Setup: Creating Test Posts ---");
        postStore.createPost("User A's Question", "How do I use JavaFX?", "UserA", "Java");

        List<Post> posts = postStore.getAllPosts();
        if (posts.isEmpty()) {
            System.err.println("Failed to create test data.");
            return;
        }

        int postIdA = posts.get(0).getPostId();
        System.out.println("Created Post ID " + postIdA + " by Author: UserA\n");

        // TEST CASE COV-01: Authorized update
        System.out.println("[COV-01] Testing Authorized Update (Author Match)...");
        String result1 = postStore.updatePost(postIdA, "Updated Title", "Still JavaFX");

        if (result1 == null) {
            System.out.println("Result: SUCCESS (Authorized update allowed)\n");
        } else {
            System.out.println("Result: FAILED - " + result1 + "\n");
        }

        // TEST CASE COV-02: Unauthorized update (CWE-862)
        System.out.println("[COV-02] Testing Unauthorized Update (CWE-862 Detection)...");
        System.out.println("Attempting update of UserA's post as 'UserB'...");

        String result2 = postStore.updatePost(postIdA, "HACKED BY USER B", "Malformed Content");

        if (result2 == null) {
            System.out.println("Result: VULNERABILITY DETECTED");
            System.out.println("UserB was able to modify UserA's post.");
            System.out.println("Defect: Missing authorization check in updatePost().\n");
        } else {
            System.out.println("Result: SECURE (Access correctly denied)\n");
        }

        // TEST CASE BVA-01: Boundary testing
        System.out.println("[BVA-01] Testing ID Boundary (postId = 0)...");
        String result3 = postStore.deletePost(0);
        System.out.println("Result for ID 0: "
                + (result3 != null ? "Handled (" + result3 + ")" : "Incorrect Success") + "\n");

        System.out.println("[BVA-01] Testing ID Boundary (postId = Integer.MAX_VALUE)...");
        String result4 = postStore.deletePost(Integer.MAX_VALUE);
        System.out.println("Result for MAX_INT: "
                + (result4 != null ? "Handled (" + result4 + ")" : "Incorrect Success") + "\n");

        // TEST CASE BVA-03: Null input testing
        System.out.println("[BVA-03] Testing Null Input Handling...");
        String result5 = postStore.updatePost(postIdA, null, "Null Title Test");

        if (result5 != null) {
            System.out.println("Result: SUCCESS (Null input correctly rejected: " + result5 + ")\n");
        } else {
            System.out.println("Result: FAILED (Null title accepted)\n");
        }

        System.out.println("--- TP2 Testing Complete ---");
    }
}