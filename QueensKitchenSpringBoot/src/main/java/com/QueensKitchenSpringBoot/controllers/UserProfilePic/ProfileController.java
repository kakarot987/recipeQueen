package com.QueensKitchenSpringBoot.controllers.UserProfilePic;

import com.QueensKitchenSpringBoot.controllers.Authentication.security.UserDetailsImpl;
import com.QueensKitchenSpringBoot.controllers.UserProfilePic.Entity.ProfilePic;
import com.QueensKitchenSpringBoot.controllers.UserProfilePic.Service.FileStorageService;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/me/profile-photo")
public class ProfileController {
    private final FileStorageService service;
    public ProfileController(FileStorageService service) { this.service = service; }
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void upload(@AuthenticationPrincipal UserDetailsImpl user,
        @RequestPart("file") MultipartFile file) throws IOException {
        service.saveMine(user.getId(), file);
    }
    @GetMapping
    public ResponseEntity<byte[]> image(@AuthenticationPrincipal UserDetailsImpl user) {
        ProfilePic picture = service.mine(user.getId());
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(picture.getType()))
            .cacheControl(CacheControl.noStore()).body(picture.getData());
    }
}
