package cn.edu.xmu.service.controller;

import cn.edu.xmu.common.model.ReturnObject;
import cn.edu.xmu.service.controller.dto.ReviewDraftRequest;
import cn.edu.xmu.service.service.ServiceProviderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ServiceProviderControllerTest {

    @Mock
    private ServiceProviderService serviceProviderService;

    private ServiceProviderController controller;

    @BeforeEach
    void setUp() {
        controller = new ServiceProviderController(serviceProviderService);
    }

    @Test
    void reviewDraftShouldDelegateToServiceAndReturnOk() {
        ReviewDraftRequest request = new ReviewDraftRequest();
        request.setConclusion(1);
        request.setOpinion("通过");

        ReturnObject result = controller.reviewDraft(10L, request);

        verify(serviceProviderService).reviewDraft(10L, 1, "通过");
        assertEquals(0, result.getErrno());
        assertEquals("成功", result.getErrmsg());
    }
}
