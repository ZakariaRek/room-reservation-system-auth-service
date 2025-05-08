package com.reservation_system.authService.payload.request;
import lombok.Data;

@Data
public class ReservationRequest {
    private String date;
    private String time;
    private Long roomId;
    private Long userId;
}
