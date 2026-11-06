package com.QueensKitchenSpringBoot.controllers.Authentication;

import com.QueensKitchenSpringBoot.controllers.Authentication.models.*;
import com.QueensKitchenSpringBoot.controllers.Authentication.payload.*;
import com.QueensKitchenSpringBoot.controllers.Authentication.repository.*;
import com.QueensKitchenSpringBoot.controllers.Authentication.security.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PasswordEncoder encoder;
    @Autowired private JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@Valid @RequestBody LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();
        List<String> roles = user.getAuthorities().stream()
            .map(authority -> authority.getAuthority()).collect(Collectors.toList());
        return ResponseEntity.ok(new JwtResponse(jwtUtils.generateJwtToken(auth), user.getId(),
            user.getUsername(), user.getEmail(), roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            return ResponseEntity.badRequest().body(new MessageResponse("Username is already taken"));
        if (userRepository.existsByEmail(request.getEmail()))
            return ResponseEntity.badRequest().body(new MessageResponse("Email is already in use"));
        User user = new User(request.getUsername(), request.getEmail(), encoder.encode(request.getPassword()));
        // Never accept an administrator or moderator role from a public registration request.
        Role role = roleRepository.findByName(ERole.ROLE_USER)
            .orElseGet(() -> roleRepository.save(new Role(ERole.ROLE_USER)));
        user.setRoles(Collections.singleton(role));
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully"));
    }
}
