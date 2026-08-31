
-- 1. ЗАЛЫ
INSERT INTO hall (id, rows, seats_per_row)
SELECT * FROM (VALUES
                   (1, 10, 12),
                   (2, 8, 14),
                   (3,  12, 10),
                   (4,  10, 12),
                   (5, 8, 12)
              ) AS h(id, rows, seats_per_row)
WHERE NOT EXISTS (SELECT 1 FROM hall WHERE hall.id = h.id);

ALTER TABLE movie ALTER COLUMN active SET DEFAULT true;
-- 2. ФИЛЬМЫ (все 7 фильмов)
INSERT INTO movie (title, description, duration, genre, age_rating, poster_url, backdrop_url)
SELECT * FROM (VALUES
    ('Барби', 'Барби и Кен отправляются в приключение в реальном мире после того, как идеальный мир Барбиленда начинает давать трещину', 114, 'комедия', 12, '/images/posters/barbie.jpg', '/images/backdrops/barbie-bg.jpg'),
    ('Оппенгеймер', 'История американского физика Роберта Оппенгеймера, который руководил созданием первой атомной бомбы', 180, 'биография', 18, '/images/posters/oppenheimer.jpg', '/images/backdrops/oppenheimer-bg.jpg'),
    ('Дюна: Часть вторая', 'Пол Атрейдес объединяется с Чанни и фрименами, чтобы отомстить заговорщикам, уничтожившим его семью', 166, 'фантастика', 16, '/images/posters/dune2.jpg', '/images/backdrops/dune2-bg.jpg'),
    ('Операция "Фортуна"', 'Британский разведчик вынужден работать с голливудской звездой, чтобы завербовать миллиардера и остановить продажу смертоносного оружия', 114, 'боевик', 16, '/images/posters/fortune.jpg', '/images/backdrops/fortune-bg.jpg'),
    ('Наполеон', 'Взгляд на происхождение Наполеона Бонапарта и его стремительное восхождение к власти на фоне его зависимых отношений с женой Жозефиной', 158, 'история', 18, '/images/posters/napoleon.jpg', '/images/backdrops/napoleon-bg.jpg'),
    ('Убийцы цветочной луны', 'Расследование серии убийств индейцев племени осейджей после того, как на их землях нашли нефть', 206, 'детектив', 18, '/images/posters/killers.jpg', '/images/backdrops/killers-bg.jpg'),
    ('Мастер и Маргарита', 'Мистическая история о визите дьявола в советскую Москву, переплетающаяся с романом о Понтии Пилате', 157, 'мистика', 16, '/images/posters/master.jpg', '/images/backdrops/master-bg.jpg')
) AS m(title, description, duration, genre, age_rating, poster_url, backdrop_url);

INSERT INTO users (email, password, name, birth_date, role)
VALUES ('admin@email.com', '$2a$10$gSTUsYNKz.y9zuWjUSgljegSx6pXw3Ep8lvWY1/eESByh7K8b.bCe', 'adminame', '2005-06-25', 'ADMIN');
INSERT INTO users (email, password, name, birth_date, role)
VALUES ('user@email.com', '$2a$10$hCeKBUgH0EHGfAe/qEKMier5d.0wyNzzig5yksNE3H8MV5AFbPCby', 'username', '2005-06-25', 'USER');

ALTER TABLE seance ALTER COLUMN cancelled SET DEFAULT false;
ALTER TABLE seance ALTER COLUMN price SET DEFAULT 0;
INSERT INTO seance (start_time, end_time, movie_id, hall_id)
SELECT * FROM (VALUES

                   -- === ЗАЛ 1 ===
                   ('10:00:00'::time, '12:46:00'::time, 3, 1),   -- Дюна 2 (166 мин)
                   ('13:00:00'::time, '15:00:00'::time, 1, 1),   -- Оппенгеймер (120 мин)
                   ('15:30:00'::time, '17:24:00'::time,  2, 1),   -- Барби (114 мин)
                   ('18:00:00'::time, '19:54:00'::time, 4, 1),   -- Фортуна (114 мин)
                   ('20:30:00'::time, '23:08:00'::time, 5, 1),   -- Наполеон (158 мин)

                   -- === ЗАЛ 2 ===
                   ('10:30:00'::time, '13:16:00'::time, 3, 2),   -- Дюна 2 (166 мин)
                   ('14:00:00'::time, '17:00:00'::time, 1, 2),   -- Оппенгеймер (180 мин)
                   ('17:30:00'::time, '19:24:00'::time, 2, 2),   -- Барби (114 мин)
                   ('20:00:00'::time, '21:54:00'::time, 4, 2),   -- Фортуна (114 мин)

                   -- === ЗАЛ 3 ===
                   ('11:00:00'::time, '13:24:00'::time, 6, 3),   -- Убийцы (144 мин)
                   ('14:00:00'::time, '16:38:00'::time, 5, 3),   -- Наполеон (158 мин)
                   ('17:00:00'::time, '20:00:00'::time, 1, 3),   -- Оппенгеймер (180 мин)
                   ('20:30:00'::time, '22:24:00'::time, 2, 3),   -- Барби (114 мин)

                   -- === ЗАЛ 4 (НОВЫЙ) ===
                   ('09:30:00'::time, '12:16:00'::time, 3, 4),   -- Дюна 2 (166 мин)
                   ('13:00:00'::time, '16:00:00'::time, 1, 4),   -- Оппенгеймер (180 мин)
                   ('16:30:00'::time, '18:24:00'::time, 2, 4),   -- Барби (114 мин)
                   ('19:00:00'::time, '20:54:00'::time, 4, 4),   -- Фортуна (114 мин)
                   ('21:30:00'::time, '23:08:00'::time, 5, 4),   -- Наполеон (98 мин)

                   -- === ЗАЛ 5 (НОВЫЙ) ===
                   ('10:00:00'::time, '13:00:00'::time, 1, 5),   -- Оппенгеймер (180 мин)
                   ('13:30:00'::time, '15:24:00'::time, 2, 5),   -- Барби (114 мин)
                   ('16:00:00'::time, '18:46:00'::time, 3, 5),   -- Дюна 2 (166 мин)
                   ('19:30:00'::time, '22:06:00'::time, 6, 5)    -- Убийцы (156 мин)

              ) AS s(start_time, end_time, movie_id, hall_id)
WHERE NOT EXISTS (
    SELECT 1 FROM seance
    WHERE seance.start_time = s.start_time
      AND seance.movie_id = s.movie_id
      AND seance.hall_id = s.hall_id
);

INSERT INTO seat (row, number, status, type, hall_id, seance_id)
SELECT
    row_num,
    seat_num,
    'FREE'::varchar,
    'ORDINARY'::varchar,
    h.id,
    s.id
FROM seance s
         JOIN hall h ON h.id = s.hall_id
         CROSS JOIN generate_series(1, h.rows) AS row_num
         CROSS JOIN generate_series(1, h.seats_per_row) AS seat_num
WHERE NOT EXISTS (
    SELECT 1 FROM seat
    WHERE seat.hall_id = h.id
      AND seat.row = row_num
      AND seat.number = seat_num
      AND seat.seance_id = s.id
);

UPDATE seance SET price =
                      CASE
                          -- === ЗАЛ 1 ===
                          WHEN id = 1 THEN 500  -- 10:00 Дюна 2
                          WHEN id = 2 THEN 500  -- 13:00 Оппенгеймер
                          WHEN id = 3 THEN 500  -- 15:30 Барби
                          WHEN id = 4 THEN 600  -- 18:00 Фортуна (вечер)
                          WHEN id = 5 THEN 600  -- 20:30 Наполеон (вечер)

                      -- === ЗАЛ 2 ===
                          WHEN id = 6 THEN 450  -- 10:30 Дюна 2
                          WHEN id = 7 THEN 450  -- 14:00 Оппенгеймер
                          WHEN id = 8 THEN 450  -- 17:30 Барби
                          WHEN id = 9 THEN 550  -- 20:00 Фортуна (вечер)

                      -- === ЗАЛ 3 ===
                          WHEN id = 10 THEN 400 -- 11:00 Убийцы
                          WHEN id = 11 THEN 400 -- 14:00 Наполеон
                          WHEN id = 12 THEN 400 -- 17:00 Оппенгеймер
                          WHEN id = 13 THEN 500 -- 20:30 Барби (вечер)

                      -- === ЗАЛ 4 ===
                          WHEN id = 14 THEN 350 -- 09:30 Дюна 2
                          WHEN id = 15 THEN 350 -- 13:00 Оппенгеймер
                          WHEN id = 16 THEN 350 -- 16:30 Барби
                          WHEN id = 17 THEN 450 -- 19:00 Фортуна (вечер)
                          WHEN id = 18 THEN 450 -- 21:30 Наполеон (вечер)

                      -- === ЗАЛ 5 ===
                          WHEN id = 19 THEN 300 -- 10:00 Оппенгеймер
                          WHEN id = 20 THEN 300 -- 13:30 Барби
                          WHEN id = 21 THEN 300 -- 16:00 Дюна 2
                          WHEN id = 22 THEN 400 -- 19:30 Убийцы (вечер)

                          ELSE 350
                          END
WHERE price = 0 OR price IS NULL;