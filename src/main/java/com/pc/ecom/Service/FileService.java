package com.pc.ecom.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface FileService {

    public String uploadImage(String path, MultipartFile imageFile) throws IOException;
}
