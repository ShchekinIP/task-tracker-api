package org.example.task.tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto {
    private Boolean answer;

    public static ResponseDto makeDefault(Boolean answer) {
        return builder().answer(answer).build();
    }
}
