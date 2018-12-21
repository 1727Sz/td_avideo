package td.h;

import org.apache.logging.log4j.util.Strings;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import td.h.o.*;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class HAdminRepository {

    @Autowired private JdbcTemplate jdbcTemplate;
    @Value("${server.domain}") private String domain;

    private <T> T queryForObject(String sql, Object[] args, RowMapper<T> rowMapper) {
        try {
            return jdbcTemplate.queryForObject(sql, args, rowMapper);
        } catch (DataAccessException e) {
            return null;
        }
    }

    private <T> T queryForObject(String sql, RowMapper<T> rowMapper) {
        return queryForObject(sql, new Object[]{}, rowMapper);
    }


    /**
     * 注册
     *
     * @param username
     * @param password
     * @return
     */
    public T_User register(String username, String password) {
        // 校验用户名唯一性
        boolean alreadyUsernameExisted = queryForObject(
                "select count(1) from h.t_user where username = ?", new Object[]{username},
                new BeanPropertyRowMapper<>(Boolean.class));
        if (alreadyUsernameExisted) {
            throw new BizException(18121701, "用户名已存在");
        }
        // 生成token， token有效期30天
        String token = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
        Date tokenExpireTime = Time.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());
        String nickname = T_User.showNickname("", username);
        //注册
        jdbcTemplate.update(
                "insert into h.t_user (nickname, username, password, createTime, token, tokenExpireTime) values (?, ?, ?, ?, ?, ?)",
                nickname, username, password, new Date(), token, tokenExpireTime);

        // 返回
        T_User user = queryForObject(
                "select * from h.t_user where token = ?",
                new Object[]{token},
                new BeanPropertyRowMapper<>(T_User.class));

        user.setAvatar(ImagePath.showAvatar(domain, user.getRelativeAvatar()));

        return user;
    }

    /**
     * 修改密码
     *
     * @param token
     * @param password
     * @param newPassword
     */
    public void updatePassword(String token, String password, String newPassword) {

        T_User user = getUserByToken(token);
        if (!user.getPassword().equalsIgnoreCase(password)) {
            throw new BizException(18121702, "请输入正确的原密码");
        }
        jdbcTemplate.update("update h.t_user set password = ? where id = ?",
                newPassword, user.getId());

        // 清空token，强制重新登陆
        jdbcTemplate.update("update h.t_user set token = null, tokenExpireTime = now() where id = ?", user.getId());
    }

    /**
     * 更新头像
     *
     * @param token
     * @param avatar
     */
    public void updateAvatar(String token, String avatar) {

        T_User user = getUserByToken(token);
        jdbcTemplate.update("update h.t_user set relativeAvatar = ? where id = ?",
                new Object[]{avatar, user.getId()});
    }

    /**
     * 修改昵称
     *
     * @param token
     * @param nickname
     */
    public void updateNickname(String token, String nickname) {

        T_User user = getUserByToken(token);
        nickname = T_User.showNickname(nickname, user.getUsername());
        jdbcTemplate.update("update h.t_user set nickname = ? where id = ?",
                nickname, user.getId());
    }


    /**
     * 登陆
     *
     * @param username
     * @param password
     * @return
     */
    public T_User login(String username, String password) {

        T_User user = queryForObject(
                "select * from h.t_user where username = ?",
                new Object[]{username},
                new BeanPropertyRowMapper<>(T_User.class));
        if (Objects.isNull(user)) {
            throw new BizException(18121703, "账户不存在或密码错误");
        }

        if (!password.equals(user.getPassword())) {
            throw new BizException(18121704, "账户不存在或密码错误");
        }

        // 生成token， token有效期30天
        String token = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
        Date tokenExpireTime = Time.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());
        //注册
        jdbcTemplate.update(
                "update h.t_user set token = ?, tokenExpireTime = ? where id = ?",
                new Object[]{token, tokenExpireTime, user.getId()});

        // 返回
        user = queryForObject("select * from h.t_user where token = ?", new Object[]{token},
                new BeanPropertyRowMapper<>(T_User.class));

        user.setAvatar(ImagePath.showAvatar(domain, user.getRelativeAvatar()));
        return user;
    }

    /**
     * 根据token获取用户
     *
     * @param token
     * @return
     */
    public T_User getUserByToken(String token) {
        if (Strings.isEmpty(token)) {
            throw new BizException(17031701);
        }
        T_User user = queryForObject(
                "select * from h.t_user where token = ? and tokenExpireTime > now()",
                new Object[]{token}, new BeanPropertyRowMapper<>(T_User.class));

        if (Objects.isNull(user)) {
            throw new BizException(17031702, "您的登录状态已过期，请重新登录");
        }

        user.setAvatar(ImagePath.showAvatar(domain, user.getRelativeAvatar()));

        return user;
    }

    /**
     * 根据token获取uid
     *
     * @param token
     * @return
     */
    public int getUidByTokenWithoutCheck(String token) {
        try {
            return getUserByToken(token).getId();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 评论
     *
     * @param vid
     * @param token
     * @param content
     */
    public void comment(int vid, String token, String content) {
        T_User user = getUserByToken(token);
        jdbcTemplate.update(
                "insert into h.t_comment (content, target, uid, nickname, avatar) values (?, ?, ?, ?, ?)",
                content, vid, user.getId(), user.getNickname(), user.getRelativeAvatar());

        syncVideoComments(vid);
    }

    /**
     * 更新视频的评论数
     *
     * @param vid
     */
    private void syncVideoComments(int vid) {
        jdbcTemplate.update(
                "update h.t_video set comments = (select count(1) from h.t_comment c where c.target = ?) where id = ?",
                new Object[]{vid, vid});
    }

    /**
     * 删除评论
     *
     * @param cid
     * @param token
     */
    public void deleteComment(int cid, String token) {
        T_User user = getUserByToken(token);
        int uid = user.getId();
        int vid = queryForObject(
                "select vid from h.t_comment where id = ?", new Object[]{cid},
                new BeanPropertyRowMapper<>(Integer.class));
        jdbcTemplate.update("delete from h.t_comment where id = ? and uid = ?", new Object[]{cid, uid});
        syncVideoComments(vid);
    }

    /**
     * 点赞
     *
     * @param token
     * @param vid
     */
    public void like(String token, int vid) {
        if (alreadyVideoLiked(token, vid)) {
            throw new BizException(18121709, "已赞过~");
        }
        T_User user = getUserByToken(token);
        jdbcTemplate.update(
                "insert into h.t_like (uid, target) values (?, ?)",
                new Object[]{user.getId(), vid});

        jdbcTemplate.update(
                "update h.t_video set likes = (select count(1) from h.t_like c where c.target = ?) where id = ?",
                new Object[]{vid, vid});
    }

    /**
     * 校验是否点赞过
     *
     * @param token
     * @param vid
     * @return
     */
    public boolean alreadyVideoLiked(String token, int vid) {
        T_User user = getUserByToken(token);
        return queryForObject(
                "select count(1) from h.t_like where uid = ? and target = ?",
                new Object[]{user.getId(), vid},
                new BeanPropertyRowMapper<>(Boolean.class));
    }

    /**
     * 分页查询视频评论
     *
     * @param token
     * @param vid
     * @param page
     * @param pageSize
     * @return
     */
    public List<T_Comment> pageVideoComment(String token, int vid, int page, int pageSize) {

        int uid = getUidByTokenWithoutCheck(token);

        return jdbcTemplate.query(
                "select * from h.t_comment",
                new Object[]{vid, (page - 1) * pageSize, pageSize},
                new BeanPropertyRowMapper<>(T_Comment.class))
                .stream()
                .peek(it -> it.setZj(uid == it.getUid()))
                .peek(it -> it.setAvatar(ImagePath.showAvatar(domain, it.getAvatar())))
                .collect(Collectors.toList());
    }

    /**
     * 视频列表
     *
     * @param page
     * @param pageSize
     */
    public Pair<Long, List<T_Video>> pageVideo(int page, int pageSize) {
        long total = jdbcTemplate.queryForObject(
                "select count(1) from h.t_video",
                Long.class);

        List<T_Video> values = jdbcTemplate.query(
                "select * from h.t_video order by id desc limit ?, ?",
                new Object[]{(page - 1) * pageSize, pageSize},
                new BeanPropertyRowMapper<>(T_Video.class));

        return Pair.with(total, values);
    }

    /**
     * 分页查询关注的视频列表
     *
     * @param token
     * @param page
     * @param pageSize
     * @return
     */
    public List<T_Video> pageFollowedVideo(String token, int page, int pageSize) {
        int uid = getUidByTokenWithoutCheck(token);
        if (uid == 0) {
            return Collections.emptyList();
        }
        return jdbcTemplate.query(
                "select * from h.t_video v inner join h.t_follow_video fv on fv.target = v.id and fv.uid = ? order by fv.createTime desc",
                new Object[]{uid, (page - 1) * pageSize, pageSize},
                new BeanPropertyRowMapper<>(T_Video.class))
                .stream()
                .peek(it -> {
                    it.setPlayUrl("成为VIP会员后可永久免费观看海量视频");
                    it.setVip(false);
                })
                .collect(Collectors.toList());
    }

    /**
     * 校验是否已关注
     *
     * @param token
     * @param vid
     * @return
     */
    public boolean alreadyFollowed(String token, int vid) {
        T_User user = getUserByToken(token);
        return queryForObject(
                "select count(1) from h.t_comment where uid = ? and target = ?",
                new Object[]{user.getId(), vid},
                new BeanPropertyRowMapper<>(Boolean.class));
    }

    /**
     * 关注视频
     *
     * @param token
     * @param vid
     */
    public void followVideo(String token, int vid) {
        T_User user = getUserByToken(token);
        if (alreadyFollowed(token, vid)) {
            throw new BizException(18121707, "无需重复关注");
        }
        jdbcTemplate.update("insert into h.t_comment (target, uid) values (?, ?)",
                vid, user.getId());
    }

    /**
     * 取消关注视频
     *
     * @param token
     * @param vid
     */
    public void followCancelVideo(String token, int vid) {
        T_User user = getUserByToken(token);
        jdbcTemplate.update("delete from h.t_comment where target = ? and uid = ?",
                new Object[]{vid, user.getId()});
    }

    /**
     * 获取视频详情
     *
     * @param token
     * @param vid
     * @return
     */
    public T_Video getVideoDetailById(String token, int vid) {
        T_User user = getUserByToken(token);
        T_Video video = queryForObject(
                "select * from h.t_video where id = ? and enable = true",
                new Object[]{vid},
                new BeanPropertyRowMapper<>(T_Video.class));
        if (Objects.isNull(video)) {
            throw new BizException(18121706, "视频资源异常，请稍后再试");
        }
        if (!user.isVip()) {
            video.setPlayUrl("成为VIP会员后可永久免费观看海量视频");
            video.setVip(false);
        }

        video.setLiked(alreadyVideoLiked(token, vid));
        video.setFollowed(alreadyFollowed(token, vid));
        return video;

    }

    /**
     * 配置
     *
     * @return
     */
    public T_Configuration getConfiguration() {
        return queryForObject(
                "select * from h.t_configuration",
                new Object[]{},
                new BeanPropertyRowMapper<>(T_Configuration.class));
    }
}