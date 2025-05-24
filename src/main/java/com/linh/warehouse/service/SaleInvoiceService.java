package com.linh.warehouse.service;

import com.linh.warehouse.dto.response.SaleInvoiceResponse;
import com.linh.warehouse.entity.DeliveryOrder;
import com.linh.warehouse.entity.SaleInvoice;
import com.linh.warehouse.entity.Customer;
import com.linh.warehouse.exception.AppException;
import com.linh.warehouse.exception.ErrorCode;
import com.linh.warehouse.repository.SaleInvoiceRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class SaleInvoiceService {

    SaleInvoiceRepository saleInvoiceRepository;

    public SaleInvoiceResponse getInvoiceByDeliveryOrderId(Integer deliveryOrderId) {
        SaleInvoice invoice = saleInvoiceRepository.findByDeliveryOrderId(deliveryOrderId)
                .orElseThrow(() -> new AppException(ErrorCode.SALE_INVOICE_NOT_FOUND));

        SaleInvoiceResponse response = new SaleInvoiceResponse();
        response.setId(invoice.getId());
        response.setCode(invoice.getCode());
        response.setTotalAmount(invoice.getTotalAmount());
        response.setStatus(invoice.getStatus());
        response.setCreatedAt(invoice.getCreatedAt());

        // Lấy thông tin DeliveryOrder
        if (invoice.getDeliveryOrder() != null) {
            response.setDeliveryOrderCode(invoice.getDeliveryOrder().getCode());
        }

        // Lấy thông tin customer từ delivery order
        if (invoice.getCustomer() != null) {
            response.setCustomerName(invoice.getCustomer().getName());
        }

        return response;
    }

}
