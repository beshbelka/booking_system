function payTicket() {
    const row = document.getElementById('rowSelect').value;
    const number = document.getElementById('seatSelect').value;

    if (!row || !number) {
        alert('Пожалуйста, выберите ряд и место');
        return;
    }

    const urlParams = new URLSearchParams(window.location.search);
    const seanceId = urlParams.get('seanceId');
    createBooking(seanceId, row, number);

}

function cancelBooking() {
    if (confirm('Вы уверены, что хотите отменить бронирование?')) {
        window.location.href = '/';
    }
}

async function createBooking(seanceId, row, number) {
    try {
        const response = await fetch('/book', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'include',  // ← отправлять куки
            body: JSON.stringify({
                seanceId: seanceId,
                row: row,
                number: number
            })
        });

        // Если вернулся 401 (неавторизован) — редирект на логин
        if (response.status === 401 || response.status === 403) {
            alert('Сессия истекла. Войдите заново.');
            window.location.href = '/auth/login?seats=true&seanceId=' + seanceId;
            return;
        }

        const result = await response.json();

        if (response.ok && result.success) {
            const bookId = result.data.bookId;
            window.location.href = '/payment?bookId=' + bookId;
        } else {
            alert(result.message || '❌ Ошибка при бронировании');
        }

    } catch (error) {
        console.error('Ошибка:', error);
        alert('⚠️ Ошибка соединения');
    }
}