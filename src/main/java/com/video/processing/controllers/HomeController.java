package com.video.processing.controllers;

import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import org.slf4j.Logger;

@RestController
@RequestMapping("/api")
public class HomeController {
    private final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @GetMapping("/home")
    public ResponseEntity<?> home(){
        return ResponseEntity.status(HttpStatus.OK)
                .body("Welcome to video processing...");
    }

    @PostMapping(value = "/process-video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String processVideo(@RequestParam("file") MultipartFile file)
            throws IOException, InterruptedException {

        String filename = file.getOriginalFilename();
        String inputPath = "src/main/resources/static/" + filename;
        String outputDir = "src/main/resources/static/hls/";

        file.transferTo(new java.io.File(inputPath));

        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg", "-i", inputPath,
                "-profile:v", "baseline", "-level", "3.0",
                "-start_number", "0",
                "-hls_time", "10",
                "-hls_list_size", "0",
                "-f", "hls", outputDir + "output.m3u8"
        );

        pb.inheritIO();
        Process process = pb.start();
        process.waitFor();

        return "Uploaded and processed successfully. HLS generated at /static/hls/output.m3u8";
    }

    public static class VideoRequest {
        private String filename;
        public String getFilename() { return filename; }
        public void setFilename(String filename) { this.filename = filename; }
    }
}
