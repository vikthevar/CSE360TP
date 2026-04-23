package entityClasses;

import java.time.LocalDateTime;

/**
 * The Post class represents a studen created discussion post in the TP2
 * discussion system. A Post object stores the core information needed to
 * support student interactions, including, identity, ownership, content,
 * discussion grouping, creation time, and deletion state.
 *
 * <p>This class supports the data and operations needed for the CRUD behaviors
 * required by the Students User Stories:
 * create a post, read a post, update a post, and delete a post.</p>
 *
 * <p>CRUD mapping in this class:
 * <ul>
 *   <li><b>Create</b>: constructor initializes a new post</li>
 *   <li><b>Read</b>: getter methods expose stored post data</li>
 *   <li><b>Update</b>: setter methods allow editable fields to change</li>
 *   <li><b>Delete</b>: soft deletion through the deletion methods</li>
 * </ul>
 * 
 *
 * <p>This implementation uses soft deletion rather than physical removal.
 * A deleted post remains stored but is marked as deleted. This design supports
 * safer controller behavior and preserves information that may later be useful
 * for audit, testing, moderation, or staff/admin analysis.</p>
 *
 * <p>If a thread name is not supplied, the post is automatically assigned to
 * the General thread so that the post remains categorized and visible
 * to the rest of the system.</p>
 *
 * <p>This class focuses on representing and updating post data. Advanced input
 * validation rules, permissions, filtering logic, and GUI interaction behavior
 * are expected to be handled by controller, test, and interface code that uses
 * this class.</p>
 *
 * @author Vikram Thevar
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
    
    /** Indicates whether the post has been flagged by staff */
    private boolean isFlagged;

    /** Indicates whether the post has been hidden from normal student view */
    private boolean isHidden;

    /** Indicates whether the post has been highlighted as high quality */
    private boolean isHighlighted;

    /** Optional staff-provided reason for flagging */
    private String flagReason;

    /** Username of the staff member who flagged the post */
    private String flaggedBy;

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
        this.isFlagged = false;
        this.isHidden = false;
        this.isHighlighted = false;
        this.flagReason = "";
        this.flaggedBy = "";
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
    
    public boolean isFlagged() { return isFlagged; }
    public void setFlagged(boolean flagged) { this.isFlagged = flagged; }

    public boolean isHidden() { return isHidden; }
    public void setHidden(boolean hidden) { this.isHidden = hidden; }

    public boolean isHighlighted() { return isHighlighted; }
    public void setHighlighted(boolean highlighted) { this.isHighlighted = highlighted; }

    public String getFlagReason() { return flagReason; }
    public void setFlagReason(String flagReason) { this.flagReason = flagReason; }

    public String getFlaggedBy() { return flaggedBy; }
    public void setFlaggedBy(String flaggedBy) { this.flaggedBy = flaggedBy; }
}