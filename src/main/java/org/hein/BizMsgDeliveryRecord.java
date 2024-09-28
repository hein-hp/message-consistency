package org.hein;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.Arrays;

@Data
@Builder
public class BizMsgDeliveryRecord {

    /**
     * 消息投递 id，关联投递实体的主键 id
     */
    private Long id;

    /**
     * 消息 topic
     */
    private String msgTopic;

    /**
     * 消息 tag
     */
    private String msgTag;

    /**
     * 消息内容
     */
    private String msgContent;

    /**
     * 投递状态
     */
    private MsgDeliveryStatus deliveryStatus;

    /**
     * 处理器 bean
     */
    private String deliveryBean;

    /**
     * 投递次数
     */
    private Integer deliveryRetryTime;

    /**
     * 最大投递次数
     */
    private Integer maxDeliveryRetryTime;

    /**
     * 投递次数限制
     */
    public boolean deliveryRetryLimited() {
        return deliveryRetryTime >= maxDeliveryRetryTime;
    }

    /**
     * 开始投递
     */
    public void startDelivery() {
        deliveryStatus = MsgDeliveryStatus.DELIVERY_STARTED;
    }

    /**
     * 完成投递
     */
    public void finishDelivery() {
        deliveryStatus = MsgDeliveryStatus.DELIVERY_FINISHED;
    }

    /**
     * 消息投递状态
     */
    @Getter
    public enum MsgDeliveryStatus {

        /**
         * 开始投递
         */
        DELIVERY_STARTED(1, "开始投递"),

        /**
         * 投递完成
         */
        DELIVERY_FINISHED(2, "投递完成");

        /**
         * code
         */
        private final Integer code;

        /**
         * desc
         */
        private final String desc;

        MsgDeliveryStatus(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public static MsgDeliveryStatus of(Integer status) {
            return Arrays.stream(values()).filter(e -> e.getCode().equals(status)).findFirst().orElse(null);
        }
    }
}
