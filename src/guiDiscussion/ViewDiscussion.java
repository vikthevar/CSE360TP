package guiDiscussion;

import java.util.List;

import guiDiscussion.ModelDiscussion.Post;
import guiDiscussion.ModelDiscussion.Reply;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/*******
 * <p> Title: ViewDiscussion Class. </p>
 *
 * <p> Description: View for the standalone discussion CRUD demonstration. This
 * class defines all JavaFX widgets and scenes for the home page, post manager,
 * and reply manager.</p>
 */
public class ViewDiscussion {

	private static double width = applicationMain.DiscussionMain.WINDOW_WIDTH;
	private static double height = applicationMain.DiscussionMain.WINDOW_HEIGHT;

	// Shared stage and scenes
	protected static Stage theStage;
	private static Scene homeScene;
	private static Scene postScene;
	private static Scene replyScene;

	// Home page widgets
	private static Label label_HomeTitle = new Label();
	private static Label label_HomeDescription = new Label();
	private static Button button_HomeManagePosts = new Button("Manage Posts");
	private static Button button_HomeManageReplies = new Button("Manage Replies");
	private static Button button_HomeQuit = new Button("Quit");

	// Post manager widgets (simplified)
	private static Label label_PostTitleBanner = new Label("Manage Posts");
	private static Label label_PostId = new Label("Post ID:");
	protected static TextField text_PostId = new TextField();
	private static Label label_PostBody = new Label("Body:");
	protected static TextArea text_PostBody = new TextArea();
	private static Label label_PostTitle = new Label("Title / Description:");
	protected static TextField text_PostTitle = new TextField();
	private static Label label_PostSearch = new Label("Search (keyword in title/body):");
	protected static TextField text_PostSearch = new TextField();
	protected static Button button_PostCreate = new Button("Create");
	protected static Button button_PostUpdate = new Button("Update");
	protected static Button button_PostDelete = new Button("Delete");
	protected static Button button_PostClear = new Button("Clear");
	protected static Button button_PostSearch = new Button("Search");
	protected static Button button_PostShowAll = new Button("Show All");
	protected static Button button_PostBack = new Button("Back");
	protected static TextArea text_PostList = new TextArea();

	// Reply manager widgets
	private static Label label_ReplyTitleBanner = new Label("Manage Replies");
	private static Label label_ReplyId = new Label("Reply ID:");
	protected static TextField text_ReplyId = new TextField();
	private static Label label_ReplyPostId = new Label("Post ID:");
	protected static TextField text_ReplyPostId = new TextField();
	private static Label label_ReplyAuthor = new Label("Author:");
	protected static TextField text_ReplyAuthor = new TextField();
	private static Label label_ReplyBody = new Label("Body:");
	protected static TextArea text_ReplyBody = new TextArea();
	protected static CheckBox check_ReplyInstructor = new CheckBox("Instructor Answer");
	protected static CheckBox check_ReplyEndorsed = new CheckBox("Endorsed");
	private static Label label_ReplySearch = new Label("Search (keyword in body):");
	protected static TextField text_ReplySearch = new TextField();
	protected static Button button_ReplyCreate = new Button("Create");
	protected static Button button_ReplyUpdate = new Button("Update");
	protected static Button button_ReplyDelete = new Button("Delete");
	protected static Button button_ReplyClear = new Button("Clear");
	protected static Button button_ReplySearch = new Button("Search");
	protected static Button button_ReplyShowAll = new Button("Show All");
	protected static Button button_ReplyBack = new Button("Back");
	protected static TextArea text_ReplyList = new TextArea();

	private static boolean initialized = false;

	public static void displayHome(Stage ps) {
		theStage = ps;
		if (!initialized) {
			initializeAllScenes();
			initialized = true;
		}
		theStage.setTitle("CSE 360 Discussion CRUD Demo");
		theStage.setScene(homeScene);
		theStage.show();
	}

	protected static void showPostScene() {
		refreshPostList(ModelDiscussion.getAllPosts());
		theStage.setTitle("Manage Posts");
		theStage.setScene(postScene);
		theStage.show();
	}

	protected static void showReplyScene() {
		refreshReplyList(ModelDiscussion.getAllReplies());
		theStage.setTitle("Manage Replies");
		theStage.setScene(replyScene);
		theStage.show();
	}

	protected static void refreshPostList(List<Post> subset) {
		StringBuilder sb = new StringBuilder();
		for (Post p : subset) {
			String body = p.getBody();
			if (body == null) {
				body = "";
			}
			String preview = body.length() > 200 ? body.substring(0, 200) + "..." : body;
			sb.append(p.getTitle()).append("  |  ").append(preview).append(System.lineSeparator());
		}
		text_PostList.setText(sb.toString());
	}

	protected static void refreshReplyList(List<Reply> subset) {
		StringBuilder sb = new StringBuilder();
		for (Reply r : subset) {
			sb.append(r.toString()).append(System.lineSeparator());
		}
		text_ReplyList.setText(sb.toString());
	}

	private static void initializeAllScenes() {
		initializeHomeScene();
		initializePostScene();
		initializeReplyScene();
	}

	private static void initializeHomeScene() {
		Pane root = new Pane();
		homeScene = new Scene(root, width, height);

		label_HomeTitle.setText("Discussion CRUD Demonstration");
		setupLabel(label_HomeTitle, "Arial", 28, width, Pos.CENTER, 0, 40);

		label_HomeDescription.setText("Standalone posts and replies with simple CRUD operations.");
		setupLabel(label_HomeDescription, "Arial", 16, width, Pos.CENTER, 0, 90);

		setupButton(button_HomeManagePosts, "Dialog", 18, 250, Pos.CENTER, 275, 160);
		button_HomeManagePosts.setOnAction((_) -> ControllerDiscussion.showPostManager());

		setupButton(button_HomeManageReplies, "Dialog", 18, 250, Pos.CENTER, 275, 220);
		button_HomeManageReplies.setOnAction((_) -> ControllerDiscussion.showReplyManager());

		setupButton(button_HomeQuit, "Dialog", 18, 250, Pos.CENTER, 275, 320);
		button_HomeQuit.setOnAction((_) -> ControllerDiscussion.performQuit());

		root.getChildren().addAll(label_HomeTitle, label_HomeDescription, button_HomeManagePosts,
				button_HomeManageReplies, button_HomeQuit);
	}

	private static void initializePostScene() {
		Pane root = new Pane();
		postScene = new Scene(root, width, height);

		setupLabel(label_PostTitleBanner, "Arial", 24, width, Pos.CENTER, 0, 5);

		setupLabel(label_PostId, "Arial", 14, 120, Pos.BASELINE_LEFT, 20, 60);
		setupTextField(text_PostId, "Arial", 14, 80, Pos.BASELINE_LEFT, 140, 56, true);

		// Body first
		setupLabel(label_PostBody, "Arial", 14, 140, Pos.BASELINE_LEFT, 20, 95);
		text_PostBody.setFont(Font.font("Arial", 14));
		text_PostBody.setWrapText(true);
		text_PostBody.setPrefWidth(400);
		text_PostBody.setPrefHeight(140);
		text_PostBody.setLayoutX(140);
		text_PostBody.setLayoutY(91);

		// Then description / title
		setupLabel(label_PostTitle, "Arial", 14, 160, Pos.BASELINE_LEFT, 20, 245);
		setupTextField(text_PostTitle, "Arial", 14, 400, Pos.BASELINE_LEFT, 180, 241, true);

		setupLabel(label_PostSearch, "Arial", 14, 260, Pos.BASELINE_LEFT, 20, 285);
		setupTextField(text_PostSearch, "Arial", 14, 260, Pos.BASELINE_LEFT, 280, 281, true);

		setupButton(button_PostCreate, "Dialog", 14, 90, Pos.CENTER, 580, 80);
		button_PostCreate.setOnAction((_) -> ControllerDiscussion.performCreatePost());

		setupButton(button_PostUpdate, "Dialog", 14, 90, Pos.CENTER, 580, 120);
		button_PostUpdate.setOnAction((_) -> ControllerDiscussion.performUpdatePost());

		setupButton(button_PostDelete, "Dialog", 14, 90, Pos.CENTER, 580, 160);
		button_PostDelete.setOnAction((_) -> ControllerDiscussion.performDeletePost());

		setupButton(button_PostClear, "Dialog", 14, 90, Pos.CENTER, 580, 200);
		button_PostClear.setOnAction((_) -> ControllerDiscussion.performClearPostFields());

		setupButton(button_PostSearch, "Dialog", 14, 90, Pos.CENTER, 580, 340);
		button_PostSearch.setOnAction((_) -> ControllerDiscussion.performSearchPosts());

		setupButton(button_PostShowAll, "Dialog", 14, 90, Pos.CENTER, 580, 380);
		button_PostShowAll.setOnAction((_) -> ControllerDiscussion.performShowAllPosts());

		setupButton(button_PostBack, "Dialog", 14, 90, Pos.CENTER, 580, 420);
		button_PostBack.setOnAction((_) -> ControllerDiscussion.showHome());

		text_PostList.setFont(Font.font("Monospaced", 12));
		text_PostList.setEditable(false);
		text_PostList.setWrapText(true);
		text_PostList.setPrefWidth(520);
		text_PostList.setPrefHeight(200);
		text_PostList.setLayoutX(20);
		text_PostList.setLayoutY(320);

		root.getChildren().addAll(label_PostTitleBanner, label_PostId, text_PostId,
				label_PostBody, text_PostBody,
				label_PostTitle, text_PostTitle,
				label_PostSearch, text_PostSearch,
				button_PostCreate, button_PostUpdate,
				button_PostDelete, button_PostClear,
				button_PostSearch, button_PostShowAll,
				button_PostBack, text_PostList);
	}

	private static void initializeReplyScene() {
		Pane root = new Pane();
		replyScene = new Scene(root, width, height);

		setupLabel(label_ReplyTitleBanner, "Arial", 24, width, Pos.CENTER, 0, 5);

		setupLabel(label_ReplyId, "Arial", 14, 120, Pos.BASELINE_LEFT, 20, 60);
		setupTextField(text_ReplyId, "Arial", 14, 80, Pos.BASELINE_LEFT, 140, 56, true);

		setupLabel(label_ReplyPostId, "Arial", 14, 120, Pos.BASELINE_LEFT, 20, 95);
		setupTextField(text_ReplyPostId, "Arial", 14, 80, Pos.BASELINE_LEFT, 140, 91, true);

		setupLabel(label_ReplyAuthor, "Arial", 14, 120, Pos.BASELINE_LEFT, 20, 130);
		setupTextField(text_ReplyAuthor, "Arial", 14, 200, Pos.BASELINE_LEFT, 140, 126, true);

		setupLabel(label_ReplyBody, "Arial", 14, 120, Pos.BASELINE_LEFT, 20, 165);
		text_ReplyBody.setFont(Font.font("Arial", 14));
		text_ReplyBody.setWrapText(true);
		text_ReplyBody.setPrefWidth(400);
		text_ReplyBody.setPrefHeight(180);
		text_ReplyBody.setLayoutX(140);
		text_ReplyBody.setLayoutY(161);

		check_ReplyInstructor.setLayoutX(140);
		check_ReplyInstructor.setLayoutY(350);
		check_ReplyEndorsed.setLayoutX(280);
		check_ReplyEndorsed.setLayoutY(350);

		setupLabel(label_ReplySearch, "Arial", 14, 260, Pos.BASELINE_LEFT, 20, 385);
		setupTextField(text_ReplySearch, "Arial", 14, 260, Pos.BASELINE_LEFT, 280, 381, true);

		setupButton(button_ReplyCreate, "Dialog", 14, 90, Pos.CENTER, 580, 80);
		button_ReplyCreate.setOnAction((_) -> ControllerDiscussion.performCreateReply());

		setupButton(button_ReplyUpdate, "Dialog", 14, 90, Pos.CENTER, 580, 120);
		button_ReplyUpdate.setOnAction((_) -> ControllerDiscussion.performUpdateReply());

		setupButton(button_ReplyDelete, "Dialog", 14, 90, Pos.CENTER, 580, 160);
		button_ReplyDelete.setOnAction((_) -> ControllerDiscussion.performDeleteReply());

		setupButton(button_ReplyClear, "Dialog", 14, 90, Pos.CENTER, 580, 200);
		button_ReplyClear.setOnAction((_) -> ControllerDiscussion.performClearReplyFields());

		setupButton(button_ReplySearch, "Dialog", 14, 90, Pos.CENTER, 580, 380);
		button_ReplySearch.setOnAction((_) -> ControllerDiscussion.performSearchReplies());

		setupButton(button_ReplyShowAll, "Dialog", 14, 90, Pos.CENTER, 580, 420);
		button_ReplyShowAll.setOnAction((_) -> ControllerDiscussion.performShowAllReplies());

		setupButton(button_ReplyBack, "Dialog", 14, 90, Pos.CENTER, 580, 460);
		button_ReplyBack.setOnAction((_) -> ControllerDiscussion.showHome());

		text_ReplyList.setFont(Font.font("Monospaced", 12));
		text_ReplyList.setEditable(false);
		text_ReplyList.setWrapText(true);
		text_ReplyList.setPrefWidth(520);
		text_ReplyList.setPrefHeight(160);
		text_ReplyList.setLayoutX(20);
		text_ReplyList.setLayoutY(420);

		root.getChildren().addAll(label_ReplyTitleBanner, label_ReplyId, text_ReplyId, label_ReplyPostId,
				text_ReplyPostId, label_ReplyAuthor, text_ReplyAuthor, label_ReplyBody, text_ReplyBody,
				check_ReplyInstructor, check_ReplyEndorsed, label_ReplySearch, text_ReplySearch, button_ReplyCreate,
				button_ReplyUpdate, button_ReplyDelete, button_ReplyClear, button_ReplySearch, button_ReplyShowAll,
				button_ReplyBack, text_ReplyList);
	}

	private static void setupLabel(Label l, String ff, double f, double w, Pos p, double x, double y) {
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}

	private static void setupButton(Button b, String ff, double f, double w, Pos p, double x, double y) {
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}

	private static void setupTextField(TextField t, String ff, double f, double w, Pos p, double x, double y,
			boolean e) {
		t.setFont(Font.font(ff, f));
		t.setMinWidth(w);
		t.setMaxWidth(w);
		t.setAlignment(p);
		t.setLayoutX(x);
		t.setLayoutY(y);
		t.setEditable(e);
	}
}

