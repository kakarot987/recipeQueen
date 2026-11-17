-- Baseline schema for a new MySQL 8 database.
-- Existing legacy tables are preserved by IF NOT EXISTS statements; V2 handles missing columns.
CREATE TABLE IF NOT EXISTS users (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(20) NOT NULL,
  email VARCHAR(50) NOT NULL,
  password VARCHAR(120) NOT NULL,
  reset_password_token VARCHAR(255),
  UNIQUE KEY uk_users_username (username),
  UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS roles (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(20),
  UNIQUE KEY uk_roles_name (name)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS user_roles (
  user_id BIGINT NOT NULL,
  role_id INT NOT NULL,
  PRIMARY KEY (user_id, role_id),
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (role_id) REFERENCES roles(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS recipe_info (
  recipe_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  title VARCHAR(160) NOT NULL,
  category VARCHAR(60) NOT NULL,
  recipe_description VARCHAR(2000) NOT NULL,
  published BIT(1) NOT NULL DEFAULT b'1',
  servings INT NOT NULL DEFAULT 2,
  prep_minutes INT NOT NULL DEFAULT 10,
  cook_minutes INT NOT NULL DEFAULT 20,
  image_data LONGBLOB,
  image_content_type VARCHAR(30),
  INDEX idx_recipe_owner (user_id),
  INDEX idx_recipe_category (category)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS recipe_ingredients (
  ingredient_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  ingredient_quantity VARCHAR(80) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS recipe_steps (
  step_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  number VARCHAR(20) NOT NULL,
  headline VARCHAR(120) NOT NULL,
  description VARCHAR(2000) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS recipe_info_recipe_ingredients (
  recipe_recipe_id BIGINT NOT NULL,
  recipe_ingredient_list_ingredient_id BIGINT NOT NULL,
  PRIMARY KEY (recipe_recipe_id, recipe_ingredient_list_ingredient_id),
  UNIQUE KEY uk_recipe_ingredient_assignment (recipe_ingredient_list_ingredient_id),
  FOREIGN KEY (recipe_recipe_id) REFERENCES recipe_info(recipe_id),
  FOREIGN KEY (recipe_ingredient_list_ingredient_id) REFERENCES recipe_ingredients(ingredient_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS recipe_info_recipe_steps (
  recipe_recipe_id BIGINT NOT NULL,
  recipe_steps_step_id BIGINT NOT NULL,
  PRIMARY KEY (recipe_recipe_id, recipe_steps_step_id),
  UNIQUE KEY uk_recipe_step_assignment (recipe_steps_step_id),
  FOREIGN KEY (recipe_recipe_id) REFERENCES recipe_info(recipe_id),
  FOREIGN KEY (recipe_steps_step_id) REFERENCES recipe_steps(step_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS profile_pic (
  profile_id VARCHAR(255) NOT NULL PRIMARY KEY,
  name VARCHAR(255),
  type VARCHAR(255),
  data LONGBLOB,
  user_id BIGINT
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS recipe_favorites (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  recipe_id BIGINT NOT NULL,
  UNIQUE KEY uk_fav_user_recipe (user_id, recipe_id),
  FOREIGN KEY (recipe_id) REFERENCES recipe_info(recipe_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS shopping_items (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  name VARCHAR(100) NOT NULL,
  quantity VARCHAR(120) NOT NULL,
  checked BIT(1) NOT NULL DEFAULT b'0'
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS meal_plan_entries (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  meal_day DATE NOT NULL,
  meal_name VARCHAR(16) NOT NULL,
  recipe_id BIGINT NOT NULL,
  UNIQUE KEY uk_meal_user_day_slot (user_id, meal_day, meal_name),
  FOREIGN KEY (recipe_id) REFERENCES recipe_info(recipe_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS recipe_reviews (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  recipe_id BIGINT NOT NULL,
  rating INT NOT NULL,
  comment VARCHAR(1000),
  updated_at DATETIME(6) NOT NULL,
  UNIQUE KEY uk_review_user_recipe (user_id, recipe_id),
  FOREIGN KEY (recipe_id) REFERENCES recipe_info(recipe_id)
) ENGINE=InnoDB;
