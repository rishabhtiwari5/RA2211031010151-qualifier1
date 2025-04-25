package com.bajaj.webhookapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultPayload {
    private String regNo;
    private int[][] outcome;
} 