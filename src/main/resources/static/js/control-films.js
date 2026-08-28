document.addEventListener('DOMContentLoaded', function() {
    // === УДАЛЕНИЕ ===
    const deleteButtons = document.querySelectorAll('.btn-delete');

    deleteButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();

            const movieId = this.getAttribute('data-id');
            const movieTitle = this.getAttribute('data-title');

            if (!confirm(`Вы уверены, что хотите удалить фильм "${movieTitle}"?`)) {
                return;
            }

            fetch('/admin/control-films-delete', {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ id: movieId })
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        alert('Фильм успешно удален!');
                        location.reload();
                    } else {
                        alert('Ошибка: ' + (data.message || 'Неизвестная ошибка'));
                    }
                })
                .catch(error => {
                    console.error('Ошибка:', error);
                    alert('Произошла ошибка при удалении фильма.');
                });
        });
    });

    // === РЕДАКТИРОВАНИЕ ===
    const editButtons = document.querySelectorAll('.btn-edit');

    editButtons.forEach(button => {
        button.addEventListener('click', function (e) {
            e.preventDefault();
            const movieId = this.getAttribute('data-id');
            window.location.href = '/admin/control-films-edit?movieId=' + movieId;
        });
    });
});