# CREATE TABLE regional_cities
# (
#     id   BIGINT AUTO_INCREMENT PRIMARY KEY,
#     city VARCHAR(255),
#     lat  DECIMAL(9, 6),
#     lon  DECIMAL(9, 6)
# );

INSERT INTO regional_city (id, city, lat, lon)
VALUES (1, 'Brno', 49.195278, 16.608611),
       (2, 'České Budějovice', 48.974444, 14.474444),
       (3, 'Hradec Králové', 50.209444, 15.8325),
       (4, 'Jihlava', 49.396944, 15.590833),
       (5, 'Karlovy Vary', 50.232222, 12.871389),
       (6, 'Liberec', 50.764722, 15.046667),
       (7, 'Olomouc', 49.593889, 17.250833),
       (8, 'Ostrava', 49.841111, 18.291944),
       (9, 'Pardubice', 50.034444, 15.782778),
       (10, 'Plzeň', 49.743056, 13.373056),
       (11, 'Praha', 50.0755381, 14.4378005),
       (12, 'Ústí nad Labem', 50.660556, 14.041944),
       (13, 'Zlín', 49.226389, 17.668333);
