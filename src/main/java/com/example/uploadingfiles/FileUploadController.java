package com.example.uploadingfiles;

import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.List;

import com.example.uploadingfiles.model.Metadata;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

@RestController
public class FileUploadController {

    private final FileSystemStorageService storageService;
    private final MetadataRepository metadataRepository;

    @Autowired
    public FileUploadController(
            FileSystemStorageService storageService,
            MetadataRepository metadataRepository) {
        this.storageService = storageService;
        this.metadataRepository = metadataRepository;
    }

    @GetMapping("/")
    public List<String> listUploadedFiles() {

        return this.storageService.fullList();
    }

    @GetMapping("/{object}")
    public void getObject(@PathVariable("filename") String filename, HttpServletResponse response) throws Exception {
        InputStream inputStream = this.storageService.get(Path.of(filename));

        // Set the content type and attachment header.
        response.addHeader("Content-disposition", "attachment;filename=" + filename);
        response.setContentType(URLConnection.guessContentTypeFromName(filename));

        // Copy the stream to the response's output stream.
        IOUtils.copy(inputStream, response.getOutputStream());
        response.flushBuffer();
    }

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {

        Metadata metadata = new Metadata(file.getOriginalFilename(), file.getContentType());

        metadataRepository.save(metadata);

        storageService.store(file);

        return "Successfully uploaded.";
    }

    @GetMapping("/metadata")
    public List<Metadata> getAll() {

        return this.metadataRepository.findAll();
    }

}