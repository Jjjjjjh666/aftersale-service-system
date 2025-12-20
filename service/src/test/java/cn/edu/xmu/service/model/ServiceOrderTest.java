package cn.edu.xmu.service.model;

import cn.edu.xmu.common.exception.BusinessException;
import cn.edu.xmu.service.dao.po.ServiceOrderPo;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ServiceOrderTest {

    @Test
    void fromPoShouldHandleUnknownStatus() {
        ServiceOrderPo po = buildPo();
        po.setStatus("UNKNOWN");

        ServiceOrder order = ServiceOrder.fromPo(po);

        assertEquals(ServiceOrderStatus.CREATED, order.getStatus());
        assertEquals(po.getShopId(), order.getShopId());
    }

    @Test
    void toPoShouldCopyAllFields() {
        ServiceOrder order = ServiceOrder.builder()
                .id(8L)
                .shopId(9L)
                .aftersalesId(10L)
                .serviceProviderId(20L)
                .type(ServiceOrderType.ONSITE_REPAIR)
                .status(ServiceOrderStatus.ACCEPTED)
                .consignee("张三")
                .address("地址")
                .trackingNumber("TN")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ServiceOrderPo po = order.toPo();

        assertEquals(order.getId(), po.getId());
        assertEquals(order.getStatus().name(), po.getStatus());
        assertEquals(order.getType().getCode(), po.getType());
    }

    @Test
    void checkCreatedStatusShouldThrowWhenNotCreated() {
        ServiceOrder order = ServiceOrder.builder()
                .status(ServiceOrderStatus.ACCEPTED)
                .build();

        assertThrows(BusinessException.class, order::checkCreatedStatus);
    }

    @Test
    void checkAcceptedStatusShouldThrowWhenNotAccepted() {
        ServiceOrder order = ServiceOrder.builder()
                .status(ServiceOrderStatus.CREATED)
                .build();

        assertThrows(BusinessException.class, order::checkAcceptedStatus);
    }

    @Test
    void acceptShouldSetProviderAndStatus() {
        ServiceOrder order = ServiceOrder.builder()
                .status(ServiceOrderStatus.CREATED)
                .build();

        order.accept(66L);

        assertEquals(66L, order.getServiceProviderId());
        assertEquals(ServiceOrderStatus.ACCEPTED, order.getStatus());
    }

    @Test
    void cancelShouldUpdateStatus() {
        ServiceOrder order = ServiceOrder.builder()
                .status(ServiceOrderStatus.ACCEPTED)
                .build();

        order.cancel();

        assertEquals(ServiceOrderStatus.CANCELLED, order.getStatus());
    }

    private ServiceOrderPo buildPo() {
        ServiceOrderPo po = new ServiceOrderPo();
        po.setId(1L);
        po.setShopId(2L);
        po.setAftersalesId(3L);
        po.setServiceProviderId(4L);
        po.setType(ServiceOrderType.MAIL_IN_REPAIR.getCode());
        po.setStatus("CREATED");
        po.setConsignee("张三");
        po.setAddress("地址");
        po.setTrackingNumber("TN");
        po.setCreatedAt(LocalDateTime.now());
        po.setUpdatedAt(LocalDateTime.now());
        return po;
    }
}
