package guiDiscussion;

import java.util.List;

import entityClasses.Post;
import entityClasses.Reply;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/*******
 * <p> Title: ViewDiscussion Class. </p>
 * 
 * <p> Description: The Java/FX-based HW2 Discussion Page. This class provides the JavaFX GUI widgets
 * used to demonstrate CRUD and input validation for Posts and Replies, including subset list results.</p>
 * 
 * @author Vikram Thevar
 *
 */

public class ViewDiscussion {

	/*-*******************************************************************************************

	Attributes
	
	*/

	// These are the application values required by the user interface
	
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	// Alerts used by this page
	protected static Alert alertError = new Alert(AlertType.INFORMATION);
	protected static Alert alertInfo = new Alert(AlertType.INFORMATION);

	// GUI Area 1: Title
	protected static Label label_PageTitle = new Label("HW2 Discussion Page");

	// Separators
	private static Line line_Separator1 = new Line(20, 55, width-20, 55);
	private static Line line_Separator2 = new Line(20, 285, width-20, 285);
	private static Line line_Separator3 = new Line(20, 515, width-20, 515);

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

	protected static Label label_SearchPosts = new Label("Search Posts:");
	protected static TextField text_SearchPosts = new TextField();
	protected static Button button_SearchPosts = new Button("Search");

	protected static Label label_DeletePost = new Label("Delete Post by ID:");
	protected static TextField text_DeletePostId = new TextField();
	protected static Button button_DeletePost = new Button("Delete Post");

	// GUI Area 3: Post lists (all + subset)
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

	protected static TextArea area_RepliesForPost = new TextArea();

	// These attributes are used to configure the page
	private static ViewDiscussion theView;

	protected static Stage theStage;
	private static Pane theRootPane;
	private static Scene theDiscussionScene;

	/*-*******************************************************************************************

	Constructors
	
	*/

	/**********
	 * <p> Method: displayDiscussion(Stage ps) </p>
	 * 
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the HW2 Discussion page to be displayed.</p>
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and its methods
	 */
	public static void displayDiscussion(Stage ps) {

		theStage = ps;

		if (theView == null) theView = new ViewDiscussion();

		updatePostListDisplays();
		area_RepliesForPost.setText("");

		theStage.setTitle("CSE 360 HW2: Discussion");
		theStage.setScene(theDiscussionScene);
		theStage.show();
	}

	/**********
	 * <p> Method: ViewDiscussion() </p>
	 * 
	 * <p> Description: Initializes all static aspects of the GUI widgets and registers event
	 * handlers for buttons.</p>
	 */
	private ViewDiscussion() {

		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theDiscussionScene = new Scene(theRootPane, width, height);

		// Configure alerts
		alertError.setTitle("*** Error ***");
		alertError.setHeaderText(null);

		alertInfo.setTitle("Information");
		alertInfo.setHeaderText(null);

		// GUI Area 1: Title
		setupLabelUI(label_PageTitle, "Arial", 26, width, Pos.CENTER, 0, 10);

		// GUI Area 2: Posts
		setupLabelUI(label_PostSection, "Arial", 20, 200, Pos.BASELINE_LEFT, 20, 70);

		setupLabelUI(label_PostTitle, "Arial", 14, 80, Pos.BASELINE_LEFT, 20, 105);
		setupTextUI(text_PostTitle, "Arial", 14, 300, Pos.BASELINE_LEFT, 120, 100, true);

		setupLabelUI(label_PostBody, "Arial", 14, 80, Pos.BASELINE_LEFT, 20, 140);
		setupTextUI(text_PostBody, "Arial", 14, 300, Pos.BASELINE_LEFT, 120, 135, true);

		setupLabelUI(label_PostAuthor, "Arial", 14, 80, Pos.BASELINE_LEFT, 20, 175);
		setupTextUI(text_PostAuthor, "Arial", 14, 300, Pos.BASELINE_LEFT, 120, 170, true);

		setupLabelUI(label_PostThread, "Arial", 14, 130, Pos.BASELINE_LEFT, 20, 210);
		setupTextUI(text_PostThread, "Arial", 14, 300, Pos.BASELINE_LEFT, 150, 205, true);

		setupButtonUI(button_CreatePost, "Dialog", 14, 140, Pos.CENTER, 470, 135);
		button_CreatePost.setOnAction((_) -> { ControllerDiscussion.performCreatePost(); });

		// Search + Delete
		setupLabelUI(label_SearchPosts, "Arial", 14, 100, Pos.BASELINE_LEFT, 20, 245);
		setupTextUI(text_SearchPosts, "Arial", 14, 220, Pos.BASELINE_LEFT, 120, 240, true);
		setupButtonUI(button_SearchPosts, "Dialog", 14, 100, Pos.CENTER, 350, 240);
		button_SearchPosts.setOnAction((_) -> { ControllerDiscussion.performSearchPosts(); });

		setupLabelUI(label_DeletePost, "Arial", 14, 140, Pos.BASELINE_LEFT, 470, 245);
		setupTextUI(text_DeletePostId, "Arial", 14, 70, Pos.BASELINE_LEFT, 610, 240, true);
		setupButtonUI(button_DeletePost, "Dialog", 14, 120, Pos.CENTER, 690, 240);
		button_DeletePost.setOnAction((_) -> { ControllerDiscussion.performDeletePost(); });

		// GUI Area 3: Post list displays
		setupLabelUI(label_AllPosts, "Arial", 14, 200, Pos.BASELINE_LEFT, 20, 295);
		setupTextAreaUI(area_AllPosts, "Arial", 12, 360, 190, 20, 320);

		setupLabelUI(label_SubsetPosts, "Arial", 14, 260, Pos.BASELINE_LEFT, 410, 295);
		setupTextAreaUI(area_SubsetPosts, "Arial", 12, 360, 190, 410, 320);

		// GUI Area 4: Replies
		setupLabelUI(label_ReplySection, "Arial", 20, 200, Pos.BASELINE_LEFT, 20, 525);

		setupLabelUI(label_ReplyPostId, "Arial", 14, 140, Pos.BASELINE_LEFT, 20, 560);
		setupTextUI(text_ReplyPostId, "Arial", 14, 80, Pos.BASELINE_LEFT, 170, 555, true);

		setupLabelUI(label_ReplyBody, "Arial", 14, 90, Pos.BASELINE_LEFT, 270, 560);
		setupTextUI(text_ReplyBody, "Arial", 14, 240, Pos.BASELINE_LEFT, 350, 555, true);

		setupLabelUI(label_ReplyAuthor, "Arial", 14, 110, Pos.BASELINE_LEFT, 20, 595);
		setupTextUI(text_ReplyAuthor, "Arial", 14, 160, Pos.BASELINE_LEFT, 170, 590, true);

		setupButtonUI(button_CreateReply, "Dialog", 14, 140, Pos.CENTER, 350, 590);
		button_CreateReply.setOnAction((_) -> { ControllerDiscussion.performCreateReply(); });

		setupLabelUI(label_ViewRepliesForPost, "Arial", 14, 170, Pos.BASELINE_LEFT, 520, 560);
		setupTextUI(text_ViewRepliesPostId, "Arial", 14, 70, Pos.BASELINE_LEFT, 690, 555, true);
		setupButtonUI(button_ViewReplies, "Dialog", 14, 120, Pos.CENTER, 690, 590);
		button_ViewReplies.setOnAction((_) -> { performViewRepliesForPost(); });

		setupTextAreaUI(area_RepliesForPost, "Arial", 12, 750, 110, 20, 635);

		// Place all widget items into the Root Pane's list of children
		theRootPane.getChildren().addAll(
				label_PageTitle, line_Separator1,

				label_PostSection,
				label_PostTitle, text_PostTitle,
				label_PostBody, text_PostBody,
				label_PostAuthor, text_PostAuthor,
				label_PostThread, text_PostThread,
				button_CreatePost,
				label_SearchPosts, text_SearchPosts, button_SearchPosts,
				label_DeletePost, text_DeletePostId, button_DeletePost,

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
				area_RepliesForPost
				);
	}

	/*-*******************************************************************************************

	Methods called by Controller
	
	*/

	/**
	 * Updates the "All Posts" and "Subset Posts" displays.
	 */
	protected static void updatePostListDisplays() {
		List<Post> all = ControllerDiscussion.getPostStore().getAllPosts();
		List<Post> subset = ControllerDiscussion.getPostStore().getSubsetPosts();

		area_AllPosts.setText(formatPosts(all));
		area_SubsetPosts.setText(formatPosts(subset));
	}

	/**
	 * Placeholder for controller calls (kept for consistency).
	 */
	protected static void updateReplyListDisplays() {
		// Replies are displayed when the user presses "View Replies".
	}

	/*-*******************************************************************************************

	Local helper actions
	
	*/

	private static void performViewRepliesForPost() {

		String raw = text_ViewRepliesPostId.getText();

		int postId;
		try {
			postId = Integer.parseInt(raw.trim());
		} catch (Exception e) {
			alertError.setContentText("Invalid post ID.");
			alertError.showAndWait();
			return;
		}

		List<Reply> replies = ControllerDiscussion.getReplyStore().getRepliesByPostId(postId);

		Post p = ControllerDiscussion.getPostStore().getPostById(postId);
		boolean postDeleted = (p != null && p.isDeleted());

		StringBuilder sb = new StringBuilder();

		if (replies.isEmpty()) {
			sb.append("No replies found.\n");
		} else {
			for (Reply r : replies) {
				sb.append("Reply ").append(r.getReplyId()).append(" (Post ").append(r.getPostId()).append(")\n");
				sb.append("Author: ").append(r.getAuthor()).append("\n");
				if (postDeleted) {
					sb.append("Original post has been deleted.\n");
				}
				sb.append(r.getBody()).append("\n");
				sb.append("----\n");
			}
		}

		area_RepliesForPost.setText(sb.toString());
	}

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

	/*-*******************************************************************************************

	Helper methods used to minimize the number of lines of code needed above
	
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