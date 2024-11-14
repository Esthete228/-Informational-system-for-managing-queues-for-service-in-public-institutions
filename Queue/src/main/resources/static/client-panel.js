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
        const serviceSelect = document.getElementById('serviceId'); // Вибираємо правильний select
        services.forEach(service => {
            const option = document.createElement('option');
            option.value = service.id;
            option.textContent = service.serviceName; // Відображаємо назву послуги
            serviceSelect.appendChild(option); // Додаємо опцію у select
        });
    })
    .catch(error => console.error('Error:', error));

// Логіка для бронювання послуги
document.getElementById('bookAppointmentForm').addEventListener('submit', function(event) {
    event.preventDefault();

    const serviceId = document.getElementById('serviceId').value;
    const appointmentTime = document.getElementById('appointmentTime').value;
    const clientId = document.getElementById('clientId').value;

    if (!clientId) {
        alert('Не вдалося отримати ваш ідентифікатор клієнта. Спробуйте ще раз.');
        return;
    }

    fetch('/appointments/book', { // Правильний шлях
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            serviceId: serviceId,
            clientId: clientId,
            appointmentTime: appointmentTime
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

    if (newTime) {
        fetch(`/appointments/update/${appointmentId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ newAppointmentTime: newTime })
        })
            .then(response => {
                if (response.ok) {
                    closeModal('rescheduleModal');
                    location.reload();
                } else {
                    alert("Не вдалося перезаписати запис. Спробуйте ще раз.");
                }
            });
    }
});

// Видалення запису
function deleteAppointment(appointmentId) {
    fetch(`/appointments/delete/${appointmentId}`, {
        method: 'DELETE'
    })
        .then(response => {
            if (response.ok) {
                location.reload();
            } else {
                alert("Не вдалося видалити запис. Спробуйте ще раз.");
            }
        });
}