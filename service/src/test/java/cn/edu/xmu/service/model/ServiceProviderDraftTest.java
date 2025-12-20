package cn.edu.xmu.service.model;

import cn.edu.xmu.common.exception.BusinessException;
import cn.edu.xmu.service.dao.po.ServiceProviderDraftPo;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ServiceProviderDraftTest {

    @Test
    void fromPoShouldConvertFields() {
        ServiceProviderDraftPo po = new ServiceProviderDraftPo();
        po.setId(1L);
        po.setServiceProviderId(2L);
        po.setProviderName("服务商");
        po.setContactPerson("张三");
        po.setContactPhone("123456");
        po.setAddress("地址");
        po.setStatus("PENDING");
        po.setOpinion("无");
        po.setCreatedAt(LocalDateTime.now());
        po.setUpdatedAt(LocalDateTime.now());

        ServiceProviderDraft draft = ServiceProviderDraft.fromPo(po);

        assertEquals(po.getId(), draft.getId());
        assertEquals(DraftStatus.PENDING, draft.getStatus());
    }

    @Test
    void toPoShouldCopyFields() {
        ServiceProviderDraft draft = ServiceProviderDraft.builder()
                .id(3L)
                .serviceProviderId(4L)
                .providerName("provider")
                .contactPerson("person")
                .contactPhone("phone")
                .address("addr")
                .status(DraftStatus.APPROVED)
                .opinion("OK")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ServiceProviderDraftPo po = draft.toPo();

        assertEquals(draft.getProviderName(), po.getProviderName());
        assertEquals("APPROVED", po.getStatus());
    }

    @Test
    void checkPendingStatusShouldThrowWhenNotPending() {
        ServiceProviderDraft draft = ServiceProviderDraft.builder()
                .status(DraftStatus.APPROVED)
                .build();

        assertThrows(BusinessException.class, draft::checkPendingStatus);
    }

    @Test
    void approveAndRejectShouldUpdateStatus() {
        ServiceProviderDraft draft = ServiceProviderDraft.builder()
                .status(DraftStatus.PENDING)
                .build();

        draft.approve("通过");
        assertEquals(DraftStatus.APPROVED, draft.getStatus());
        assertEquals("通过", draft.getOpinion());

        draft.reject("拒绝");
        assertEquals(DraftStatus.REJECTED, draft.getStatus());
        assertEquals("拒绝", draft.getOpinion());
    }
}
