package pers.cherish.userservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Map;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVo {
    @MongoId
    private Long id;
    private String  profile = null;
    private String userName = null;
    private String  signature = null;
    private Boolean gender = null;
    private String currentAddress = null;
    private boolean isDeleted = false;


    public void setFiledValue(String filed, Object value) {
        switch (filed) {
            case "profile" -> setProfile((String) value);
            case "userName" -> setUserName((String) value);
            case "signature" -> setSignature((String) value);
            case "gender" -> setGender((boolean) value);
            case "currentAddress" -> setCurrentAddress((String) value);
        }
    }

    public Map<String , String > gNotNullFieldMap() {
        if (profile != null) {
            return Map.of("field", "profile", "value", profile);
        }
        if (userName != null) {
            return  Map.of("field", "userName", "value", userName);
        }
        if (signature != null) {
            return Map.of("field", signature, "value", signature);
        }
        if (currentAddress != null)
            return Map.of("field", currentAddress, "value", currentAddress);
        return Map.of("id", id.toString());
    }

}
