// Загальна функція для оновлення доступних годин для обох сценаріїв
function updateAvailableTimes(dateInputId, timeSelectId, serviceId = null, appointmentId = null) {
    const appointmentDate = document.getElementById(dateInputId).value;
    const timeSelect = document.getElementById(timeSelectId);
    timeSelect.innerHTML = '<option value="">Виберіть час</option>'; // Очистити попередні значення

    if (appointmentDate) {
        const selectedDate = new Date(appointmentDate);
        const dayOfWeek = selectedDate.getDay();
        const today = new Date();
        const currentTime = today.getHours() * 60 + today.getMinutes(); // Поточний час у хвилинах (для порівняння)

        // Забороняємо вибір попередніх днів
        if (selectedDate < today) {
            alert('Вибір дати обмежений поточним днем або в майбутньому.');
            return;
        }

        // Завантаження записів клієнта для перевірки на доступність
        fetch('/appointments/client-appointments')
            .then(response => response.json())
            .then(appointments => {
                const takenTimes = appointments
                    .filter(appointment => {
                        const appointmentDate = new Date(appointment.appointmentTime);
                        return appointmentDate.toLocaleDateString() === selectedDate.toLocaleDateString() &&
                            (appointmentId === null || appointment.id !== appointmentId) && // Перевірка, чи не це поточний запис
                            (serviceId === null || appointment.serviceEntity.id !== serviceId); // Перевірка на іншу послугу
                    })
                    .map(appointment => new Date(appointment.appointmentTime).toLocaleTimeString('en-GB').slice(0, 5)); // Заброньовані години на цей день

                const availableHours = ["09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00"];

                availableHours.forEach(hour => {
                    const option = document.createElement('option');
                    option.value = hour;
                    option.textContent = hour;

                    const [hourValue, minuteValue] = hour.split(":").map(Number);
                    const selectedTimeInMinutes = hourValue * 60 + minuteValue;

                    // Якщо день вибраний як поточний, перевіряємо, чи не пройшов час
                    if (selectedDate.toLocaleDateString() === today.toLocaleDateString() && selectedTimeInMinutes < currentTime) {
                        option.disabled = true; // Відключити години, що вже пройшли на поточний день
                    }

                    // Якщо година вже заброньована, не даємо її вибрати
                    if (takenTimes.includes(hour)) {
                        option.disabled = true;
                    }

                    timeSelect.appendChild(option);
                });
            })
            .catch(error => console.error('Error loading appointments:', error));
    }
}

// Використовуємо для первинного запису
document.getElementById('appointmentDate').addEventListener('change', () => {
    updateAvailableTimes('appointmentDate', 'appointmentTime');
});

// Використовуємо для перезапису
document.getElementById('rescheduleAppointmentDate').addEventListener('change', () => {
    updateAvailableTimes('rescheduleAppointmentDate', 'newAppointmentTime');
});

// Логіка для відкриття модального вікна
// Відкрити модальне вікно
function openModal(modalId, appointmentId = null) {
    if (appointmentId !== null) {
        document.getElementById('rescheduleAppointmentId').value = appointmentId;
    }
    document.getElementById(modalId).style.display = 'flex';
}

// Закрити модальне вікно
function closeModal(modalId) {
    document.getElementById(modalId).style.display = 'none';
}

// Завантаження всіх послуг для запису клієнта
fetch('/services/all-services')
    .then(response => response.json())
    .then(services => {
        const serviceSelect = document.getElementById('serviceId');
        services.forEach(service => {
            const option = document.createElement('option');
            option.value = service.id;
            option.textContent = service.serviceName;
            serviceSelect.appendChild(option);
        });
    })
    .catch(error => console.error('Error:', error));

// Завантаження записів клієнта
fetch('/appointments/client-appointments')
    .then(response => response.json())
    .then(appointments => {
        const appointmentsDiv = document.getElementById('appointments');
        if (appointments.length === 0) {
            appointmentsDiv.innerHTML = 'У вас немає записів.';
        } else {
            appointments.forEach(appointment => {
                const appointmentElement = document.createElement('div');
                appointmentElement.id = `appointment-${appointment.id}`; // Унікальний ID для кожного запису
                appointmentElement.innerHTML = `
                    <strong>Послуга:</strong> ${appointment.serviceEntity.serviceName}<br>
                    <strong>Час запису:</strong> ${new Date(appointment.appointmentTime).toLocaleString()}<br>
                    <button onclick="openModal('rescheduleModal', ${appointment.id})" class="btn-primary">Перезапис</button>
                    <button onclick="deleteAppointment(${appointment.id})" class="btn-secondary">Видалити</button>
                    <hr>
                `;
                appointmentsDiv.appendChild(appointmentElement);
            });
        }
    })
    .catch(error => {
        console.error('Помилка при завантаженні записів:', error);
    });

// Логіка для бронювання послуги
document.getElementById('bookAppointmentForm').addEventListener('submit', function(event) {
    event.preventDefault();

    const serviceId = document.getElementById('serviceId').value;
    const appointmentDate = document.getElementById('appointmentDate').value;
    const appointmentTime = document.getElementById('appointmentTime').value;
    const clientId = document.getElementById('clientId').value;

    if (!clientId) {
        alert('Не вдалося отримати ваш ідентифікатор клієнта. Спробуйте ще раз.');
        return;
    }

    if (!appointmentDate || !appointmentTime) {
        alert('Будь ласка, виберіть дату і час для запису.');
        return;
    }

    const appointmentDateTime = `${appointmentDate}T${appointmentTime}:00`; // Формуємо повний час запису

    fetch('/appointments/book', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            serviceId: serviceId,
            clientId: clientId,
            appointmentTime: appointmentDateTime
        })
    })
        .then(response => {
            if (response.ok) {
                alert('Запис успішно створено!');
                location.reload();
            } else {
                response.text().then(text => alert('Не вдалося створити запис: ' + text));
            }
        })
        .catch(error => console.error('Error:', error));
});

// Підтвердити перезапис
document.getElementById('rescheduleForm').addEventListener('submit', function (event) {
    event.preventDefault();
    const appointmentId = document.getElementById('rescheduleAppointmentId').value;
    const newTime = document.getElementById('newAppointmentTime').value;
    const newDate = document.getElementById('rescheduleAppointmentDate').value;

    if (newTime && newDate) {
        const newAppointmentDateTime = `${newDate}T${newTime}:00`;

        fetch(`/appointments/update/${appointmentId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ newAppointmentTime: newAppointmentDateTime })
        })
            .then(response => {
                if (response.ok) {
                    alert('Перезапис успішно виконано!');
                    location.reload();
                } else {
                    alert("Не вдалося перезаписати запис. Спробуйте ще раз.");
                }
            });
    } else {
        alert('Будь ласка, виберіть нову дату та час для перезапису.');
    }
});

// Видалення запису
function deleteAppointment(appointmentId) {
    fetch(`/appointments/delete/${appointmentId}`, {
        method: 'DELETE'
    })
        .then(response => {
            if (response.ok) {
                // Знайдемо елемент запису, який потрібно видалити, і видалимо його з DOM
                const appointmentElement = document.getElementById(`appointment-${appointmentId}`);
                if (appointmentElement) {
                    appointmentElement.remove();
                }
                alert('Запис успішно видалено!');
            } else {
                // Якщо сервер повертає помилку, вивести повідомлення
                response.text().then(text => alert('Не вдалося видалити запис: ' + text));
            }
        })
        .catch(error => {
            console.error('Помилка при видаленні запису:', error);
            alert("Не вдалося видалити запис. Спробуйте ще раз.");
        });
}