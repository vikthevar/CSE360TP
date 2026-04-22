package applicationMain;

import database.Database;
import discussionStore.PostStore;
import entityClasses.Post;
import java.sql.SQLException;
import java.util.List;

/**
 * Test Bed for Task 2.4: Implementing Boundary Value and Coverage Tests 
 * for TP2 with a focus on CWE-862 (Missing Authorization).
 */
public class TP2AuthorizationTest {

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

        //  TEST CASE COV-01: Author Match (Expected: SUCCESS)
        System.out.println("[COV-01] Testing Authorized Update (Author Match)...");
        String result1 = postStore.updatePost(postIdA, "Updated Title", "Still JavaFX");
        if (result1 == null) {
            System.out.println("Result: SUCCESS (As expected for the author)\n");
        } else {
            System.out.println("Result: FAILED - " + result1 + "\n");
        }

        // TEST CASE COV-02: Author Mismatch (Expected SECURE: FAIL, Current: SUCCESS)
        System.out.println("[COV-02] Testing Unauthorized Update (CWE-862 Detection)...");
        System.out.println("Attempting update of UserA's post as 'UserB'...");
        
        // BUG DETECTION: In current TP2, this will succeed because updatePost doesn't check the user
        String result2 = postStore.updatePost(postIdA, "HACKED BY USER B", "Malformed Content");
        
        if (result2 == null) {
            System.out.println("Result: VULNERABILITY DETECTED! UserB was able to edit UserA's post.");
            System.out.println("IDENTIFIED DEFECT: PostStore.updatePost lacks author verification.\n");
        } else {
            System.out.println("Result: SECURE (Access Denied)\n");
        }

        // TEST CASE BVA-01: ID Limits (Boundary Values)
        System.out.println("[BVA-01] Testing ID Boundary (postId = 0)...");
        String result3 = postStore.deletePost(0);
        System.out.println("Result for ID 0: " + (result3 != null ? "Handled (" + result3 + ")" : "Incorrect Success") + "\n");

        System.out.println("[BVA-01] Testing ID Boundary (postId = Integer.MAX_VALUE)...");
        String result4 = postStore.deletePost(Integer.MAX_VALUE);
        System.out.println("Result for MAX_INT: " + (result4 != null ? "Handled (" + result4 + ")" : "Incorrect Success") + "\n");

        //TEST CASE BVA-03: Null Inputs
        System.out.println("[BVA-03] Testing Null Input Handling...");
        String result5 = postStore.updatePost(postIdA, null, "Null Title Test");
        if (result5 != null) {
            System.out.println("Result: SUCCESS (Validation correctly blocked null title: " + result5 + ")\n");
        } else {
            System.out.println("Result: FAILED (System accepted a null title!)\n");
        }

        System.out.println("--- TP2 Testing Complete ---");
    }
}
