document.getElementById('loginForm').addEventListener('submit', async function(e) {
    e.preventDefault();

    const formData = new FormData(this);
    const email = formData.get('email');
    const password = formData.get('password');

    try {
        const response = await fetch('/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email, password })
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

        if (response.ok && result.accessToken) {
            // Редирект на профиль
            window.location.href = '/profile';
        } else {
            // Показываем ошибку
            document.getElementById('errorMessage').style.display = 'block';
        }
    } catch (error) {
        console.error('Error:', error);
        document.getElementById('errorMessage').textContent = '❌ Ошибка соединения с сервером';
        document.getElementById('errorMessage').style.display = 'block';
    }
});