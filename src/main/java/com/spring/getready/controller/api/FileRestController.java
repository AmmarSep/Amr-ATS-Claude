package com.spring.getready.controller.api;

import com.spring.getready.model.UploadFile;
import com.spring.getready.repository.UploadFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Optional;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FileRestController {

    @Autowired
    private UploadFileRepository uploadFileRepository;

    @Value("${file.upload-path}")
    private String uploadPath;

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Integer id) {
        try {
            Optional<UploadFile> fileOpt = uploadFileRepository.findById(id);
            if (!fileOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            UploadFile uploadFile = fileOpt.get();
            File file = new File(uploadPath + File.separator + uploadFile.getFileName());

            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }

            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + uploadFile.getFileOriginalName() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(file.length())
                    .body(resource);
        } catch (FileNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{id}/view")
    public ResponseEntity<Resource> viewFile(@PathVariable Integer id) {
        try {
            Optional<UploadFile> fileOpt = uploadFileRepository.findById(id);
            if (!fileOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            UploadFile uploadFile = fileOpt.get();
            File file = new File(uploadPath + File.separator + uploadFile.getFileName());

            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }

            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

            // Determine media type based on file extension
            String fileName = uploadFile.getFileOriginalName().toLowerCase();
            MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;

            if (fileName.endsWith(".pdf")) {
                mediaType = MediaType.APPLICATION_PDF;
            } else if (fileName.endsWith(".doc") || fileName.endsWith(".docx")) {
                mediaType = MediaType.valueOf("application/msword");
            } else if (fileName.endsWith(".txt")) {
                mediaType = MediaType.TEXT_PLAIN;
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + uploadFile.getFileOriginalName() + "\"")
                    .contentType(mediaType)
                    .contentLength(file.length())
                    .body(resource);
        } catch (FileNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
