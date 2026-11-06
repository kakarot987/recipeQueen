# Queen's Kitchen / RecipeQueen

A Spring Boot 2.5 + Java 11 + MySQL 8 recipe API with an Angular 13 frontend.

> **Security notice:** This repository previously contained hardcoded database passwords and a JWT secret.
> Treat those values as compromised, rotate them in every environment where they were used,
> and remove them from deployments. Removing values from the current branch does not erase Git history.
> All secrets are now supplied at runtime via environment variables.

## Quick start

**Prerequisites:** Docker with Compose, Java 11, Node.js 16 (legacy Angular 13), npm.

The Angular lockfile currently has an older ng-bootstrap peer dependency. The project-local `.npmrc` uses `legacy-peer-deps=true` as a **temporary compatibility measure**, not a substitute for upgrading Angular and ng-bootstrap together.

1. From `QueensKitchenSpringBoot/`, copy `.env.example` to `.env` and replace *all* placeholder secrets.
2. Run `./gradlew clean bootJar` (Windows: `gradlew.bat clean bootJar`).
3. Run `docker compose up --build -d` from the same backend directory.
4. Run `cd Angular/QueenKitchen && npm ci && npm start` from the repository root.
5. Visit http://localhost:4200, register, sign in, publish a recipe, and try searching.

**Alternative backend without Docker:** Start MySQL locally and export
`DB_URL`, `DB_USERNAME`, `DB_PASSWORD` and `JWT_SECRET`, then run `./gradlew bootRun`.
The default API port is 5000. The MySQL Compose port exposed to the host is 3307;
inside Compose the API connects to `mysql_db:3306`.

For development only, the schema is currently managed with `spring.jpa.hibernate.ddl-auto=update`.
Before production, replace this with reviewed Flyway migrations and `validate`.
Do not deploy this legacy dependency stack until dependencies and security settings are reviewed and upgraded.

## Features

- Register and sign in with JWT-backed sessions.
- Create, edit, delete and publish recipes, or keep them as private drafts.
- Search by title and ingredients, filter by category, paginate results.
- Add JPEG/PNG recipe photos (2 MB maximum), stored in MySQL.
- Favorite and unfavorite published recipes, with persistent database-backed favorites.
- Build a session shopping list from selected recipes.
- Responsive Angular dashboard and form validation.

Recipe editing and deletion require ownership according to the authenticated token.
Published recipes are discoverable to signed-in users; private drafts are only available to their owners.
All API routes except health and sign-in/sign-up require authentication.

## API overview

| Method | Path | Purpose |
| --- | --- | --- |
| POST | `/api/auth/signup` | Register a normal user |
| POST | `/api/auth/signin` | Obtain JWT |
| GET | `/api/recipes` | Search, category, ingredient, mine, page, size |
| GET | `/api/recipes/{id}` | Fetch visible recipe |
| POST | `/api/recipes` | Create recipe |
| PUT | `/api/recipes/{id}` | Update owned recipe |
| DELETE | `/api/recipes/{id}` | Delete owned recipe |
| POST | `/api/recipes/{id}/image` | Upload owned recipe JPEG/PNG as multipart `file` |
| GET | `/api/recipes/{id}/image` | Fetch visible recipe image |
| GET | `/api/favorites` | List personal favorites |
| POST | `/api/favorites/{id}` | Favorite a visible recipe |
| DELETE | `/api/favorites/{id}` | Remove favorite |

Example authenticated search:
```bash
curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:5000/api/recipes?ingredient=tomato&category=veg&page=0&size=9"
```

Example recipe request:
```json
{
  "title": "Tomato soup",
  "category": "veg",
  "categoryDescription": "A quick tomato soup",
  "published": true,
  "recipeIngredientList": [{"name": "Tomato", "ingredientQuantity": "3 pieces"}],
  "recipeStepsList": [{"number": "1", "headline": "Simmer", "description": "Cook the tomatoes."}]
}
```

## Local verification checklist

1. Run `./gradlew test` in `QueensKitchenSpringBoot`.
2. Run `npm run build` in `Angular/QueenKitchen`.
3. Register two separate users and confirm neither can edit/delete the other's recipe (expect HTTP 403).
4. Save a draft and confirm the other account cannot discover or view it (expect HTTP 404).
5. Search using blanks and unknown ingredients, then test page navigation.
6. Upload a valid image and reject a malformed or oversized upload.
7. Favorite a recipe, restart Compose, and confirm it remains in Favorites.
8. Create a shopping list and verify ingredients from selected recipes appear.

## Known limitations and next steps

- The shopping list lives in browser component state and does not survive a refresh.
- Images are stored as database BLOBs; object storage is preferable at scale.
- The existing Spring Boot and Angular framework versions need planned upgrades.
- OAuth, ratings, meal planning and nutritional data are not yet implemented.
- Database migrations, more integration/E2E tests, rate limiting, monitoring and deployment hardening remain future work.
- Do not rely on Angular route hiding for authorization; the backend checks ownership.
