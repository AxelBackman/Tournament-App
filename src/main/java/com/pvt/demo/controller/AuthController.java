package com.pvt.demo.controller;

import java.util.Collections;

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
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier
                    .Builder(GoogleNetHttpTransport.newTrustedTransport(), com.google.api.client.json.gson.GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList("570496735293-ag91l8e5p1vf9dbk1nout9qrqcdlvt0t.apps.googleusercontent.com"))
                    .build();

            GoogleIdToken idToken = verifier.verify(dto.idToken);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                String email = payload.getEmail();
                String name = (String) payload.get("name");

                User user = userRepository.findByEmail(email);

                if (user == null) {
                    Organisation organisation = organisationRepository.findById(1L).orElse(null);

                    if (organisation == null) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Standardorganisation saknas");
                    }

                    user = new User(name, email, organisation);
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
}
