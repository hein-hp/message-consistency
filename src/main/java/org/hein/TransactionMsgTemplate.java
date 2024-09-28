package org.hein;

import com.alibaba.fastjson2.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class TransactionMsgTemplate {

    @Autowired
    private TransactionTemplate txTemplate;

    @Autowired
    private IBizMsgDeliveryService deliveryService;

    public <T> void doTransactionWithSendMsg(String beanName, Class<? extends MqProducer<T>> clazz, String topic, String tag, T msg, BizTask bizTask) {
        MqProducer<T> sender = ApplicationContextUtils.getBean(beanName, clazz);
        Long deliveryId = txTemplate.execute(status -> {
            // do biz
            bizTask.doBiz();
            // save delivery record
            BizMsgDeliveryRecord record = BizMsgDeliveryRecord.builder()
                    .deliveryBean(beanName)
                    .maxDeliveryRetryTime(5)
                    .deliveryRetryTime(0)
                    .msgTopic(topic)
                    .msgTag(tag)
                    .msgContent(JSON.toJSONString(msg))
                    .build();
            record.startDelivery();
            return deliveryService.createMsgDelivery(record);
        });
        if (deliveryId != null) {
            // send
            sender.sendMsg(topic, tag, msg);
            // finish
            deliveryService.finishMsgDelivery(deliveryId, false);
        }
    }
}