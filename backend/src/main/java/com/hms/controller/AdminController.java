package com.hms.controller;

import com.hms.dto.SettingsDto;
import com.hms.dto.UserCreateRequest;
import com.hms.dto.UserStatusRequest;
import com.hms.entity.User;
import com.hms.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepo;
    public AdminController(UserRepository userRepo) { this.userRepo = userRepo; }

    // ---------- SETTINGS (in-memory demo) ----------
    private final AtomicReference<SettingsDto> SETTINGS = new AtomicReference<>();
    {
        SettingsDto d = new SettingsDto();
        d.setName("HMS Demo");
        d.setTimezone("Asia/Kolkata");
        d.setLocale("en-IN");
        d.setEmailEnabled(true);
        d.setSupportEmail("support@hospital.com");
        SETTINGS.set(d);
    }

    @GetMapping("/settings")
    public SettingsDto getSettings() { return SETTINGS.get(); }

    // accept both POST and PUT from the client
    @RequestMapping(value = "/settings", method = {RequestMethod.POST, RequestMethod.PUT})
    public SettingsDto saveSettings(@RequestBody SettingsDto dto) {
        SettingsDto prev = SETTINGS.get();

        SettingsDto merged = new SettingsDto();
        merged.setName(         dto.getName()        != null ? dto.getName()        : prev.getName());
        merged.setTimezone(     dto.getTimezone()    != null ? dto.getTimezone()    : prev.getTimezone());
        merged.setLocale(       dto.getLocale()      != null ? dto.getLocale()      : prev.getLocale());
        merged.setEmailEnabled( dto.isEmailEnabled() );
        merged.setSupportEmail( dto.getSupportEmail()!= null ? dto.getSupportEmail(): prev.getSupportEmail());

        SETTINGS.set(merged);
        return merged;
    }

    // ---------- USERS ----------
    @GetMapping("/users")
    public List<User> users() { return userRepo.findAll(); }

    @PostMapping("/users")
    public User create(@RequestBody UserCreateRequest in) {
        User u = new User();

        // required fields – try setter, else write the field directly
        set(u, "setName",      "name",      in.name);
        set(u, "setEmail",     "email",     in.email);
        set(u, "setUsername",  "username",  in.email);   // many schemas use username as email
        set(u, "setRole",      "role",      in.role);
        set(u, "setEnabled",   "enabled",   Boolean.TRUE);
        set(u, "setActive",    "active",    Boolean.TRUE);
        set(u, "setPassword",  "password",  in.password);
        set(u, "setPasswordHash","passwordHash", in.password);

        return userRepo.save(u);
    }

    @PatchMapping("/users/{id}/status")
    public User toggle(@PathVariable Long id, @RequestBody UserStatusRequest in) {
        User u = userRepo.findById(id).orElseThrow();
        Boolean enabled = in.enabled != null ? in.enabled : Boolean.TRUE;
        set(u, "setEnabled", "enabled", enabled);
        set(u, "setActive",  "active",  enabled);
        return userRepo.save(u);
    }

    /** Try setter first; if missing, set the field directly. */
    private static void set(Object target, String setterName, String fieldName, Object value) {
        try {
            // 1) setter?
            for (Method m : target.getClass().getMethods()) {
                if (m.getName().equals(setterName) && m.getParameterCount() == 1) {
                    m.invoke(target, value);
                    return;
                }
            }
            // 2) public field?
            try {
                Field f = target.getClass().getField(fieldName);
                f.set(target, value);
                return;
            } catch (NoSuchFieldException ignored) {}
            // 3) private field?
            try {
                Field f = target.getClass().getDeclaredField(fieldName);
                f.setAccessible(true);
                f.set(target, value);
            } catch (NoSuchFieldException ignored) {}
        } catch (Exception ignored) {}
    }
}
