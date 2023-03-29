package pers.cherish.commons.cos;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.StorageClass;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class COSTemplate {
    private COSProperties cosProperties;

    private COSClient cosClient;

    public COSTemplate(COSProperties cosProperties, COSClient cosClient) {
        this.cosProperties = cosProperties;
        this.cosClient = cosClient;
    }

    public String uploadBytes(byte[] bytes, String key) throws CosClientException {
        InputStream stream = new ByteArrayInputStream(bytes);
        final ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(bytes.length);
        final PutObjectRequest request = new PutObjectRequest(cosProperties.getProfileBucket(), key, stream, metadata);
        request.setStorageClass(StorageClass.Standard);
        String message = null;
        try {
            PutObjectResult result = cosClient.putObject(request);
            message = result.getRequestId();
        } catch (CosClientException e) {
            e.printStackTrace();
            message = "fault";
        }
        return message;
    }

    public String uploadFile(File file, String key) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosProperties.getProfileBucket(), key, file);
        String message = null;
        try {
            PutObjectResult result = cosClient.putObject(putObjectRequest);
            message = result.getRequestId();
        } catch (CosClientException e) {
            e.printStackTrace();
            message = "fault";
        }
        return message;
    }

    public String uploadProfile(String base64Img, String id) {
//        // 获取类型信息
//        String mediaType = parts[0].split(":")[1];
//        final String type = MimeTypeUtils.parseMimeType(mediaType).getSubtype();
        // 解码base64信息
        String[] parts = base64Img.split(",");
        byte[] imageBytes = Base64.getDecoder().decode(parts[1]);
        // 转化为需要的类型
        final BufferedImage image;
        try {
            image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            final String s1 = id + ".jpg";
            File newImage = new File(s1);
            ImageIO.write(image, "jpg", newImage);
            return this.uploadFile(newImage, s1);
        } catch (IOException e) {
            System.out.println("upload failed");
        }
        return "failed";
    }

    private void shutdown() {
        cosClient.shutdown();
    }

    public String uploadImage(String profile) {
        return "";
    }

    public String generatePreSignedUrl(String key) {

        // 存储桶的命名格式为 BucketName-APPID，此处填写的存储桶名称必须为此格式
        //        String bucketName = "examplebucket-1250000000";
        //// 对象键(Key)是对象在存储桶中的唯一标识。详情请参见 [对象键](https://cloud.tencent.com/document/product/436/13324)
//        String key = UUID.randomUUID().toString() + ".png";

        // 设置签名过期时间(可选), 若未进行设置则默认使用 ClientConfig 中的签名过期时间(1小时)
        // 这里设置签名5分钟后过期
        Date expirationDate = new Date(System.currentTimeMillis() + 5 * 60 * 1000);

        // 填写本次请求的参数，需与实际请求相同，能够防止用户篡改此签名的 HTTP 请求的参数
        Map<String, String> params = new HashMap<String, String>();
//        params.put("param1", "value1");

        // 填写本次请求的头部，需与实际请求相同，能够防止用户篡改此签名的 HTTP 请求的头部
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "audio/mp4");

        // 请求的 HTTP 方法，上传请求用 PUT，下载请求用 GET，删除请求用 DELETE
        HttpMethodName method = HttpMethodName.PUT;

        URL url = cosClient.generatePresignedUrl(cosProperties.getVideoBucket(), key, expirationDate, method, headers, params);
//        System.out.println(url.toString());

        return url.toString();
    }
}
