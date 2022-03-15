package com.QueensKitchenSpringBoot.controllers.UserProfilePic.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import com.QueensKitchenSpringBoot.controllers.UserProfilePic.Entity.ProfilePic;
import com.QueensKitchenSpringBoot.controllers.UserProfilePic.Repository.ProfilePicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    @Autowired
    private ProfilePicRepository fileDBRepository;

    public ProfilePic store(MultipartFile file,Long user_id) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        ProfilePic FileDB = new ProfilePic(fileName, file.getContentType(), file.getBytes(),user_id);

        return fileDBRepository.save(FileDB);
    }


    public ProfilePic getFile(String id) {
        return fileDBRepository.findById(id).get();
    }

    public List<ProfilePic> getUserPic(Long user_id){
        return fileDBRepository.findByUserId(user_id);
    }
    public Stream<ProfilePic> getAllFiles() {
        return fileDBRepository.findAll().stream();
    }
}