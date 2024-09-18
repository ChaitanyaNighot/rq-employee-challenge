package com.example.rqchallenge.controller;

import com.example.rqchallenge.dto.Employee;
import com.example.rqchallenge.service.IEmployeeService;
import com.example.rqchallenge.service.impl.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/employee")
public class EmployeeController implements IEmployeeController {

    private final IEmployeeService employeeService;

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    @GetMapping()
    public ResponseEntity<List<Employee>> getAllEmployees() throws IOException {
        logger.info("Received request to load all the employees.");
        List<Employee> employeeDtoList = employeeService.getAllEmployees();
        logger.info("Done loading all the employees.");
        return new ResponseEntity<>(employeeDtoList, HttpStatus.OK);
    }

    @GetMapping("/search/{searchString}")
    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        logger.info("Received request to load all the employees with search string : {}.", searchString);
        List<Employee> employees = employeeService.getEmployeesByNameSearch(searchString);
        logger.info("Done loading all the employees with search string : {}.", searchString);
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(String id) {
        logger.info("Received request to load employee details with id : {}.", id);
        Employee employee = employeeService.getEmployeeById(id);
        logger.info("Done loading all the employees with id : {}.", id);
        return new ResponseEntity<>(employee, HttpStatus.OK);
    }

    @Override
    @GetMapping("/highestSalary")
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        logger.info("Received request to load highest salary.");
        Integer highestSalary = employeeService.getHighestSalaryOfEmployees();
        logger.info("Done loading highest salary.");
        return new ResponseEntity<>(highestSalary, HttpStatus.OK);
    }

    @Override
    @GetMapping("/topTenHighestEarningEmployeeNames")
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        logger.info("Received request to load top ten highest earning employees.");
        List<String> top10EmployeeNames = employeeService.getTop10HighestEarningEmployeeNames();
        logger.info("Done loading top ten highest earning employees.");
        return new ResponseEntity<>(top10EmployeeNames, HttpStatus.OK);
    }

    @PostMapping()
    @Override
    public ResponseEntity<Employee> createEmployee( @RequestBody Map<String, Object> employeeInput) {
        logger.info("Received request to create employee.");
        String name = (String) employeeInput.get("name");
        Integer salary = Integer.parseInt(String.valueOf(employeeInput.get("salary")));
        Integer age = Integer.parseInt(String.valueOf(employeeInput.get("age")));
        Employee employee =  employeeService.createEmployee(name,salary,age);
        logger.info("Done creating new employee.");
        return new ResponseEntity<>(employee, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity deleteEmployeeById(@PathVariable String id) {
        logger.info("Received request to delete employee with id : {}.", id);
        employeeService.deleteEmployee(id);
        logger.info("Successfully deleted employee with id : {}.", id);
        return new  ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
