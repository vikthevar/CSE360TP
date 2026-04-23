package StaffFeedback;

import java.util.List;

import entityClasses.Post;
import entityClasses.User;
import guiDiscussion.ControllerDiscussion;
import guiDiscussion.ViewDiscussion;
import guiRole2.ViewRole2Home;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * <p> Title: ViewStaffFeedback Class. </p>
 *
 * <p> Description: This class represents the graphical user interface for staff
 * to review student posts and provide private feedback. Staff users can select
 * a post, enter feedback, and save it to the database.</p>
 *
 * <p>This TP3-oriented version loads flagged posts by default so staff can
 * review content that is most relevant to moderation and evaluation tasks.</p>
 *
 * @author David Rowlands
 */
public class ViewStaffFeedback {

    /** The primary stage used for this view. */
    private static Stage theStage;

    /** The currently logged-in user. */
    private static User theUser;

    /** Scene for the staff feedback interface. */
    private static Scene scene;

    /** Root layout pane. */
    private static Pane root;

    /** Title label for the page. */
    private static Label label_Title = new Label("Staff Feedback Page");

    /** Small status label for user feedback. */
    private static Label label_Status = new Label("");

    /** Text area for entering feedback. */
    private static TextArea text_Feedback = new TextArea();

    /** Button to save feedback. */
    private static Button button_Save = new Button("Save Feedback");

    /** Button to return to the staff home page. */
    private static Button button_Back = new Button("Back");

    /** Button to open the discussion page for moderation/review. */
    private static Button button_Discussion = new Button("Go To Discussion");

    /** ListView displaying available posts. */
    private static ListView<Post> list_Posts = new ListView<>();

    /**
     * Displays the staff feedback interface.
     *
     * @param stage the primary stage
     * @param user the currently logged-in staff user
     */
    public static void displayStaffFeedback(Stage stage, User user) {
        theStage = stage;
        theUser = user;

        root = new Pane();
        scene = new Scene(root, 950, 650);

        setupUI();

        theStage.setTitle("Staff Feedback");
        theStage.setScene(scene);
        theStage.show();
    }

    /**
     * Sets up all UI components, layouts, and event handlers
     * for the staff feedback page.
     */
    private static void setupUI() {
        label_Title.setFont(Font.font("Arial", 24));
        label_Title.setLayoutX(340);
        label_Title.setLayoutY(35);

        label_Status.setFont(Font.font("Arial", 13));
        label_Status.setLayoutX(50);
        label_Status.setLayoutY(85);

        list_Posts.setLayoutX(50);
        list_Posts.setLayoutY(120);
        list_Posts.setPrefSize(320, 380);

        text_Feedback.setLayoutX(420);
        text_Feedback.setLayoutY(120);
        text_Feedback.setPrefSize(470, 380);
        text_Feedback.setWrapText(true);
        text_Feedback.setPromptText("Enter private feedback for the selected post here...");

        button_Back.setLayoutX(50);
        button_Back.setLayoutY(550);

        button_Discussion.setLayoutX(140);
        button_Discussion.setLayoutY(550);

        button_Save.setLayoutX(760);
        button_Save.setLayoutY(550);

        loadPostsIntoList();

        list_Posts.setCellFactory(param -> new ListCell<Post>() {
            @Override
            protected void updateItem(Post p, boolean empty) {
                super.updateItem(p, empty);
                if (empty || p == null) {
                    setText(null);
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("[").append(p.getPostId()).append("] ")
                      .append(p.getTitle())
                      .append(" - ").append(p.getAuthor());

                    if (p.isFlagged()) {
                        sb.append(" [FLAGGED]");
                    }
                    if (p.isHidden()) {
                        sb.append(" [HIDDEN]");
                    }
                    if (p.isHighlighted()) {
                        sb.append(" [HIGHLIGHTED]");
                    }

                    setText(sb.toString());
                }
            }
        });

        button_Save.setOnAction(e -> {
            Post selected = list_Posts.getSelectionModel().getSelectedItem();

            if (selected == null) {
                showAlert(AlertType.WARNING, "No Post Selected", "Please select a post before saving feedback.");
                return;
            }

            String feedback = text_Feedback.getText();

            if (feedback == null || feedback.trim().isEmpty()) {
                showAlert(AlertType.WARNING, "Empty Feedback", "Feedback cannot be empty.");
                return;
            }

            try {
                ControllerDiscussion.getDatabase().hw2SaveFeedback(
                        selected.getPostId(),
                        selected.getAuthor(),
                        theUser.getUserName(),
                        feedback.trim()
                );

                showAlert(AlertType.INFORMATION, "Success", "Feedback saved successfully.");
                text_Feedback.clear();
                label_Status.setText("Feedback saved for Post " + selected.getPostId() + ".");
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(AlertType.ERROR, "Error", "Failed to save feedback. Please try again.");
            }
        });

        button_Back.setOnAction(e -> ViewRole2Home.displayRole2Home(theStage, theUser));

        button_Discussion.setOnAction(e -> ViewDiscussion.displayDiscussion(theStage, theUser));

        root.getChildren().addAll(
                label_Title, label_Status,
                list_Posts, text_Feedback,
                button_Back, button_Discussion, button_Save
        );
    }

    /**
     * Loads flagged posts into the list view. If no flagged posts exist,
     * the method falls back to loading all posts so the page is still usable.
     */
    private static void loadPostsIntoList() {
        try {
            List<Post> flaggedPosts = ControllerDiscussion.getPostStore().getFlaggedPosts();

            if (flaggedPosts != null && !flaggedPosts.isEmpty()) {
                list_Posts.getItems().setAll(flaggedPosts);
                label_Status.setText("Showing flagged posts for staff review.");
            } else {
                List<Post> allPosts = ControllerDiscussion.getPostStore().getAllPosts();
                list_Posts.getItems().setAll(allPosts);

                if (allPosts.isEmpty()) {
                    label_Status.setText("No posts are available.");
                } else {
                    label_Status.setText("No flagged posts found. Showing all posts instead.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            label_Status.setText("Failed to load posts.");
        }
    }

    /**
     * Displays a simple alert dialog.
     *
     * @param type the alert type
     * @param title the alert title
     * @param message the alert content message
     */
    private static void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}