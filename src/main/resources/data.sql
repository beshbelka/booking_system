
-- 1. ЗАЛЫ
INSERT INTO hall (id)
SELECT id FROM (VALUES (1), (2), (3)) AS h(id)
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

-- 3. СЕАНСЫ (статус как СТРОКА)
INSERT INTO seance (id, start_time, end_time, movie_id, hall_id, status)
SELECT * FROM (VALUES
    (1, '10:00:00'::time, '13:00:00'::time, 1, 1, 'UNDEFINED'),
    (2, '13:30:00'::time, '15:24:00'::time, 2, 2, 'UNDEFINED'),
    (3, '16:00:00'::time, '18:46:00'::time, 3, 1, 'UNDEFINED'),
    (4, '11:00:00'::time, '12:54:00'::time, 4, 3, 'UNDEFINED'),
    (5, '14:00:00'::time, '16:38:00'::time, 5, 2, 'UNDEFINED'),
    (6, '18:30:00'::time, '21:30:00'::time, 1, 1, 'UNDEFINED'),
    (7, '19:00:00'::time, '21:37:00'::time, 7, 3, 'UNDEFINED'),
    (8, '20:00:00'::time, '22:46:00'::time, 3, 2, 'UNDEFINED')
) AS s(id, start_time, end_time, movie_id, hall_id, status)
WHERE NOT EXISTS (SELECT 1 FROM seance WHERE seance.id = s.id);