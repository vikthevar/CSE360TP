package StaffFeedback;

import javafx.stage.Stage;
import entityClasses.User;
import StaffFeedback.ViewStaffFeedback;

/**
 * <p> Title: ControllerStaffFeedback Class. </p>
 *
 * <p> Description: This controller manages access to the staff feedback
 * interface. It ensures that only authorized staff users are able to
 * access and interact with the feedback system.</p>
 *
 * <p> The controller performs access control by verifying the user's role
 * before allowing navigation to the staff feedback view. If the user does
 * not have the appropriate permissions, access is denied.</p>
 *
 * @author David Rowlands
 */
public class ControllerStaffFeedback {

    /**
     * Displays the staff feedback interface if the user has staff permissions.
     *
     * <p> This method enforces access control by checking whether the user
     * has the staff role. If the user is not authorized, access is denied
     * and the interface is not displayed.</p>
     *
     * @param stage the primary stage used for displaying the interface
     * @param user the currently logged-in user attempting to access the page
     */
    public static void showStaffFeedback(Stage stage, User user) {

        // Access Control: ONLY staff allowed
        if (!user.getNewRole2()) {
            System.out.println("ACCESS DENIED: Not staff.");
            return;
        }

        ViewStaffFeedback.displayStaffFeedback(stage, user);
    }
}