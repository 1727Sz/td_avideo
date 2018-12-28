package td.h.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import td.h.o.T_User;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {

    boolean alreadyUsernameExisted(String username);
    T_User getById(int uid);
    T_User getByToken(String token);
    T_User getByTokenWithValid(@Param("token") String token, @Param("tokenExpireTime") Date tokenExpireTime);
    boolean updatePassword(@Param("id") int uid, @Param("password") String password);
    boolean updateAvatar(@Param("id") int uid, @Param("relativeAvatar") String avatar);
    boolean updateNickname(@Param("id") int uid, @Param("nickname") String nickname);
    boolean clearToken(int uid);
    T_User getByUsername(String username);
    boolean updateToken(@Param("token") String token, @Param("tokenExpireTime") Date tokenExpireTime, @Param("id") int uid);
    boolean createNewUser(Map<String, Object> params);

    int count(Map<String, Object> params);
    List<T_User.ComplexAdminUser> adminPage(Map<String, Object> params, RowBounds rowBounds);
}
