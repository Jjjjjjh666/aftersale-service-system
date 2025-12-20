package cn.edu.xmu.service.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnumsTest {

    @Test
    void draftStatusOfShouldReturnEnum() {
        assertEquals(DraftStatus.PENDING, DraftStatus.of("PENDING"));
        assertThrows(IllegalArgumentException.class, () -> DraftStatus.of("UNKNOWN"));
    }

    @Test
    void serviceOrderStatusOfShouldReturnEnum() {
        assertEquals(ServiceOrderStatus.CREATED, ServiceOrderStatus.of("CREATED"));
        assertThrows(IllegalArgumentException.class, () -> ServiceOrderStatus.of("invalid"));
    }

    @Test
    void serviceOrderTypeValueOfShouldReturnEnum() {
        assertEquals(ServiceOrderType.ONSITE_REPAIR, ServiceOrderType.valueOf(0));
        assertThrows(IllegalArgumentException.class, () -> ServiceOrderType.valueOf(99));
    }
}
