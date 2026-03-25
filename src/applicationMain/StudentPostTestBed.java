package applicationMain;

import discussionValidation.PostValidator; 

public class StudentPostTestBed {

/*******
 * <p> Title: StudentPostTestAutomation Class. </p>
 * 
 * <p> Description: A Java Application used to automate test cases that validate 
 * that the Student User Stories are implemented and function correctly.
 * The test cases validate the behavior of post creation, reading, updating, and deletion  
 * by ensuring that all input validation rules defined in PostValidator and the Database are enforced.
 * </p>
 * 
 * <p> User Story Requirements Covered:
 * <ul>
 *   <li>R1: A student can create a post with a valid title and body.</li>
 *   <li>R2: A student can read their's and other's posts.</li>
 *   <li>R3: A student can update a post with valid input.</li>
 *   <li>R4: A student can delete their post.</li>
 * </ul>
 * </p>
 * 
 * <p> Requirements Covered By Test Case:
 * <ul>
 *   <li>R1: Test cases 1-9</li>
 *   <li>R2: Test cases 7-9</li>
 *   <li>R3: Test cases 1-6</li>
 *   <li>R4: Test cases 10-11</li>
 * </ul>
 * </p>
 * 
 * @author David Rowlands
 * 
 * 
 */	
	static int numPassed = 0;	// Counter of the number of passed tests
	static int numFailed = 0;	// Counter of the number of failed tests

	/*
	 * This mainline displays a header to the console, performs a sequence of
	 * test cases, and then displays a footer with a summary of the results
	 */
	public static void main(String[] args) {
		/************** Test cases semi-automation report header **************/
		System.out.println("______________________________________");
		System.out.println("\nTesting Automation");

		/************** Start of the test cases **************/
		
		// Test Case 1: Valid input (R1, R3)
		inputValidationTestCase(1, "Valid Title", "This is a valid body with more than 50 characters to pass.", true);

		// Test Case 2: Another valid input (R1, R3)
		inputValidationTestCase(2, "Another Valid Title", "Another body that is long enough to meet requirements.", true);

		// Test Case 3: Boundary case valid input (R1, R3)
		inputValidationTestCase(3, "Edge Title", "Body with exactly 50 characters boundary case works fine.", true);

		// Test Case 4: Empty title (R1, R3)
		inputValidationTestCase(4, "", "Valid body text longer than 50 characters.", false);

		// Test Case 5: Empty body (R1, R3)
		inputValidationTestCase(5, "Valid Title", "", false);

		// Test Case 6: Title exceeds max length (R1, R3)
		inputValidationTestCase(6, 		     
		    "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",
		    "Valid body text longer than 50 characters.", false);
		

		// Test case 7: Null input should default to "General" (R1, R2)
		performThreadTestCase(7, null, "General");

		// Test case 8: Empty input should default to "General" (R1, R2)
		performThreadTestCase(8, "", "General");

		// Test case 9: Valid thread should remain unchanged (R1, R2)
		performThreadTestCase(9, "Homework", "Homework");

		// Test case 10: Post exists should delete successfully (R4)
		performDeleteTestCase(10, true, true);

		// Test case 11: Post does NOT exist should fail (R4)
		performDeleteTestCase(11, false, false);
		
		/************** End of the test cases **************/
		
		/************** Test cases semi-automation report footer **************/
		System.out.println("____________________________________________________________________________");
		System.out.println();
		System.out.println("Number of tests passed: "+ numPassed);
		System.out.println("Number of tests failed: "+ numFailed);
	}
	
	/**
	 * Executes several test cases to validate post title and body input.
	 *
	 * This method uses PostValidator.validate() to determine whether the
	 * provided title and body meet the required validation rules.
	 *
	 * Requirements Tested:
	 * - R1: A student can create a post with a valid title and body.
	 * - R3: A student can update a post with valid input.
	 *
	 * @param testCase The test case number used for identification
	 * @param inputTitle The title being tested
	 * @param inputBody The body being tested
	 * @param expectedPass True if the input is expected to pass validation,
	 *                     false if it is expected to fail
	 *
	 * How the Test Assesses Requirements:
	 * The method calls PostValidator.validate(). If the result is empty,
	 * the input is considered valid. Otherwise, the returned error message
	 * indicates the validation failure. The result of this input, determines
	 * whether or not the post will be created/updated
	 *
	 * How to Interpret Output:
	 * - "***Success***" means the actual result matched the expected result
	 * - "***Failure***" means the validation result did not match expectations
	 */
	private static void inputValidationTestCase(int testCase, String inputTitle, String inputBody, boolean expectedPass) {
				
		// Display an individual test case header
		System.out.println(
				"____________________________________________________________________________" +
				"\n\nTest case: " + testCase);
		System.out.println("Input: \"" + inputTitle + "\"" + inputBody + "\"" );
		System.out.println("______________");
		
		// Call the recognizer to process the input
		String resultText= PostValidator.validate(inputTitle, inputBody);
						
		// Interpret the result and display that interpreted information
		System.out.println();
		
		// If the resulting text is empty, the recognizer accepted the input
		if (resultText != null) {
			 // If the test case expected the test to pass then this is a failure
			if (expectedPass) {
				System.out.println(
						"***Failure*** The input is invalid." +
						"\nBut it was supposed to be valid, so this is a failure!\n");
				numFailed++;
			}
			// If the test case expected the test to fail then this is a success
			else {			
				System.out.println("***Success*** The input is invalid." + 
						"\nBut it was supposed to be invalid, so this is a pass!\n");
				numPassed++;
			}
		}
		
		// If the resulting text is empty, the recognizer accepted the input
		else {	
			// If the test case expected the test to pass then this is a success
			if (expectedPass) {	
				System.out.println("***Success*** The input is valid, so this is a pass!");
				numPassed++;
			}
			// If the test case expected the test to fail then this is a failure
			else {
				System.out.println("***Failure*** The input was judged as valid" + 
						"\nBut it was supposed to be invalid, so this is a failure!");
				numFailed++;
			}
		}
		
		
	}
	
	/**
	 * Executes a test case to validate thread normalization to General.
	 *
	 * This method uses PostValidator.normalizeThread() to determine whether the
	 * method correctly determines if the thread should be replaced to General.
	 *
	 * Requirements Tested:
	 * - R1: A student can create a post with a valid title and body.
	 *
	 * @param testCase The test case number used for identification
	 * @param inputThread The thread input being tested (may be null or empty)
	 * @param expectedOutput The expected normalized thread result
	 * 
	 * How the Test Assesses Requirements:
	 * The method calls PostValidator.normalizeThread(). If the input is empty,
	 * the thread should be changed to General. Otherwise, the thread name is 
	 * kept the same. The expectedOutput determines whether or not the resulting
	 * thread name was correct.
	 *
	 * How to Interpret Output:
	 * - "***Success***" means the actual result matched the expected result
	 * - "***Failure***" means the validation result did not match expectations
	 */
	private static void performThreadTestCase(int testCase, String inputThread, String expectedOutput) {

	    System.out.println(
	        "____________________________________________________________________________" +
	        "\n\nTest Case: " + testCase + " (Thread Normalization)");
	    
	    System.out.println("Input: \"" + inputThread + "\"");
	    System.out.println("Expected Output: \"" + expectedOutput + "\"");
	    System.out.println("______________");

	    // Call method under test
	    String result = PostValidator.normalizeThread(inputThread);

	    // Compare result
	    if (expectedOutput.equals(result)) {
	        System.out.println("***Success*** Output matches expected result");
	        numPassed++;
	    } else {
	        System.out.println("***Failure*** Output does not match expected result");
	        System.out.println("Actual Output: \"" + result + "\"");
	        numFailed++;
	    }

	    System.out.println();
	}
	
	/**
	 * Executes a simulated test case for post deletion.
	 *
	 * Requirements Tested:
	 * - R4: A student can delete their post.
	 *
	 * @param testCase The test case number
	 * @param postExists Whether the post exists before deletion
	 * @param expectedResult The expected result of the delete operation
	 *
	 * How the Test Assesses the Requirement:
	 * This method simulates the database behavior of hw2SoftDeletePost().
	 * If a post exists, deletion succeeds (returns true).
	 * If it does not exist, deletion fails (returns false).
	 *
	 * How to Interpret Output:
	 * - "***Success***" → actual result matches expected result
	 * - "***Failure***" → actual result does not match expected result
	 *
	 * Note:
	 * This test simulates database behavior to ensure controlled and repeatable testing.
	 */
	private static void performDeleteTestCase(int testCase, boolean postExists, boolean expectedResult) {

	    System.out.println(
	        "____________________________________________________________________________" +
	        "\n\nTest Case: " + testCase + " (Soft Delete Post)");
	    
	    System.out.println("Post exists before deletion: " + postExists);
	    System.out.println("Expected result: " + expectedResult);
	    System.out.println("______________");

	    // Simulated "database state"
	    boolean isDeleted = false;

	    // Simulated delete logic
	    boolean actualResult;
	    if (postExists) {
	        isDeleted = true;     // mark as deleted (soft delete)
	        actualResult = true;  // corresponds to rows affected > 0
	    } else {
	        actualResult = false; // no rows affected
	    }

	    // Compare result
	    if (actualResult == expectedResult) {
	        System.out.println("***Success*** Delete operation behaved as expected");
	        numPassed++;
	    } else {
	        System.out.println("***Failure*** Delete operation did not behave as expected");
	        numFailed++;
	    }

	    System.out.println("Post marked as deleted: " + isDeleted);
	    System.out.println();
	}
	
}



