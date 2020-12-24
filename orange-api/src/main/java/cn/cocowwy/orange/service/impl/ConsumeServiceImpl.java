package cn.cocowwy.orange.service.impl;

import cn.cocowwy.orange.entity.Consume;
import cn.cocowwy.orange.mapper.ConsumeMapper;
import cn.cocowwy.orange.service.ConsumeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 消费记录表(Consume)表服务实现类
 *
 * @author wangwy8@asiainfo.com
 * @since 2020-12-03 14:27:15
 */
@Service
public class ConsumeServiceImpl extends ServiceImpl<ConsumeMapper, Consume> implements ConsumeService {
    @Autowired
    private ConsumeMapper consumeMapper;

    @Override
    public boolean save(Consume dto) {
        return super.save(dto);
    }

}