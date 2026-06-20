// Модальное окно входа
function showLoginModal() {
    const modal = document.getElementById('loginModal');
    modal.classList.add('show');
    document.body.style.overflow = 'hidden';
}

function closeLoginModal() {
    const modal = document.getElementById('loginModal');
    modal.classList.remove('show');
    document.body.style.overflow = '';
}

// Демо-обработчик формы
function handleModalLogin(event) {
    event.preventDefault();
    const email = document.getElementById('modalEmail').value;
    const password = document.getElementById('modalPassword').value;
    const errorMessage = document.getElementById('modalErrorMessage');

    if (!email || !password) {
        errorMessage.textContent = 'Заполните все поля';
        errorMessage.classList.add('show');
        return false;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        errorMessage.textContent = 'Введите корректный email';
        errorMessage.classList.add('show');
        return false;
    }

    // Демо-режим
    console.log('Вход:', { email, password });
    closeLoginModal();
    return false;
}

// Закрытие по клику вне окна (исправлено)
document.addEventListener('DOMContentLoaded', function() {
    const modal = document.getElementById('loginModal');

    if (modal) {
        // Клик по затемнению закрывает окно
        modal.addEventListener('click', function(e) {
            if (e.target === modal) {
                closeLoginModal();
            }
        });
    }

    // Очистка ошибок при вводе
    const emailInput = document.getElementById('modalEmail');
    const passwordInput = document.getElementById('modalPassword');

    if (emailInput) {
        emailInput.addEventListener('input', function() {
            document.getElementById('modalErrorMessage').classList.remove('show');
        });
    }
    if (passwordInput) {
        passwordInput.addEventListener('input', function() {
            document.getElementById('modalErrorMessage').classList.remove('show');
        });
    }
});

// Закрытие по Escape
document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') {
        closeLoginModal();
    }
});