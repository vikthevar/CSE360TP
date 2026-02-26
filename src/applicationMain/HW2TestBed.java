package applicationMain;

import discussionStore.PostStore;
import discussionStore.ReplyStore;
import entityClasses.Post;
import entityClasses.Reply;
import guiDiscussion.ControllerDiscussion;

import java.util.List;

public class HW2TestBed {

    private static final PostStore postStore = new PostStore();
    private static final ReplyStore replyStore = new ReplyStore();

    public static void main(String[] args) {

        System.out.println("===== HW2 TESTBED START =====\n");

        tc1_createPostValid();
        tc2_createPostMissingTitle();
        tc3_updatePostInvalidId();
        tc4_deletePostRepliesRemain();
        tc5_createReplyInvalidPost();
        tc6_deleteReplyInvalidId();
        tc7_searchPostsSubset();

        System.out.println("\n===== HW2 TESTBED END =====");
    }

    // ---------------------------------------------------------
    // TC1 – Create Post (Valid + Default Thread)
    // ---------------------------------------------------------
    private static void tc1_createPostValid() {
        System.out.println("TC1 – Create Post (Valid + Default Thread)");

        String err = postStore.createPost(
                "HW2 CRUD clarification",
                "Are students required to implement UPDATE for posts?",
                "Vikram",
                ""   // no thread provided
        );

        if (err == null) {
            Post p = postStore.getAllPosts().get(0);
            if ("General".equals(p.getThread())) {
                System.out.println("PASS");
            } else {
                System.out.println("FAIL – Default thread not set");
            }
        } else {
            System.out.println("FAIL – Unexpected error: " + err);
        }

        System.out.println();
    }

    // ---------------------------------------------------------
    // TC2 – Create Post (Invalid: Missing Title)
    // ---------------------------------------------------------
    private static void tc2_createPostMissingTitle() {
        System.out.println("TC2 – Create Post (Missing Title)");

        String err = postStore.createPost(
                "",
                "Valid body text.",
                "Vikram",
                "General"
        );

        if (err != null && err.contains("Title")) {
            System.out.println("PASS");
        } else {
            System.out.println("FAIL");
        }

        System.out.println();
    }

    // ---------------------------------------------------------
    // TC3 – Update Post (Invalid: Post Not Found)
    // ---------------------------------------------------------
    private static void tc3_updatePostInvalidId() {
        System.out.println("TC3 – Update Post (Invalid ID)");

        String err = postStore.updatePost(999, "New Title", "Updated content.");

        if (err != null && err.contains("not found")) {
            System.out.println("PASS");
        } else {
            System.out.println("FAIL");
        }

        System.out.println();
    }

    // ---------------------------------------------------------
    // TC4 – Delete Post and Replies Remain
    // ---------------------------------------------------------
    private static void tc4_deletePostRepliesRemain() {
        System.out.println("TC4 – Delete Post (Replies Remain)");

        postStore.createPost("Delete Me", "Body", "Vikram", "General");
        Post p = postStore.getAllPosts().get(postStore.getAllPosts().size() - 1);

        replyStore.createReply(p.getPostId(), "Reply text", "Vikram");

        postStore.deletePost(p.getPostId());

        List<Reply> replies = replyStore.getRepliesByPostId(p.getPostId());

        if (!replies.isEmpty()) {
            System.out.println("PASS");
        } else {
            System.out.println("FAIL – Replies removed incorrectly");
        }

        System.out.println();
    }

    // ---------------------------------------------------------
    // TC5 – Create Reply (Invalid Post)
    // ---------------------------------------------------------
    private static void tc5_createReplyInvalidPost() {
        System.out.println("TC5 – Create Reply (Invalid Post)");

        String err = ControllerDiscussion.testCreateReply(999, "This is a reply.", "Vikram");

        if (err != null && err.contains("Post")) {
            System.out.println("PASS");
        } else {
            System.out.println("FAIL");
        }

        System.out.println();
    }

    // ---------------------------------------------------------
    // TC6 – Delete Reply (Invalid ID)
    // ---------------------------------------------------------
    private static void tc6_deleteReplyInvalidId() {
        System.out.println("TC6 – Delete Reply (Invalid ID)");

        String err = replyStore.deleteReply(999);

        if (err != null && err.contains("not found")) {
            System.out.println("PASS");
        } else {
            System.out.println("FAIL");
        }

        System.out.println();
    }

    // ---------------------------------------------------------
    // TC7 – Search Posts (Subset)
    // ---------------------------------------------------------
    private static void tc7_searchPostsSubset() {
        System.out.println("TC7 – Search Posts (Subset Behavior)");

        postStore.createPost("CRUD Example", "Discuss CRUD", "Vikram", "General");
        postStore.createPost("Another Post", "No keyword", "Vikram", "General");

        postStore.searchPosts("CRUD");
        if (!postStore.getSubsetPosts().isEmpty()) {
            System.out.println("PASS – Match case");
        } else {
            System.out.println("FAIL – Expected match not found");
        }

        postStore.searchPosts("nonexistentkeyword");
        if (postStore.getSubsetPosts().isEmpty()) {
            System.out.println("PASS – No match case");
        } else {
            System.out.println("FAIL – Expected empty subset");
        }

        System.out.println();
    }
}