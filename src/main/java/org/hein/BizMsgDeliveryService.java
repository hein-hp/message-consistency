package org.hein;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.hein.entity.BizMsgDeliveryDO;
import org.hein.mapper.BizMsgDeliveryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hein.BizMsgDeliveryRecord.MsgDeliveryStatus.*;

@Component
public class BizMsgDeliveryService implements IBizMsgDeliveryService {

    @Autowired
    private TransactionTemplate txTemplate;

    @Autowired
    private BizMsgDeliveryMapper deliveryMapper;

    @Override
    public Long createMsgDelivery(BizMsgDeliveryRecord record) {
        BizMsgDeliveryDO bizMsgDeliveryDO = new BizMsgDeliveryDO();
        // fill properties
        bizMsgDeliveryDO.setMsgTopic(record.getMsgTopic());
        bizMsgDeliveryDO.setMsgTag(record.getMsgTag());
        bizMsgDeliveryDO.setMsgContent(record.getMsgContent());
        bizMsgDeliveryDO.setDeliveryStatus(record.getDeliveryStatus().getCode());
        bizMsgDeliveryDO.setMaxDeliveryRetryTime(record.getMaxDeliveryRetryTime());
        bizMsgDeliveryDO.setDeliveryRetryTime(record.getDeliveryRetryTime());
        bizMsgDeliveryDO.setDeliveryBean(record.getDeliveryBean());
        // insert
        deliveryMapper.insert(bizMsgDeliveryDO);
        return bizMsgDeliveryDO.getId();
    }

    @Override
    public void tryMsgDelivery(Long deliveryId) {
        deliveryMapper.update(null,
                Wrappers.lambdaUpdate(BizMsgDeliveryDO.class)
                        .eq(BizMsgDeliveryDO::getId, deliveryId)
                        .setSql("delivery_retry_time = delivery_retry_time + 1")
        );
    }

    @Override
    public boolean finishMsgDelivery(Long deliveryId, boolean deleteAfterFinish) {
        BizMsgDeliveryRecord record = getMsgDelivery(deliveryId);
        if (record.getDeliveryStatus().equals(DELIVERY_STARTED)) {
            record.finishDelivery();
            return Boolean.TRUE.equals(txTemplate.execute(status -> {
                boolean updateSuccess = deliveryMapper.update(null,
                        Wrappers.lambdaUpdate(BizMsgDeliveryDO.class)
                                .eq(BizMsgDeliveryDO::getId, deliveryId)
                                .set(BizMsgDeliveryDO::getDeliveryStatus, record.getDeliveryStatus())
                ) > 0;
                boolean deleteSuccess = true;
                if (deleteAfterFinish) {
                    deleteSuccess = deliveryMapper.deleteById(deliveryId) > 0;
                }
                return updateSuccess && deleteSuccess;
            }));
        }
        return record.getDeliveryStatus() == DELIVERY_FINISHED;
    }

    @Override
    public BizMsgDeliveryRecord getMsgDelivery(Long deliveryId) {
        BizMsgDeliveryDO bizMsgDeliveryDO = deliveryMapper.selectById(deliveryId);
        return BizMsgDeliveryRecord.builder()
                .deliveryStatus(of(bizMsgDeliveryDO.getDeliveryStatus()))
                .msgContent(bizMsgDeliveryDO.getMsgContent())
                .build();
    }

    @Override
    public List<BizMsgDeliveryRecord> getStartedDeliveries() {
        return getDeliveries(DELIVERY_STARTED);
    }

    @Override
    public List<BizMsgDeliveryRecord> getFinishDeliveries() {
        return getDeliveries(DELIVERY_FINISHED);
    }

    private List<BizMsgDeliveryRecord> getDeliveries(BizMsgDeliveryRecord.MsgDeliveryStatus status) {
        List<BizMsgDeliveryDO> bizMsgDeliveries = deliveryMapper.selectList(
                Wrappers.lambdaQuery(BizMsgDeliveryDO.class)
                        .eq(BizMsgDeliveryDO::getDeliveryStatus, status.getCode())
        );
        if (bizMsgDeliveries != null && !bizMsgDeliveries.isEmpty()) {
            return bizMsgDeliveries.stream().map(bizMsgDeliveryDO -> BizMsgDeliveryRecord.builder()
                    .deliveryStatus(of(bizMsgDeliveryDO.getDeliveryStatus()))
                    .msgContent(bizMsgDeliveryDO.getMsgContent())
                    .id(bizMsgDeliveryDO.getId())
                    .build()).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
