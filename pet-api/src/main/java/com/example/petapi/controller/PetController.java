package com.example.petapi.controller;

import com.example.petapi.model.Pet;
import com.example.petapi.model.PetCreatedResponse;
import com.example.petapi.service.PetService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pet")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping("/{petId}")
    public Pet getPet(@PathVariable Long petId) {
        return petService.getPet(petId);
    }

    @PostMapping
    public PetCreatedResponse createPet(@RequestBody Pet pet) {
        return petService.createPet(pet);
    }
}
