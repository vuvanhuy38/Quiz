document.addEventListener("DOMContentLoaded", function () {
    fetchProfile();

    document.getElementById("profileForm").addEventListener("submit", function (e) {
        e.preventDefault();
        updateProfile();
    });

    document.getElementById("passwordForm").addEventListener("submit", function (e) {
        e.preventDefault();
        changePassword();
    });
});

function showAlert(message, type = "success") {
    const alertContainer = document.getElementById("alertContainer");
    const alertBox = document.getElementById("alertBox");
    const alertMessage = document.getElementById("alertMessage");

    alertContainer.classList.remove("d-none");
    alertBox.className = `alert alert-${type} alert-dismissible fade show`;
    alertMessage.textContent = message;

    // Auto-scroll to alert
    window.scrollTo({ top: 0, behavior: 'smooth' });

    // Auto-hide after 5 seconds
    setTimeout(() => {
        alertContainer.classList.add("d-none");
    }, 5000);
}

function fetchProfile() {
    fetch("/api/users/profile")
        .then(response => response.json())
        .then(res => {
            if (res.data) {
                const user = res.data;
                
                // Điền thông tin vào Sidebar
                document.getElementById("displayFullName").textContent = `${user.lastName} ${user.firstName}`;
                document.getElementById("displayUsername").textContent = `@${user.username}`;
                document.getElementById("sidebarEmail").textContent = user.email || "-";
                document.getElementById("sidebarPhone").textContent = user.phone || "-";
                
                const roleEl = document.getElementById("displayRole");
                roleEl.textContent = user.role;
                if (user.role === "ADMIN") {
                    roleEl.className = "role-badge role-admin";
                } else {
                    roleEl.className = "role-badge role-user";
                }

                // Điền Avatar chữ cái đầu
                const initials = ((user.lastName ? user.lastName.charAt(0) : "") + 
                                  (user.firstName ? user.firstName.charAt(0) : "")).toUpperCase();
                document.getElementById("avatarCircle").textContent = initials || "?";

                // Điền thông tin vào Form
                document.getElementById("lastName").value = user.lastName || "";
                document.getElementById("firstName").value = user.firstName || "";
                document.getElementById("email").value = user.email || "";
                document.getElementById("phone").value = user.phone || "";
            }
        })
        .catch(err => {
            console.error("Lỗi lấy thông tin cá nhân:", err);
            showAlert("Không thể lấy thông tin cá nhân. Vui lòng tải lại trang.", "danger");
        });
}

function updateProfile() {
    const firstName = document.getElementById("firstName").value;
    const lastName = document.getElementById("lastName").value;
    const email = document.getElementById("email").value;
    const phone = document.getElementById("phone").value;

    const requestBody = { firstName, lastName, email, phone };

    fetch("/api/users/profile", {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(requestBody)
    })
    .then(response => response.json().then(data => ({ status: response.status, body: data })))
    .then(res => {
        if (res.status === 200) {
            showAlert("Cập nhật thông tin tài khoản thành công!", "success");
            
            // Cập nhật giao diện Sidebar
            document.getElementById("displayFullName").textContent = `${lastName} ${firstName}`;
            document.getElementById("sidebarEmail").textContent = email;
            document.getElementById("sidebarPhone").textContent = phone || "-";
            
            // Cập nhật tên trên thanh Navbar nếu tồn tại
            const navbarName = document.getElementById("navbarUserFullName");
            if (navbarName) {
                navbarName.textContent = `${lastName} ${firstName}`;
            }

            const initials = ((lastName ? lastName.charAt(0) : "") + 
                              (firstName ? firstName.charAt(0) : "")).toUpperCase();
            document.getElementById("avatarCircle").textContent = initials || "?";
        } else {
            const errorMsg = res.body.message || "Có lỗi xảy ra khi cập nhật thông tin.";
            showAlert(errorMsg, "danger");
        }
    })
    .catch(err => {
        console.error("Lỗi cập nhật:", err);
        showAlert("Đã xảy ra lỗi mạng. Vui lòng thử lại sau.", "danger");
    });
}

function changePassword() {
    const oldPassword = document.getElementById("oldPassword").value;
    const newPassword = document.getElementById("newPassword").value;
    const confirmPassword = document.getElementById("confirmPassword").value;

    if (newPassword.length < 6) {
        showAlert("Mật khẩu mới phải có ít nhất 6 ký tự.", "danger");
        return;
    }

    if (newPassword !== confirmPassword) {
        showAlert("Mật khẩu xác nhận không khớp.", "danger");
        return;
    }

    const requestBody = { oldPassword, newPassword, confirmPassword };

    fetch("/api/users/profile/change-password", {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(requestBody)
    })
    .then(response => response.json().then(data => ({ status: response.status, body: data })))
    .then(res => {
        if (res.status === 200) {
            showAlert("Đổi mật khẩu thành công!", "success");
            // Reset form
            document.getElementById("passwordForm").reset();
        } else {
            const errorMsg = res.body.message || "Có lỗi xảy ra khi đổi mật khẩu.";
            showAlert(errorMsg, "danger");
        }
    })
    .catch(err => {
        console.error("Lỗi đổi mật khẩu:", err);
        showAlert("Đã xảy ra lỗi mạng. Vui lòng thử lại sau.", "danger");
    });
}
