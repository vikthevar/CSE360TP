package prototype;

/**
 * <p> Title: PostPrototype Class. </p>
 *
 * <p> Description: This is a simplified prototype representation of a discussion post.
 * It includes moderation-related state fields used to support grading workflows,
 * such as flagged, hidden, and high-quality indicators. </p>
 *
 * <p> This class is intentionally minimal and independent of the TP2 system to
 * support risk-reduction prototyping for moderation logic. </p>
 *
 * <p> Copyright: Vikram Thevar © 2026 </p>
 *
 * @author Vikram Thevar
 * @version 1.00 2026-04-08 Initial prototype version
 */
public class PostPrototype {

    private int postId;
    private boolean flagged;
    private boolean hidden;
    private boolean highQuality;

    /**
     * <p> Constructor: PostPrototype </p>
     *
     * <p> Description: Creates a new PostPrototype object with a unique identifier.
     * All moderation states are initialized to false so that the new post begins in
     * a normal visible and unreviewed state. </p>
     *
     * @param postId unique identifier for the post
     */
    public PostPrototype(int postId) {

        this.postId = postId;

        // WHY: Default state must represent a normal visible post
        this.flagged = false;
        this.hidden = false;
        this.highQuality = false;
    }

    /**
     * <p> Method: getPostId </p>
     *
     * <p> Description: Returns the unique identifier associated with this post
     * prototype. </p>
     *
     * @return the post identifier
     */
    public int getPostId() {
        return postId;
    }

    /**
     * <p> Method: isFlagged </p>
     *
     * <p> Description: Returns whether this post has been marked as flagged for
     * review by instructional staff. </p>
     *
     * @return true if the post is flagged; false otherwise
     */
    public boolean isFlagged() {
        return flagged;
    }

    /**
     * <p> Method: isHidden </p>
     *
     * <p> Description: Returns whether this post has been marked as hidden from
     * standard views. </p>
     *
     * @return true if the post is hidden; false otherwise
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * <p> Method: isHighQuality </p>
     *
     * <p> Description: Returns whether this post has been marked as high-quality
     * for prioritization during grading or review. </p>
     *
     * @return true if the post is marked high-quality; false otherwise
     */
    public boolean isHighQuality() {
        return highQuality;
    }

    /**
     * <p> Method: setFlagged </p>
     *
     * <p> Description: Updates the flagged state of this post. </p>
     *
     * @param flagged the new flagged state
     */
    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }

    /**
     * <p> Method: setHidden </p>
     *
     * <p> Description: Updates the hidden state of this post. </p>
     *
     * @param hidden the new hidden state
     */
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    /**
     * <p> Method: setHighQuality </p>
     *
     * <p> Description: Updates the high-quality state of this post. </p>
     *
     * @param highQuality the new high-quality state
     */
    public void setHighQuality(boolean highQuality) {
        this.highQuality = highQuality;
    }
}