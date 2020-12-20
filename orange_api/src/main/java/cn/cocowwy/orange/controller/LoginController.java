package cn.cocowwy.orange.controller;

import cn.cocowwy.orange.api.dto.ILoginOpenServiceDTO;
import cn.cocowwy.orange.api.svc.ILoginOpenService;
import cn.cocowwy.orange.api.svc.ITradeOpenService;
import cn.cocowwy.orange.entity.User;
import cn.cocowwy.orange.utils.WxOpenIdUtil;
import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

/**
 *@author Cocowwy
 *@create 2020-12-12-21:30
 */
@RestController
@Slf4j
@RefreshScope
public class LoginController {
    @Autowired
    ILoginOpenService loginOpenService;

    /**
     * 根据openId进行登录功能
     * 小程序用户唯一标识
     * @param code
     */
    @PostMapping("/LoginWx")
    public Map<String, Object> LoginWx(String code) {
        ILoginOpenServiceDTO.IUserLoginWxRespDTO iUserLoginWxRespDTO = loginOpenService.UserLoginWx(code);
        return BeanUtil.beanToMap(iUserLoginWxRespDTO);
    }

    /**
     * 登录
     *
     * @param username
     * @param password
     */
    @PostMapping("/Login")
    public Map<String, Object> Login(@RequestParam("username") String username, @RequestParam("password") String password) {
        ILoginOpenServiceDTO.UserLoginMesageRespDTO userLoginMesageRespDTO = loginOpenService.UserLoginMesage(username, password);
        return BeanUtil.beanToMap(userLoginMesageRespDTO);
    }

    /**
     * 注册
     */
    @PostMapping("/registered")
    public Map<String, Object> registered(@RequestParam("user") User user) {
        ILoginOpenServiceDTO.UserRegistered canBeRegisteredRespDTO = loginOpenService.UserRegistered(user);
        return BeanUtil.beanToMap(canBeRegisteredRespDTO);
    }
}
