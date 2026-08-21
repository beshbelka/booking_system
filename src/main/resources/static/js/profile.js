import { fetchWithAuth } from './fetchWithAuth.js';

let isEditMode = false;
let originalValues = {};

// Включение/выключение режима редактирования
function toggleEditMode() {
    isEditMode = !isEditMode;
    const btn = document.getElementById('toggleEditBtn');
    const profileActions = document.getElementById('profileActions');
    const editActions = document.getElementById('editActions');
    const displayFields = document.querySelectorAll('.display-mode');
    const editFields = document.querySelectorAll('.edit-mode');

    if (isEditMode) {
        // Включаем режим редактирования
        btn.textContent = '✕ Отменить';
        btn.classList.add('btn-cancel-mode');

        // Скрываем кнопки действий
        profileActions.style.display = 'none';
        // Показываем кнопки редактирования
        editActions.style.display = 'flex';

        // Сохраняем оригинальные значения
        document.querySelectorAll('.editable-field').forEach(field => {
            const display = field.querySelector('.display-mode');
            const input = field.querySelector('.edit-input');
            if (display && input) {
                originalValues[input.id] = input.value;
            }
        });

        // Показываем поля ввода, скрываем отображение
        displayFields.forEach(el => el.style.display = 'none');
        editFields.forEach(el => el.style.display = 'block');

    } else {
        // Выключаем режим редактирования
        btn.textContent = '✎ Редактировать';
        btn.classList.remove('btn-cancel-mode');

        // Показываем кнопки действий
        profileActions.style.display = 'flex';
        // Скрываем кнопки редактирования
        editActions.style.display = 'none';

        // Восстанавливаем оригинальные значения
        document.querySelectorAll('.editable-field').forEach(field => {
            const input = field.querySelector('.edit-input');
            if (input && originalValues[input.id]) {
                input.value = originalValues[input.id];
            }
        });

        // Показываем отображение, скрываем поля ввода
        displayFields.forEach(el => el.style.display = 'inline');
        editFields.forEach(el => el.style.display = 'none');
    }
}

// Отмена всех изменений
function cancelAllFields() {
    if (isEditMode) {
        // Восстанавливаем оригинальные значения
        document.querySelectorAll('.editable-field').forEach(field => {
            const input = field.querySelector('.edit-input');
            if (input && originalValues[input.id]) {
                input.value = originalValues[input.id];
            }
        });

        // Выходим из режима редактирования
        toggleEditMode();
    }
}

// Сохранение всех полей
async function saveAllFields() {
    if (!isEditMode) return;

    // Собираем данные
    const nameInput = document.getElementById('userNameInput');
    const birthDateInput = document.getElementById('userBirthDateInput');

    const name = nameInput.value.trim();
    const birthDate = birthDateInput.value;

    if (!name) {
        showNotification('Имя не может быть пустым', 'error');
        return;
    }

    if (!birthDate) {
        showNotification('Дата рождения не может быть пустой', 'error');
        return;
    }

    // Проверка имени (минимум 2 символа)
    if (name.length < 2) {
        showNotification('Имя должно содержать минимум 2 символа', 'error');
        return;
    }

    // Проверка даты рождения
    const birthDateObj = new Date(birthDate);
    const today = new Date();
    const minDate = new Date('1900-01-01');

    if (birthDateObj > today) {
        showNotification('Дата рождения не может быть в будущем', 'error');
        return;
    }

    if (birthDateObj < minDate) {
        showNotification('Некорректная дата рождения', 'error');
        return;
    }

    try {
        const updateData = {
            name: name,
            birthDate: birthDate
        };

        // Показываем индикатор загрузки
        const saveBtn = document.querySelector('.btn-save-all');
        const originalText = saveBtn.textContent;
        saveBtn.textContent = 'Сохранение...';
        saveBtn.disabled = true;

        const response = await fetchWithAuth('/profile/edit', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'same-origin',
            body: JSON.stringify(updateData)
        });

        const result = await response.json();

        // Восстанавливаем кнопку
        saveBtn.textContent = originalText;
        saveBtn.disabled = false;

        if (result.success) {
            // Обновляем отображение
            document.getElementById('userNameDisplay').textContent = name;
            document.getElementById('userBirthDateDisplay').textContent = formatDate(birthDate);

            // Обновляем оригинальные значения
            originalValues['userNameInput'] = name;
            originalValues['userBirthDateInput'] = birthDate;

            // Выходим из режима редактирования
            toggleEditMode();

        } else {
            showNotification(result.message || 'Ошибка при обновлении данных', 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showNotification('Ошибка соединения с сервером', 'error');

        // Восстанавливаем кнопку
        const saveBtn = document.querySelector('.btn-save-all');
        saveBtn.textContent = 'Сохранить изменения';
        saveBtn.disabled = false;
    }
}

function logout() {

    fetch('/auth/logout', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        credentials: 'same-origin'
    })
        .then(response => {
            return response.json();
        })
        .then(data => {
            if (data.success === true) {
                window.location.href = '/';
            } else {
                alert('Ошибка: ' + (data.error || 'Неизвестная ошибка'));
            }
        })
        .catch(error => {
            console.error('Ошибка:', error);
            alert('Ошибка соединения с сервером');
        });
}

function formatDate(dateString) {
    if (!dateString) return 'Дата не указана';
    try {
        const date = new Date(dateString);
        return date.toLocaleDateString('ru-RU', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
        });
    } catch {
        return dateString;
    }
}

document.addEventListener('DOMContentLoaded', function() {

    // Обработчик для Enter в полях ввода
    document.querySelectorAll('.edit-input').forEach(input => {
        input.addEventListener('keydown', function(e) {
            if (e.key === 'Enter') {
                saveAllFields();
            }
            if (e.key === 'Escape') {
                cancelAllFields();
            }
        });
    });
});

function showNotification(message, type = 'info') {
    const notification = document.getElementById('notification');
    notification.textContent = message;
    notification.className = `notification ${type}`;
    notification.style.display = 'block';

    // Автоматически скрываем через 4 секунды
    clearTimeout(window.notificationTimeout);
    window.notificationTimeout = setTimeout(() => {
        notification.style.display = 'none';
    }, 4000);
}

function deleteAccount() {
    fetchWithAuth('/profile/deleteAccount', {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        },
        credentials: 'same-origin'
    })
        .then(response => {
            return response.json();
        })
        .then(data => {
            if (data.success === true) {
                window.location.href = '/';
            } else {
                alert('Ошибка: ' + (data.error || 'Неизвестная ошибка'));
            }
        })
        .catch(error => {
            console.error('Ошибка:', error);
            alert('Ошибка соединения с сервером');
        });
}

function cancelBooking(button) {
    const bookId = button.getAttribute('th:data-book-id') || button.dataset.bookId;
    if (!bookId) {
        showNotification('Ошибка: ID бронирования не найден', 'error');
        return;
    }

    fetchWithAuth('/profile/deleteBooking', {
        method: 'DELETE',
        headers: {
            'Content-Type' : 'application/json'
        },
        credentials: 'include',
        body: JSON.stringify({ bookId : bookId})
    })
        .then(response => response.json())
        .then(result => {
            if (result.success) {
                location.reload()
            } else {
                showNotification(result.message || 'Ошибка отмены', 'error');
            }
        })
        .catch(() => showNotification('Ошибка соединения', 'error'));
}