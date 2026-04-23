package guiDiscussion;

import java.util.List;

import discussionStore.ThreadLifecycleService;
import discussionStore.ThreadStatus;
import entityClasses.Post;
import entityClasses.Reply;
import entityClasses.User;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/*******
 * <p> Title: ViewDiscussion Class. </p>
 * 
 * <p> Description: The JavaFX-based discussion page used to demonstrate CRUD,
 * search, thread lifecycle management, engagement statistics, replies, and TP3
 * moderation/content flagging behavior. Status feedback is shown inline on the
 * page.</p>
 * 
 * <p>This page supports:
 * <ul>
 *   <li>post create, update, delete, search, and display</li>
 *   <li>reply create, update, delete, and display</li>
 *   <li>admin-only thread lifecycle actions</li>
 *   <li>staff/admin moderation actions such as flagging, hiding, and highlighting posts</li>
 * </ul>
 * </p>
 * 
 * @author Vikram Thevar
 */
public class ViewDiscussion {

	/*-*******************************************************************************************
	Attributes
	**********************************************************************************************/

	private static double width = 1180;
	private static double height = 1340;

	// GUI Area 1: Title
	protected static Label label_PageTitle = new Label("HW2 Discussion Page");

	// Inline status label
	protected static Label label_Status = new Label("");

	// Back button
	protected static Button button_Back = new Button("Back");

	// GUI Area: Thread Lifecycle (Admin Only)
	protected static Label label_ThreadSection = new Label("Thread Lifecycle Management (Admin)");
	protected static Label label_ThreadIdInput = new Label("Thread ID:");
	protected static TextField text_ThreadIdInput = new TextField();
	protected static Button button_LockThread = new Button("Lock Thread");
	protected static Button button_OpenThread = new Button("Open Thread");
	protected static Button button_ArchiveThread = new Button("Archive Thread");
	protected static Label label_ThreadStatus = new Label("Status: OPEN");

	// Separators
	private static Line line_Separator1 = new Line(40, 70, width - 40, 70);
	private static Line line_Separator2 = new Line(40, 680, width - 40, 680);
	private static Line line_Separator3 = new Line(40, 970, width - 40, 970);

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
	protected static Button button_ShowStats = new Button("Engagement Stats");

	protected static Label label_DeletePost = new Label("Delete Post by ID:");
	protected static TextField text_DeletePostId = new TextField();
	protected static Button button_DeletePost = new Button("Delete Post");

	protected static Label label_UpdatePostId = new Label("Update Post ID:");
	protected static TextField text_UpdatePostId = new TextField();
	protected static Button button_UpdatePost = new Button("Update Post");

	// GUI Area 2B: Moderation (Staff/Admin)
	protected static Label label_ModerationSection = new Label("Moderation (Staff/Admin)");
	protected static Label label_ModerationPostId = new Label("Post ID:");
	protected static TextField text_ModerationPostId = new TextField();

	protected static Label label_FlagReason = new Label("Flag Reason:");
	protected static TextField text_FlagReason = new TextField();

	protected static Button button_FlagPost = new Button("Flag Post");
	protected static Button button_UnflagPost = new Button("Unflag Post");
	protected static Button button_HidePost = new Button("Hide Post");
	protected static Button button_UnhidePost = new Button("Unhide Post");
	protected static Button button_HighlightPost = new Button("Highlight Post");
	protected static Button button_RemoveHighlight = new Button("Remove Highlight");
	protected static Button button_ViewFlaggedPosts = new Button("View Flagged Only");

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
	private static User theUser;

	protected static Stage theStage;
	private static Pane theRootPane;
	private static Scene theDiscussionScene;

	/*-*******************************************************************************************
	Constructors
	**********************************************************************************************/

	/**
	 * Displays the discussion page for the supplied user.
	 *
	 * @param ps the primary stage used to display the page
	 * @param user the currently logged-in user
	 */
	public static void displayDiscussion(Stage ps, User user) {
		theStage = ps;
		theUser = user;

		if (theView == null) theView = new ViewDiscussion();

		boolean isAdmin = (user != null && user.getAdminRole());
		boolean canModerate = (user != null && (user.getAdminRole() || user.getNewRole2()));

		label_ThreadSection.setVisible(isAdmin);
		label_ThreadIdInput.setVisible(isAdmin);
		text_ThreadIdInput.setVisible(isAdmin);
		button_LockThread.setVisible(isAdmin);
		button_OpenThread.setVisible(isAdmin);
		button_ArchiveThread.setVisible(isAdmin);
		label_ThreadStatus.setVisible(isAdmin);

		label_ModerationSection.setVisible(canModerate);
		label_ModerationPostId.setVisible(canModerate);
		text_ModerationPostId.setVisible(canModerate);
		label_FlagReason.setVisible(canModerate);
		text_FlagReason.setVisible(canModerate);
		button_FlagPost.setVisible(canModerate);
		button_UnflagPost.setVisible(canModerate);
		button_HidePost.setVisible(canModerate);
		button_UnhidePost.setVisible(canModerate);
		button_HighlightPost.setVisible(canModerate);
		button_RemoveHighlight.setVisible(canModerate);
		button_ViewFlaggedPosts.setVisible(canModerate);

		updatePostListDisplays();
		area_RepliesForPost.setText("");
		setStatus("Discussion page ready.", false);

		theStage.setTitle("CSE 360: Discussion");
		theStage.setScene(theDiscussionScene);
		theStage.show();
	}

	/**
	 * Constructs the discussion page and initializes all widgets.
	 */
	private ViewDiscussion() {
		theRootPane = new Pane();
		theDiscussionScene = new Scene(theRootPane, width, height);

		setupLabelUI(label_PageTitle, "Arial", 26, width, Pos.CENTER, 0, 12);

		setupLabelUI(label_Status, "Arial", 13, 500, Pos.BASELINE_LEFT, 40, 45);
		label_Status.setTextFill(Color.DARKGREEN);

		setupButtonUI(button_Back, "Dialog", 13, 100, Pos.CENTER, 1030, 35);
		button_Back.setOnAction(e -> performBackUI());

		setupLabelUI(label_ThreadSection, "Arial", 15, 400, Pos.BASELINE_LEFT, 40, 82);
		label_ThreadSection.setTextFill(Color.DARKBLUE);

		setupLabelUI(label_ThreadIdInput, "Arial", 14, 80, Pos.BASELINE_LEFT, 40, 118);
		setupTextUI(text_ThreadIdInput, "Arial", 14, 180, Pos.BASELINE_LEFT, 120, 113, true);
		text_ThreadIdInput.setText("Assignment1");

		setupButtonUI(button_LockThread, "Dialog", 13, 130, Pos.CENTER, 320, 113);
		button_LockThread.setOnAction(e -> performChangeThreadStatus(ThreadStatus.LOCKED));

		setupButtonUI(button_OpenThread, "Dialog", 13, 130, Pos.CENTER, 470, 113);
		button_OpenThread.setOnAction(e -> performChangeThreadStatus(ThreadStatus.OPEN));

		setupButtonUI(button_ArchiveThread, "Dialog", 13, 140, Pos.CENTER, 620, 113);
		button_ArchiveThread.setOnAction(e -> performChangeThreadStatus(ThreadStatus.ARCHIVED));

		setupLabelUI(label_ThreadStatus, "Arial", 13, 220, Pos.BASELINE_LEFT, 790, 118);
		label_ThreadStatus.setTextFill(Color.DARKBLUE);

		setupLabelUI(label_PostSection, "Arial", 20, 220, Pos.BASELINE_LEFT, 40, 165);

		setupLabelUI(label_PostTitle, "Arial", 14, 90, Pos.BASELINE_LEFT, 40, 205);
		setupTextUI(text_PostTitle, "Arial", 14, 430, Pos.BASELINE_LEFT, 160, 200, true);

		setupLabelUI(label_PostBody, "Arial", 14, 90, Pos.BASELINE_LEFT, 40, 245);
		setupTextUI(text_PostBody, "Arial", 14, 430, Pos.BASELINE_LEFT, 160, 240, true);

		setupLabelUI(label_PostAuthor, "Arial", 14, 90, Pos.BASELINE_LEFT, 40, 285);
		setupTextUI(text_PostAuthor, "Arial", 14, 430, Pos.BASELINE_LEFT, 160, 280, true);

		setupLabelUI(label_PostThread, "Arial", 14, 130, Pos.BASELINE_LEFT, 40, 325);
		setupTextUI(text_PostThread, "Arial", 14, 430, Pos.BASELINE_LEFT, 160, 320, true);

		setupButtonUI(button_CreatePost, "Dialog", 14, 170, Pos.CENTER, 640, 240);
		button_CreatePost.setOnAction(e -> performCreatePostUI());

		setupLabelUI(label_SearchPosts, "Arial", 14, 130, Pos.BASELINE_LEFT, 40, 375);
		setupTextUI(text_SearchPosts, "Arial", 14, 220, Pos.BASELINE_LEFT, 170, 370, true);

		setupLabelUI(label_SearchThread, "Arial", 14, 130, Pos.BASELINE_LEFT, 430, 375);
		setupTextUI(text_SearchThread, "Arial", 14, 220, Pos.BASELINE_LEFT, 570, 370, true);

		setupButtonUI(button_SearchPosts, "Dialog", 14, 110, Pos.CENTER, 820, 370);
		button_SearchPosts.setOnAction(e -> performSearchPostsUI());

		setupButtonUI(button_ShowStats, "Dialog", 14, 170, Pos.CENTER, 640, 415);
		button_ShowStats.setOnAction(e -> performShowEngagementStatsUI());

		setupLabelUI(label_DeletePost, "Arial", 14, 150, Pos.BASELINE_LEFT, 40, 455);
		setupTextUI(text_DeletePostId, "Arial", 14, 90, Pos.BASELINE_LEFT, 190, 450, true);
		setupButtonUI(button_DeletePost, "Dialog", 14, 140, Pos.CENTER, 300, 450);
		button_DeletePost.setOnAction(e -> performDeletePostUI());

		setupLabelUI(label_UpdatePostId, "Arial", 14, 150, Pos.BASELINE_LEFT, 470, 455);
		setupTextUI(text_UpdatePostId, "Arial", 14, 90, Pos.BASELINE_LEFT, 620, 450, true);
		setupButtonUI(button_UpdatePost, "Dialog", 14, 140, Pos.CENTER, 740, 450);
		button_UpdatePost.setOnAction(e -> performUpdatePostUI());

		setupLabelUI(label_ModerationSection, "Arial", 18, 300, Pos.BASELINE_LEFT, 40, 525);
		label_ModerationSection.setTextFill(Color.DARKRED);

		setupLabelUI(label_ModerationPostId, "Arial", 14, 80, Pos.BASELINE_LEFT, 40, 565);
		setupTextUI(text_ModerationPostId, "Arial", 14, 100, Pos.BASELINE_LEFT, 120, 560, true);

		setupLabelUI(label_FlagReason, "Arial", 14, 100, Pos.BASELINE_LEFT, 260, 565);
		setupTextUI(text_FlagReason, "Arial", 14, 360, Pos.BASELINE_LEFT, 360, 560, true);

		setupButtonUI(button_FlagPost, "Dialog", 13, 110, Pos.CENTER, 40, 605);
		button_FlagPost.setOnAction(e -> performFlagPostUI());

		setupButtonUI(button_UnflagPost, "Dialog", 13, 110, Pos.CENTER, 165, 605);
		button_UnflagPost.setOnAction(e -> performUnflagPostUI());

		setupButtonUI(button_HidePost, "Dialog", 13, 110, Pos.CENTER, 290, 605);
		button_HidePost.setOnAction(e -> performHidePostUI());

		setupButtonUI(button_UnhidePost, "Dialog", 13, 110, Pos.CENTER, 415, 605);
		button_UnhidePost.setOnAction(e -> performUnhidePostUI());

		setupButtonUI(button_HighlightPost, "Dialog", 13, 130, Pos.CENTER, 540, 605);
		button_HighlightPost.setOnAction(e -> performHighlightPostUI());

		setupButtonUI(button_RemoveHighlight, "Dialog", 13, 150, Pos.CENTER, 685, 605);
		button_RemoveHighlight.setOnAction(e -> performRemoveHighlightUI());

		setupButtonUI(button_ViewFlaggedPosts, "Dialog", 13, 170, Pos.CENTER, 40, 645);
		button_ViewFlaggedPosts.setOnAction(e -> performViewFlaggedPostsUI());

		setupLabelUI(label_AllPosts, "Arial", 14, 220, Pos.BASELINE_LEFT, 40, 700);
		setupTextAreaUI(area_AllPosts, "Arial", 12, 500, 220, 40, 725);

		setupLabelUI(label_SubsetPosts, "Arial", 14, 280, Pos.BASELINE_LEFT, 580, 700);
		setupTextAreaUI(area_SubsetPosts, "Arial", 12, 500, 220, 580, 725);

		setupLabelUI(label_ReplySection, "Arial", 20, 200, Pos.BASELINE_LEFT, 40, 995);

		setupLabelUI(label_ReplyPostId, "Arial", 14, 140, Pos.BASELINE_LEFT, 40, 1035);
		setupTextUI(text_ReplyPostId, "Arial", 14, 90, Pos.BASELINE_LEFT, 180, 1030, true);

		setupLabelUI(label_ReplyAuthor, "Arial", 14, 110, Pos.BASELINE_LEFT, 340, 1035);
		setupTextUI(text_ReplyAuthor, "Arial", 14, 220, Pos.BASELINE_LEFT, 460, 1030, true);

		setupLabelUI(label_ReplyBody, "Arial", 14, 90, Pos.BASELINE_LEFT, 40, 1075);
		setupTextUI(text_ReplyBody, "Arial", 14, 600, Pos.BASELINE_LEFT, 180, 1070, true);

		setupButtonUI(button_CreateReply, "Dialog", 14, 160, Pos.CENTER, 180, 1115);
		button_CreateReply.setOnAction(e -> performCreateReplyUI());

		setupLabelUI(label_ViewRepliesForPost, "Arial", 14, 180, Pos.BASELINE_LEFT, 390, 1120);
		setupTextUI(text_ViewRepliesPostId, "Arial", 14, 90, Pos.BASELINE_LEFT, 585, 1115, true);

		setupButtonUI(button_ViewReplies, "Dialog", 14, 160, Pos.CENTER, 690, 1115);
		button_ViewReplies.setOnAction(e -> performViewRepliesForPost());

		setupLabelUI(label_DeleteReply, "Arial", 14, 140, Pos.BASELINE_LEFT, 40, 1155);
		setupTextUI(text_DeleteReplyId, "Arial", 14, 90, Pos.BASELINE_LEFT, 180, 1150, true);

		setupButtonUI(button_DeleteReply, "Dialog", 14, 160, Pos.CENTER, 300, 1150);
		button_DeleteReply.setOnAction(e -> performDeleteReplyUI());

		setupLabelUI(label_UpdateReplyId, "Arial", 14, 140, Pos.BASELINE_LEFT, 500, 1155);
		setupTextUI(text_UpdateReplyId, "Arial", 14, 90, Pos.BASELINE_LEFT, 650, 1150, true);

		setupButtonUI(button_UpdateReply, "Dialog", 14, 160, Pos.CENTER, 770, 1150);
		button_UpdateReply.setOnAction(e -> performUpdateReplyUI());

		setupTextAreaUI(area_RepliesForPost, "Arial", 12, 1040, 130, 40, 1190);

		theRootPane.getChildren().addAll(
				label_PageTitle, line_Separator1, label_Status, button_Back,

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
				button_SearchPosts, button_ShowStats,
				label_DeletePost, text_DeletePostId, button_DeletePost,
				label_UpdatePostId, text_UpdatePostId, button_UpdatePost,

				label_ModerationSection,
				label_ModerationPostId, text_ModerationPostId,
				label_FlagReason, text_FlagReason,
				button_FlagPost, button_UnflagPost,
				button_HidePost, button_UnhidePost,
				button_HighlightPost, button_RemoveHighlight,
				button_ViewFlaggedPosts,

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
	**********************************************************************************************/

	/**
	 * Refreshes the all-post and subset-post text areas using the current store state.
	 */
	protected static void updatePostListDisplays() {
		List<Post> all = ControllerDiscussion.getPostStore().getAllPosts();
		List<Post> subset = ControllerDiscussion.getPostStore().getSubsetPosts();

		area_AllPosts.setText(formatPosts(all));
		area_SubsetPosts.setText(formatPosts(subset));
	}

	/**
	 * Placeholder for reply list display updates when needed.
	 */
	protected static void updateReplyListDisplays() {
		// Replies are shown in the replies text area for a selected post.
	}

	/*-*******************************************************************************************
	UI action handlers
	**********************************************************************************************/

	private static void performBackUI() {
		if (theUser == null) {
			setStatus("No active user session found.", true);
			return;
		}

		if (theUser.getNewRole1()) {
			guiRole1.ViewRole1Home.displayRole1Home(theStage, theUser);
			return;
		}

		if (theUser.getNewRole2()) {
			guiRole2.ViewRole2Home.displayRole2Home(theStage, theUser);
			return;
		}

		if (theUser.getAdminRole()) {
			guiAdminHome.ViewAdminHome.displayAdminHome(theStage, theUser);
			return;
		}

		setStatus("Unable to determine which home page to return to.", true);
	}

	private static void performCreatePostUI() {
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

		text_PostTitle.clear();
		text_PostBody.clear();
		text_PostThread.clear();

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

	private static void performShowEngagementStatsUI() {
		String report = ControllerDiscussion.buildEngagementStatisticsReport();
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Engagement Statistics");
		alert.setHeaderText("Discussion participation metrics");
		alert.setContentText(report);
		alert.setResizable(true);
		alert.getDialogPane().setPrefSize(620, 360);
		alert.showAndWait();
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

	private static Integer parseModerationPostId() {
		try {
			return Integer.parseInt(text_ModerationPostId.getText().trim());
		} catch (Exception e) {
			setStatus("Invalid moderation post ID.", true);
			return null;
		}
	}

	private static void clearModerationInputs() {
		text_ModerationPostId.clear();
		text_FlagReason.clear();
	}

	private static void performFlagPostUI() {
		Integer postId = parseModerationPostId();
		if (postId == null) return;

		String err = ControllerDiscussion.getPostStore().flagPost(
				postId,
				text_FlagReason.getText(),
				theUser == null ? "" : theUser.getUserName()
		);

		if (err != null) {
			setStatus(err, true);
			return;
		}

		updatePostListDisplays();
		clearModerationInputs();
		setStatus("Post flagged successfully.", false);
	}

	private static void performUnflagPostUI() {
		Integer postId = parseModerationPostId();
		if (postId == null) return;

		String err = ControllerDiscussion.getPostStore().unflagPost(postId);
		if (err != null) {
			setStatus(err, true);
			return;
		}

		updatePostListDisplays();
		clearModerationInputs();
		setStatus("Post unflagged successfully.", false);
	}

	private static void performHidePostUI() {
		Integer postId = parseModerationPostId();
		if (postId == null) return;

		String err = ControllerDiscussion.getPostStore().hidePost(postId);
		if (err != null) {
			setStatus(err, true);
			return;
		}

		updatePostListDisplays();
		clearModerationInputs();
		setStatus("Post hidden successfully.", false);
	}

	private static void performUnhidePostUI() {
		Integer postId = parseModerationPostId();
		if (postId == null) return;

		String err = ControllerDiscussion.getPostStore().unhidePost(postId);
		if (err != null) {
			setStatus(err, true);
			return;
		}

		updatePostListDisplays();
		clearModerationInputs();
		setStatus("Post unhidden successfully.", false);
	}

	private static void performHighlightPostUI() {
		Integer postId = parseModerationPostId();
		if (postId == null) return;

		String err = ControllerDiscussion.getPostStore().highlightPost(postId);
		if (err != null) {
			setStatus(err, true);
			return;
		}

		updatePostListDisplays();
		clearModerationInputs();
		setStatus("Post highlighted successfully.", false);
	}

	private static void performRemoveHighlightUI() {
		Integer postId = parseModerationPostId();
		if (postId == null) return;

		String err = ControllerDiscussion.getPostStore().removeHighlightPost(postId);
		if (err != null) {
			setStatus(err, true);
			return;
		}

		updatePostListDisplays();
		clearModerationInputs();
		setStatus("Post highlight removed successfully.", false);
	}

	private static void performViewFlaggedPostsUI() {
		List<Post> flagged = ControllerDiscussion.getPostStore().getFlaggedPosts();
		area_SubsetPosts.setText(formatPosts(flagged));
		setStatus("Flagged posts loaded.", false);
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

		text_ReplyBody.clear();
		text_ReplyAuthor.clear();

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
	Thread Lifecycle handler
	**********************************************************************************************/

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
	**********************************************************************************************/

	private static String formatPosts(List<Post> posts) {
		if (posts == null || posts.isEmpty()) return "No posts found.\n";

		StringBuilder sb = new StringBuilder();

		for (Post p : posts) {
			sb.append("Post ").append(p.getPostId()).append("\n");
			sb.append("Thread: ").append(p.getThread()).append("\n");
			sb.append("Author: ").append(p.getAuthor()).append("\n");

			if (p.isFlagged()) {
				sb.append("[FLAGGED]");
				if (p.getFlagReason() != null && !p.getFlagReason().isBlank()) {
					sb.append(" Reason: ").append(p.getFlagReason());
				}
				if (p.getFlaggedBy() != null && !p.getFlaggedBy().isBlank()) {
					sb.append(" By: ").append(p.getFlaggedBy());
				}
				sb.append("\n");
			}

			if (p.isHidden()) {
				sb.append("[HIDDEN]\n");
			}

			if (p.isHighlighted()) {
				sb.append("[HIGHLIGHTED]\n");
			}

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
	UI setup helpers
	**********************************************************************************************/

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