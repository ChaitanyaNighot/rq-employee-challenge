package com.example.rqchallenge.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class EmployeeUrlConfigs {

    @Value("${employee.base-url}")
    private String baseUrl;

    @Value("${employee.employees-resource}")
    private String employeesResource;

    @Value("${employee.employee-resource}")
    private String employeeResource;

    @Value("${employee.employee-create-resource}")
    private String employeeCreateResource;

    @Value("${employee.employee-delete-resource}")
    private String employeeDeleteResource;

}
