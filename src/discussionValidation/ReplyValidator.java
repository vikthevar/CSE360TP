package discussionValidation;

/**
 * The ReplyValidator class provides input validation for creating and updating
 * discussion replies.
 *
 * Validation rules:
 * - postId must be positive.
 * - Body is required.
 * - Body must be between 2 and 5000 characters (trimmed).
 */
public class ReplyValidator {

    /** Minimum allowed reply body length (after trimming) */
    public static final int MIN_BODY_LENGTH = 2;

    /** Maximum allowed reply body length (after trimming) */
    public static final int MAX_BODY_LENGTH = 5000;

    /**
     * Validates reply input for create/update operations.
     *
     * @param postId associated post ID
     * @param body reply body (may be null)
     * @return null if valid; otherwise a helpful error message
     */
    public static String validate(int postId, String body) {

        if (postId <= 0) {
            return "Invalid post ID.";
        }

        if (body == null || body.trim().isEmpty()) {
            return "Reply body is required.";
        }

        String trimmedBody = body.trim();

        if (trimmedBody.length() < MIN_BODY_LENGTH) {
            return "Reply body must be at least " + MIN_BODY_LENGTH + " characters.";
        }

        if (trimmedBody.length() > MAX_BODY_LENGTH) {
            return "Reply body must be at most " + MAX_BODY_LENGTH + " characters.";
        }

        return null;
    }
}