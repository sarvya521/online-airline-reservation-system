CREATE TABLE IF NOT EXISTS app_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(320) NOT NULL,
  password VARCHAR(300) NOT NULL,
  f_name VARCHAR(50) NOT NULL,
  l_name VARCHAR(50) NOT NULL,
  CONSTRAINT uk_app_user_email UNIQUE(email)
);

CREATE TABLE IF NOT EXISTS app_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  CONSTRAINT uk_app_role_name UNIQUE(name)
);

CREATE TABLE IF NOT EXISTS user_role (
  fk_user_id BIGINT NOT NULL,
  fk_role_id BIGINT NOT NULL,
  PRIMARY KEY (fk_user_id, fk_role_id),
  CONSTRAINT fk_user_role_app_user FOREIGN KEY(fk_user_id) REFERENCES app_user(id),
  CONSTRAINT fk_user_role_app_role FOREIGN KEY(fk_role_id) REFERENCES app_role(id)
);
