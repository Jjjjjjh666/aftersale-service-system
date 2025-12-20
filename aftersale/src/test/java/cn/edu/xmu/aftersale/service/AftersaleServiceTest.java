package cn.edu.xmu.aftersale.service;

import cn.edu.xmu.aftersale.dao.AftersaleOrderRepository;
import cn.edu.xmu.aftersale.model.AftersaleOrder;
import cn.edu.xmu.aftersale.model.AftersaleStatus;
import cn.edu.xmu.aftersale.model.AftersaleType;
import cn.edu.xmu.aftersale.model.strategy.AftersaleCancelStrategy;
import cn.edu.xmu.aftersale.model.strategy.AftersaleConfirmStrategy;
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
 * 售后服务单元测试
 * 测试多态策略模式和状态转换
 */
class AftersaleServiceTest {

    @Mock
    private AftersaleOrderRepository repository;

    @Mock
    private AftersaleConfirmStrategy returnStrategy;

    @Mock
    private AftersaleConfirmStrategy exchangeStrategy;

    @Mock
    private AftersaleConfirmStrategy repairStrategy;

    @Mock
    private AftersaleCancelStrategy returnCancelStrategy;

    @Mock
    private AftersaleCancelStrategy exchangeCancelStrategy;

    @Mock
    private AftersaleCancelStrategy repairCancelStrategy;

    @InjectMocks
    private AftersaleService aftersaleService;

    private List<AftersaleConfirmStrategy> confirmStrategies;
    private List<AftersaleCancelStrategy> cancelStrategies;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // 设置审核策略支持的类型
        when(returnStrategy.support(0)).thenReturn(true);
        when(exchangeStrategy.support(1)).thenReturn(true);
        when(repairStrategy.support(2)).thenReturn(true);
        
        // 设置取消策略支持的类型
        when(returnCancelStrategy.support(0)).thenReturn(true);
        when(exchangeCancelStrategy.support(1)).thenReturn(true);
        when(repairCancelStrategy.support(2)).thenReturn(true);
        
        confirmStrategies = Arrays.asList(returnStrategy, exchangeStrategy, repairStrategy);
        cancelStrategies = Arrays.asList(returnCancelStrategy, exchangeCancelStrategy, repairCancelStrategy);
        
        aftersaleService = new AftersaleService(repository, confirmStrategies, cancelStrategies);
    }

    @Test
    void testConfirmAftersale_Return() {
        // 准备测试数据 - 退货类型
        Long shopId = 1L;
        Long id = 1L;
        AftersaleOrder order = AftersaleOrder.builder()
                .id(id)
                .shopId(shopId)
                .type(0)
                .status(AftersaleStatus.PENDING)
                .build();

        // Mock行为
        when(repository.findById(shopId, id)).thenReturn(order);

        // 执行测试
        String result = aftersaleService.confirmAftersale(shopId, id, true, "同意退货");

        // 验证结果
        assertEquals("APPROVED", result);
        verify(repository).findById(shopId, id);
        verify(returnStrategy).confirm(eq(order), eq(true), eq("同意退货"));
        verify(repository).save(order);
        
        // 验证只调用了退货策略
        verify(exchangeStrategy, never()).confirm(any(), any(), any());
        verify(repairStrategy, never()).confirm(any(), any(), any());
    }

    @Test
    void testConfirmAftersale_Exchange() {
        // 准备测试数据 - 换货类型
        Long shopId = 1L;
        Long id = 2L;
        AftersaleOrder order = AftersaleOrder.builder()
                .id(id)
                .shopId(shopId)
                .type(1)
                .status(AftersaleStatus.PENDING)
                .build();

        // Mock行为
        when(repository.findById(shopId, id)).thenReturn(order);

        // 执行测试
        String result = aftersaleService.confirmAftersale(shopId, id, true, "同意换货");

        // 验证结果
        assertEquals("APPROVED", result);
        verify(repository).findById(shopId, id);
        verify(exchangeStrategy).confirm(eq(order), eq(true), eq("同意换货"));
        verify(repository).save(order);
        
        // 验证只调用了换货策略
        verify(returnStrategy, never()).confirm(any(), any(), any());
        verify(repairStrategy, never()).confirm(any(), any(), any());
    }

    @Test
    void testConfirmAftersale_Repair() {
        // 准备测试数据 - 维修类型
        Long shopId = 1L;
        Long id = 3L;
        AftersaleOrder order = AftersaleOrder.builder()
                .id(id)
                .shopId(shopId)
                .type(2)
                .status(AftersaleStatus.PENDING)
                .build();

        // Mock行为
        when(repository.findById(shopId, id)).thenReturn(order);

        // 执行测试
        String result = aftersaleService.confirmAftersale(shopId, id, true, "同意维修");

        // 验证结果
        assertEquals("APPROVED", result);
        verify(repository).findById(shopId, id);
        verify(repairStrategy).confirm(eq(order), eq(true), eq("同意维修"));
        verify(repository).save(order);
        
        // 验证只调用了维修策略
        verify(returnStrategy, never()).confirm(any(), any(), any());
        verify(exchangeStrategy, never()).confirm(any(), any(), any());
    }

    @Test
    void testConfirmAftersale_Reject() {
        // 准备测试数据 - 拒绝审核
        Long shopId = 1L;
        Long id = 1L;
        AftersaleOrder order = AftersaleOrder.builder()
                .id(id)
                .shopId(shopId)
                .type(0)
                .status(AftersaleStatus.PENDING)
                .build();

        // Mock行为
        when(repository.findById(shopId, id)).thenReturn(order);

        // 执行测试
        String result = aftersaleService.confirmAftersale(shopId, id, false, "拒绝退货");

        // 验证结果
        verify(repository).findById(shopId, id);
        verify(returnStrategy).confirm(eq(order), eq(false), eq("拒绝退货"));
        verify(repository).save(order);
    }

    @Test
    void testCancelAftersale_Return() {
        // 准备测试数据 - 取消退货
        Long shopId = 1L;
        Long id = 4L;
        AftersaleOrder order = AftersaleOrder.builder()
                .id(id)
                .shopId(shopId)
                .type(0)
                .status(AftersaleStatus.APPROVED)
                .build();

        // Mock行为
        when(repository.findById(shopId, id)).thenReturn(order);

        // 执行测试
        String result = aftersaleService.cancelAftersale(shopId, id, "客户要求取消");

        // 验证结果
        assertEquals("CANCELLED", result);
        verify(repository).findById(shopId, id);
        verify(returnCancelStrategy).cancel(eq(order), eq("客户要求取消"));
        verify(repository).save(order);
        
        // 验证只调用了退货取消策略
        verify(exchangeCancelStrategy, never()).cancel(any(), any());
        verify(repairCancelStrategy, never()).cancel(any(), any());
    }

    @Test
    void testCancelAftersale_Exchange() {
        // 准备测试数据 - 取消换货
        Long shopId = 1L;
        Long id = 5L;
        AftersaleOrder order = AftersaleOrder.builder()
                .id(id)
                .shopId(shopId)
                .type(1)
                .status(AftersaleStatus.APPROVED)
                .build();

        // Mock行为
        when(repository.findById(shopId, id)).thenReturn(order);

        // 执行测试
        String result = aftersaleService.cancelAftersale(shopId, id, "商品缺货");

        // 验证结果
        assertEquals("CANCELLED", result);
        verify(repository).findById(shopId, id);
        verify(exchangeCancelStrategy).cancel(eq(order), eq("商品缺货"));
        verify(repository).save(order);
        
        // 验证只调用了换货取消策略
        verify(returnCancelStrategy, never()).cancel(any(), any());
        verify(repairCancelStrategy, never()).cancel(any(), any());
    }

    @Test
    void testCancelAftersale_Repair() {
        // 准备测试数据 - 取消维修（会调用service模块）
        Long shopId = 1L;
        Long id = 6L;
        AftersaleOrder order = AftersaleOrder.builder()
                .id(id)
                .shopId(shopId)
                .type(2)
                .status(AftersaleStatus.APPROVED)
                .build();

        // Mock行为
        when(repository.findById(shopId, id)).thenReturn(order);

        // 执行测试
        String result = aftersaleService.cancelAftersale(shopId, id, "服务商无法提供服务");

        // 验证结果
        assertEquals("CANCELLED", result);
        verify(repository).findById(shopId, id);
        verify(repairCancelStrategy).cancel(eq(order), eq("服务商无法提供服务"));
        verify(repository).save(order);
        
        // 验证只调用了维修取消策略
        verify(returnCancelStrategy, never()).cancel(any(), any());
        verify(exchangeCancelStrategy, never()).cancel(any(), any());
    }
}

