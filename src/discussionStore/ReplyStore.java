package discussionStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import discussionValidation.ReplyValidator;
import entityClasses.Reply;

/**
 * The ReplyStore class manages all replies and subset reply lists.
 * This class supports CRUD operations and search functionality.
 *
 * Post existence validation is handled in the Controller layer(guiDiscussion).
 */
public class ReplyStore {

    /** List containing all replies */
    private final List<Reply> allReplies;

    /** List containing subset replies (e.g., search results) */
    private final List<Reply> subsetReplies;

    /** Auto-incrementing reply ID */
    private int nextReplyId;

    /**
     * Constructs an empty ReplyStore.
     */
    public ReplyStore() {
        this.allReplies = new ArrayList<>();
        this.subsetReplies = new ArrayList<>();
        this.nextReplyId = 1;
    }

    /**
     * Creates and stores a new reply.
     * Post existence must be checked before calling this method.
     *
     * @param postId associated post ID
     * @param body reply content
     * @param author reply author
     * @return null if success; otherwise validation error message
     */
    public String createReply(int postId, String body, String author) {

        String validationMessage = ReplyValidator.validate(postId, body);
        if (validationMessage != null) {
            return validationMessage;
        }

        Reply reply = new Reply(nextReplyId, postId,
                body.trim(),
                author == null ? "" : author.trim());

        allReplies.add(reply);
        nextReplyId++;

        return null;
    }

    /**
     * Returns a reply by ID.
     *
     * @param replyId reply ID
     * @return Reply if found; otherwise null
     */
    public Reply getReplyById(int replyId) {
        for (Reply r : allReplies) {
            if (r.getReplyId() == replyId) {
                return r;
            }
        }
        return null;
    }

    /**
     * Returns all replies associated with a given post ID.
     *
     * @param postId post ID
     * @return list of matching replies (may be empty)
     */
    public List<Reply> getRepliesByPostId(int postId) {
        List<Reply> results = new ArrayList<>();
        for (Reply r : allReplies) {
            if (r.getPostId() == postId) {
                results.add(r);
            }
        }
        return results;
    }

    /**
     * Updates the body of an existing reply.
     *
     * @param replyId reply ID
     * @param newBody new reply body
     * @return null if success; otherwise error message
     */
    public String updateReply(int replyId, String newBody) {

        Reply reply = getReplyById(replyId);
        if (reply == null) {
            return "Reply not found.";
        }

        String validationMessage = ReplyValidator.validate(reply.getPostId(), newBody);
        if (validationMessage != null) {
            return validationMessage;
        }

        reply.setBody(newBody.trim());
        return null;
    }

    /**
     * Deletes a reply.
     *
     * @param replyId reply ID
     * @return null if success; otherwise error message
     */
    public String deleteReply(int replyId) {

        for (int i = 0; i < allReplies.size(); i++) {
            if (allReplies.get(i).getReplyId() == replyId) {
                allReplies.remove(i);
                return null;
            }
        }

        return "Reply not found.";
    }

    /**
     * Searches replies by keyword (case-insensitive) and
     * stores results in subsetReplies.
     *
     * @param keyword search term
     */
    public void searchReplies(String keyword) {

        subsetReplies.clear();

        if (keyword == null || keyword.trim().isEmpty()) {
            return;
        }

        String key = keyword.trim().toLowerCase();

        for (Reply r : allReplies) {
            if (r.getBody().toLowerCase().contains(key)) {
                subsetReplies.add(r);
            }
        }
    }

    /**
     * Returns all replies (unmodifiable).
     *
     * @return list of all replies
     */
    public List<Reply> getAllReplies() {
        return Collections.unmodifiableList(allReplies);
    }

    /**
     * Returns the current subset reply list (unmodifiable).
     *
     * @return subset reply list
     */
    public List<Reply> getSubsetReplies() {
        return Collections.unmodifiableList(subsetReplies);
    }
}