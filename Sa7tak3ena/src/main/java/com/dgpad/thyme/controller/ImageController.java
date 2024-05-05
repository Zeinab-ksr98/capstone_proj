package com.dgpad.thyme.controller;

import com.dgpad.thyme.model.Image;
import com.dgpad.thyme.service.UserComplements.HSectionsService;
import com.dgpad.thyme.service.UserComplements.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ImageController {
    @Autowired
    private ImageService imageService;
    @GetMapping(value = "/image/{imageId}")
    public ResponseEntity<byte[]> getProductImage(@PathVariable Long imageId) {
        Image image =imageService.getImage(imageId);
        if (image != null) {
            byte[] imageBytes = java.util.Base64.getDecoder().decode(image.getImage());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new byte[0], HttpStatus.NOT_FOUND);
        }
    }
}
