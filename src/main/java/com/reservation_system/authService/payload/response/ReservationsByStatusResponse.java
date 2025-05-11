package com.reservation_system.authService.payload.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationsByStatusResponse {
    private String status;
    private Long count;
}
