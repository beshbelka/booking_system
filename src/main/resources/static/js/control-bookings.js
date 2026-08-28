document.addEventListener('DOMContentLoaded', function() {
    const findBtn = document.getElementById('findBookingsBtn');
    const resultsCount = document.getElementById('resultsCount');
    const tableBody = document.getElementById('bookingsTableBody');

    // Загрузка фильмов
    function loadFilms() {
        fetch('/admin/control-bookings-get-data')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Ошибка загрузки фильмов');
                }
                return response.json();
            })
            .then(apiResponse => {
                if (!apiResponse.success) {
                    throw new Error(apiResponse.message || 'Ошибка загрузки фильмов');
                }

                const data = apiResponse.data;
                const filmSelect = document.getElementById('filmSelect');

                filmSelect.innerHTML = '<option value="0">Все фильмы</option>';

                Object.keys(data).forEach(key => {
                    const option = document.createElement('option');
                    option.value = key;
                    option.textContent = data[key];
                    filmSelect.appendChild(option);
                });
            })
            .catch(error => {
                console.error('Ошибка:', error);
                const filmSelect = document.getElementById('filmSelect');
                filmSelect.innerHTML = '<option value="0">Ошибка загрузки фильмов</option>';
            });
    }

    // Поиск бронирований
    function searchBookings() {
        const filmId = document.getElementById('filmSelect').value || '0';
        const url = `/admin/control-bookings-find?movieId=${filmId}`;

        fetch(url)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Ошибка загрузки бронирований');
                }
                return response.json();
            })
            .then(bookings => {
                resultsCount.textContent = `Найдено бронирований: ${bookings.length}`;
                updateTable(bookings);
            })
            .catch(error => {
                console.error('Ошибка:', error);
                showError(error.message || 'Ошибка загрузки бронирований');
            });
    }

    function updateTable(bookings) {
        if (bookings.length === 0) {
            tableBody.innerHTML = `
                <tr class="empty-row">
                    <td colspan="7">
                        <div class="empty-state">
                            <h3>Нет бронирований</h3>
                            <p>Бронирований не найдено.</p>
                        </div>
                    </td>
                </tr>
            `;
            return;
        }

        let html = '';
        bookings.forEach(booking => {
            const statusClass = booking.status === 'Оплачено' ? 'badge-active' :
                booking.status === 'Отменено' ? 'badge-cancelled' : 'badge-pending';
            const statusText = booking.status;

            const actionsHtml = booking.status !== 'Отменено' ? `
                <td class="actions-cell">
                    <button class="btn-cancel-booking" data-id="${booking.id}" type="button">Отменить</button>
                </td>
            ` : `
                <td class="actions-cell">
                    
                </td>
            `;

            html += `
                <tr>
                    <td>${escapeHtml(booking.id)}</td>
                    <td>${escapeHtml(booking.movieTitle || '—')}</td>
                    <td>${escapeHtml(booking.userEmail || '—')}</td>
                    <td>${escapeHtml(booking.seats || '—')}</td>
                    <td>${escapeHtml(booking.seanceId || '—')}</td>
                    <td><span class="badge ${statusClass}">${statusText}</span></td>
                    ${actionsHtml}
                </tr>
            `;
        });
        tableBody.innerHTML = html;

        document.querySelectorAll('.btn-cancel-booking').forEach(btn => {
            btn.addEventListener('click', function() {
                const id = this.dataset.id;
                if (confirm('Вы уверены, что хотите отменить это бронирование?')) {
                    cancelBooking(id);
                }
            });
        });
    }

    function cancelBooking(id) {
        fetch('/admin/control-bookings-cancel?bookingId=' + id, {
            method: 'POST'
        })
            .then(response => response.json())
            .then(apiResponse => {
                if (!apiResponse.success) {
                    throw new Error(apiResponse.message || 'Ошибка отмены бронирования');
                }
                alert('✅ ' + (apiResponse.message || 'Бронирование отменено'));
                searchBookings();
            })
            .catch(error => {
                console.error('Ошибка:', error);
                alert('❌ ' + error.message);
            });
    }

    function escapeHtml(text) {
        if (text === null || text === undefined) return '—';
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    function showError(message) {
        tableBody.innerHTML = `
            <tr class="empty-row">
                <td colspan="7">
                    <div class="empty-state">
                        <h3>Ошибка</h3>
                        <p style="color: #e50914;">${escapeHtml(message)}</p>
                    </div>
                </td>
            </tr>
        `;
        resultsCount.textContent = 'Ошибка загрузки';
    }

    // Вызываем загрузку фильмов
    loadFilms();

    // Обработчик кнопки "Найти"
    findBtn.addEventListener('click', function(e) {
        e.preventDefault();
        searchBookings();
    });

    // Поиск при нажатии Enter
    document.getElementById('filmSelect').addEventListener('keydown', function(e) {
        if (e.key === 'Enter') {
            e.preventDefault();
            searchBookings();
        }
    });
});