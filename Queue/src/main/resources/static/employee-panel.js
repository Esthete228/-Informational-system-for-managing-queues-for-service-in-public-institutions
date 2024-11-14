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
        if (!response.ok) throw new Error("Network response was not ok");
        return await response.json();
    }

    // Populate dropdowns for services and workplaces
    async function populateDropdowns() {
        try {
            const [services, workplaces] = await Promise.all([
                fetchJSON('/services/all-services'),
                fetchJSON('/workplaces/all-workplaces'),
            ]);

            // Populate service and workplace dropdowns
            const serviceSelects = [document.getElementById('serviceId'), document.getElementById('newServiceId')];
            const workplaceSelects = [document.getElementById('workplaceId'), document.getElementById('newWorkplaceId'), document.getElementById('transferWorkplaceId')];

            serviceSelects.forEach(select => {
                select.innerHTML = '<option value="">Оберіть послугу</option>';
                services.forEach(service => select.appendChild(new Option(service.serviceName, service.id)));
            });

            workplaceSelects.forEach(select => {
                select.innerHTML = '<option value="">Оберіть робоче місце</option>';
                workplaces.forEach(workplace => select.appendChild(new Option(workplace.workplaceName, workplace.id)));
            });
        } catch (error) {
            console.error('Error fetching data:', error);
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
                    div.innerText = `Талон: ${ticket.ticketNumber}, Послуга: ${ticket.
                        serviceEntity.serviceName}, Статус: ${ticket.status}`;
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
            console.log("Current client data:", currentClientData);

            if (currentClientData) {
                currentClientDiv.innerText = `Клієнт: ${currentClientData.ticketNumber}, Послуга: ${currentClientData.serviceEntity.serviceName}`;
                currentClientDiv.dataset.currentTicketId = currentClientData.id;
            } else {
                currentClientDiv.innerText = "Клієнт не обслуговується.";
                delete currentClientDiv.dataset.currentTicketId;
            }
        } catch (error) {
            console.error("Error loading current client:", error);
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
    forms.create.addEventListener("submit", function(event) {
        const serviceId = document.getElementById("serviceId").value;
        const workplaceId = document.getElementById("workplaceId").value;
        handleSubmit(event, '/queues/create-ticket', 'POST', { serviceId, workplaceId }, "Талон успішно створено!", "Не вдалося створити талон.");
    });

    forms.update.addEventListener("submit", function(event) {
        const queueId = document.getElementById("queueId").value;
        const newServiceId = document.getElementById("newServiceId").value;
        const newWorkplaceId = document.getElementById("newWorkplaceId").value;
        handleSubmit(event, `/queues/update-ticket/${queueId}`, 'PUT', { serviceId: newServiceId, workplaceId: newWorkplaceId }, "Талон успішно оновлено!", "Не вдалося оновити талон.");
    });

    forms.delete.addEventListener("submit", function(event) {
        const queueId = document.getElementById("deleteQueueId").value;
        handleSubmit(event, `/queues/delete-ticket/${queueId}`, 'DELETE', { workplaceId: document.getElementById("workplaceId").value }, "Талон успішно видалено!", "Не вдалося видалити талон.");
    });

    // Call next client button click
    document.getElementById("call-next-client").addEventListener("click", async function() {
        try {
            const workplaceId = await fetchWorkplaceId();
            const response = await fetch(`/queues/call-next-client/${workplaceId}`, { method: 'POST' });

            if (response.ok) {
                const client = await response.json();
                document.getElementById("current-client").innerText = `Клієнт: ${client.ticketNumber}`;
                showNotification("Клієнта викликано!");
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

    // Transfer client form submit
    forms.transfer.addEventListener("submit", async function(event) {
        const transferWorkplaceId = document.getElementById("transferWorkplaceId").value;
        const currentTicketId = currentClientDiv.dataset.currentTicketId;

        if (currentTicketId) {
            await handleSubmit(event, `/queues/transfer-client/${currentTicketId}`, 'PUT', {
                fromWorkplaceId: await fetchWorkplaceId(),
                toWorkplaceId: transferWorkplaceId,
            }, "Клієнта успішно передано!", "Не вдалося передати клієнта.");
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