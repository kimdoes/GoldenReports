package com.example.GoldenReport.DTO.HTTPResponseDTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HTTPResponseDTO {
    private int status;
    private String message;
}
