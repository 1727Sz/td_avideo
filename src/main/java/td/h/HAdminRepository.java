package td.h;

import com.google.common.base.Strings;
import org.apache.ibatis.session.RowBounds;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import td.h.mapper.*;
import td.h.o.*;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class HAdminRepository {

    @Value("${server.domain}") private String domain;
    @Autowired private VideoMapper videoMapper;
    @Autowired private CommentMapper commentMapper;
    @Autowired private ConfigurationMapper configurationMapper;
    @Autowired private UserMapper userMapper;
    @Autowired private VersionMapper versionMapper;
    @Autowired private VipRechargeMapper vipRechargeMapper;
    @Autowired private ReferMapper referMapper;
    @Autowired private AdminMapper adminMapper;


    /**
     * 视频列表
     *
     * @param page
     * @param pageSize
     */
    public Pair<Long, List<T_Video>> pageVideo(Map<String, Object> params, int page, int pageSize) {
        long total = videoMapper.count(params);
        List<T_Video> values = videoMapper.adminPage(params, new RowBounds((page - 1) * pageSize, pageSize));
        return Pair.with(total, values);
    }

    public boolean deleteVideo(List<Integer> ids) {
        return videoMapper.delete(ids);
    }

    public boolean deleteComment(List<Integer> ids) {
        return commentMapper.delete(ids);
    }

    public boolean addVideo(String title, String cover, String playUrl) {
        Map<String, Object> params = new HashMap<>();
        params.put("title", title);
        params.put("cover", cover);
        params.put("playUrl", playUrl);
        return videoMapper.gte(params);
    }

    public boolean updateVideo(int id, String title, String cover, String playUrl) {
        Map<String, Object> params = new HashMap<>();
        params.put("title", title);
        params.put("cover", cover);
        params.put("playUrl", playUrl);
        params.put("id", id);
        params.put("updateTime", new Date());
        params.put("enable", true);
        return videoMapper.update(params);
    }

    public boolean updateConfiguration(int yearVipPrice, int quarterVipPrice, int monthVipPrice) {
        configurationMapper.delete();
        return configurationMapper.save(new T_Configuration(monthVipPrice, quarterVipPrice, yearVipPrice));
    }

    public Pair<Long, List<T_User.ComplexAdminUser>> pageUser(Map<String, Object> params, int page, int pageSize) {
        long total = userMapper.count(params);
        List<T_User.ComplexAdminUser> values = userMapper.adminPage(params, new RowBounds((page - 1) * pageSize, pageSize));

        return Pair.with(total, values);
    }

    public Pair<Long, List<T_Comment>> pageComment(Map<String, Object> params, int page, int pageSize) {
        long total = commentMapper.countComment(params);
        List<T_Comment> values = commentMapper.adminPage(params, new RowBounds((page - 1) * pageSize, pageSize));

        return Pair.with(total, values);
    }

    public Pair<Long, List<T_Version>> pageVersion(Map<String, Object> params, int page, int pageSize) {
        long total = versionMapper.countVersion(params);
        List<T_Version> values = versionMapper.pageVersion(params, new RowBounds((page - 1) * pageSize, pageSize));

        return Pair.with(total, values);
    }

    public boolean deleteVersion(List<Integer> ids) {
        return versionMapper.delete(ids);
    }

    public boolean enableVersion(List<Integer> ids) {
        return versionMapper.enable(ids);
    }

    public boolean disableVersion(List<Integer> ids) {
        return versionMapper.disable(ids);
    }

    public boolean updateVersion(int id, DeviceType deviceType, String version, String lowVersion, String apkUrl, String downloadUrl, String apkSize, String remark, boolean upgrade) {
        Map<String, Object> params = new HashMap<>();
        params.put("platform", deviceType.getCode());
        params.put("versionNo", version);
        params.put("lowVersionNo", lowVersion);
        params.put("apkURL", apkUrl);
        params.put("downloadUrl", downloadUrl);
        params.put("apkSize", apkSize);
        params.put("remark", remark);
        params.put("upgrade", upgrade);
        params.put("id", id);
        return versionMapper.update(params);
    }

    /**
     * 新增版本记录
     *
     * @param deviceType
     * @param version
     * @param lowVersion
     * @param apkUrl
     * @param downloadUrl
     * @param apkSize
     * @param remark
     * @param upgrade
     * @return
     */
    public boolean addVersion(DeviceType deviceType, String version, String lowVersion, String apkUrl, String downloadUrl, String apkSize, String remark, boolean upgrade) {
        Map<String, Object> params = new HashMap<>();
        params.put("platform", deviceType.getCode());
        params.put("versionNo", version);
        params.put("lowVersionNo", lowVersion);
        params.put("apkURL", apkUrl);
        params.put("downloadUrl", downloadUrl);
        params.put("apkSize", apkSize);
        params.put("remark", remark);
        params.put("upgrade", upgrade);
        return versionMapper.add(params);
    }

    public Pair<Long, List<T_Refer_User>> pageReferUser(Map<String, Object> params, int page, int pageSize) {
        long total = referMapper.countReferUser(params);
        List<T_Refer_User> values = referMapper
                .pageReferUser(params, new RowBounds((page - 1) * pageSize, pageSize))
                .stream()
                .peek(it -> it.setUrl(MessageFormat.format(referUserRegUrlTemplate, String.valueOf(it.getId()))))
                .collect(Collectors.toList());
        ;

        return Pair.with(total, values);
    }

    public Pair<Long, List<ComplexRefer>> pageComplexRefer(Map<String, Object> params, int page, int pageSize) {
        long total = referMapper.countComplexRefer(params);
        List<ComplexRefer> values = referMapper.pageComplexRefer(params, new RowBounds((page - 1) * pageSize, pageSize));

        return Pair.with(total, values);
    }

    /**
     * 推广员资金明细
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Pair<Long, List<T_Refer_Fee.ComplexReferFee>> pageReferFee(Map<String, Object> params, int page, int pageSize) {
        long total = referMapper.countComplexReferFee(params);
        List<T_Refer_Fee.ComplexReferFee> values = referMapper.pageComplexReferFee(params, new RowBounds((page - 1) * pageSize, pageSize));

        return Pair.with(total, values);
    }

    @Value("${refer.template.reg}") private String referUserRegUrlTemplate;

    public boolean addReferUser(Map<String, Object> params) {
        params.put("createTime", new Date());
        params.put("enable", Integer.parseInt(String.valueOf(params.getOrDefault("enable", 0))));
        params.put("url", referUserRegUrlTemplate);
        return referMapper.addReferUser(params);
    }

    public boolean updateReferUser(Map<String, Object> params) {
        params.put("enable", Integer.parseInt(String.valueOf(params.getOrDefault("enable", 0))));
        return referMapper.updateReferUser(params);
    }

    public boolean deleteReferUser(List<Integer> id) {
        return referMapper.deleteReferUser(id);
    }

    public boolean enableReferUser(List<Integer> id) {
        return referMapper.enableReferUser(id);
    }

    public boolean disableReferUser(List<Integer> id) {
        return referMapper.disableReferUser(id);
    }


    public Pair<Long, List<ComplexVipRecharge>> pageVipRecharge(Map<String, Object> params, int page, int pageSize) {
        long total = vipRechargeMapper.count(params);
        List<ComplexVipRecharge> values = vipRechargeMapper.adminPage(params, new RowBounds((page - 1) * pageSize, pageSize));

        return Pair.with(total, values);
    }

    public boolean operateReferUserFee(Map<String, Object> params) {
        int ruid = Integer.parseInt(String.valueOf(params.getOrDefault("ruid", "0")));
        int fee = ((int) (Float.parseFloat(String.valueOf(params.getOrDefault("fee", "0"))) * 100));
        String description = String.valueOf(params.get("description"));

        Map<String, Object> feeParams = new HashMap<>();
        feeParams.put("ruid", ruid);
        feeParams.put("description", description);
        feeParams.put("sourceType", 2);
        feeParams.put("sourceValue", fee);
        feeParams.put("value", fee);
        feeParams.put("createTime", new Date());
        if (!referMapper.operateFee(feeParams)) {
            return false;
        }
        // 同步余额
        return referMapper.syncReferUserBalance(ruid);
    }

    @Scheduled(fixedDelay = 1000 * 30)
    public void autoExpireVipRechargeOrder() {
        vipRechargeMapper.expire();
    }

    public Pair<Long, List<T_Admin>> pageAdmin(Map<String, Object> params, int page, int pageSize) {
        long total = videoMapper.count(params);
        List<T_Admin> values = adminMapper.page(params, new RowBounds((page - 1) * pageSize, pageSize));
        return Pair.with(total, values);
    }

    public boolean createAdmin(Map<String, Object> params) {
        String username = String.valueOf(params.getOrDefault("username", ""));
        if (Strings.isNullOrEmpty(username)) {
            return false;
        }
        if (adminMapper.alreadyHasUsername(username)) {
            return false;
        }
        return adminMapper.createNewAdmin(params);
    }

    public boolean updateAdminPassword(Map<String, Object> params) {
        return adminMapper.updatePassword(params);
    }


    public T_Admin getAdminByLogin(String username, String password) {

        // 特别注意：
        // 为防止数据库里的admin账号被误删导致登陆不上，此处设置一个默认系统管理员
        T_Admin defaultAdmin = T_Admin.defalutAadmin;
        if (defaultAdmin.getUsername().equals(username)) {
            if (defaultAdmin.getPassword().equals(password)) {
                return defaultAdmin;
            }
        }

        T_Admin admin = adminMapper.getByUserName(username);
        if (Objects.nonNull(admin)) {
            if (password.equals(admin.getPassword())) {
                return admin;
            }
        }
        return null;
    }

    public boolean deleteAdmin(List<Integer> id){
        return adminMapper.deleteAdmin(id);
    }
}