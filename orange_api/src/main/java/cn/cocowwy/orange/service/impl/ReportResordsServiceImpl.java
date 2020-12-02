package cn.cocowwy.orange.service.impl;

import cn.cocowwy.orange.entity.ReportResords;
import cn.cocowwy.orange.mapper.ReportResordsMapper;
import cn.cocowwy.orange.service.ReportResordsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 举报记录表(ReportResords)表服务实现类
 *
 * @author Cocowwy
 * @since 2020-12-02 14:04:07
 */
@Service
public class ReportResordsServiceImpl extends ServiceImpl<ReportResordsMapper, ReportResords> implements ReportResordsService {
    @Autowired
    private ReportResordsMapper reportResordsMapper;

    @Override
    public boolean save(ReportResords dto) {
        return super.save(dto);
    }

}