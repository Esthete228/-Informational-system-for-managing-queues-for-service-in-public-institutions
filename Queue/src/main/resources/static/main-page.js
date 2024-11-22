let lastTicketNumber = null;

// Функція для безпечного екранування даних
function sanitizeHTML(text) {
    const element = document.createElement('div');
    element.textContent = text;
    return element.innerHTML;
}

// Функція для підключення до SSE
function connectToQueueUpdates() {
    const eventSource = new EventSource('/queues/queue-updates');

    eventSource.onmessage = function(event) {
        const queue = JSON.parse(event.data);
        updateQueueTable(queue);
    };

    eventSource.onerror = function() {
        console.error('Error receiving SSE');
        eventSource.close();
    };
}

// Функція для оновлення таблиці черги
function updateQueueTable(queue) {
    const queueDiv = document.getElementById('in-progress-queue');
    queueDiv.innerHTML = '';

    if (queue.length === 0) {
        queueDiv.textContent = 'Черга пуста.';
    } else {
        const table = document.createElement('table');
        const header = document.createElement('tr');
        const th1 = document.createElement('th');
        const th2 = document.createElement('th');

        th1.textContent = 'Талон';
        th2.textContent = 'Робоче місце';

        header.appendChild(th1);
        header.appendChild(th2);
        table.appendChild(header);

        queue.forEach(entry => {
            const row = document.createElement('tr');
            const ticketCell = document.createElement('td');
            const workplaceCell = document.createElement('td');

            // Екрануємо значення перед вставкою
            ticketCell.textContent = sanitizeHTML(entry.ticketNumber);
            workplaceCell.textContent = sanitizeHTML(entry.workplace?.workplaceName || entry.workplace.id);

            row.appendChild(ticketCell);
            row.appendChild(workplaceCell);
            table.appendChild(row);
        });

        queueDiv.appendChild(table);

        // Оновлюємо звукове сповіщення для поточного талону
        const currentClient = queue[0];

        // Перевірка на новий талон
        if (currentClient.ticketNumber !== lastTicketNumber) {
            playSoundAlert(currentClient);
            lastTicketNumber = currentClient.ticketNumber;
        }
    }
}

// Функція для програвання звукового сповіщення та відображення повідомлення
function playSoundAlert(client) {
    const soundAlertElement = document.getElementById('sound-alert');

    // Екрануємо дані для виводу
    const ticketNumber = sanitizeHTML(client.ticketNumber);
    const workplaceName = sanitizeHTML(client.workplace?.workplaceName || client.workplace.id);

    soundAlertElement.textContent = `Шановний клієнте з талоном №${ticketNumber}, пройдіть будь ласка до робочого місця №${workplaceName}`;
    soundAlertElement.style.display = 'block';

    const audio = new Audio('/sounds/message.wav');
    audio.play();

    setTimeout(() => {
        soundAlertElement.style.display = 'none';
    }, 5000);
}

connectToQueueUpdates();
