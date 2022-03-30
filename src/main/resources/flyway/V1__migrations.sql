CREATE TABLE users(
  id SERIAL NOT NULL PRIMARY KEY,
  u_key VARCHAR(200) NOT NULL,
  u_value TEXT NOT NULL,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL
);

CREATE UNIQUE INDEX user_profile_key ON users(u_key);

CREATE TABLE statuses
(
   u_id integer REFERENCES users(id) UNIQUE,
   u_status VARCHAR(200) NOT NULL,
   updated_at TIMESTAMP NOT NULL
);
