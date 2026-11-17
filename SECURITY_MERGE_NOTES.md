# PR #1 security merge reconciliation

This feature branch was rebased in effect by merging `main` (after PR #4) while preserving the in-progress secure-recipe implementation and its stacked downstream PRs.

**Preserved:** feature branch recipe CRUD and ownership logic; minimal principal-only login response; customer-only signup; explicit CORS origins; feature database settings and Docker Compose parameters.

**Integrated from newer main:** root secret-ignore rules, root example secrets file, base backend regression CI, signup regression test (adapted to the new `signup` controller method), and Docker image built from source using Gradle.

**Still pending:** PR #1 is a draft until the CI checks and local frontend/backend smoke tests pass. PRs #2 and #3 remain stacked and need separate validation. Credentials committed in history must be rotated if reused. This merge does not prove production-readiness or a successful browser walkthrough.
