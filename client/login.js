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
    fetch("http://127.0.0.1:9091/api/login", {
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

        /* ===== STORE SESSION ===== */
        sessionStorage.setItem("user", JSON.stringify(data.user));
        sessionStorage.setItem("hms_token", data.token);

        /* ===== ROLE BASED REDIRECT ===== */
        if (data.role === "ADMIN") {
            window.location.replace("INDEX.HTML");
        }
        else if (data.role === "DOCTOR") {
            window.location.replace("doctor.html");
        }
        else if (data.role === "PATIENT") {
            window.location.replace("patient.html");
        }
        else {
            errorEl.innerText = "❌ Role not recognized";
        }
    })
    .catch(err => {
        console.error(err);
        errorEl.innerText = "❌ Login failed";
    });
}
