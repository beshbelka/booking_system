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

        const contentType = response.headers.get('content-type');
        if (!contentType || !contentType.includes('application/json')) {
            const text = await response.text();
            console.error('Non-JSON response:', text);
            document.getElementById('emailError').textContent = 'Ошибка сервера. Попробуйте позже.';
            document.getElementById('emailError').style.display = 'block';
            return;
        }

        const result = await response.json();

        const urlParams = new URLSearchParams(window.location.search);
        const seats = urlParams.get('seats');

        if (response.ok && result.success) {
            if (seats === 'true') {
                const seanceId = urlParams.get('seanceId');
                window.location.href = "/seats?&seanceId=" + seanceId;
            } else {
                window.location.href = '/profile';
            }
        } else {
            // Показываем message под email
            const msg = document.getElementById('emailError');
            msg.textContent = result.message || 'Ошибка входа';
            msg.style.display = 'block';
        }
    } catch (error) {
        console.error('Error:', error);
        const msg = document.getElementById('emailError');
        msg.textContent = '❌ Ошибка соединения с сервером';
        msg.style.display = 'block';
    }
});