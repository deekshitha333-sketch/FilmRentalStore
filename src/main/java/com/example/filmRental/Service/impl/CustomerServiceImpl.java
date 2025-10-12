package com.example.filmRental.Service.impl;

import com.example.filmRental.Dto.*;
import com.example.filmRental.Entity.Address;
import com.example.filmRental.Entity.Customer;
import com.example.filmRental.Exception.NotFoundException;
import com.example.filmRental.Mapper.CustomerMapper;
import com.example.filmRental.Repository.AddressRepository;
import com.example.filmRental.Repository.CustomerRepository;
import com.example.filmRental.Service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final NamedParameterJdbcTemplate jdbc;

    @Autowired
    public CustomerServiceImpl(
            CustomerRepository customerRepository,
            AddressRepository addressRepository,
            NamedParameterJdbcTemplate jdbc) {
        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
        this.jdbc = jdbc;
    }

    @Override
    public CustomerResponseDto create(CustomerRequestDto request) {
        // minimal FK presence checks for better DX
        if (request.getAddressId() == null || request.getAddressId() < 1) {
            throw new NotFoundException("addressId invalid: " + request.getAddressId());
        }
        addressRepository.findById(request.getAddressId())
    .orElseThrow(() -> new NotFoundException("Address not found: " + request.getAddressId()));

        Customer saved = customerRepository.save(CustomerMapper.toEntity(request));
        return CustomerMapper.toDto(saved);
    }

    @Override
    public PagedResponse<CustomerResponseDto> active(Pageable pageable) {
        return toPaged(customerRepository.findByActive(Boolean.TRUE, pageable), pageable);
    }

    @Override
    public PagedResponse<CustomerResponseDto> inactive(Pageable pageable) {
        return toPaged(customerRepository.findByActive(Boolean.FALSE, pageable), pageable);
    }

    @Override
    public PagedResponse<CustomerResponseDto> byLastName(String ln, Pageable pageable) {
        return toPaged(customerRepository.findByLast_nameContainingIgnoreCase(ln, pageable), pageable);
    }

    @Override
    public PagedResponse<CustomerResponseDto> byFirstName(String fn, Pageable pageable) {
        return toPaged(customerRepository.findByFirst_nameContainingIgnoreCase(fn, pageable), pageable);
    }

    @Override
    public CustomerResponseDto byEmail(String email) {
        Customer c = customerRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Customer not found by email: " + email));
        return CustomerMapper.toDto(c);
    }

    @Override
    public PagedResponse<CustomerResponseDto> byPhone(String phone, Pageable pageable) {
        return toPaged(customerRepository.findByPhone(phone, pageable), pageable);
    }

    @Override
    public PagedResponse<CustomerWithAddressResponseDto> byCity(String city, Pageable pageable) {
        return toWithAddressPaged(cityCountryQuery("city", city, pageable), pageable);
    }

    @Override
    public PagedResponse<CustomerWithAddressResponseDto> byCountry(String country, Pageable pageable) {
        return toWithAddressPaged(cityCountryQuery("country", country, pageable), pageable);
    }

    @Override
    public CustomerResponseDto updateEmail(short id, String email) {
        Customer c = get(id);
        CustomerMapper.applyEmail(c, email);
        return CustomerMapper.toDto(customerRepository.save(c));
    }

    @Override
    public CustomerResponseDto updatePhone(short id, String phone) {
        Customer c = get(id);
        Address a = addressRepository.findById(c.getAddress_id())
                .orElseThrow(() -> new NotFoundException("Address not found: " + c.getAddress_id()));
        a.setPhone(phone);
        addressRepository.save(a);
        return CustomerMapper.toDto(c);
    }

    @Override
    public CustomerResponseDto updateStore(short id, short storeId) {
        Customer c = get(id);
        CustomerMapper.applyStore(c, storeId);
        return CustomerMapper.toDto(customerRepository.save(c));
    }

    @Override
    public CustomerResponseDto updateAddress(short id, short addressId) {
        // ensure new address exists
        addressRepository.findById(addressId)
                .orElseThrow(() -> new NotFoundException("Address not found: " + addressId));
        Customer c = get(id);
        CustomerMapper.applyAddress(c, addressId);
        return CustomerMapper.toDto(customerRepository.save(c));
    }

    @Override
    public CustomerResponseDto updateFirstName(short id, String value) {
        Customer c = get(id);
        CustomerMapper.applyFirstName(c, value);
        return CustomerMapper.toDto(customerRepository.save(c));
    }

    @Override
    public CustomerResponseDto updateLastName(short id, String value) {
        Customer c = get(id);
        CustomerMapper.applyLastName(c, value);
        return CustomerMapper.toDto(customerRepository.save(c));
    }

    private Customer get(short id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found: " + id));
    }

    private PagedResponse<CustomerResponseDto> toPaged(Page<Customer> page, Pageable pageable) {
        return PagedResponse.<CustomerResponseDto>builder()
                .setContent(page.getContent().stream().map(CustomerMapper::toDto).toList())
                .setTotalElements(page.getTotalElements())
                .setTotalPages(page.getTotalPages())
                .setPageNumber(pageable.getPageNumber())
                .setPageSize(pageable.getPageSize())
                .setHasNext(page.hasNext())
                .setHasPrevious(page.hasPrevious())
                .build();
    }

    // With-address helper using JDBC for richer payload (address/city/country/phone)
    private Page<Map<String,Object>> cityCountryQuery(String field, String value, Pageable pageable) {
    	String base = "SELECT c.customer_id, c.first_name, c.last_name, c.email, c.active, a.address, a.district, ci.city, co.country, a.phone FROM customer c JOIN address a ON a.address_id = c.address_id JOIN city ci ON ci.city_id = a.city_id JOIN country co ON co.country_id = ci.country_id WHERE LOWER(" + ("city".equals(field) ? "ci.city" : "co.country") + ") = LOWER(:v) ORDER BY c.customer_id ASC LIMIT :limit OFFSET :offset";
    	String countSql = "SELECT COUNT(*) AS cnt FROM customer c JOIN address a ON a.address_id = c.address_id JOIN city ci ON ci.city_id = a.city_id JOIN country co ON co.country_id = ci.country_id WHERE LOWER(" + ("city".equals(field) ? "ci.city" : "co.country") + ") = LOWER(:v)";

        Map<String,Object> params = new HashMap<>();
        params.put("v", value);
        params.put("limit", pageable.getPageSize());
        params.put("offset", pageable.getPageNumber() * pageable.getPageSize());

        List<Map<String,Object>> rows = jdbc.queryForList(base, params);
        long total = jdbc.queryForObject(countSql, Map.of("v", value), Long.class);

        return new PageImpl<>(rows, pageable, total);
    }

    private PagedResponse<CustomerWithAddressResponseDto> toWithAddressPaged(Page<Map<String,Object>> page, Pageable pageable) {
        return PagedResponse.<CustomerWithAddressResponseDto>builder()
                .setContent(page.getContent().stream().map(CustomerMapper::rowToWithAddressDto).toList())
                .setTotalElements(page.getTotalElements())
                .setTotalPages(page.getTotalPages())
                .setPageNumber(pageable.getPageNumber())
                .setPageSize(pageable.getPageSize())
                .setHasNext(page.hasNext())
                .setHasPrevious(page.hasPrevious())
                .build();
    }
}
