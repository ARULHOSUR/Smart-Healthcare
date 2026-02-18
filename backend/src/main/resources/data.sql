/* =====================================================
   CLEAN + SEED with FK checks OFF (whole seed section)
   ===================================================== */



-- If tables may not exist yet, H2 ignores TRUNCATE errors, but we do it anyway
TRUNCATE TABLE lab_result;
TRUNCATE TABLE lab_order;
TRUNCATE TABLE emr;
TRUNCATE TABLE billing;
TRUNCATE TABLE appointment;
TRUNCATE TABLE doctor;
TRUNCATE TABLE medicine;
TRUNCATE TABLE lab_test;
TRUNCATE TABLE staff;
TRUNCATE TABLE patients;

-- Hard reset identities so first insert gets ID = 1
ALTER TABLE patients     ALTER COLUMN id RESTART WITH 1;
ALTER TABLE staff        ALTER COLUMN id RESTART WITH 1;
ALTER TABLE lab_test     ALTER COLUMN id RESTART WITH 1;
ALTER TABLE medicine     ALTER COLUMN id RESTART WITH 1;
ALTER TABLE doctor       ALTER COLUMN id RESTART WITH 1;
ALTER TABLE appointment  ALTER COLUMN id RESTART WITH 1;
ALTER TABLE billing      ALTER COLUMN id RESTART WITH 1;
ALTER TABLE emr          ALTER COLUMN id RESTART WITH 1;
ALTER TABLE lab_order    ALTER COLUMN id RESTART WITH 1;
ALTER TABLE lab_result   ALTER COLUMN id RESTART WITH 1;

/* -------------------------
   SEED MASTER/BASE TABLES
   ------------------------- */

-- PATIENTS
INSERT INTO patients (name, age, gender) VALUES
('Alice', 30, 'F'),
('Bob',   42, 'M');

-- STAFF
INSERT INTO staff (name, role, shift) VALUES
('Dr. Andrew',  'Doctor', 'Day'),
('Nurse Kelly', 'Nurse',  'Night');

-- LAB TESTS
INSERT INTO lab_test (category, code, name, price) VALUES
('Hematology',  'CBC', 'Complete Blood Count', 350.00),
('Biochemistry','BMP', 'Basic Metabolic Panel', 500.00),
('Biochemistry','LFT', 'Liver Function Test',  600.00);

-- MEDICINES
INSERT INTO medicine (expiry_date, manufacturer, name, price, stock) VALUES
('2026-12-31', 'Pfizer',      'Amoxicillin',        25.00, 100),
('2026-06-30', 'Cipla',       'Ibuprofen',          15.00, 200),
('2027-03-31', 'HealthCorp',  'Paracetamol 500mg',  12.00, 90);

-- DOCTORS
INSERT INTO doctor (name, specialization) VALUES
(1, 'Dr. Arun Kumar', 'Cardiology'),
(2, 'Dr. Meena', 'Dermatology'),
(3, 'Dr. Ravi Shankar', 'Neurology'),
(4, 'Dr. Priya Singh', 'Orthopedics');


-- EMR (needs patient 1 to exist)
INSERT INTO emr (patient_id, diagnosis, treatment_plan, visit_date, notes) VALUES
(1, 'Fever', 'Paracetamol 500mg', '2025-10-24', 'Monitor temperature daily.');

-- LAB ORDERS (needs patient 1 and test 1)
INSERT INTO lab_order (patient_id, test_id, status, ordered_date) VALUES
(1, 1, 'Pending', '2025-10-24');

-- LAB RESULTS (needs order 1)
INSERT INTO lab_result (order_id, result_value, reference_range, interpretation, result_date) VALUES
(1, '13.5 g/dL', '13-17', 'Normal', '2025-10-25');

/* -------------------------------------------
   FK CONSTRAINTS WITH ON DELETE CASCADE
   (drop/recreate to ensure cascade behavior)
   ------------------------------------------- */

-- Appointments → Patients
ALTER TABLE appointment DROP CONSTRAINT IF EXISTS FK_APPOINTMENT_PATIENT;
ALTER TABLE appointment
  ADD CONSTRAINT FK_APPOINTMENT_PATIENT
  FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE;

-- EMR → Patients
ALTER TABLE emr DROP CONSTRAINT IF EXISTS FK_EMR_PATIENT;
ALTER TABLE emr
  ADD CONSTRAINT FK_EMR_PATIENT
  FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE;

-- Lab Orders → Patients
ALTER TABLE lab_order DROP CONSTRAINT IF EXISTS FK_LAB_ORDER_PATIENT;
ALTER TABLE lab_order
  ADD CONSTRAINT FK_LAB_ORDER_PATIENT
  FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE;

-- Billing → Patients
ALTER TABLE billing DROP CONSTRAINT IF EXISTS FK_BILLING_PATIENT;
ALTER TABLE billing
  ADD CONSTRAINT FK_BILLING_PATIENT
  FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE;

-- Lab Results → Lab Orders
ALTER TABLE lab_result DROP CONSTRAINT IF EXISTS FK_LAB_RESULT_ORDER;
ALTER TABLE lab_result
  ADD CONSTRAINT FK_LAB_RESULT_ORDER
  FOREIGN KEY (order_id) REFERENCES lab_order(id) ON DELETE CASCADE;

-- Done seeding, turn FK checks back on
SET REFERENTIAL_INTEGRITY TRUE;

/* -------------------------------------------
   Make every table’s next id = MAX(id)+1
   (so new rows keep counting 1,2,3,…)
   ------------------------------------------- */
ALTER TABLE patients     ALTER COLUMN id RESTART WITH (SELECT COALESCE(MAX(id),0)+1 FROM patients);
ALTER TABLE staff        ALTER COLUMN id RESTART WITH (SELECT COALESCE(MAX(id),0)+1 FROM staff);
ALTER TABLE lab_test     ALTER COLUMN id RESTART WITH (SELECT COALESCE(MAX(id),0)+1 FROM lab_test);
ALTER TABLE medicine     ALTER COLUMN id RESTART WITH (SELECT COALESCE(MAX(id),0)+1 FROM medicine);
ALTER TABLE doctor       ALTER COLUMN id RESTART WITH (SELECT COALESCE(MAX(id),0)+1 FROM doctor);
ALTER TABLE appointment  ALTER COLUMN id RESTART WITH (SELECT COALESCE(MAX(id),0)+1 FROM appointment);
ALTER TABLE billing      ALTER COLUMN id RESTART WITH (SELECT COALESCE(MAX(id),0)+1 FROM billing);
ALTER TABLE emr          ALTER COLUMN id RESTART WITH (SELECT COALESCE(MAX(id),0)+1 FROM emr);
ALTER TABLE lab_order    ALTER COLUMN id RESTART WITH (SELECT COALESCE(MAX(id),0)+1 FROM lab_order);
ALTER TABLE lab_result   ALTER COLUMN id RESTART WITH (SELECT COALESCE(MAX(id),0)+1 FROM lab_result);
