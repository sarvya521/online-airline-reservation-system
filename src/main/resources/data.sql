INSERT INTO app_user(email, password, f_name, l_name, role)
SELECT 'bruce_wayne@batman.com', 'martha', 'bruce', 'wayne', 'ADMIN'
FROM app_user
WHERE (SELECT COUNT(1) FROM app_user WHERE email = 'bruce_wayne@batman.com') != 1;

--INSERT INTO app_user(email, password, f_name, l_name, role)
--VALUES ('bruce_wayne@batman.com', 'martha', 'bruce', 'wayne', 'ADMIN')
--
--INSERT INTO airport(name, alias) VALUES
--('Los Angeles International Airport', 'LAX'),
--('Denver International Airport', 'DEN'),
--('Dallas/Fort Worth International Airport', 'DFW'),
--('John F. Kennedy International Airport', 'JFK'),
--('San Francisco International Airport', 'SFO'),
--('Miami International Airport', 'MIA'),
--('San Diego International Airport', 'SAN'),
--('Portland International Airport', 'PDX');
--
--INSERT INTO aircraft(code, model, total_business_seats, total_firstclass_seats, total_economy_seats) VALUES
--('A380', 'Airbus', 6, 10, 20),
--('A330', 'Airbus', 6, 10, 20),
--('A320', 'Airbus', 6, 10, 20),
--('747', 'Boeing', 6, 10, 20);

--INSERT INTO flight(travel_date, departure_time, arrival_time, departure_from, arrival_at,
--business_class_fare, firstclass_fare, economy_class_fare,
--remaining_business_seats, remaining_firstclass_seats, remaining_economy_seats,
--airline, fk_aircraft_id) VALUES
--('2019-12-24', '10:00:00', '11:00:00', 1, 2,
--600, 1000, 200,
--6, 10, 20,
--'Lufthansa', 1),
--('2019-12-24', '10:00:00', '11:00:00', 1, 2,
--600, 1000, 200,
--6, 10, 20,
--'Emirate', 1),
--('2019-12-25', '10:00:00', '11:00:00', 1, 2,
--600, 1000, 200,
--6, 10, 20,
--'Lufthansa', 1),
--('2019-12-25', '10:00:00', '11:00:00', 1, 2,
--600, 1000, 200,
--6, 10, 20,
--'Emirate', 1),
--('2019-12-26', '10:00:00', '11:00:00', 1, 2,
--600, 1000, 200,
--6, 10, 20,
--'Lufthansa', 1),
--('2019-12-26', '10:00:00', '11:00:00', 1, 2,
--600, 1000, 200,
--6, 10, 20,
--'Emirate', 1),
--('2019-12-27', '10:00:00', '11:00:00', 1, 2,
--600, 1000, 200,
--6, 10, 20,
--'Lufthansa', 1),
--('2019-12-27', '10:00:00', '11:00:00', 1, 2,
--600, 1000, 200,
--6, 10, 20,
--'Emirate', 1);