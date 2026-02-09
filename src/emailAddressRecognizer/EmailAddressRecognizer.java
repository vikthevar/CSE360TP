package emailAddressRecognizer;


public class EmailAddressRecognizer {
	/**
	 * <p> Title: FSM-translated EmailAddressRecognizer. </p>
	 * 
	 * <p> Description: A demonstration of the mechanical translation of Finite State Machine 
	 * diagram into an executable Java program using the Email Address Recognizer. The code 
	 * detailed design is based on a while loop with a select list</p>
	 * 
	 * <p> Copyright: Lynn Robert Carter © 2022 </p>
	 * 
	 * @author Lynn Robert Carter
	 * 
	 * @version 0.00		2018-02-04	Initial baseline 
	 * @version 2.00		2022-01-06	Rewritten to recognize email addresses and enhanced
	 * 										to support FSM with up through 999 states for the 
	 * 										trace output to align nicely
	 * @version 3.00		2022-03-22	Adjusted to clean up the code and resolving alignment
	 * 										issues with the design and to correct the issue
	 * 										with an empty email address
	 * 
	 */

	/**********************************************************************************************
	 * 
	 * Result attributes to be used for GUI applications where a detailed error message and a 
	 * pointer to the character of the error will enhance the user experience.
	 * 
	 */

	public static String emailAddressErrorMessage = "";	// The error message text
	public static String emailAddressInput = "";		// The input being processed
	public static int emailAddressIndexofError = -1;	// The index where the error was located
	private static int state = 0;						// The current state value
	private static int nextState = 0;					// The next state value
	private static boolean finalState = false;			// Is this state a final state?
	private static String inputLine = "";				// The input line
	private static char currentChar;					// The current character in the line
	private static int currentCharNdx;					// The index of the current character
	private static boolean running;						// The flag that specifies if the FSM is 
														// running
	private static int domainPartCounter = 0;			// A domain name may not exceed 63 characters

	/**********
	 * This private method display the input line and then on a line under it displays an up arrow
	 * at the point where an error should one be detected.  This method is designed to be used to 
	 * display the error message on the console terminal.
	 * 
	 * @param input				The input string
	 * @param currentCharNdx	The location where an error was found
	 * @return					Two lines, the entire input line followed by a line with an up arrow
	 */
	private static String displayInput(String input, int currentCharNdx) {
		// Display the entire input line
		String result = input.substring(0,currentCharNdx) + "?\n";

		return result;
	}


	private static void displayDebuggingInfo() {
		// Display the current state of the FSM as part of an execution trace
		if (currentCharNdx >= inputLine.length())
			// display the line with the current state numbers aligned
			System.out.println(((state > 99) ? " " : (state > 9) ? "  " : "   ") + state + 
					((finalState) ? "       F   " : "           ") + "None");
		else
			System.out.println(((state > 99) ? " " : (state > 9) ? "  " : "   ") + state + 
					((finalState) ? "       F   " : "           ") + "  " + currentChar + " " + 
					((nextState > 99) ? "" : (nextState > 9) || (nextState == -1) ? "   " : "    ") + 
					nextState + "     " + domainPartCounter);
	}

	private static void moveToNextCharacter() {
		currentCharNdx++;
		if (currentCharNdx < inputLine.length())
			currentChar = inputLine.charAt(currentCharNdx);
		else {
			System.out.println("End of input was found!");
			currentChar = ' ';
			running = false;
		}
	}

	/**********
	 * This method is a mechanical transformation of a Finite State Machine diagram into a Java
	 * method.
	 * 
	 * @param input		The input string for the Finite State Machine
	 * @return			An output string that is empty if every things is okay or it will be
	 * 						a string with a help description of the error follow by two lines
	 * 						that shows the input line follow by a line with an up arrow at the
	 *						point where the error was found.
	 */
	public static String checkEmailAddress(String input) {
		// The following are the local variable used to perform the Finite State Machine simulation
		state = 0;							// This is the FSM state number
		inputLine = input;					// Save the reference to the input line as a global
		currentCharNdx = 0;					// The index of the current character

		// The Finite State Machines continues until the end of the input is reached or at some 
		// state the current character does not match any valid transition to a next state

		emailAddressInput = input;			// Save a copy of the input

		// Let's ensure there is input
		if (input.length() <= 0) {
			emailAddressErrorMessage = "There was no email address found.\n";
			return emailAddressErrorMessage + displayInput(input, 0);
		}
		currentChar = input.charAt(0);		// The current character from the above indexed position

		// Let's ensure the address is not too long
		if (input.length() > 255) {
			emailAddressErrorMessage = "A valid email address must be no more than 255 characters.\n";
			return emailAddressErrorMessage + displayInput(input, 255);
		}
		running = true;						// Start the loop
		System.out.println("\nCurrent Final Input  Next  DomainName\nState   State Char  State  Size");

		// The Finite State Machines continues until the end of the input is reached or at some 
		// state the current character does not match any valid transition to a next state
		while (running) {
			// The switch statement takes the execution to the code for the current state, where
			// that code sees whether or not the current character is valid to transition to a
			// next state
			nextState = -1;						// Default to there is no next state		
			
			switch (state) {
			case 0: 
				// State 0 has just 1 valid transition.
				// The current character is must be checked against 62 options. If any are matched
				// the FSM must go to state 1
				// The first and the second check for an alphabet character the third a numeric
				if ((currentChar >= 'A' && currentChar <= 'Z')|| 		// Upper case
						(currentChar >= 'a' && currentChar <= 'z') ||	// Lower case
						(currentChar >= '0' && currentChar <= '9')) {	// Digit
					nextState = 1;
				}
								
				// If it is none of those characters, the FSM halts
				else { 
					running = false;
				}
				
				break;				
				// The execution of this state is finished
			
			case 1: 
				// State 1 has three valid transitions.  

				if ((currentChar >= 'A' && currentChar <= 'Z') ||		// Upper Case
						(currentChar >= 'a' && currentChar <= 'z') ||	// Lower Case	
						(currentChar >= '0' && currentChar <= '9')) {	// Digit
					nextState = 1;										// LPChar loop continues
				}
				
				else if (currentChar == '.') {							// '.' takes back to state 0
					nextState = 0;
				}
				
				else if (currentChar == '@') {							// '@' takes to to state 2
					nextState = 2;
					domainPartCounter = 0;								// Begin domain counting
				}
				
				else {
					running = false;
				}
				
				break;
				// The execution of this state is finished
							
			case 2: 
				// State 2 has one valid transition.
				
				if ((currentChar >= 'A' && currentChar <= 'Z') ||		// Upper Case
						(currentChar >= 'a' && currentChar <= 'z') ||	// Lower Case	
						(currentChar >= '0' && currentChar <= '9')) {	// Digit
					nextState = 3;										// DPChar takes to state 3
					domainPartCounter = 1;								// First domain character
				}
				
				else {
					running = false;
				}

				// The execution of this state is finished
				break;
	
			case 3:
				// State 3 has three valid transition.
				
				if ((currentChar >= 'A' && currentChar <= 'Z') ||		// Upper Case
						(currentChar >= 'a' && currentChar <= 'z') ||	// Lower Case	
						(currentChar >= '0' && currentChar <= '9')) {	// Digit
					domainPartCounter += 1;								// Increment domain character count
					nextState = 3;										// DPChar returns back to state 3
				}
				
				else if (currentChar == '.') {							// '.' transitions FSM back to state 2
					nextState = 2;
					domainPartCounter = 0;								// New domain part initialized
				}
				
				else if (currentChar == '-') {							// Hyphen transitions to state 
					domainPartCounter += 1;								// Increment domain character count
					nextState = 4;										
				}
				
				else {
					running = false;
				}

				// The execution of this state is finished
				break;

			case 4: 
				// State 4 has one valid transition.

				if ((currentChar >= 'A' && currentChar <= 'Z') ||		// Upper Case
						(currentChar >= 'a' && currentChar <= 'z') ||	// Lower Case	
						(currentChar >= '0' && currentChar <= '9')) {	// Digit
					domainPartCounter += 1;								// Increment domain character count(State 4 is not final)
					nextState = 3;										// DPChar returns back to state 3
				}
				
				else {
					running = false;
				}

				// The execution of this state is finished
				break;

			}
			
			if (running) {
				displayDebuggingInfo();
				// When the processing of a state has finished, the FSM proceeds to the next character
				// in the input and if there is one, it fetches that character and updates the 
				// currentChar.  If there is no next character the currentChar is set to a blank.
				
				moveToNextCharacter();
				
				// Move to the next state
				state = nextState;
				nextState = -1;
			}
			// Should the FSM get here, the loop starts again

		}
		displayDebuggingInfo();
		
		System.out.println("The loop has ended.");

		emailAddressIndexofError = currentCharNdx;		// Copy the index of the current character;
		
		// When the FSM halts, we must determine if the situation is an error or not.  That depends
		// of the current state of the FSM and whether or not the whole string has been consumed.
		// This switch directs the execution to separate code for each of the FSM states and that
		// makes it possible for this code to display a very specific error message to improve the
		// user experience.
		switch (state) {
		case 0:
			// State 0 is not a final state, so we can return a very specific error message
			emailAddressIndexofError = currentCharNdx;		// Copy the index of the current character;
			emailAddressErrorMessage = "May only be alphanumberic.\n";
			return emailAddressErrorMessage;

		case 1:
			// State 1 is not a final state, so we can return a very specific error message
			// If not all of the string has been consumed, we point to the current character
			// in the input line and specify what that character must be in order to move
			// forward.
			if (currentCharNdx < input.length()) {
				//
				emailAddressIndexofError = currentCharNdx;	// Copy the index of the current character
				emailAddressErrorMessage = "Local part may only contain letters, digits, '.', or '@'.\n";
				return emailAddressErrorMessage + displayInput(input, currentCharNdx);
			}
			
			else {
				// If all of the string has been consumed, the input ended before reaching '@' or 
				// the domain part
				emailAddressIndexofError = currentCharNdx;
		        emailAddressErrorMessage = "Email address is incomplete. Missing '@' and domain part.\n";
		        return emailAddressErrorMessage + displayInput(input, currentCharNdx);
			}

		case 2:
			// State 2 is not a final state, so we can return a very specific error message
		    if (currentCharNdx < input.length()) {
		        // If not all of the string has been consumed, we point to the current character
		        // in the input line and specify what that character must be in order to move
		        // forward.
		        emailAddressIndexofError = currentCharNdx;		// Copy the index of the current character;
		        emailAddressErrorMessage = "Domain part must begin with a letter or digit.\n";
		        return emailAddressErrorMessage + displayInput(input, currentCharNdx);
		    }
		    else {
		        // If all of the string has been consumed, the input ended immediately after '@'
		        // or after '.' in the domain, so the domain part is incomplete.
		        emailAddressIndexofError = currentCharNdx;
		        emailAddressErrorMessage = "Domain part is incomplete. A letter or digit is required.\n";
		        return emailAddressErrorMessage + displayInput(input, currentCharNdx);
		    }

		case 3:
			// State 3 is a Final State, so this is not an error if the input is empty, otherwise
			// we can return a very specific error message.

			if (currentCharNdx<input.length()) {
				// If not all of the string has been consumed, we point to the current character
				// in the input line and specify what that character must be in order to move
				// forward.
				emailAddressIndexofError = currentCharNdx;		// Copy the index of the current character;
				emailAddressErrorMessage = "This must be the end of the input.\n";
				return emailAddressErrorMessage + displayInput(input, currentCharNdx);
			}
			else 
			{
				emailAddressIndexofError = -1;
				emailAddressErrorMessage = "";
				return emailAddressErrorMessage;
			}

		case 4:
			// State 4 is not a final state, so we can return a very specific error message. 
			if (currentCharNdx < input.length()) {
		        // If not all of the string has been consumed, we point to the current character
		        // in the input line and specify what that character must be in order to move
		        // forward.
		        emailAddressIndexofError = currentCharNdx;		// Copy the index of the current character;
		        emailAddressErrorMessage = "A '-' in the domain must be followed by a letter or digit.\n";
		        return emailAddressErrorMessage + displayInput(input, currentCharNdx);
		    }
		    else {
		        // If all of the string has been consumed, the domain part ended with a hyphen.
		        emailAddressIndexofError = currentCharNdx;
		        emailAddressErrorMessage = "Domain part may not end with a '-'.\n";
		        return emailAddressErrorMessage + displayInput(input, currentCharNdx);
		    }

		default:
			return "";
		}
	}
}
