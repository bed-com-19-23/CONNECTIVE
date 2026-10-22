# Tasks

- `[x]` Candidate Qualification & Ineligibility Checking Logic
  - `[x]` Define `QualificationResult` sealed class
  - `[x]` Implement `minEducation` and `minExperienceYears` verification logic
  - `[x]` Surface qualification error card in `JobDetailsScreen`
- `[x]` Document & Attachment Uploading
  - `[x]` Add attachment list to `JobApplication`
  - `[x]` Add document selector in application dialog
  - `[x]` Display attachments in employer candidate review screen (`CandidatesScreen`)
- `[x]` SQLite Persistence & Session Preserving
  - `[x]` Implement `ConnectiveDbHelper` for Users, Jobs, Applications, Messages
  - `[x]` Implement `SessionManager` for session persistence
  - `[x]` Wire SQLite persistence into `ConnectiveRepository`
- `[x]` Unit Testing & Verification
  - `[x]` Write `HomeScreenTest` covering qualification logic and repository operations
  - `[x]` Verify zero static analysis errors across project files
