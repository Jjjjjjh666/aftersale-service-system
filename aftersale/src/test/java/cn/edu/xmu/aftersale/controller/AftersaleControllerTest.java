package cn.edu.xmu.aftersale.controller;

import cn.edu.xmu.aftersale.controller.dto.CancelAftersaleRequest;
import cn.edu.xmu.aftersale.controller.dto.ConfirmAftersaleRequest;
import cn.edu.xmu.aftersale.service.AftersaleService;
import cn.edu.xmu.common.model.ReturnNo;
import cn.edu.xmu.common.model.ReturnObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AftersaleControllerTest {

    @Mock
    private AftersaleService aftersaleService;

    private AftersaleController controller;

    @BeforeEach
    void setUp() {
        controller = new AftersaleController(aftersaleService);
    }

    @Test
    void confirmAftersaleShouldReturnStatusFromService() {
        ConfirmAftersaleRequest request = new ConfirmAftersaleRequest();
        request.setConfirm(true);
        request.setConclusion("同意");
        when(aftersaleService.confirmAftersale(1L, 2L, true, "同意"))
                .thenReturn("APPROVED");

        ReturnObject result = controller.confirmAftersale(1L, 2L, request);

        verify(aftersaleService).confirmAftersale(1L, 2L, true, "同意");
        Map<?, ?> data = (Map<?, ?>) result.getData();
        assertEquals(ReturnNo.OK.getCode(), data.get("errno"));
        assertEquals("APPROVED", data.get("status"));
    }

    @Test
    void cancelAftersaleShouldValidateConfirmFlag() {
        CancelAftersaleRequest request = new CancelAftersaleRequest();
        request.setConfirm(false);
        request.setReason("客户取消");

        ReturnObject result = controller.cancelAftersale(1L, 2L, request);

        assertEquals(ReturnNo.BAD_REQUEST.getCode(), result.getErrno());
        assertEquals("confirm必须为true", result.getErrmsg());
    }

    @Test
    void cancelAftersaleShouldInvokeServiceWhenConfirmed() {
        CancelAftersaleRequest request = new CancelAftersaleRequest();
        request.setConfirm(true);
        request.setReason("客户取消");
        when(aftersaleService.cancelAftersale(1L, 2L, "客户取消"))
                .thenReturn("CANCELLED");

        ReturnObject result = controller.cancelAftersale(1L, 2L, request);

        verify(aftersaleService).cancelAftersale(1L, 2L, "客户取消");
        Map<?, ?> data = (Map<?, ?>) result.getData();
        assertEquals(ReturnNo.OK.getCode(), data.get("errno"));
        assertEquals("CANCELLED", data.get("status"));
        assertEquals(ReturnNo.OK.getMessage(), data.get("errmsg"));
    }
}
