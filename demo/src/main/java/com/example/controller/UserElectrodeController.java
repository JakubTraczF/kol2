package com.example.controller;

import com.example.model.UserElectrode;
import com.example.repository.UserElectrodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserElectrodeController {

    @Autowired
    private UserElectrodeRepository userElectrodeRepository;

    @GetMapping("/user-electrode")
    public String getUserElectrode(
            @RequestParam("username") String username,
            @RequestParam("electrodeNumber") int electrodeNumber,
            Model model) {

        // Wyszukaj odpowiedni wiersz w bazie danych
        UserElectrode userElectrode = userElectrodeRepository.findByUsernameAndElectrodeNumber(username, electrodeNumber);

        // Przekazanie danych do widoku
        model.addAttribute("username", userElectrode.getUsername());
        model.addAttribute("electrodeNumber", userElectrode.getElectrodeNumber());
        model.addAttribute("image", userElectrode.getImage()); // Załóżmy, że obrazek jest przechowywany jako string URL

        // Zwróć nazwę widoku (HTML template)
        return "userElectrodeDetails"; // Nazwa pliku HTML w resources/templates
    }
}
