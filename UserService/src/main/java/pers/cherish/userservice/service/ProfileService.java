package pers.cherish.userservice.service;

import org.springframework.stereotype.Service;
import pers.cherish.userservice.model.Profile;

import java.util.List;

@Service
public interface ProfileService {
    void updateProfile(String  id, Long ownerId, String img);
    List<Profile> getHistoricalProfiles(Long id);

    String getCurrentProfile(Long id);

    boolean isExist(Long id, String profileId);

    void updateProfileHistorical(Long id, String profileId);
}
