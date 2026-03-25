package entityClasses;

import java.time.LocalDateTime;

/**
 * The Reply class represents a reply to a discussion post.
 * A reply is associated with a specific post and contains
 * content, author information, and a timestamp.
 *
 * This class supports basic data storage for CRUD operations.
 */
public class Reply {

    /** Unique identifier for the reply */
    private int replyId;

    /** Identifier of the post this reply belongs to */
    private int postId;

    /** Body content of the reply */
    private String body;

    /** Author username or identifier */
    private String author;

    /** Timestamp of when the reply was created */
    private LocalDateTime createdTimestamp;
    
    private boolean isDeleted;

    /**
     * Constructs a new Reply object.
     *
     * @param replyId unique identifier for the reply
     * @param postId identifier of the post being replied to
     * @param body body content of the reply
     * @param author author of the reply
     */
    public Reply(int replyId, int postId, String body, String author) {
        this.replyId = replyId;
        this.postId = postId;
        this.body = body;
        this.author = author;
        this.createdTimestamp = LocalDateTime.now();
        this.isDeleted = false;   
    }

    /**
     * Returns the reply ID.
     *
     * @return reply ID
     */
    public int getReplyId() {
        return replyId;
    }

    /**
     * Returns the associated post ID.
     *
     * @return post ID
     */
    public int getPostId() {
        return postId;
    }

    /**
     * Returns the body of the reply.
     *
     * @return reply body
     */
    public String getBody() {
        return body;
    }

    /**
     * Sets the body of the reply.
     *
     * @param body - new reply content
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Returns the author of the reply.
     *
     * @return author identifier
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Returns the timestamp when the reply was created.
     *
     * @return creation timestamp
     */
    public LocalDateTime getCreatedTimestamp() {
        return createdTimestamp;
    }
    
    public void setDeleted(boolean deleted) {
        this.isDeleted = deleted;
    }
    public boolean isDeleted() {
        return isDeleted;
    }
}