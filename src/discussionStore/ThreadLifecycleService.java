package discussionStore;

import entityClasses.User;
import java.util.HashMap;
import java.util.Map;

/** 
 * @author Anuj Gandhi
 * @version 1.00 - Initial prototype for Homework 3.
 */
public class ThreadLifecycleService {

    /** Map to simulate database persistence for thread statuses */
    private final Map<String, ThreadStatus> threadStore = new HashMap<>();
    
    /** Singleton instance to ensure a single source of truth for thread state */
    private static ThreadLifecycleService instance;

    /**
     * Private constructor for the Singleton pattern. 
     * Initializes the system with a default open thread.
     */
    private ThreadLifecycleService() {
        threadStore.put("Assignment1", ThreadStatus.OPEN);
    }

    /**
     * Gets the global instance of the ThreadLifecycleService.
     * @return the singleton instance.
     */
    public static ThreadLifecycleService getInstance() {
        if (instance == null) {
            instance = new ThreadLifecycleService();
        }
        return instance;
    }

    /**
     * <p> Method: changeThreadStatus </p>
     * 
     * <p> Description: Changes the status of a thread. This method implements 
     * the  authorization logic for Homework 3. </p>
     * 
     * @param threadId The unique identifier of the thread.
     * @param newStatus The state to transition to.
     * @param user The user object making the request.
     * @return null if successful, an error message string if failed.
     */
    public String changeThreadStatus(String threadId, ThreadStatus newStatus, User user) {
        
        /* 
         * WHY: This is where we handle that CWE-862 security hole I found in Task 2. 
         * Basically, I'm checking if the user is actually an admin before letting 
         * them mess with the thread status—otherwise, anyone could just hop in 
         * and lock the whole class out of the discussion.
         */
        if (user == null || !user.getAdminRole()) {
            return "UNAUTHORIZED: Only administrators can modify thread lifecycle states.";
        }

        /* 
         * WHY: Just making sure the thread ID actually exists in the map first. 
         * If we don't check this, the app could crash with a null pointer error 
         * or just glitch out the database.
         */
        if (!threadStore.containsKey(threadId)) {
            return "ERROR: The requested thread does not exist.";
        }

        // Apply the new status to the mock database
        threadStore.put(threadId, newStatus);
        
        System.out.println("AUDIT LOG: Thread [" + threadId + "] set to [" + newStatus + "] by " + user.getUserName());
        
        return null; // Signals success
    }

    /**
     * Retrieves the current status of a thread.
     * @param threadId thread to check.
     * @return the current ThreadStatus.
     */
    public ThreadStatus getStatus(String threadId) {
        return threadStore.getOrDefault(threadId, ThreadStatus.OPEN);
    }
}
