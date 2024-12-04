// Функції для відкриття та закриття модальних вікон
function openModal(modalId) {
    document.getElementById(modalId).style.display = "block";
}

function closeModal(modalId) {
    document.getElementById(modalId).style.display = "none";
}

// Закриття модального вікна при кліці поза його межами
window.onclick = function(event) {
    const modals = document.querySelectorAll('.modal');
    modals.forEach(modal => {
        if (event.target == modal) {
            modal.style.display = "none";
        }
    });
}

document.addEventListener("DOMContentLoaded", function() {
    const forms = {
        create: document.getElementById("create-ticket-form"),
        update: document.getElementById("update-ticket-form"),
        delete: document.getElementById("delete-ticket-form"),
        transfer: document.getElementById("transfer-client-form"),
    };
    const currentQueueDiv = document.getElementById("current-queue");
    const currentClientDiv = document.getElementById("current-client");
    const notificationDiv = document.getElementById("notification");

    // Populate dropdowns and update queue at intervals
    populateDropdowns();
    populateWorkplaceDropdown();
    populateCurrentQueue();
    populateCurrentClient();
    setInterval(loadQueue, 5000);
    setInterval(populateCurrentQueue, 5000);
    setInterval(populateCurrentClient, 5000);

    // Function to fetch current workplace ID
    async function fetchWorkplaceId() {
        const response = await fetch('/current-workplace');
        if (!response.ok) throw new Error("Failed to load workplace ID.");
        return await response.json();
    }

    // Function to fetch JSON data from URL
    async function fetchJSON(url) {
        const response = await fetch(url);

        // Перевіряємо, чи відповідь успішна
        if (!response.ok) {
            throw new Error(`Network response was not ok: ${response.statusText}`);
        }

        // Перевіряємо, чи тіло відповіді не порожнє
        const text = await response.text();
        if (!text) {
            console.warn('Empty response received.');
            return null; // Повертаємо null для порожньої відповіді
        }

        try {
            return JSON.parse(text); // Розбираємо JSON
        } catch (error) {
            throw new Error("Failed to parse JSON response: " + error.message);
        }
    }

    async function populateDropdowns() {
        try {
            const workplaceId = await fetchWorkplaceId(); // Отримання id робочого місця

            // Отримуємо послуги для поточного робочого місця
            const servicesForWorkplace = await fetchJSON(`/service-workplace/available-services/${workplaceId}`);

            // Наповнюємо список послуг
            const serviceSelects = [document.getElementById('serviceId'), document.getElementById('newServiceId')];
            serviceSelects.forEach(select => {
                select.innerHTML = '<option value="">Оберіть послугу</option>'; // очищаємо попередній список
                servicesForWorkplace.forEach(service => {
                    select.appendChild(new Option(service.serviceName, service.id)); // додаємо послугу до списку
                });
            });

        } catch (error) {
            console.error('Error fetching services for workplace:', error); // Виведення помилки, якщо запит не вдалося виконати
        }
    }

    // Функція для наповнення списку робочих місць на основі serviceId
        async function populateWorkplaceDropdown(serviceId) {
            try {
                // Отримуємо доступні робочі місця для даної послуги
                const workplacesForService = await fetchJSON(`/service-workplace/available-workplaces/${serviceId}`);

                // Наповнюємо список робочих місць для передачі клієнта
                const transferWorkplaceSelect = document.getElementById('transferWorkplaceId');
                transferWorkplaceSelect.innerHTML = '<option value="">Оберіть робоче місце</option>'; // очищаємо попередній список

                workplacesForService.forEach(workplace => {
                    transferWorkplaceSelect.appendChild(new Option(workplace.workplaceName, workplace.id)); // додаємо робоче місце до списку
                });

            } catch (error) {
                console.error('Error fetching workplaces for service:', error); // Виведення помилки, якщо запит не вдалося виконати
            }
        }

    // Load the current queue data
    async function loadQueue() {
        try {
            const workplaceId = await fetchWorkplaceId();
            const queueData = await fetchJSON(`/queues/current-queue/${workplaceId}`);
            console.log("Queue data:", queueData);
        } catch (error) {
            console.error('Error loading queue:', error);
        }
    }

    // Populate the current queue
    async function populateCurrentQueue() {
        try {
            const workplaceId = await fetchWorkplaceId();
            const queueData = await fetchJSON(`/queues/current-queue/${workplaceId}`);
            console.log("Queue data:", queueData);
            currentQueueDiv.innerHTML = "";

            const queueSelects = [
                document.getElementById("queueId"),
                document.getElementById("deleteQueueId"),
            ];

            queueSelects.forEach(select => {
                select.innerHTML = '<option value="">Оберіть талон</option>';
            });

            if (queueData.length > 0) {
                queueData.forEach(ticket => {
                    const div = document.createElement("div");
                    div.innerText = `Талон: ${ticket.ticketNumber},\ 
                    Клієнт: ${ticket.clientName},\
                    Час: ${new Date(ticket.createdAt).toLocaleString()},\ 
                    Послуга: ${ticket.serviceEntity.serviceName},\
                    Статус: ${ticket.status}`;
                    currentQueueDiv.appendChild(div);

                    queueSelects.forEach(select => {
                        const option = new Option(`Талон ${ticket.ticketNumber}`, ticket.id);
                        select.appendChild(option);
                    });
                });
            } else {
                currentQueueDiv.innerText = "Черга пуста.";
            }
        } catch (error) {
            console.error("Error loading current queue:", error);
        }
    }

    // Populate current client data
    async function populateCurrentClient() {
        try {
            const workplaceId = await fetchWorkplaceId();
            const currentClientData = await fetchJSON(`/queues/current-client/${workplaceId}`);

            if (currentClientData && currentClientData.ticketNumber) {
                currentClientDiv.innerText = `Клієнт: ${currentClientData.ticketNumber}, Послуга: ${currentClientData.serviceEntity.serviceName}`;
                currentClientDiv.dataset.currentTicketId = currentClientData.id;
            } else {
                // Якщо клієнта немає або відповіді порожні
                console.warn("Немає клієнта для обслуговування або порожня відповідь.");
                currentClientDiv.innerText = "Клієнта не викликано.";
                delete currentClientDiv.dataset.currentTicketId;
            }
        } catch (error) {
            console.error("Error loading current client:", error);
            // Встановлення значення на випадок помилки
            currentClientDiv.innerText = "Не вдалося завантажити дані про клієнта.";
            delete currentClientDiv.dataset.currentTicketId;
        }
    }

    // Show notifications on success or error
    function showNotification(message) {
        notificationDiv.innerText = message;
        notificationDiv.style.display = 'block';
        setTimeout(() => {
            notificationDiv.style.display = 'none';
        }, 3000);
    }

    // Handle form submissions
    async function handleSubmit(event, url, method, bodyData, successMessage, errorMessage) {
        event.preventDefault();
        try {
            const response = await fetch(url, {
                method: method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(bodyData),
            });

            if (response.ok) {
                showNotification(successMessage);
                await populateCurrentQueue();
                await populateCurrentClient();
            } else {
                showNotification(errorMessage);
            }
        } catch (error) {
            console.error('Error:', error);
            showNotification("Сталася помилка.");
        }
    }

    // Add event listeners for form submissions and button clicks
    // Обробник для створення талону
    forms.create.addEventListener("submit", function(event) {
        event.preventDefault(); // Запобігаємо стандартному перезавантаженню сторінки при відправці форми

        const serviceId = document.getElementById("serviceId").value;

        if (!serviceId) {
            showNotification("Будь ласка, оберіть послугу.");
            return; // Якщо не вибрано послугу, вивести повідомлення
        }

        // Викликаємо сервер для створення талону без передавання робочого місця
        handleSubmit(event, `/queues/create-ticket/${serviceId}`, 'POST', null, "Талон успішно створено!", "Не вдалося створити талон.");
    });

    // Обробник для оновлення талону
    forms.update.addEventListener("submit", function(event) {
        const queueId = document.getElementById("queueId").value;
        const newServiceId = document.getElementById("newServiceId").value;

        if (!newServiceId) {
            showNotification("Будь ласка, оберіть нову послугу.");
            return; // Якщо не вибрано нову послугу, вивести повідомлення
        }

        // Викликаємо сервер для оновлення талону без передавання нового робочого місця
        handleSubmit(event, `/queues/update-ticket/${queueId}`, 'PUT', { newServiceId }, "Талон успішно оновлено!", "Не вдалося оновити талон.");
    });

    forms.delete.addEventListener("submit", function(event) {
        const queueId = document.getElementById("deleteQueueId").value;
        handleSubmit(event, `/queues/delete-ticket/${queueId}`, 'DELETE', { workplaceId: document.getElementById("workplaceId").value }, "Талон успішно видалено!", "Не вдалося видалити талон.");
    });

    document.getElementById("call-next-client").addEventListener("click", async function() {
        try {
            const workplaceId = await fetchWorkplaceId();

            // Викликаємо наступного клієнта
            const response = await fetch(`/queues/call-next-client/${workplaceId}`, { method: 'POST' });

            if (response.ok) {
                const client = await response.json();
                document.getElementById("current-client").innerText = `Клієнт: ${client.ticketNumber}, Послуга: ${client.serviceEntity.serviceName}`;
                showNotification("Клієнта викликано!");

                // Тепер, коли у нас є клієнт, отримуємо його serviceId
                const serviceId = client.serviceEntity.id;

                // Перевірка, чи serviceId не undefined
                if (serviceId) {
                    // Викликаємо функцію для наповнення списку робочих місць для цієї послуги
                    await populateWorkplaceDropdown(serviceId);
                } else {
                    console.error("Не вдалося отримати serviceId для клієнта.");
                    showNotification("Не вдалося отримати дані послуги для клієнта.");
                }

                await populateCurrentQueue();
                await populateCurrentClient();
            } else {
                showNotification("Не вдалося викликати клієнта.");
            }
        } catch (error) {
            console.error('Error:', error);
            showNotification("Сталася помилка при виклику клієнта.");
        }
    });

    forms.transfer.addEventListener("submit", async function(event) {
        const transferWorkplaceId = document.getElementById("transferWorkplaceId").value;
        const currentTicketId = currentClientDiv.dataset.currentTicketId;

        if (currentTicketId) {
            try {

                const response = await fetch(`/queues/transfer-client/${currentTicketId}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        toWorkplaceId: transferWorkplaceId,
                    })
                });

                if (response.ok) {
                    const client = await response.json();
                    document.getElementById("current-client").innerText = `Клієнт: ${client.ticketNumber}, Послуга: ${client.serviceEntity.serviceName}`;
                    showNotification("Клієнта успішно передано!");
                    await populateCurrentQueue();
                    await populateCurrentClient();
                } else {
                    const error = await response.text();
                    showNotification(error || "Не вдалося передати клієнта.");
                }
            } catch (error) {
                console.error('Error:', error);
                showNotification("Сталася помилка при передачі клієнта.");
            }
        } else {
            showNotification("Немає клієнта для передачі.");
        }
    });

    // Complete session button click
    document.getElementById("complete-session-button").addEventListener("click", async function(event) {
        event.preventDefault();

        const currentTicketId = currentClientDiv.dataset.currentTicketId;

        if (currentTicketId) {
            await handleSubmit(event, `/queues/complete-session/${currentTicketId}`, 'PUT', {
                workplaceId: await fetchWorkplaceId(),
            }, "Сеанс успішно завершено!", "Не вдалося завершити сеанс.");
        } else {
            showNotification("Немає клієнта для завершення сеансу.");
        }
    });
});