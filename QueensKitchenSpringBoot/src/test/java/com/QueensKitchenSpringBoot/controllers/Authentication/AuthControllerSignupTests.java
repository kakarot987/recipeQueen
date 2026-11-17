package com.QueensKitchenSpringBoot.controllers.Authentication;

import com.QueensKitchenSpringBoot.controllers.Authentication.models.ERole;
import com.QueensKitchenSpringBoot.controllers.Authentication.models.Role;
import com.QueensKitchenSpringBoot.controllers.Authentication.models.User;
import com.QueensKitchenSpringBoot.controllers.Authentication.payload.SignupRequest;
import com.QueensKitchenSpringBoot.controllers.Authentication.repository.RoleRepository;
import com.QueensKitchenSpringBoot.controllers.Authentication.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthControllerSignupTests {
    @Test
    void publicSignupIgnoresRequestedAdminRole() {
        AuthController controller = new AuthController();
        controller.userRepository = mock(UserRepository.class);
        controller.roleRepository = mock(RoleRepository.class);
        controller.encoder = mock(PasswordEncoder.class);
        when(controller.roleRepository.findByName(ERole.ROLE_USER))
                .thenReturn(Optional.of(new Role(ERole.ROLE_USER)));
        when(controller.encoder.encode("password123")).thenReturn("encoded-password");

        SignupRequest request = new SignupRequest();
        request.setUsername("tester");
        request.setEmail("tester@example.invalid");
        request.setPassword("password123");
        request.setRole(Set.of("admin", "mod"));
        controller.registerUser(request);

        ArgumentCaptor<User> captured = ArgumentCaptor.forClass(User.class);
        verify(controller.userRepository).save(captured.capture());
        assertEquals(Collections.singleton(ERole.ROLE_USER),
                Collections.singleton(captured.getValue().getRoles().iterator().next().getName()));
        assertEquals(1, captured.getValue().getRoles().size());
        verify(controller.roleRepository, never()).findByName(ERole.ROLE_ADMIN);
    }
}
