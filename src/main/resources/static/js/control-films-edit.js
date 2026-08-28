document.addEventListener('DOMContentLoaded', function() {
    // Превью для постера
    const posterFileInput = document.getElementById('posterFileInput');
    const posterPreview = document.getElementById('posterPreview');
    const posterPreviewImage = document.getElementById('posterPreviewImage');
    const posterFileName = document.getElementById('posterFileName');

    posterFileInput.addEventListener('change', function(e) {
        const file = this.files[0];
        if (file) {
            posterFileName.textContent = '📎 ' + file.name + ' (' + (file.size / 1024).toFixed(1) + ' KB)';
            const reader = new FileReader();
            reader.onload = function(e) {
                posterPreviewImage.src = e.target.result;
                posterPreview.classList.add('visible');
            };
            reader.readAsDataURL(file);
        } else {
            posterFileName.textContent = '';
            posterPreview.classList.remove('visible');
            posterPreviewImage.src = '';
        }
    });

    // Превью для бэкдропа
    const backdropFileInput = document.getElementById('backdropFileInput');
    const backdropPreview = document.getElementById('backdropPreview');
    const backdropPreviewImage = document.getElementById('backdropPreviewImage');
    const backdropFileName = document.getElementById('backdropFileName');

    backdropFileInput.addEventListener('change', function(e) {
        const file = this.files[0];
        if (file) {
            backdropFileName.textContent = '📎 ' + file.name + ' (' + (file.size / 1024).toFixed(1) + ' KB)';
            const reader = new FileReader();
            reader.onload = function(e) {
                backdropPreviewImage.src = e.target.result;
                backdropPreview.classList.add('visible');
            };
            reader.readAsDataURL(file);
        } else {
            backdropFileName.textContent = '';
            backdropPreview.classList.remove('visible');
            backdropPreviewImage.src = '';
        }
    });

    // Отправка формы через AJAX
    const form = document.getElementById('editFilmForm');
    if (form) {
        form.addEventListener('submit', function(e) {
            e.preventDefault();

            const formData = new FormData(this);
            const submitBtn = form.querySelector('.btn-save');
            const originalText = submitBtn.textContent;
            submitBtn.textContent = 'Сохранение...';
            submitBtn.disabled = true;

            fetch('/admin/control-films-edit', {
                method: 'POST',
                body: formData
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Ошибка сервера: ' + response.status);
                    }
                    return response.json();
                })
                .then(data => {
                    if (data.success) {
                        window.location.href = '/admin/control-films';
                    } else {
                        alert('Ошибка: ' + (data.message || 'Неизвестная ошибка'));
                        submitBtn.textContent = originalText;
                        submitBtn.disabled = false;
                    }
                })
                .catch(error => {
                    console.error('Ошибка:', error);
                    alert('Произошла ошибка: ' + error.message);
                    submitBtn.textContent = originalText;
                    submitBtn.disabled = false;
                });
        });
    }
});