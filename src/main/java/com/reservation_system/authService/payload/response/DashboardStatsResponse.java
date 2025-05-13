// DashboardStatsResponse.java
package com.reservation_system.authService.payload.response;

import lombok.Data;

@Data
public class DashboardStatsResponse {
    private Long totalRooms;
    private Long totalReservations;
    private Long totalUsers;
    private Long pendingReservations;
    private Long confirmedReservations;
    private Long cancelledReservations;
    private Long todayReservations;
    private Long weekReservations;
}


