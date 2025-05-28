package com.pvt.demo.controller;

import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.pvt.demo.dto.GoogleTokenDto;
import com.pvt.demo.model.Organisation;
import com.pvt.demo.model.User;
import com.pvt.demo.repository.OrganisationRepository;
import com.pvt.demo.repository.UserRepository;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @PostMapping("/google")
    public ResponseEntity<?> authenticateWithGoogle(@RequestBody GoogleTokenDto dto) {
        try {
            System.out.println("Mottagen idToken: " + dto.getIdToken());
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier
                    .Builder(GoogleNetHttpTransport.newTrustedTransport(), com.google.api.client.json.gson.GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList("522180760468-melhoial2731rkaj0pvsghf0dp7ga2h3.apps.googleusercontent.com"))
                    .build();

            GoogleIdToken idToken = verifier.verify(dto.getIdToken());
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                String email = payload.getEmail();
                String name = (String) payload.get("name");

                Optional<User> optionalUser = userRepository.findByEmail(email);

                User user;
                if (optionalUser.isPresent()) {
                    user = optionalUser.get();
                }
                else {
                    // Om användaren inte finns, skapa en ny med standardorganisation (SK)
                    Organisation organisation = organisationRepository.findById(1L).orElse(null);

                    if (organisation == null) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Standardorganisation saknas");
                    }

                    user = new User(name, email, organisation, false);
                    userRepository.save(user);
                }

                return ResponseEntity.ok("Inloggad som: " + name + " (" + email + ")");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Ogiltig ID-token");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fel vid verifiering: " + e.getMessage());
        }
    }

    @PostMapping("/google/testning")
    public ResponseEntity<?> authenticateWithGoogleTest(@RequestBody GoogleTokenDto dto) {
        try {
            System.out.println("Mottagen idToken: " + dto.getIdToken());
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier
                    .Builder(GoogleNetHttpTransport.newTrustedTransport(), com.google.api.client.json.gson.GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList("570496735293-htssr9kvsj68e0bttaluogihqfah04al.apps.googleusercontent.com"))
                    .setIssuer("https://accounts.google.com")
                    .build();

            GoogleIdToken idToken = verifier.verify(dto.getIdToken());
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                String email = payload.getEmail();
                String name = (String) payload.get("name");

                Optional<User> optionalUser = userRepository.findByEmail(email);

                User user;
                if (optionalUser.isPresent()) {
                    user = optionalUser.get();
                }
                else {
                    Organisation organisation = organisationRepository.findById(1L).orElse(null);

                    if (organisation == null) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Standardorganisation saknas");
                    }

                    user = new User(name, email, organisation, false);
                    userRepository.save(user);
                }

                return ResponseEntity.ok("Inloggad som: " + name + " (" + email + ")");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Ogiltig ID-token" + dto.getIdToken());
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fel vid verifiering: " + e.getMessage());
        }
    }
    
}
