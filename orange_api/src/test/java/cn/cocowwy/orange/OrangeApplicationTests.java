package cn.cocowwy.orange;


import cn.cocowwy.orange.api.svc.ILoginOpenService;
import cn.cocowwy.orange.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@SpringBootTest
@Slf4j
class OrangeApplicationTests {
    @Autowired
    UserService userService;
    @Autowired
    ILoginOpenService loginOpenService;

    /**
     * 测试服务
     */
    @Test
    void testService() {
        // 测试esayCode模板生成是否有效
        System.out.println(userService.queryUser("111", "222").size());;
    }

    /**
     * 测试自定义userId生成策略
     * 时间戳+用户名的hashcode值 截取16为长度
     */
    @Test
    void  userIdRandomStrategy(){
//        Timestamp time = Timestamp.from(Instant.now());
//        LocalDateTime localDateTime = time.toLocalDateTime();
        String username="cocowwy";
        Timestamp time = Timestamp.valueOf(LocalDateTime.now());
        System.out.println(time.getTime());
        String temp=String.valueOf(time.getTime())+Long.valueOf(username.hashCode());
        System.out.println(temp.substring(0,16));

    }

    // 测试svc接口
    @Test
    void svc(){
        System.out.println(loginOpenService.UserLoginMesage("123456", "123456"));
        System.out.println( loginOpenService.UserLoginMesage("111", "111"));

    }
}
