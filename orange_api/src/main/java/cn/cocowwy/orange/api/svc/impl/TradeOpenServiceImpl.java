package cn.cocowwy.orange.api.svc.impl;

import cn.cocowwy.orange.api.dto.ITradeOpenServiceDTO;
import cn.cocowwy.orange.api.svc.ITradeOpenService;
import cn.cocowwy.orange.entity.Trade;
import cn.cocowwy.orange.entity.User;
import cn.cocowwy.orange.service.TradeService;
import cn.cocowwy.orange.service.UserService;
import cn.cocowwy.orange.utils.*;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 订单操作对外服务
 * @author Cocowwy
 * @create 2020-12-12-17:35
 */
@Service
public class TradeOpenServiceImpl implements ITradeOpenService {
    @Autowired
    private NacosParam nacosParam;

    @Autowired
    private AutoSetDefault autoSetDefault;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private UserService userService;

    /**
     * 得到在线订单列表
     * @param userId
     * @return
     */
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
        returnList.stream()
                .filter(o -> "0".equals(o.getStatusTag()))
                .sorted((x, y) -> x.getCreateTime().compareTo(y.getCreateTime()));
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

    /**
     * 用户在线接单接口
     * @param tradeId
     * @param userId
     * @return
     */
    @Override
    public ITradeOpenServiceDTO.AcceptTradeRespDTO acceptTrade(Long tradeId, Long userId) {
        //TODO 判断用户是否还能接单

        // redis新增接单信息 并且修改onLineTrade数据
        // 入redis
        String onLineTradekey = RedisUtils.getRedisKey("onLineTrade", String.valueOf(tradeId));
        String acceptTradekey = RedisUtils.getRedisKey("acceptTrade", String.valueOf(tradeId));
        Trade onLineTrade = (Trade) redisUtils.getJsonTemplate().opsForValue().get(onLineTradekey);
        Assert.notNull(onLineTrade, "对不起，该订单已下线！");
        // 删除online状态，新增acceptTrade订单
        // 订单创建者信息
        List<User> users = userService.queryByUserId(onLineTrade.getCreateUser());
        Assert.notNull(users.get(0), "对不起，该订单创建者信息有误！");

        // 填充信息
        onLineTrade.setStatusTag("1");
        onLineTrade.setAddress(onLineTrade.getAddress() == null ? users.get(0).getAddress1() : onLineTrade.getAddress());
        onLineTrade.setAcceptUser(userId);
        onLineTrade.setAcceptTime(LocalDateTimeUtil.now());
        onLineTrade.setChangeTime(LocalDateTimeUtil.now());

        // 删除online的  新增accept的
        redisUtils.getJsonTemplate().delete(onLineTradekey);
        redisUtils.getJsonTemplate().opsForValue().set(acceptTradekey, onLineTrade);

        // 修改数据库订单状态
        tradeService.updateByTradeId(tradeId, onLineTrade);

        // 返回信息  过滤掉用户敏感信息
        User user = new User();
        user.setName(users.get(0).getName());
        user.setSex(users.get(0).getSex());
        user.setAddress1(users.get(0).getAddress1());
        user.setAddress2(users.get(0).getAddress2());
        user.setPhone(users.get(0).getPhone());
        user.setUserRealName(users.get(0).getUserRealName());
        user.setWxId(users.get(0).getWxId());
        return ITradeOpenServiceDTO.AcceptTradeRespDTO.builder().trade(onLineTrade).user(user).build();
    }

    /**
     * 查询订单记录，即已接单和未完成的派单接口
     * @param userId
     * @return
     */
    @Override
    public ITradeOpenServiceDTO.QueryTradeRecordsRespDTO queryTradeRecords(Long userId) {
        //拿到redis上的接单信息
        Set<String> keys = redisUtils.getJsonTemplate().keys("acceptTrade" + "*");
        List<Trade> inTrades = new ArrayList<>();
        for (String key : keys) {
            inTrades.add((Trade) redisUtils.getJsonTemplate().opsForValue().get(key));
        }

        // 根据userId和状态进行过滤操作
        inTrades.stream().filter(o -> userId.equals(o.getAcceptUser()) && "1".equals(o.getStatusTag()));

        List<Map<String, Object>> inMap = new ArrayList<>();
        for (Trade inTrade : inTrades) {
            List<User> users = userService.queryByUserId(inTrade.getCreateUser());
            Assert.notNull(users.get(0), "该下单用户信息有误！");
            Map<String, Object> map = BeanUtil.beanToMap(inTrade);
            map.put("name", users.get(0).getName());
            map.put("address1", users.get(0).getAddress1());
            map.put("phone", users.get(0).getPhone());
            map.put("userRealName", users.get(0).getUserRealName());
            map.put("wxId", users.get(0).getWxId());
            inMap.add(map);
        }

        // 历史单查询
        List<Trade> outTrades = tradeService.qureyHisByUserId(userId);

        // 读取Redis上全部online信息
        Set<String> keysOnLine = redisUtils.getJsonTemplate().keys("onLineTrade" + "*");
        for (String key : keysOnLine) {
            Trade trade = (Trade) redisUtils.getJsonTemplate().opsForValue().get(key);
            outTrades.add(trade);
        }

        // 根据userId和状态进行过滤操作
        outTrades.stream().filter(o -> userId.equals(o.getCreateUser()) && "0".equals(o.getStatusTag()));

        return ITradeOpenServiceDTO.QueryTradeRecordsRespDTO
                .builder()
                .inTrade(inMap)
                .outTrade(outTrades)
                .build();
    }

    /**
     * 订单完成接口
     * @param tradeId
     * @return
     */
    @Override
    public ITradeOpenServiceDTO.AccomplishTradeRespDTO accomplishTrade(Long tradeId) {

        // 修改状态 入库
        String acceptTradeKey = RedisUtils.getRedisKey("acceptTrade", String.valueOf(tradeId));
        Trade acceptTrade = (Trade) redisUtils.getByKey(acceptTradeKey);
        Assert.notNull(acceptTrade, ErrorMsg.ERROR_OFFLINE_TRADE);
        acceptTrade.setStatusTag("2");
        acceptTrade.setChangeTime(LocalDateTime.now());
        tradeService.updateByTradeId(acceptTrade.getTradeId(), acceptTrade);

        // redis上删除该信息
        redisUtils.rmvByKey(acceptTradeKey);

        return ITradeOpenServiceDTO.AccomplishTradeRespDTO
                .builder()
                .result(true)
                .message("订单完工！")
                .build();
    }
}
