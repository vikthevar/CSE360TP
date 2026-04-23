package discussionStore;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import database.Database;
import discussionValidation.PostValidator;
import entityClasses.Post;

/**
 * The {@code PostStore} class manages discussion posts using a database-backed
 * persistence layer.
 *
 * <p>This store supports both full-post collection access and subset-based views
 * such as search results or moderation-specific filtered results. The class is
 * responsible for coordinating validation, database calls, and in-memory refresh
 * behavior so the rest of the discussion system can work with current post data.</p>
 *
 * <p>In addition to the original TP2 post CRUD behavior, this class also supports
 * TP3 moderation features such as flagging, hiding, and highlighting posts.</p>
 *
 * <p>Core responsibilities include:
 * <ul>
 *   <li>creating posts after validation</li>
 *   <li>reading posts by ID or as collections</li>
 *   <li>updating title/body content</li>
 *   <li>soft deleting posts</li>
 *   <li>searching posts into a subset view</li>
 *   <li>performing staff moderation actions</li>
 * </ul>
 * </p>
 *
 * @author Vikram Thevar
 */
public class PostStore {

    /** Shared database reference used for all post persistence operations. */
    private final Database database;

    /** In-memory cache of all posts currently loaded from the database. */
    private final List<Post> allPosts;

    /**
     * In-memory cache of the current subset of posts, such as search results
     * or a moderation-specific filtered list.
     */
    private final List<Post> subsetPosts;

    /**
     * Constructs a {@code PostStore} and loads the current post state from the database.
     *
     * @param database the shared database connection/helper object used by the discussion system
     */
    public PostStore(Database database) {
        this.database = database;
        this.allPosts = new ArrayList<>();
        this.subsetPosts = new ArrayList<>();
        refreshAllPostsFromDatabase();
        subsetPosts.addAll(allPosts);
    }

    /**
     * Reloads the full post collection from the database into the in-memory cache.
     *
     * <p>This method is used after create, update, delete, and moderation actions so
     * the store always reflects the latest persisted state.</p>
     *
     * @throws RuntimeException if the database load fails
     */
    private void refreshAllPostsFromDatabase() {
        allPosts.clear();
        try {
            allPosts.addAll(database.hw2ListPosts());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load posts from database.", e);
        }
    }

    /**
     * Rebuilds the subset list so it mirrors the full post collection.
     *
     * <p>This is used after mutations where the current subset should no longer reflect
     * an older search or filtered view.</p>
     */
    private void resetSubsetToAllPosts() {
        subsetPosts.clear();
        subsetPosts.addAll(allPosts);
    }

    /**
     * Creates and stores a new post if the supplied title and body pass validation.
     *
     * <p>The thread is normalized before persistence so posts without a supplied thread
     * are placed into the default General thread.</p>
     *
     * @param title the title of the post
     * @param body the body content of the post
     * @param author the username or identifier of the post author
     * @param thread the thread name; defaults to {@code General} when blank
     * @return {@code null} if the post is created successfully; otherwise a helpful error message
     */
    public String createPost(String title, String body, String author, String thread) {
        String validationMessage = PostValidator.validate(title, body);
        if (validationMessage != null) {
            return validationMessage;
        }

        String normalizedThread = PostValidator.normalizeThread(thread);

        try {
            database.hw2CreatePost(
                    title.trim(),
                    body.trim(),
                    (author == null ? "" : author.trim()),
                    normalizedThread
            );
            refreshAllPostsFromDatabase();
            resetSubsetToAllPosts();
            return null;
        } catch (SQLException e) {
            return "Database error while creating post.";
        }
    }

    /**
     * Returns a post by its unique identifier.
     *
     * <p>The in-memory cache is refreshed before searching so the caller receives
     * the latest version of the post currently stored in the database.</p>
     *
     * @param postId the post ID to search for
     * @return the matching {@code Post} if found; otherwise {@code null}
     */
    public Post getPostById(int postId) {
        refreshAllPostsFromDatabase();
        for (Post p : allPosts) {
            if (p.getPostId() == postId) {
                return p;
            }
        }
        return null;
    }

    /**
     * Updates the title and body of an existing post.
     *
     * <p>The thread is preserved from the existing post record. Validation is applied
     * before the update is sent to the database.</p>
     *
     * @param postId the post ID to update
     * @param newTitle the replacement title
     * @param newBody the replacement body content
     * @return {@code null} if the update succeeds; otherwise a helpful error message
     */
    public String updatePost(int postId, String newTitle, String newBody) {
        Post post = getPostById(postId);
        if (post == null) {
            return "Post not found.";
        }

        String validationMessage = PostValidator.validate(newTitle, newBody);
        if (validationMessage != null) {
            return validationMessage;
        }

        try {
            boolean updated = database.hw2UpdatePost(
                    postId,
                    newTitle.trim(),
                    newBody.trim(),
                    post.getThread()
            );

            if (!updated) {
                return "Post not found.";
            }

            refreshAllPostsFromDatabase();
            resetSubsetToAllPosts();
            return null;
        } catch (SQLException e) {
            return "Database error while updating post.";
        }
    }

    /**
     * Soft deletes a post by marking it as deleted in persistent storage.
     *
     * <p>The row remains in the database so reply handling and historical reference
     * behavior can still detect that the original post existed but was deleted.</p>
     *
     * @param postId the post ID to delete
     * @return {@code null} if the delete succeeds; otherwise a helpful error message
     */
    public String deletePost(int postId) {
        Post post = getPostById(postId);
        if (post == null) {
            return "Post not found.";
        }

        try {
            boolean deleted = database.hw2SoftDeletePost(postId);
            if (!deleted) {
                return "Post not found.";
            }

            refreshAllPostsFromDatabase();
            resetSubsetToAllPosts();
            return null;
        } catch (SQLException e) {
            return "Database error while deleting post.";
        }
    }

    /**
     * Flags a post for staff moderation review.
     *
     * <p>A non-empty flag reason is required so the moderation action remains explainable
     * and useful when reviewed later by staff or shown during testing/documentation.</p>
     *
     * @param postId the post ID to flag
     * @param reason the staff-provided reason for flagging the post
     * @param staffUser the username of the staff member performing the action
     * @return {@code null} if the flag action succeeds; otherwise a helpful error message
     */
    public String flagPost(int postId, String reason, String staffUser) {
        Post post = getPostById(postId);
        if (post == null) {
            return "Post not found.";
        }

        String validationMessage = PostValidator.validateFlagReason(reason);
        if (validationMessage != null) {
            return validationMessage;
        }

        try {
            boolean updated = database.hw2FlagPost(postId, reason, staffUser);
            if (!updated) {
                return "Post not found.";
            }

            refreshAllPostsFromDatabase();
            resetSubsetToAllPosts();
            return null;
        } catch (SQLException e) {
            return "Database error while flagging post.";
        }
    }

    /**
     * Removes the flagged state from a post and clears related moderation metadata.
     *
     * @param postId the post ID to unflag
     * @return {@code null} if the unflag action succeeds; otherwise a helpful error message
     */
    public String unflagPost(int postId) {
        Post post = getPostById(postId);
        if (post == null) {
            return "Post not found.";
        }

        try {
            boolean updated = database.hw2UnflagPost(postId);
            if (!updated) {
                return "Post not found.";
            }

            refreshAllPostsFromDatabase();
            resetSubsetToAllPosts();
            return null;
        } catch (SQLException e) {
            return "Database error while unflagging post.";
        }
    }

    /**
     * Hides a post from normal student-facing visibility while preserving it for staff review.
     *
     * @param postId the post ID to hide
     * @return {@code null} if the hide action succeeds; otherwise a helpful error message
     */
    public String hidePost(int postId) {
        Post post = getPostById(postId);
        if (post == null) {
            return "Post not found.";
        }

        try {
            boolean updated = database.hw2HidePost(postId);
            if (!updated) {
                return "Post not found.";
            }

            refreshAllPostsFromDatabase();
            resetSubsetToAllPosts();
            return null;
        } catch (SQLException e) {
            return "Database error while hiding post.";
        }
    }

    /**
     * Removes the hidden state from a post.
     *
     * @param postId the post ID to unhide
     * @return {@code null} if the unhide action succeeds; otherwise a helpful error message
     */
    public String unhidePost(int postId) {
        Post post = getPostById(postId);
        if (post == null) {
            return "Post not found.";
        }

        try {
            boolean updated = database.hw2UnhidePost(postId);
            if (!updated) {
                return "Post not found.";
            }

            refreshAllPostsFromDatabase();
            resetSubsetToAllPosts();
            return null;
        } catch (SQLException e) {
            return "Database error while unhiding post.";
        }
    }

    /**
     * Marks a post as highlighted to recognize a strong contribution.
     *
     * @param postId the post ID to highlight
     * @return {@code null} if the highlight action succeeds; otherwise a helpful error message
     */
    public String highlightPost(int postId) {
        Post post = getPostById(postId);
        if (post == null) {
            return "Post not found.";
        }

        try {
            boolean updated = database.hw2HighlightPost(postId);
            if (!updated) {
                return "Post not found.";
            }

            refreshAllPostsFromDatabase();
            resetSubsetToAllPosts();
            return null;
        } catch (SQLException e) {
            return "Database error while highlighting post.";
        }
    }

    /**
     * Removes the highlighted state from a post.
     *
     * @param postId the post ID whose highlight should be removed
     * @return {@code null} if the removal succeeds; otherwise a helpful error message
     */
    public String removeHighlightPost(int postId) {
        Post post = getPostById(postId);
        if (post == null) {
            return "Post not found.";
        }

        try {
            boolean updated = database.hw2RemoveHighlightPost(postId);
            if (!updated) {
                return "Post not found.";
            }

            refreshAllPostsFromDatabase();
            resetSubsetToAllPosts();
            return null;
        } catch (SQLException e) {
            return "Database error while removing highlight.";
        }
    }

    /**
     * Loads all currently flagged posts from the database.
     *
     * <p>This method is used for moderation views where staff need to review flagged
     * content separately from the normal full or searched post lists.</p>
     *
     * @return a list of flagged posts
     * @throws RuntimeException if the database load fails
     */
    public List<Post> getFlaggedPosts() {
        try {
            return database.hw2GetFlaggedPosts();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load flagged posts from database.", e);
        }
    }

    /**
     * Searches posts by keyword and optional thread, then stores the matching results
     * in the subset cache.
     *
     * <p>Search behavior:
     * <ul>
     *   <li>blank keyword + blank thread => all posts</li>
     *   <li>blank keyword + thread => all posts in that thread</li>
     *   <li>keyword + blank thread => keyword search across all threads</li>
     *   <li>keyword + thread => keyword search within that thread</li>
     * </ul>
     * </p>
     *
     * @param keyword the search keyword; may be {@code null} or blank
     * @param thread the thread filter; may be {@code null} or blank
     */
    public void searchPosts(String keyword, String thread) {
        subsetPosts.clear();

        try {
            subsetPosts.addAll(database.hw2SearchPosts(keyword, thread));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to search posts from database.", e);
        }
    }

    /**
     * Returns an unmodifiable view of all posts currently stored in the database.
     *
     * <p>The underlying cache is refreshed before the collection is returned so the
     * caller receives the most recent stored state.</p>
     *
     * @return an unmodifiable list containing all posts
     */
    public List<Post> getAllPosts() {
        refreshAllPostsFromDatabase();
        return Collections.unmodifiableList(new ArrayList<>(allPosts));
    }

    /**
     * Returns an unmodifiable view of the current subset of posts.
     *
     * <p>This subset may represent search results, flagged-only content loaded into
     * a view, or a reset copy of the full list depending on the most recent action.</p>
     *
     * @return an unmodifiable list containing the current subset view
     */
    public List<Post> getSubsetPosts() {
        return Collections.unmodifiableList(new ArrayList<>(subsetPosts));
    }
}