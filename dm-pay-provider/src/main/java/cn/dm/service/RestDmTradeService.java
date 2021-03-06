package cn.dm.service;

import cn.dm.common.Constants;
import cn.dm.common.IdWorker;
import cn.dm.pojo.DmOrder;
import cn.dm.vo.DmItemMessageVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.dm.mapper.DmTradeMapper;
import cn.dm.pojo.DmTrade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by zezhong.shang on 18-5-15.
 */
@RestController
public class RestDmTradeService {
    private static final Logger logger = LoggerFactory.getLogger(RestDmTradeService.class);
     @Resource
     private DmTradeMapper dmTradeMapper;
     @Resource
     private RabbitTemplate rabbitTemplate;


    @RequestMapping(value = "/insertTrade",method = RequestMethod.POST)
    public Integer insertTrade(@RequestBody DmItemMessageVo dmItemMessageVo) throws Exception {
        DmTrade qgTrade = new DmTrade();
        qgTrade.setId(IdWorker.getId().toString());
        qgTrade.setAmount(dmItemMessageVo.getAmount());
        qgTrade.setCreatedTime(Calendar.getInstance().getTime());
        qgTrade.setOrderNo(dmItemMessageVo.getOrderNo());
        qgTrade.setPayMethod(1);
        qgTrade.setTradeNo(dmItemMessageVo.getTradeNo());
//        发送消息修改订单状态
        rabbitTemplate.convertAndSend("topicExchange", "key.toUpdateOrder", dmItemMessageVo);
        logger.info("[insertTrade]" + "发送消息成功，消息的内容为："+dmItemMessageVo.toString());
        return dmTradeMapper.insertDmTrade(qgTrade);
    }
}
