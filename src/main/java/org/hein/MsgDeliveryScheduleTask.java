package org.hein;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MsgDeliveryScheduleTask {

    private static final Logger log = LoggerFactory.getLogger(MsgDeliveryScheduleTask.class);

    @Autowired
    private IBizMsgDeliveryService deliveryService;

    @Scheduled(cron = "0 0 0,12 * * ?")
    public void retryMsgDeliveryJob() {
        List<BizMsgDeliveryRecord> retryDeliveries = deliveryService.getStartedDeliveries();
        retryDeliveries.forEach(record -> {
            if (record.deliveryRetryLimited()) {
                // alarm
                log.error("alarm.");
            } else {
                try {
                    deliveryService.tryMsgDelivery(record.getId());
                    MqProducer sender = ApplicationContextUtils.getBean(record.getDeliveryBean(), MqProducer.class);
                    // 这里也可以先查询一次下游业务系统的状态，如果已经成功则直接将投递记录推进为已完成
                    boolean success = true;
                    if (success) {
                        deliveryService.finishMsgDelivery(record.getId(), false);
                        return;
                    }
                    sender.sendMsg(record.getMsgTopic(), record.getMsgTag(), record.getMsgContent());
                    deliveryService.finishMsgDelivery(record.getId(), false);
                } catch (Exception e) {
                    log.error("exception.");
                }
            }
        });
    }
}
