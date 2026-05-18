const form = document.getElementById("registerForm");
const message = document.getElementById("message");

function clearErrors() {

    document.getElementById("usernameError").innerText = "";
    document.getElementById("emailError").innerText = "";
    document.getElementById("firstNameError").innerText = "";
    document.getElementById("lastNameError").innerText = "";
    document.getElementById("passwordError").innerText = "";

    message.innerHTML = "";
}

function showValidationErrors(errors) {

    clearErrors();

    Object.keys(errors).forEach(field => {

        const errorElement = document.getElementById(field + "Error");

        if (errorElement) {
            errorElement.innerText = errors[field];
        }
    });
}

form.addEventListener("submit", async (e) => {

    e.preventDefault();

    clearErrors();

    const data = {
        username: document.getElementById("username").value,
        email: document.getElementById("email").value,
        phone: document.getElementById("phone").value,
        password: document.getElementById("password").value,
        firstName: document.getElementById("firstName").value,
        lastName: document.getElementById("lastName").value
    };

    try {

        const response = await fetch("http://localhost:8080/api/auth/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        });

        const result = await response.json();

        if (response.ok) {

            message.innerHTML = `
                <div class="alert alert-success">
                    Đăng ký thành công
                </div>
            `;

            form.reset();

        } else {

            // lỗi validate
            showValidationErrors(result);
        }

    } catch (error) {

        message.innerHTML = `
            <div class="alert alert-danger">
                Không thể kết nối server
            </div>
        `;
    }
});
