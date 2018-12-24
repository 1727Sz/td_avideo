package td.h;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import td.h.o.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

import static td.h.o.ApiHeader.HK_TOKEN;

@Slf4j
@RestController
@RequestMapping("/user")
@Api(tags = "user", description = "用户")
public class HUserApi {

    @Autowired private HRepository hRepository;
    @Autowired private FileService fileService;
    @Value("${server.domain}") private String domain;

    @PostMapping("/register")
    @ApiOperation(value = "注册", response = T_User.class)
    public ApiResponse register(@RequestBody UserRegisterRb body) {

        T_User user = hRepository.register(body.getUsername(), body.getPassword(), body.getRefer());
        return new ApiResponse.Ok("", user);
    }

    @PostMapping("/login")
    @ApiOperation(value = "登陆", response = T_User.class)
    public ApiResponse login(@RequestBody UserLoginRb body) {

        T_User user = hRepository.login(body.getUsername(), body.getPassword());
        return new ApiResponse.Ok("", user);
    }

    @PostMapping("/updatePassword")
    @ApiOperation(value = "修改密码", response = T_User.class)
    public ApiResponse updatePassword(
            @RequestHeader(HK_TOKEN) String token,
            @RequestBody UserPasswordUpdateRb body) {

        hRepository.updatePassword(token, body.getPassword(), body.getNewPassword());
        return new ApiResponse.Ok("密码修改成功，请重新登陆");
    }

    @PostMapping("/updateAvatar")
    @ApiOperation(value = "修改头像", response = T_User.class)
    public ApiResponse updateAvatar(
            @RequestHeader(HK_TOKEN) String token,
            @RequestBody UserPropertiesUpdateRb body) {

        hRepository.updateAvatar(token, body.getValue());
        return new ApiResponse.Ok("修改成功");
    }

    @PostMapping("/updateNickname")
    @ApiOperation(value = "修改昵称", response = T_User.class)
    public ApiResponse updateNickname(
            @RequestHeader(HK_TOKEN) String token,
            @RequestBody UserPropertiesUpdateRb body) {

        hRepository.updateNickname(token, body.getValue());
        return new ApiResponse.Ok("修改成功");
    }


    @PostMapping("/image/upload")
    @ApiOperation(value = "上传图片", response = UploadVo.class)
    public ApiResponse uploadImage(
            @RequestHeader(HK_TOKEN) String token,
            @RequestParam("file") MultipartFile file) throws IOException {
        hRepository.getUserByToken(token);
        ImagePath path = fileService.save(file);
        return new ApiResponse.Ok("", new UploadVo(path.showPath(domain), path.relativePath()));
    }

    @GetMapping("/config")
    @ApiOperation(value = "获取系统配置", response = T_Configuration.class)
    public ApiResponse getConfiguration() {
        return new ApiResponse.Ok("", hRepository.getConfiguration());
    }

    @PostMapping("/vip/recharge")
    @ApiOperation(value = "会员充值，生成支付订单", response = ApiResponse.Ok.class)
    public ApiResponse generateVipRechargeOrder(
            @RequestHeader(HK_TOKEN) String token,
            @RequestBody RechargeRb body) {
        RechargeVo rechargeVo = hRepository.generateVipRechargeOrder(token, body.getDay());
        return new ApiResponse.Ok("", rechargeVo);
    }

    @GetMapping("/vip/recharge/preview")
    @ApiOperation(value = "会员充值预览", response = RechargePreviewVo.class)
    public ApiResponse previewRecharge(@RequestHeader(HK_TOKEN) String token) {
        return new ApiResponse.Ok("", hRepository.previewRecharge(token));
    }

    @GetMapping("/vip/recharge/state")
    @ApiOperation(value = "校验充值是否成功", response = RechargeStateVo.class)
    public ApiResponse checkRechargeState(@RequestParam String orderNo){
        return new ApiResponse.Ok("", hRepository.loopRechargeSuccess(orderNo));
    }


}
