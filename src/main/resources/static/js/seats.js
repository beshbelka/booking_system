let selectedSeats = []; // Массив выбранных мест [{row, number}, ...]
let occupiedSeats = new Set();
let seanceId = null;

document.addEventListener('DOMContentLoaded', function() {
    const urlParams = new URLSearchParams(window.location.search);
    seanceId = urlParams.get('seanceId');

    if (!seanceId) {
        alert('Ошибка: не указан сеанс');
        return;
    }

    loadHallInfo(seanceId);
});

async function loadHallInfo(seanceId) {
    try {
        const hallResponse = await fetch(`/hall/info?seanceId=${seanceId}`);
        if (!hallResponse.ok) {
            throw new Error('Ошибка загрузки информации о зале');
        }
        const hallInfo = await hallResponse.json();

        const occupiedResponse = await fetch(`/seat/occupied?seanceId=${seanceId}`);
        if (!occupiedResponse.ok) {
            throw new Error('Ошибка загрузки занятых мест');
        }
        const result = await occupiedResponse.json();

        const occupiedData = result.data || {};

        occupiedSeats = new Set();
        for (const value of Object.values(occupiedData)) {
            occupiedSeats.add(value);
        }

        generateHallScheme(
            hallInfo.data.rows,
            hallInfo.data.seatsPerRow,
            occupiedSeats
        );
    } catch (error) {
        alert('Ошибка загрузки данных о зале. Попробуйте обновить страницу.');
        generateHallScheme(10, 10, new Set());
    }
}

function generateHallScheme(rows, seatsPerRow, occupied) {
    const scheme = document.getElementById('hallScheme');
    scheme.innerHTML = '';

    const screen = document.createElement('div');
    screen.className = 'screen';
    screen.textContent = 'ЭКРАН';
    scheme.appendChild(screen);

    for (let row = 1; row <= rows; row++) {
        const rowDiv = document.createElement('div');
        rowDiv.className = 'seat-row';

        const rowLabel = document.createElement('span');
        rowLabel.className = 'row-label';
        rowLabel.textContent = row;
        rowDiv.appendChild(rowLabel);

        // Определяем, VIP ли ряд
        const isVip = isVipRow(row);

        for (let seat = 1; seat <= seatsPerRow; seat++) {
            const seatBtn = document.createElement('button');
            seatBtn.className = 'seat-btn available';
            seatBtn.dataset.row = row;
            seatBtn.dataset.seat = seat;
            seatBtn.textContent = seat;
            seatBtn.type = 'button';

            // Если VIP - добавляем класс и иконку
            if (isVip) {
                seatBtn.classList.add('vip');
                seatBtn.innerHTML = `⭐ ${seat}`; // Или просто оставить номер
                seatBtn.title = 'VIP место';
            }

            const key = `${row}-${seat}`;
            if (occupied.has(key)) {
                seatBtn.classList.add('occupied');
                seatBtn.disabled = true;
                seatBtn.title = isVip ? 'VIP место занято' : 'Место занято';
            }

            seatBtn.addEventListener('click', function() {
                toggleSeat(row, seat);
            });

            rowDiv.appendChild(seatBtn);
        }

        scheme.appendChild(rowDiv);
    }
}

function toggleSeat(row, seat) {
    const seatBtn = document.querySelector(`.seat-btn[data-row="${row}"][data-seat="${seat}"]`);

    if (!seatBtn || seatBtn.classList.contains('occupied')) {
        return;
    }

    // Проверяем, выбрано ли уже это место
    const index = selectedSeats.findIndex(s => s.row === row && s.number === seat);

    if (index === -1) {
        // Место не выбрано - добавляем
        seatBtn.classList.add('selected');
        selectedSeats.push({ row, number: seat });
    } else {
        // Место уже выбрано - удаляем
        seatBtn.classList.remove('selected');
        selectedSeats.splice(index, 1);
    }

    updateSelectedSeatsInfo();
    updatePayButton();
}

function updateSelectedSeatsInfo() {
    const display = document.getElementById('selectedSeatDisplay');
    const info = document.getElementById('selectedSeatInfo');

    if (selectedSeats.length === 0) {
        display.textContent = 'Места не выбраны';
        info.style.borderColor = '#2a2a2a';
    } else {
        const seatsStr = selectedSeats
            .map(s => `${s.row}-${s.number}`)
            .join(', ');
        display.textContent = `Выбрано мест: ${selectedSeats.length} (${seatsStr})`;
        info.style.borderColor = '#e50914';
    }
}

function updatePayButton() {
    const payButton = document.getElementById('payButton');
    payButton.disabled = selectedSeats.length === 0;

    if (selectedSeats.length === 0) {
        payButton.textContent = 'Выберите места';
    } else {
        payButton.textContent = `Продолжить (${selectedSeats.length} мест)`;
    }
}

function payTicket() {
    if (selectedSeats.length === 0) {
        alert('Пожалуйста, выберите места на схеме зала');
        return;
    }

    if (!seanceId) {
        alert('Ошибка: сеанс не найден');
        return;
    }

    createBooking(seanceId, selectedSeats);
}

function cancelBooking() {
    if (selectedSeats.length === 0) {
        window.location.href = '/';
        return;
    }

    if (confirm('Вы уверены, что хотите отменить выбор?')) {
        window.location.href = '/';
    }
}

async function createBooking(seanceId, seats) {
    try {
        const payButton = document.getElementById('payButton');
        payButton.disabled = true;
        payButton.textContent = 'Обработка...';

        // Формируем запрос с массивом мест
        const bookRequest = {
            seanceId: parseInt(seanceId),
            seats: seats.map(s => ({
                row: parseInt(s.row),
                number: parseInt(s.number)
            }))
        };

        console.log('Отправка запроса:', bookRequest);

        const response = await fetch('/book', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'include',
            body: JSON.stringify(bookRequest)
        });

        const result = await response.json();
        console.log('Ответ сервера:', result);

        if (response.ok && result.success) {
            const bookId = result.data.bookId;
            window.location.href = '/payment?bookId=' + bookId;
        } else {
            payButton.disabled = false;
            payButton.textContent = `Продолжить (${selectedSeats.length} мест)`;
            if (result.code === 401) {
                window.location.href = '/auth/login?seats=true&seanceId=' + seanceId;
            }
        }

    } catch (error) {
        alert('Произошла ошибка');
        const payButton = document.getElementById('payButton');
        payButton.disabled = false;
        payButton.textContent = `Продолжить (${selectedSeats.length} мест)`;
    }
}

function clearAllSeats() {
    if (selectedSeats.length === 0) return;

    if (confirm('Отменить выбор всех мест?')) {
        // Удаляем класс selected со всех кнопок
        document.querySelectorAll('.seat-btn.selected').forEach(btn => {
            btn.classList.remove('selected');
        });
        selectedSeats = [];
        updateSelectedSeatsInfo();
        updatePayButton();
    }
}

function cancelSelection() {
    if (selectedSeats.length === 0) {
        window.location.href = '/';
        return;
    }

    if (confirm('Отменить выбор всех мест?')) {
        document.querySelectorAll('.seat-btn.selected').forEach(btn => {
            btn.classList.remove('selected');
        });
        selectedSeats = [];
        updateSelectedSeatsInfo();
        updatePayButton();
    }
}

function isVipRow(row) {
    return row >= 3 && row <= 5; // 3, 4, 5 ряды - VIP
}