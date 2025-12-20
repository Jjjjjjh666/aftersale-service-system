package cn.edu.xmu.service.controller;

import cn.edu.xmu.common.model.ReturnObject;
import cn.edu.xmu.service.service.ServiceOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ServiceProviderOrderControllerTest {

    @Mock
    private ServiceOrderService serviceOrderService;

    private ServiceProviderOrderController controller;

    @BeforeEach
    void setUp() {
        controller = new ServiceProviderOrderController(serviceOrderService);
    }

    @Test
    void acceptServiceOrderShouldInvokeService() {
        ReturnObject result = controller.acceptServiceOrder(3L, 8L);

        verify(serviceOrderService).acceptServiceOrder(3L, 8L);
        assertEquals(0, result.getErrno());
    }

    @Test
    void cancelServiceOrderShouldInvokeService() {
        ReturnObject result = controller.cancelServiceOrder(5L, 11L);

        verify(serviceOrderService).cancelServiceOrder(5L, 11L);
        assertEquals(0, result.getErrno());
    }
}
