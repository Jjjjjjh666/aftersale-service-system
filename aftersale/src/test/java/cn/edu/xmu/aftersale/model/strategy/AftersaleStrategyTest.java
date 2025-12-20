package cn.edu.xmu.aftersale.model.strategy;

import cn.edu.xmu.aftersale.client.ServiceClient;
import cn.edu.xmu.aftersale.client.dto.CreateServiceOrderRequest;
import cn.edu.xmu.aftersale.model.AftersaleOrder;
import cn.edu.xmu.aftersale.model.AftersaleStatus;
import cn.edu.xmu.aftersale.model.AftersaleType;
import cn.edu.xmu.aftersale.model.strategy.impl.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AftersaleStrategyTest {

    private final ReturnConfirmStrategy returnConfirmStrategy = new ReturnConfirmStrategy();
    private final ExchangeConfirmStrategy exchangeConfirmStrategy = new ExchangeConfirmStrategy();
    private final ReturnCancelStrategy returnCancelStrategy = new ReturnCancelStrategy();
    private final ExchangeCancelStrategy exchangeCancelStrategy = new ExchangeCancelStrategy();

    @Mock
    private ServiceClient serviceClient;

    private RepairConfirmStrategy repairConfirmStrategy;
    private RepairCancelStrategy repairCancelStrategy;

    @BeforeEach
    void init() {
        repairConfirmStrategy = new RepairConfirmStrategy(serviceClient);
        repairCancelStrategy = new RepairCancelStrategy(serviceClient);
    }

    @Test
    void returnConfirmStrategyShouldApproveAccordingToFlag() {
        AftersaleOrder order = buildOrder(AftersaleType.RETURN);

        returnConfirmStrategy.confirm(order, true, "同意退货");

        assertEquals(AftersaleStatus.APPROVED, order.getStatus());
        assertTrue(returnConfirmStrategy.support(AftersaleType.RETURN.getCode()));

        returnConfirmStrategy.confirm(order, false, "拒绝退货");
        assertEquals("拒绝退货", order.getConclusion());
    }

    @Test
    void exchangeCancelShouldUpdateStatus() {
        AftersaleOrder order = buildOrder(AftersaleType.EXCHANGE);
        order.setStatus(AftersaleStatus.APPROVED);

        exchangeCancelStrategy.cancel(order, "库存不足");

        assertEquals(AftersaleStatus.CANCELLED, order.getStatus());
        assertTrue(exchangeCancelStrategy.support(AftersaleType.EXCHANGE.getCode()));
    }

    @Test
    void repairConfirmShouldInvokeServiceClient() {
        AftersaleOrder order = buildOrder(AftersaleType.REPAIR);

        repairConfirmStrategy.confirm(order, true, "维修");

        ArgumentCaptor<CreateServiceOrderRequest> captor = ArgumentCaptor.forClass(CreateServiceOrderRequest.class);
        verify(serviceClient).createServiceOrder(eq(order.getShopId()), eq(order.getId()), captor.capture());
        assertEquals(2, captor.getValue().getType());
        assertEquals(AftersaleStatus.APPROVED, order.getStatus());
        assertTrue(repairConfirmStrategy.support(AftersaleType.REPAIR.getCode()));
    }

    @Test
    void repairConfirmShouldThrowWhenServiceClientFails() {
        AftersaleOrder order = buildOrder(AftersaleType.REPAIR);
        doThrow(new RuntimeException("error"))
                .when(serviceClient).createServiceOrder(any(), any(), any());

        assertThrows(RuntimeException.class,
                () -> repairConfirmStrategy.confirm(order, true, "维修"));
    }

    @Test
    void repairConfirmRejectShouldSkipServiceClient() {
        AftersaleOrder order = buildOrder(AftersaleType.REPAIR);

        repairConfirmStrategy.confirm(order, false, "拒绝维修");

        verify(serviceClient, never()).createServiceOrder(any(), any(), any());
        assertEquals("拒绝维修", order.getConclusion());
    }

    @Test
    void repairCancelShouldAlwaysCancelOrder() {
        AftersaleOrder order = buildOrder(AftersaleType.REPAIR);
        repairCancelStrategy.cancel(order, "原因");
        verify(serviceClient).cancelServiceOrder(order.getShopId(), order.getId(), "原因");
        assertEquals(AftersaleStatus.CANCELLED, order.getStatus());
    }

    @Test
    void repairCancelShouldSwallowClientException() {
        AftersaleOrder order = buildOrder(AftersaleType.REPAIR);
        doThrow(new RuntimeException("err"))
                .when(serviceClient).cancelServiceOrder(any(), any(), any());

        repairCancelStrategy.cancel(order, "原因");

        assertEquals(AftersaleStatus.CANCELLED, order.getStatus());
    }

    private AftersaleOrder buildOrder(AftersaleType type) {
        return AftersaleOrder.builder()
                .id(10L)
                .shopId(20L)
                .type(type.getCode())
                .status(AftersaleStatus.PENDING)
                .build();
    }
}
