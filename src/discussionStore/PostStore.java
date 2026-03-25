package discussionStore;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import discussionValidation.PostValidator;
import entityClasses.Post;
import database.Database;

/**
 * The PostStore class manages discussion posts and supports operating on
 * subsets of posts (e.g., search results).
 *
 * This version is database-backed so posts persist across application relaunches.
 */
public class PostStore {

    /** Shared database reference */
    private final Database database;

    /** List containing all posts */
    private final List<Post> allPosts;

    /** List containing the current subset of posts (e.g., search results) */
    private final List<Post> subsetPosts;

    /**
     * Constructs a PostStore and loads posts from the database.
     *
     * @param database database connection/helper object
     */
    public PostStore(Database database) {
        this.database = database;
        this.allPosts = new ArrayList<>();
        this.subsetPosts = new ArrayList<>();
        refreshAllPostsFromDatabase();
        subsetPosts.addAll(allPosts);
    }

    /**
     * Reloads all posts from the database into memory.
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
     * Creates and stores a new post if inputs are valid.
     *
     * @param title post title
     * @param body post body
     * @param author post author
     * @param thread thread name (defaults to "General" if null/empty)
     * @return null if success; otherwise an error message
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
            subsetPosts.clear();
            subsetPosts.addAll(allPosts);
            return null;
        } catch (SQLException e) {
            return "Database error while creating post.";
        }
    }

    /**
     * Returns a post by ID.
     *
     * @param postId post ID
     * @return the Post if found; otherwise null
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
     * Updates an existing post's title/body/thread.
     *
     * @param postId post ID
     * @param newTitle new title
     * @param newBody new body
     * @return null if success; otherwise an error message
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
            subsetPosts.clear();
            subsetPosts.addAll(allPosts);
            return null;
        } catch (SQLException e) {
            return "Database error while updating post.";
        }
    }

    /**
     * Deletes a post by marking it as deleted (soft delete).
     * Replies remain stored and can detect deleted posts via this flag.
     *
     * @param postId post ID
     * @return null if success; otherwise an error message
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
            subsetPosts.clear();
            subsetPosts.addAll(allPosts);
            return null;
        } catch (SQLException e) {
            return "Database error while deleting post.";
        }
    }

    /**
     * Searches all posts by keyword and optional thread, then stores results in subsetPosts.
     *
     * Rules:
     * - keyword blank + thread blank => show all posts
     * - keyword blank + thread filled => show all posts in that thread
     * - keyword filled + thread blank => search title/body across all threads
     * - keyword filled + thread filled => search title/body within that thread
     *
     * @param keyword search keyword (may be null/blank)
     * @param thread thread filter (may be null/blank)
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
     * Returns an unmodifiable view of all posts.
     *
     * @return list of all posts
     */
    public List<Post> getAllPosts() {
        refreshAllPostsFromDatabase();
        return Collections.unmodifiableList(new ArrayList<>(allPosts));
    }

    /**
     * Returns an unmodifiable view of the current subset list.
     *
     * @return subset list of posts
     */
    public List<Post> getSubsetPosts() {
        return Collections.unmodifiableList(new ArrayList<>(subsetPosts));
    }
}