package cn.edu.xmu.aftersale.model;

import cn.edu.xmu.aftersale.dao.po.AftersaleOrderPo;
import cn.edu.xmu.common.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AftersaleOrderTest {

    @Test
    void fromPoShouldConvertFields() {
        AftersaleOrderPo po = buildPo();
        po.setStatus(1);

        AftersaleOrder order = AftersaleOrder.fromPo(po);

        assertEquals(po.getId(), order.getId());
        assertEquals(AftersaleStatus.APPROVED, order.getStatus());
    }

    @Test
    void toPoShouldMirrorDomainValues() {
        AftersaleOrder order = AftersaleOrder.builder()
                .id(2L)
                .shopId(3L)
                .orderId(4L)
                .type(0)
                .status(AftersaleStatus.CANCELLED)
                .reason("reason")
                .conclusion("done")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        AftersaleOrderPo po = order.toPo();

        assertEquals(order.getType(), po.getType());
        assertEquals(Integer.valueOf(2), po.getStatus());
        assertEquals(order.getConclusion(), po.getConclusion());
    }

    @Test
    void checkPendingStatusShouldThrowWhenNotPending() {
        AftersaleOrder order = AftersaleOrder.builder()
                .status(AftersaleStatus.APPROVED)
                .build();

        assertThrows(BusinessException.class, order::checkPendingStatus);
    }

    @Test
    void checkApprovedStatusShouldThrowWhenNotApproved() {
        AftersaleOrder order = AftersaleOrder.builder()
                .status(AftersaleStatus.PENDING)
                .build();

        assertThrows(BusinessException.class, order::checkApprovedStatus);
    }

    @Test
    void approveCancelAndRejectShouldUpdateStatus() {
        AftersaleOrder order = AftersaleOrder.builder()
                .status(AftersaleStatus.PENDING)
                .build();

        order.approve("同意");
        assertEquals(AftersaleStatus.APPROVED, order.getStatus());
        assertEquals("同意", order.getConclusion());

        order.cancel();
        assertEquals(AftersaleStatus.CANCELLED, order.getStatus());

        order.reject("拒绝");
        assertEquals("拒绝", order.getConclusion());
    }

    @Test
    void getAftersaleTypeShouldConvertCode() {
        AftersaleOrder order = AftersaleOrder.builder()
                .type(2)
                .build();

        assertEquals(AftersaleType.REPAIR, order.getAftersaleType());
    }

    private AftersaleOrderPo buildPo() {
        AftersaleOrderPo po = new AftersaleOrderPo();
        po.setId(1L);
        po.setShopId(2L);
        po.setOrderId(3L);
        po.setType(1);
        po.setReason("reason");
        po.setStatus(0);
        po.setConclusion("结论");
        po.setGmtCreate(LocalDateTime.now());
        po.setGmtModified(LocalDateTime.now());
        return po;
    }
}
