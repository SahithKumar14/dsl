package com.apiGenerator.model;

import java.util.List;
import java.util.Map;

public class ApiSpecification {
    private String baseClassName;
    private List<Map<String, Object>> endpoints;
    private List<String> httpMethods;

    // Getters and Setters
    public String getBaseClassName() {
        return baseClassName;
    }

    public void setBaseClassName(String baseClassName) {
        this.baseClassName = baseClassName;
    }

    public List<Map<String, Object>> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<Map<String, Object>> endpoints) {
        this.endpoints = endpoints;
    }

    public List<String> getHttpMethods() {
        return httpMethods;
    }

    public void setHttpMethods(List<String> httpMethods) {
        this.httpMethods = httpMethods;
    }
}
