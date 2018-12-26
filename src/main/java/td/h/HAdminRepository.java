package td.h;

import org.apache.ibatis.session.RowBounds;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import td.h.mapper.*;
import td.h.o.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Pair<Long, List<T_User>> pageUser(Map<String, Object> params, int page, int pageSize) {
        long total = userMapper.count(params);
        List<T_User> values = userMapper.adminPage(params, new RowBounds((page - 1) * pageSize, pageSize));

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
        List<T_Refer_User> values = referMapper.pageReferUser(params, new RowBounds((page - 1) * pageSize, pageSize));

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

    public boolean addReferUser(Map<String, Object> params) {
        params.put("createTime", new Date());
        params.put("enable", Integer.parseInt(String.valueOf(params.getOrDefault("enable", 0))));
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
}