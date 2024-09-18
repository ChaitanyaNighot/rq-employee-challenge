package com.example.rqchallenge;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.rqchallenge.dto.Employee;
import com.example.rqchallenge.integration.EmployeeIntegration;
import com.example.rqchallenge.integration.dto.EmployeeResponseDto;
import com.example.rqchallenge.integration.dto.GetAllEmployeeResponseDto;
import com.example.rqchallenge.service.impl.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.List;

class EmployeeServiceTest {

    @Mock
    private EmployeeIntegration employeeIntegration;

    @InjectMocks
    private EmployeeService employeeService;

    private List<Employee> employees;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        employees = Arrays.asList(
                new Employee("Raj", 70000, 30),
                new Employee("Rahul", 60000, 40),
                new Employee("Chaitanya", 80000, 25)
        );
    }

    @Test
    void testGetAllEmployees() {
        GetAllEmployeeResponseDto responseDto = new GetAllEmployeeResponseDto();
        responseDto.setData(employees);
        when(employeeIntegration.getAllEmployees()).thenReturn(responseDto);

        List<Employee> result = employeeService.getAllEmployees();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.containsAll(employees));
    }

    @Test
    void testGetEmployeesByNameSearch() {
        GetAllEmployeeResponseDto responseDto = new GetAllEmployeeResponseDto();
        responseDto.setData(employees);
        when(employeeIntegration.getAllEmployees()).thenReturn(responseDto);

        List<Employee> result = employeeService.getEmployeesByNameSearch("Ra");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Raj", result.get(0).getEmployeeName());
    }

    @Test
    void testGetEmployeeById() {
        String id = "123";
        EmployeeResponseDto responseDto = new EmployeeResponseDto();
        Employee employee = new Employee("Raj", 70000, 30);
        responseDto.setData(employee);
        when(employeeIntegration.getEmployeeById(id)).thenReturn(responseDto);

        Employee result = employeeService.getEmployeeById(id);

        assertNotNull(result);
        assertEquals("Raj", result.getEmployeeName());
    }

    @Test
    void testGetHighestSalaryOfEmployees() {
        GetAllEmployeeResponseDto responseDto = new GetAllEmployeeResponseDto();
        responseDto.setData(employees);
        when(employeeIntegration.getAllEmployees()).thenReturn(responseDto);

        Integer result = employeeService.getHighestSalaryOfEmployees();

        assertNotNull(result);
        assertEquals(80000, result);
    }

    @Test
    void testGetTop10HighestEarningEmployeeNames() {
        GetAllEmployeeResponseDto responseDto = new GetAllEmployeeResponseDto();
        responseDto.setData(employees);
        when(employeeIntegration.getAllEmployees()).thenReturn(responseDto);

        List<String> result = employeeService.getTop10HighestEarningEmployeeNames();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Chaitanya", result.get(0));
        assertEquals("Raj", result.get(1));
        assertEquals("Rahul", result.get(2));
    }

    @Test
    void testCreateEmployee() {
        EmployeeResponseDto responseDto = new EmployeeResponseDto();
        Employee newEmployee = new Employee("Rahul", 50000, 28);
        responseDto.setData(newEmployee);
        when(employeeIntegration.createEmployee(any(String.class), any(Integer.class), any(Integer.class)))
                .thenReturn(responseDto);

        Employee result = employeeService.createEmployee("Rahul", 50000, 28);

        assertNotNull(result);
        assertEquals("Rahul", result.getEmployeeName());
        assertEquals(50000, result.getEmployeeSalary());
    }

    @Test
    void testDeleteEmployee() {
        String id = "123";
        doNothing().when(employeeIntegration).deleteEmployee(id);

        String result = employeeService.deleteEmployee(id);

        assertNull(result);
        verify(employeeIntegration, times(1)).deleteEmployee(id);
    }
}

