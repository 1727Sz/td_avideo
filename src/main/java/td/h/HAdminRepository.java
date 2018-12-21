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

    public boolean deleteVideo(List<Integer> ids) {
        String sql = "delete from h.t_video where id in (" + ids.stream().map(String::valueOf).collect(Collectors.joining(",")) + ")";
        return jdbcTemplate.update(sql) > 0;
    }

    public boolean deleteComment(List<Integer> ids) {
        String sql = "delete from h.t_comment where id in (" + ids.stream().map(String::valueOf).collect(Collectors.joining(",")) + ")";
        return jdbcTemplate.update(sql) > 0;
    }

    public boolean addVideo(String title, String cover, String playUrl) {
        return jdbcTemplate.update(
                "insert into h.t_video (title, cover, playUrl) values (?, ?, ?)"
                , title, cover, playUrl) > 0;
    }

    public boolean updateVideo(int id, String title, String cover, String playUrl) {
        return jdbcTemplate.update(
                "update h.t_video set title = ?, cover = ?, playUrl = ?, enable = ?, updateTime = ? where id = ?",
                title, cover, playUrl, true, new Date(), id) > 0;
    }

    public boolean updateConfiguration(int yearVipPrice, int quarterVipPrice, int monthVipPrice){
        jdbcTemplate.update("delete from t_configuration");
        return jdbcTemplate.update(
                "insert into t_configuration (monthVipPrice, quarterVipPrice, yearVipPrice) values (?, ?, ?)",
                monthVipPrice, quarterVipPrice, yearVipPrice) > 0;
    }

    public Pair<Long, List<T_User>> pageUser(int page, int pageSize) {
        long total = jdbcTemplate.queryForObject(
                "select count(1) from h.t_user",
                Long.class);

        List<T_User> values = jdbcTemplate.query(
                "select * from h.t_user order by id desc limit ?, ?",
                new Object[]{(page - 1) * pageSize, pageSize},
                new BeanPropertyRowMapper<>(T_User.class));

        return Pair.with(total, values);
    }

    public Pair<Long, List<T_Comment>> pageComment(int page, int pageSize) {
        long total = jdbcTemplate.queryForObject(
                "select count(1) from h.t_comment",
                Long.class);

        List<T_Comment> values = jdbcTemplate.query(
                "select * from h.t_comment order by id desc limit ?, ?",
                new Object[]{(page - 1) * pageSize, pageSize},
                new BeanPropertyRowMapper<>(T_Comment.class));

        return Pair.with(total, values);
    }
}