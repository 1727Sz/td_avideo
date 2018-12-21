package td.h;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import td.h.o.*;

import static td.h.o.ApiHeader.HK_TOKEN;

@RestController
@RequestMapping("/video")
@Api(tags = "video", description = "视频")
public class HVideoApi {

    @Autowired private HRepository hRepository;
    @Autowired private FileService fileService;
    @Value("${server.domain}") private String domain;

    @GetMapping("/page")
    @ApiOperation(value = "分页查询视频列表", response = T_Video.VideoArrayVo.class)
    public ApiResponse page(@RequestParam(required = false, defaultValue = "1") int page,
                            @RequestParam(required = false, defaultValue = "20") int pageSize) {
        T_Video.VideoArrayVo videoArrayVo = new T_Video.VideoArrayVo();
        hRepository.pageVideo(page, pageSize).forEach(it -> videoArrayVo.getValues().add(it));
        return new ApiResponse.Ok("", videoArrayVo);
    }

    @GetMapping("/pageFollowed")
    @ApiOperation(value = "分页查询关注的视频列表", response = T_Video.VideoArrayVo.class)
    public ApiResponse pageFollowed(
            @RequestHeader(required = false, value = HK_TOKEN) String token,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "20") int pageSize) {
        T_Video.VideoArrayVo videoArrayVo = new T_Video.VideoArrayVo();
        hRepository.pageFollowedVideo(token, page, pageSize).forEach(it -> videoArrayVo.getValues().add(it));
        return new ApiResponse.Ok("", videoArrayVo);
    }

    @GetMapping("/byId")
    @ApiOperation(value = "视频详情", response = T_Video.class)
    public ApiResponse byId(
            @RequestHeader(HK_TOKEN) String token,
            @RequestParam int vid) {
        return new ApiResponse.Ok("", hRepository.getVideoDetailById(token, vid));
    }

    @PostMapping("/follow")
    @ApiOperation(value = "关注", response = ApiResponse.Ok.class)
    public ApiResponse follow(@RequestHeader(HK_TOKEN) String token,
                              @RequestBody FollowTargetRb body) {
        hRepository.followVideo(token, body.getValue());
        return new ApiResponse.Ok("操作成功");
    }

    @PostMapping("/followCancel")
    @ApiOperation(value = "取消关注", response = ApiResponse.Ok.class)
    public ApiResponse followCancel(@RequestHeader(HK_TOKEN) String token,
                                    @RequestBody FollowTargetRb body) {
        hRepository.followCancelVideo(token, body.getValue());
        return new ApiResponse.Ok("操作成功");
    }

    @GetMapping("/pageComments")
    @ApiOperation(value = "分页查询视频评论", response = T_Comment.CommentArrayVo.class)
    public ApiResponse pageComments(
            @RequestHeader(required = false, value = HK_TOKEN) String token,
            @ApiParam("视频ID") @RequestParam int vid,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "20") int pageSize) {
        T_Comment.CommentArrayVo commentArrayVo = new T_Comment.CommentArrayVo();
        hRepository.pageVideoComment(token, vid, page, pageSize).forEach(it -> commentArrayVo.getValues().add(it));
        return new ApiResponse.Ok("", commentArrayVo);
    }

    @PostMapping("/comment")
    @ApiOperation(value = "评论", response = ApiResponse.Ok.class)
    public ApiResponse followCancel(@RequestHeader(HK_TOKEN) String token,
                                    @RequestBody CommentCreateRb body) {
        hRepository.comment(body.getVid(), token, body.getContent());
        return new ApiResponse.Ok("操作成功");
    }

    @PostMapping("/comment/delete")
    @ApiOperation(value = "删除评论", response = ApiResponse.Ok.class)
    public ApiResponse deleteComment(@RequestHeader(HK_TOKEN) String token,
                                     @RequestBody CommentDeleteRb body) {
        hRepository.deleteComment(body.getCid(), token);
        return new ApiResponse.Ok("操作成功");
    }

    @PostMapping("/like")
    @ApiOperation(value = "点赞", response = ApiResponse.Ok.class)
    public ApiResponse like(
            @RequestHeader(HK_TOKEN) String token, @RequestBody LikeRb body) {
        hRepository.like(token, body.getVid());
        return new ApiResponse.Ok("操作成功");
    }




}
