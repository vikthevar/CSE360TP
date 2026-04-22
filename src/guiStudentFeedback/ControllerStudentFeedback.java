package guiStudentFeedback;

import javafx.stage.Stage;
import entityClasses.User;
import guiStudentFeedback.ViewStudentFeedback;

/**
 * <p> Title: ControllerStudentFeedback Class. </p>
 *
 * <p> Description: Controller for the student feedback feature. This class
 * handles access control and directs student users to the student feedback view.</p>
 *
 * @author David Rowlands
 */
public class ControllerStudentFeedback {

    /**
     * Displays the student feedback screen if the user has student permissions.
     *
     * @param stage the primary stage of the application
     * @param user the current logged-in user
     */
    public static void showStudentFeedback(Stage stage, User user) {

        // Access Control: ONLY students allowed
        if (!user.getNewRole1()) {
            System.out.println("ACCESS DENIED: Not a student.");
            return;
        }

        ViewStudentFeedback.displayStudentFeedback(stage, user);
    }
}