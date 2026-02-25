package discussionValidation;

/**
 * The PostValidator class provides input validation for creating and updating
 * discussion posts.
 *
 * Validation rules are explicit to ensure consistent behavior for both the UI
 * and automated tests:
 * - Title is required and must be between 5 and 100 characters (trimmed).
 * - Body is required and must be between 10 and 5000 characters (trimmed).
 * - Thread defaults to "General" if not provided.
 */
public class PostValidator {

    /** Minimum allowed title length (after trimming) */
    public static final int MIN_TITLE_LENGTH = 5;

    /** Maximum allowed title length (after trimming) */
    public static final int MAX_TITLE_LENGTH = 100;

    /** Minimum allowed body length (after trimming) */
    public static final int MIN_BODY_LENGTH = 10;

    /** Maximum allowed body length (after trimming) */
    public static final int MAX_BODY_LENGTH = 5000;

    /**
     * Validates title and body values for create/update operations.
     *
     * @param title post title (may be null)
     * @param body post body (may be null)
     * @return null if valid; otherwise a helpful error message
     */
    public static String validate(String title, String body) {

        if (title == null || title.trim().isEmpty()) {
            return "Title is required.";
        }

        String trimmedTitle = title.trim();
        if (trimmedTitle.length() < MIN_TITLE_LENGTH) {
            return "Title must be at least " + MIN_TITLE_LENGTH + " characters.";
        }
        if (trimmedTitle.length() > MAX_TITLE_LENGTH) {
            return "Title must be at most " + MAX_TITLE_LENGTH + " characters.";
        }

        if (body == null || body.trim().isEmpty()) {
            return "Body is required.";
        }

        String trimmedBody = body.trim();
        if (trimmedBody.length() < MIN_BODY_LENGTH) {
            return "Body must be at least " + MIN_BODY_LENGTH + " characters.";
        }
        if (trimmedBody.length() > MAX_BODY_LENGTH) {
            return "Body must be at most " + MAX_BODY_LENGTH + " characters.";
        }

        return null;
    }

    /**
     * Normalizes a thread name. If thread is null or empty, returns "General".
     *
     * @param thread thread name (may be null)
     * @return a non-empty thread name
     */
    public static String normalizeThread(String thread) {
        if (thread == null || thread.trim().isEmpty()) {
            return "General";
        }
        return thread.trim();
    }
}