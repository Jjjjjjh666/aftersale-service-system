package cn.edu.xmu.service.model.strategy;

import cn.edu.xmu.service.model.ServiceOrder;
import cn.edu.xmu.service.model.ServiceOrderStatus;
import cn.edu.xmu.service.model.ServiceOrderType;
import cn.edu.xmu.service.model.strategy.impl.MailInAcceptStrategy;
import cn.edu.xmu.service.model.strategy.impl.MailInCancelStrategy;
import cn.edu.xmu.service.model.strategy.impl.OnsiteAcceptStrategy;
import cn.edu.xmu.service.model.strategy.impl.OnsiteCancelStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServiceOrderStrategyTest {

    private final OnsiteAcceptStrategy onsiteAcceptStrategy = new OnsiteAcceptStrategy();
    private final OnsiteCancelStrategy onsiteCancelStrategy = new OnsiteCancelStrategy();
    private final MailInAcceptStrategy mailInAcceptStrategy = new MailInAcceptStrategy();
    private final MailInCancelStrategy mailInCancelStrategy = new MailInCancelStrategy();

    @Test
    void onsiteAcceptShouldAssignProviderAndChangeStatus() {
        ServiceOrder order = ServiceOrder.builder()
                .id(1L)
                .status(ServiceOrderStatus.CREATED)
                .build();

        onsiteAcceptStrategy.accept(order, 88L);

        assertEquals(88L, order.getServiceProviderId());
        assertEquals(ServiceOrderStatus.ACCEPTED, order.getStatus());
        assertTrue(onsiteAcceptStrategy.support(ServiceOrderType.ONSITE_REPAIR.getCode()));
        assertFalse(onsiteAcceptStrategy.support(ServiceOrderType.MAIL_IN_REPAIR.getCode()));
    }

    @Test
    void onsiteCancelShouldMarkOrderCancelled() {
        ServiceOrder order = ServiceOrder.builder()
                .id(2L)
                .status(ServiceOrderStatus.ACCEPTED)
                .build();

        onsiteCancelStrategy.cancel(order);

        assertEquals(ServiceOrderStatus.CANCELLED, order.getStatus());
        assertTrue(onsiteCancelStrategy.support(ServiceOrderType.ONSITE_REPAIR.getCode()));
        assertFalse(onsiteCancelStrategy.support(ServiceOrderType.MAIL_IN_REPAIR.getCode()));
    }

    @Test
    void mailInAcceptShouldSetAddressAndAccept() {
        ServiceOrder order = ServiceOrder.builder()
                .id(3L)
                .status(ServiceOrderStatus.CREATED)
                .build();

        mailInAcceptStrategy.accept(order, 99L);

        assertEquals(ServiceOrderStatus.ACCEPTED, order.getStatus());
        assertNotNull(order.getAddress());
        assertTrue(order.getAddress().contains("维修中心"));
        assertTrue(mailInAcceptStrategy.support(ServiceOrderType.MAIL_IN_REPAIR.getCode()));
    }

    @Test
    void mailInCancelShouldHandleTrackingNumberBranches() {
        ServiceOrder withTracking = ServiceOrder.builder()
                .id(4L)
                .status(ServiceOrderStatus.ACCEPTED)
                .trackingNumber("TN123")
                .build();
        ServiceOrder withoutTracking = ServiceOrder.builder()
                .id(5L)
                .status(ServiceOrderStatus.ACCEPTED)
                .build();

        mailInCancelStrategy.cancel(withTracking);
        mailInCancelStrategy.cancel(withoutTracking);

        assertEquals(ServiceOrderStatus.CANCELLED, withTracking.getStatus());
        assertEquals(ServiceOrderStatus.CANCELLED, withoutTracking.getStatus());
        assertTrue(mailInCancelStrategy.support(ServiceOrderType.MAIL_IN_REPAIR.getCode()));
        assertFalse(mailInCancelStrategy.support(ServiceOrderType.ONSITE_REPAIR.getCode()));
    }
}
