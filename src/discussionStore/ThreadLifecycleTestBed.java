package discussionStore;

import entityClasses.User;

/**
 * Title: ThreadLifecycleTestBed Class.
 *
 * <p>Description:</p>
 * <p>
 * This class provides a semi-automated test bed for validating thread lifecycle
 * management functionality. It tests state transitions (OPEN, LOCKED, ARCHIVED)
 * and enforces role-based access control using the {@link ThreadLifecycleService}.
 * </p>
 *
 * <p>Test Coverage Includes:</p>
 * <ul>
 *   <li>Valid state transitions performed by administrators</li>
 *   <li>Unauthorized access attempts by non-admin users</li>
 *   <li>Handling of invalid thread identifiers</li>
 *   <li>Handling of no-operation (same-state) transitions</li>
 * </ul>
 *
 * <p>
 * This test bed complements JUnit testing by providing readable console output
 * that demonstrates correct lifecycle behavior and identifies authorization issues.
 * </p>
 */
public class ThreadLifecycleTestBed {
    
    /** Shared instance of the lifecycle service under test. */
    private static ThreadLifecycleService service = ThreadLifecycleService.getInstance();
    
    /**
     * Entry point for executing all thread lifecycle test cases.
     *
     * <p>Description:</p>
     * <p>
     * Executes a sequence of positive and negative test cases to validate thread
     * state transitions and authorization enforcement.
     * </p>
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("--- Starting Thread Lifecycle Test Bed ---");
        
        testPOS01_AdminLocksOpenThread();
        testPOS02_AdminOpensLockedThread();
        testPOS03_AdminArchivesOpenThread();
        testPOS04_AdminReopensArchivedThread();
        testNEG01_NonAdminAttemptsToLockThread();
        testNEG05_NonexistentThreadOperation();
        testST07_NoOpTransition();
        
        System.out.println("--- Testing Completed ---");
    }

    /**
     * Creates a test user with administrative privileges.
     *
     * @return a user configured as an administrator
     */
    private static User createAdminUser() {
        User admin = new User();
        admin.setUserName("adminUser");
        admin.setAdminRole(true);
        return admin;
    }

    /**
     * Creates a test user without administrative privileges.
     *
     * @return a user configured as a non-admin student
     */
    private static User createStudentUser() {
        User student = new User();
        student.setUserName("studentUser");
        student.setAdminRole(false);
        return student;
    }

    /**
     * POS-01: Validates that an admin can lock an OPEN thread.
     *
     * <p>Expected Result:</p>
     * <ul>
     *   <li>Operation succeeds</li>
     *   <li>Thread status becomes LOCKED</li>
     * </ul>
     */
    private static void testPOS01_AdminLocksOpenThread() {
        System.out.println("\nExecuting POS-01: Admin Locks Open Thread");
        User admin = createAdminUser();

        String result = service.changeThreadStatus("Assignment1", ThreadStatus.LOCKED, admin);
        
        if (result == null && service.getStatus("Assignment1") == ThreadStatus.LOCKED) {
            System.out.println("PASS: Admin successfully locked the thread.");
        } else {
            System.out.println("FAIL: Admin could not lock the thread. Output: " + result);
        }
    }

    /**
     * POS-02: Validates that an admin can reopen a LOCKED thread.
     *
     * <p>Expected Result:</p>
     * <ul>
     *   <li>Operation succeeds</li>
     *   <li>Thread status becomes OPEN</li>
     * </ul>
     */
    private static void testPOS02_AdminOpensLockedThread() {
        System.out.println("\nExecuting POS-02: Admin Opens Locked Thread");
        User admin = createAdminUser();

        String result = service.changeThreadStatus("Assignment1", ThreadStatus.OPEN, admin);
        
        if (result == null && service.getStatus("Assignment1") == ThreadStatus.OPEN) {
            System.out.println("PASS: Admin successfully opened the locked thread.");
        } else {
            System.out.println("FAIL: Admin could not open the thread. Output: " + result);
        }
    }

    /**
     * POS-03: Validates that an admin can archive an OPEN thread.
     *
     * <p>Expected Result:</p>
     * <ul>
     *   <li>Operation succeeds</li>
     *   <li>Thread status becomes ARCHIVED</li>
     * </ul>
     */
    private static void testPOS03_AdminArchivesOpenThread() {
        System.out.println("\nExecuting POS-03: Admin Archives Open Thread");
        User admin = createAdminUser();

        String result = service.changeThreadStatus("Assignment1", ThreadStatus.ARCHIVED, admin);
        
        if (result == null && service.getStatus("Assignment1") == ThreadStatus.ARCHIVED) {
            System.out.println("PASS: Admin successfully archived the thread.");
        } else {
            System.out.println("FAIL: Admin could not archive the thread. Output: " + result);
        }
    }

    /**
     * POS-04: Validates that an admin can reopen an ARCHIVED thread.
     *
     * <p>Expected Result:</p>
     * <ul>
     *   <li>Operation succeeds</li>
     *   <li>Thread status becomes OPEN</li>
     * </ul>
     */
    private static void testPOS04_AdminReopensArchivedThread() {
        System.out.println("\nExecuting POS-04: Admin Reopens Archived Thread");
        User admin = createAdminUser();

        String result = service.changeThreadStatus("Assignment1", ThreadStatus.OPEN, admin);
        
        if (result == null && service.getStatus("Assignment1") == ThreadStatus.OPEN) {
            System.out.println("PASS: Admin successfully opened the archived thread.");
        } else {
            System.out.println("FAIL: Admin could not open the archived thread. Output: " + result);
        }
    }

    /**
     * NEG-01: Validates that a non-admin cannot modify thread state.
     *
     * <p>Expected Result:</p>
     * <ul>
     *   <li>Operation fails</li>
     *   <li>Error message indicates unauthorized access</li>
     *   <li>Thread state remains unchanged</li>
     * </ul>
     */
    private static void testNEG01_NonAdminAttemptsToLockThread() {
        System.out.println("\nExecuting NEG-01: Non-Admin Attempts to Lock Thread");
        User student = createStudentUser();

        ThreadStatus initialStatus = service.getStatus("Assignment1");
        String result = service.changeThreadStatus("Assignment1", ThreadStatus.LOCKED, student);
        
        if (result != null && result.contains("UNAUTHORIZED")
                && service.getStatus("Assignment1") == initialStatus) {
            System.out.println("PASS: Student was appropriately blocked from locking the thread.");
        } else {
            System.out.println("FAIL: Student was not blocked appropriately. Output: " + result);
        }
    }

    /**
     * NEG-05: Validates handling of operations on a nonexistent thread.
     *
     * <p>Expected Result:</p>
     * <ul>
     *   <li>Operation fails</li>
     *   <li>Error message indicates invalid thread</li>
     * </ul>
     */
    private static void testNEG05_NonexistentThreadOperation() {
        System.out.println("\nExecuting NEG-05: Nonexistent Thread Operation");
        User admin = createAdminUser();

        String result = service.changeThreadStatus("GhostThread", ThreadStatus.LOCKED, admin);
        
        if (result != null && result.contains("ERROR")) {
            System.out.println("PASS: System blocked change to nonexistent thread.");
        } else {
            System.out.println("FAIL: System did not handle nonexistent thread correctly.");
        }
    }

    /**
     * ST-07: Validates behavior when attempting a no-op state transition.
     *
     * <p>Expected Result:</p>
     * <ul>
     *   <li>Operation succeeds without changes</li>
     *   <li>Thread state remains the same</li>
     * </ul>
     */
    private static void testST07_NoOpTransition() {
        System.out.println("\nExecuting ST-07: No-Op Transition (Same Status)");
        User admin = createAdminUser();

        String result = service.changeThreadStatus("Assignment1", ThreadStatus.OPEN, admin);
        
        if (result == null && service.getStatus("Assignment1") == ThreadStatus.OPEN) {
            System.out.println("PASS: System cleanly handled identical transition.");
        } else {
            System.out.println("FAIL: Issue transitioning to the same state.");
        }
    }
}