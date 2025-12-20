package cn.edu.xmu.aftersale.service;

import cn.edu.xmu.aftersale.dao.AftersaleOrderRepository;
import cn.edu.xmu.aftersale.model.AftersaleOrder;
import cn.edu.xmu.aftersale.model.AftersaleStatus;
import cn.edu.xmu.aftersale.model.AftersaleType;
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

    @InjectMocks
    private AftersaleService aftersaleService;

    private List<AftersaleConfirmStrategy> confirmStrategies;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // 设置策略支持的类型
        when(returnStrategy.support(0)).thenReturn(true);
        when(exchangeStrategy.support(1)).thenReturn(true);
        when(repairStrategy.support(2)).thenReturn(true);
        
        confirmStrategies = Arrays.asList(returnStrategy, exchangeStrategy, repairStrategy);
        aftersaleService = new AftersaleService(repository, confirmStrategies, null);
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
    }
}

