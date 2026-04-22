package StaffFeedback;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import entityClasses.User;
import database.Database;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import java.util.List;
import java.util.ArrayList;
import entityClasses.Post;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * <p> Title: ViewStaffFeedback Class. </p>
 *
 * <p> Description: This class represents the graphical user interface for staff
 * to review student posts and provide private feedback. Staff users can select
 * a post, enter feedback, and save it to the database. Access is restricted to
 * authorized staff users through controller-level validation.</p>
 *
 * The interface includes:
 * <ul>
 *   <li>A list of student posts</li>
 *   <li>A text area for entering feedback</li>
 *   <li>Buttons for saving feedback and navigating back</li>
 * </ul>
 * 
 *
 * <p> Feedback is stored in the database and associated with a specific post
 * and student. Students can later view feedback directed to them through
 * their own interface.</p>
 *
 * @author David Rowlands
 */
public class ViewStaffFeedback {

    /** The primary stage used for this view */
    private static Stage theStage;

    /** The currently logged-in user */
    private static User theUser;

    /** Scene for the staff feedback interface */
    private static Scene scene;

    /** Root layout pane */
    private static Pane root;

    /** Title label for the page */
    private static Label label_Title = new Label("Staff Feedback Page");

    /** Text area for entering feedback */
    private static TextArea text_Feedback = new TextArea();

    /** Button to save feedback */
    private static Button button_Save = new Button("Save Feedback");

    /** Button to return to previous screen */
    private static Button button_Back = new Button("Back");

    /** ListView displaying available posts */
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
        scene = new Scene(root, 800, 600);

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
        label_Title.setLayoutX(250);
        label_Title.setLayoutY(50);

        text_Feedback.setLayoutX(350);
        text_Feedback.setLayoutY(150);
        text_Feedback.setPrefSize(500, 200);

        button_Save.setLayoutX(150);
        button_Save.setLayoutY(500);

        button_Back.setLayoutX(50);
        button_Back.setLayoutY(500);

        list_Posts.setLayoutX(50);
        list_Posts.setLayoutY(120);
        list_Posts.setPrefSize(250, 300);

        // Load posts from database
        try {
            Database db = new Database();
            db.connectToDatabase();

            list_Posts.getItems().setAll(db.hw2ListPosts());

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Customize how posts are displayed in the list
        list_Posts.setCellFactory(param -> new ListCell<Post>() {
            @Override
            protected void updateItem(Post p, boolean empty) {
                super.updateItem(p, empty);
                if (empty || p == null) {
                    setText(null);
                } else {
                    setText("[" + p.getPostId() + "] " + p.getTitle() + " - " + p.getAuthor());
                }
            }
        });

        /**
         * Handles saving feedback for a selected post.
         * Displays alerts for validation, success, or failure.
         */
        button_Save.setOnAction(e -> {
            Post selected = list_Posts.getSelectionModel().getSelectedItem();

            if (selected == null) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("No Post Selected");
                alert.setHeaderText(null);
                alert.setContentText("Please select a post before saving feedback.");
                alert.showAndWait();
                return;
            }

            String feedback = text_Feedback.getText();

            if (feedback == null || feedback.trim().isEmpty()) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Empty Feedback");
                alert.setHeaderText(null);
                alert.setContentText("Feedback cannot be empty.");
                alert.showAndWait();
                return;
            }

            try {
                Database db = new Database();
                db.connectToDatabase();

                db.hw2SaveFeedback(
                    selected.getPostId(),
                    selected.getAuthor(),
                    theUser.getUserName(),
                    feedback
                );

                Alert success = new Alert(AlertType.INFORMATION);
                success.setTitle("Success");
                success.setHeaderText(null);
                success.setContentText("Feedback saved successfully!");
                success.showAndWait();

                text_Feedback.clear();

            } catch (Exception ex) {
                ex.printStackTrace();

                Alert error = new Alert(AlertType.ERROR);
                error.setTitle("Error");
                error.setHeaderText(null);
                error.setContentText("Failed to save feedback. Please try again.");
                error.showAndWait();
            }
        });

        /**
         * Handles navigation back to the staff home screen.
         */
        button_Back.setOnAction(e -> {
            guiRole2.ViewRole2Home.displayRole2Home(theStage, theUser);
        });

        root.getChildren().addAll(label_Title, text_Feedback, button_Save, button_Back, list_Posts);
    }
}