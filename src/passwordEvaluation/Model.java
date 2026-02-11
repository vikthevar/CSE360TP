package passwordEvaluation;

import javafx.scene.paint.Color;

/*******
 * <p> Title: Model Class - establishes the required GUI data and the computations.
 * </p>
 *
 * <p> Description: This Model class is a major component of a Model View Controller (MVC)
 * application design that provides the user with a Graphical User Interface using JavaFX
 * widgets as opposed to a command line interface.
 * 
 * In this case the Model deals with an input from the user and checks to see if it conforms to
 * the requirements specified by a graphical representation of a finite state machine.
 * 
 * This is a purely static component of the MVC implementation.  There is no need to instantiate
 * the class.
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Lynn Robert Carter
 *
 * @version 2.00	2025-07-30 Rewrite of this application for the Fall 2025 offering of CSE 360
 * and other ASU courses.
 */

public class Model {
		
	/*******
	 * <p> Title: updatePassword - Protected Method </p>
	 * 
	 * <p> Description: This method is called every time the user changes the password (e.g., with 
	 * every key pressed) using the GUI from the PasswordEvaluationGUITestbed.  It resets the 
	 * messages associated with each of the requirements and then evaluates the current password
	 * with respect to those requirements.  The results of that evaluation are display via the View
	 * to the user and via the console.</p>
	 */
	
	/*
	 * Lines 41-105 & lines 147-183 have been commented out
	 * The View-related UI update logic has been commented out because this TP1
	 * implementation does not use the original PasswordEvaluation testbed View.
	 * 
	 * In this project, the Controller is responsible for updating the GUI components,
	 * and the Model is used strictly for password validation logic (evaluatePassword).
	 * 
	 * Keeping UI references (View.*) inside the Model creates dependency issues
	 * and compilation errors, so the UI update code was removed to maintain
	 * proper separation of concerns and avoid build errors.
	 */
	
	
	/*
	protected static void updatePassword() {
		View.resetAssessments();						// Reset the assessment flags to the
		String password = View.text_Password.getText();	// initial state and fetch the input
		
		// If the input is empty, clear the aspects of the user interface having to do with the
		// user input and tell the user that the input is empty.
		if (password.isEmpty()) {
			View.errPasswordPart1.setText("");
			View.errPasswordPart2.setText("");
			View.noInputFound.setText("No input text found!");
		}
		else
		{
			// There is user input, so evaluate it to see if it satisfies the requirements
			String errMessage = evaluatePassword(password);
			
			// Based on the evaluation, change the flag to green for each satisfied requirement
			updateFlags();
			
			// An empty string means there is no error message, which means the input is valid
			if (errMessage != "") {
				
				// Since the output is not empty, at least one requirement have not been satisfied.
				System.out.println(errMessage);			// Display the message to the console
				
				View.noInputFound.setText("");			// There was input, so no error message
				
				// Extract the input up to the point of the error and place it in Part 1
				View.errPasswordPart1.setText(password.substring(0, passwordIndexofError));
				
				// Place the red up arrow into Part 2
				View.errPasswordPart2.setText("\u21EB");
				
				// Tell the user about the meaning of the red up arrow
				View.errPasswordPart3.setText(
						"The red arrow points at the character causing the error!");
				
				// Tell the user that the password is not valid with a red message
				View.validPassword.setTextFill(Color.RED);
				View.validPassword.setText("Failure! The password is not valid.");
				
				// Ensure the button is disabled
				View.button_Finish.setDisable(true);
			}
			else {
				// All the requirements were satisfied - the password is valid
				System.out.println("Success! The password satisfies the requirements.");
				
				// Hide all of the error messages elements
				View.errPasswordPart1.setText("");
				View.errPasswordPart2.setText("");
				View.errPasswordPart3.setText("");
				
				// Tell the user that the password is valid with a green message
				View.validPassword.setTextFill(Color.GREEN);
				View.validPassword.setText("Success! The password satisfies the requirements.");
				
				// Enable the button so the user can accept this password or continue to add
				// more characters to the password and make it longer.
				View.button_Finish.setDisable(false);
			} 
		}
	}
	*/
	
	/*-********************************************************************************************
	 * 
	 * Attributes used by the Finite State Machine to inform the user about what was and was not
	 * valid and point to the character of the error.  This will enhance the user experience.
	 * 
	 */

	public static String passwordErrorMessage = "";		// The error message text
	public static String passwordInput = "";			// The input being processed
	public static int passwordIndexofError = -1;		// The index where the error was located
	public static boolean foundUpperCase = false;
	public static boolean foundLowerCase = false;
	public static boolean foundNumericDigit = false;
	public static boolean foundSpecialChar = false;
	public static boolean foundLongEnough = false;
	public static boolean foundTooLong = false;
	private static String inputLine = "";				// The input line
	private static char currentChar;					// The current character in the line
	private static int currentCharNdx;					// The index of the current character
	private static boolean running;						// The flag that specifies if the FSM is 
														// running

	/*
	 * This private method displays the input line and then on a line under it displays the input
	 * up to the point of the error.  At that point, a question mark is place and the rest of the 
	 * input is ignored. This method is designed to be used to display information to make it clear
	 * to the user where the error in the input can be found, and show that on the console 
	 * terminal.
	 * 
	 */
	
	private static void displayInputState() {
		// Display the entire input line
		System.out.println(inputLine);
		System.out.println(inputLine.substring(0,currentCharNdx) + "?");
		System.out.println("The password size: " + inputLine.length() + "  |  The currentCharNdx: " + 
				currentCharNdx + "  |  The currentChar: \"" + currentChar + "\"");
	}
	
	
	/*
	 * This private method checks each of the requirements and if one is satisfied, it changes the
	 * the text to tell the user of this fact and changes the text color from red to green.
	 * 
	 */
	/*
	private static void updateFlags() {
		if (foundUpperCase) {
			View.label_UpperCase.setText("At least one upper case letter - Satisfied");
			View.label_UpperCase.setTextFill(Color.GREEN);
		}

		if (foundLowerCase) {
			View.label_LowerCase.setText("At least one lower case letter - Satisfied");
			View.label_LowerCase.setTextFill(Color.GREEN);
		}

		if (foundNumericDigit) {
			View.label_NumericDigit.setText("At least one numeric digit - Satisfied");
			View.label_NumericDigit.setTextFill(Color.GREEN);
		}

		if (foundSpecialChar) {
			View.label_SpecialChar.setText("At least one special character - Satisfied");
			View.label_SpecialChar.setTextFill(Color.GREEN);
		}

		if (foundLongEnough) {
			View.label_LongEnough.setText("At least eight characters - Satisfied");
			View.label_LongEnough.setTextFill(Color.GREEN);
		}
		if (!foundTooLong) {
			View.label_TooLong.setText("Less than sixtyfour characters - Satisfied");
			View.label_TooLong.setTextFill(Color.GREEN);
		}
	}
	*/

	/**********
	 * <p> Title: evaluatePassword - Public Method </p>
	 * 
	 * <p> Description: This method is a mechanical transformation of a Directed Graph diagram 
	 * into a Java method. This method is used by both the GUI version of the application as well
	 * as the testing automation version.
	 * 
	 * @param input		The input string evaluated by the directed graph processing
	 * @return			An output string that is empty if every things is okay or it will be
	 * 						a string with a helpful description of the error follow by two lines
	 * 						that shows the input line follow by a line with an up arrow at the
	 *						point where the error was found.
	 */
	
	public static String evaluatePassword(String input) {
		// The following are the local variable used to perform the Directed Graph simulation
		passwordErrorMessage = "";
		passwordIndexofError = 0;			// Initialize the IndexofError
		inputLine = input;					// Save the reference to the input line as a global
		currentCharNdx = 0;					// The index of the current character
		
		if(input.length() <= 0) {
			return "*** Error *** The password is empty!";
		}
		
		// The input is not empty, so we can access the first character
		currentChar = input.charAt(0);		// The current character from the above indexed position

		// The Directed Graph simulation continues until the end of the input is reached or at some 
		// state the current character does not match any valid transition to a next state.  This
		// local variable is a working copy of the input.
		passwordInput = input;				// Save a copy of the input
		
		// The following are the attributes associated with each of the requirements
		foundUpperCase = false;				// Reset the Boolean flag
		foundLowerCase = false;				// Reset the Boolean flag
		foundNumericDigit = false;			// Reset the Boolean flag
		foundSpecialChar = false;			// Reset the Boolean flag
		foundNumericDigit = false;			// Reset the Boolean flag
		foundLongEnough = false;			// Reset the Boolean flag
		foundTooLong = false;
		
		// This flag determines whether the directed graph (FSM) loop is operating or not
		running = true;						// Start the loop

		// The Directed Graph simulation continues until the end of the input is reached or at some
		// state the current character does not match any valid transition
		while (running) {
			displayInputState();
			// The cascading if statement sequentially tries the current character against all of
			// the valid transitions, each associated with one of the requirements
			if (currentChar >= 'A' && currentChar <= 'Z') {
				System.out.println("Upper case letter found");
				foundUpperCase = true;
			} else if (currentChar >= 'a' && currentChar <= 'z') {
				System.out.println("Lower case letter found");
				foundLowerCase = true;
			} else if (currentChar >= '0' && currentChar <= '9') {
				System.out.println("Digit found");
				foundNumericDigit = true;
			} else if ("~`!@#$%^&*()_-+={}[]|\\:;\"'<>,.?/".indexOf(currentChar) >= 0) {
				System.out.println("Special character found");
				foundSpecialChar = true;
			} else {
				passwordIndexofError = currentCharNdx;
				return "*** Error *** An invalid character has been found!";
			}
			if (currentCharNdx >= 7) {
				System.out.println("At least 8 characters found");
				foundLongEnough = true;
			}
			if (currentCharNdx >= 63) {
				System.out.println("Too many characters found");
				foundTooLong = true;
			}
			
			// Go to the next character if there is one
			currentCharNdx++;
			if (currentCharNdx >= inputLine.length())
				running = false;
			else
				currentChar = input.charAt(currentCharNdx);
			
			System.out.println();
		}
		
		// Construct a String with a list of the requirement elements that were found.
		//updated error messages to be more descriptive
		String errMessage = "";
		if (!foundUpperCase)
			errMessage += "Password must contain at least one upper case letter\n";
		
		if (!foundLowerCase)
			errMessage += "Password must contain at least one lower case letter\n";
		
		if (!foundNumericDigit)
			errMessage += "Password must contain at least one numeric digit\n";
			
		if (!foundSpecialChar)
			errMessage += "Password must contain at least one special character\n";
			
		if (!foundLongEnough)
			errMessage += "Password must be at least 8 characters long\n";
		
		if(foundTooLong)
			errMessage += "Password must be less than 64 characters long\n";
		
		if (errMessage == "")
			return "";
		
		// If it gets here, there something was not found, so return an appropriate message
		passwordIndexofError = currentCharNdx;
		return errMessage + "conditions were not satisfied";
	}
}
 