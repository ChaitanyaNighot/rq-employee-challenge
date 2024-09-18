package com.example.rqchallenge.integration.dto;

import com.example.rqchallenge.dto.Employee;
import lombok.Data;

@Data
public class EmployeeResponseDto {

    private String status;
    private Employee data;

}
