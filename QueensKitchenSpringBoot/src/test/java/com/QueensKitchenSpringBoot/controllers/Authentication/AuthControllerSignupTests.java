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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthControllerSignupTests {
    @Test
    void publicSignupAlwaysAssignsUserRoleEvenWhenAdminRequested() {
        AuthController controller = new AuthController();
        UserRepository users = mock(UserRepository.class);
        RoleRepository roles = mock(RoleRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        ReflectionTestUtils.setField(controller, "userRepository", users);
        ReflectionTestUtils.setField(controller, "roleRepository", roles);
        ReflectionTestUtils.setField(controller, "encoder", encoder);
        when(roles.findByName(ERole.ROLE_USER))
                .thenReturn(Optional.of(new Role(ERole.ROLE_USER)));
        when(encoder.encode("password123")).thenReturn("hashed");

        SignupRequest request = new SignupRequest();
        request.setUsername("tester");
        request.setEmail("tester@example.invalid");
        request.setPassword("password123");
        request.setRole(new HashSet<>(Arrays.asList("admin", "mod")));
        controller.signup(request);

        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(users).save(saved.capture());
        assertEquals(1, saved.getValue().getRoles().size());
        assertEquals(ERole.ROLE_USER, saved.getValue().getRoles().iterator().next().getName());
        verify(roles, never()).findByName(ERole.ROLE_ADMIN);
        verify(roles, never()).findByName(ERole.ROLE_MODERATOR);
    }
}
