package pers.cherish.commons.cos;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.StorageClass;
import com.tencent.cloud.CosStsClient;
import com.tencent.cloud.Credentials;
import com.tencent.cloud.Response;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.time.temporal.TemporalField;
import java.util.*;


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

    public String uploadFile(String bucket, File file, String key) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key, file);
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

    public String uploadStream(InputStream stream, String key) {
        ObjectMetadata metadata = new ObjectMetadata();
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosProperties.getProfileBucket(), key, stream, metadata);
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
        // 获取类型信息

        // 解码base64信息
        String[] parts = base64Img.split(",");
        byte[] imageBytes = Base64.getDecoder().decode(parts[1]);
        // 转化为需要的类型
        final BufferedImage image;
        try {
            image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            final String s1 = id + ".jpg";
            File newImage = File.createTempFile(id, ".jpg");
            ImageIO.write(image, "jpg", newImage);
            String s = this.uploadFile(cosProperties.getProfileBucket(), newImage, s1);
            newImage.delete();
            return s;
        } catch (IOException e) {
            System.out.println("upload failed");
        }
        return "failed";
    }

    private void shutdown() {
        cosClient.shutdown();
    }

    public String uploadVideoProfile(String profile, String id) {
        byte[] imageBytes = Base64.getDecoder().decode(profile);
        // 转化为需要的类型
        final BufferedImage image;
        try {
            image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            final String s1 = UUID.randomUUID().toString();
            File newImage = File.createTempFile(s1, ".jpeg");
            ImageIO.write(image, "jpeg", newImage);
            String s = this.uploadFile(cosProperties.getVideoBucket(), newImage, s1 + ".jpeg");
            newImage.delete();
            return s1;
        } catch (IOException e) {
            System.out.println("upload failed");
        }
        return "failed";
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

        return url.toString();
    }

    public Response generateCredential() {
        TreeMap<String, Object> config = new TreeMap<String, Object>();
        Response result;
        try {
            config.put("secretId", cosProperties.getSecretId());
            // 替换为您的云 api 密钥 SecretKey
            config.put("secretKey", cosProperties.getSecretKey());
            // 设置域名:
            // 如果您使用了腾讯云 cvm，可以设置内部域名
            //config.put("host", "sts.internal.tencentcloudapi.com");

            // 临时密钥有效时长，单位是秒，默认 1800 秒，目前主账号最长 2 小时（即 7200 秒），子账号最长 36 小时（即 129600）秒
            config.put("durationSeconds", 1800);

            // 换成您的 bucket
            config.put("bucket", cosProperties.getVideoBucket());
            // 换成 bucket 所在地区
            config.put("region", cosProperties.getRegion());

            // 这里改成允许的路径前缀，可以根据自己网站的用户登录态判断允许上传的具体路径
            // 列举几种典型的前缀授权场景：
            // 1、允许访问所有对象："*"
            // 2、允许访问指定的对象："a/a1.txt", "b/b1.txt"
            // 3、允许访问指定前缀的对象："a*", "a/*", "b/*"
            // 如果填写了“*”，将允许用户访问所有资源；除非业务需要，否则请按照最小权限原则授予用户相应的访问权限范围。
            config.put("allowPrefixes", new String[] {
                    "*",
            });
            // 密钥的权限列表。必须在这里指定本次临时密钥所需要的权限。
            // 简单上传、表单上传和分块上传需要以下的权限，其他权限列表请参见 https://cloud.tencent.com/document/product/436/31923
            String[] allowActions = new String[] {
                    // 简单上传
                    "name/cos:PutObject",
                    // 表单上传、小程序上传
                    "name/cos:PostObject",
                    // 分块上传
                    "name/cos:InitiateMultipartUpload",
                    "name/cos:ListMultipartUploads",
                    "name/cos:ListParts",
                    "name/cos:UploadPart",
                    "name/cos:CompleteMultipartUpload"
            };
            config.put("allowActions", allowActions);
            Response response = CosStsClient.getCredential(config);
//            System.out.println(response.credentials.tmpSecretId);
//            System.out.println(response.credentials.tmpSecretKey);
//            System.out.println(response.credentials.sessionToken);
            result = response;
        } catch (Exception e) {
            e.printStackTrace();
//            throw new IllegalArgumentException("no valid secret !");
            result = null;
        }
        return result;
    }

    public String uploadMaterial(String key, File f) {
        return this.uploadFile(cosProperties.getMaterialBucket(), f, key);
    }
}
