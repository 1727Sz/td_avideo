package td.h;

import com.google.common.hash.Hashing;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.ibatis.session.RowBounds;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import td.h.mapper.*;
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

    @Value("${server.domain}") private String domain;
    @Autowired private OkHttpClient okHttpClient;
    @Autowired private UserMapper userMapper;
    @Autowired private CommentMapper commentMapper;
    @Autowired private VideoMapper videoMapper;
    @Autowired private ConfigurationMapper configurationMapper;
    @Autowired private VipRechargeMapper vipRechargeMapper;
    @Autowired private VersionMapper versionMapper;
    @Autowired private LikeMapper likeMapper;
    @Autowired private ReferMapper referMapper;


    /**
     * 注册
     *
     * @param username
     * @param password
     * @param refer
     * @return
     */
    public T_User register(String username, String password, int refer) {
        // 校验用户名唯一性
        boolean alreadyUsernameExisted = userMapper.alreadyUsernameExisted(username);
        if (alreadyUsernameExisted) {
            throw new BizException(18121701, "用户名已存在");
        }
        // 生成token， token有效期30天
        String token = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
        Date tokenExpireTime = Time.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());
        //注册 <<<< 注册为普通用户

        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("createTime", new Date());
        params.put("token", token);
        params.put("tokenExpireTime", tokenExpireTime);
        params.put("registerRefer", refer);
        userMapper.createNewUser(params);
        // 推广渠道同步
        syncReferCount(refer);

        // 返回
        T_User user = getUserByToken(token);

        user.setAvatar(ImagePath.showAvatar(domain, user.getRelativeAvatar()));

        return user;
    }

    public void syncReferCount(int refer) {

        referMapper.syncReferUserCount(refer);
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

        userMapper.updatePassword(user.getId(), newPassword);

        // 清空token，强制重新登陆
        userMapper.clearToken(user.getId());
    }

    /**
     * 更新头像
     *
     * @param token
     * @param avatar
     */
    public void updateAvatar(String token, String avatar) {

        T_User user = getUserByToken(token);
        userMapper.updateAvatar(user.getId(), avatar);
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
        userMapper.updateNickname(user.getId(), nickname);
    }


    /**
     * 登陆
     *
     * @param username
     * @param password
     * @return
     */
    public T_User login(String username, String password) {

        T_User user = userMapper.getByUsername(username);
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
        userMapper.updateToken(token, tokenExpireTime, user.getId());

        // 返回
        user = userMapper.getByToken(token);

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
        T_User user = userMapper.getByTokenWithValid(token, new Date());

        if (Objects.isNull(user)) {
            throw new BizException(17031702, "您的登录状态已过期，请重新登录");
        }

        user.setAvatar(ImagePath.showAvatar(domain, user.getRelativeAvatar()));

        return user;
    }

    public T_User getUserById(int uid) {
        T_User user = userMapper.getById(uid);


        if (Objects.nonNull(user)) {
            user.setAvatar(ImagePath.showAvatar(domain, user.getRelativeAvatar()));
        }

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
        Map<String, Object> params = new HashMap<>();
        params.put("content", content);
        params.put("target", vid);
        params.put("uid", user.getId());
        params.put("nickname", T_User.showNickname(user.getNickname(), user.getUsername()));
        params.put("avatar", user.getRelativeAvatar());
        commentMapper.comment(params);

        syncVideoComments(vid);
    }

    /**
     * 更新视频的评论数
     *
     * @param vid
     */
    private void syncVideoComments(int vid) {
        commentMapper.syncVideoComments(vid);
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

        T_Comment comment = commentMapper.getCommentById(cid);

        if (!commentMapper.deleteComment(cid, uid)) {
            return;
        }

        int vid = comment.getVid();
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
        likeMapper.like(vid, user.getId());

        likeMapper.syncLikes(vid);
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
        return likeMapper.alreadyLiked(vid, user.getId());
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

        return commentMapper.page(vid, new RowBounds((page - 1) * pageSize, pageSize))
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
        return videoMapper.page(new RowBounds((page - 1) * pageSize, pageSize)).stream()
                .peek(it -> {
                    if (!Strings.isEmpty(it.getCover()) && !it.getCover().contains("http")) {
                        it.setCover(ImagePath.showAvatar(domain, it.getCover()));
                    }
                })
                .collect(Collectors.toList());
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
        return videoMapper.pageFollowed(uid, new RowBounds((page - 1) * pageSize, pageSize))
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
        return videoMapper.alreadyFollowed(user.getId(), vid);
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
        videoMapper.follow(user.getId(), vid);
    }

    /**
     * 取消关注视频
     *
     * @param token
     * @param vid
     */
    public void followCancelVideo(String token, int vid) {
        T_User user = getUserByToken(token);
        videoMapper.followCancel(user.getId(), vid);
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
        T_Video video = videoMapper.getEnabledVideoById(vid);
        if (Objects.isNull(video)) {
            throw new BizException(18121706, "视频资源异常，请稍后再试");
        }
        if (!user.isVip()) {
            video.setPlayUrl("成为VIP会员后可永久免费观看海量视频");
            video.setVip(false);
        }

        video.setLiked(alreadyVideoLiked(token, vid));
        video.setFollowed(alreadyFollowed(token, vid));

        if (!Strings.isEmpty(video.getCover())) {
            if (!video.getCover().contains("http")) {
                video.setCover(ImagePath.showAvatar(domain, video.getCover()));
            }
        }
        return video;

    }

    /**
     * 配置
     *
     * @return
     */
    public T_Configuration getConfiguration() {
        return configurationMapper.getConfig();
    }

    @Value("${pay.api.gte}") private String gtePayApi;
    @Value("${pay.notify.async}") private String asyncNotifyUrl;
    @Value("${pay.notify.sync}") private String syncNotifyUrl;
    @Value("${pay.notify.callback_show}") private String callbackShowUrl;
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
                .add("showUrl", callbackShowUrl)
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

        Map<String, Object> params = new HashMap<>();
        params.put("orderNo", orderNo);
        params.put("uid", user.getId());
        params.put("fee", totalFee);
        params.put("createTime", now);
        params.put("goodsName", goodsName);
        params.put("vipPlusDay", day);
        params.put("payState", 0);
        params.put("payExpireTime", payExpireTime);
        params.put("payUrl", payUrl);

        vipRechargeMapper.gte(params);

        return new RechargeVo(orderNo, payUrl);
    }

    /**
     * 轮询充值支付状态
     *
     * @param orderNo
     * @return
     */
    public boolean loopRechargeSuccess(String orderNo) {
        T_VipRecharge recharge = vipRechargeMapper.getByOrderNo(orderNo);
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
                vipRechargeMapper.updateRechargeState(orderNo, 1, new Date());

                T_User user = getUserById(recharge.getUid());

                // 更新会员到期时间
                syncUserVipExpireTime(user, recharge.getVipPlusDay());

                // 同步推广员的提成
                syncReferUser(user, recharge);

                return true;
            }
            // 否则，直接返回，继续等待
            return false;
        }

        return true;
    }

    /**
     * 同步推广员的提成
     * 根据该充值记录，找到该用户的上线推广员，计算提成
     *
     * @param user
     * @param recharge
     */
    public void syncReferUser(T_User user, T_VipRecharge recharge) {
        if (Objects.isNull(user)) {
            return;
        }
        if (user.getRegisterRefer() <= 0) {
            return;
        }

        T_Refer_User referUser = referMapper.getReferUserById(user.getRegisterRefer());
        if (Objects.isNull(referUser)) {
            return;
        }

        if (referUser.getRate() <= 0 || !referUser.isEnable()) {
            return;
        }

        int value = (int) (referUser.getRate() * recharge.getFee());
        String description = MessageFormat.format("{0}购买{1}，提成{2}", user.getUsername(), recharge.getGoodsName(), Moneys.format_pretty(value));

        // 先增加一条抽成记录
        Map<String, Object> params = new HashMap<>();
        params.put("ruid", user.getRegisterRefer());
        params.put("source", recharge.getOrderNo());
        params.put("sourceType", 1);
        params.put("sourceValue", recharge.getFee());
        params.put("value", value);
        params.put("createTime", new Date());
        params.put("description", description);
        params.put("rate", referUser.getRate());
        referMapper.createReferUserFee(params);
        // 同步余额
        referMapper.syncReferUserBalance(user.getRegisterRefer());
    }

    public RechargePreviewVo previewRecharge(String token) {

        T_User user = getUserByToken(token);
        Date now = new Date();
        LocalDateTime vipExpireLocalDateTime =
                (
                        Objects.nonNull(user.getVipExpireTime())
                                && user.getVipExpireTime().after(now)
                                ? user.getVipExpireTime() : now
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
            log.error("查询第三方订单，order={}", response);
            if (!"success".equals(json.get("status").getAsString())) {
                throw new BizException(18122008, json.get("message").getAsString());
            }
            JsonObject order = json.get("order").getAsJsonObject();
//            // 1：代表未支付；2：代表已支付
//            int payStatus = order.get("payStatus").getAsInt();
//            return 2 == payStatus;

            // fuck 对方的支付状态好像没用。支付了，依然显示未支付payStatus=1，
            // 无奈用payTime有无值来判断。
            // 希望这个值能说明是否支付

            return order.keySet().contains("payTime");
        } catch (IOException e) {
            log.error("充值订单查询第三方时失败，orderNo={}, e={}", orderNo, e);
            throw new BizException(18122003, "订单提交失败，请稍后再试");
        }


    }

    private void syncUserVipExpireTime(T_User user, int day) {
        if (Objects.isNull(user)) {
            return;
        }
        Date now = new Date();
        LocalDateTime vipExpireLocalDateTime =
                (
                        Objects.nonNull(user.getVipExpireTime())
                                && user.getVipExpireTime().after(now)
                                ? user.getVipExpireTime() : now
                )
                        .toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        Date expireTime = Date.from(vipExpireLocalDateTime.plusDays(day).atZone(ZoneId.systemDefault()).toInstant());
        vipRechargeMapper.updateVipExpire(user.getId(), expireTime);
    }

    public void syncRechargeByNotify(String orderNo) {
        try {
            loopRechargeSuccess(orderNo);
        } catch (Exception e) {
            log.error("第三方支付平台通知时失败，orderNo={}, e={}", orderNo, e);
            throw new BizException(18122101, "回调未成功");
        }
    }

    /**
     * 最新的版本信息
     *
     * @param deviceType
     * @return
     */
    public T_Version getLatestVersion(DeviceType deviceType) {
        return versionMapper.getLatestVersion(deviceType.getCode());
    }

    /**
     * 版本列表
     *
     * @param deviceType
     * @return
     */
    public List<T_Version> listVersion(DeviceType deviceType) {
        return versionMapper.listVersion(deviceType.getCode());
    }


}