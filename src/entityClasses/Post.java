package entityClasses;

import java.time.LocalDateTime;

/**
 * The Post class represents a discussion post created by a student.
 * A post contains identifying information, content, thread classification,
 * timestamp, and deletion state.
 *
 * This class supports basic data storage for CRUD operations.
 *
 * @author - Vikram Thevar
 */
public class Post {

    /** Unique identifier for the post */
    private int postId;

    /** Title of the post */
    private String title;

    /** Body content of the post */
    private String body;

    /** Author username or identifier */
    private String author;

    /** Thread name the post belongs to */
    private String thread;

    /** Timestamp of when the post was created */
    private LocalDateTime createdTimestamp;

    /** Indicates whether the post has been deleted */
    private boolean isDeleted;

    /**
     * Constructs a new Post object.
     *
     * @param postId - unique identifier for post
     * @param title - title of the post
     * @param body - body content of the post
     * @param author - author of the post
     * @param thread - thread name (defaults to "General" if null)
     */
    public Post(int postId, String title, String body, String author, String thread) {
        this.postId = postId;
        this.title = title;
        this.body = body;
        this.author = author;
        this.thread = (thread == null || thread.isEmpty()) ? "General" : thread;
        this.createdTimestamp = LocalDateTime.now();
        this.isDeleted = false;
    }

    /**
     * Returns the post ID.
     *
     * @return post ID
     */
    public int getPostId() {
        return postId;
    }

    /**
     * Returns the title of the post.
     *
     * @return post title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the post.
     *
     * @param title - new title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the body of the post.
     *
     * @return post body
     */
    public String getBody() {
        return body;
    }

    /**
     * Sets the body of the post.
     *
     * @param body - new body content
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Returns the author of the post.
     *
     * @return author identifier
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Returns the thread name.
     *
     * @return thread name
     */
    public String getThread() {
        return thread;
    }

    /**
     * Sets the thread name.
     *
     * @param thread - new thread name
     */
    public void setThread(String thread) {
        this.thread = thread;
    }

    /**
     * Returns the timestamp when the post was created.
     *
     * @return creation timestamp
     */
    public LocalDateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    /**
     * Returns whether the post has been deleted.
     *
     * @return true if deleted, false otherwise
     */
    public boolean isDeleted() {
        return isDeleted;
    }

    /**
     * Marks the post as deleted.
     * The post content remains stored, but is flagged as deleted.
     */
    public void markDeleted() {
        this.isDeleted = true;
    }
    
    /**
     * Sets the deletion state of the post.
     *
     * @param deleted true if the post should be marked deleted; false otherwise
     */
    public void setDeleted(boolean deleted) {
        this.isDeleted = deleted;
    }
}