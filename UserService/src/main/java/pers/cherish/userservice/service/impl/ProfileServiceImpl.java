package pers.cherish.userservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.cherish.commons.cos.COSTemplate;
import pers.cherish.userservice.mapper.ProfileMapper;
import pers.cherish.userservice.model.Profile;
import pers.cherish.userservice.service.ProfileService;

import java.sql.Date;
import java.time.Instant;
import java.util.List;

@Service
public class ProfileServiceImpl implements ProfileService {
    private ProfileMapper profileMapper;
    private COSTemplate cosTemplate;

    @Autowired
    public void setProfileMapper(ProfileMapper profileMapper) {
        this.profileMapper = profileMapper;
    }

    @Autowired(required = true)
    public void setCosTemplate(COSTemplate cosTemplate) {
        this.cosTemplate = cosTemplate;
    }

    @Override
    @Transactional
    public void updateProfile(String  id, Long ownerId, String img) {
        System.out.println(img);
        int lastPointIndex = img.lastIndexOf(",");
        final byte[] bytes = Base64.decodeBase64(img.substring(lastPointIndex + 1));
        String imgType = img.substring(0, lastPointIndex); // jpg, png, gif
        final String s = cosTemplate.uploadBytes(bytes, id + "." + imgType);
        if (s.equals("fault")) {
            throw new RuntimeException("上传失败");
        } else {
            profileMapper.insert(new Profile(id, ownerId, Date.from(Instant.now())));
        }
    }

    @Override
    public List<String > getHistoricalProfiles(Long id) {
        final QueryWrapper<Profile> wrapper = new QueryWrapper<>();
        wrapper.eq("owner_id", id)
                .orderBy(true, false, "register_time");
        return profileMapper.selectList(wrapper).stream()
                .map(Profile::getId).toList();
    }
}
