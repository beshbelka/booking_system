document.getElementById('registrationForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    clearErrors();

    const formData = new FormData(this);
    const data = {
        email: formData.get('email'),
        name: formData.get('name'),
        birthDate: formData.get('birthDate'),
        password: formData.get('password'),
        confirmPassword: formData.get('confirmPassword')
    };

    // Проверка на клиенте
    const validationErrors = validateForm(data);
    if (Object.keys(validationErrors).length > 0) {
        Object.keys(validationErrors).forEach(field => {
            showError(field + 'Error', validationErrors[field]);
            const input = document.querySelector(`[name="${field}"]`);
            if (input) input.classList.add('error');
        });
        return;
    }

    try {
        const response = await fetch('/auth/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });

        // Проверяем Content-Type
        const contentType = response.headers.get('content-type');
        if (!contentType || !contentType.includes('application/json')) {
            const text = await response.text();
            console.error('Non-JSON response:', text);
            showError('emailError', 'Ошибка сервера. Попробуйте позже.');
            return;
        }

        const result = await response.json();

        if (response.ok && result.success) {
            window.location.href = '/profile';
        } else {
            // Обработка ошибок в зависимости от статуса
            if (response.status === 409) {
                // EmailTakenException - конфликт
                showError('emailError', result.message || 'Email уже используется');
            } else if (response.status === 400 && result.errors) {
                // Ошибки валидации (если есть Map<String, String> errors)
                Object.keys(result.errors).forEach(field => {
                    const errorEl = document.getElementById(field + 'Error');
                    if (errorEl) {
                        showError(field + 'Error', result.errors[field]);
                        const input = document.querySelector(`[name="${field}"]`);
                        if (input) input.classList.add('error');
                    }
                });
            } else if (response.status === 500) {
                // Внутренняя ошибка сервера
                showError('emailError', result.message || 'Внутренняя ошибка сервера');
            } else {
                // Остальные ошибки
                showError('emailError', result.message || 'Ошибка регистрации');
            }
        }
    } catch (error) {
        console.error('Error:', error);
        showError('emailError', error.message || 'Ошибка соединения с сервером');
    }
});

function validateForm(data) {
    const errors = {};
    if (!data.email) errors.email = 'Email обязателен';
    else if (!isValidEmail(data.email)) errors.email = 'Некорректный формат email';
    if (!data.name) errors.name = 'Имя обязательно';
    if (!data.birthDate) errors.birthDate = 'Дата рождения обязательна';
    else if (!isValidDate(data.birthDate)) errors.birthDate = 'Некорректная дата';
    if (!data.password) errors.password = 'Пароль обязателен';
    else if (data.password.length < 6) errors.password = 'Пароль должен быть не менее 6 символов';
    if (data.password !== data.confirmPassword) errors.confirmPassword = 'Пароли не совпадают';
    return errors;
}

function isValidEmail(email) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

function isValidDate(dateStr) {
    // Проверяем формат YYYY-MM-DD
    const regex = /^\d{4}-\d{2}-\d{2}$/;
    if (!regex.test(dateStr)) return false;

    const date = new Date(dateStr);
    const now = new Date();
    now.setHours(0, 0, 0, 0);

    const minDate = new Date('1900-01-01');

    return !isNaN(date.getTime()) && date >= minDate && date <= now;
}

function clearErrors() {
    document.querySelectorAll('.error-message').forEach(el => {
        el.textContent = '';
        el.style.color = '#e50914';
    });
    document.querySelectorAll('.form-input').forEach(el => {
        el.classList.remove('error');
    });
}

function showError(elementId, message) {
    const el = document.getElementById(elementId);
    if (el) {
        el.textContent = message;
        el.style.color = '#e50914';
    }
}