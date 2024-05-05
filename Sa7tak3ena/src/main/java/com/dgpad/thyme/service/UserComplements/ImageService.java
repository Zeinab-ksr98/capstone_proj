package com.dgpad.thyme.service.UserComplements;


import com.dgpad.thyme.model.Image;
import com.dgpad.thyme.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class ImageService {
    @Autowired
    private ImageRepository imageRepository;

    public List<Image> saveImages (List<MultipartFile> files) throws IOException {
        List< Image> images= new ArrayList<>();
        for (MultipartFile file: files){
            Image image =new Image();
            image.setImage(Base64.getEncoder().encodeToString(file.getBytes()));
            images.add(imageRepository.save(image));
        }
        return images;
    }
    public Image getImage(Long id){
        Optional<Image> image = imageRepository.findById(id);
        if (image.isPresent())
            return image.get();
        return null;
    }
    public List<Image> getImages (List<Long> ids){
        List<Image> images=new ArrayList<>();
        for (Long id:ids) {
            Optional<Image> image = imageRepository.findById(id);
            if (image.isPresent())
                images.add(image.get());
        }
        return images;
    }


}

