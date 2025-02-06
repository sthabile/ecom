package com.pc.ecom.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    /**
     * Uploads file to a directory and returns the file name
     * @param path
     * @param imageFile
     * @return
     * @throws IOException
     */
    @Override
    public String uploadImage(String path, MultipartFile imageFile) throws IOException {
        String originalFileName = imageFile.getOriginalFilename();

        String uniqueFileName = UUID.randomUUID().toString();
        String fileName = uniqueFileName.concat(originalFileName.substring(originalFileName.lastIndexOf(".")));
        String filePath = path + File.separator + fileName;

        File folder = new File(path);
        if(!folder.exists()){
            folder.mkdirs();
        }

        Files.copy(imageFile.getInputStream(), Paths.get(filePath));

        return fileName;
    }
}
