package cn.cocowwy.orange.api.svc;

import cn.cocowwy.orange.api.dto.ITradeOpenServiceDTO;
import cn.cocowwy.orange.entity.Trade;


/**
 * 订单类对外提供接口
 *@author Cocowwy
 *@create 2020-12-12-17:25
 */
public interface ITradeOpenService {
    /**
     * 提供查询在线的订单
     * @param userId
     * @return
     */
    ITradeOpenServiceDTO.GetOnlineTradeRespDTO getOnlineTrade(Long userId);

    /**
     * 添加在线订单接口
     * @param trade
     * @return
     */
    ITradeOpenServiceDTO.AddOnLineTradeRespDTO addOnLineTrade(Trade trade);

}
