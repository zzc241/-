package ruiji.ruiji.common;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 文件上传、下载
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${ruiji.path}")
    private String basePath;
    @PostMapping("/upload")
    public R<String> upload(@RequestParam("file") MultipartFile file){
        /**
         *  文件上传
         */
        log.info(file.toString());
        String originalFilename = file.getOriginalFilename();
        String fileName = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString();
        File files = new File(basePath + uuid + fileName);
        if(!files.exists())files.mkdirs();
        try {
            file.transferTo(files);
        }catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(uuid + fileName);
    }


    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){ 

        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(basePath + name);

            ServletOutputStream outputStream = response.getOutputStream();

            int len = 0;
            byte[] bytes = new byte[1024];
            while((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            outputStream.close();
            fileInputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
