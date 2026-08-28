document.addEventListener('DOMContentLoaded', function() {
    // Находим все кнопки удаления
    const deleteButtons = document.querySelectorAll('.btn-delete');

    deleteButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();

            // Получаем данные из data-атрибутов
            const userId = this.getAttribute('data-id');
            const userName = this.getAttribute('data-title');

            // Показываем подтверждение
            const confirmMessage = `Вы уверены, что хотите удалить пользователя "${userName}"?`;
            if (!confirm(confirmMessage)) {
                return;
            }

            // Отправляем запрос на сервер
            fetch('/admin/control-users-delete', {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ id: userId })
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        alert('Пользователь успешно удален!');
                        location.reload();
                    } else {
                        alert('Ошибка: ' + (data.message || 'Неизвестная ошибка'));
                    }
                })
                .catch(error => {
                    console.error('Ошибка:', error);
                    alert('Произошла ошибка при удалении пользователя.');
                });
        });
    });
});