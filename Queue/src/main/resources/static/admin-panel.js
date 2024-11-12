// Відкриття підменю
function openSubMenu(menuId) {
    document.querySelectorAll('.submenu').forEach(menu => menu.style.display = 'none');
    document.getElementById(menuId).style.display = 'block';
}

// Функція для відкриття модального вікна
function openModal(modalId) {
    const modal = document.getElementById(modalId);
    modal.style.display = 'flex';
}

// Функція для закриття модального вікна
function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    modal.style.display = 'none';
}

// Закриття модального вікна при кліку поза його межами
window.onclick = function(event) {
    const modals = document.querySelectorAll('.modal');
    modals.forEach(modal => {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    });
};

// Додатково, щоб закривати модальне вікно при натисканні на кнопку "Закрити"
document.querySelectorAll('.close').forEach(function(closeButton) {
    closeButton.addEventListener('click', function() {
        const modalId = closeButton.closest('.modal').id;
        closeModal(modalId);
    });
});


// Загальна функція для отримання даних
function fetchData(url, successCallback) {
    fetch(url)
        .then(response => response.json())
        .then(successCallback)
        .catch(error => {
            console.error('Error:', error);
            alert('Щось пішло не так, спробуйте пізніше.');
        });
}

// Завантаження робочих місць
// Завантаження робочих місць
fetchData('/workplaces/all-workplaces', workplaces => {
    console.log('Робочі місця:', workplaces); // Перевірте тут, чи отримуєте ви правильний список
    if (Array.isArray(workplaces)) {
        const updateSelect = document.getElementById('workplaceIdToUpdate');
        const deleteSelect = document.getElementById('workplaceIdToDelete');
        const addWorkplaceSelect = document.getElementById('assignedWorkplace');
        const updateWorkplaceSelect = document.getElementById('updatedAssignedWorkplace');

        // Очищаємо попередній вміст
        updateSelect.innerHTML = deleteSelect.innerHTML = addWorkplaceSelect.innerHTML = updateWorkplaceSelect.innerHTML = '<option value="">Виберіть робоче місце</option>';

        workplaces.forEach(workplace => {
            const optionUpdate = document.createElement('option');
            optionUpdate.value = workplace.id;
            optionUpdate.textContent = `ID: ${workplace.id}, Назва: ${workplace.workplaceName}`;
            updateSelect.appendChild(optionUpdate);

            const optionDelete = document.createElement('option');
            optionDelete.value = workplace.id;
            optionDelete.textContent = `ID: ${workplace.id}, Назва: ${workplace.workplaceName}`;
            deleteSelect.appendChild(optionDelete);

            // Додаємо робоче місце в список для додавання працівника
            const optionAdd = document.createElement('option');
            optionAdd.value = workplace.id;
            optionAdd.textContent = `ID: ${workplace.id}, Назва: ${workplace.workplaceName}`;
            addWorkplaceSelect.appendChild(optionAdd);

            // Додаємо робоче місце в список для оновлення працівника
            const optionUpdateWorkplace = document.createElement('option');
            optionUpdateWorkplace.value = workplace.id;
            optionUpdateWorkplace.textContent = `ID: ${workplace.id}, Назва: ${workplace.workplaceName}`;
            updateWorkplaceSelect.appendChild(optionUpdateWorkplace);
        });
    } else {
        console.error('Невірний формат даних робочих місць');
    }
});

// Завантаження працівників
    fetchData('/employees/all-employees', employees => {
        console.log('Працівники:', employees);
        const updateSelect = document.getElementById('employeeIdToUpdate');
        const deleteSelect = document.getElementById('employeeIdToDelete');
        employees.forEach(employee => {
            const option = document.createElement('option');
            option.value = employee.id;
            option.textContent = `${employee.username} (Робоче місце: ${employee.workplace ? employee.workplace.workplaceName : 'Не призначено'})`;
            updateSelect.appendChild(option);
            deleteSelect.appendChild(option.cloneNode(true));
        });
    });

// Завантаження послуг
fetchData('/services/all-services', services => {
    console.log('Послуги:', services); // Логуємо отримані дані
    const updateSelect = document.getElementById('serviceIdToUpdate');
    const deleteSelect = document.getElementById('serviceIdToDelete');
    updateSelect.innerHTML = deleteSelect.innerHTML = '<option value="">Виберіть послугу</option>';

    services.forEach(service => {
        const option = document.createElement('option');
        option.value = service.id;
        option.textContent = service.serviceName;
        updateSelect.appendChild(option.cloneNode(true));
        deleteSelect.appendChild(option);
    });
});

// Генерація звіту
document.getElementById('report-form').addEventListener('submit', function(event) {
    event.preventDefault(); // Prevent default form submission

    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;

    if (!startDate || !endDate) {
        alert('Будь ласка, заповніть обидва поля для дати.');
        return;
    }

    // Підготовка даних для запиту
    const data = {
        startDate: startDate,
        endDate: endDate
    };

    // Відправка запиту на сервер для генерації звіту
    fetch('/reports/generate', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
        .then(response => response.json())
        .then(report => {
            // Відображення звіту на сторінці
            if (report) {
                document.getElementById('totalTickets').innerText = `Кількість талонів: ${report.totalTickets}`;
                document.getElementById('averageWaitingTime').innerText = `Середній час очікування: ${report.averageWaitingTime} хвилин`;

                // Створення посилання для завантаження звіту у форматі CSV
                const downloadLink = document.getElementById('download-link');
                downloadLink.href = `data:text/csv;charset=utf-8,${encodeURIComponent(report.csvContent)}`;

                // Показуємо результати
                document.getElementById('report-results').style.display = 'block';
            } else {
                alert('Не вдалося згенерувати звіт.');
            }
        })
        .catch(error => {
            console.error('Error generating report:', error);
            alert('Сталася помилка при генерації звіту.');
        });
});

// Додати нового працівника
document.getElementById('addEmployeeForm').addEventListener('submit', function(event) {
    event.preventDefault();
    const username = document.getElementById('employeeUsername').value;
    const password = document.getElementById('employeePassword').value;
    const role = document.getElementById('employeeRole').value;
    const workplaceId = document.getElementById('assignedWorkplace').value;

    fetch('/employees/add-employee', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password, role, workplaceId })
    })
        .then(response => response.json())
        .then(data => {
            alert('Працівника успішно додано: ' + data.username);
            location.reload();
        })
        .catch(error => console.error('Error:', error));
});

// Оновлення працівника
document.getElementById('updateEmployeeForm').addEventListener('submit', function(event) {
    event.preventDefault();
    const employeeId = document.getElementById('employeeIdToUpdate').value;
    const updatedUsername = document.getElementById('updatedEmployeeUsername').value;
    const updatedPassword = document.getElementById('updatedEmployeePassword').value;
    const updatedRole = document.getElementById('updatedEmployeeRole').value;
    const updatedWorkplaceId = document.getElementById('updatedAssignedWorkplace').value;

    fetch(`/employees/update-employee/${employeeId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username: updatedUsername, password: updatedPassword, role: updatedRole, workplaceId: updatedWorkplaceId })
    })
        .then(response => response.json())
        .then(data => {
            alert('Працівника успішно оновлено: ' + data.username);
            location.reload();
        })
        .catch(error => console.error('Error:', error));
});

// Видалення працівника
document.getElementById('deleteEmployeeForm').addEventListener('submit', function(event) {
    event.preventDefault();
    const employeeId = document.getElementById('employeeIdToDelete').value;

    fetch(`/employees/delete-employee/${employeeId}`, { method: 'DELETE' })
        .then(response => {
            if (response.ok) {
                alert('Працівника успішно видалено');
                location.reload();
            } else {
                throw new Error('Не вдалося видалити працівника');
            }
        })
        .catch(error => console.error('Error:', error));
});

// Додавання нової послуги
document.getElementById('addServiceForm').addEventListener('submit', function(event) {
    event.preventDefault();

    const serviceName = document.getElementById('serviceName').value;
    const serviceDescription = document.getElementById('serviceDescription').value;

    fetch('/services/add-service', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            serviceName: serviceName,
            serviceDescription: serviceDescription
        })
    })
        .then(response => response.json())
        .then(data => {
            alert('Послугу успішно додано: ' + data.serviceName);
            location.reload();
        })
        .catch(error => console.error('Error:', error));
});

// Оновлення існуючої послуги
document.getElementById('updateServiceForm').addEventListener('submit', function(event) {
    event.preventDefault();

    const serviceId = document.getElementById('serviceIdToUpdate').value;
    const updatedServiceName = document.getElementById('updatedServiceName').value;
    const updatedServiceDescription = document.getElementById('updatedServiceDescription').value;

    fetch(`/services/update-service/${serviceId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            serviceName: updatedServiceName,
            serviceDescription: updatedServiceDescription
        })
    })
        .then(response => response.json())
        .then(data => {
            alert('Послугу успішно оновлено: ' + data.serviceName);
            location.reload();  // Оновити сторінку після оновлення
        })
        .catch(error => console.error('Error:', error));
});

// Видалити послугу
document.getElementById('deleteServiceForm').addEventListener('submit', function(event) {
    event.preventDefault();
    const serviceId = document.getElementById('serviceIdToDelete').value;

    fetch(`/services/delete-service/${serviceId}`, {
        method: 'DELETE'
    })
        .then(response => {
            if (response.ok) {
                alert('Послугу успішно видалено');
                location.reload();
            }
        })
        .catch(error => console.error('Error:', error));
});

// Додати нове робоче місце
document.getElementById('addWorkplaceForm').addEventListener('submit', function(event) {
    event.preventDefault();
    const workplaceName = document.getElementById('workplaceName').value;

    fetch('/workplaces/add-workplace', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            workplaceName: workplaceName
        })
    })
        .then(response => response.json())
        .then(data => {
            alert('Робоче місце успішно додано: ' + data.workplaceName);
            loadWorkplaces();
        })
        .catch(error => console.error('Error:', error));
});

// Оновити робоче місце
document.getElementById('updateWorkplaceForm').addEventListener('submit', function(event) {
    event.preventDefault();
    const workplaceId = document.getElementById('workplaceIdToUpdate').value;
    const updatedWorkplaceName = document.getElementById('updatedWorkplaceName').value;

    fetch(`/workplaces/update-workplace/${workplaceId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            workplaceName: updatedWorkplaceName
        })
    })
        .then(response => response.json())
        .then(data => {
            alert('Робоче місце успішно оновлено: ' + data.workplaceName);
            loadWorkplaces();
        })
        .catch(error => console.error('Error:', error));
});

// Видалити робоче місце
document.getElementById('deleteWorkplaceForm').addEventListener('submit', function(event) {
    event.preventDefault();
    const workplaceId = document.getElementById('workplaceIdToDelete').value;

    fetch(`/workplaces/delete-workplace/${workplaceId}`, {
        method: 'DELETE'
    }).then(response => {
        if (response.ok) {
            alert('Робоче місце успішно видалено');
            loadWorkplaces();
        }
    }).catch(error => console.error('Error:', error));
});
