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
            alert('Регистрация успешна! Перенаправление...');
            window.location.href = '/auth/login';
        } else {
            if (result.errors) {
                Object.keys(result.errors).forEach(field => {
                    const errorEl = document.getElementById(field + 'Error');
                    if (errorEl) {
                        showError(field + 'Error', result.errors[field]);
                        const input = document.querySelector(`[name="${field}"]`);
                        if (input) input.classList.add('error');
                    }
                });
            } else if (result.error) {
                showError('emailError', result.error);
            } else {
                showError('emailError', 'Неизвестная ошибка');
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
    if (!data.name) errors.name = 'Имя обязательно';
    if (!data.birthDate) errors.birthDate = 'Дата рождения обязательна';
    if (!data.password) errors.password = 'Пароль обязателен';
    if (data.password !== data.confirmPassword) errors.confirmPassword = 'Пароли не совпадают';
    return errors;
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