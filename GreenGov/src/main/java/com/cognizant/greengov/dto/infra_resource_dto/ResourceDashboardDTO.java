package com.cognizant.greengov.dto.infra_resource_dto;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResourceDashboardDTO {
    private long totalResources;
    private long totalQuantity;
    private Map<String, Long> statusCounts; 
    private Map<String, Long> typeDistribution; 
    private long lowStockAlerts; 
}
