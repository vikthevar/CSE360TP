package guiDiscussion;

import java.util.List;

import discussionStore.ThreadLifecycleService;
import discussionStore.ThreadStatus;
import entityClasses.Post;
import entityClasses.Reply;
import entityClasses.User;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/*******
 * <p> Title: ViewDiscussion Class. </p>
 * 
 * <p> Description: The Java/FX-based HW2 Discussion Page. This class provides the
 * JavaFX GUI widgets used to demonstrate CRUD and input validation for Posts and Replies,
 * including subset list results. Status feedback is shown inline on the page.</p>
 * 
 * @author Vikram Thevar
 *
 */
public class ViewDiscussion {

	/*-*******************************************************************************************

	Attributes
	
	*/

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT + 140;

	// GUI Area 1: Title
	protected static Label label_PageTitle = new Label("HW2 Discussion Page");

	// Inline status label
	protected static Label label_Status = new Label("");

	// GUI Area: Thread Lifecycle (Admin Only)
	protected static Label label_ThreadSection = new Label("Thread Lifecycle Management (Admin)");
	protected static Label label_ThreadIdInput = new Label("Thread ID:");
	protected static TextField text_ThreadIdInput = new TextField();
	protected static Button button_LockThread = new Button("Lock Thread");
	protected static Button button_OpenThread = new Button("Open Thread");
	protected static Button button_ArchiveThread = new Button("Archive Thread");
	protected static Label label_ThreadStatus = new Label("Status: OPEN");

	// Separators
	private static Line line_Separator1 = new Line(40, 60, width - 40, 60);
	private static Line line_Separator2 = new Line(40, 390, width - 40, 390);
	private static Line line_Separator3 = new Line(40, 645, width - 40, 645);
	
	// GUI Area 2: Post CRUD + Search
	protected static Label label_PostSection = new Label("Posts");

	protected static Label label_PostTitle = new Label("Title:");
	protected static TextField text_PostTitle = new TextField();

	protected static Label label_PostBody = new Label("Body:");
	protected static TextField text_PostBody = new TextField();

	protected static Label label_PostAuthor = new Label("Author:");
	protected static TextField text_PostAuthor = new TextField();

	protected static Label label_PostThread = new Label("Thread (optional):");
	protected static TextField text_PostThread = new TextField();

	protected static Button button_CreatePost = new Button("Create Post");

	protected static Label label_SearchPosts = new Label("Search Keyword:");
	protected static TextField text_SearchPosts = new TextField();

	protected static Label label_SearchThread = new Label("Search Thread:");
	protected static TextField text_SearchThread = new TextField();

	protected static Button button_SearchPosts = new Button("Search");

	protected static Label label_DeletePost = new Label("Delete Post by ID:");
	protected static TextField text_DeletePostId = new TextField();
	protected static Button button_DeletePost = new Button("Delete Post");

	protected static Label label_UpdatePostId = new Label("Update Post ID:");
	protected static TextField text_UpdatePostId = new TextField();
	protected static Button button_UpdatePost = new Button("Update Post");

	// GUI Area 3: Post lists
	protected static Label label_AllPosts = new Label("All Posts:");
	protected static TextArea area_AllPosts = new TextArea();

	protected static Label label_SubsetPosts = new Label("Subset Posts (Search Results):");
	protected static TextArea area_SubsetPosts = new TextArea();

	// GUI Area 4: Reply CRUD + View
	protected static Label label_ReplySection = new Label("Replies");

	protected static Label label_ReplyPostId = new Label("Reply to Post ID:");
	protected static TextField text_ReplyPostId = new TextField();

	protected static Label label_ReplyBody = new Label("Reply Body:");
	protected static TextField text_ReplyBody = new TextField();

	protected static Label label_ReplyAuthor = new Label("Reply Author:");
	protected static TextField text_ReplyAuthor = new TextField();

	protected static Button button_CreateReply = new Button("Create Reply");

	protected static Label label_ViewRepliesForPost = new Label("View Replies for Post ID:");
	protected static TextField text_ViewRepliesPostId = new TextField();
	protected static Button button_ViewReplies = new Button("View Replies");

	protected static Label label_DeleteReply = new Label("Delete Reply ID:");
	protected static TextField text_DeleteReplyId = new TextField();
	protected static Button button_DeleteReply = new Button("Delete Reply");

	protected static Label label_UpdateReplyId = new Label("Update Reply ID:");
	protected static TextField text_UpdateReplyId = new TextField();
	protected static Button button_UpdateReply = new Button("Update Reply");

	protected static TextArea area_RepliesForPost = new TextArea();

	// Page configuration
	private static ViewDiscussion theView;
	private static User theUser;  // The current logged-in user

	protected static Stage theStage;
	private static Pane theRootPane;
	private static Scene theDiscussionScene;

	/*-*******************************************************************************************

	Constructors
	
	*/

	public static void displayDiscussion(Stage ps, User user) {

		theStage = ps;
		theUser = user;

		if (theView == null) theView = new ViewDiscussion();

		// Show/hide admin lifecycle controls based on role
		boolean isAdmin = (user != null && user.getAdminRole());
		label_ThreadSection.setVisible(isAdmin);
		label_ThreadIdInput.setVisible(isAdmin);
		text_ThreadIdInput.setVisible(isAdmin);
		button_LockThread.setVisible(isAdmin);
		button_OpenThread.setVisible(isAdmin);
		button_ArchiveThread.setVisible(isAdmin);
		label_ThreadStatus.setVisible(isAdmin);

		updatePostListDisplays();
		area_RepliesForPost.setText("");
		setStatus("Discussion page ready.", false);

		theStage.setTitle("CSE 360: Discussion");
		theStage.setScene(theDiscussionScene);
		theStage.show();
	}

	private ViewDiscussion() {

		theRootPane = new Pane();
		theDiscussionScene = new Scene(theRootPane, width, height);

		// GUI Area 1: Title
		setupLabelUI(label_PageTitle, "Arial", 26, width, Pos.CENTER, 0, 10);

		// Status label — sits ABOVE the separator so it's never covered
		setupLabelUI(label_Status, "Arial", 13, width - 80, Pos.BASELINE_LEFT, 40, 40);
		label_Status.setTextFill(Color.DARKGREEN);

		// GUI Area: Thread Lifecycle Admin Controls (below separator at y=60)
		setupLabelUI(label_ThreadSection, "Arial", 15, 400, Pos.BASELINE_LEFT, 40, 68);
		label_ThreadSection.setTextFill(Color.DARKBLUE);

		setupLabelUI(label_ThreadIdInput, "Arial", 14, 80, Pos.BASELINE_LEFT, 40, 100);
		setupTextUI(text_ThreadIdInput, "Arial", 14, 180, Pos.BASELINE_LEFT, 120, 95, true);
		text_ThreadIdInput.setText("Assignment1");

		setupButtonUI(button_LockThread, "Dialog", 13, 130, Pos.CENTER, 320, 95);
		button_LockThread.setOnAction((_) -> performChangeThreadStatus(ThreadStatus.LOCKED));

		setupButtonUI(button_OpenThread, "Dialog", 13, 130, Pos.CENTER, 460, 95);
		button_OpenThread.setOnAction((_) -> performChangeThreadStatus(ThreadStatus.OPEN));

		setupButtonUI(button_ArchiveThread, "Dialog", 13, 130, Pos.CENTER, 600, 95);
		button_ArchiveThread.setOnAction((_) -> performChangeThreadStatus(ThreadStatus.ARCHIVED));

		setupLabelUI(label_ThreadStatus, "Arial", 13, 200, Pos.BASELINE_LEFT, 750, 100);
		label_ThreadStatus.setTextFill(Color.DARKBLUE);

		// GUI Area 2: Posts (shifted down to make room for lifecycle section)
		setupLabelUI(label_PostSection, "Arial", 20, 200, Pos.BASELINE_LEFT, 40, 130);

		setupLabelUI(label_PostTitle, "Arial", 14, 90, Pos.BASELINE_LEFT, 40, 135);
		setupTextUI(text_PostTitle, "Arial", 14, 360, Pos.BASELINE_LEFT, 150, 130, true);

		setupLabelUI(label_PostBody, "Arial", 14, 90, Pos.BASELINE_LEFT, 40, 175);
		setupTextUI(text_PostBody, "Arial", 14, 360, Pos.BASELINE_LEFT, 150, 170, true);

		setupLabelUI(label_PostAuthor, "Arial", 14, 90, Pos.BASELINE_LEFT, 40, 215);
		setupTextUI(text_PostAuthor, "Arial", 14, 360, Pos.BASELINE_LEFT, 150, 210, true);

		setupLabelUI(label_PostThread, "Arial", 14, 130, Pos.BASELINE_LEFT, 40, 255);
		setupTextUI(text_PostThread, "Arial", 14, 360, Pos.BASELINE_LEFT, 150, 250, true);

		setupButtonUI(button_CreatePost, "Dialog", 14, 150, Pos.CENTER, 560, 170);		
		button_CreatePost.setOnAction((_) -> performCreatePostUI());

		setupLabelUI(label_SearchPosts, "Arial", 14, 120, Pos.BASELINE_LEFT, 40, 300);
		setupTextUI(text_SearchPosts, "Arial", 14, 180, Pos.BASELINE_LEFT, 160, 295, true);

		setupLabelUI(label_SearchThread, "Arial", 14, 120, Pos.BASELINE_LEFT, 370, 300);
		setupTextUI(text_SearchThread, "Arial", 14, 180, Pos.BASELINE_LEFT, 500, 295, true);

		setupButtonUI(button_SearchPosts, "Dialog", 14, 100, Pos.CENTER, 710, 295);
		button_SearchPosts.setOnAction((_) -> performSearchPostsUI());

		setupLabelUI(label_DeletePost, "Arial", 14, 140, Pos.BASELINE_LEFT, 40, 335);
		setupTextUI(text_DeletePostId, "Arial", 14, 80, Pos.BASELINE_LEFT, 185, 330, true);
		setupButtonUI(button_DeletePost, "Dialog", 14, 130, Pos.CENTER, 285, 330);
		button_DeletePost.setOnAction((_) -> performDeletePostUI());

		setupLabelUI(label_UpdatePostId, "Arial", 14, 140, Pos.BASELINE_LEFT, 430, 335);
		setupTextUI(text_UpdatePostId, "Arial", 14, 80, Pos.BASELINE_LEFT, 575, 330, true);
		setupButtonUI(button_UpdatePost, "Dialog", 14, 130, Pos.CENTER, 675, 330);
		button_UpdatePost.setOnAction((_) -> performUpdatePostUI());

		// GUI Area 3: Post list displays
		setupLabelUI(label_AllPosts, "Arial", 14, 220, Pos.BASELINE_LEFT, 40, 405);
		setupTextAreaUI(area_AllPosts, "Arial", 12, 430, 200, 40, 430);

		setupLabelUI(label_SubsetPosts, "Arial", 14, 260, Pos.BASELINE_LEFT, 500, 405);
		setupTextAreaUI(area_SubsetPosts, "Arial", 12, 430, 200, 500, 430);

		// GUI Area 4: Replies
		setupLabelUI(label_ReplySection, "Arial", 20, 200, Pos.BASELINE_LEFT, 40, 660);

		setupLabelUI(label_ReplyPostId, "Arial", 14, 140, Pos.BASELINE_LEFT, 40, 700);
		setupTextUI(text_ReplyPostId, "Arial", 14, 90, Pos.BASELINE_LEFT, 170, 695, true);

		setupLabelUI(label_ReplyAuthor, "Arial", 14, 110, Pos.BASELINE_LEFT, 290, 700);
		setupTextUI(text_ReplyAuthor, "Arial", 14, 220, Pos.BASELINE_LEFT, 390, 695, true);

		setupLabelUI(label_ReplyBody, "Arial", 14, 90, Pos.BASELINE_LEFT, 40, 740);
		setupTextUI(text_ReplyBody, "Arial", 14, 520, Pos.BASELINE_LEFT, 170, 735, true);

		setupButtonUI(button_CreateReply, "Dialog", 14, 150, Pos.CENTER, 170, 775);
		button_CreateReply.setOnAction((_) -> performCreateReplyUI());

		setupLabelUI(label_ViewRepliesForPost, "Arial", 14, 170, Pos.BASELINE_LEFT, 360, 780);
		setupTextUI(text_ViewRepliesPostId, "Arial", 14, 90, Pos.BASELINE_LEFT, 535, 775, true);

		setupButtonUI(button_ViewReplies, "Dialog", 14, 150, Pos.CENTER, 645, 775);
		button_ViewReplies.setOnAction((_) -> performViewRepliesForPost());

		setupLabelUI(label_DeleteReply, "Arial", 14, 130, Pos.BASELINE_LEFT, 40, 815);
		setupTextUI(text_DeleteReplyId, "Arial", 14, 90, Pos.BASELINE_LEFT, 170, 810, true);

		setupButtonUI(button_DeleteReply, "Dialog", 14, 150, Pos.CENTER, 290, 810);
		button_DeleteReply.setOnAction((_) -> performDeleteReplyUI());

		setupLabelUI(label_UpdateReplyId, "Arial", 14, 130, Pos.BASELINE_LEFT, 470, 815);
		setupTextUI(text_UpdateReplyId, "Arial", 14, 90, Pos.BASELINE_LEFT, 610, 810, true);

		setupButtonUI(button_UpdateReply, "Dialog", 14, 150, Pos.CENTER, 730, 810);
		button_UpdateReply.setOnAction((_) -> performUpdateReplyUI());

		setupTextAreaUI(area_RepliesForPost, "Arial", 12, 890, 120, 40, 845);

		theRootPane.getChildren().addAll(
				label_PageTitle, line_Separator1, label_Status,

				// Thread lifecycle admin panel
				label_ThreadSection,
				label_ThreadIdInput, text_ThreadIdInput,
				button_LockThread, button_OpenThread, button_ArchiveThread,
				label_ThreadStatus,

				label_PostSection,
				label_PostTitle, text_PostTitle,
				label_PostBody, text_PostBody,
				label_PostAuthor, text_PostAuthor,
				label_PostThread, text_PostThread,
				button_CreatePost,
				label_SearchPosts, text_SearchPosts,
				label_SearchThread, text_SearchThread,
				button_SearchPosts,
				label_DeletePost, text_DeletePostId, button_DeletePost,
				label_UpdatePostId, text_UpdatePostId, button_UpdatePost,

				line_Separator2,
				label_AllPosts, area_AllPosts,
				label_SubsetPosts, area_SubsetPosts,

				line_Separator3,
				label_ReplySection,
				label_ReplyPostId, text_ReplyPostId,
				label_ReplyBody, text_ReplyBody,
				label_ReplyAuthor, text_ReplyAuthor,
				button_CreateReply,
				label_ViewRepliesForPost, text_ViewRepliesPostId, button_ViewReplies,
				label_DeleteReply, text_DeleteReplyId, button_DeleteReply,
				label_UpdateReplyId, text_UpdateReplyId, button_UpdateReply,
				area_RepliesForPost
		);
	}

	/*-*******************************************************************************************

	Methods called by Controller / UI refresh
	
	*/

	protected static void updatePostListDisplays() {
		List<Post> all = ControllerDiscussion.getPostStore().getAllPosts();
		List<Post> subset = ControllerDiscussion.getPostStore().getSubsetPosts();

		area_AllPosts.setText(formatPosts(all));
		area_SubsetPosts.setText(formatPosts(subset));
	}

	protected static void updateReplyListDisplays() {
		// Replies are shown in the replies text area for a selected post.
	}

	/*-*******************************************************************************************

	UI action handlers
	
	*/

	private static void performCreatePostUI() {
		// Check thread status — block posting if locked or archived
		String threadName = text_PostThread.getText().trim();
		if (!threadName.isEmpty()) {
			ThreadStatus status = ThreadLifecycleService.getInstance().getStatus(threadName);
			if (status == ThreadStatus.LOCKED) {
				setStatus("This thread is locked. No new posts allowed.", true);
				return;
			}
			if (status == ThreadStatus.ARCHIVED) {
				setStatus("This thread is archived. No new posts allowed.", true);
				return;
			}
		}

		String err = ControllerDiscussion.getPostStore().createPost(
				text_PostTitle.getText(),
				text_PostBody.getText(),
				text_PostAuthor.getText(),
				text_PostThread.getText()
		);

		if (err != null) {
			setStatus(err, true);
			return;
		}

		text_PostTitle.setText("");
		text_PostBody.setText("");
		text_PostThread.setText("");

		updatePostListDisplays();
		setStatus("Post created successfully.", false);
	}

	private static void performSearchPostsUI() {
		ControllerDiscussion.getPostStore().searchPosts(
				text_SearchPosts.getText(),
				text_SearchThread.getText()
		);
		updatePostListDisplays();
		setStatus("Post search completed.", false);
	}

	private static void performDeletePostUI() {
		String raw = text_DeletePostId.getText();

		int postId;
		try {
			postId = Integer.parseInt(raw.trim());
		} catch (Exception e) {
			setStatus("Invalid post ID.", true);
			return;
		}

		Post post = ControllerDiscussion.getPostStore().getPostById(postId);
		if (post == null) {
			setStatus("Post not found.", true);
			return;
		}

		Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION);
		confirmDelete.setTitle("Confirm Delete");
		confirmDelete.setHeaderText("Delete Post " + postId + "?");
		confirmDelete.setContentText("Are you sure you want to delete this post? Replies will remain visible.");

		ButtonType result = confirmDelete.showAndWait().orElse(ButtonType.CANCEL);

		if (result != ButtonType.OK) {
			setStatus("Delete cancelled.", false);
			return;
		}

		String err = ControllerDiscussion.getPostStore().deletePost(postId);
		if (err != null) {
			setStatus(err, true);
			return;
		}

		updatePostListDisplays();

		String replyViewId = text_ViewRepliesPostId.getText();
		if (replyViewId != null && !replyViewId.trim().isEmpty()) {
			try {
				int viewedPostId = Integer.parseInt(replyViewId.trim());
				if (viewedPostId == postId) {
					area_RepliesForPost.setText(ControllerDiscussion.buildRepliesDisplayText(postId));
				}
			} catch (Exception e) {
				// Best-effort refresh only
			}
		}

		setStatus("Post deleted successfully.", false);
	}

	private static void performUpdatePostUI() {
		String rawPostId = text_UpdatePostId.getText();

		int postId;
		try {
			postId = Integer.parseInt(rawPostId.trim());
		} catch (Exception e) {
			setStatus("Invalid post ID.", true);
			return;
		}

		String newTitle = text_PostTitle.getText();
		String newBody = text_PostBody.getText();

		String err = ControllerDiscussion.getPostStore().updatePost(postId, newTitle, newBody);
		if (err != null) {
			setStatus(err, true);
			return;
		}

		updatePostListDisplays();
		setStatus("Post updated successfully.", false);
	}
	
	private static void performCreateReplyUI() {
		int postId;
		try {
			postId = Integer.parseInt(text_ReplyPostId.getText().trim());
		} catch (Exception e) {
			setStatus("Invalid Post ID.", true);
			return;
		}

		Post p = ControllerDiscussion.getPostStore().getPostById(postId);
		if (p == null) {
			setStatus("Post ID does not exist.", true);
			return;
		}

		String err = ControllerDiscussion.getReplyStore().createReply(
				postId,
				text_ReplyBody.getText(),
				text_ReplyAuthor.getText()
		);

		if (err != null) {
			setStatus(err, true);
			return;
		}

		text_ReplyBody.setText("");
		text_ReplyAuthor.setText("");

		area_RepliesForPost.setText(ControllerDiscussion.buildRepliesDisplayText(postId));
		setStatus("Reply created successfully.", false);
	}

	private static void performViewRepliesForPost() {
		String raw = text_ViewRepliesPostId.getText();

		int postId;
		try {
			postId = Integer.parseInt(raw.trim());
		} catch (Exception e) {
			setStatus("Invalid post ID.", true);
			return;
		}

		Post p = ControllerDiscussion.getPostStore().getPostById(postId);
		if (p == null) {
			setStatus("Post ID does not exist.", true);
			return;
		}

		area_RepliesForPost.setText(ControllerDiscussion.buildRepliesDisplayText(postId));
		setStatus("Replies loaded.", false);
	}

	private static void performDeleteReplyUI() {
		String rawReplyId = text_DeleteReplyId.getText();

		int replyId;
		try {
			replyId = Integer.parseInt(rawReplyId.trim());
		} catch (Exception e) {
			setStatus("Invalid reply ID.", true);
			return;
		}

		Reply reply = ControllerDiscussion.getReplyStore().getReplyById(replyId);
		if (reply == null || reply.isDeleted()) {
			setStatus("Reply not found.", true);
			return;
		}

		int postId = reply.getPostId();

		String err = ControllerDiscussion.getReplyStore().deleteReply(replyId);
		if (err != null) {
			setStatus(err, true);
			return;
		}

		area_RepliesForPost.setText(ControllerDiscussion.buildRepliesDisplayText(postId));
		text_ViewRepliesPostId.setText(String.valueOf(postId));

		setStatus("Reply deleted successfully.", false);
	}

	private static void performUpdateReplyUI() {
		String rawReplyId = text_UpdateReplyId.getText();

		int replyId;
		try {
			replyId = Integer.parseInt(rawReplyId.trim());
		} catch (Exception e) {
			setStatus("Invalid reply ID.", true);
			return;
		}

		Reply reply = ControllerDiscussion.getReplyStore().getReplyById(replyId);
		if (reply == null || reply.isDeleted()) {
			setStatus("Reply not found.", true);
			return;
		}

		String newBody = text_ReplyBody.getText();

		String err = ControllerDiscussion.getReplyStore().updateReply(replyId, newBody);
		if (err != null) {
			setStatus(err, true);
			return;
		}

		int postId = reply.getPostId();
		area_RepliesForPost.setText(ControllerDiscussion.buildRepliesDisplayText(postId));
		text_ViewRepliesPostId.setText(String.valueOf(postId));

		setStatus("Reply updated successfully.", false);
	}

	/*-*******************************************************************************************

	Thread Lifecycle handler (Admin only)

	*/

	private static void performChangeThreadStatus(ThreadStatus newStatus) {
		String threadId = text_ThreadIdInput.getText().trim();
		if (threadId.isEmpty()) {
			setStatus("Please enter a Thread ID.", true);
			return;
		}
		String result = ThreadLifecycleService.getInstance().changeThreadStatus(threadId, newStatus, theUser);
		if (result != null) {
			setStatus(result, true);
		} else {
			ThreadStatus current = ThreadLifecycleService.getInstance().getStatus(threadId);
			label_ThreadStatus.setText("Status: " + current.name());
			setStatus("Thread [" + threadId + "] is now " + current.name() + ".", false);
		}
	}

	/*-*******************************************************************************************

	Local helpers

	*/

	private static String formatPosts(List<Post> posts) {
		if (posts == null || posts.isEmpty()) return "No posts found.\n";

		StringBuilder sb = new StringBuilder();

		for (Post p : posts) {
			sb.append("Post ").append(p.getPostId()).append("\n");
			sb.append("Thread: ").append(p.getThread()).append("\n");
			sb.append("Author: ").append(p.getAuthor()).append("\n");
			if (p.isDeleted()) {
				sb.append("[DELETED]\n");
			} else {
				sb.append("Title: ").append(p.getTitle()).append("\n");
				sb.append(p.getBody()).append("\n");
			}
			sb.append("----\n");
		}

		return sb.toString();
	}

	private static void setStatus(String message, boolean isError) {
		label_Status.setText(message);
		if (isError) {
			label_Status.setTextFill(Color.DARKRED);
		} else {
			label_Status.setTextFill(Color.DARKGREEN);
		}
	}

	/*-*******************************************************************************************

	Helper methods used to minimize code above
	
	*/

	private void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y) {
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}

	private void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y) {
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}

	private void setupTextUI(TextField t, String ff, double f, double w, Pos p, double x, double y, boolean e) {
		t.setFont(Font.font(ff, f));
		t.setMinWidth(w);
		t.setMaxWidth(w);
		t.setAlignment(p);
		t.setLayoutX(x);
		t.setLayoutY(y);
		t.setEditable(e);
	}

	private void setupTextAreaUI(TextArea a, String ff, double f, double w, double h, double x, double y) {
		a.setFont(Font.font(ff, f));
		a.setMinWidth(w);
		a.setMaxWidth(w);
		a.setMinHeight(h);
		a.setMaxHeight(h);
		a.setLayoutX(x);
		a.setLayoutY(y);
		a.setEditable(false);
		a.setWrapText(true);
	}
}