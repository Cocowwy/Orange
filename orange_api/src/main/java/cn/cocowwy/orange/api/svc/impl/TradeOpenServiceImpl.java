package cn.cocowwy.orange.api.svc.impl;

import cn.cocowwy.orange.api.dto.ITradeOpenServiceDTO;
import cn.cocowwy.orange.api.svc.ITradeOpenService;
import cn.cocowwy.orange.entity.Trade;
import cn.cocowwy.orange.service.TradeService;
import cn.cocowwy.orange.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 *@author Cocowwy
 *@create 2020-12-12-17:35
 */
@Service
public class TradeOpenServiceImpl implements ITradeOpenService {
    @Autowired
    NacosParam nacosParam;

    @Autowired
    AutoSetDefault autoSetDefault;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    TradeService tradeService;


    @Override
    public ITradeOpenServiceDTO.GetOnlineTradeRespDTO getOnlineTrade(Long userId) {
        // 读取Redis上全部online信息
        Set<String> keys = redisUtils.getJsonTemplate().keys("onLineTrade" + "*");
        List<Trade> returnList = new ArrayList<>();
        for (String key : keys) {
            Object o = redisUtils.getJsonTemplate().opsForValue().get(key);
            returnList.add((Trade) o);
        }

        //过滤出正常的订单
        returnList.stream().filter(o -> "0".equals(o.getStatusTag()));
        return ITradeOpenServiceDTO.GetOnlineTradeRespDTO.builder().trades(returnList).build();
    }

    /**
     * 订单Online新增规则如下：
     * 使用redis的hash结构保存在线订单
     * key为订单号
     * 每次每笔订单会在redis存一份online版本，数据库存放一份记录版本
     * @param trade
     * @return
     */
    @Override
    public ITradeOpenServiceDTO.AddOnLineTradeRespDTO addOnLineTrade(Trade trade) {
        // 校验新增订单非空
        try {
            AuthCheckUtil.checkAddLogin(trade);
        } catch (Exception e) {
            // 处理异常信息
            return ITradeOpenServiceDTO.AddOnLineTradeRespDTO
                    .builder()
                    .result(false)
                    .message(e.getMessage())
                    .build();
        }

        // 新增默认值
        autoSetDefault.setTradeDefault(trade);

        // 入redis
        String key = RedisUtils.getRedisKey("onLineTrade", String.valueOf(trade.getTradeId()));
        redisUtils.getJsonTemplate().opsForValue().set(key, trade, nacosParam.getDefaultTips(), TimeUnit.HOURS);

        // 入数据库
        boolean save = tradeService.save(trade);
        if (!save) {
            return ITradeOpenServiceDTO.AddOnLineTradeRespDTO
                    .builder()
                    .result(false)
                    .message("订单入库不成功，请联系管理员！")
                    .build();
        }

        return ITradeOpenServiceDTO.AddOnLineTradeRespDTO
                .builder()
                .result(true)
                .message("派单成功!订单编号为" + trade.getTradeId())
                .build();
    }
}
