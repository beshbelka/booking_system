// Превью загруженного изображения
document.addEventListener('DOMContentLoaded', function() {
    const fileInput = document.getElementById('fileInput');
    const preview = document.getElementById('posterPreview');
    const previewImage = document.getElementById('previewImage');
    const fileName = document.getElementById('fileName');

    fileInput.addEventListener('change', function(e) {
        const file = this.files[0];
        if (file) {
            // Показываем имя файла
            fileName.textContent = '📎 ' + file.name + ' (' + (file.size / 1024).toFixed(1) + ' KB)';

            // Превью
            const reader = new FileReader();
            reader.onload = function(e) {
                previewImage.src = e.target.result;
                preview.classList.add('visible');
            };
            reader.readAsDataURL(file);
        } else {
            fileName.textContent = '';
            preview.classList.remove('visible');
            previewImage.src = '';
        }
    });

    // Автоматическое обновление превью при вводе URL
    const posterUrl = document.getElementById('posterUrl');
    posterUrl.addEventListener('input', function() {
        const url = this.value.trim();
        if (url) {
            previewImage.src = url;
            preview.classList.add('visible');
        } else {
            // Если есть загруженный файл, не убираем превью
            if (!fileInput.files.length) {
                preview.classList.remove('visible');
                previewImage.src = '';
            }
        }
    });
});
document.addEventListener('DOMContentLoaded', function() {

    // === Логика для постера ===
    const posterFileInput = document.getElementById('posterFileInput');
    const posterPreview = document.getElementById('posterPreview');
    const posterPreviewImage = document.getElementById('posterPreviewImage');
    const posterFileName = document.getElementById('posterFileName');

    posterFileInput.addEventListener('change', function(e) {
        const file = this.files[0];
        if (file) {
            // Проверка размера (макс. 5MB)
            if (file.size > 5 * 1024 * 1024) {
                alert('Файл слишком большой! Максимальный размер 5MB');
                this.value = '';
                posterFileName.textContent = '';
                posterPreview.classList.remove('visible');
                posterPreviewImage.src = '';
                return;
            }

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

    // === Логика для бэкдропа ===
    const backdropFileInput = document.getElementById('backdropFileInput');
    const backdropPreview = document.getElementById('backdropPreview');
    const backdropPreviewImage = document.getElementById('backdropPreviewImage');
    const backdropFileName = document.getElementById('backdropFileName');

    backdropFileInput.addEventListener('change', function(e) {
        const file = this.files[0];
        if (file) {
            // Проверка размера (макс. 5MB)
            if (file.size > 5 * 1024 * 1024) {
                alert('Файл слишком большой! Максимальный размер 5MB');
                this.value = '';
                backdropFileName.textContent = '';
                backdropPreview.classList.remove('visible');
                backdropPreviewImage.src = '';
                return;
            }

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

    // === Отправка формы ===
    const form = document.getElementById('addFilmForm');
    if (form) {
        form.addEventListener('submit', function(e) {
            e.preventDefault();

            // Проверка: выбран ли файл постера
            if (!posterFileInput.files.length) {
                alert('Пожалуйста, выберите файл постера');
                return;
            }

            const formData = new FormData(this);

            // Показываем индикатор загрузки (опционально)
            const submitBtn = form.querySelector('.btn-save');
            const originalText = submitBtn.textContent;
            submitBtn.textContent = 'Загрузка...';
            submitBtn.disabled = true;

            fetch('/admin/control-films-add', {
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
                    alert('Произошла ошибка при добавлении фильма: ' + error.message);
                    submitBtn.textContent = originalText;
                    submitBtn.disabled = false;
                });
        });
    }
});