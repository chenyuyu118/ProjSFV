package pers.cherish.userservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.cherish.commons.cos.COSTemplate;
import pers.cherish.userservice.domain.UserDTORegister;
import pers.cherish.userservice.mapper.ProfileMapper;
import pers.cherish.userservice.mapper.UserMapper;
import pers.cherish.userservice.model.Profile;
import pers.cherish.userservice.model.User;
import pers.cherish.userservice.model.UserDTO;
import pers.cherish.userservice.model.UserVo;
import pers.cherish.userservice.service.UserService;

import java.sql.Date;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;

    private ProfileMapper profileMapper;

    private COSTemplate cosTemplate;

    @Autowired
    public UserServiceImpl(UserMapper userMapper, ProfileMapper profileMapper) {
        this.userMapper = userMapper;
        this.profileMapper = profileMapper;
    }

    @Autowired
    public void setCosTemplate(COSTemplate cosTemplate) {
        this.cosTemplate = cosTemplate;
    }

    @Override
    public List<UserVo> getAllUserVo() {
        return userMapper.selectAllUserVo();
    }

    @Override
    public void updateSignature(Long id, String value) {
        updateFiled(id, FiledNames.SIGNATURE, value);
    }

    @Override
    public Long register(UserDTORegister userDTORegister) {
        User user = new User();
        BeanUtils.copyProperties(userDTORegister, user, "profile");
        user.setProfile(userDTORegister.getProfileId());
        String img = userDTORegister.getProfile();
        int lastPointIndex = img.lastIndexOf(",");
        final byte[] bytes = Base64.getDecoder().decode(img.substring(lastPointIndex + 1));
        String imgType = img.substring(0, lastPointIndex); // jpg, png, gif
        final String s = cosTemplate.uploadBytes(bytes, userDTORegister.getProfileId() + "." + imgType);
        Profile profile = new Profile(userDTORegister.getProfileId(), userDTORegister.getId(), Date.from(Instant.now()));
        profileMapper.insert(profile);
        userMapper.insert(user);
        return user.getId();
    }

    @Override
    public User login(String user, String password) {
        final List<User> users = userMapper.selectByMap(Map.of("user_name", user, "password", password));
        if (users.size() == 0) {
            return null;
        } else if (users.size() == 1) {
            return users.get(0);
        } else
            return null;
    }

    @Override
    public UserDTO getInfo(Long id) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(userMapper.selectById(id), userDTO);
        return userDTO;
    }

    @Override
    public void cancelAccount(Long id) {
        userMapper.updateIsDeletedById(id, true);
    }

    @Override
    public List<UserVo> searchUser(String key) {
        key = "*" + key + "*";
        final QueryWrapper<User> wrapper = new QueryWrapper<User>().like("username", key).or().
                like("phoneNumber", key).or()
                                .eq("id", Long.valueOf(key));
        final List<User> list = userMapper.selectList(wrapper);
        return list.stream().map(user -> {
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(user, userVo);
            return userVo;
        }).toList();
    }

    @Override
    public void updateUserName(Long id, String name) {
        updateFiled(id, FiledNames.USERNAME, name);
    }

    @Override
    public void updateUserPassword(Long id, String password) {
        updateFiled(id, FiledNames.PASSWORD, password);
    }

    @Override
    public void updatePhoneNumber(Long id, String phoneNumber) {
        updateFiled(id, FiledNames.PHONE_NUMBER, phoneNumber);
    }

    @Override
    public void updateEmail(Long id, String email) {
        updateFiled(id, FiledNames.EMAIL, email);
    }

    @Override
    public void updateVxNumber(Long id, String vxNumber) {
        updateFiled(id, FiledNames.VX_NUMBER, vxNumber);
    }

    @Override
    public void updateQqNumber(Long id, String qqNumber) {
        updateFiled(id, FiledNames.QQ_NUMBER, qqNumber);
    }

    private void updateFiled(Long id, FiledNames filedName, String value) {
        userMapper.updateFiledById(id, filedName.toString(), value);
    }

    @Override
    public List<UserVo> searchUser(String key, int page) {
        int startIndex = (page-1) * 10;
        int endIndex = page * 10;
        return userMapper.selectUserPage(key, startIndex, endIndex).stream().map(user -> {
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(user, userVo);
            return userVo;
        }).toList();
    }
    @Override
    @Deprecated
    public boolean logout(Long id) {
        return false;
    }

    @Override
    public User getUserByName(String name) {
        final List<User> userName = userMapper.selectByMap(Map.of("user_name", name));
        return userName.size() == 0 ? null : userName.get(0);
    }
}
