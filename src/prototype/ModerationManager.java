package prototype;

/**
 * <p> Title: ModerationManager Class. </p>
 *
 * <p> Description: This prototype class provides moderation operations for discussion posts.
 * It allows instructional team members to flag posts, hide inappropriate content,
 * and mark high-quality responses. These features support grading workflows and
 * improve discussion evaluation efficiency. </p>
 *
 * <p> This class is designed as a standalone prototype to validate moderation logic
 * before integration into the full system. </p>
 *
 * <p> Copyright: Vikram Thevar © 2026 </p>
 *
 * @author Vikram Thevar
 * @version 1.00 2026-04-08 Initial prototype implementation
 */
public class ModerationManager {

    /**
     * Flags a post for review.
     *
     * @param post the post to be flagged
     */
    public void flagPost(PostPrototype post) {

        // WHY: Prevent null pointer crashes during grading workflows
        if (post == null) {
            return;
        }

        // WHY: Flagging should not alter content, only mark state
        post.setFlagged(true);
    }

    /**
     * Hides a post from standard views.
     *
     * @param post the post to be hidden
     */
    public void hidePost(PostPrototype post) {

        // WHY: Hidden posts must still exist for auditing and grading
        if (post == null) {
            return;
        }

        post.setHidden(true);
    }

    /**
     * Marks a post as high-quality.
     *
     * @param post the post to prioritize
     */
    public void markHighQuality(PostPrototype post) {

        // WHY: High-quality marking should coexist with other states
        if (post == null) {
            return;
        }

        post.setHighQuality(true);
    }

    /**
     * Determines if a post should be visible in grading view.
     *
     * @param post the post to check
     * @return true if visible, false otherwise
     */
    public boolean isVisible(PostPrototype post) {

        // WHY: Null posts should never appear in UI
        if (post == null) {
            return false;
        }

        // WHY: Hidden posts are excluded from normal grading views
        return !post.isHidden();
    }
}