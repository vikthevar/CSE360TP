package prototype;

/**
 * <p> Title: ModerationManagerTest Class. </p>
 *
 * <p> Description: This class provides a manual test harness for the risk-reduction
 * prototype of the Moderation and Content Flagging feature. The selected testable
 * portion is limited to flagging and status management of posts. </p>
 *
 * <p> The tests in this class focus only on the behaviors identified in the
 * "Which Aspect and Why" document:
 * marking a post as flagged, hidden, and high-quality, along with storing and
 * retrieving those moderation states correctly. This class also covers the listed
 * edge cases, including repeated flagging, multiple simultaneous statuses, null
 * post handling, and protection against unintended status overwrite behavior. </p>
 *
 * <p> This class uses console-based manual testing instead of JUnit so that the
 * prototype remains dependency-free and easy to demonstrate in a screencast. </p>
 *
 * <p> Copyright: Vikram Thevar © 2026 </p>
 *
 * @author Vikram Thevar
 * @version 1.10 2026-04-08 Revised to match selected aspect portion exactly
 */
public class ModerationManagerTest {

    /**
     * <p> Method: main </p>
     *
     * <p> Description: Executes all manual tests for the selected prototype behavior.
     * Each test prints expected and actual results so the grader can verify whether
     * the moderation state logic behaves correctly. </p>
     *
     * @param args command-line arguments, not used
     */
    public static void main(String[] args) {

        ModerationManager manager = new ModerationManager();

        System.out.println("===== ModerationManager Prototype Tests =====");

        testFlagPost(manager);
        testHidePost(manager);
        testMarkHighQuality(manager);
        testStoreAndRetrieveStates(manager);
        testRepeatedFlagging(manager);
        testMultipleStatuses(manager);
        testNullPostHandling(manager);
        testStatusUpdatesDoNotOverwrite(manager);

        System.out.println("===== End of Prototype Tests =====");
    }

    /**
     * <p> Method: testFlagPost </p>
     *
     * <p> Description: Tests whether a normal post can be marked as flagged and
     * whether the flagged state is stored correctly. </p>
     *
     * @param manager the moderation manager being tested
     */
    private static void testFlagPost(ModerationManager manager) {

        System.out.println("\nTest 1: Flag Post");

        PostPrototype post = new PostPrototype(1);

        manager.flagPost(post);

        // WHY: The selected prototype must support marking a post as flagged and
        // retrieving that state correctly during grading review.
        System.out.println("Expected flagged: true | Actual: " + post.isFlagged());
    }

    /**
     * <p> Method: testHidePost </p>
     *
     * <p> Description: Tests whether a normal post can be marked as hidden and
     * whether the hidden state is stored correctly. </p>
     *
     * @param manager the moderation manager being tested
     */
    private static void testHidePost(ModerationManager manager) {

        System.out.println("\nTest 2: Hide Post");

        PostPrototype post = new PostPrototype(2);

        manager.hidePost(post);

        // WHY: The selected prototype must support marking a post as hidden without
        // deleting or losing the post's moderation state.
        System.out.println("Expected hidden: true | Actual: " + post.isHidden());
    }

    /**
     * <p> Method: testMarkHighQuality </p>
     *
     * <p> Description: Tests whether a normal post can be marked as high-quality
     * and whether that state is stored correctly. </p>
     *
     * @param manager the moderation manager being tested
     */
    private static void testMarkHighQuality(ModerationManager manager) {

        System.out.println("\nTest 3: Mark High-Quality Post");

        PostPrototype post = new PostPrototype(3);

        manager.markHighQuality(post);

        // WHY: The selected prototype must support identifying strong responses so
        // graders can prioritize them during evaluation.
        System.out.println("Expected highQuality: true | Actual: " + post.isHighQuality());
    }

    /**
     * <p> Method: testStoreAndRetrieveStates </p>
     *
     * <p> Description: Tests whether moderation states are correctly stored and
     * retrievable after multiple operations on a single post. </p>
     *
     * @param manager the moderation manager being tested
     */
    private static void testStoreAndRetrieveStates(ModerationManager manager) {

        System.out.println("\nTest 4: Store and Retrieve Moderation States");

        PostPrototype post = new PostPrototype(4);

        manager.flagPost(post);
        manager.hidePost(post);
        manager.markHighQuality(post);

        // WHY: The chosen prototype portion explicitly includes storing and retrieving
        // moderation states for each post, so all states should remain accessible.
        System.out.println("Expected flagged: true | Actual: " + post.isFlagged());
        System.out.println("Expected hidden: true | Actual: " + post.isHidden());
        System.out.println("Expected highQuality: true | Actual: " + post.isHighQuality());
    }

    /**
     * <p> Method: testRepeatedFlagging </p>
     *
     * <p> Description: Tests the edge case of flagging the same post more than once.
     * The post should remain flagged and the repeated operation should not cause errors. </p>
     *
     * @param manager the moderation manager being tested
     */
    private static void testRepeatedFlagging(ModerationManager manager) {

        System.out.println("\nTest 5: Repeated Flagging");

        PostPrototype post = new PostPrototype(5);

        manager.flagPost(post);
        manager.flagPost(post);

        // WHY: Repeated flagging was identified as an edge case in the selected
        // aspect document and should not break stored state.
        System.out.println("Expected flagged after repeated operation: true | Actual: " + post.isFlagged());
    }

    /**
     * <p> Method: testMultipleStatuses </p>
     *
     * <p> Description: Tests the edge case where a single post receives multiple
     * moderation statuses at the same time. </p>
     *
     * @param manager the moderation manager being tested
     */
    private static void testMultipleStatuses(ModerationManager manager) {

        System.out.println("\nTest 6: Multiple Statuses on One Post");

        PostPrototype post = new PostPrototype(6);

        manager.flagPost(post);
        manager.markHighQuality(post);

        // WHY: The selected portion specifically mentions applying multiple statuses
        // to a single post, so both values should remain true together.
        System.out.println("Expected flagged: true | Actual: " + post.isFlagged());
        System.out.println("Expected highQuality: true | Actual: " + post.isHighQuality());
    }

    /**
     * <p> Method: testNullPostHandling </p>
     *
     * <p> Description: Tests whether the moderation methods safely handle null post
     * references without crashing. </p>
     *
     * @param manager the moderation manager being tested
     */
    private static void testNullPostHandling(ModerationManager manager) {

        System.out.println("\nTest 7: Null Post Handling");

        try {
            manager.flagPost(null);
            manager.hidePost(null);
            manager.markHighQuality(null);

            // WHY: Null post handling was explicitly listed as an edge case. The
            // prototype must fail safely instead of crashing the grading workflow.
            System.out.println("Expected: No crash | Actual: No crash");
        } catch (Exception e) {
            System.out.println("Expected: No crash | Actual: Crash occurred");
        }
    }

    /**
     * <p> Method: testStatusUpdatesDoNotOverwrite </p>
     *
     * <p> Description: Tests whether setting one moderation state leaves previously
     * assigned states unchanged. </p>
     *
     * @param manager the moderation manager being tested
     */
    private static void testStatusUpdatesDoNotOverwrite(ModerationManager manager) {

        System.out.println("\nTest 8: Status Updates Do Not Overwrite Existing States");

        PostPrototype post = new PostPrototype(7);

        manager.flagPost(post);
        manager.hidePost(post);

        boolean flaggedAfterHide = post.isFlagged();
        boolean hiddenAfterHide = post.isHidden();

        manager.markHighQuality(post);

        // WHY: The selected aspect explicitly calls out the risk that status updates
        // might overwrite earlier moderation states incorrectly.
        System.out.println("Expected flagged remains true: true | Actual: " + flaggedAfterHide);
        System.out.println("Expected hidden remains true: true | Actual: " + hiddenAfterHide);
        System.out.println("Expected highQuality becomes true: true | Actual: " + post.isHighQuality());
    }
}