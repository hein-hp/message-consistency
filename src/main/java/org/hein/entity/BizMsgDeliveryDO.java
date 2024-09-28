package org.hein.entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
public class BizMsgDeliveryDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5064147520385552589L;

    /**
     * 消息投递表主键
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
     * 消息投递状态，1-未投递 2-完成投递
     */
    private int deliveryStatus;

    /**
     * 消息投递重试最大次数，达到则业务告警，人工介入
     */
    private Integer maxDeliveryRetryTime;

    /**
     * 消息投递重试次数
     */
    private Integer deliveryRetryTime;

    /**
     * 负责投递该消息的 bean，定时任务拿到记录后根据该字段找到对应的 bean 进行具体的消息处理
     */
    private String deliveryBean;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 逻辑删除
     */
    private int isDeleted;
}