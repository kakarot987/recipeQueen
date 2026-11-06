package com.QueensKitchenSpringBoot.controllers.UserProfilePic.Service;

import com.QueensKitchenSpringBoot.controllers.UserProfilePic.Entity.ProfilePic;
import com.QueensKitchenSpringBoot.controllers.UserProfilePic.Repository.ProfilePicRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {
    private final ProfilePicRepository repository;
    public FileStorageService(ProfilePicRepository repository) { this.repository = repository; }

    @Transactional
    public void saveMine(Long userId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty() || file.getSize() > 2 * 1024 * 1024)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose a picture up to 2MB");
        String type = file.getContentType();
        if (!"image/png".equals(type) && !"image/jpeg".equals(type))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PNG or JPEG required");
        byte[] bytes = file.getBytes();
        if (ImageIO.read(new ByteArrayInputStream(bytes)) == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid picture");
        List<ProfilePic> existing = repository.findByUserId(userId);
        ProfilePic picture;
        if (existing.isEmpty()) {
            picture = new ProfilePic("profile", type, bytes, userId);
            picture.setProfile_id(UUID.randomUUID().toString());
        } else {
            picture = existing.get(0);
            picture.setName("profile");
            picture.setType(type);
            picture.setData(bytes);
        }
        repository.save(picture);
    }

    @Transactional(readOnly = true)
    public ProfilePic mine(Long userId) {
        return repository.findByUserId(userId).stream().findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No picture"));
    }
}
