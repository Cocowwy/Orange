package cn.cocowwy.orange.service.impl;

import cn.cocowwy.orange.entity.UserDetails;
import cn.cocowwy.orange.mapper.UserDetailsMapper;
import cn.cocowwy.orange.service.UserDetailsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户详细信息表(UserDetails)表服务实现类
 *
 * @author wangwy8@asiainfo.com
 * @since 2020-12-03 14:27:54
 */
@Service
public class UserDetailsServiceImpl extends ServiceImpl<UserDetailsMapper, UserDetails> implements UserDetailsService {
    @Autowired
    private UserDetailsMapper userDetailsMapper;

    @Override
    public boolean save(UserDetails dto) {
        return super.save(dto);
    }

}