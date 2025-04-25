package com.bajaj.webhookapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebhookResponse {
    private String webhook;
    private String accessToken;
    private Data data;

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {
        private User[] users;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class User {
        private int id;
        private String name;
        private int[] follows;
    }
} 