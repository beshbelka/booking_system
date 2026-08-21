
function openPasswordModal() {
    document.getElementById('passwordModal').classList.add('active');
    document.getElementById('passwordForm').reset();
    document.getElementById('modalError').style.display = 'none';
    document.getElementById('modalSuccess').style.display = 'none';
}

function closePasswordModal() {
    document.getElementById('passwordModal').classList.remove('active');
}

// Закрыть по клику вне модалки
document.getElementById('passwordModal').addEventListener('click', function(e) {
    if (e.target === this) closePasswordModal();
});

async function changePassword(event) {
    event.preventDefault();

    const oldPass = document.getElementById('oldPassword').value;
    const newPass = document.getElementById('newPassword').value;
    const confirmPass = document.getElementById('confirmPassword').value;
    const errorEl = document.getElementById('modalError');
    const successEl = document.getElementById('modalSuccess');

    errorEl.style.display = 'none';
    successEl.style.display = 'none';

    if (newPass.length < 6) {
        errorEl.textContent = 'Пароль должен быть минимум 6 символов';
        errorEl.style.display = 'block';
        return;
    }

    if (newPass !== confirmPass) {
        errorEl.textContent = 'Пароли не совпадают';
        errorEl.style.display = 'block';
        return;
    }

    try {
        const response = await fetch('/profile/password', {
            method: 'PUT',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({oldPassword: oldPass, newPassword: newPass})
        });

        const contentType = response.headers.get('content-type');
        if (!contentType || !contentType.includes('application/json')) {
            const text = await response.text();
            console.error('Non-JSON response:', text);
            document.getElementById('emailError').textContent = 'Ошибка сервера. Попробуйте позже.';
            document.getElementById('emailError').style.display = 'block';
            return;
        };

        const result = await response.json();

        if (response.ok && result.success) {
            window.location.href = '/profile';
        } else {
            errorEl.textContent = result.message || 'Не удалось сменить пароль';
            errorEl.style.display = 'block';
        }
    } catch (error) {
        errorEl.textContent = 'Ошибка соединения. Проверьте интернет-соединение.';
        errorEl.style.display = 'block';
        console.error('Change password error:', error);
    }
}