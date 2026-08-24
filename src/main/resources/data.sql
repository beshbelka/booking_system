
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

-- 2. ФИЛЬМЫ (все 7 фильмов)
INSERT INTO movie (id, title, description, duration, genre, age_rating, poster_url, backdrop_url)
SELECT * FROM (VALUES
    (1, 'Оппенгеймер', 'История американского физика Роберта Оппенгеймера, который руководил созданием первой атомной бомбы', 180, 'биография', '18+', '/images/posters/oppenheimer.jpg', '/images/backdrops/oppenheimer-bg.jpg'),
    (2, 'Барби', 'Барби и Кен отправляются в приключение в реальном мире после того, как идеальный мир Барбиленда начинает давать трещину', 114, 'комедия', '12+', '/images/posters/barbie.jpg', '/images/backdrops/barbie-bg.jpg'),
    (3, 'Дюна: Часть вторая', 'Пол Атрейдес объединяется с Чанни и фрименами, чтобы отомстить заговорщикам, уничтожившим его семью', 166, 'фантастика', '16+', '/images/posters/dune2.jpg', '/images/backdrops/dune2-bg.jpg'),
    (4, 'Операция "Фортуна"', 'Британский разведчик вынужден работать с голливудской звездой, чтобы завербовать миллиардера и остановить продажу смертоносного оружия', 114, 'боевик', '16+', '/images/posters/fortune.jpg', '/images/backdrops/fortune-bg.jpg'),
    (5, 'Наполеон', 'Взгляд на происхождение Наполеона Бонапарта и его стремительное восхождение к власти на фоне его зависимых отношений с женой Жозефиной', 158, 'история', '18+', '/images/posters/napoleon.jpg', '/images/backdrops/napoleon-bg.jpg'),
    (6, 'Убийцы цветочной луны', 'Расследование серии убийств индейцев племени осейджей после того, как на их землях нашли нефть', 206, 'детектив', '18+', '/images/posters/killers.jpg', '/images/backdrops/killers-bg.jpg'),
    (7, 'Мастер и Маргарита', 'Мистическая история о визите дьявола в советскую Москву, переплетающаяся с романом о Понтии Пилате', 157, 'мистика', '16+', '/images/posters/master.jpg', '/images/backdrops/master-bg.jpg')
) AS m(id, title, description, duration, genre, age_rating, poster_url, backdrop_url)
WHERE NOT EXISTS (SELECT 1 FROM movie WHERE movie.id = m.id);

INSERT INTO users (email, password, name, birth_date, role)
VALUES ('user@email.com', '$2a$10$hCeKBUgH0EHGfAe/qEKMier5d.0wyNzzig5yksNE3H8MV5AFbPCby', 'username', '2005-06-25', 'USER');
INSERT INTO users (email, password, name, birth_date, role)
VALUES ('admin@email.com', '$2a$10$gSTUsYNKz.y9zuWjUSgljegSx6pXw3Ep8lvWY1/eESByh7K8b.bCe', 'adminame', '2005-06-25', 'ADMIN');

INSERT INTO seance (start_time, end_time, status, movie_id, hall_id)
SELECT * FROM (VALUES

                   -- === ЗАЛ 1 ===
                   ('10:00:00'::time, '12:46:00'::time, 'UNDEFINED', 3, 1),   -- Дюна 2 (166 мин)
                   ('13:00:00'::time, '15:00:00'::time, 'UNDEFINED', 1, 1),   -- Оппенгеймер (120 мин)
                   ('15:30:00'::time, '17:24:00'::time, 'UNDEFINED', 2, 1),   -- Барби (114 мин)
                   ('18:00:00'::time, '19:54:00'::time, 'UNDEFINED', 4, 1),   -- Фортуна (114 мин)
                   ('20:30:00'::time, '23:08:00'::time, 'UNDEFINED', 5, 1),   -- Наполеон (158 мин)

                   -- === ЗАЛ 2 ===
                   ('10:30:00'::time, '13:16:00'::time, 'UNDEFINED', 3, 2),   -- Дюна 2 (166 мин)
                   ('14:00:00'::time, '17:00:00'::time, 'UNDEFINED', 1, 2),   -- Оппенгеймер (180 мин)
                   ('17:30:00'::time, '19:24:00'::time, 'UNDEFINED', 2, 2),   -- Барби (114 мин)
                   ('20:00:00'::time, '21:54:00'::time, 'UNDEFINED', 4, 2),   -- Фортуна (114 мин)

                   -- === ЗАЛ 3 ===
                   ('11:00:00'::time, '13:24:00'::time, 'UNDEFINED', 6, 3),   -- Убийцы (144 мин)
                   ('14:00:00'::time, '16:38:00'::time, 'UNDEFINED', 5, 3),   -- Наполеон (158 мин)
                   ('17:00:00'::time, '20:00:00'::time, 'UNDEFINED', 1, 3),   -- Оппенгеймер (180 мин)
                   ('20:30:00'::time, '22:24:00'::time, 'UNDEFINED', 2, 3),   -- Барби (114 мин)

                   -- === ЗАЛ 4 (НОВЫЙ) ===
                   ('09:30:00'::time, '12:16:00'::time, 'UNDEFINED', 3, 4),   -- Дюна 2 (166 мин)
                   ('13:00:00'::time, '16:00:00'::time, 'UNDEFINED', 1, 4),   -- Оппенгеймер (180 мин)
                   ('16:30:00'::time, '18:24:00'::time, 'UNDEFINED', 2, 4),   -- Барби (114 мин)
                   ('19:00:00'::time, '20:54:00'::time, 'UNDEFINED', 4, 4),   -- Фортуна (114 мин)
                   ('21:30:00'::time, '23:08:00'::time, 'UNDEFINED', 5, 4),   -- Наполеон (98 мин)

                   -- === ЗАЛ 5 (НОВЫЙ) ===
                   ('10:00:00'::time, '13:00:00'::time, 'UNDEFINED', 1, 5),   -- Оппенгеймер (180 мин)
                   ('13:30:00'::time, '15:24:00'::time, 'UNDEFINED', 2, 5),   -- Барби (114 мин)
                   ('16:00:00'::time, '18:46:00'::time, 'UNDEFINED', 3, 5),   -- Дюна 2 (166 мин)
                   ('19:30:00'::time, '22:06:00'::time, 'UNDEFINED', 6, 5)    -- Убийцы (156 мин)

              ) AS s(start_time, end_time, status, movie_id, hall_id)
WHERE NOT EXISTS (
    SELECT 1 FROM seance
    WHERE seance.start_time = s.start_time
      AND seance.movie_id = s.movie_id
      AND seance.hall_id = s.hall_id
);

INSERT INTO seat (row, number, status, price, type, hall_id, seance_id)
SELECT
    row_num,
    seat_num,
    'FREE'::varchar,
    0,
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