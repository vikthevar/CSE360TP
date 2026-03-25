package discussionStore;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import discussionValidation.ReplyValidator;
import entityClasses.Reply;
import database.Database;

/**
 * The ReplyStore class manages all replies and subset reply lists.
 * This version is database-backed so replies persist across relaunches.
 */
public class ReplyStore {

    /** Shared database reference */
    private final Database database;

    /** Cached replies (for quick UI display) */
    private final List<Reply> allReplies;

    /** Subset replies (e.g., search results) */
    private final List<Reply> subsetReplies;

    /**
     * Constructs a ReplyStore and loads replies from DB.
     */
    public ReplyStore(Database database) {
        this.database = database;
        this.allReplies = new ArrayList<>();
        this.subsetReplies = new ArrayList<>();
        refreshAllReplies();
    }

    /**
     * Reloads all replies from database (simple global load).
     */
    private void refreshAllReplies() {
        allReplies.clear();
        try {
            // Load everything by scanning posts is expensive; instead, lazy-load per post.
            // Keep cache minimal here.
        } catch (Exception e) {
            throw new RuntimeException("Failed to load replies.", e);
        }
    }

    /**
     * Creates and stores a new reply.
     */
    public String createReply(int postId, String body, String author) {

        String validationMessage = ReplyValidator.validate(postId, body);
        if (validationMessage != null) {
            return validationMessage;
        }

        try {
            database.hw2CreateReply(
                    postId,
                    body.trim(),
                    (author == null ? "" : author.trim())
            );
            return null;
        } catch (SQLException e) {
            return "Database error while creating reply.";
        }
    }

    /**
     * Returns a reply by ID.
     */
    public Reply getReplyById(int replyId) {
        try {
            return database.hw2GetReplyById(replyId);
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Returns all non-deleted replies for a post.
     */
    public List<Reply> getRepliesByPostId(int postId) {
        try {
            List<Reply> replies = database.hw2ListRepliesByPostId(postId);
            List<Reply> filtered = new ArrayList<>();

            for (Reply r : replies) {
                if (!r.isDeleted()) {
                    filtered.add(r);
                }
            }

            return filtered;
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Updates a reply body.
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

        try {
            boolean updated = database.hw2UpdateReply(replyId, newBody.trim());
            if (!updated) {
                return "Reply not found.";
            }
            return null;
        } catch (SQLException e) {
            return "Database error while updating reply.";
        }
    }

    /**
     * Deletes a reply (soft delete).
     */
    public String deleteReply(int replyId) {

        Reply reply = getReplyById(replyId);
        if (reply == null) {
            return "Reply not found.";
        }

        try {
            boolean deleted = database.hw2DeleteReplySoft(replyId);
            if (!deleted) {
                return "Reply not found.";
            }
            return null;
        } catch (SQLException e) {
            return "Database error while deleting reply.";
        }
    }

    /**
     * Searches replies (non-deleted).
     */
    public void searchReplies(String keyword) {

        subsetReplies.clear();

        try {
            List<Reply> results = database.hw2SearchReplies(keyword, null);

            for (Reply r : results) {
                if (!r.isDeleted()) {
                    subsetReplies.add(r);
                }
            }
        } catch (SQLException e) {
            // fail silently for UI
        }
    }

    /**
     * Returns all replies (rarely used).
     */
    public List<Reply> getAllReplies() {
        return Collections.unmodifiableList(allReplies);
    }

    /**
     * Returns subset replies.
     */
    public List<Reply> getSubsetReplies() {
        return Collections.unmodifiableList(subsetReplies);
    }
}