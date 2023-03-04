package pers.cherish.userservice.service;

import org.springframework.stereotype.Service;
import pers.cherish.userservice.domain.UserDTORegister;
import pers.cherish.userservice.model.User;
import pers.cherish.userservice.model.UserDTO;
import pers.cherish.userservice.model.UserVo;

import java.util.List;

@Service
public interface UserService {
    List<UserVo> getAllUserVo();

    void updateSignature(Long id, String value);

    Long register(UserDTORegister userDTORegister);

    User login(String user, String password);

    UserDTO getInfo(Long id);

    void cancelAccount(Long id);

    @Deprecated
    List<UserVo> searchUser(String key);

    List<UserVo> searchUser(String key, int page);

    void updateUserName(Long id, String name);

    void updateUserPassword(Long id, String password);

    void updatePhoneNumber(Long id, String phoneNumber);

    void updateEmail(Long id, String email);

    void updateVxNumber(Long id, String vxNumber);

    void updateQqNumber(Long id, String qqNumber);

    @Deprecated
    boolean logout(Long id);

    User getUserByName(String  name);

    public static enum FiledNames {
        SIGNATURE {
            @Override
            public String toString() {
                return "signature";
            }
        },
        USERNAME {
            @Override
            public String toString() {
                return "user_name";
            }
        },
        PASSWORD {
            @Override
            public String toString() {
                return "password";
            }
        },
        PHONE_NUMBER {
            @Override
            public String toString() {
                return "phone_number";
            }
        }, EMAIL {
            @Override
            public String toString() {
                return "email";
            }
        }, VX_NUMBER {
            @Override
            public String toString() {
                return "vx_number";
            }
        }, QQ_NUMBER {
            @Override
            public String toString() {
                return "qq_number";
            }
        };
    }
}
