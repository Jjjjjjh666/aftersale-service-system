package cn.edu.xmu.service;

import cn.edu.xmu.service.dao.ServiceOrderRepository;
import cn.edu.xmu.service.model.ServiceOrder;
import cn.edu.xmu.service.model.ServiceOrderStatus;
import cn.edu.xmu.service.model.ServiceOrderType;
import cn.edu.xmu.service.model.strategy.ServiceOrderAcceptStrategy;
import cn.edu.xmu.service.model.strategy.ServiceOrderCancelStrategy;
import cn.edu.xmu.service.service.ServiceOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 服务单服务测试
 * 测试多态策略模式的实现
 */
class ServiceOrderServiceTest {

    @Mock
    private ServiceOrderRepository repository;

    @Mock
    private ServiceOrderAcceptStrategy onsiteAcceptStrategy;

    @Mock
    private ServiceOrderAcceptStrategy mailInAcceptStrategy;

    @Mock
    private ServiceOrderCancelStrategy onsiteCancelStrategy;

    @Mock
    private ServiceOrderCancelStrategy mailInCancelStrategy;

    @InjectMocks
    private ServiceOrderService serviceOrderService;

    private List<ServiceOrderAcceptStrategy> acceptStrategies;
    private List<ServiceOrderCancelStrategy> cancelStrategies;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // 设置接受策略支持的类型
        when(onsiteAcceptStrategy.support(0)).thenReturn(true);    // 上门维修
        when(mailInAcceptStrategy.support(1)).thenReturn(true);     // 寄修
        
        // 设置取消策略支持的类型
        when(onsiteCancelStrategy.support(0)).thenReturn(true);    // 上门维修
        when(mailInCancelStrategy.support(1)).thenReturn(true);     // 寄修
        
        acceptStrategies = Arrays.asList(onsiteAcceptStrategy, mailInAcceptStrategy);
        cancelStrategies = Arrays.asList(onsiteCancelStrategy, mailInCancelStrategy);
        
        serviceOrderService = new ServiceOrderService(repository, acceptStrategies, cancelStrategies);
    }

    @Test
    void testCreateServiceOrder() {
        // 准备测试数据
        Long shopId = 1L;
        Long aftersalesId = 3L;
        Integer type = 0;
        String consignee = "张三";

        // Mock行为 - repository.create 会设置 order 的 ID
        doAnswer(invocation -> {
            ServiceOrder order = invocation.getArgument(0);
            order.setId(100L);
            return order;
        }).when(repository).create(any(ServiceOrder.class));

        // 执行测试
        Long orderId = serviceOrderService.createServiceOrder(shopId, aftersalesId, type, consignee);

        // 验证结果
        assertNotNull(orderId);
        assertEquals(100L, orderId);
        verify(repository).create(any(ServiceOrder.class));
    }

    @Test
    void testAcceptServiceOrder_OnsiteRepair() {
        // 准备测试数据 - 上门维修
        Long serviceProviderId = 1L;
        Long orderId = 1L;
        ServiceOrder order = ServiceOrder.builder()
                .id(orderId)
                .shopId(1L)
                .aftersalesId(3L)
                .type(ServiceOrderType.ONSITE_REPAIR)
                .status(ServiceOrderStatus.CREATED)
                .consignee("张三")
                .build();

        // Mock行为
        when(repository.findById(orderId)).thenReturn(order);

        // 执行测试
        serviceOrderService.acceptServiceOrder(serviceProviderId, orderId);

        // 验证结果
        verify(repository).findById(orderId);
        verify(onsiteAcceptStrategy).accept(eq(order), eq(serviceProviderId));
        verify(repository).save(order);
        
        // 验证只调用了上门维修策略，没有调用寄修策略
        verify(mailInAcceptStrategy, never()).accept(any(), any());
    }

    @Test
    void testAcceptServiceOrder_MailInRepair() {
        // 准备测试数据 - 寄修
        Long serviceProviderId = 2L;
        Long orderId = 2L;
        ServiceOrder order = ServiceOrder.builder()
                .id(orderId)
                .shopId(1L)
                .aftersalesId(6L)
                .type(ServiceOrderType.MAIL_IN_REPAIR)
                .status(ServiceOrderStatus.CREATED)
                .consignee("李四")
                .build();

        // Mock行为
        when(repository.findById(orderId)).thenReturn(order);

        // 执行测试
        serviceOrderService.acceptServiceOrder(serviceProviderId, orderId);

        // 验证结果
        verify(repository).findById(orderId);
        verify(mailInAcceptStrategy).accept(eq(order), eq(serviceProviderId));
        verify(repository).save(order);
        
        // 验证只调用了寄修策略，没有调用上门维修策略
        verify(onsiteAcceptStrategy, never()).accept(any(), any());
    }

    @Test
    void testCancelServiceOrder_OnsiteRepair() {
        // 准备测试数据 - 上门维修
        Long serviceProviderId = 1L;
        Long orderId = 3L;
        ServiceOrder order = ServiceOrder.builder()
                .id(orderId)
                .shopId(1L)
                .aftersalesId(102L)
                .type(ServiceOrderType.ONSITE_REPAIR)
                .status(ServiceOrderStatus.ACCEPTED)
                .serviceProviderId(serviceProviderId)
                .consignee("张三")
                .build();

        // Mock行为
        when(repository.findById(orderId)).thenReturn(order);

        // 执行测试
        serviceOrderService.cancelServiceOrder(serviceProviderId, orderId);

        // 验证结果
        verify(repository).findById(orderId);
        verify(onsiteCancelStrategy).cancel(eq(order));
        verify(repository).save(order);
        
        // 验证只调用了上门维修取消策略
        verify(mailInCancelStrategy, never()).cancel(any());
    }

    @Test
    void testCancelServiceOrder_MailInRepair() {
        // 准备测试数据 - 寄修
        Long serviceProviderId = 2L;
        Long orderId = 4L;
        ServiceOrder order = ServiceOrder.builder()
                .id(orderId)
                .shopId(1L)
                .aftersalesId(103L)
                .type(ServiceOrderType.MAIL_IN_REPAIR)
                .status(ServiceOrderStatus.ACCEPTED)
                .serviceProviderId(serviceProviderId)
                .consignee("李四")
                .build();

        // Mock行为
        when(repository.findById(orderId)).thenReturn(order);

        // 执行测试
        serviceOrderService.cancelServiceOrder(serviceProviderId, orderId);

        // 验证结果
        verify(repository).findById(orderId);
        verify(mailInCancelStrategy).cancel(eq(order));
        verify(repository).save(order);
        
        // 验证只调用了寄修取消策略
        verify(onsiteCancelStrategy, never()).cancel(any());
    }

    @Test
    void testCancelServiceOrderByAftersale() {
        // 准备测试数据
        Long aftersalesId = 3L;
        String reason = "客户要求取消";

        // 执行测试
        serviceOrderService.cancelServiceOrderByAftersale(aftersalesId, reason);

        // 验证 - 此方法目前是简化实现，主要验证不抛异常
        // 实际项目中需要验证更多行为
    }
}

