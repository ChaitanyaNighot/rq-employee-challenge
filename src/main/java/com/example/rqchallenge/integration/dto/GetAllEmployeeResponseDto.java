package com.example.rqchallenge.integration.dto;

import com.example.rqchallenge.dto.Employee;
import lombok.Data;

import java.util.List;

@Data
public class GetAllEmployeeResponseDto {
    private String status;
    private List<Employee> data;
}
