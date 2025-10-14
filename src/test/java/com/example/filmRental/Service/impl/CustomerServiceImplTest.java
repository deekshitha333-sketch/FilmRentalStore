package com.example.filmRental.Service.impl;

import com.example.filmRental.Dto.*;
import com.example.filmRental.Entity.Address;
import com.example.filmRental.Entity.Customer;
import com.example.filmRental.Exception.NotFoundException;
import com.example.filmRental.Repository.AddressRepository;
import com.example.filmRental.Repository.CustomerRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock CustomerRepository customerRepository;
    @Mock AddressRepository addressRepository;
    @Mock org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate jdbc; // used in city/country pages
    @InjectMocks CustomerServiceImpl service;

    @Test
    void create_404_when_address_missing() {
        // Arrange a request with only the required field for this negative-path:
        CustomerRequestDto req = new CustomerRequestDto();
        // If your DTO uses wrapper type Short, adjust to (short) 999 or Short.valueOf((short)999) accordingly
        req.setAddressId((short) 999);

        when(addressRepository.findById((short) 999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Address not found");
    }

    @Test
    void byEmail_404_when_missing() {
        when(customerRepository.findByEmail("x@y.com")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.byEmail("x@y.com"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updatePhone_reads_address_and_updates() {
        short addrId = 3;
        Customer c = new Customer();
        c.setCustomer_id((short)10);
        c.setAddress_id(addrId);
        when(customerRepository.findById((short)10)).thenReturn(Optional.of(c));
        Address a = new Address();
        a.setAddress_id(addrId);
        when(addressRepository.findById(addrId)).thenReturn(Optional.of(a));

        CustomerResponseDto out = service.updatePhone((short)10, "777");
        assertThat(out).isNotNull();
        verify(addressRepository).save(argThat(ad -> "777".equals(ad.getPhone())));
    }

    @Test
    void active_inactive_delegates() {
        Pageable p = PageRequest.of(0,5);
        when(customerRepository.findByActive(true, p)).thenReturn(Page.empty(p));
        assertThat(service.active(p).getContent()).isEmpty();

        when(customerRepository.findByActive(false, p)).thenReturn(Page.empty(p));
        assertThat(service.inactive(p).getContent()).isEmpty();
    }
}