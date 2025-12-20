package cn.edu.xmu.service.controller.dto;

import lombok.Data;

/**
 * 创建服务单请求
 */
@Data
public class CreateServiceOrderRequest {
    private Integer type;
    private ConsigneeInfo consignee;

    @Data
    public static class ConsigneeInfo {
        private String name;
        private String mobile;
    }
}

