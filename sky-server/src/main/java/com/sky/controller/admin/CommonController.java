package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController("adminCommonController")
@RequestMapping("/admin/common")
@Api(value = "通用接口", tags = "通用接口")
@Slf4j
public class CommonController {
    // 自动注入AliOssUtil对象，用于后续的文件上传操作
    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 处理文件上传请求
     * 该方法接收一个文件作为参数，尝试将文件上传到阿里云OSS，并返回上传结果
     *
     * @param file 用户上传的文件，包含文件名和文件内容
     * @return 返回一个Result对象，其中包含上传文件的URL，表示上传成功；如果上传失败，则返回null
     */
    @PostMapping("/upload")
    @ApiOperation(value = "文件上传", notes = "文件上传")
    public Result<String> upload(MultipartFile file){
        // 记录文件上传的日志信息
        log.info("文件上传：{}", file.toString());

        try {
            // 获取文件的原始名称，用于后续生成唯一的对象名
            String originalFilename = file.getOriginalFilename();
            // 提取文件的后缀名，确保上传后的文件类型不变
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            // 使用UUID生成唯一的对象名，避免文件名冲突，并保留原始文件的后缀名
            String objectName = UUID.randomUUID().toString() + suffix;

            // 调用AliOssUtil的upload方法上传文件，并获取上传后的文件URL
            String url = aliOssUtil.upload(file.getBytes(), originalFilename);

            // 返回上传成功的结果，包含文件的URL
            return Result.success(url);
        } catch (IOException e) {
            // 如果上传过程中发生IO异常，则记录错误日志
            log.error("文件上传失败：{}", e);
        }

        // 如果上传失败，则返回null
        return null;
    }
}
