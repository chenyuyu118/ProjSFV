package pers.cherish.userservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.cherish.userservice.model.User;
import pers.cherish.userservice.model.UserVo;

import java.util.List;


@Mapper
public interface UserMapper extends BaseMapper<User> {
    void updateIsDeletedById(Long id, boolean isDeleted);

    void updateFiledById(Long id, String filed, String value);

    List<User> selectUserPage(String value, int startIndex, int endIndex);

    List<UserVo> selectAllUserVo();
}
