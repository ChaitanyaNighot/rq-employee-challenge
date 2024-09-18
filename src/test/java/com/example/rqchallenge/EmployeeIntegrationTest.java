package com.example.rqchallenge;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.rqchallenge.config.EmployeeUrlConfigs;
import com.example.rqchallenge.dto.Employee;
import com.example.rqchallenge.exception.EmployeeIntegrationException;
import com.example.rqchallenge.integration.EmployeeIntegration;
import com.example.rqchallenge.integration.dto.EmployeeResponseDto;
import com.example.rqchallenge.integration.dto.GetAllEmployeeResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeIntegrationTest {

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private EmployeeUrlConfigs employeeUrlConfigs;
    @InjectMocks
    private EmployeeIntegration employeeIntegration;
    private String url = "http://baseurl:8080/employees";
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private final String SUCCESS = "success";

    @Test
    void testGetAllEmployees() {
        String url = "http://baseurl/employee";
        GetAllEmployeeResponseDto responseDto = new GetAllEmployeeResponseDto();
        responseDto.setStatus(SUCCESS);
        ResponseEntity<GetAllEmployeeResponseDto> responseEntity = new ResponseEntity<>(responseDto, HttpStatus.OK);

        when(employeeUrlConfigs.getBaseUrl()).thenReturn("http://baseurl");
        when(employeeUrlConfigs.getEmployeesResource()).thenReturn("/employee");
        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), isNull(), eq(GetAllEmployeeResponseDto.class)))
                .thenReturn(responseEntity);

        GetAllEmployeeResponseDto result = employeeIntegration.getAllEmployees();

        assertNotNull(result);
        assertEquals(result.getStatus(),SUCCESS);
    }

    @Test
    void testGetEmployeeById() {
        String id = "123";
        String url = "http://baseurl/employee/123";
        EmployeeResponseDto responseDto = new EmployeeResponseDto(); // Add necessary setup here
        responseDto.setStatus(SUCCESS);
        ResponseEntity<EmployeeResponseDto> responseEntity = new ResponseEntity<>(responseDto, HttpStatus.OK);

        when(employeeUrlConfigs.getBaseUrl()).thenReturn("http://baseurl");
        when(employeeUrlConfigs.getEmployeeResource()).thenReturn("/employee/");
        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), isNull(), eq(EmployeeResponseDto.class)))
                .thenReturn(responseEntity);

        EmployeeResponseDto result = employeeIntegration.getEmployeeById(id);

        assertNotNull(result);
        assertEquals(result.getStatus(),SUCCESS);
    }

    @Test
    void testCreateEmployee() {
        String url = "http://baseurl/employee/create";
        Employee newEmployee = new Employee("Raj", 50000, 32);
        EmployeeResponseDto responseDto = new EmployeeResponseDto(); // Add necessary setup
        responseDto.setData(newEmployee);
        responseDto.setStatus(SUCCESS);
        ResponseEntity<EmployeeResponseDto> responseEntity = new ResponseEntity<>(responseDto, HttpStatus.OK);

        when(employeeUrlConfigs.getBaseUrl()).thenReturn("http://baseurl");
        when(employeeUrlConfigs.getEmployeeCreateResource()).thenReturn("/employee/create");
        when(restTemplate.postForEntity(eq(url), any(HttpEntity.class), eq(EmployeeResponseDto.class)))
                .thenReturn(responseEntity);

        EmployeeResponseDto result = employeeIntegration.createEmployee("Raj", 50000, 32);
        Employee employee = result.getData();

        assertNotNull(result);
        assertEquals(employee.getEmployeeName(), "Raj");
        assertEquals(responseDto.getStatus(),SUCCESS);
    }

    @Test
    void testCreateEmployeeWithException() {
        String url = "http://baseurl/employee/create";
        Employee newEmployee = new Employee("Raj", 50000, 32);
        EmployeeResponseDto responseDto = new EmployeeResponseDto(); // Add necessary setup
        responseDto.setData(newEmployee);
        responseDto.setStatus("success");
        ResponseEntity<EmployeeResponseDto> responseEntity = new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);

        when(employeeUrlConfigs.getBaseUrl()).thenReturn("http://baseurl");
        when(employeeUrlConfigs.getEmployeeCreateResource()).thenReturn("/employee/create");
        when(restTemplate.postForEntity(eq(url), any(HttpEntity.class), eq(EmployeeResponseDto.class)))
                .thenReturn(responseEntity);

        EmployeeIntegrationException thrown = assertThrows(EmployeeIntegrationException.class, () -> {
            employeeIntegration.createEmployee("Raj", 50000, 32);
        });

    }

    @Test
    void testDeleteEmployee_Success() {
        String id = "123";
        String url = "http://baseurl/employee/delete/123";

        when(employeeUrlConfigs.getBaseUrl()).thenReturn("http://baseurl");
        when(employeeUrlConfigs.getEmployeeDeleteResource()).thenReturn("/employee/delete/");
        doNothing().when(restTemplate).delete(url);

        assertDoesNotThrow(() -> employeeIntegration.deleteEmployee(id));
    }

}

