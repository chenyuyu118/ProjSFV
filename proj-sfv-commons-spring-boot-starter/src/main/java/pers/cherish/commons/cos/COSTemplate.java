package pers.cherish.commons.cos;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.StorageClass;


import java.io.ByteArrayInputStream;
import java.io.InputStream;


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

    private void shutdown() {
        cosClient.shutdown();
    }
}
