package com.example.smartcampus.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class ApiRootResponse {
    private String name;
    private String version;
    private String contact;
    private Map<String, String> links;

    public ApiRootResponse() {
    }

    public ApiRootResponse(String name, String version, String contact) {
        this.name = name;
        this.version = version;
        this.contact = contact;
        this.links = new LinkedHashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    public void setLinks(Map<String, String> links) {
        this.links = links;
    }
}
