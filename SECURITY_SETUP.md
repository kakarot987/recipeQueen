# Local security setup

Public signup assigns only `ROLE_USER`; admin/moderator role provisioning must be performed by a trusted administrator, not via the signup payload. Protected API routes require a valid JWT, and recipe ownership is verified against the authenticated user ID.

Copy `.env.example` to `.env` in the repository root, set fresh **unique** passwords and an appropriately random JWT secret, then run `docker compose -f QueensKitchenSpringBoot/docker-compose.yml --env-file .env config` and `docker compose -f QueensKitchenSpringBoot/docker-compose.yml --env-file .env up --build`. When starting Spring without Compose, supply `JWT_SECRET`, `DB_PASSWORD`, and optionally `DB_USERNAME` and `SPRING_DATASOURCE_URL`.

Secrets previously committed are still present in Git history. Rotate any keys/passwords that were ever used; deleting tracked plaintext files alone cannot revoke them. This change does not claim the full end-to-end Playwright browser flow has been rerun or repaired.
