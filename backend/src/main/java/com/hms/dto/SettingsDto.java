package com.hms.dto;

public class SettingsDto {
    private String name;
    private String timezone;
    private String locale;
    private boolean emailEnabled;
    private String supportEmail;

    public SettingsDto() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    public String getLocale() { return locale; }
    public void setLocale(String locale) { this.locale = locale; }

    public boolean isEmailEnabled() { return emailEnabled; }
    public void setEmailEnabled(boolean emailEnabled) { this.emailEnabled = emailEnabled; }

    public String getSupportEmail() { return supportEmail; }
    public void setSupportEmail(String supportEmail) { this.supportEmail = supportEmail; }
}
