package org.hein;

import lombok.Data;

import java.util.List;

/**
 * 消息投递服务
 */
public interface IBizMsgDeliveryService {

    /**
     * 创建消息投递记录
     */
    Long createMsgDelivery(BizMsgDeliveryRecord record);

    /**
     * 尝试消息投递（投递次数加一）
     */
    void tryMsgDelivery(Long deliveryId);

    /**
     * 完成消息投递
     */
    boolean finishMsgDelivery(Long deliveryId, boolean deleteAfterFinish);

    /**
     * 获取消息投递记录
     */
    BizMsgDeliveryRecord getMsgDelivery(Long deliveryId);

    /**
     * 获取开始投递的消息投递记录
     */
    List<BizMsgDeliveryRecord> getStartedDeliveries();

    /**
     * 获取投递完成的消息投递记录
     */
    List<BizMsgDeliveryRecord> getFinishDeliveries();
}
