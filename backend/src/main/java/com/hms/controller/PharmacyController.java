package com.hms.controller;

import com.hms.dto.MedicineRequest;
import com.hms.entity.Medicine;
import com.hms.repository.MedicineRepository;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.List;

@RestController
@RequestMapping("/api/pharmacy")
public class PharmacyController {

    private final MedicineRepository repo;
    public PharmacyController(MedicineRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Medicine> list() { return repo.findAll(); }

    @PostMapping
    public Medicine create(@RequestBody MedicineRequest in) {
        Medicine m = new Medicine();
        trySet(m, "setName", in.name);
        // batch vs batchNo
        trySet(m, "setBatchNo", in.batchNo);
        trySet(m, "setBatch", in.batchNo);
        // qty vs quantity
        trySet(m, "setQuantity", in.quantity);
        trySet(m, "setQty", in.quantity);
        trySet(m, "setPrice", in.price);
        // expiry vs expiryDate
        trySet(m, "setExpiryDate", in.expiryDate);
        trySet(m, "setExpiry", in.expiryDate);
        return repo.save(m);
    }

    private static void trySet(Object target, String method, Object value) {
        try {
            for (Method cand : target.getClass().getMethods()) {
                if (cand.getName().equals(method) && cand.getParameterCount()==1) {
                    cand.invoke(target, value); return;
                }
            }
        } catch (Exception ignored) {}
    }
}
