package guiDiscussion;

import discussionStore.PostStore;
import discussionStore.ReplyStore;
import entityClasses.Post;
import java.util.List;
import entityClasses.Reply;

/**
 * <p> Title: ControllerDiscussion Class. </p>
 *
 * <p> Description: The Java/FX-based Discussion controller. This class provides
 * the controller actions based on the user's use of the JavaFX GUI widgets
 * defined by the View class. </p>
 *
 * @author Vikram Thevar
 *
 */
public class ControllerDiscussion {
	
	public static String testCreateReply(int postId, String body, String author) {
	    Post p = getPostStore().getPostById(postId);
	    if (p == null) return "Post does not exist.";
	    return getReplyStore().createReply(postId, body, author);
	}
	
    /** Default constructor is not used. */
    public ControllerDiscussion() { 
    	
    }

    /** Stores used by the discussion feature */
    private static final PostStore postStore = new PostStore();
    private static final ReplyStore replyStore = new ReplyStore();

    /**
     * Creates a post using inputs from the View.
     */
    protected static void performCreatePost() {
        String title = ViewDiscussion.text_PostTitle.getText();
        String body = ViewDiscussion.text_PostBody.getText();
        String author = ViewDiscussion.text_PostAuthor.getText();
        String thread = ViewDiscussion.text_PostThread.getText();

        String err = postStore.createPost(title, body, author, thread);
        if (err != null) {
            ViewDiscussion.alertError.setContentText(err);
            ViewDiscussion.alertError.showAndWait();
            return;
        }

        ViewDiscussion.alertInfo.setContentText("Post created successfully.");
        ViewDiscussion.alertInfo.showAndWait();

        // Clear fields
        ViewDiscussion.text_PostTitle.setText("");
        ViewDiscussion.text_PostBody.setText("");
        ViewDiscussion.text_PostThread.setText("");
    }

    /**
     * Searches posts and updates the subset list.
     */
    protected static void performSearchPosts() {
        String keyword = ViewDiscussion.text_SearchPosts.getText();
        postStore.searchPosts(keyword);
        ViewDiscussion.updatePostListDisplays();
    }

    /**
     * Deletes a post using a postId from the View.
     */
    protected static void performDeletePost() {
        String raw = ViewDiscussion.text_DeletePostId.getText();

        int postId;
        try {
            postId = Integer.parseInt(raw.trim());
        } catch (Exception e) {
            ViewDiscussion.alertError.setContentText("Invalid post ID.");
            ViewDiscussion.alertError.showAndWait();
            return;
        }

        String err = postStore.deletePost(postId);
        if (err != null) {
            ViewDiscussion.alertError.setContentText(err);
            ViewDiscussion.alertError.showAndWait();
            return;
        }

        ViewDiscussion.alertInfo.setContentText("Post deleted.");
        ViewDiscussion.alertInfo.showAndWait();
        ViewDiscussion.updatePostListDisplays();
    }

    /**
     * Creates a reply. Post existence check is enforced here.
     */
    protected static void performCreateReply() {
        try {
            int postId = Integer.parseInt(ViewDiscussion.text_ReplyPostId.getText().trim());
            String body = ViewDiscussion.text_ReplyBody.getText().trim();
            String author = ViewDiscussion.text_ReplyAuthor.getText().trim();

            if (body.isEmpty()) {
                ViewDiscussion.alertError.setContentText("Reply body cannot be empty.");
                ViewDiscussion.alertError.showAndWait();
                return;
            }
            if (author.isEmpty()) {
                ViewDiscussion.alertError.setContentText("Reply author cannot be empty.");
                ViewDiscussion.alertError.showAndWait();
                return;
            }

            Post p = getPostStore().getPostById(postId);
            if (p == null) {
                ViewDiscussion.alertError.setContentText("Post ID does not exist.");
                ViewDiscussion.alertError.showAndWait();
                return;
            }

            String err = getReplyStore().createReply(postId, body, author);
            if (err != null) {
                ViewDiscussion.alertError.setContentText(err);
                ViewDiscussion.alertError.showAndWait();
                return;
            }

            ViewDiscussion.alertInfo.setContentText("Reply created successfully.");
            ViewDiscussion.alertInfo.showAndWait();

            // refresh replies display immediately
            List<Reply> replies = getReplyStore().getRepliesByPostId(postId);

            StringBuilder sb = new StringBuilder();
            if (replies.isEmpty()) {
                sb.append("No replies found.\n");
            } else {
                for (Reply r : replies) {
                    sb.append("Reply ").append(r.getReplyId()).append(" (Post ").append(r.getPostId()).append(")\n");
                    sb.append("Author: ").append(r.getAuthor()).append("\n");
                    if (p.isDeleted()) sb.append("Original post has been deleted.\n");
                    sb.append(r.getBody()).append("\n");
                    sb.append("----\n");
                }
            }
            ViewDiscussion.area_RepliesForPost.setText(sb.toString());

            // clear reply inputs
            ViewDiscussion.text_ReplyBody.setText("");
            ViewDiscussion.text_ReplyAuthor.setText("");

        } catch (NumberFormatException ex) {
            ViewDiscussion.alertError.setContentText("Invalid Post ID.");
            ViewDiscussion.alertError.showAndWait();
        } catch (Exception ex) {
            ViewDiscussion.alertError.setContentText("Could not create reply: " + ex.getMessage());
            ViewDiscussion.alertError.showAndWait();
        }
    }
    
    
    /**
     * Provides View access to the PostStore.
     *
     * @return postStore
     */
    protected static PostStore getPostStore() {
        return postStore;
    }

    /**
     * Provides View access to the ReplyStore.
     *
     * @return replyStore
     */
    protected static ReplyStore getReplyStore() {
        return replyStore;
    }
}