package discussionStore;

import entityClasses.User;

public class ThreadLifecycleTestBed {
    
    private static ThreadLifecycleService service = ThreadLifecycleService.getInstance();
    
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

    private static User createAdminUser() {
        User admin = new User();
        admin.setUserName("adminUser");
        admin.setAdminRole(true);
        return admin;
    }

    private static User createStudentUser() {
        User student = new User();
        student.setUserName("studentUser");
        student.setAdminRole(false);
        return student;
    }

    private static void testPOS01_AdminLocksOpenThread() {
        System.out.println("\nExecuting POS-01: Admin Locks Open Thread");
        User admin = createAdminUser();
        // The service initializes "Assignment1" as OPEN by default
        String result = service.changeThreadStatus("Assignment1", ThreadStatus.LOCKED, admin);
        
        if (result == null && service.getStatus("Assignment1") == ThreadStatus.LOCKED) {
            System.out.println("PASS: Admin successfully locked the thread.");
        } else {
            System.out.println("FAIL: Admin could not lock the thread. Output: " + result);
        }
    }

    private static void testPOS02_AdminOpensLockedThread() {
        System.out.println("\nExecuting POS-02: Admin Opens Locked Thread");
        User admin = createAdminUser();
        
        // Relies on POS-01 having locked the thread, or just forces it
        String result = service.changeThreadStatus("Assignment1", ThreadStatus.OPEN, admin);
        
        if (result == null && service.getStatus("Assignment1") == ThreadStatus.OPEN) {
            System.out.println("PASS: Admin successfully opened the locked thread.");
        } else {
            System.out.println("FAIL: Admin could not open the thread. Output: " + result);
        }
    }

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

    private static void testNEG01_NonAdminAttemptsToLockThread() {
        System.out.println("\nExecuting NEG-01: Non-Admin Attempts to Lock Thread");
        User student = createStudentUser();
        
        ThreadStatus initialStatus = service.getStatus("Assignment1");
        String result = service.changeThreadStatus("Assignment1", ThreadStatus.LOCKED, student);
        
        if (result != null && result.contains("UNAUTHORIZED") && service.getStatus("Assignment1") == initialStatus) {
            System.out.println("PASS: Student was appropriately blocked from locking the thread.");
        } else {
            System.out.println("FAIL: Student was not blocked appropriately. Output: " + result);
        }
    }

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

    private static void testST07_NoOpTransition() {
        System.out.println("\nExecuting ST-07: No-Op Transition (Same Status)");
        User admin = createAdminUser();
        
        // Assuming thread is OPEN
        String result = service.changeThreadStatus("Assignment1", ThreadStatus.OPEN, admin);
        
        if (result == null && service.getStatus("Assignment1") == ThreadStatus.OPEN) {
            System.out.println("PASS: System cleanly handled identical transition.");
        } else {
            System.out.println("FAIL: Issue transitioning to the same state.");
        }
    }
}
