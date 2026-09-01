
// Элементы
const card = document.getElementById('paymentCard');
const icon = document.getElementById('paymentIcon');
const svg = document.getElementById('paymentSvg');
const title = document.getElementById('paymentTitle');
const message = document.getElementById('paymentMessage');
const btn = document.getElementById('paymentBtn');

// Запускаем процесс оплаты
startPayment();

function startPayment() {
    // Загрузка (3 секунды)
    setLoadingState();

    setTimeout(() => {
        // Имитация запроса к серверу
        fetchPaymentStatus();
    }, 3000);
}

function setLoadingState() {
    // Анимация загрузки
    svg.innerHTML = `
            <circle cx="50" cy="50" r="45" fill="none" stroke="#e50914" stroke-width="4">
                <animate attributeName="stroke-dasharray" from="0 283" to="283 283" dur="1.5s" repeatCount="indefinite"/>
                <animate attributeName="stroke-dashoffset" from="0" to="-283" dur="1.5s" repeatCount="indefinite"/>
            </circle>
        `;
    card.className = 'payment-card loading';
    title.textContent = 'Обработка платежа...';
    message.textContent = 'Пожалуйста, подождите, идёт оплата';
    btn.style.display = 'none';
}

function fetchPaymentStatus() {
    const delay = 500 + Math.random() * 1000;

    setTimeout(async () => {
        const urlParams = new URLSearchParams(window.location.search);
        const bookId = urlParams.get('bookId');

        if (!bookId) {
            console.error('bookId не найден в URL');
            showError();
            return;
        }

        try {
            const response = await fetch('/payment', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                credentials: 'include',
                body: JSON.stringify({ bookId: bookId })
            });

            // Проверяем, что пришёл JSON
            const contentType = response.headers.get('content-type');
            if (!contentType || !contentType.includes('application/json')) {
                console.error('Ответ не JSON:', await response.text());
                showError();
                return;
            }

            const result = await response.json();

            if (response.ok && result.success) {
                showSuccess();
            } else {
                if (result.message === "Бронь уже оплачена") {
                    alreadyPayed();
                } else {
                    showError();
                }
            }
        } catch (error) {
            showError();
        }
    }, delay);
}

function showSuccess() {
    svg.innerHTML = `
            <circle cx="50" cy="50" r="45" fill="none" stroke="#4CAF50" stroke-width="4"/>
            <path d="M30 50 L45 65 L70 35" fill="none" stroke="#4CAF50" stroke-width="6" stroke-linecap="round" stroke-linejoin="round">
                <animate attributeName="stroke-dasharray" from="0 100" to="100 0" dur="0.5s" fill="freeze"/>
            </path>
        `;
    card.className = 'payment-card success';
    title.textContent = 'Оплата прошла успешно!';
    message.textContent = 'Билеты забронированы. Приятного просмотра!';
    btn.style.display = 'block';
    btn.textContent = 'Перейти к билетам';
    btn.onclick = () => window.location.href = '/profile';
}

function alreadyPayed() {
    svg.innerHTML = `
            <circle cx="50" cy="50" r="45" fill="none" stroke="#4CAF50" stroke-width="4"/>
            <path d="M30 50 L45 65 L70 35" fill="none" stroke="#4CAF50" stroke-width="6" stroke-linecap="round" stroke-linejoin="round">
                <animate attributeName="stroke-dasharray" from="0 100" to="100 0" dur="0.5s" fill="freeze"/>
            </path>
        `;
    card.className = 'payment-card success';
    title.textContent = 'Бронь уже оплачена!';
    message.textContent = 'Билеты забронированы. Приятного просмотра!';
    btn.style.display = 'block';
    btn.textContent = 'Перейти к билетам';
    btn.onclick = () => window.location.href = '/profile';
}

function showError() {
    svg.innerHTML = `
            <circle cx="50" cy="50" r="45" fill="none" stroke="#e50914" stroke-width="4"/>
            <line x1="30" y1="30" x2="70" y2="70" stroke="#e50914" stroke-width="6" stroke-linecap="round">
                <animate attributeName="stroke-dasharray" from="0 60" to="60 0" dur="0.3s" fill="freeze"/>
            </line>
            <line x1="70" y1="30" x2="30" y2="70" stroke="#e50914" stroke-width="6" stroke-linecap="round">
                <animate attributeName="stroke-dasharray" from="0 60" to="60 0" dur="0.3s" fill="freeze"/>
            </line>
        `;
    card.className = 'payment-card error';
    title.textContent = 'Оплата не прошла';
    message.textContent = 'Попробуйте позже или используйте другую карту.';
    btn.style.display = 'block';
    btn.textContent = 'Попробовать снова';
    btn.onclick = () => location.reload();
}

function resetPayment() {
    // Кнопка "На главную"
    window.location.href = '/';
}