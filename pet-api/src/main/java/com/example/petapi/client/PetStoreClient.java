package com.example.petapi.client;

import com.example.petapi.model.Pet;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PetStoreClient {

    private final RestClient restClient = RestClient.create("https://petstore.swagger.io/v2");

    public Pet getPet(Long petId) {
        return restClient.get()
                .uri("/pet/{petId}", petId)
                .retrieve()
                .body(Pet.class);
    }

    public Pet createPet(Pet pet) {
        return restClient.post()
                .uri("/pet")
                .contentType(MediaType.APPLICATION_JSON)
                .body(pet)
                .retrieve()
                .body(Pet.class);
    }
}
