package discussionStore;

/**
 * Title: ThreadStatus Enumeration.
 *
 * <p>Description:</p>
 * <p>
 * Defines the possible operational states of a discussion thread within the system.
 * These states control whether users are allowed to create or interact with posts
 * associated with a given thread.
 * </p>
 *
 * <p>States:</p>
 * <ul>
 *   <li><b>OPEN</b> – The thread is active. Users are allowed to create posts and replies.</li>
 *   <li><b>LOCKED</b> – The thread is temporarily restricted. No new posts or replies are allowed,
 *       but existing content remains visible.</li>
 *   <li><b>ARCHIVED</b> – The thread is permanently closed. No modifications are allowed and the
 *       thread is considered read-only for historical purposes.</li>
 * </ul>
 *
 * <p>
 * This enumeration is used by {@link ThreadLifecycleService} to enforce lifecycle
 * rules and validate state transitions. It ensures consistent handling of thread
 * accessibility across the application.
 * </p>
 *
 * <p>Validated by:</p>
 * <ul>
 *   <li>ThreadLifecycleTestBed.testPOS01_AdminLocksOpenThread</li>
 *   <li>ThreadLifecycleTestBed.testPOS02_AdminOpensLockedThread</li>
 *   <li>ThreadLifecycleTestBed.testPOS03_AdminArchivesOpenThread</li>
 *   <li>ThreadLifecycleTestBed.testPOS04_AdminReopensArchivedThread</li>
 *   <li>ThreadLifecycleTestBed.testNEG01_NonAdminAttemptsToLockThread</li>
 *   <li>ThreadLifecycleTestBed.testNEG05_NonexistentThreadOperation</li>
 *   <li>ThreadLifecycleTestBed.testST07_NoOpTransition</li>
 * </ul>
 */
public enum ThreadStatus {

    /** Thread is active and accepts new posts and replies. */
    OPEN,

    /** Thread is restricted and does not allow new posts or replies. */
    LOCKED,

    /** Thread is permanently closed and read-only. */
    ARCHIVED
}