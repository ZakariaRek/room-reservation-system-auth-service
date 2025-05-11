package com.reservation_system.authService.payload.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
public class RoomUtilizationResponse {
    private Long roomId;
    private String roomName;
    private Long reservationCount;
    private Double utilizationRate; // As percentage
}
