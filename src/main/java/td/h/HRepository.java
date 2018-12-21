package td.h;

import com.google.common.hash.Hashing;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import td.h.o.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Time;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class HRepository {

    @Autowired private JdbcTemplate jdbcTemplate;
    @Value("${server.domain}") private String domain;
    @Autowired private OkHttpClient okHttpClient;

    private <T> T queryForObject(String sql, Object[] args, RowMapper<T> rowMapper) {
        try {
            return jdbcTemplate.queryForObject(sql, args, rowMapper);
        } catch (DataAccessException e) {
            return null;
        }
    }

    private <T> T queryForObject(String sql, Object[] args, Class<T> targetType) {
        try {
            return jdbcTemplate.queryForObject(sql, args, targetType);
        } catch (DataAccessException e) {
            log.error("jdbc 执行失败，e={}", e);
            return null;
        }
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
                Boolean.class);
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

    public T_User getUserById(int uid) {
        T_User user = queryForObject(
                "select * from h.t_user where id = ?",
                new Object[]{uid}, new BeanPropertyRowMapper<>(T_User.class));

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
                "select target from h.t_comment where id = ?", new Object[]{cid},
                Integer.class);
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
                Boolean.class);
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
                "select * from h.t_comment where target = ? order by createTime desc limit ?, ?",
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
    public List<T_Video> pageVideo(int page, int pageSize) {
        return jdbcTemplate.query(
                "select * from h.t_video where enable = true order by updateTime desc limit ?, ?",
                new Object[]{(page - 1) * pageSize, pageSize},
                new BeanPropertyRowMapper<>(T_Video.class));
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
                "select * from h.t_video v inner join h.t_follow_video fv on fv.target = v.id and fv.uid = ? order by fv.createTime desc limit ?, ?",
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
                "select count(1) from h.t_follow_video where uid = ? and target = ?",
                new Object[]{user.getId(), vid},
                Boolean.class);
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
        jdbcTemplate.update("insert into h.t_follow_video (target, uid) values (?, ?)",
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
        jdbcTemplate.update("delete from h.t_follow_video where target = ? and uid = ?",
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

    @Value("${pay.api.gte}") private String gtePayApi;
    @Value("${pay.notify.async}") private String asyncNotifyUrl;
    @Value("${pay.notify.sync}") private String syncNotifyUrl;
    @Value("${pay.channel}") private String payChannel;
    @Value("${pay.secret}") private String paySecret;
    @Value("${pay.type}") private String payType;
    @Value("${pay.category}") private String payCategory;

    /**
     * 生成支付订单
     *
     * @param totalFee
     * @param name
     */
    private String gtePayScheme(int totalFee, String name, String orderNo, Date create) {
        String orderTime = Times.format(create, Times.yyyyMMddHHmmss);
        String timestamp = String.valueOf(create.getTime());
        String sign = generatePaySign(orderNo, totalFee, asyncNotifyUrl, orderTime, timestamp, paySecret);
        FormBody body = new FormBody.Builder()
                .add("orderNo", orderNo)
                .add("timeStamp", timestamp)
                .add("channelId", payChannel)
                .add("sign", sign)
                .add("notifyUrl", asyncNotifyUrl)
                .add("returnUrl", syncNotifyUrl)
                .add("payType", payType)
                .add("payCategory", payCategory)
                .add("goodsName", name)
                .add("totalFee", String.valueOf(totalFee))
                .add("orderTime", orderTime)
                .build();
        Request request = new Request.Builder()
                .url(gtePayApi)
                .post(body)
                .build();
        try {
            Response execute = okHttpClient.newCall(request).execute();
            String response = execute.body().string();
            JsonObject json = JSONMapper.fromJson(response, JsonObject.class);
            if (json.get("code").getAsInt() != 0) {
                throw new BizException(18122002, json.get("data").getAsString());
            }
            String payUrl = json.get("data").getAsString();
            return payUrl;
        } catch (IOException e) {
            log.error("充值订单往第三方投递时失败，e={}", e);
            throw new BizException(18122003, "订单提交失败，请稍后再试");
        }

    }

    private String generateOrderNo(int uid) {
        String orderNo = MessageFormat.format("{0}000{1}", String.valueOf(uid), String.valueOf(System.currentTimeMillis()));
        return orderNo;
    }

    private String generatePaySign(String orderNo, int totalFee, String notifyUrl, String orderTime, String timestamp, String secret) {
        Charset utf8 = Charset.forName("utf8");
        String sign = Hashing.md5().newHasher()
                .putString(orderNo, utf8)
                .putString(String.valueOf(totalFee), utf8)
                .putString(notifyUrl, utf8)
                .putString(orderTime, utf8)
                .putString(timestamp, utf8)
                .putString(secret, utf8)
                .hash().toString().toLowerCase();
        return sign;
    }

    public RechargeVo generateVipRechargeOrder(String token, int day) {
        if (!Arrays.asList(30, 120, 360).contains(day)) {
            throw new BizException(18122001, "请选择正确的充值类型");
        }
        T_User user = getUserByToken(token);
        Date now = new Date();

        T_Configuration configuration = getConfiguration();

        int totalFee = 0;
        String goodsName = "";

        if (30 == day) {
            totalFee = configuration.getMonthVipPrice();
            goodsName = "会员月卡";
        }
        if (120 == day) {
            totalFee = configuration.getQuarterVipPrice();
            goodsName = "会员季卡";
        }
        if (360 == day) {
            totalFee = configuration.getYearVipPrice();
            goodsName = "会员年卡";
        }
        Date payExpireTime = Date.from(LocalDateTime.now().plusMinutes(10).atZone(ZoneId.systemDefault()).toInstant());


        // 往第三方投递支付订单
        String orderNo = generateOrderNo(user.getId());
        String payUrl = gtePayScheme(totalFee, goodsName, orderNo, now);

        // 支付订单入库
        jdbcTemplate.update(
                "insert into h.t_vip_recharge " +
                        "(orderNo, uid, fee, createTime, goodsName, vipPlusDay, payState, payExpireTime, payUrl) " +
                        "values " +
                        "(?, ?, ?, ?, ?, ?, ?, ?, ?)",
                orderNo, user.getId(), totalFee, now, goodsName, day, 0, payExpireTime, payUrl);

        return new RechargeVo(orderNo, payUrl);
    }

    /**
     * 轮询充值支付状态
     *
     * @param orderNo
     * @return
     */
    public boolean loopRechargeSuccess(String orderNo) {
        T_VipRecharge recharge = queryForObject(
                "select * from h.t_vip_recharge where orderNo = ?",
                new Object[]{orderNo},
                new BeanPropertyRowMapper<>(T_VipRecharge.class));
        if (Objects.isNull(recharge)) {
            throw new BizException(18122004, "充值订单不存在");
        }
        if (-1 == recharge.getPayState() || recharge.getPayExpireTime().before(new Date())) {
            throw new BizException(18122005, "超过10分钟未付款，订单被取消");
        }

        if (0 == recharge.getPayState()) {
            // 未支付，主动查询一次第三方系统中订单的状态
            boolean success = success(orderNo);
            // 若支付成功，则变更系统中的支付状态
            if (success) {

                // 更新数据库状态
                jdbcTemplate.update("update t_vip_recharge set payState = ?, payTime = ?", 1, new Date());
                // 更新会员到期时间
                syncUserVipExpireTime(recharge.getUid(), recharge.getVipPlusDay());

                return true;
            }
            // 否则，直接返回，继续等待
            return false;
        }

        return true;
    }

    public RechargePreviewVo previewRecharge(String token) {

        T_User user = getUserByToken(token);
        Date now = new Date();
        LocalDateTime vipExpireLocalDateTime =
                (
                        Objects.nonNull(user.getVipExpireDate())
                                && user.getVipExpireDate().after(now)
                                ? user.getVipExpireDate() : now
                )
                        .toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return new RechargePreviewVo(getConfiguration(), vipExpireLocalDateTime);
    }


    private boolean success(String orderNo) {
        String url = MessageFormat.format("http://p.showsung.com/api/query/{0}/{1}", payChannel, orderNo);
        Request request = new Request.Builder().url(url).build();
        try {
            Response execute = okHttpClient.newCall(request).execute();
            String response = execute.body().string();
            JsonObject json = JSONMapper.fromJson(response, JsonObject.class);
            if (!"success".equals(json.get("status").getAsString())) {
                throw new BizException(18122008, json.get("message").getAsString());
            }
            JsonObject order = json.get("order").getAsJsonObject();
            // 1：代表未支付；2：代表已支付
            int payStatus = order.get("payStatus").getAsInt();
            return 2 == payStatus;
        } catch (IOException e) {
            log.error("充值订单查询第三方时失败，orderNo={}, e={}", orderNo, e);
            throw new BizException(18122003, "订单提交失败，请稍后再试");
        }


    }

    private void syncUserVipExpireTime(int uid, int day) {
        T_User user = getUserById(uid);
        Date now = new Date();
        LocalDateTime vipExpireLocalDateTime =
                (
                        Objects.nonNull(user.getVipExpireDate())
                                && user.getVipExpireDate().after(now)
                                ? user.getVipExpireDate() : now
                )
                        .toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        Date expireTime = Date.from(vipExpireLocalDateTime.plusDays(day).atZone(ZoneId.systemDefault()).toInstant());
        jdbcTemplate.update("update h.t_user set vipExpireTime = ? where id = ?", expireTime, uid);
    }

    public void syncRechargeByNotify(String orderNo) {
        try {
            loopRechargeSuccess(orderNo);
        } catch (Exception e) {
            log.error("第三方支付平台通知时失败，orderNo={}, e={}", orderNo, e);
            throw new BizException(18122101, "回调未成功");
        }
    }


}