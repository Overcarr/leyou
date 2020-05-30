package cn.leyou.upload.service;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ImageService {
    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);
    private static final List<String> CONTENT_TYPES = Arrays.asList("image/png","image/gif","image/jpg");

    @Autowired
    private FastFileStorageClient storageClient;

    public String uploadImage(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        // 校验类型是否正确
        String contentType = file.getContentType();
        if(!CONTENT_TYPES.contains(contentType)){
            logger.info("文件类型不合法：{}" + originalFilename);
            return null;
        }
        // 校验内容是否为空
        try {
            BufferedImage read = ImageIO.read(file.getInputStream());
            if(read == null){
                logger.info("文件内容为空:{}" + originalFilename);
                return null;
            }
            // 保存到服务器
            // file.transferTo(new File("E:\\levle5\\leyou\\images\\" + originalFilename));
            String ext = StringUtils.substringAfterLast(originalFilename, ".");
            StorePath storePath = this.storageClient.uploadFile(file.getInputStream(), file.getSize(), ext, null);
            // 生成url地址，返回
            return "http://image.leyou.com/" + storePath.getFullPath();
        } catch (IOException e) {
            logger.info("服务器内部发送错误:{}" + originalFilename);
            e.printStackTrace();
        }
        return  null;
    }
}
