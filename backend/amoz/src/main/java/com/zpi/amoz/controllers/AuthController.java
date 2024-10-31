package com.zpi.amoz.controllers;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.zpi.amoz.models.User;
import com.zpi.amoz.requests.VerifyTokenRequest;
import com.zpi.amoz.responses.MessageResponse;
import com.zpi.amoz.services.UserService;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    @Value("${spring.security.oauth2.resource-server.opaque-token.client-id}")
    private String clientId;

    @Autowired
    private OpaqueTokenIntrospector introspector;

    @Autowired
    private UserService userService;

    @PostMapping("/auth/verify-token")
    public ResponseEntity<MessageResponse> verifyToken(@RequestBody VerifyTokenRequest request) {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(clientId))
                .build();

        GoogleIdToken idToken;
        try {
            idToken = verifier.verify(request.tokenId());
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                String userId = payload.getSubject();
//                String email = payload.getEmail();
//                String fullName = (String) payload.get("name");
//
//                Optional<User> user = userService.findById(userId);
//
//                if (user.isPresent()) {
//                    return ResponseEntity.ok(new MessageResponse("Logged in successfully!" + user.get().get()));
//                } else {
//                    String[] nameParts = fullName.split(" ");
//                    String firstName = nameParts[0];
//                    String lastName = nameParts.length > 1 ? nameParts[nameParts.length - 1] : "";
//
//                    User newUser = new User();
//                    newUser.setUserId(userId);
//                    newUser.setName(firstName);
//                    newUser.setSurname(lastName);
//                    newUser.setEmail(email);
//
//                    userService.saveUser(newUser);
//
//                    return ResponseEntity.ok(new MessageResponse("New user created " + newUser.getName()));
//                }
                return ResponseEntity.ok(new MessageResponse("User logged in " + userId));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Invalid ID token"));
            }
        } catch (GeneralSecurityException | IOException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Error during token validation: " + e.getMessage()));
        }
    }
}
