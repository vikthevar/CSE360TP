// ============================================================
// HOW TO INTEGRATE THE GRADER DASHBOARD
// ============================================================
// 
// STEP 1 — Copy the package folder
// ----------------------------------
// Copy the entire  guiGraderDashboard/  folder into your project's
//   src/
// directory, so it sits alongside guiAdminHome, guiDiscussion, etc.
//
// Your src/ should look like:
//   src/
//     applicationMain/
//     database/
//     guiAdminHome/
//     guiGraderDashboard/       <-- NEW
//       GraderDashboardDataStore.java
//       GraderDashboardController.java
//       GraderDashboardPage.java
//     ...
//
//
// STEP 2 — Fix the database import in GraderDashboardDataStore.java
// ------------------------------------------------------------------
// Open GraderDashboardDataStore.java and update this import
// to match however your project gets a database connection:
//
//   import database.DatabaseHelper;   // <-- change to your actual class
//
// Then in the constructor, replace:
//   connection = DatabaseHelper.getConnection();
// with whatever your project uses, e.g.:
//   connection = DatabaseManager.getInstance().getConnection();
//
// Also update the SQL table/column names if yours differ from:
//   DiscussionPosts  (columns: id, thread_id, user_id, content, created_at)
//   DiscussionThreads (columns: id, title, created_by, created_at)
//   Users            (columns: id, username, email, role)
//
//
// STEP 3 — Add a button in guiAdminHome
// --------------------------------------
// In your AdminHome page class (inside guiAdminHome/), add:
//
//   import guiGraderDashboard.GraderDashboardPage;
//
// Then add a button to your admin home layout:
//
//   Button graderDashBtn = new Button("Grader Dashboard");
//   graderDashBtn.setOnAction(e -> {
//       GraderDashboardPage dashboard = new GraderDashboardPage();
//       dashboard.show(primaryStage);   // pass your existing Stage
//       // OR: dashboard.show();        // opens in a new Stage
//   });
//
//
// STEP 4 — Append the CSS
// -------------------------
// Open  guiGraderDashboard/dashboard_css_additions.css
// and copy-paste its entire contents to the bottom of:
//   src/applicationMain/application.css
//
//
// STEP 5 — Rebuild and run
// -------------------------
// In Eclipse: Project → Clean → Build Project, then run.
// The Grader Dashboard button should appear on the Admin Home screen.
//
// ============================================================
