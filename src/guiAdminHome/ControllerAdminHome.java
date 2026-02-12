package guiAdminHome;

import database.Database;
import emailAddressRecognizer.EmailAddressRecognizer;
import java.util.List;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import java.util.Optional;

/*******
 * <p> Title: GUIAdminHomePage Class. </p>
 * 
 * <p> Description: The Java/FX-based Admin Home Page.  This class provides the controller actions
 * basic on the user's use of the JavaFX GUI widgets defined by the View class.
 * 
 * This page contains a number of buttons that have not yet been implemented.  WHen those buttons
 * are pressed, an alert pops up to tell the user that the function associated with the button has
 * not been implemented. Also, be aware that What has been implemented may not work the way the
 * final product requires and there maybe defects in this code.
 * 
 * The class has been written assuming that the View or the Model are the only class methods that
 * can invoke these methods.  This is why each has been declared at "protected".  Do not change any
 * of these methods to public.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-17 Initial version
 * @version 1.01		2025-09-16 Update Javadoc documentation *  
 */

public class ControllerAdminHome {
	
	/*-*******************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	*/
	
	/**
	 * Default constructor is not used.
	 */
	public ControllerAdminHome() {
	}
	
	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/**********
	 * <p> 
	 * 
	 * Title: performInvitation () Method. </p>
	 * 
	 * <p> Description: Protected method to send an email inviting a potential user to establish
	 * an account and a specific role. </p>
	 */
	protected static void performInvitation () {
		// Verify that the email address is valid - If not alert the user and return
		String emailAddress = ViewAdminHome.text_InvitationEmailAddress.getText();
		if (invalidEmailAddress(emailAddress)) {
			return;
		}
		
		// Check to ensure that we are not sending a second message with a new invitation code to
		// the same email address.  
		if (theDatabase.emailaddressHasBeenUsed(emailAddress)) {
			ViewAdminHome.alertEmailError.setContentText(
					"An invitation has already been sent to this email address.");
			ViewAdminHome.alertEmailError.showAndWait();
			return;
		}
		
		// Inform the user that the invitation has been sent and display the invitation code
		String theSelectedRole = (String) ViewAdminHome.combobox_SelectRole.getValue();
		String invitationCode = theDatabase.generateInvitationCode(emailAddress,
				theSelectedRole);
		String msg = "Code: " + invitationCode + " for role " + theSelectedRole + 
				" was sent to: " + emailAddress;
		System.out.println(msg);
		ViewAdminHome.alertEmailSent.setContentText(msg);
		ViewAdminHome.alertEmailSent.showAndWait();
		
		// Update the Admin Home pages status
		ViewAdminHome.text_InvitationEmailAddress.setText("");
		ViewAdminHome.label_NumberOfInvitations.setText("Number of outstanding invitations: " + 
				theDatabase.getNumberOfInvitations());
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: manageInvitations () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void manageInvitations () {
		
		
		System.out.println("\n*** WARNING ***: Manage Invitations Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.setTitle("*** WARNING ***");
		ViewAdminHome.alertNotImplemented.setHeaderText("Manage Invitations Issue");
		ViewAdminHome.alertNotImplemented.setContentText("Manage Invitations Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.showAndWait();
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: setOnetimePassword () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void setOnetimePassword () {
		
		String username = ViewAdminHome.text_TargetUsername.getText().trim();
				
		if (username.isEmpty()) {
			ViewAdminHome.alertNotImplemented.setTitle("*** Error ***");
			ViewAdminHome.alertNotImplemented.setHeaderText(null);
			ViewAdminHome.alertNotImplemented.setContentText("Please Enter Username");
			ViewAdminHome.alertNotImplemented.showAndWait();
	        return;
	    }
		
		if (!theDatabase.doesUserExist(username)) {
			ViewAdminHome.alertNotImplemented.setTitle("*** Error ***");
			ViewAdminHome.alertNotImplemented.setHeaderText(null);
			ViewAdminHome.alertNotImplemented.setContentText("User does not exist");
			ViewAdminHome.alertNotImplemented.showAndWait();
	        return;
	    }
		
		// Generates new Password
		String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";
	    StringBuilder password = new StringBuilder();

	    for (int i = 0; i < 8; i++) {
	        int index = (int) (Math.random() * chars.length());
	        password.append(chars.charAt(index));
	    }

		String tempPassword = password.toString();

		boolean success = theDatabase.setOneTimePassword(username, tempPassword);

	    if (!success) {
	    	ViewAdminHome.alertNotImplemented.setTitle("*** Error ***");
			ViewAdminHome.alertNotImplemented.setHeaderText(null);
			ViewAdminHome.alertNotImplemented.setContentText("falied to set one time password");
			ViewAdminHome.alertNotImplemented.showAndWait();
	        return;
	    }

	    ViewAdminHome.alertNotImplemented.setTitle("One-Time Password Created");
		ViewAdminHome.alertNotImplemented.setHeaderText(null);
		ViewAdminHome.alertNotImplemented.setContentText("Temporary password for user \"" + username + "\" is:\n\n" + tempPassword);
		ViewAdminHome.alertNotImplemented.showAndWait();


	}
	
	/**********
	 * <p> 
	 * 
	 * Title: deleteUser () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void deleteUser() {
		
		String username = ViewAdminHome.text_TargetUsername.getText().trim();
		
		if (username.isEmpty()) {
			ViewAdminHome.alertNotImplemented.setTitle("*** Error ***");
			ViewAdminHome.alertNotImplemented.setHeaderText(null);
			ViewAdminHome.alertNotImplemented.setContentText("Please Enter Username");
			ViewAdminHome.alertNotImplemented.showAndWait();
	        return;
	    }
		
		if (!theDatabase.doesUserExist(username)) {
			ViewAdminHome.alertNotImplemented.setTitle("*** Error ***");
			ViewAdminHome.alertNotImplemented.setHeaderText(null);
			ViewAdminHome.alertNotImplemented.setContentText("User does not exist");
			ViewAdminHome.alertNotImplemented.showAndWait();
	        return;
	    }
		
		Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
	    confirmAlert.setTitle("Confirm Deletion");
	    confirmAlert.setHeaderText(null);
	    confirmAlert.setContentText("Are you sure you want to delete the account \"" + username + "\"?");
	    
	    ButtonType yesButton = new ButtonType("Yes");
	    ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
	    confirmAlert.getButtonTypes().setAll(yesButton, noButton);

	    Optional<ButtonType> result = confirmAlert.showAndWait();
	    if(result.isPresent() && result.get() == yesButton) {
	        // Delete the user
	        boolean success = theDatabase.deleteUser(username);
	        ViewAdminHome.alertNotImplemented.setTitle(success ? "Success" : "Error");
	        ViewAdminHome.alertNotImplemented.setHeaderText(null);
	        ViewAdminHome.alertNotImplemented.setContentText(success ? 
	            "User \"" + username + "\" has been deleted." :
	            "Failed to delete user \"" + username + "\".");
	        ViewAdminHome.alertNotImplemented.showAndWait();
	    }

	}
	
	/**********
	 * <p> 
	 * 
	 * Title: listUsers () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void listUsers() {
		
		List<String> userList = theDatabase.getAllUsersForDisplay(); // 'database' is your Database instance
	    if(userList.isEmpty()) {
	        ViewAdminHome.alertNotImplemented.setTitle("User List");
	        ViewAdminHome.alertNotImplemented.setHeaderText(null);
	        ViewAdminHome.alertNotImplemented.setContentText("No users found in the database.");
	        ViewAdminHome.alertNotImplemented.showAndWait();
	        return;
	    }

	    // Build the message for the popup
	    StringBuilder message = new StringBuilder();
	    for(String user : userList) {
	        message.append(user).append("\n"); // extra newline between users
	    }

	    // Show the alert popup
	    ViewAdminHome.alertNotImplemented.setTitle("User List");
	    ViewAdminHome.alertNotImplemented.setHeaderText(null);
	    ViewAdminHome.alertNotImplemented.setContentText(message.toString());
	    ViewAdminHome.alertNotImplemented.showAndWait();

	}
	
	/**********
	 * <p> 
	 * 
	 * Title: addRemoveRoles () Method. </p>
	 * 
	 * <p> Description: Protected method that allows an admin to add and remove roles for any of
	 * the users currently in the system.  This is done by invoking the AddRemoveRoles Page. There
	 * is no need to specify the home page for the return as this can only be initiated by and
	 * Admin.</p>
	 */
	protected static void addRemoveRoles() {
		guiAddRemoveRoles.ViewAddRemoveRoles.displayAddRemoveRoles(ViewAdminHome.theStage, 
				ViewAdminHome.theUser);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: invalidEmailAddress () Method. </p>
	 * 
	 * <p> Description: Protected method that is intended to check an email address before it is
	 * used to reduce errors.  The code currently only checks to see that the email address is not
	 * empty.  In the future, a syntactic check must be performed and maybe there is a way to check
	 * if a properly email address is active.</p>
	 * 
	 * @param emailAddress	This String holds what is expected to be an email address
	 */
	protected static boolean invalidEmailAddress(String emailAddress) {
		//Trim input
	    emailAddress = (emailAddress == null) ? "" : emailAddress.trim();

	    // FSM email validation
	    String emailError = EmailAddressRecognizer.checkEmailAddress(emailAddress);

	    if (!emailError.isEmpty()) {
	        ViewAdminHome.alertEmailError.setContentText(emailError.trim());
	        ViewAdminHome.alertEmailError.showAndWait();
	        return true;
	    }
	    return false;
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: performLogout () Method. </p>
	 * 
	 * <p> Description: Protected method that logs this user out of the system and returns to the
	 * login page for future use.</p>
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewAdminHome.theStage);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: performQuit () Method. </p>
	 * 
	 * <p> Description: Protected method that gracefully terminates the execution of the program.
	 * </p>
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}
