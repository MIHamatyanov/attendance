package ru.vmk.attendance.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.vmk.attendance.exception.FileException;
import ru.vmk.attendance.model.User;
import ru.vmk.attendance.service.FileService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    @Value("${custom.file.filePath}")
    private String filePath;

    @Override
    public String savePhoto(MultipartFile file) {
        try {
            byte[] data = file.getBytes();

            String fileName = String.valueOf(System.currentTimeMillis());
            String originalFileName = file.getOriginalFilename();
            assert originalFileName != null;
            String ext = originalFileName.substring(originalFileName.lastIndexOf('.') + 1);
            fileName += "." + ext;

            File fileToSave = new File(filePath + "/" + fileName);
            doWrite(fileToSave.toPath(), data, true);

            return fileName;
        } catch (IOException e) {
            throw new FileException();
        }
    }

    private static void doWrite(Path pathToFile, byte[] contents, boolean overwrite) {
        try {
            Files.createDirectories(pathToFile.getParent());
            if (overwrite)
                Files.write(pathToFile, contents);
            else
                Files.write(pathToFile, contents, StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            throw new FileException();
        }
    }

    @Override
    public byte[] getPhotoByteArray(String filename, User authUser) {
        if (!authUser.getPhotoUrl().equals(filename)) {
            throw new AccessDeniedException("Access denied");
        }

        File file = new File(filePath + "/" + filename);
        InputStream in;
        try {
            in = new FileInputStream(file);
            return IOUtils.toByteArray(in);
        } catch (IOException e) {
            return null;
        }
    }
}
