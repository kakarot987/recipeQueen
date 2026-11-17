# PR #3 modernization merge reconciliation

This PR's old base, `feature/platform-completion`, was closed while the updated `feature/secure-recipes-and-discovery` branch incorporated that feature stack. PR #3 now incorporates the active PR #1 branch directly.

The merge preserves **Java 21, Spring Boot 4.1, Gradle 8.14.3, Angular 22, TypeScript 6, Flyway, JWT hardening, and the existing E2E workflow**. It also carries over root-level secret ignore rules and security regression tests from the parent branch; the legacy backend CI job was updated to Java 21.

The previous browser workflow failed at the registration message after submitting the form. The E2E scenario now checks the signup HTTP status separately so API failure cannot be misreported as a browser change-detection issue.

This is still a draft until all backend, frontend and real-browser tests pass on the current head. Local acceptance, backups, and rotating old credentials are separate requirements.
