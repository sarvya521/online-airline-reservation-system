CREATE EXTENSION IF NOT EXISTS pgcrypto;
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'status') THEN
       CREATE TYPE status AS ENUM ('CREATED', 'UPDATED', 'DELETED', 'DEACTIVATED', 'REACTIVATED');
    END IF;
END
$$;

/*
 * - Every table has UUID along with its primary key.
 *    We shall not share ID(primary key) in API response, instead we should share UUID for particular entity
 *
 * - Every table has history table for auditing purpose. Master table and mapping tables have no history table
 *   History table will store extra columns like status, and performed_by
 *
 * - No UUID in mapping table
 *
 * - `status` is Enum
 * - `performed_by` (id of user whoever inserting/updating/deleting record in table)
 * - `ts` is TIMESTAMPTZ column which holds UTC timestamp
 *
 * - No foreign key constraints in history table
 */

/*
 * For every DML(insert,update,delete) action,
 * We will perform DML (One Insert in History and DML on original table)
 *  - insert into history table with latest status and current timestamp
 *  - then we will perform similar DML on original table
 * --------------------------------------------------------------------------------
 * Case 1: insert into user (insert bruce user)
 *
 *
 * user_history
 * id   uuid              name          status    performed_by    ts
 * 1    aasad-asas-asas   bruce         CREATED   10000           2019-10-10 10:10:10
 *
 * user
 * id   uuid              name          status    performed_by    ts
 * 1    aasad-asas-asas   bruce         CREATED   10000           2019-10-10 10:10:10
 * ---------------------------------------------------------------------------------
 * Case 2: update user (Update name of sarvesh)
 *
 * user_history
 * id   uuid              name          status    performed_by    ts
 * 1    aasad-asas-asas   bruce         CREATED   10000           2019-10-10 10:10:10
 * 1    aasad-asas-asas   batman        UPDATED   10001           2019-11-11 10:10:10
 *
 * user
 * id   uuid              name          status    performed_by    ts
 * 1    aasad-asas-asas   batman        UPDATED   10001           2019-11-11 10:10:10
 */

CREATE TABLE IF NOT EXISTS app_user (
  id SERIAL PRIMARY KEY,
  uuid UUID NOT NULL,
  email VARCHAR(50) NOT NULL,
  f_name VARCHAR(50) NOT NULL,
  l_name VARCHAR(50) NOT NULL,
  status status NOT NULL,
  performed_by BIGINT NOT NULL,
  ts TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uk_app_user_uuid UNIQUE(uuid),
  CONSTRAINT uk_app_user_email UNIQUE(email)
);
CREATE TABLE IF NOT EXISTS app_user_history (
  id BIGINT NOT NULL PRIMARY KEY,
  uuid UUID NOT NULL,
  email VARCHAR(50) NOT NULL,
  f_name VARCHAR(50) NOT NULL,
  l_name VARCHAR(50) NOT NULL,
  status status NOT NULL,
  performed_by BIGINT NOT NULL,
  ts TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS role (
  id SERIAL PRIMARY KEY,
  uuid UUID DEFAULT gen_random_uuid(),
  name VARCHAR(50) NOT NULL,
  status status NOT NULL,
  performed_by BIGINT NOT NULL,
  ts TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uk_role_uuid UNIQUE(uuid),
  CONSTRAINT uk_role_name UNIQUE(name)
);
CREATE TABLE IF NOT EXISTS role_history (
  id BIGINT NOT NULL PRIMARY KEY,
  uuid UUID NOT NULL,
  name VARCHAR(50) NOT NULL,
  status status NOT NULL,
  performed_by BIGINT NOT NULL,
  ts TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS claim (
  id SERIAL PRIMARY KEY,
  uuid UUID DEFAULT gen_random_uuid(),
  resource_name VARCHAR(50) NOT NULL,
  resource_http_method VARCHAR(50) NOT NULL,
  resource_endpoint VARCHAR(50) NOT NULL,
  status status NOT NULL,
  performed_by BIGINT NOT NULL,
  ts TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uk_claim_uuid UNIQUE(uuid),
  CONSTRAINT uk_claim_name UNIQUE(resource_name)
);
CREATE TABLE IF NOT EXISTS role_claim (
  fk_role_id BIGINT NOT NULL,
  fk_claim_id BIGINT NOT NULL,
  performed_by BIGINT NOT NULL,
  ts TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (fk_role_id, fk_claim_id),
  CONSTRAINT fk_role_claim_role FOREIGN KEY(fk_role_id) REFERENCES role(id),
  CONSTRAINT fk_role_claim_claim FOREIGN KEY(fk_claim_id) REFERENCES claim(id)
);
CREATE TABLE IF NOT EXISTS user_role (
  fk_user_id BIGINT NOT NULL,
  fk_role_id BIGINT NOT NULL,
  performed_by BIGINT NOT NULL,
  ts TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (fk_user_id, fk_role_id),
  CONSTRAINT fk_user_role_app_user FOREIGN KEY(fk_user_id) REFERENCES app_user(id),
  CONSTRAINT fk_user_role_role FOREIGN KEY(fk_role_id) REFERENCES role(id)
);