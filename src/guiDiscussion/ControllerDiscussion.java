package guiDiscussion;

import java.util.List;

import guiDiscussion.ModelDiscussion.Post;
import guiDiscussion.ModelDiscussion.Reply;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/*******
 * <p> Title: ControllerDiscussion Class. </p>
 *
 * <p> Description: Controller for the standalone discussion CRUD demonstration.
 * This class connects the View and the Model and performs simple input
 * validation.</p>
 */
public class ControllerDiscussion {

	private static Alert alert = new Alert(AlertType.INFORMATION);

	// Navigation

	public static void showHome() {
		ViewDiscussion.displayHome(ViewDiscussion.theStage);
	}

	public static void showPostManager() {
		ViewDiscussion.showPostScene();
	}

	public static void showReplyManager() {
		ViewDiscussion.showReplyScene();
	}

	public static void performQuit() {
		Platform.exit();
	}

	// Post actions

	public static void performCreatePost() {
		String title = ViewDiscussion.text_PostTitle.getText().trim();
		String body = ViewDiscussion.text_PostBody.getText().trim();

		String error = validatePostFields(title, body);
		if (!error.isEmpty()) {
			showMessage("Invalid Post", error);
			return;
		}

		ModelDiscussion.createPost(title, body, "anonymous", "", false);
		clearPostFields();
		ViewDiscussion.refreshPostList(ModelDiscussion.getAllPosts());
		showMessage("Post Created", "The post was created successfully.");
	}

	public static void performUpdatePost() {
		int id = parseInt(ViewDiscussion.text_PostId.getText());
		if (id <= 0) {
			showMessage("Invalid ID", "Enter a valid numeric Post ID to update.");
			return;
		}

		Post p = ModelDiscussion.getPostById(id);
		if (p == null) {
			showMessage("Not Found", "No post exists with id " + id + ".");
			return;
		}

		String title = ViewDiscussion.text_PostTitle.getText().trim();
		String body = ViewDiscussion.text_PostBody.getText().trim();

		String error = validatePostFields(title, body);
		if (!error.isEmpty()) {
			showMessage("Invalid Post", error);
			return;
		}

		p.setTitle(title);
		p.setBody(body);

		ViewDiscussion.refreshPostList(ModelDiscussion.getAllPosts());
		showMessage("Post Updated", "Post #" + id + " was updated.");
	}

	public static void performDeletePost() {
		int id = parseInt(ViewDiscussion.text_PostId.getText());
		if (id <= 0) {
			showMessage("Invalid ID", "Enter a valid numeric Post ID to delete.");
			return;
		}

		boolean removed = ModelDiscussion.deletePost(id);
		if (!removed) {
			showMessage("Not Found", "No post exists with id " + id + ".");
			return;
		}

		ViewDiscussion.refreshPostList(ModelDiscussion.getAllPosts());
		showMessage("Post Deleted", "Post #" + id + " and its replies were deleted.");
		clearPostFields();
	}

	public static void performClearPostFields() {
		clearPostFields();
	}

	public static void performSearchPosts() {
		String keyword = ViewDiscussion.text_PostSearch.getText().trim();
		List<Post> subset = ModelDiscussion.searchPostsByKeyword(keyword);
		ViewDiscussion.refreshPostList(subset);
	}

	public static void performShowAllPosts() {
		ViewDiscussion.refreshPostList(ModelDiscussion.getAllPosts());
	}

	// Reply actions

	public static void performCreateReply() {
		int postId = parseInt(ViewDiscussion.text_ReplyPostId.getText());
		if (postId <= 0) {
			showMessage("Invalid Post ID", "Enter a valid numeric Post ID for the reply.");
			return;
		}

		Post targetPost = ModelDiscussion.getPostById(postId);
		if (targetPost == null) {
			showMessage("Post Not Found", "No post exists with id " + postId + ".");
			return;
		}

		String author = ViewDiscussion.text_ReplyAuthor.getText().trim();
		String body = ViewDiscussion.text_ReplyBody.getText().trim();
		boolean instructor = ViewDiscussion.check_ReplyInstructor.isSelected();
		boolean endorsed = ViewDiscussion.check_ReplyEndorsed.isSelected();

		String error = validateReplyFields(author, body);
		if (!error.isEmpty()) {
			showMessage("Invalid Reply", error);
			return;
		}

		ModelDiscussion.createReply(postId, body, author, instructor, endorsed);
		clearReplyFields();
		ViewDiscussion.refreshReplyList(ModelDiscussion.getAllReplies());
		showMessage("Reply Created", "The reply was created successfully for post #" + postId + ".");
	}

	public static void performUpdateReply() {
		int replyId = parseInt(ViewDiscussion.text_ReplyId.getText());
		if (replyId <= 0) {
			showMessage("Invalid Reply ID", "Enter a valid numeric Reply ID to update.");
			return;
		}

		Reply r = ModelDiscussion.getReplyById(replyId);
		if (r == null) {
			showMessage("Not Found", "No reply exists with id " + replyId + ".");
			return;
		}

		int postId = parseInt(ViewDiscussion.text_ReplyPostId.getText());
		if (postId <= 0) {
			showMessage("Invalid Post ID", "Enter a valid numeric Post ID for the reply.");
			return;
		}

		Post targetPost = ModelDiscussion.getPostById(postId);
		if (targetPost == null) {
			showMessage("Post Not Found", "No post exists with id " + postId + ".");
			return;
		}

		String author = ViewDiscussion.text_ReplyAuthor.getText().trim();
		String body = ViewDiscussion.text_ReplyBody.getText().trim();
		boolean instructor = ViewDiscussion.check_ReplyInstructor.isSelected();
		boolean endorsed = ViewDiscussion.check_ReplyEndorsed.isSelected();

		String error = validateReplyFields(author, body);
		if (!error.isEmpty()) {
			showMessage("Invalid Reply", error);
			return;
		}

		r.setPostId(postId);
		r.setAuthorName(author);
		r.setBody(body);
		r.setInstructorAnswer(instructor);
		r.setEndorsed(endorsed);

		ViewDiscussion.refreshReplyList(ModelDiscussion.getAllReplies());
		showMessage("Reply Updated", "Reply #" + replyId + " was updated.");
	}

	public static void performDeleteReply() {
		int replyId = parseInt(ViewDiscussion.text_ReplyId.getText());
		if (replyId <= 0) {
			showMessage("Invalid Reply ID", "Enter a valid numeric Reply ID to delete.");
			return;
		}

		boolean removed = ModelDiscussion.deleteReply(replyId);
		if (!removed) {
			showMessage("Not Found", "No reply exists with id " + replyId + ".");
			return;
		}

		ViewDiscussion.refreshReplyList(ModelDiscussion.getAllReplies());
		showMessage("Reply Deleted", "Reply #" + replyId + " was deleted.");
		clearReplyFields();
	}

	public static void performClearReplyFields() {
		clearReplyFields();
	}

	public static void performSearchReplies() {
		String keyword = ViewDiscussion.text_ReplySearch.getText().trim();
		List<Reply> subset = ModelDiscussion.searchRepliesByKeyword(keyword);
		ViewDiscussion.refreshReplyList(subset);
	}

	public static void performShowAllReplies() {
		ViewDiscussion.refreshReplyList(ModelDiscussion.getAllReplies());
	}

	// Helpers

	private static void clearPostFields() {
		ViewDiscussion.text_PostId.setText("");
		ViewDiscussion.text_PostTitle.setText("");
		ViewDiscussion.text_PostBody.setText("");
		ViewDiscussion.text_PostSearch.setText("");
	}

	private static void clearReplyFields() {
		ViewDiscussion.text_ReplyId.setText("");
		ViewDiscussion.text_ReplyPostId.setText("");
		ViewDiscussion.text_ReplyAuthor.setText("");
		ViewDiscussion.text_ReplyBody.setText("");
		ViewDiscussion.text_ReplySearch.setText("");
		ViewDiscussion.check_ReplyInstructor.setSelected(false);
		ViewDiscussion.check_ReplyEndorsed.setSelected(false);
	}

	private static int parseInt(String text) {
		if (text == null || text.trim().isEmpty()) {
			return -1;
		}
		try {
			return Integer.parseInt(text.trim());
		} catch (NumberFormatException ex) {
			return -1;
		}
	}

	private static String validatePostFields(String title, String body) {
		StringBuilder sb = new StringBuilder();
		if (title.isEmpty()) {
			sb.append("Title is required.\n");
		}
		if (body.isEmpty()) {
			sb.append("Body is required.\n");
		}
		if (title.length() > 100) {
			sb.append("Title must be at most 100 characters.\n");
		}
		return sb.toString().trim();
	}

	private static String validateReplyFields(String author, String body) {
		StringBuilder sb = new StringBuilder();
		if (author.isEmpty()) {
			sb.append("Author is required.\n");
		}
		if (body.isEmpty()) {
			sb.append("Body is required.\n");
		}
		if (author.length() > 40) {
			sb.append("Author must be at most 40 characters.\n");
		}
		return sb.toString().trim();
	}

	private static void showMessage(String header, String message) {
		alert.setTitle("Discussion CRUD");
		alert.setHeaderText(header);
		alert.setContentText(message);
		alert.showAndWait();
	}
}

