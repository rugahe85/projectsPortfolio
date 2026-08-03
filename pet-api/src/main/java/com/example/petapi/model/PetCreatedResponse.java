package com.example.petapi.model;

public record PetCreatedResponse(String transactionId, String dateCreated, String status, String name) {
}
