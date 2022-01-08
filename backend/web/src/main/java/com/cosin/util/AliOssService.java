package com.cosin.util;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;


public class AliOssService {
    private static String endpoint = "oss-cn-beijing.aliyuncs.com";
    private static String accessKeyId = "LTAI5tBREsDUBhUzeHVkdHr6";
    private static String accessKeySecret = "tGkdUVbcgNezLi8CJNt6Po6LkoQCAd";
    private static String bucketName = "software-2";
    private static String objectKey = "sec3/";

    public static void upload(MultipartFile multipartFile, String fileName) {
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {

            InputStream multipartFileInputStream = multipartFile.getInputStream();
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectKey+fileName, multipartFileInputStream);
            ossClient.putObject(putObjectRequest);

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            // 关闭流
            ossClient.shutdown();
        }
    }

}