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
            alert('❌ Ошибка соединения с сервером');
        });
}