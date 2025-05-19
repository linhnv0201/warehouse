package com.linh.warehouse.service;

import com.linh.warehouse.dto.request.CustomerCreationRequest;
import com.linh.warehouse.dto.request.CustomerUpdateRequest;
import com.linh.warehouse.dto.response.CustomerResponse;
import com.linh.warehouse.entity.Customer;
import com.linh.warehouse.exception.AppException;
import com.linh.warehouse.exception.ErrorCode;
import com.linh.warehouse.mapper.CustomerMapper;
import com.linh.warehouse.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CustomerService {

    CustomerRepository customerRepository;
    CustomerMapper customerMapper;

    public CustomerResponse createCustomer(CustomerCreationRequest request) {
        if (customerRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.CUSTOMER_EXISTED);
        }

        Customer customer = customerMapper.toCustomer(request);
        return customerMapper.toCustomerResponse(customerRepository.save(customer));
    }

    public CustomerResponse updateCustomer(Integer id, CustomerUpdateRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));

        if (customerRepository.existsByName(request.getName()) &&
                !customer.getName().equalsIgnoreCase(request.getName())) {
            throw new AppException(ErrorCode.CUSTOMER_EXISTED);
        }

        customerMapper.updateCustomer(customer, request);
        return customerMapper.toCustomerResponse(customerRepository.save(customer));
    }

    public CustomerResponse getCustomer(Integer id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));
        return customerMapper.toCustomerResponse(customer);
    }

    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toCustomerResponse)
                .toList();
    }

    public void deleteCustomer(Integer id) {
        if (!customerRepository.existsById(id)) {
            throw new AppException(ErrorCode.CUSTOMER_NOT_FOUND);
        }
        customerRepository.deleteById(id);
    }
}
