package guiDiscussion;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/*******
 * <p> Title: ModelDiscussion Class. </p>
 *
 * <p> Description: Model for the standalone discussion CRUD demonstration. This
 * class contains the core data structures and logic for posts and replies and
 * provides methods that the controller calls to perform Create, Read, Update,
 * Delete, and search operations.</p>
 *
 * <p> All data is stored in memory; there is no database or persistent
 * storage.</p>
 */
public class ModelDiscussion {

	/*******
	 * <p> Title: Post Class </p>
	 *
	 * <p> Description: Represents a discussion post similar to an Ed Discussion
	 * thread.</p>
	 */
	public static class Post {
		private int postId;
		private String title;
		private String body;
		private String authorName;
		private String tags;
		private LocalDateTime createdAt;
		private boolean resolved;

		public Post(int postId, String title, String body, String authorName, String tags,
				LocalDateTime createdAt, boolean resolved) {
			this.postId = postId;
			this.title = title;
			this.body = body;
			this.authorName = authorName;
			this.tags = tags;
			this.createdAt = createdAt;
			this.resolved = resolved;
		}

		public int getPostId() {
			return postId;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getBody() {
			return body;
		}

		public void setBody(String body) {
			this.body = body;
		}

		public String getAuthorName() {
			return authorName;
		}

		public void setAuthorName(String authorName) {
			this.authorName = authorName;
		}

		public String getTags() {
			return tags;
		}

		public void setTags(String tags) {
			this.tags = tags;
		}

		public boolean isResolved() {
			return resolved;
		}

		public void setResolved(boolean resolved) {
			this.resolved = resolved;
		}

		@Override
		public String toString() {
			String status = resolved ? "RESOLVED" : "OPEN";
			return "#" + postId + " [" + status + "] " + title + " (by " + authorName + ", tags: " + tags + ")";
		}
	}

	/*******
	 * <p> Title: Reply Class </p>
	 *
	 * <p> Description: Represents a reply to a discussion post.</p>
	 */
	public static class Reply {
		private int replyId;
		private int postId;
		private String body;
		private String authorName;
		private LocalDateTime createdAt;
		private boolean instructorAnswer;
		private boolean endorsed;

		public Reply(int replyId, int postId, String body, String authorName, LocalDateTime createdAt,
				boolean instructorAnswer, boolean endorsed) {
			this.replyId = replyId;
			this.postId = postId;
			this.body = body;
			this.authorName = authorName;
			this.createdAt = createdAt;
			this.instructorAnswer = instructorAnswer;
			this.endorsed = endorsed;
		}

		public int getReplyId() {
			return replyId;
		}

		public int getPostId() {
			return postId;
		}

		public void setPostId(int postId) {
			this.postId = postId;
		}

		public String getBody() {
			return body;
		}

		public void setBody(String body) {
			this.body = body;
		}

		public String getAuthorName() {
			return authorName;
		}

		public void setAuthorName(String authorName) {
			this.authorName = authorName;
		}

		public boolean isInstructorAnswer() {
			return instructorAnswer;
		}

		public void setInstructorAnswer(boolean instructorAnswer) {
			this.instructorAnswer = instructorAnswer;
		}

		public boolean isEndorsed() {
			return endorsed;
		}

		public void setEndorsed(boolean endorsed) {
			this.endorsed = endorsed;
		}

		@Override
		public String toString() {
			String kind = instructorAnswer ? "STAFF" : "STUDENT";
			String flag = endorsed ? "ENDORSED" : "UNENDORSED";
			return "#" + replyId + " [post #" + postId + ", " + kind + ", " + flag + "] " + body;
		}
	}

	private static final List<Post> posts = new ArrayList<>();
	private static final List<Reply> replies = new ArrayList<>();
	private static int nextPostId = 1;
	private static int nextReplyId = 1;

	static {
		// Seed posts with realistic Ed-style data
		createPost("HW1 Q3 Big-O clarification",
				"In HW1 question 3, should we assume the input list is already sorted "
						+ "when analyzing the Big-O of our algorithm?",
				"student_ahmed", "hw1,big-o", false);

		createPost("TP1 database schema question",
				"For TP1, can we store all discussion posts in a single table, or should we "
						+ "normalize posts and replies into separate tables?",
				"student_jane", "tp1,database,design", false);

		createPost("Lecture 5 recording link",
				"Could someone please share the link to the Lecture 5 recording? "
						+ "I cannot find it in Canvas.",
				"student_mia", "lecture,logistics", true);

		// Seed replies related to the above posts
		createReply(1,
				"You should assume the input is not sorted unless the problem statement "
						+ "explicitly says so.",
				"instructor_roberts", true, true);

		createReply(2,
				"It's cleaner to use separate tables for posts and replies, with a foreign key "
						+ "from reply to post.",
				"ta_sam", true, true);

		createReply(3,
				"The recording is in the 'Media' tab under Lecture 5.",
				"student_luis", false, false);
	}

	// Post operations

	public static Post createPost(String title, String body, String authorName, String tags, boolean resolved) {
		Post p = new Post(nextPostId++, title, body, authorName, tags, LocalDateTime.now(), resolved);
		posts.add(p);
		return p;
	}

	public static List<Post> getAllPosts() {
		return new ArrayList<>(posts);
	}

	public static Post getPostById(int id) {
		for (Post p : posts) {
			if (p.getPostId() == id) {
				return p;
			}
		}
		return null;
	}

	public static boolean deletePost(int id) {
		boolean removed = posts.removeIf(p -> p.getPostId() == id);
		if (removed) {
			replies.removeIf(r -> r.getPostId() == id);
		}
		return removed;
	}

	public static List<Post> searchPostsByKeyword(String keyword) {
		if (keyword == null || keyword.trim().isEmpty()) {
			return getAllPosts();
		}
		String lower = keyword.toLowerCase();
		return posts.stream()
				.filter(p -> p.getTitle().toLowerCase().contains(lower)
						|| p.getBody().toLowerCase().contains(lower))
				.collect(Collectors.toList());
	}

	// Reply operations

	public static Reply createReply(int postId, String body, String authorName,
			boolean instructorAnswer, boolean endorsed) {
		Reply r = new Reply(nextReplyId++, postId, body, authorName, LocalDateTime.now(), instructorAnswer, endorsed);
		replies.add(r);
		return r;
	}

	public static List<Reply> getAllReplies() {
		return new ArrayList<>(replies);
	}

	public static Reply getReplyById(int id) {
		for (Reply r : replies) {
			if (r.getReplyId() == id) {
				return r;
			}
		}
		return null;
	}

	public static boolean deleteReply(int id) {
		return replies.removeIf(r -> r.getReplyId() == id);
	}

	public static List<Reply> getRepliesForPost(int postId) {
		return replies.stream()
				.filter(r -> r.getPostId() == postId)
				.collect(Collectors.toList());
	}

	public static List<Reply> searchRepliesByKeyword(String keyword) {
		if (keyword == null || keyword.trim().isEmpty()) {
			return getAllReplies();
		}
		String lower = keyword.toLowerCase();
		return replies.stream()
				.filter(r -> r.getBody().toLowerCase().contains(lower))
				.collect(Collectors.toList());
	}
}

