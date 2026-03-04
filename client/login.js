/* ================= GLOBAL ================= */
let captchaValue = "";

/* ================= DOM READY ================= */
document.addEventListener("DOMContentLoaded", function () {

    /* ===== OPEN LOGIN PANEL ===== */
    const openBtn = document.getElementById("openLogin");
    const loginPanel = document.getElementById("loginPanel");

    if (openBtn && loginPanel) {
        openBtn.addEventListener("click", () => {
            loginPanel.classList.add("show");
        });
    }

    /* ===== GENERATE CAPTCHA ON LOAD ===== */
    generateCaptcha();
});

/* ================= CAPTCHA ================= */
function generateCaptcha() {
    const chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    captchaValue = "";

    for (let i = 0; i < 6; i++) {
        captchaValue += chars[Math.floor(Math.random() * chars.length)];
    }

    const captchaEl = document.getElementById("captchaText");
    if (captchaEl) {
        captchaEl.innerText = captchaValue;
    }
}

/* ================= LOGIN ================= */
function login() {

    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();
    const captchaInput = document.getElementById("captchaInput").value.trim();
    const errorEl = document.getElementById("error");

    errorEl.innerText = "";

    /* ===== CAPTCHA CHECK ===== */
    if (captchaInput !== captchaValue) {
        errorEl.innerText = "❌ Invalid Captcha";
        generateCaptcha();
        document.getElementById("captchaInput").value = "";
        return;
    }

    /* ===== BACKEND LOGIN ===== */
    fetch("/api/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error("HTTP error " + response.status);
        }
        return response.text();
    })
    .then(text => {
        if (!text) {
            errorEl.innerText = "❌ Invalid Credentials";
            return;
        }

        const data = JSON.parse(text);
        
        console.log("Full response:", data);
        
        // Handle different response formats - backend may return user directly or wrapped in "user" key
        let userData = data.user || data;
        let token = data.token || data.jwt || data.accessToken;
        
        console.log("User data:", userData);
        console.log("Token:", token);
        
        // Store BOTH in sessionStorage and localStorage as backup
        if (userData) {
            sessionStorage.setItem("user", JSON.stringify(userData));
            localStorage.setItem("user", JSON.stringify(userData));
        }
        
        if (token) {
            sessionStorage.setItem("hms_token", token);
            localStorage.setItem("hms_token", token);
        }
        
        // Also store full response
        sessionStorage.setItem("loginData", text);
        
        console.log("User stored in session:", userData);

/* ===== ROLE BASED REDIRECT ===== */

console.log("Login Response:", data);

// Safely get role from backend response - check multiple possible locations
const role = data.role || (data.user && data.user.role) || data.userRole;

// If no role found → login failed
if (!role) {
    errorEl.innerText = "❌ Login failed or role not found";
    return;
}

// Convert to lowercase for flexible matching
const roleLower = role.toLowerCase();

alert("Login successful! Redirecting as " + role + "...");

// Redirect based on role
if (roleLower === "admin") {
    window.location.replace("admin.html");
}
else if (roleLower === "doctor") {
    window.location.replace("doctor.html");
}
else if (roleLower === "patient") {
    window.location.replace("patient.html");
}
else {
    errorEl.innerText = "❌ Role not recognized: " + role;
}
    })
    .catch(err => {
        console.error(err);
        errorEl.innerText = "❌ Login failed: " + err.message;
    });
}
