document.addEventListener('DOMContentLoaded', function() {
    const movieSelect = document.getElementById('movieId');
    const hallSelect = document.getElementById('hallId');
    const durationInput = document.getElementById('duration');
    const dateInput = document.getElementById('date');
    const startTimeInput = document.getElementById('startTime');
    const priceInput = document.getElementById('price');
    const form = document.getElementById('addSeanceForm');

    // Загрузка фильмов и количества залов
    function loadData() {
        fetch('/admin/control-seances-add-data')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Ошибка загрузки данных');
                }
                return response.json();
            })
            .then(apiResponse => {
                if (!apiResponse.success) {
                    throw new Error(apiResponse.message || 'Ошибка загрузки данных');
                }

                const data = apiResponse.data;

                // Заполняем список фильмов
                movieSelect.innerHTML = '<option value="">Выберите фильм</option>';

                Object.keys(data).forEach(key => {
                    if (key === 'countHalls') return;

                    const title = key;
                    const duration = data[key];

                    const option = document.createElement('option');
                    option.value = title;
                    option.textContent = title;
                    option.dataset.duration = duration;
                    movieSelect.appendChild(option);
                });

                // Заполняем список залов
                hallSelect.innerHTML = '<option value="">Выберите зал</option>';
                const countHalls = parseInt(data['countHalls']);

                if (countHalls && countHalls > 0) {
                    for (let i = 1; i <= countHalls; i++) {
                        const option = document.createElement('option');
                        option.value = i;
                        option.textContent = 'Зал ' + i;
                        hallSelect.appendChild(option);
                    }
                }
            })
            .catch(error => {
                console.error('Ошибка:', error);
                movieSelect.innerHTML = '<option value="">Ошибка загрузки фильмов</option>';
                hallSelect.innerHTML = '<option value="">Ошибка загрузки залов</option>';
            });
    }

    // Автоматическое заполнение длительности при выборе фильма
    movieSelect.addEventListener('change', function() {
        const selectedOption = this.options[this.selectedIndex];
        const duration = selectedOption.dataset.duration;
        if (duration) {
            durationInput.value = duration;
        } else {
            durationInput.value = 'Длительность не указана';
        }
    });

    // Отправка формы
    form.addEventListener('submit', function(e) {
        e.preventDefault();

        const movieTitle = movieSelect.value;
        const hallId = hallSelect.value;
        const startTime = startTimeInput.value;
        const price = priceInput.value;

        // Валидация
        if (!movieTitle) {
            alert('Пожалуйста, выберите фильм');
            return;
        }

        if (!hallId) {
            alert('Пожалуйста, выберите зал');
            return;
        }

        if (!startTime) {
            alert('Пожалуйста, выберите время начала сеанса');
            return;
        }

        if (!price || price <= 0) {
            alert('Пожалуйста, укажите корректную цену билета');
            return;
        }

        // Формируем запрос
        const requestData = {
            movieTitle: movieTitle,
            hallId: parseInt(hallId),
            start_time: startTime,
            price: parseFloat(price)
        };

        // Отправляем запрос
        fetch('/admin/control-seances-add', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(requestData)
        })
            .then(response => {
                // Парсим JSON в любом случае
                return response.json().then(data => {
                    return { data: data };
                });
            })
            .then(({ data }) => {
                if (data.success) {
                    alert('✅ ' + (data.message || 'Сеанс успешно добавлен!'));
                    window.location.href = '/admin/control-seances';
                } else {
                    // Показываем message из ответа
                    const errorMessage = data.message || 'Произошла ошибка';
                    alert('❌ ' + errorMessage);
                }
            })
            .catch(error => {
                console.error('Ошибка:', error);
                alert('❌ Ошибка соединения с сервером');
            });
    });

    // Загружаем данные при загрузке страницы
    loadData();
});