package com.apiGenerator.validator;

import com.apiGenerator.model.ApiSpecification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import  java.util.Map;

@Component
public class JsonValidator {

    public List<String> validate(ApiSpecification spec) {
        List<String> errors = new ArrayList<>();

        if (spec.getBaseClassName() == null || spec.getBaseClassName().isEmpty()) {
            errors.add("BaseClassName is required.");
        }

        if (spec.getEndpoints() == null || spec.getEndpoints().isEmpty()) {
            errors.add("Endpoints are required.");
        } else {
            for (Map<String, Object> endpoint : spec.getEndpoints()) {
                if (!endpoint.containsKey("Path") || endpoint.get("Path").toString().isEmpty()) {
                    errors.add("Each endpoint must have a valid Path.");
                }
                if (!endpoint.containsKey("Methods")) {
                    errors.add("Each endpoint must specify Methods.");
                }
            }
        }

        return errors;
    }
}
