package pers.cherish.userservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.cherish.commons.cos.COSTemplate;
import pers.cherish.userservice.mapper.ProfileMapper;
import pers.cherish.userservice.mapper.UserMapper;
import pers.cherish.userservice.model.Profile;
import pers.cherish.userservice.service.ProfileService;

import java.sql.Date;
import java.time.Instant;
import java.util.List;

@Service
public class ProfileServiceImpl implements ProfileService {
    private ProfileMapper profileMapper;
    private COSTemplate cosTemplate;
    private UserMapper userMapper;

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Autowired
    public void setProfileMapper(ProfileMapper profileMapper) {
        this.profileMapper = profileMapper;
    }

    @Autowired
    public void setCosTemplate(COSTemplate cosTemplate) {
        this.cosTemplate = cosTemplate;
    }

    @Override
    @Transactional
    public void updateProfile(String  id, Long ownerId, String img) {
        String s = cosTemplate.uploadProfile(img, id);
        if (s.equals("failed")) {
            throw new RuntimeException("上传失败");
        } else {
            profileMapper.insert(new Profile(id, ownerId, Date.from(Instant.now())));
            userMapper.updateFiledById(ownerId, "profile", id);
        }
    }

    @Override
    public List<Profile > getHistoricalProfiles(Long id) {
        final QueryWrapper<Profile> wrapper = new QueryWrapper<>();
        wrapper.eq("owner_id", id)
                .orderBy(true, false, "register_time");
        return profileMapper.selectList(wrapper);
    }

    @Override
    public String getCurrentProfile(Long id) {
        final QueryWrapper<Profile> wrapper = new QueryWrapper<>();
        wrapper.eq("owner_id", id)
                .orderBy(true, false, "register_time");
        return profileMapper.selectList(wrapper).get(0).getId();
    }

    @Override
    public boolean isExist(Long id, String profileId) {
        QueryWrapper<Profile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("owner_id", id).eq("id", profileId);
        return profileMapper.exists(queryWrapper);
    }

    @Override
    public void updateProfileHistorical(Long id, String profileId) {
        profileMapper.updateProfileHistorical(id, profileId);
    }
}
