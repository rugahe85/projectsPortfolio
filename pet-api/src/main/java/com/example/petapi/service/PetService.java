package com.example.petapi.service;

import com.example.petapi.client.PetStoreClient;
import com.example.petapi.model.Pet;
import com.example.petapi.model.PetCreatedResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PetService {

    private final PetStoreClient petStoreClient;

    public PetService(PetStoreClient petStoreClient) {
        this.petStoreClient = petStoreClient;
    }

    public Pet getPet(Long petId) {
        Pet pet = petStoreClient.getPet(petId);
        System.out.println("[GET] Pet obtenido de Petstore: " + pet);
        return pet;
    }

    public PetCreatedResponse createPet(Pet pet) {
        Pet created = petStoreClient.createPet(pet);
        PetCreatedResponse response = new PetCreatedResponse(
                UUID.randomUUID().toString(),          // transactionId (UUID v4)
                LocalDateTime.now().toString(),        // dateCreated (fecha del sistema)
                created.status(),
                created.name());
        System.out.println("[POST] Pet creado en Petstore: " + created);
        System.out.println("[POST] Response generado: " + response);
        return response;
    }
}
