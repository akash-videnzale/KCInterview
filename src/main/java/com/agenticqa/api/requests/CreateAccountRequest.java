package com.agenticqa.api.requests;

import com.agenticqa.data.models.RegistrationData;

import java.util.LinkedHashMap;
import java.util.Map;

public final class CreateAccountRequest {

    private CreateAccountRequest() {
    }

    public static Map<String, Object> from(RegistrationData data) {
        Map<String, Object> form = new LinkedHashMap<>();
        form.put("name", data.name());
        form.put("email", data.email());
        form.put("password", data.password());
        form.put("title", "Mr");
        form.put("birth_date", data.birthDay());
        form.put("birth_month", data.birthMonth());
        form.put("birth_year", data.birthYear());
        form.put("firstname", data.firstName());
        form.put("lastname", data.lastName());
        form.put("company", data.company());
        form.put("address1", data.address1());
        form.put("address2", data.address2());
        form.put("country", data.country());
        form.put("zipcode", data.zipcode());
        form.put("state", data.state());
        form.put("city", data.city());
        form.put("mobile_number", data.mobileNumber());
        return form;
    }
}
