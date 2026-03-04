package com.hms.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    @Value("${twilio.accountSid}")
    private String accountSid;

    @Value("${twilio.authToken}")
    private String authToken;

    @Value("${twilio.phoneNumber}")
    private String fromNumber;

    public void sendAppointmentSMS(String toPhone,
                                   String doctorName,
                                   String date,
                                   String slot) {

        try {
            Twilio.init(accountSid, authToken);

            String body =
                    "Appointment Confirmed!\n\n" +
                    "Doctor: " + doctorName + "\n" +
                    "Date: " + date + "\n" +
                    "Slot: " + slot + "\n\n" +
                    "Thank you for choosing HMS.";

            Message.creator(
                    new PhoneNumber("+91" + toPhone),
                    new PhoneNumber(fromNumber),
                    body
            ).create();

            System.out.println("SMS SENT TO +91" + toPhone);
        } catch (Exception e) {
            System.out.println("SMS FAILED (non-critical): " + e.getMessage());
            // Don't throw - SMS is not critical for the app to work
        }
    }
}
