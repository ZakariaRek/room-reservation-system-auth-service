package com.reservation_system.authService.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeSlotResponse {
    private String fromTime;
    private String toTime;
    private boolean available;
}