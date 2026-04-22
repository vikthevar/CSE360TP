package guiStudentFeedback;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import entityClasses.User;
import database.Database;
import java.util.List;

/**
 * <p> Title: ViewStudentFeedback Class. </p>
 *
 * <p> Description: This class represents the graphical user interface for
 * students to view feedback provided by staff. The interface displays all
 * feedback associated with the currently logged-in student, retrieved from
 * the database.</p>
 *
 * <p> Students are restricted to viewing feedback only and do not have
 * permissions to create, modify, or delete feedback. This enforces proper
 * access control and supports the evaluation process.</p>
 *
 * The interface includes:
 * <ul>
 *   <li>A title label</li>
 *   <li>A display area showing feedback entries</li>
 *   <li>A navigation button to return to the home screen</li>
 * </ul>
 * 
 *
 * @author David Rowlands
 */
public class ViewStudentFeedback {

    /** The primary stage used for this view */
    private static Stage theStage;

    /** The currently logged-in user */
    private static User theUser;

    /** Scene for the student feedback interface */
    private static Scene scene;

    /** Root layout pane */
    private static Pane root;

    /** Title label for the page */
    private static Label label_Title = new Label("Student Feedback Page");

    /** Label used to display feedback content */
    private static Label label_Feedback = new Label("Your feedback will appear here.");

    /** Button to return to previous screen */
    private static Button button_Back = new Button("Back");

    /**
     * Displays the student feedback interface.
     *
     * @param stage the primary stage
     * @param user the currently logged-in student user
     */
    public static void displayStudentFeedback(Stage stage, User user) {
        theStage = stage;
        theUser = user;

        root = new Pane();
        scene = new Scene(root, 800, 600);

        setupUI();

        theStage.setTitle("Student Feedback");
        theStage.setScene(scene);
        theStage.show();
    }

    /**
     * Sets up all UI components, layouts, and data loading
     * for the student feedback page.
     */
    private static void setupUI() {

        label_Title.setFont(Font.font("Arial", 24));
        label_Title.setLayoutX(250);
        label_Title.setLayoutY(50);

        label_Feedback.setLayoutX(200);
        label_Feedback.setLayoutY(150);

        button_Back.setLayoutX(50);
        button_Back.setLayoutY(500);

        /**
         * Handles navigation back to the student home screen.
         */
        button_Back.setOnAction(e -> {
            guiRole1.ViewRole1Home.displayRole1Home(theStage, theUser);
        });

        /**
         * Loads feedback for the current student from the database
         * and displays it in the label.
         */
        try {
            Database db = new Database();
            db.connectToDatabase();

            List<String> feedbackList = db.hw2GetFeedbackForStudent(theUser.getUserName());

            StringBuilder sb = new StringBuilder();

            for (String f : feedbackList) {
                sb.append(f).append("\n-------------------\n");
            }

            label_Feedback.setText(sb.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        root.getChildren().addAll(label_Title, label_Feedback, button_Back);
    }
}