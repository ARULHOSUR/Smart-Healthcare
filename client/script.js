/* ================== API CORE ================== */
const API = {
  base: 'http://localhost:9091/api',

  headers() {
    const token = sessionStorage.getItem('hms_token') || '';
    return { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` };
  },

  async get(path) {
    const r = await fetch(`${this.base}${path}`, { headers: this.headers() });
    if (!r.ok) throw await r.json().catch(() => ({ message: r.statusText }));
    return r.json();
  },
  async post(path, bodyObj) {
    const r = await fetch(`${this.base}${path}`, {
      method: 'POST', headers: this.headers(), body: JSON.stringify(bodyObj)
    });
    if (!r.ok) throw await r.json().catch(() => ({ message: r.statusText }));
    return r.json();
  },
  async put(path, bodyObj) {
    const r = await fetch(`${this.base}${path}`, {
      method: 'PUT', headers: this.headers(), body: JSON.stringify(bodyObj)
    });
    if (!r.ok) throw await r.json().catch(() => ({ message: r.statusText }));
    return r.json();
  }
};

/* ================== COMMON HELPERS ================== */
function alertJson(title, err) {
  const msg = typeof err === 'string' ? err : (err.message || JSON.stringify(err));
  alert(`${title}: ${msg}`);
}

// <input type="date"> gives "YYYY-MM-DD". Several controllers expect "DD-MM-YYYY".
function isoToDMY(iso) {
  if (!iso) return '';
  const [y, m, d] = iso.split('-');
  return `${d}-${m}-${y}`;
}

function fmt(s) { return s || ''; }

/* ================== PATIENTS ================== */
async function uiLoadPatients() {
  try {
    const data = await API.get('/patients');
    const tb = document.querySelector('#patientsTable tbody');
    const rows = (data || []).slice().sort((a, b) => (a.id || 0) - (b.id || 0));
    tb.innerHTML = rows.map(p =>
      `<tr><td>${p.id}</td><td>${p.name || ''}</td><td>${p.age || ''}</td><td>${p.gender || ''}</td></tr>`
    ).join('');
  } catch (e) { alertJson('Failed to load patients', e); }
}

function uiWirePatientForm() {
  document.getElementById('reloadPatientsBtn')?.addEventListener('click', uiLoadPatients);
  document.getElementById('addPatientForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    try {
      const body = {
        name: document.getElementById('p_name').value,
        age: +document.getElementById('p_age').value || 0,
        gender: document.getElementById('p_gender').value
      };
      await API.post('/patients', body);
      await uiLoadPatients();
      e.target.reset();
    } catch (err) { alertJson('Failed to create patient', err); }
  });
}

/* ================== APPOINTMENTS ================== */
async function uiLoadAppointments() {
  try {
    const data = await API.get('/appointments');
    const tb = document.querySelector('#apptsTable tbody');
    const rows = (data || []).slice().sort((a, b) => (a.id || 0) - (b.id || 0));
    tb.innerHTML = rows.map(a =>
      `<tr><td>${a.id}</td><td>${fmt(a.date)}</td><td>${a.status || ''}</td><td>${a.doctor?.id || a.doctorId || ''}</td><td>${a.patient?.id || a.patientId || ''}</td></tr>`
    ).join('');
  } catch (e) { alertJson('Failed to load appointments', e); }
}

function uiWireAppointmentForm() {
  document.getElementById('reloadApptsBtn')?.addEventListener('click', uiLoadAppointments);
  document.getElementById('addApptForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    try {
      const body = {
        date: isoToDMY(document.getElementById('a_date').value),
        status: document.getElementById('a_status').value || 'SCHEDULED',
        doctorId: +document.getElementById('a_doctor').value,
        patientId: +document.getElementById('a_patient').value
      };
      await API.post('/appointments', body);
      await uiLoadAppointments();
      e.target.reset();
    } catch (err) { alertJson('Failed to create appointment', err); }
  });
}

/* ================== EMR ================== */
async function uiLoadEMR() {
  try {
    const pid = +document.getElementById('filterEmrPatientId')?.value || null;
    const url = pid ? `/emr?patientId=${encodeURIComponent(pid)}` : `/emr`;
    const data = await API.get(url);
    const tb = document.querySelector('#emrTable tbody');
    tb.innerHTML = (data || []).map(r =>
      `<tr><td>${r.id}</td><td>${fmt(r.visitDate || r.date)}</td><td>${r.diagnosis || ''}</td><td>${r.treatmentPlan || r.treatment || ''}</td><td>${r.patient?.name || r.patientId || ''}</td></tr>`
    ).join('');
  } catch (e) { alertJson('Failed to load EMR', e); }
}

function uiWireEMRForm() {
  document.getElementById('reloadEmrBtn')?.addEventListener('click', uiLoadEMR);
  document.getElementById('addEMRForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    try {
      const body = {
        date: isoToDMY(document.getElementById('e_date').value),
        diagnosis: document.getElementById('e_diagnosis').value,
        treatment: document.getElementById('e_treatment').value,
        patientId: +document.getElementById('e_patient').value,
        notes: document.getElementById('e_notes').value
      };
      await API.post('/emr', body);
      await uiLoadEMR();
      e.target.reset();
    } catch (err) { alertJson('Failed to create EMR', err); }
  });
}

/* ================== BILLING ================== */
async function uiLoadBillings() {
  try {
    const pid = +document.getElementById('filterBillPatientId')?.value || null;
    const raw = pid ? await API.get(`/billing/patient/${pid}`) : await API.get('/billing');
    const data = Array.isArray(raw) ? raw : (raw?.data || raw?.content || []);
    const tb = document.querySelector('#billingTable tbody');
    const rowsSorted = (data || []).slice().sort((a, b) => (a.id || 0) - (b.id || 0));
    tb.innerHTML = rowsSorted.map(b => {
      const insurance = b.insurance ?? b.insuranceProvider ?? '';
      const status    = b.status ?? b.claimStatus ?? '';
      return `<tr>
        <td>${b.id ?? ''}</td>
        <td>${fmt(b.date)}</td>
        <td>${b.patient?.name ?? b.patientName ?? b.patientId ?? ''}</td>
        <td>${b.amount ?? ''}</td>
        <td>${b.paymentMethod ?? b.method ?? ''}</td>
        <td>${insurance}</td>
        <td>${status}</td>
        <td>${b.remarks ?? ''}</td>
      </tr>`;
    }).join('');
  } catch (e) { alertJson('Failed to load billing', e); }
}

function uiWireBillingForm() {
  document.getElementById('reloadBillingBtn')?.addEventListener('click', uiLoadBillings);
  document.getElementById('addBillForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    try {
      // backends vary; send the most common names AND fallbacks
      const dateIso = document.getElementById('b_date').value;
      const body = {
        date: isoToDMY(dateIso),
        patientId: +document.getElementById('b_patient').value,
        amount: +document.getElementById('b_amount').value,
        paymentMethod: document.getElementById('b_payment').value,
        // aliases:
        method: document.getElementById('b_payment').value,
        insurance: document.getElementById('b_insurance').value,
        insuranceProvider: document.getElementById('b_insurance').value,
        status: document.getElementById('b_status').value,
        claimStatus: document.getElementById('b_status').value,
        remarks: document.getElementById('b_remarks').value
      };
      await API.post('/billing', body);
      await uiLoadBillings();
      e.target.reset();
    } catch (err) { alertJson('Failed to create billing record', err); }
  });
}

/* ================== LAB ORDERS ================== */
async function uiLoadLabOrders() {
  try {
    const data = await API.get('/lab/orders');
    const tb = document.querySelector('#labOrdersTable tbody');

    const rows = Array.isArray(data)
      ? data
      : (data?.data || data?.content || []); // tolerate paged responses

    const list = (rows || []).slice().sort((a, b) => (a.id || 0) - (b.id || 0));

    tb.innerHTML = list.map(o => {
      const testCell =
        (o.test?.name ?? '') ||
        (o.test?.code ?? '') ||
        (o.test?.id ?? '') ||
        (o.testId ?? o.testID ?? o.test_id ?? o.testCode ?? '');

      const patient =
        (o.patient?.name ?? '') ||
        (o.patientName ?? '') ||
        (o.patient?.id ?? o.patientId ?? '');

      const doctor =
        (o.doctor?.name ?? '') ||
        (o.doctorName ?? '') ||
        (o.doctor?.id ?? o.doctorId ?? '');

      return `<tr>
        <td>${o.id ?? ''}</td>
        <td>${fmt(o.orderedDate || o.date)}</td>
        <td>${o.status ?? ''}</td>
        <td>${o.sampleId ?? ''}</td>
        <td>${testCell}</td>
        <td>${patient}</td>
        <td>${doctor}</td>
      </tr>`;
    }).join('');
  } catch (e) {
    alertJson('Failed to load lab orders', e);
  }
}

function uiWireLISForm() {
  document.getElementById('reloadLabOrdersBtn')?.addEventListener('click', uiLoadLabOrders);

  const form = document.getElementById('addLabOrderForm');
  if (!form) return;

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    e.stopPropagation(); // ensure no navigation/back action

    try {
      const dateIso = document.getElementById('l_date').value; // yyyy-mm-dd

      const body = {
        // Most common field names:
        orderedDate: dateIso,
        status: document.getElementById('l_status').value,
        sampleId: document.getElementById('l_sample').value || null,
        testId: +document.getElementById('l_test').value,
        patientId: +document.getElementById('l_patient').value,
        doctorId: +document.getElementById('l_doctor').value,

        // Fallback for controllers expecting dd-mm-yyyy as "date"
        date: isoToDMY(dateIso)
      };

      await API.post('/lab/orders', body);
      await uiLoadLabOrders();
      e.target.reset();
    } catch (err) {
      const msg = (err && (err.error || err.message)) ? (err.error || err.message) : 'Unknown error';
      alertJson('Failed to create lab order', msg);
      return false;
    }
  });
}

/* ================== PHARMACY ================== */
async function uiLoadMedicines() {
  try {
    const data = await API.get('/pharmacy');
    const tb = document.querySelector('#medsTable tbody');

    const rows = Array.isArray(data)
      ? data
      : (data?.data || data?.content || []);

    tb.innerHTML = (rows || []).map(m => {
      const batch  = m.batchNo ?? m.batch ?? m.batch_number ?? m.batchNumber ?? '';
      const qty    = m.quantity ?? m.qty ?? m.stock ?? '';
      const price  = m.price ?? m.unitPrice ?? m.cost ?? '';
      const expiry = m.expiry ?? m.expiryDate ?? m.expiry_date ?? '';
      return `<tr>
        <td>${m.id ?? ''}</td>
        <td>${m.name ?? ''}</td>
        <td>${batch}</td>
        <td>${qty}</td>
        <td>${price}</td>
        <td>${expiry}</td>
      </tr>`;
    }).join('');
  } catch (e) { alertJson('Failed to load medicines', e); }
}

function uiWirePharmacyForm() {
  document.getElementById('reloadMedsBtn')?.addEventListener('click', uiLoadMedicines);
  document.getElementById('addMedForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    try {
      const name   = document.getElementById('m_name').value;
      const batch  = document.getElementById('m_batch').value || null;
      const qty    = +document.getElementById('m_qty').value;
      const price  = +document.getElementById('m_price').value;
      const expIso = document.getElementById('m_exp').value || null;

      // backend most likely expects stock + expiryDate
      const body = {
        name,
        price,
        stock: qty,
        expiryDate: expIso,   // keep ISO yyyy-mm-dd
        // also send aliases so either controller variant works
        quantity: qty,
        expiry: expIso,
        batchNo: batch,
        batch
      };
      await API.post('/pharmacy', body);
      await uiLoadMedicines();
      e.target.reset();
    } catch (err) { alertJson('Failed to add medicine', err); }
  });
}

/* ================== STAFF ================== */
async function uiLoadStaff() {
  try {
    const data = await API.get('/staff');
    const tb = document.querySelector('#staffTable tbody');
    const rows = (data || []).slice().sort((a, b) => (a.id || 0) - (b.id || 0));
    tb.innerHTML = rows.map(s =>
      `<tr><td>${s.id}</td><td>${s.name}</td><td>${s.role || ''}</td><td>${s.shift || ''}</td></tr>`
    ).join('');
  } catch (e) { alertJson('Failed to load staff', e); }
}

function uiWireStaffForm() {
  document.getElementById('reloadStaffBtn')?.addEventListener('click', uiLoadStaff);
  document.getElementById('addStaffForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    try {
      const body = {
        name: document.getElementById('s_name').value,
        role: document.getElementById('s_role').value,
        shift: document.getElementById('s_shift').value
      };
      await API.post('/staff', body);
      await uiLoadStaff();
      e.target.reset();
    } catch (err) { alertJson('Failed to create staff', err); }
  });
}

/* ================== ADMIN (Settings + Users) ================== */
async function uiLoadSettings() {
  try {
    const d = await API.get('/admin/settings');
    document.getElementById('set_name').value = d.name || '';
    document.getElementById('set_tz').value = d.timezone || '';
    document.getElementById('set_locale').value = d.locale || '';
    document.getElementById('set_email').value = String(!!d.emailEnabled);
    document.getElementById('set_support').value = d.supportEmail || '';
  } catch (_) {}
}

async function uiLoadUsers() {
  try {
    const data = await API.get('/admin/users');

    const rows = Array.isArray(data)
      ? data
      : (data?.data || data?.content || []);

    const tb = document.querySelector('#usersTable tbody');
    tb.innerHTML = (rows || []).map(u => {
      const id     = u.id ?? u.userId ?? '';
      const name   = u.name ?? u.fullName ?? u.username ?? '';
      const email  = u.email ?? u.mail ?? '';
      const role   =
        u.role ?? u.roleName ??
        (Array.isArray(u.authorities) ? (u.authorities[0]?.authority ?? u.authorities[0]) : '') ?? '';
      const status = (u.enabled === false || u.active === false || u.status === 'DISABLED') ? 'Disabled' : 'Enabled';
      return `<tr>
        <td>${id}</td>
        <td>${name}</td>
        <td>${email}</td>
        <td>${role}</td>
        <td>${status}</td>
        <td></td>
      </tr>`;
    }).join('');
  } catch (e) {
    alertJson('Failed to load users', e);
  }
}

function uiWireReportsTab() {
  const form = document.getElementById('exportForm');
  if (!form) return;

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    try {
      const from = document.getElementById('r_from').value; // yyyy-mm-dd
      const to   = document.getElementById('r_to').value;   // yyyy-mm-dd

      const rowsRaw = await API.get('/billing');
      const rows = Array.isArray(rowsRaw)
        ? rowsRaw
        : (rowsRaw?.data || rowsRaw?.content || []);

      // normalize any date to yyyy-mm-dd to compare
      const toIso = (s) => {
        if (!s) return '';
        // dd-mm-yyyy -> yyyy-mm-dd
        if (s.indexOf('-') === 2) { const [d, m, y] = s.split('-'); return `${y}-${m}-${d}`; }
        return s; // already yyyy-mm-dd (or close enough)
      };

      const fromIso = toIso(from);
      const toIsoMax = toIso(to);

      const filtered = (rows || []).filter(r => {
        const d = toIso(r.date);
        return (!fromIso || (d && d >= fromIso)) && (!toIsoMax || (d && d <= toIsoMax));
      });

      const header = ['ID','Date','Patient','Amount','Payment Method','Insurance','Status','Remarks'];
      const csvRows = [header];

      filtered.forEach(b => {
        const insurance = b.insurance ?? b.insuranceProvider ?? '';
        const status    = b.status ?? b.claimStatus ?? '';
        const pm        = b.paymentMethod ?? b.method ?? '';
        const patient   = b.patient?.name ?? b.patientName ?? b.patientId ?? '';
        csvRows.push([
          b.id ?? '',
          (b.date ? `="${String(b.date)}"` : ''),  // Excel-safe date
          patient,
          b.amount ?? '',
          pm,
          insurance,
          status,
          b.remarks ?? ''
        ]);
      });

      const csv = csvRows.map(r => r.map(v => {
        const s = String(v ?? '');
        return /[",\n]/.test(s) ? `"${s.replace(/"/g,'""')}"` : s;
      }).join(',')).join('\n');

      const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = 'hms-report.csv';
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(url);
    } catch (e) {
      alertJson('Export failed', e);
    }
  });
}

function uiInitAdminReporting() {
  // SETTINGS
  uiLoadSettings();
  document.getElementById('reloadSettingsBtn')?.addEventListener('click', uiLoadSettings);
  document.getElementById('settingsForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    try {
      const body = {
        name: document.getElementById('set_name').value,
        timezone: document.getElementById('set_tz').value,
        locale: document.getElementById('set_locale').value,
        emailEnabled: document.getElementById('set_email').value === 'true',
        supportEmail: document.getElementById('set_support').value
      };
      await API.put('/admin/settings', body);
      alert('Settings saved');
    } catch (err) { alertJson('Failed to save settings', err); }
  });

  // USERS
  uiLoadUsers();
  document.getElementById('addUserForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    try {
      const body = {
        name: document.getElementById('u_name').value,
        email: document.getElementById('u_email').value,
        role: document.getElementById('u_role').value,
        password: document.getElementById('u_password').value,
        enabled: true,
        active: true,
        status: 'ENABLED'
      };
      await API.post('/admin/users', body);
      await uiLoadUsers();
      e.target.reset();
    } catch (err) { alertJson('Create failed', err); }
  });

  // REPORTS
  uiWireReportsTab();

  // ANALYTICS
  document.getElementById('reloadAnalyticsBtn')?.addEventListener('click', uiLoadMonthlyActivity);
  uiLoadMonthlyActivity();
}

/* ================== ANALYTICS (Monthly) ================== */
async function uiLoadMonthlyActivity() {
  try {
    const rowsRaw = await API.get('/billing');
    const rows = Array.isArray(rowsRaw) ? rowsRaw : (rowsRaw?.data || rowsRaw?.content || []);

    // normalize to yyyy-mm
    const toYm = (s) => {
      if (!s) return '';
      // dd-mm-yyyy -> yyyy-mm
      if (s.indexOf('-') === 2) { const [d, m, y] = s.split('-'); return `${y}-${m}`; }
      // yyyy-mm-dd -> yyyy-mm
      if (s.indexOf('-') === 4) { return s.slice(0, 7); }
      return '';
    };

    const monthly = {};
    (rows || []).forEach(b => {
      const ym = toYm(b.date);
      if (!ym) return;
      monthly[ym] = (monthly[ym] || 0) + (Number(b.amount) || 0);
    });

    const box = document.getElementById('monthlyActivityBox');
    if (!box) return;

    const items = Object.entries(monthly).sort((a, b) => a[0].localeCompare(b[0]));
    if (!items.length) { box.innerHTML = '<p class="text-muted m-0">No data.</p>'; return; }

    box.innerHTML = items.map(([ym, total]) =>
      `<div class="d-flex justify-content-between py-1">
         <span>${ym}</span><strong>${total}</strong>
       </div>`).join('');
  } catch (e) {
    alertJson('Failed to load analytics', e);
  }
}/* =========================================================
 /* =========================================================
   STEP 4: LOAD MY APPOINTMENTS (PATIENT DASHBOARD)
   ========================================================= */
async function loadMyAppointments() {
  try {
    const user = JSON.parse(sessionStorage.getItem("user"));
    if (!user || !user.id) {
      console.warn("User not logged in");
      return;
    }

    const data = await API.get("/appointments");

    const myAppts = (data || []).filter(a =>
      (a.patient?.id || a.patientId) === user.id
    );

    const tbody = document.querySelector("#myAppointmentsTable tbody");
    if (!tbody) return;

    tbody.innerHTML = myAppts.map(a => `
      <tr>
        <td>${a.id}</td>
        <td>${a.date}</td>
        <td>${a.doctor?.name || '-'}</td>
        <td>${a.slot || '-'}</td>
        <td>${a.status}</td>
        <td>
          ${a.status === "SCHEDULED"
            ? `<button class="btn btn-sm btn-danger"
                onclick="cancelAppointment(${a.id})">
                Cancel
              </button>`
            : "-"}
        </td>
      </tr>
    `).join("");

  } catch (err) {
    alertJson("Failed to load my appointments", err);
  }
}

/* =========================================================
   STEP 5: CANCEL APPOINTMENT
   ========================================================= */
async function cancelAppointment(apptId) {
  if (!confirm("Cancel this appointment?")) return;

  try {
    await API.put(`/appointments/${apptId}`, {
      status: "CANCELLED"
    });

    alert("Appointment cancelled");
    loadMyAppointments();

  } catch (err) {
    alertJson("Cancel failed", err);
  }
}
/* ================== STEP 4: LOAD DOCTORS ================== */
async function loadDoctors() {
  try {
    const doctors = await API.get("/doctors");

    const table = document.getElementById("doctorsTable");
    if (!table) return;

    table.innerHTML = doctors.map(d => `
      <tr>
        <td>${d.name}</td>
        <td>${d.specialization}</td>
        <td>
          <button class="btn btn-primary btn-sm"
            onclick="openBooking(${d.id}, '${d.name}')">
            Book
          </button>
        </td>
      </tr>
    `).join("");

  } catch (err) {
    console.error(err);
    alert("Failed to load doctors");
  }
}
async function openBooking(doctorId, doctorName) {
  try {
    document.getElementById("doctorId").value = doctorId;
    document.getElementById("doctorName").value = doctorName;

    const slots = await API.get(`/doctors/${doctorId}/slots`);

    const slotSelect = document.getElementById("slotSelect");
    slotSelect.innerHTML = slots.map(s =>
      `<option value="${s}">${s}</option>`
    ).join("");

    new bootstrap.Modal(
      document.getElementById("bookingModal")
    ).show();

  } catch (err) {
    alertJson("Failed to load slots", err);
  }
}
async function bookAppointment() {
  try {
    const user = JSON.parse(localStorage.getItem("user"));
    if (!user || !user.id) throw "Patient not logged in";

    const body = {
      doctorId: +document.getElementById("doctorId").value,
      patientId: user.id,
      date: isoToDMY(document.getElementById("appointmentDate").value),
      slot: document.getElementById("slotSelect").value,
      status: "SCHEDULED"
    };

    await API.post("/appointments", body);

    alert("✅ Appointment booked successfully");
    location.reload();

  } catch (err) {
    alertJson("Booking failed", err);
  }
}
document.addEventListener("DOMContentLoaded", () => {
  loadDoctors();
  loadMyAppointments();
});
