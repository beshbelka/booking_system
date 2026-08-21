
let isRefreshing = false;
let failedQueue = [];

const processQueue = (error, token = null) => {
    failedQueue.forEach(prom => {
        if (error) {
            prom.reject(error);
        } else {
            prom.resolve(token);
        }
    });
    failedQueue = [];
};

async function refreshToken() {
    try {
        const response = await fetch('/auth/refresh', {
            method: 'POST',
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error('Refresh failed');
        }
        return true;
    } catch (error) {
        window.location.href = '/auth/login';
        throw error;
    }
}

async function fetchWithAuth(url, options = {}) {
    options.credentials = 'include';

    let response = await fetch(url, options);

    // Если 401 - пробуем обновить токен
    if (response.status === 401) {
        if (!isRefreshing) {
            isRefreshing = true;

            try {
                await refreshToken();
                isRefreshing = false;
                processQueue(null);
            } catch (error) {
                processQueue(error);
                throw error;
            }
        }

        // Ждем пока обновится токен
        await new Promise((resolve, reject) => {
            failedQueue.push({ resolve, reject });
        });

        // Повторяем запрос с новым токеном
        response = await fetch(url, options);
    }

    return response;
}