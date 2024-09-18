package com.example.rqchallenge.integration;

import com.example.rqchallenge.config.EmployeeUrlConfigs;
import com.example.rqchallenge.dto.Employee;
import com.example.rqchallenge.exception.EmployeeCreationException;
import com.example.rqchallenge.exception.EmployeeDeletionException;
import com.example.rqchallenge.exception.EmployeeIntegrationException;
import com.example.rqchallenge.exception.EmployeeNotFoundException;
import com.example.rqchallenge.integration.dto.EmployeeResponseDto;
import com.example.rqchallenge.integration.dto.GetAllEmployeeResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class EmployeeIntegration {

    private final RestTemplate restTemplate;
    private final EmployeeUrlConfigs employeeUrlConfigs;
    private static final Logger logger = LoggerFactory.getLogger(EmployeeIntegration.class);

    @Autowired
    public EmployeeIntegration(RestTemplate restTemplate, EmployeeUrlConfigs employeeUrlConfigs) {
        this.restTemplate = restTemplate;
        this.employeeUrlConfigs = employeeUrlConfigs;
    }

    public GetAllEmployeeResponseDto getAllEmployees() {
        String url = employeeUrlConfigs.getBaseUrl() + employeeUrlConfigs.getEmployeesResource();
        logger.info("Fetching all employees from URL: {}", url);
        try {
            ResponseEntity<GetAllEmployeeResponseDto> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    GetAllEmployeeResponseDto.class);

            logger.info("Received response status: {}", responseEntity.getStatusCode());
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                logger.info("Successfully fetched all employees.");
                return responseEntity.getBody();
            } else {
                logger.error("Failed to fetch employees: {}", responseEntity.getStatusCode());
                throw new EmployeeIntegrationException("Failed to fetch employees: " + responseEntity.getStatusCode(), null);
            }
        } catch (Exception e) {
            logger.error("Error while fetching all employees", e);
            throw new EmployeeIntegrationException("Error while fetching all employees", e);
        }
    }

    public EmployeeResponseDto getEmployeeById(String id) {
        validateId(id);
        String url = employeeUrlConfigs.getBaseUrl() + employeeUrlConfigs.getEmployeeResource() + id;
        logger.info("Fetching employee with ID: {} from URL: {}", id, url);
        try {
            ResponseEntity<EmployeeResponseDto> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    EmployeeResponseDto.class);

            logger.info("Received response status: {}", responseEntity.getStatusCode());
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                EmployeeResponseDto responseBody = responseEntity.getBody();
                if (responseBody != null) {
                    logger.info("Successfully fetched employee with ID: {}", id);
                    return responseBody;
                } else {
                    logger.warn("Employee not found with ID: {}", id);
                    throw new EmployeeNotFoundException("Employee not found with id: " + id);
                }
            } else {
                logger.error("Failed to fetch employee: {}", responseEntity.getStatusCode());
                throw new EmployeeIntegrationException("Failed to fetch employee: " + responseEntity.getStatusCode(), null);
            }
        } catch (Exception e) {
            logger.error("Error while fetching employee with ID: {}", id, e);
            throw new EmployeeIntegrationException("Error while fetching employee with id: " + id, e);
        }
    }

    public EmployeeResponseDto createEmployee(String name, int salary, int age) {
        validateEmployeeData(name, salary, age);
        String url = employeeUrlConfigs.getBaseUrl() + employeeUrlConfigs.getEmployeeCreateResource();
        logger.info("Creating employee with name: {}, salary: {}, age: {} at URL: {}", name, salary, age, url);
        Employee newEmployee = new Employee(name, salary, age);
        HttpEntity<Employee> requestEntity = new HttpEntity<>(newEmployee, null);

        try {
            ResponseEntity<EmployeeResponseDto> response = restTemplate.postForEntity(url, requestEntity, EmployeeResponseDto.class);
            logger.info("Received response status for creation: {}", response.getStatusCode());
            if (response.getStatusCode() == HttpStatus.CREATED) {
                logger.info("Successfully created employee.");
                return response.getBody();
            } else {
                logger.error("Failed to create employee: {}", response.getStatusCode());
                throw new EmployeeCreationException("Failed to create employee: " + response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Error while creating employee", e);
            throw new EmployeeIntegrationException("Error while creating employee", e);
        }
    }

    public void deleteEmployee(String id) {
        validateId(id);
        String url = employeeUrlConfigs.getBaseUrl() + employeeUrlConfigs.getEmployeeDeleteResource() + id;
        logger.info("Deleting employee with ID: {} at URL: {}", id, url);
        try {
            restTemplate.delete(url);
            logger.info("Successfully deleted employee with ID: {}", id);
        } catch (Exception e) {
            logger.error("Error while deleting employee with ID: {}", id, e);
            throw new EmployeeDeletionException("Error while deleting employee with id: " + id);
        }
    }

    private void validateId(String id) {
        if (id == null || id.trim().isEmpty()) {
            logger.warn("Invalid Employee ID: {}", id);
            throw new IllegalArgumentException("Employee ID must not be null or empty");
        }
    }

    private void validateEmployeeData(String name, int salary, int age) {
        if (name == null || name.trim().isEmpty()) {
            logger.warn("Invalid Employee name: {}", name);
            throw new IllegalArgumentException("Employee name must not be null or empty");
        }
        if (salary <= 0) {
            logger.warn("Invalid Employee salary: {}", salary);
            throw new IllegalArgumentException("Employee salary must be greater than zero");
        }
        if (age <= 0) {
            logger.warn("Invalid Employee age: {}", age);
            throw new IllegalArgumentException("Employee age must be greater than zero");
        }
    }
}
