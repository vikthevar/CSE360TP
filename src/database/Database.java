package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import entityClasses.Post;
import entityClasses.Reply;
import entityClasses.User;

/*******
 * <p> Title: Database Class. </p>
 * 
 * <p> Description: This is an in-memory database built on H2. Detailed documentation of H2 can
 * be found at https://www.h2database.com/html/main.html. This class leverages H2 and provides
 * numerous special supporting methods for user, post, reply, feedback, and moderation workflows.
 * </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 2.00 2025-04-29 Updated and expanded from the version produced by Pravalika Mukkiri
 *         and Ishwarya Hidkimath Basavaraj
 * @version 2.01 2025-12-17 Minor updates for Spring 2026
 * @version 2.02 2026-04-22 Added TP3 moderation and content flagging support for posts
 */

/*
 * The Database class is responsible for establishing and managing the connection
 * to the database, and performing operations such as user registration, login
 * validation, handling invitation codes, CRUD for posts and replies, private
 * feedback storage, and moderation-related updates for TP3.
 */
public class Database {

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "org.h2.Driver";
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";

	// Database credentials
	static final String USER = "sa";
	static final String PASS = "";

	// Shared variables used within this class
	private Connection connection = null; // Singleton to access the database
	private Statement statement = null;   // The H2 Statement is used to construct queries

	// Easily accessible attributes of the currently logged-in user
	// This is only useful for single-user applications
	private String currentUsername;
	private String currentPassword;
	private String currentFirstName;
	private String currentMiddleName;
	private String currentLastName;
	private String currentPreferredFirstName;
	private String currentEmailAddress;
	private boolean currentAdminRole;
	private boolean currentNewRole1;
	private boolean currentNewRole2;

	/*******
	 * <p> Method: Database </p>
	 * 
	 * <p> Description: The default constructor used to establish this singleton object.</p>
	 */
	public Database() {
	}

	/*******
	 * <p> Method: connectToDatabase </p>
	 * 
	 * <p> Description: Used to establish the in-memory instance of the H2 database from
	 * secondary storage.</p>
	 *
	 * @throws SQLException when the DriverManager is unable to establish a connection
	 */
	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement();

			// Use this only when you intentionally want to reset the local database.
			//statement.execute("DROP ALL OBJECTS");

			createTables(); // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	/*******
	 * <p> Method: createTables </p>
	 * 
	 * <p> Description: Used to create new instances of the database tables used by this class.</p>
	 * 
	 * @throws SQLException if any table creation statement fails
	 */
	private void createTables() throws SQLException {
		String userTable = "CREATE TABLE IF NOT EXISTS userDB ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, "
				+ "password VARCHAR(255), "
				+ "firstName VARCHAR(255), "
				+ "middleName VARCHAR(255), "
				+ "lastName VARCHAR(255), "
				+ "preferredFirstName VARCHAR(255), "
				+ "emailAddress VARCHAR(255), "
				+ "adminRole BOOL DEFAULT FALSE, "
				+ "newRole1 BOOL DEFAULT FALSE, "
				+ "newRole2 BOOL DEFAULT FALSE)";
		statement.execute(userTable);

		String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
				+ "code VARCHAR(10) PRIMARY KEY, "
				+ "emailAddress VARCHAR(255), "
				+ "role VARCHAR(10))";
		statement.execute(invitationCodesTable);

		String postTable = "CREATE TABLE IF NOT EXISTS HW2_POSTS ("
				+ "postId INT AUTO_INCREMENT PRIMARY KEY, "
				+ "title VARCHAR(200) NOT NULL, "
				+ "body VARCHAR(5000) NOT NULL, "
				+ "author VARCHAR(100) NOT NULL, "
				+ "thread VARCHAR(100) NOT NULL, "
				+ "isDeleted BOOL DEFAULT FALSE, "
				+ "isFlagged BOOL DEFAULT FALSE, "
				+ "isHidden BOOL DEFAULT FALSE, "
				+ "isHighlighted BOOL DEFAULT FALSE, "
				+ "flagReason VARCHAR(500), "
				+ "flaggedBy VARCHAR(100)"
				+ ")";
		statement.execute(postTable);

		String replyTable = "CREATE TABLE IF NOT EXISTS HW2_REPLIES ("
				+ "replyId INT AUTO_INCREMENT PRIMARY KEY, "
				+ "postId INT NOT NULL, "
				+ "body VARCHAR(5000) NOT NULL, "
				+ "author VARCHAR(100) NOT NULL, "
				+ "isDeleted BOOL DEFAULT FALSE"
				+ ")";
		statement.execute(replyTable);

		String feedbackTable = "CREATE TABLE IF NOT EXISTS HW2_FEEDBACK ("
				+ "feedbackId INT AUTO_INCREMENT PRIMARY KEY, "
				+ "postId INT NOT NULL, "
				+ "studentUsername VARCHAR(100) NOT NULL, "
				+ "staffUsername VARCHAR(100) NOT NULL, "
				+ "comment VARCHAR(5000) NOT NULL"
				+ ")";
		statement.execute(feedbackTable);
	}

	/*******
	 * <p> Method: isDatabaseEmpty </p>
	 * 
	 * <p> Description: If the user database has no rows, true is returned, else false.</p>
	 * 
	 * @return true if the database is empty, else false
	 */
	public boolean isDatabaseEmpty() {
		String query = "SELECT COUNT(*) AS count FROM userDB";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count") == 0;
			}
		} catch (SQLException e) {
			return false;
		}
		return true;
	}

	/*******
	 * <p> Method: getNumberOfUsers </p>
	 * 
	 * <p> Description: Returns an integer of the number of users currently in the user database.</p>
	 * 
	 * @return the number of user records in the database
	 */
	public int getNumberOfUsers() {
		String query = "SELECT COUNT(*) AS count FROM userDB";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count");
			}
		} catch (SQLException e) {
			return 0;
		}
		return 0;
	}

	/*******
	 * <p> Method: register(User user) </p>
	 * 
	 * <p> Description: Creates a new row in the database using the user parameter.</p>
	 * 
	 * @param user specifies a user object to be added to the database
	 * @throws SQLException when there is an issue creating the SQL command or executing it
	 */
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO userDB (userName, password, firstName, middleName, "
				+ "lastName, preferredFirstName, emailAddress, adminRole, newRole1, newRole2) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			currentUsername = user.getUserName();
			pstmt.setString(1, currentUsername);

			currentPassword = user.getPassword();
			pstmt.setString(2, currentPassword);

			currentFirstName = user.getFirstName();
			pstmt.setString(3, currentFirstName);

			currentMiddleName = user.getMiddleName();
			pstmt.setString(4, currentMiddleName);

			currentLastName = user.getLastName();
			pstmt.setString(5, currentLastName);

			currentPreferredFirstName = user.getPreferredFirstName();
			pstmt.setString(6, currentPreferredFirstName);

			currentEmailAddress = user.getEmailAddress();
			pstmt.setString(7, currentEmailAddress);

			currentAdminRole = user.getAdminRole();
			pstmt.setBoolean(8, currentAdminRole);

			currentNewRole1 = user.getNewRole1();
			pstmt.setBoolean(9, currentNewRole1);

			currentNewRole2 = user.getNewRole2();
			pstmt.setBoolean(10, currentNewRole2);

			pstmt.executeUpdate();
		}
	}

	/*******
	 * <p> Method: getUserList </p>
	 * 
	 * <p> Description: Generate a list of Strings, one for each user in the database,
	 * starting with {@code "<Select User>"} at the start of the list.</p>
	 * 
	 * @return a list of usernames found in the database
	 */
	public List<String> getUserList() {
		List<String> userList = new ArrayList<>();
		userList.add("<Select a User>");
		String query = "SELECT userName FROM userDB";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				userList.add(rs.getString("userName"));
			}
		} catch (SQLException e) {
			return null;
		}
		return userList;
	}

	/*******
	 * <p> Method: loginAdmin </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username, password, and
	 * admin role is the same as a row in the table.</p>
	 * 
	 * @param user specifies the specific user that should be logged in playing the Admin role
	 * @return true if the specified user has been logged in as an Admin else false
	 */
	public boolean loginAdmin(User user) {
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND adminRole = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/*******
	 * <p> Method: loginRole1 </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username, password, and
	 * role1 is the same as a row in the table.</p>
	 * 
	 * @param user specifies the specific user that should be logged in playing Role1
	 * @return true if the specified user has been logged in as Role1 else false
	 */
	public boolean loginRole1(User user) {
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND newRole1 = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/*******
	 * <p> Method: loginRole2 </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username, password, and
	 * role2 is the same as a row in the table.</p>
	 * 
	 * @param user specifies the specific user that should be logged in playing Role2
	 * @return true if the specified user has been logged in as Role2 else false
	 */
	public boolean loginRole2(User user) {
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND newRole2 = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/*******
	 * <p> Method: doesUserExist </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username is in the table.</p>
	 * 
	 * @param userName specifies the specific user that we want to determine if it is in the table
	 * @return true if the specified user is in the table else false
	 */
	public boolean doesUserExist(String userName) {
		String query = "SELECT COUNT(*) FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/*******
	 * <p> Method: getNumberOfRoles </p>
	 * 
	 * <p> Description: Determine the number of roles a specified user plays.</p>
	 * 
	 * @param user specifies the specific user whose roles are counted
	 * @return the number of roles this user plays
	 */
	public int getNumberOfRoles(User user) {
		int numberOfRoles = 0;
		if (user.getAdminRole()) numberOfRoles++;
		if (user.getNewRole1()) numberOfRoles++;
		if (user.getNewRole2()) numberOfRoles++;
		return numberOfRoles;
	}

	/*******
	 * <p> Method: generateInvitationCode </p>
	 * 
	 * <p> Description: Given an email address and a role, this method establishes an invitation
	 * code and adds a record to the InvitationCodes table.</p>
	 * 
	 * @param emailAddress specifies the email address for this new user
	 * @param role specifies the role that this new user will play
	 * @return the generated six-character invitation code
	 */
	public String generateInvitationCode(String emailAddress, String role) {
		String code = UUID.randomUUID().toString().substring(0, 6);
		String query = "INSERT INTO InvitationCodes (code, emailaddress, role) VALUES (?, ?, ?)";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			pstmt.setString(2, emailAddress);
			pstmt.setString(3, role);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return code;
	}

	/*******
	 * <p> Method: getNumberOfInvitations </p>
	 * 
	 * <p> Description: Determine the number of outstanding invitations in the table.</p>
	 * 
	 * @return the number of invitations in the table
	 */
	public int getNumberOfInvitations() {
		String query = "SELECT COUNT(*) AS count FROM InvitationCodes";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/*******
	 * <p> Method: emailaddressHasBeenUsed </p>
	 * 
	 * <p> Description: Determine if an email address has been used to establish an invitation.</p>
	 * 
	 * @param emailAddress is a string that identifies an email address in the table
	 * @return true if the email address is in the table, else false
	 */
	public boolean emailaddressHasBeenUsed(String emailAddress) {
		String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE emailAddress = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, emailAddress);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("count") > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/*******
	 * <p> Method: getRoleGivenAnInvitationCode </p>
	 * 
	 * <p> Description: Get the role associated with an invitation code.</p>
	 * 
	 * @param code is the invitation code
	 * @return the role for the code or an empty string
	 */
	public String getRoleGivenAnInvitationCode(String code) {
		String query = "SELECT * FROM InvitationCodes WHERE code = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getString("role");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

	/*******
	 * <p> Method: getEmailAddressUsingCode </p>
	 * 
	 * <p> Description: Get the email address associated with an invitation code.</p>
	 * 
	 * @param code is the invitation code
	 * @return the email address for the code or an empty string
	 */
	public String getEmailAddressUsingCode(String code) {
		String query = "SELECT emailAddress FROM InvitationCodes WHERE code = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getString("emailAddress");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

	/*******
	 * <p> Method: removeInvitationAfterUse </p>
	 * 
	 * <p> Description: Remove an invitation record once it is used.</p>
	 * 
	 * @param code is the invitation code
	 */
	public void removeInvitationAfterUse(String code) {
		String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE code = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				int counter = rs.getInt(1);
				if (counter > 0) {
					query = "DELETE FROM InvitationCodes WHERE code = ?";
					try (PreparedStatement pstmt2 = connection.prepareStatement(query)) {
						pstmt2.setString(1, code);
						pstmt2.executeUpdate();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*******
	 * <p> Method: getFirstName </p>
	 * 
	 * <p> Description: Get the first name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * @return the first name of a user given that user's username
	 */
	public String getFirstName(String username) {
		String query = "SELECT firstName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getString("firstName");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*******
	 * <p> Method: updateFirstName </p>
	 * 
	 * <p> Description: Update the first name of a user.</p>
	 * 
	 * @param username is the username of the user
	 * @param firstName is the new first name for the user
	 */
	public void updateFirstName(String username, String firstName) {
		String query = "UPDATE userDB SET firstName = ? WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, firstName);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
			currentFirstName = firstName;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*******
	 * <p> Method: getMiddleName </p>
	 * 
	 * <p> Description: Get the middle name of a user.</p>
	 * 
	 * @param username is the username of the user
	 * @return the middle name of the user
	 */
	public String getMiddleName(String username) {
		String query = "SELECT middleName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getString("middleName");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*******
	 * <p> Method: updateMiddleName </p>
	 * 
	 * <p> Description: Update the middle name of a user.</p>
	 * 
	 * @param username is the username of the user
	 * @param middleName is the new middle name for the user
	 */
	public void updateMiddleName(String username, String middleName) {
		String query = "UPDATE userDB SET middleName = ? WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, middleName);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
			currentMiddleName = middleName;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*******
	 * <p> Method: getLastName </p>
	 * 
	 * <p> Description: Get the last name of a user.</p>
	 * 
	 * @param username is the username of the user
	 * @return the last name of the user
	 */
	public String getLastName(String username) {
		String query = "SELECT lastName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getString("lastName");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*******
	 * <p> Method: updateLastName </p>
	 * 
	 * <p> Description: Update the last name of a user.</p>
	 * 
	 * @param username is the username of the user
	 * @param lastName is the new last name for the user
	 */
	public void updateLastName(String username, String lastName) {
		String query = "UPDATE userDB SET lastName = ? WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, lastName);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
			currentLastName = lastName;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*******
	 * <p> Method: getPreferredFirstName </p>
	 * 
	 * <p> Description: Get the preferred first name of a user.</p>
	 * 
	 * @param username is the username of the user
	 * @return the preferred first name of the user
	 */
	public String getPreferredFirstName(String username) {
		String query = "SELECT preferredFirstName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getString("preferredFirstName");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*******
	 * <p> Method: updatePreferredFirstName </p>
	 * 
	 * <p> Description: Update the preferred first name of a user.</p>
	 * 
	 * @param username is the username of the user
	 * @param preferredFirstName is the new preferred first name for the user
	 */
	public void updatePreferredFirstName(String username, String preferredFirstName) {
		String query = "UPDATE userDB SET preferredFirstName = ? WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, preferredFirstName);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
			currentPreferredFirstName = preferredFirstName;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*******
	 * <p> Method: getEmailAddress </p>
	 * 
	 * <p> Description: Get the email address of a user.</p>
	 * 
	 * @param username is the username of the user
	 * @return the email address of the user
	 */
	public String getEmailAddress(String username) {
		String query = "SELECT emailAddress FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getString("emailAddress");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*******
	 * <p> Method: updateEmailAddress </p>
	 * 
	 * <p> Description: Update the email address of a user.</p>
	 * 
	 * @param username is the username of the user
	 * @param emailAddress is the new email address for the user
	 */
	public void updateEmailAddress(String username, String emailAddress) {
		String query = "UPDATE userDB SET emailAddress = ? WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, emailAddress);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
			currentEmailAddress = emailAddress;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*******
	 * <p> Method: getUserAccountDetails </p>
	 * 
	 * <p> Description: Get all the attributes of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * @return true if the get is successful, else false
	 */
	public boolean getUserAccountDetails(String username) {
		String query = "SELECT * FROM userDB WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			rs.next();
			currentUsername = rs.getString(2);
			currentPassword = rs.getString(3);
			currentFirstName = rs.getString(4);
			currentMiddleName = rs.getString(5);
			currentLastName = rs.getString(6);
			currentPreferredFirstName = rs.getString(7);
			currentEmailAddress = rs.getString(8);
			currentAdminRole = rs.getBoolean(9);
			currentNewRole1 = rs.getBoolean(10);
			currentNewRole2 = rs.getBoolean(11);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	/*******
	 * <p> Method: updateUserRole </p>
	 * 
	 * <p> Description: Update a specified role for a specified user.</p>
	 * 
	 * @param username is the username of the user
	 * @param role is string that specifies the role to update
	 * @param value is the string that specifies TRUE or FALSE for the role
	 * @return true if the update was successful, else false
	 */
	public boolean updateUserRole(String username, String role, String value) {
		if (role.compareTo("Admin") == 0) {
			String query = "UPDATE userDB SET adminRole = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				currentAdminRole = value.compareTo("true") == 0;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		if (role.compareTo("Role1") == 0) {
			String query = "UPDATE userDB SET newRole1 = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				currentNewRole1 = value.compareTo("true") == 0;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		if (role.compareTo("Role2") == 0) {
			String query = "UPDATE userDB SET newRole2 = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				currentNewRole2 = value.compareTo("true") == 0;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		return false;
	}

	/*******
	 * <p> Method: updatePassword </p>
	 * 
	 * <p> Description: Update the password of a user.</p>
	 * 
	 * @param username is the username of the user
	 * @param password is the new password for the user
	 */
	public void updatePassword(String username, String password) {
		String query = "UPDATE userDB SET password = ? WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, password);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
			currentPassword = password;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getCurrentUsername() { return currentUsername; }
	public String getCurrentPassword() { return currentPassword; }
	public String getCurrentFirstName() { return currentFirstName; }
	public String getCurrentMiddleName() { return currentMiddleName; }
	public String getCurrentLastName() { return currentLastName; }
	public String getCurrentPreferredFirstName() { return currentPreferredFirstName; }
	public String getCurrentEmailAddress() { return currentEmailAddress; }
	public boolean getCurrentAdminRole() { return currentAdminRole; }
	public boolean getCurrentNewRole1() { return currentNewRole1; }
	public boolean getCurrentNewRole2() { return currentNewRole2; }

	/*******
	 * <p> Method: setOneTimePassword </p>
	 * 
	 * <p> Description: Sets the one-time password for a user.</p>
	 * 
	 * @param username is a username
	 * @param newPassword is the one-time password to be stored
	 * @return true if new password is stored, else false
	 */
	public boolean setOneTimePassword(String username, String newPassword) {
		String query = "UPDATE userDB SET password = ? WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, newPassword);
			pstmt.setString(2, username);
			int rowsUpdated = pstmt.executeUpdate();
			return rowsUpdated > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/*******
	 * <p> Method: getNumberOfAdmins </p>
	 * 
	 * <p> Description: Returns the number of Admins in the program.</p>
	 * 
	 * @return integer of the number of Admins
	 */
	public int getNumberOfAdmins() {
		String query = "SELECT COUNT(*) FROM userDB WHERE adminRole = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/*******
	 * <p> Method: deleteUser </p>
	 * 
	 * <p> Description: Deletes the specified user.</p>
	 * 
	 * @param username the username
	 * @return true if the user is deleted, else false
	 */
	public boolean deleteUser(String username) {
		if (!doesUserExist(username)) return false;

		String checkAdmin = "SELECT adminRole FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(checkAdmin)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				boolean isAdmin = rs.getBoolean("adminRole");
				if (isAdmin && getNumberOfAdmins() <= 1) {
					return false;
				}
			}

			String deleteQuery = "DELETE FROM userDB WHERE userName = ?";
			try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
				deleteStmt.setString(1, username);
				return deleteStmt.executeUpdate() > 0;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/*******
	 * <p> Method: getAllUsersForDisplay </p>
	 * 
	 * <p> Description: Returns a list of formatted user info strings for display.</p>
	 * 
	 * @return a list of user display strings
	 */
	public List<String> getAllUsersForDisplay() {
		List<String> users = new ArrayList<>();
		String query = "SELECT * FROM userDB";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				String username = rs.getString("userName");
				String firstName = rs.getString("firstName");
				String middleName = rs.getString("middleName");
				String lastName = rs.getString("lastName");
				String email = rs.getString("emailAddress");
				boolean admin = rs.getBoolean("adminRole");
				boolean role1 = rs.getBoolean("newRole1");
				boolean role2 = rs.getBoolean("newRole2");

				List<String> roles = new ArrayList<>();
				if (admin) roles.add("Admin");
				if (role1) roles.add("Role1");
				if (role2) roles.add("Role2");

				String userInfo = "Username: " + username + "\n"
						+ "Name: " + firstName
						+ (middleName != null && !middleName.isEmpty() ? " " + middleName : "")
						+ " " + lastName + "\n"
						+ "Email: " + email + "\n"
						+ "Roles: " + (roles.isEmpty() ? "None" : String.join(", ", roles)) + "\n";

				users.add(userInfo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return users;
	}

	/*******
	 * <p> Method: getInvitationEmailList </p>
	 * 
	 * <p> Description: Returns a list of the emails with invitations.</p>
	 * 
	 * @return a list of invitation email addresses
	 */
	public List<String> getInvitationEmailList() {
		List<String> emailList = new ArrayList<>();
		String query = "SELECT emailAddress FROM InvitationCodes";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				emailList.add(rs.getString("emailAddress"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return emailList;
	}

	/**
	 * Creates a new post in HW2_POSTS and initializes all moderation-related
	 * state to default values.
	 *
	 * @param title the title of the post
	 * @param body the body content of the post
	 * @param author the username of the author who created the post
	 * @param thread the thread name for the post; defaults to General when blank
	 * @return the generated postId
	 * @throws SQLException if the insert fails or the generated key is unavailable
	 */
	public int hw2CreatePost(String title, String body, String author, String thread) throws SQLException {
		String safeThread = (thread == null || thread.trim().isEmpty()) ? "General" : thread.trim();

		String sql = "INSERT INTO HW2_POSTS "
				+ "(title, body, author, thread, isDeleted, isFlagged, isHidden, isHighlighted, flagReason, flaggedBy) "
				+ "VALUES (?, ?, ?, ?, FALSE, FALSE, FALSE, FALSE, '', '')";

		try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, title);
			ps.setString(2, body);
			ps.setString(3, author);
			ps.setString(4, safeThread);
			ps.executeUpdate();

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next()) return rs.getInt(1);
			}
		}
		throw new SQLException("Failed to create post (no generated key).");
	}

	/**
	 * Reads all posts, including deleted and moderated posts, for staff-facing or
	 * full system display.
	 *
	 * @return a list of all stored posts
	 * @throws SQLException if the query fails
	 */
	public List<Post> hw2ListPosts() throws SQLException {
		List<Post> out = new ArrayList<>();
		String sql = "SELECT postId, title, body, author, thread, isDeleted, "
				+ "isFlagged, isHidden, isHighlighted, flagReason, flaggedBy "
				+ "FROM HW2_POSTS ORDER BY postId";

		try (PreparedStatement ps = connection.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				Post p = new Post(
						rs.getInt("postId"),
						rs.getString("title"),
						rs.getString("body"),
						rs.getString("author"),
						rs.getString("thread")
				);
				p.setDeleted(rs.getBoolean("isDeleted"));
				p.setFlagged(rs.getBoolean("isFlagged"));
				p.setHidden(rs.getBoolean("isHidden"));
				p.setHighlighted(rs.getBoolean("isHighlighted"));
				p.setFlagReason(rs.getString("flagReason"));
				p.setFlaggedBy(rs.getString("flaggedBy"));
				out.add(p);
			}
		}
		return out;
	}

	/**
	 * Marks a post as flagged and records the staff member and optional reason.
	 *
	 * @param postId the post being flagged
	 * @param reason the reason the post was flagged
	 * @param staffUser the username of the staff member performing the action
	 * @return true if the post was updated, else false
	 * @throws SQLException if the update fails
	 */
	public boolean hw2FlagPost(int postId, String reason, String staffUser) throws SQLException {
		String sql = "UPDATE HW2_POSTS SET isFlagged = TRUE, flagReason = ?, flaggedBy = ? WHERE postId = ?";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, reason == null ? "" : reason.trim());
			ps.setString(2, staffUser == null ? "" : staffUser.trim());
			ps.setInt(3, postId);
			return ps.executeUpdate() > 0;
		}
	}

	/**
	 * Removes the flagged state and clears related moderation metadata.
	 *
	 * @param postId the post being unflagged
	 * @return true if the post was updated, else false
	 * @throws SQLException if the update fails
	 */
	public boolean hw2UnflagPost(int postId) throws SQLException {
		String sql = "UPDATE HW2_POSTS SET isFlagged = FALSE, flagReason = '', flaggedBy = '' WHERE postId = ?";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, postId);
			return ps.executeUpdate() > 0;
		}
	}

	/**
	 * Hides a post from normal student-facing views while preserving it for staff review.
	 *
	 * @param postId the post being hidden
	 * @return true if the post was updated, else false
	 * @throws SQLException if the update fails
	 */
	public boolean hw2HidePost(int postId) throws SQLException {
		String sql = "UPDATE HW2_POSTS SET isHidden = TRUE WHERE postId = ?";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, postId);
			return ps.executeUpdate() > 0;
		}
	}

	/**
	 * Removes the hidden state from a post so it can return to normal visibility.
	 *
	 * @param postId the post being unhidden
	 * @return true if the post was updated, else false
	 * @throws SQLException if the update fails
	 */
	public boolean hw2UnhidePost(int postId) throws SQLException {
		String sql = "UPDATE HW2_POSTS SET isHidden = FALSE WHERE postId = ?";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, postId);
			return ps.executeUpdate() > 0;
		}
	}

	/**
	 * Marks a post as highlighted so staff can recognize a high-quality contribution.
	 *
	 * @param postId the post being highlighted
	 * @return true if the post was updated, else false
	 * @throws SQLException if the update fails
	 */
	public boolean hw2HighlightPost(int postId) throws SQLException {
		String sql = "UPDATE HW2_POSTS SET isHighlighted = TRUE WHERE postId = ?";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, postId);
			return ps.executeUpdate() > 0;
		}
	}

	/**
	 * Removes the highlighted state from a post.
	 *
	 * @param postId the post whose highlight is removed
	 * @return true if the post was updated, else false
	 * @throws SQLException if the update fails
	 */
	public boolean hw2RemoveHighlightPost(int postId) throws SQLException {
		String sql = "UPDATE HW2_POSTS SET isHighlighted = FALSE WHERE postId = ?";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, postId);
			return ps.executeUpdate() > 0;
		}
	}

	/**
	 * Returns all posts currently flagged for moderation review.
	 *
	 * @return a list of flagged posts
	 * @throws SQLException if the query fails
	 */
	public List<Post> hw2GetFlaggedPosts() throws SQLException {
		List<Post> out = new ArrayList<>();
		String sql = "SELECT postId, title, body, author, thread, isDeleted, "
				+ "isFlagged, isHidden, isHighlighted, flagReason, flaggedBy "
				+ "FROM HW2_POSTS WHERE isFlagged = TRUE ORDER BY postId";

		try (PreparedStatement ps = connection.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				Post p = new Post(
						rs.getInt("postId"),
						rs.getString("title"),
						rs.getString("body"),
						rs.getString("author"),
						rs.getString("thread")
				);
				p.setDeleted(rs.getBoolean("isDeleted"));
				p.setFlagged(rs.getBoolean("isFlagged"));
				p.setHidden(rs.getBoolean("isHidden"));
				p.setHighlighted(rs.getBoolean("isHighlighted"));
				p.setFlagReason(rs.getString("flagReason"));
				p.setFlaggedBy(rs.getString("flaggedBy"));
				out.add(p);
			}
		}
		return out;
	}

	/**
	 * Creates a reply and returns the generated replyId.
	 *
	 * @param postId the post being replied to
	 * @param body the body of the reply
	 * @param author the author of the reply
	 * @return the generated replyId
	 * @throws SQLException if the insert fails
	 */
	public int hw2CreateReply(int postId, String body, String author) throws SQLException {
		String sql = "INSERT INTO HW2_REPLIES (postId, body, author, isDeleted) VALUES (?, ?, ?, FALSE)";
		try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setInt(1, postId);
			ps.setString(2, body);
			ps.setString(3, author);
			ps.executeUpdate();

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next()) return rs.getInt(1);
			}
		}
		throw new SQLException("Failed to create reply (no generated key).");
	}

	/**
	 * Reads all replies for a given postId.
	 *
	 * @param postId the post whose replies are requested
	 * @return a list of replies for the specified post
	 * @throws SQLException if the query fails
	 */
	public List<Reply> hw2ListRepliesByPostId(int postId) throws SQLException {
		List<Reply> out = new ArrayList<>();
		String sql = "SELECT replyId, postId, body, author, isDeleted FROM HW2_REPLIES WHERE postId = ? ORDER BY replyId";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, postId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Reply r = new Reply(
							rs.getInt("replyId"),
							rs.getInt("postId"),
							rs.getString("body"),
							rs.getString("author")
					);
					r.setDeleted(rs.getBoolean("isDeleted"));
					out.add(r);
				}
			}
		}
		return out;
	}

	/**
	 * Reads a single reply by id.
	 *
	 * @param replyId the reply identifier
	 * @return the reply if found, else null
	 * @throws SQLException if the query fails
	 */
	public Reply hw2GetReplyById(int replyId) throws SQLException {
		String sql = "SELECT replyId, postId, body, author, isDeleted FROM HW2_REPLIES WHERE replyId = ?";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, replyId);
			try (ResultSet rs = ps.executeQuery()) {
				if (!rs.next()) return null;

				Reply r = new Reply(
						rs.getInt("replyId"),
						rs.getInt("postId"),
						rs.getString("body"),
						rs.getString("author")
				);
				r.setDeleted(rs.getBoolean("isDeleted"));
				return r;
			}
		}
	}

	/**
	 * Updates reply body content.
	 *
	 * @param replyId the reply identifier
	 * @param newBody the replacement reply body
	 * @return true if updated, else false
	 * @throws SQLException if the update fails
	 */
	public boolean hw2UpdateReply(int replyId, String newBody) throws SQLException {
		String sql = "UPDATE HW2_REPLIES SET body = ? WHERE replyId = ?";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, newBody);
			ps.setInt(2, replyId);
			return ps.executeUpdate() > 0;
		}
	}

	/**
	 * Hard deletes a reply from the reply table.
	 *
	 * @param replyId the reply identifier
	 * @return true if deleted, else false
	 * @throws SQLException if the delete fails
	 */
	public boolean hw2DeleteReplyHard(int replyId) throws SQLException {
		String sql = "DELETE FROM HW2_REPLIES WHERE replyId = ?";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, replyId);
			return ps.executeUpdate() > 0;
		}
	}

	/**
	 * Soft deletes a reply by marking isDeleted = TRUE.
	 *
	 * @param replyId the reply identifier
	 * @return true if updated, else false
	 * @throws SQLException if the update fails
	 */
	public boolean hw2DeleteReplySoft(int replyId) throws SQLException {
		String sql = "UPDATE HW2_REPLIES SET isDeleted = TRUE WHERE replyId = ?";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, replyId);
			return ps.executeUpdate() > 0;
		}
	}

	/**
	 * Searches non-deleted replies by keyword and/or postId.
	 *
	 * @param keyword keyword searched in reply body and author
	 * @param postIdFilter optional postId filter
	 * @return a list of matching replies
	 * @throws SQLException if the query fails
	 */
	public List<Reply> hw2SearchReplies(String keyword, Integer postIdFilter) throws SQLException {
		List<Reply> out = new ArrayList<>();
		String kw = (keyword == null) ? "" : keyword.trim().toLowerCase();

		boolean hasKeyword = !kw.isEmpty();
		boolean hasPostId = (postIdFilter != null);

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT replyId, postId, body, author, isDeleted FROM HW2_REPLIES WHERE isDeleted = FALSE ");

		if (hasKeyword) sb.append("AND (LOWER(body) LIKE ? OR LOWER(author) LIKE ?) ");
		if (hasPostId) sb.append("AND postId = ? ");
		sb.append("ORDER BY replyId");

		try (PreparedStatement ps = connection.prepareStatement(sb.toString())) {
			int idx = 1;
			if (hasKeyword) {
				String like = "%" + kw + "%";
				ps.setString(idx++, like);
				ps.setString(idx++, like);
			}
			if (hasPostId) {
				ps.setInt(idx++, postIdFilter);
			}

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Reply r = new Reply(
							rs.getInt("replyId"),
							rs.getInt("postId"),
							rs.getString("body"),
							rs.getString("author")
					);
					r.setDeleted(rs.getBoolean("isDeleted"));
					out.add(r);
				}
			}
		}

		return out;
	}

	/**
	 * Reads a single post by id, including moderation-related state.
	 *
	 * @param postId the post identifier
	 * @return the post if found, else null
	 * @throws SQLException if the query fails
	 */
	public Post hw2GetPostById(int postId) throws SQLException {
		String sql = "SELECT postId, title, body, author, thread, isDeleted, "
				+ "isFlagged, isHidden, isHighlighted, flagReason, flaggedBy "
				+ "FROM HW2_POSTS WHERE postId = ?";

		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, postId);
			try (ResultSet rs = ps.executeQuery()) {
				if (!rs.next()) return null;

				Post p = new Post(
						rs.getInt("postId"),
						rs.getString("title"),
						rs.getString("body"),
						rs.getString("author"),
						rs.getString("thread")
				);
				p.setDeleted(rs.getBoolean("isDeleted"));
				p.setFlagged(rs.getBoolean("isFlagged"));
				p.setHidden(rs.getBoolean("isHidden"));
				p.setHighlighted(rs.getBoolean("isHighlighted"));
				p.setFlagReason(rs.getString("flagReason"));
				p.setFlaggedBy(rs.getString("flaggedBy"));
				return p;
			}
		}
	}

	/**
	 * Updates an existing post's title, body, and thread.
	 *
	 * @param postId the post identifier
	 * @param newTitle the replacement title
	 * @param newBody the replacement body
	 * @param newThread the replacement thread; defaults to General when blank
	 * @return true if updated, else false
	 * @throws SQLException if the update fails
	 */
	public boolean hw2UpdatePost(int postId, String newTitle, String newBody, String newThread) throws SQLException {
		String safeThread = (newThread == null || newThread.trim().isEmpty()) ? "General" : newThread.trim();
		String sql = "UPDATE HW2_POSTS SET title = ?, body = ?, thread = ? WHERE postId = ?";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, newTitle);
			ps.setString(2, newBody);
			ps.setString(3, safeThread);
			ps.setInt(4, postId);
			return ps.executeUpdate() > 0;
		}
	}

	/**
	 * Soft deletes a post by marking isDeleted = TRUE.
	 *
	 * @param postId the post identifier
	 * @return true if updated, else false
	 * @throws SQLException if the update fails
	 */
	public boolean hw2SoftDeletePost(int postId) throws SQLException {
		String sql = "UPDATE HW2_POSTS SET isDeleted = TRUE WHERE postId = ?";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, postId);
			return ps.executeUpdate() > 0;
		}
	}

	/**
	 * Searches posts by keyword and optional thread filter, including moderation state
	 * in the loaded Post objects.
	 *
	 * @param keyword keyword searched in title, body, author, and thread
	 * @param threadFilter optional exact thread filter
	 * @return a list of matching posts
	 * @throws SQLException if the query fails
	 */
	public List<Post> hw2SearchPosts(String keyword, String threadFilter) throws SQLException {
		List<Post> out = new ArrayList<>();
		String kw = (keyword == null) ? "" : keyword.trim().toLowerCase();
		String tf = (threadFilter == null) ? "" : threadFilter.trim().toLowerCase();

		boolean hasKeyword = !kw.isEmpty();
		boolean hasThread = !tf.isEmpty();

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT postId, title, body, author, thread, isDeleted, ")
		   .append("isFlagged, isHidden, isHighlighted, flagReason, flaggedBy ")
		   .append("FROM HW2_POSTS WHERE 1=1 ");

		if (hasKeyword) {
			sql.append("AND (LOWER(title) LIKE ? OR LOWER(body) LIKE ? OR LOWER(author) LIKE ? OR LOWER(thread) LIKE ?) ");
		}

		if (hasThread) {
			sql.append("AND LOWER(thread) = ? ");
		}

		sql.append("ORDER BY postId");

		try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
			int idx = 1;

			if (hasKeyword) {
				String like = "%" + kw + "%";
				ps.setString(idx++, like);
				ps.setString(idx++, like);
				ps.setString(idx++, like);
				ps.setString(idx++, like);
			}

			if (hasThread) {
				ps.setString(idx++, tf);
			}

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Post p = new Post(
							rs.getInt("postId"),
							rs.getString("title"),
							rs.getString("body"),
							rs.getString("author"),
							rs.getString("thread")
					);
					p.setDeleted(rs.getBoolean("isDeleted"));
					p.setFlagged(rs.getBoolean("isFlagged"));
					p.setHidden(rs.getBoolean("isHidden"));
					p.setHighlighted(rs.getBoolean("isHighlighted"));
					p.setFlagReason(rs.getString("flagReason"));
					p.setFlaggedBy(rs.getString("flaggedBy"));
					out.add(p);
				}
			}
		}

		return out;
	}

	/**
	 * Saves private staff feedback for a student post.
	 *
	 * @param postId the post being commented on
	 * @param student the student username receiving feedback
	 * @param staff the staff username creating feedback
	 * @param comment the feedback comment text
	 * @throws SQLException if the insert fails
	 */
	public void hw2SaveFeedback(int postId, String student, String staff, String comment) throws SQLException {
		String sql = "INSERT INTO HW2_FEEDBACK (postId, studentUsername, staffUsername, comment) VALUES (?, ?, ?, ?)";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, postId);
			ps.setString(2, student);
			ps.setString(3, staff);
			ps.setString(4, comment);
			ps.executeUpdate();
		}
	}

	/**
	 * Retrieves feedback entries for a student.
	 *
	 * @param studentUsername the student whose feedback is requested
	 * @return a list of formatted feedback strings
	 * @throws SQLException if the query fails
	 */
	public List<String> hw2GetFeedbackForStudent(String studentUsername) throws SQLException {
		List<String> feedbackList = new ArrayList<>();
		String sql = "SELECT postId, staffUsername, comment FROM HW2_FEEDBACK WHERE studentUsername = ?";

		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, studentUsername);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				String entry = "Post ID: " + rs.getInt("postId")
						+ "\nFrom: " + rs.getString("staffUsername")
						+ "\nFeedback: " + rs.getString("comment") + "\n";
				feedbackList.add(entry);
			}
		}

		return feedbackList;
	}

	/*******
	 * <p> Method: dump </p>
	 * 
	 * <p> Description: Debugging method that dumps the user database to the console.</p>
	 * 
	 * @throws SQLException if there is an issue accessing the database
	 */
	public void dump() throws SQLException {
		String query = "SELECT * FROM userDB";
		ResultSet resultSet = statement.executeQuery(query);
		ResultSetMetaData meta = resultSet.getMetaData();
		while (resultSet.next()) {
			for (int i = 0; i < meta.getColumnCount(); i++) {
				System.out.println(meta.getColumnLabel(i + 1) + ": " + resultSet.getString(i + 1));
			}
			System.out.println();
		}
		resultSet.close();
	}

	/*******
	 * <p> Method: closeConnection </p>
	 * 
	 * <p> Description: Closes the database statement and connection.</p>
	 */
	public void closeConnection() {
		try {
			if (statement != null) statement.close();
		} catch (SQLException se2) {
			se2.printStackTrace();
		}
		try {
			if (connection != null) connection.close();
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}
}