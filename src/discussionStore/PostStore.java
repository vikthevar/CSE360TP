package discussionStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import discussionValidation.PostValidator;
import entityClasses.Post;

/**
 * The PostStore class stores all discussion posts and supports operating on
 * subsets of posts (e.g., search results).
 *
 * This class provides CRUD operations and input validation for posts.
 */
public class PostStore {

    /** List containing all posts */
    private final List<Post> allPosts;

    /** List containing the current subset of posts (e.g., search results) */
    private final List<Post> subsetPosts;

    /** Next ID used when creating posts */
    private int nextPostId;

    /**
     * Constructs an empty PostStore.
     */
    public PostStore() {
        this.allPosts = new ArrayList<>();
        this.subsetPosts = new ArrayList<>();
        this.nextPostId = 1;
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

        Post post = new Post(nextPostId, title.trim(), body.trim(),
                (author == null ? "" : author.trim()), normalizedThread);

        allPosts.add(post);
        nextPostId++;

        return null;
    }

    /**
     * Returns a post by ID.
     *
     * @param postId post ID
     * @return the Post if found; otherwise null
     */
    public Post getPostById(int postId) {
        for (Post p : allPosts) {
            if (p.getPostId() == postId) {
                return p;
            }
        }
        return null;
    }

    /**
     * Updates an existing post's title and/or body.
     *
     * @param postId post ID
     * @param newTitle new title (required)
     * @param newBody new body (required)
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

        post.setTitle(newTitle.trim());
        post.setBody(newBody.trim());
        return null;
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

        post.markDeleted();
        return null;
    }

    /**
     * Searches all posts by keyword and stores results in subsetPosts.
     * Search is case-insensitive and checks title + body.
     *
     * @param keyword search keyword (null/empty returns an empty subset)
     */
    public void searchPosts(String keyword) {
        subsetPosts.clear();

        if (keyword == null || keyword.trim().isEmpty()) {
            return;
        }

        String key = keyword.trim().toLowerCase();

        for (Post p : allPosts) {
            String title = (p.getTitle() == null) ? "" : p.getTitle().toLowerCase();
            String body = (p.getBody() == null) ? "" : p.getBody().toLowerCase();

            if (title.contains(key) || body.contains(key)) {
                subsetPosts.add(p);
            }
        }
    }

    /**
     * Returns an unmodifiable view of all posts.
     *
     * @return list of all posts
     */
    public List<Post> getAllPosts() {
        return Collections.unmodifiableList(allPosts);
    }

    /**
     * Returns an unmodifiable view of the current subset list.
     *
     * @return subset list of posts
     */
    public List<Post> getSubsetPosts() {
        return Collections.unmodifiableList(subsetPosts);
    }
}