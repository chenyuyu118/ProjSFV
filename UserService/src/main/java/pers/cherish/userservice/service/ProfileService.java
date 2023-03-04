package pers.cherish.userservice.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProfileService {
    void updateProfile(String  id, Long ownerId, String img);
    List<String > getHistoricalProfiles(Long id);

}
