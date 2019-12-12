create TABLE IF NOT EXISTS app_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(320) NOT NULL,
  password VARCHAR(300) NOT NULL,
  f_name VARCHAR(50) NOT NULL,
  l_name VARCHAR(50) NOT NULL,
  role VARCHAR(20) NOT NULL,
  CONSTRAINT uk_app_user_email UNIQUE(email)
);

create TABLE IF NOT EXISTS airport (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  alias VARCHAR(10) NOT NULL,
  CONSTRAINT uk_airport_name UNIQUE(name),
  CONSTRAINT uk_airport_alias UNIQUE(alias),
);

create TABLE IF NOT EXISTS aircraft (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(100) NOT NULL,
  model VARCHAR(10) NOT NULL,
  total_business_seats INT,
  total_firstclass_seats INT,
  total_economy_seats INT NOT NULL,
  CONSTRAINT uk_aircraft_code UNIQUE(code)
);

create TABLE IF NOT EXISTS flight (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  travel_date DATE NOT NULL,
  departure_time TIME NOT NULL,
  arrival_time TIME NOT NULL,
  departure_from BIGINT NOT NULL,
  arrival_at BIGINT NOT NULL,
  business_class_fare INT,
  firstclass_fare INT,
  economy_class_fare INT NOT NULL,
  remaining_business_seats INT,
  remaining_firstclass_seats INT,
  remaining_economy_seats INT,
  airline VARCHAR(50) NOT NULL,
  fk_aircraft_id BIGINT NOT NULL,
  CONSTRAINT fk_flight_departure_airport FOREIGN KEY(departure_from) REFERENCES airport(id),
  CONSTRAINT fk_flight_arrival_airport FOREIGN KEY(arrival_at) REFERENCES airport(id),
  CONSTRAINT fk_flight_arrival_aircraft FOREIGN KEY(fk_aircraft_id) REFERENCES aircraft(id)
);

create TABLE IF NOT EXISTS booking (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  pnr VARCHAR(10) NOT NULL,
  booking_date DATE NOT NULL,
  travel_date DATE NOT NULL,
  seat_class VARCHAR(10) NOT NULL,
  cost INT NOT NULL,
  airline VARCHAR(50) NOT NULL,
  fk_flight_id BIGINT NOT NULL,
  fk_user_id BIGINT NOT NULL,
  CONSTRAINT fk_booking_flight FOREIGN KEY(fk_flight_id) REFERENCES flight(id),
  CONSTRAINT fk_booking_app_user FOREIGN KEY(fk_user_id) REFERENCES app_user(id)
);


