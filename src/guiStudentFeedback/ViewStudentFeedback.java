package guiStudentFeedback;

import java.util.List;

import entityClasses.User;
import guiDiscussion.ControllerDiscussion;
import guiRole1.ViewRole1Home;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * <p> Title: ViewStudentFeedback Class. </p>
 *
 * <p> Description: This class represents the graphical user interface for
 * students to view feedback provided by staff. The interface displays all
 * feedback associated with the currently logged-in student.</p>
 *
 * <p>Students are restricted to viewing feedback only and do not have
 * permissions to create, modify, or delete feedback.</p>
 *
 * @author David Rowlands
 */
public class ViewStudentFeedback {

    /** The primary stage used for this view. */
    private static Stage theStage;

    /** The currently logged-in user. */
    private static User theUser;

    /** Scene for the student feedback interface. */
    private static Scene scene;

    /** Root layout pane. */
    private static Pane root;

    /** Title label for the page. */
    private static Label label_Title = new Label("Student Feedback Page");

    /** Text area used to display feedback content. */
    private static TextArea area_Feedback = new TextArea();

    /** Button to return to previous screen. */
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
        scene = new Scene(root, 850, 600);

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
        label_Title.setLayoutX(270);
        label_Title.setLayoutY(50);

        area_Feedback.setLayoutX(80);
        area_Feedback.setLayoutY(130);
        area_Feedback.setPrefSize(680, 360);
        area_Feedback.setEditable(false);
        area_Feedback.setWrapText(true);

        button_Back.setLayoutX(80);
        button_Back.setLayoutY(520);

        button_Back.setOnAction(e -> ViewRole1Home.displayRole1Home(theStage, theUser));

        loadStudentFeedback();

        root.getChildren().addAll(label_Title, area_Feedback, button_Back);
    }

    /**
     * Loads all feedback entries for the currently logged-in student.
     * If no feedback exists, a helpful message is displayed instead.
     */
    private static void loadStudentFeedback() {
        try {
            List<String> feedbackList = ControllerDiscussion.getDatabase()
                    .hw2GetFeedbackForStudent(theUser.getUserName());

            if (feedbackList == null || feedbackList.isEmpty()) {
                area_Feedback.setText("No feedback available yet.");
                return;
            }

            StringBuilder sb = new StringBuilder();
            for (String f : feedbackList) {
                sb.append(f).append("\n-------------------\n");
            }

            area_Feedback.setText(sb.toString());

        } catch (Exception e) {
            e.printStackTrace();
            area_Feedback.setText("Unable to load feedback at this time.");
        }
    }
}