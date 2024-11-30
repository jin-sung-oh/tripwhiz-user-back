package com.tripwhiz.tripwhizuserback.luggage.controller;

import com.tripwhiz.tripwhizuserback.luggage.dto.LuggageDTO;
import com.tripwhiz.tripwhizuserback.luggage.service.LuggageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/luggage")
public class LuggageController {

    @Autowired
    private LuggageService luggageService;

    @PostMapping("/saveLuggage")
    public String saveLuggage(@RequestBody LuggageDTO luggageDTO) {
        luggageService.saveLuggage(luggageDTO);
        return "Luggage saved successfully for user: " + luggageDTO.getEmail();  // name 대신 email로 변경
    }
}