# CREATE TABLE regional_cities
# (
#     id   BIGINT AUTO_INCREMENT PRIMARY KEY,
#     city VARCHAR(255),
#     lat  DECIMAL(9, 6),
#     lon  DECIMAL(9, 6)
# );

INSERT INTO regional_cities (city, lat, lon)
VALUES ('Brno', 49.195278, 16.608611),
       ('České Budějovice', 48.974444, 14.474444),
       ('Hradec Králové', 50.209444, 15.8325),
       ('Jihlava', 49.396944, 15.590833),
       ('Karlovy Vary', 50.232222, 12.871389),
       ('Liberec', 50.764722, 15.046667),
       ('Olomouc', 49.593889, 17.250833),
       ('Ostrava', 49.841111, 18.291944),
       ('Pardubice', 50.034444, 15.782778),
       ('Plzeň', 49.743056, 13.373056),
       ('Praha', 50.0755381, 14.4378005),
       ('Ústí nad Labem', 50.660556, 14.041944),
       ('Zlín', 49.226389, 17.668333);
