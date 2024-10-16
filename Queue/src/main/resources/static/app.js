document.getElementById('loginForm').onsubmit = async function(event) {
    event.preventDefault(); // Запобігаємо стандартному надсиланню форми
    const phoneNumber = document.getElementById('phoneNumber').value;

    const response = await fetch('/users/request-otp', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ phoneNumber: phoneNumber })
    });

    if (response.ok) {
        document.getElementById('otpSection').style.display = 'block';
        alert('OTP код було відправлено на номер: ' + phoneNumber);
    } else {
        alert('Користувача з таким номером не знайдено');
    }
};

document.getElementById('verifyOtp').onclick = async function() {
    const otpCode = document.getElementById('otp').value;
    const phoneNumber = document.getElementById('phoneNumber').value;

    const response = await fetch('/users/verify-otp', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ phoneNumber: phoneNumber, otpCode: otpCode })
    });

    if (response.ok) {
        alert('Авторизація успішна!');
        // Перенаправити на кабінет користувача
        window.location.href = 'dashboard.html';
    } else {
        alert('Невірний OTP код');
    }
};
