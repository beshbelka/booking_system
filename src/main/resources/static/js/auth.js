isLoginMode = true;

// ===== Открытие =====
function openModal() {
    const modal = document.getElementById('authModal');
    if (modal) {
        modal.classList.add('show');
        document.body.style.overflow = 'hidden';
        clearErrors();
        resetForm();
    }
}

// ===== Закрытие =====
function closeModal() {
    const modal = document.getElementById('authModal');
    if (modal) {
        modal.classList.remove('show');
        document.body.style.overflow = '';
    }
    resetToLoginMode();
}

// ===== Сброс на режим входа =====
function resetToLoginMode() {
    isLoginMode = true;

    const subtitle = document.getElementById('modalSubtitle');
    const submitBtn = document.getElementById('submitBtn');
    const switchLink = document.getElementById('switchLink');
    const switchText = document.getElementById('switchText');
    const registerFields = document.getElementById('registerFields');
    const loginOptions = document.getElementById('loginOptions');

    if (subtitle) subtitle.textContent = 'Войдите, чтобы продолжить';
    if (submitBtn) submitBtn.textContent = 'Войти';
    if (switchLink) switchLink.textContent = 'Зарегистрироваться';
    if (switchText) switchText.textContent = 'Нет аккаунта?';
    if (registerFields) registerFields.style.display = 'none';
    if (loginOptions) loginOptions.style.display = 'flex';

    clearErrors();
    resetForm();
}

// ===== Переключение режима =====
function toggleMode() {
    isLoginMode = !isLoginMode;

    const subtitle = document.getElementById('modalSubtitle');
    const submitBtn = document.getElementById('submitBtn');
    const switchLink = document.getElementById('switchLink');
    const switchText = document.getElementById('switchText');
    const registerFields = document.getElementById('registerFields');
    const loginOptions = document.getElementById('loginOptions');

    if (isLoginMode) {
        subtitle.textContent = 'Войдите, чтобы продолжить';
        submitBtn.textContent = 'Войти';
        switchLink.textContent = 'Зарегистрироваться';
        switchText.textContent = 'Нет аккаунта?';
        registerFields.style.display = 'none';
        loginOptions.style.display = 'flex';
    } else {
        subtitle.textContent = 'Создайте аккаунт';
        submitBtn.textContent = 'Зарегистрироваться';
        switchLink.textContent = 'Войти';
        switchText.textContent = 'Уже есть аккаунт?';
        registerFields.style.display = 'block';
        loginOptions.style.display = 'none';
    }

    clearErrors();
    resetForm();
}

// ===== Обработка отправки (с AJAX) =====
function handleSubmit(event) {
    event.preventDefault();

    const email = document.getElementById('email');
    const password = document.getElementById('password');
    const errorDiv = document.getElementById('errorMessage');

    let errors = [];

    // Валидация
    if (!email.value.trim()) {
        errors.push('Введите email или логин');
        email.classList.add('error');
    } else {
        email.classList.remove('error');
    }

    if (!password.value) {
        errors.push('Введите пароль');
        password.classList.add('error');
    } else {
        password.classList.remove('error');
    }

    // Доп. валидация для регистрации
    if (!isLoginMode) {
        const name = document.getElementById('name');
        const birthDate = document.getElementById('birthDate');
        const confirmPassword = document.getElementById('confirmPassword');

        if (!name.value.trim() || name.value.trim().length < 2) {
            errors.push('Введите имя');
            name.classList.add('error');
        } else {
            name.classList.remove('error');
        }

        if (!birthDate.value) {
            errors.push('Выберите дату рождения');
            birthDate.classList.add('error');
        } else {
            birthDate.classList.remove('error');
        }

        if (!confirmPassword.value || password.value !== confirmPassword.value) {
            errors.push('Пароли не совпадают');
            confirmPassword.classList.add('error');
        } else {
            confirmPassword.classList.remove('error');
        }
    }

    if (errors.length > 0) {
        errorDiv.textContent = errors.join('. ');
        errorDiv.classList.add('show');
        return;
    }

    // ===== Отправка на сервер =====
    const url = isLoginMode ? '/auth/login' : '/auth/register';
    const data = isLoginMode
        ? { email: email.value.trim(), password: password.value.trim() }
        : {
            email: email.value.trim(),
            password: password.value.trim(),
            name: document.getElementById('name').value.trim(),
            birthDate: document.getElementById('birthDate').value
        };

    // Показываем загрузку
    const submitBtn = document.getElementById('submitBtn');
    const originalText = submitBtn.textContent;
    submitBtn.textContent = 'Загрузка...';
    submitBtn.disabled = true;

    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: new URLSearchParams(data)
    })
    .then(response => response.json())
    .then(result => {
        submitBtn.textContent = originalText;
        submitBtn.disabled = false;

        if (result.success) {
            // Успех
            errorDiv.classList.remove('show');
            closeModal();
            showSuccess(result.message, `Добро пожаловать, ${result.user}!`);
            // Обновляем UI
            updateUIAfterLogin(result.user);
        } else {
            // Ошибка от сервера
            errorDiv.textContent = result.message || 'Произошла ошибка';
            errorDiv.classList.add('show');
        }
    })
    .catch(error => {
        submitBtn.textContent = originalText;
        submitBtn.disabled = false;
        errorDiv.textContent = 'Ошибка соединения с сервером';
        errorDiv.classList.add('show');
        console.error('Ошибка:', error);
    });
}

// ===== Обновление UI после входа =====
function updateUIAfterLogin(username) {
    const loginLinks = document.querySelectorAll('.login-link, .open-modal-btn');
    loginLinks.forEach(link => {
        link.textContent = `👤 ${username}`;
        link.href = '#';
        link.onclick = function(e) {
            e.preventDefault();
            if (confirm('Выйти из аккаунта?')) {
                logout();
            }
        };
    });
}

// ===== Выход =====
function logout() {
    fetch('/auth/logout', {
        method: 'POST'
    })
    .then(response => response.json())
    .then(result => {
        if (result.success) {
            const loginLinks = document.querySelectorAll('.login-link, .open-modal-btn');
            loginLinks.forEach(link => {
                link.textContent = 'Вход';
                link.onclick = function(e) {
                    e.preventDefault();
                    openModal();
                };
            });
            showSuccess('Вы вышли из аккаунта');
        }
    });
}

// ===== Показ успешного сообщения =====
function showSuccess(title, message) {
    alert(title + '\n' + message);
    location.reload();
}

// ===== Очистка ошибок =====
function clearErrors() {
    const errorDiv = document.getElementById('errorMessage');
    if (errorDiv) {
        errorDiv.classList.remove('show');
        errorDiv.textContent = '';
    }
    document.querySelectorAll('.form-group input').forEach(el => {
        el.classList.remove('error');
        el.style.borderColor = '';
    });
}

// ===== Сброс формы =====
function resetForm() {
    const form = document.getElementById('authForm');
    if (form) {
        form.reset();
    }
    document.querySelectorAll('.form-group input').forEach(el => {
        el.classList.remove('error');
        el.style.borderColor = '';
    });
}

// ===== Закрытие по клику вне =====
document.addEventListener('click', function(e) {
    const modal = document.getElementById('authModal');
    if (modal && modal.classList.contains('show')) {
        if (e.target === modal) {
            closeModal();
        }
    }
});

// ===== Закрытие по Escape =====
document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') {
        closeModal();
    }
});

// ===== Проверка сессии при загрузке =====
document.addEventListener('DOMContentLoaded', function() {
    // Очистка ошибок при вводе
    const form = document.getElementById('authForm');
    if (form) {
        form.querySelectorAll('input').forEach(input => {
            input.addEventListener('input', function() {
                this.classList.remove('error');
                this.style.borderColor = '';
                const errorDiv = document.getElementById('errorMessage');
                if (errorDiv) {
                    errorDiv.classList.remove('show');
                    errorDiv.textContent = '';
                }
            });
        });
    }

    // Проверяем сессию
    fetch('/auth/check')
        .then(response => response.json())
        .then(data => {
            if (data.authenticated) {
                updateUIAfterLogin(data.user);
            }
        })
        .catch(error => {
            console.error('Ошибка проверки сессии:', error);
        });
});