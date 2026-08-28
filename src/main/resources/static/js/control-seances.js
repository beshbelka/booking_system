document.addEventListener('DOMContentLoaded', function() {
    const filmSelect = document.getElementById('filmSelect');
    const hallSelect = document.getElementById('hallSelect');
    const findBtn = document.getElementById('findSeancesBtn');
    const resultsCount = document.getElementById('resultsCount');
    const tableBody = document.getElementById('seancesTableBody');

    // Загрузка фильмов и количества залов
    function loadData() {
        fetch('/admin/control-seances-get-data')
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
                filmSelect.innerHTML = '<option value="">Все фильмы</option>';

                Object.keys(data).forEach(key => {
                    if (key === 'countHalls') return;
                    const option = document.createElement('option');
                    option.value = key;
                    option.textContent = data[key];
                    filmSelect.appendChild(option);
                });

                // Заполняем список залов
                hallSelect.innerHTML = '<option value="">Все залы</option>';
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
                filmSelect.innerHTML = '<option value="">Ошибка загрузки фильмов</option>';
                hallSelect.innerHTML = '<option value="">Ошибка загрузки залов</option>';
            });
    }

    // Поиск сеансов
    function searchSeances() {
        const filmId = filmSelect.value || '0';
        const hallId = hallSelect.value || '0';

        const url = `/admin/control-seances-find?movieId=${filmId}&hallId=${hallId}`;

        fetch(url)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Ошибка загрузки сеансов');
                }
                return response.json();
            })
            .then(seances => {
                resultsCount.textContent = `Найдено сеансов: ${seances.length}`;
                updateTable(seances);
            })
            .catch(error => {
                console.error('Ошибка:', error);
                showError(error.message || 'Ошибка загрузки сеансов');
            });
    }

    // Обновление таблицы
    function updateTable(seances) {
        if (seances.length === 0) {
            tableBody.innerHTML = `
            <tr class="empty-row">
                <td colspan="8">
                    <div class="empty-state">
                        <h3>Нет сеансов</h3>
                        <p>Для выбранных параметров сеансов не найдено.</p>
                    </div>
                </td>
            </tr>
        `;
            return;
        }

        let html = '';
        seances.forEach(seance => {
            const availableSeats = seance.seats - seance.bookedSeats;
            const statusText = seance.isAvailable ? 'Доступен' : 'Завершён';
            const statusClass = seance.isAvailable ? 'badge-active' : 'badge-inactive';

            // Кнопки показываются только если cancelled == false
            const actionsHtml = !seance.cancelled ? `
                <td class="actions-cell">
                    <button class="btn-edit" data-id="${seance.id}" data-time="${formatTime(seance.start_time)}" type="button">Редактировать</button>
                    <button class="btn-delete" data-id="${seance.id}" type="button">Отменить</button>
                </td>
            ` : `
                <td class="actions-cell">
                    <span style="color: #888; font-size: 13px;">Отменён</span>
                </td>
            `;

            html += `
            <tr>
                <td>${escapeHtml(seance.id)}</td>
                <td>${escapeHtml(formatTime(seance.start_time))}</td>
                <td>${escapeHtml(formatTime(seance.end_time))}</td>
                <td><span class="badge ${statusClass}">${statusText}</span></td>
                <td>${seance.seats}</td>
                <td>${seance.bookedSeats}</td>
                <td>${availableSeats}</td>
                ${actionsHtml}
            </tr>
        `;
        });
        tableBody.innerHTML = html;

        // Добавляем обработчики только для существующих кнопок
        document.querySelectorAll('.btn-edit').forEach(btn => {
            btn.addEventListener('click', function() {
                const id = this.dataset.id;
                const time = this.dataset.time;
                openEditModal(id, time);
            });
        });

        document.querySelectorAll('.btn-delete').forEach(btn => {
            btn.addEventListener('click', function() {
                const id = this.dataset.id;
                if (confirm('Вы уверены, что хотите удалить этот сеанс?')) {
                    deleteSeance(id);
                }
            });
        });
    }

    // Удаление сеанса
    function deleteSeance(id) {
        fetch('/admin/control-seances-delete?seanceId=' + id, {
            method: 'DELETE'
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Ошибка удаления сеанса');
                }
                return response.json();
            })
            .then(apiResponse => {
                if (!apiResponse.success) {
                    throw new Error(apiResponse.message || 'Ошибка удаления сеанса');
                }
                // Обновляем список после удаления
                searchSeances();
            })
            .catch(error => {
                console.error('Ошибка:', error);
                alert('Ошибка при удалении сеанса: ' + error.message);
            });
    }

    // Форматирование времени
    function formatTime(time) {
        if (!time) return '—';
        try {
            if (typeof time === 'string') {
                const parts = time.split(':');
                if (parts.length >= 2) {
                    return parts[0].padStart(2, '0') + ':' + parts[1].padStart(2, '0');
                }
                return time;
            }
            if (time.hour !== undefined && time.minute !== undefined) {
                return String(time.hour).padStart(2, '0') + ':' + String(time.minute).padStart(2, '0');
            }
            return time;
        } catch (e) {
            return '—';
        }
    }

    // Экранирование HTML
    function escapeHtml(text) {
        if (text === null || text === undefined) return '—';
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    // Показать ошибку
    function showError(message) {
        tableBody.innerHTML = `
            <tr class="empty-row">
                <td colspan="8">
                    <div class="empty-state">
                        <h3>Ошибка</h3>
                        <p style="color: #e50914;">${escapeHtml(message)}</p>
                    </div>
                </td>
            </tr>
        `;
        resultsCount.textContent = 'Ошибка загрузки';
    }

    // Загружаем данные при загрузке страницы
    loadData();

    // Обработчик кнопки "Найти"
    findBtn.addEventListener('click', function(e) {
        e.preventDefault();
        searchSeances();
    });

    // Поиск при нажатии Enter
    filmSelect.addEventListener('keydown', function(e) {
        if (e.key === 'Enter') {
            e.preventDefault();
            searchSeances();
        }
    });

    hallSelect.addEventListener('keydown', function(e) {
        if (e.key === 'Enter') {
            e.preventDefault();
            searchSeances();
        }
    });
});

// Модальное окно для редактирования
let editSeanceId = null;

// Открыть модалку редактирования
function openEditModal(seanceId, currentTime) {
    editSeanceId = seanceId;
    document.getElementById('editSeanceId').value = seanceId;
    document.getElementById('editStartTime').value = currentTime;
    document.getElementById('editModal').style.display = 'flex';
}

// Закрыть модалку
function closeEditModal() {
    document.getElementById('editModal').style.display = 'none';
    editSeanceId = null;
}

// Сохранить изменения
function saveEditSeance() {
    const seanceId = document.getElementById('editSeanceId').value;
    const newTime = document.getElementById('editStartTime').value;

    if (!newTime) {
        alert('Пожалуйста, выберите время');
        return;
    }

    fetch('/admin/control-seances-edit', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            seanceId: parseInt(seanceId),
            start_time: newTime
        })
    })
        .then(response => response.json())
        .then(apiResponse => {
            if (!apiResponse.success) {
                throw new Error(apiResponse.message || 'Ошибка редактирования');
            }
            alert('✅ ' + (apiResponse.message || 'Время сеанса обновлено'));
            closeEditModal();
        })
        .catch(error => {
            console.error('Ошибка:', error);
            alert('❌ ' + error.message);
        });
}

// Закрыть модалку при клике вне окна
window.addEventListener('click', function(e) {
    const modal = document.getElementById('editModal');
    if (e.target === modal) {
        closeEditModal();
    }
});

// Закрыть по Escape
document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') {
        closeEditModal();
    }
});