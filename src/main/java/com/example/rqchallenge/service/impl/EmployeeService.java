package com.example.rqchallenge.service.impl;

import com.example.rqchallenge.dto.Employee;
import com.example.rqchallenge.exception.EmployeeCreationException;
import com.example.rqchallenge.exception.EmployeeIntegrationException;
import com.example.rqchallenge.exception.EmployeeNotFoundException;
import com.example.rqchallenge.exception.EmployeeServiceException;
import com.example.rqchallenge.integration.EmployeeIntegration;
import com.example.rqchallenge.integration.dto.EmployeeResponseDto;
import com.example.rqchallenge.integration.dto.GetAllEmployeeResponseDto;
import com.example.rqchallenge.service.IEmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeService implements IEmployeeService {

    private final EmployeeIntegration employeeIntegration;

    @Autowired
    public EmployeeService(EmployeeIntegration employeeIntegration) {
        this.employeeIntegration = employeeIntegration;
    }

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    @Override
    public List<Employee> getAllEmployees() {
        logger.info("Received request to load all the employees.");
        try {
            GetAllEmployeeResponseDto getAllEmployeeResponseDto = employeeIntegration.getAllEmployees();
            logger.info("Done loading all the employees.");
            return getAllEmployeeResponseDto.getData();
        } catch (EmployeeIntegrationException e) {
            logger.error("Error occurred while fetching all the employees. Error : {}", e.getMessage());
            throw new EmployeeServiceException("Error fetching all employees", e);
        }
    }

    @Override
    public List<Employee> getEmployeesByNameSearch(String name) {
        logger.info("Received request to load all the employees with search string : {}.", name);
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Search name must not be null or empty");
        }

        try {
            GetAllEmployeeResponseDto getAllEmployeeResponseDto = employeeIntegration.getAllEmployees();
            logger.info("Done loading all the employees with search string : {}.", name);
            return getAllEmployeeResponseDto.getData().stream()
                    .filter(employee -> employee.getEmployeeName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        } catch (EmployeeIntegrationException e) {
            logger.error("Error occurred while searching for employee. Error : {}", e.getMessage());
            throw new EmployeeServiceException("Error searching for employees by name", e);
        }
    }

    @Override
    public Employee getEmployeeById(String id) {
        logger.info("Received request to load employees by id: {}.", id);
        validateId(id);
        try {
            EmployeeResponseDto employeeResponseDto = employeeIntegration.getEmployeeById(id);
            logger.info("Done loading employee details.");
            return employeeResponseDto.getData();
        } catch (EmployeeNotFoundException e) {
            logger.error("Unable to find employee with id : {}", id);
            throw e;
        } catch (EmployeeIntegrationException e) {
            throw new EmployeeServiceException("Error fetching employee with id: " + id, e);
        }
    }

    @Override
    public Integer getHighestSalaryOfEmployees() {
        logger.info("Received request to fetch highest salary.");
        try {
            GetAllEmployeeResponseDto getAllEmployeeResponseDto = employeeIntegration.getAllEmployees();
            Optional<Integer> highestSalary = getAllEmployeeResponseDto.getData().stream()
                    .map(Employee::getEmployeeSalary)
                    .max(Integer::compareTo);
            return highestSalary.orElseThrow(() -> new EmployeeServiceException("No employees found to determine highest salary"));
        } catch (EmployeeIntegrationException e) {
            throw new EmployeeServiceException("Error fetching highest salary of employees", e);
        }
    }

    @Override
    public List<String> getTop10HighestEarningEmployeeNames() {
        logger.info("Received request to fetch Top 10 Highest Earning Employees.");
        try {
            GetAllEmployeeResponseDto getAllEmployeeResponseDto = employeeIntegration.getAllEmployees();
            return getAllEmployeeResponseDto.getData().stream()
                    .sorted((e1, e2) -> Integer.compare(e2.getEmployeeSalary(), e1.getEmployeeSalary()))
                    .limit(10)
                    .map(Employee::getEmployeeName)
                    .collect(Collectors.toList());
        } catch (EmployeeIntegrationException e) {
            throw new EmployeeServiceException("Error fetching top 10 highest earning employee names", e);
        }
    }

    @Override
    public Employee createEmployee(String name, int salary, int age) {
        logger.info("Received request to create new Employee.");
        validateEmployeeData(name, salary, age);
        try {
            EmployeeResponseDto employeeResponseDto = employeeIntegration.createEmployee(name, salary, age);
            logger.info("Successfully created new Employee.");
            return employeeResponseDto.getData();
        } catch (EmployeeCreationException e) {
            throw e;
        } catch (EmployeeIntegrationException e) {
            throw new EmployeeServiceException("Error creating employee", e);
        }
    }

    @Override
    public String deleteEmployee(String id) {
        logger.info("Received request to delete employee with id : {}", id);
        validateId(id);
        try {
            employeeIntegration.deleteEmployee(id);
            logger.info("Successfully deleted employee with id : {}", id);
            return null;
        } catch (EmployeeNotFoundException e) {
            throw e;
        } catch (EmployeeIntegrationException e) {
            throw new EmployeeServiceException("Error deleting employee with id: " + id, e);
        }
    }

    private void validateId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee ID must not be null or empty");
        }
    }

    private void validateEmployeeData(String name, int salary, int age) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee name must not be null or empty");
        }
        if (salary <= 0) {
            throw new IllegalArgumentException("Employee salary must be greater than zero");
        }
        if (age <= 0) {
            throw new IllegalArgumentException("Employee age must be greater than zero");
        }
    }
}
