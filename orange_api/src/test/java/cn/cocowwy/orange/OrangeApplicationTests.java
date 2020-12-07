package cn.cocowwy.orange;


import cn.cocowwy.orange.api.svc.ILoginOpenService;
import cn.cocowwy.orange.entity.Trade;
import cn.cocowwy.orange.service.UserService;
import cn.cocowwy.orange.utils.AutoSetDefault;
import cn.cocowwy.orange.utils.NacosParam;
import cn.cocowwy.orange.utils.RandomStrategy;
import cn.cocowwy.orange.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;


@SpringBootTest
@Slf4j
@RefreshScope
class OrangeApplicationTests {
    @Autowired
    UserService userService;
    @Autowired
    ILoginOpenService loginOpenService;
    @Autowired
    RedisTemplate redisTemplate;
    @Value("${config.defaultTips:1}")
    public Integer defaultTips;
    @Autowired
    AutoSetDefault autoSetDefault;
    @Autowired
    NacosParam nacosParam;

    /**
     * 测试nacos读取配置
     */
    @Test
    void testNacos() {
//        System.out.println(defaultTips);
//        System.out.println(autoSetDefault.setTradeDefault(Trade.builder().build()));
//        System.out.println(nacosParam.getDefaultTips());
//        System.out.println(autoSetDefault.setTradeDefault(Trade.builder().build()));
//        System.out.println(nacosParam.getWelcome());
        System.out.println(nacosParam.getDefaultTips());
        System.out.println(nacosParam.defaultTips);
    }

    /**
     * 测试服务
     */
    @Test
    void testService() {
        // 测试esayCode模板生成是否有效
        System.out.println(userService.queryUser("111", "222").size());
    }

    /**
     * 测试自定义userId生成策略
     * 时间戳+用户名的hashcode值 截取16为长度
     */
    @Test
    void userIdRandomStrategy() {
//        RandomStrategy.getRandomUserId();
//        String s1 = String.valueOf(LocalDateTime.now().getYear()).substring(2, 4);
       for(int i=0;i<10;i++){
           System.out.println(RandomStrategy.getRandomUserId());
       }

    }

    // 测试svc接口
    @Test
    void svc() {
        System.out.println(loginOpenService.UserLoginMesage("123456", "123456"));
        System.out.println(loginOpenService.UserLoginMesage("111", "111"));
    }

    @Test
    void redis() {

    }
}
