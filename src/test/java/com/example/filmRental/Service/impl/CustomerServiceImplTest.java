package com.example.filmRental.Service.impl;

import com.example.filmRental.Dto.CustomerRequestDto;
import com.example.filmRental.Dto.CustomerResponseDto;
import com.example.filmRental.Dto.CustomerWithAddressResponseDto;
import com.example.filmRental.Dto.PagedResponse;
import com.example.filmRental.Entity.Address;
import com.example.filmRental.Entity.Customer;
import com.example.filmRental.Exception.NotFoundException;
import com.example.filmRental.Repository.AddressRepository;
import com.example.filmRental.Repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;     // <— AssertJ
import static org.junit.jupiter.api.Assertions.assertThrows;   // <— JUnit 5 for exceptions
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock private CustomerRepository customerRepository;
    @Mock private AddressRepository addressRepository;
    @Mock private NamedParameterJdbcTemplate jdbc;

    @InjectMocks
    private CustomerServiceImpl service;

    private Customer customer;
    private Address address;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10, Sort.by("customer_id").ascending());

        address = new Address();
        address.setAddress_id((short) 1);
        address.setAddress("47 MySakila Drive");
        address.setDistrict("Alberta");
        address.setCity_id((short) 300);
        address.setPostal_code("12345");
        address.setPhone("14033335568");
        address.setLast_update(Timestamp.from(Instant.now()));

        customer = new Customer();
        customer.setCustomer_id((short) 1);
        customer.setStore_id((short) 1);
        customer.setFirst_name("MARY");
        customer.setLast_name("SMITH");
        customer.setEmail("mary.smith@example.com");
        customer.setAddress_id((short) 1);
        customer.setActive(Boolean.TRUE);
        customer.setCreate_date(LocalDateTime.now());
        customer.setLast_update(Timestamp.from(Instant.now()));
    }

    // --------------------------
    // CREATE
    // --------------------------
    @Test
    void create_ok() {
        CustomerRequestDto req = new CustomerRequestDto();
        req.setStoreId((short) 1);
        req.setFirstName("ALICE");
        req.setLastName("WONDER");
        req.setEmail("alice@wonder.com");
        req.setAddressId((short) 1);
        req.setActive(true);

        when(addressRepository.findById((short) 1)).thenReturn(Optional.of(address));
        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> {
            Customer c = inv.getArgument(0);
            c.setCustomer_id((short) 1);
            return c;
        });

        CustomerResponseDto dto = service.create(req);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo((short) 1);
        assertThat(dto.getFirstName()).isEqualTo("ALICE");
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void create_invalid_addressId_less_than_1() {
        CustomerRequestDto req = new CustomerRequestDto();
        req.setStoreId((short) 1);
        req.setFirstName("A");
        req.setLastName("B");
        req.setEmail("a@b.com");
        req.setAddressId((short) 0); // invalid

        assertThrows(NotFoundException.class, () -> service.create(req));
        verifyNoInteractions(customerRepository);
    }

    @Test
    void create_address_not_found() {
        CustomerRequestDto req = new CustomerRequestDto();
        req.setStoreId((short) 1);
        req.setFirstName("A");
        req.setLastName("B");
        req.setEmail("a@b.com");
        req.setAddressId((short) 99);

        when(addressRepository.findById((short) 99)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.create(req));
        verify(customerRepository, never()).save(any());
    }

    // --------------------------
    // READ / FILTERS
    // --------------------------
    @Test
    void active_ok() {
        Page<Customer> page = new PageImpl<>(List.of(customer), pageable, 1);
        when(customerRepository.findByActive(Boolean.TRUE, pageable)).thenReturn(page);

        PagedResponse<CustomerResponseDto> resp = service.active(pageable);

        assertThat(resp).isNotNull();
        assertThat(resp.getTotalElements()).isEqualTo(1);
        assertThat(resp.getContent()).hasSize(1);
        assertThat(resp.getContent().get(0).getId()).isEqualTo((short) 1);
    }

    @Test
    void inactive_ok() {
        Page<Customer> page = new PageImpl<>(List.of(customer), pageable, 1);
        when(customerRepository.findByActive(Boolean.FALSE, pageable)).thenReturn(page);

        PagedResponse<CustomerResponseDto> resp = service.inactive(pageable);

        assertThat(resp.getTotalElements()).isEqualTo(1);
    }

    @Test
    void byLastName_ok() {
        Page<Customer> page = new PageImpl<>(List.of(customer), pageable, 1);
        when(customerRepository.findByLast_nameContainingIgnoreCase("SMI", pageable)).thenReturn(page);

        PagedResponse<CustomerResponseDto> resp = service.byLastName("SMI", pageable);

        assertThat(resp.getContent()).hasSize(1);
        assertThat(resp.getContent().get(0).getLastName()).isEqualTo("SMITH");
    }

    @Test
    void byFirstName_ok() {
        Page<Customer> page = new PageImpl<>(List.of(customer), pageable, 1);
        when(customerRepository.findByFirst_nameContainingIgnoreCase("MAR", pageable)).thenReturn(page);

        PagedResponse<CustomerResponseDto> resp = service.byFirstName("MAR", pageable);

        assertThat(resp.getContent()).hasSize(1);
        assertThat(resp.getContent().get(0).getFirstName()).isEqualTo("MARY");
    }

    @Test
    void byEmail_ok() {
        when(customerRepository.findByEmail("mary.smith@example.com")).thenReturn(Optional.of(customer));

        CustomerResponseDto dto = service.byEmail("mary.smith@example.com");

        assertThat(dto.getEmail()).isEqualTo("mary.smith@example.com");
        assertThat(dto.getFirstName()).isEqualTo("MARY");
    }

    @Test
    void byEmail_not_found() {
        when(customerRepository.findByEmail("x@y.com")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.byEmail("x@y.com"));
    }

    @Test
    void byPhone_ok() {
        Page<Customer> page = new PageImpl<>(List.of(customer), pageable, 1);
        when(customerRepository.findByPhone("14033335568", pageable)).thenReturn(page);

        PagedResponse<CustomerResponseDto> resp = service.byPhone("14033335568", pageable);

        assertThat(resp.getContent()).hasSize(1);
        assertThat(resp.getContent().get(0).getFirstName()).isEqualTo("MARY");
    }

    @Test
    void byCity_ok_maps_with_address_payload() {
        List<Map<String, Object>> rows = List.of(row(
                1, "MARY", "SMITH", "mary.smith@example.com", 1,
                "47 MySakila Drive", "Alberta", "Chennai", "India", "14033335568"
        ));
        when(jdbc.queryForList(anyString(), anyMap())).thenReturn(rows);
        when(jdbc.queryForObject(anyString(), anyMap(), eq(Long.class))).thenReturn(1L);

        PagedResponse<CustomerWithAddressResponseDto> resp = service.byCity("Chennai", pageable);

        assertThat(resp.getContent()).hasSize(1);
        CustomerWithAddressResponseDto dto = resp.getContent().get(0);
        assertThat(dto.getCity()).isEqualTo("Chennai");
        assertThat(dto.getCountry()).isEqualTo("India");
        assertThat(dto.getAddress()).isEqualTo("47 MySakila Drive");
    }

    @Test
    void byCountry_ok_maps_with_address_payload() {
        List<Map<String, Object>> rows = List.of(row(
                1, "MARY", "SMITH", "mary.smith@example.com", 1,
                "47 MySakila Drive", "Alberta", "Chennai", "India", "14033335568"
        ));
        when(jdbc.queryForList(anyString(), anyMap())).thenReturn(rows);
        when(jdbc.queryForObject(anyString(), anyMap(), eq(Long.class))).thenReturn(1L);

        PagedResponse<CustomerWithAddressResponseDto> resp = service.byCountry("India", pageable);

        assertThat(resp.getContent()).hasSize(1);
        assertThat(resp.getContent().get(0).getCountry()).isEqualTo("India");
    }

    // --------------------------
    // UPDATES
    // --------------------------
    @Test
    void updateEmail_ok() {
        when(customerRepository.findById((short) 1)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));

        CustomerResponseDto dto = service.updateEmail((short) 1, "new@mail.com");

        assertThat(dto.getEmail()).isEqualTo("new@mail.com");
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void updatePhone_ok_updates_address_phone() {
        when(customerRepository.findById((short) 1)).thenReturn(Optional.of(customer));
        when(addressRepository.findById((short) 1)).thenReturn(Optional.of(address));

        CustomerResponseDto dto = service.updatePhone((short) 1, "999999");

        assertThat(dto).isNotNull();
        verify(addressRepository).save(argThat(a -> "999999".equals(a.getPhone())));
    }

    @Test
    void updateStore_ok() {
        when(customerRepository.findById((short) 1)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));

        CustomerResponseDto dto = service.updateStore((short) 1, (short) 2);

        assertThat(dto.getStoreId()).isEqualTo((short) 2);
    }

    @Test
    void updateAddress_ok_new_address_must_exist() {
        when(addressRepository.findById((short) 5)).thenReturn(Optional.of(new Address()));
        when(customerRepository.findById((short) 1)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));

        CustomerResponseDto dto = service.updateAddress((short) 1, (short) 5);

        assertThat(dto.getAddressId()).isEqualTo((short) 5);
    }

    @Test
    void updateAddress_not_found_throws() {
        when(addressRepository.findById((short) 5)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.updateAddress((short) 1, (short) 5));
        verify(customerRepository, never()).save(any());
    }

    @Test
    void updateFirstName_ok() {
        when(customerRepository.findById((short) 1)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));

        CustomerResponseDto dto = service.updateFirstName((short) 1, "JANE");

        assertThat(dto.getFirstName()).isEqualTo("JANE");
    }

    @Test
    void updateLastName_ok() {
        when(customerRepository.findById((short) 1)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));

        CustomerResponseDto dto = service.updateLastName((short) 1, "DOE");

        assertThat(dto.getLastName()).isEqualTo("DOE");
    }

    @Test
    void update_on_missing_customer_throws_NotFound() {
        when(customerRepository.findById((short) 9)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.updateEmail((short) 9, "x@y.com"));
    }

    // --------------------------
    // Helpers
    // --------------------------
    private Map<String, Object> row(
            int customerId,
            String first, String last, String email, int active,
            String addr, String district, String city, String country, String phone) {
        Map<String, Object> m = new HashMap<>();
        m.put("customer_id", customerId);
        m.put("first_name", first);
        m.put("last_name", last);
        m.put("email", email);
        m.put("active", active);
        m.put("address", addr);
        m.put("district", district);
        m.put("city", city);
        m.put("country", country);
        m.put("phone", phone);
        return m;
    }
}
