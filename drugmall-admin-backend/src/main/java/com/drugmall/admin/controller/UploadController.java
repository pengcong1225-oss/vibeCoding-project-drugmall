package com.drugmall.admin.controller;

import com.drugmall.admin.common.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/admin/upload")
public class UploadController {

    @PostMapping
    public Result<Object> upload(@RequestParam("file") MultipartFile file) {
        String filename = file.getOriginalFilename();
        return Result.success(Map.of(
            "url", "https://via.placeholder.com/200x200?text=" + (filename != null ? filename : "file"),
            "name", filename != null ? filename : "file"
        ));
    }
}
