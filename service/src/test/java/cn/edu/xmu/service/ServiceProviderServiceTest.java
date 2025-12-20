package cn.edu.xmu.service;

import cn.edu.xmu.service.dao.ServiceProviderDraftRepository;
import cn.edu.xmu.service.model.DraftStatus;
import cn.edu.xmu.service.model.ServiceProviderDraft;
import cn.edu.xmu.service.service.ServiceProviderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 服务商服务测试
 * 测试服务商变更审核功能
 */
class ServiceProviderServiceTest {

    @Mock
    private ServiceProviderDraftRepository repository;

    @InjectMocks
    private ServiceProviderService serviceProviderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReviewDraft_Approve() {
        // 准备测试数据 - 审核通过
        Long draftId = 1L;
        Integer conclusion = 1;
        String opinion = "审核通过";
        
        ServiceProviderDraft draft = ServiceProviderDraft.builder()
                .id(draftId)
                .serviceProviderId(1L)
                .providerName("张三维修服务")
                .status(DraftStatus.PENDING)
                .build();

        // Mock行为
        when(repository.findById(draftId)).thenReturn(draft);

        // 执行测试
        serviceProviderService.reviewDraft(draftId, conclusion, opinion);

        // 验证结果
        verify(repository).findById(draftId);
        verify(repository).save(draft);
        assertEquals(DraftStatus.APPROVED, draft.getStatus());
        assertEquals(opinion, draft.getOpinion());
    }

    @Test
    void testReviewDraft_Reject() {
        // 准备测试数据 - 审核拒绝
        Long draftId = 2L;
        Integer conclusion = 0;
        String opinion = "资质不符合要求";
        
        ServiceProviderDraft draft = ServiceProviderDraft.builder()
                .id(draftId)
                .serviceProviderId(2L)
                .providerName("李四售后服务")
                .status(DraftStatus.PENDING)
                .build();

        // Mock行为
        when(repository.findById(draftId)).thenReturn(draft);

        // 执行测试
        serviceProviderService.reviewDraft(draftId, conclusion, opinion);

        // 验证结果
        verify(repository).findById(draftId);
        verify(repository).save(draft);
        assertEquals(DraftStatus.REJECTED, draft.getStatus());
        assertEquals(opinion, draft.getOpinion());
    }

    @Test
    void testReviewDraft_InvalidConclusion() {
        // 准备测试数据 - 无效的审核结果
        Long draftId = 3L;
        Integer conclusion = 999;  // 无效值
        String opinion = "测试";
        
        ServiceProviderDraft draft = ServiceProviderDraft.builder()
                .id(draftId)
                .serviceProviderId(3L)
                .providerName("王五技术服务")
                .status(DraftStatus.PENDING)
                .build();

        // Mock行为
        when(repository.findById(draftId)).thenReturn(draft);

        // 执行测试并验证异常
        assertThrows(IllegalArgumentException.class, () -> {
            serviceProviderService.reviewDraft(draftId, conclusion, opinion);
        });

        verify(repository).findById(draftId);
        verify(repository, never()).save(any());
    }

    @Test
    void testReviewDraft_WithNullOpinion() {
        // 准备测试数据 - 意见为空
        Long draftId = 4L;
        Integer conclusion = 1;
        String opinion = null;
        
        ServiceProviderDraft draft = ServiceProviderDraft.builder()
                .id(draftId)
                .serviceProviderId(4L)
                .providerName("测试服务商")
                .status(DraftStatus.PENDING)
                .build();

        // Mock行为
        when(repository.findById(draftId)).thenReturn(draft);

        // 执行测试
        serviceProviderService.reviewDraft(draftId, conclusion, opinion);

        // 验证结果 - 应该使用默认意见
        verify(repository).findById(draftId);
        verify(repository).save(draft);
        assertEquals(DraftStatus.APPROVED, draft.getStatus());
        assertEquals("审核通过", draft.getOpinion());
    }
}

