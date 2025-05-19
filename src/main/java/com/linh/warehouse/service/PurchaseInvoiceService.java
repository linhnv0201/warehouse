package com.linh.warehouse.service;

import com.linh.warehouse.dto.response.PurchaseInvoiceResponse;
import com.linh.warehouse.entity.*;
import com.linh.warehouse.repository.*;
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
public class PurchaseInvoiceService {

    private final PurchaseInvoiceRepository purchaseInvoiceRepository;

    public List<PurchaseInvoiceResponse> getInvoicesByReceiveOrderId(Integer receiveOrderId) {
        List<PurchaseInvoice> invoices = purchaseInvoiceRepository.findByReceiveOrderId(receiveOrderId);
        return invoices.stream().map(invoice -> {
            PurchaseInvoiceResponse response = new PurchaseInvoiceResponse();
            response.setId(invoice.getId());
            response.setCode(invoice.getCode());
            response.setTotalAmount(invoice.getTotalAmount());
            response.setStatus(invoice.getStatus());
            response.setCreatedAt(invoice.getCreatedAt());

            ReceiveOrder ro = invoice.getReceiveOrder();
            response.setReceiveOrderCode(ro.getCode());

            // Lấy supplier từ PO
            Supplier supplier = ro.getPurchaseOrder().getSupplier();
            response.setSupplierName(supplier != null ? supplier.getName() : null);

            return response;
        }).collect(Collectors.toList());
    }


}
