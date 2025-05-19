package com.linh.warehouse.mapper;

import com.linh.warehouse.dto.request.CustomerCreationRequest;
import com.linh.warehouse.dto.request.CustomerUpdateRequest;
import com.linh.warehouse.dto.response.CustomerResponse;
import com.linh.warehouse.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    Customer toCustomer(CustomerCreationRequest request);

    CustomerResponse toCustomerResponse(Customer customer);

    void updateCustomer(@MappingTarget Customer customer, CustomerUpdateRequest request);
}
