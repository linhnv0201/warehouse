package com.linh.warehouse.service;

import com.linh.warehouse.dto.response.PurchaseInvoiceResponse;
import com.linh.warehouse.entity.*;
import com.linh.warehouse.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PurchaseInvoiceService {

    private final PurchaseInvoiceRepository purchaseInvoiceRepository;

    public PurchaseInvoiceResponse getInvoiceByReceiveOrderId(Integer receiveOrderId) {
        PurchaseInvoice invoice = purchaseInvoiceRepository.findByReceiveOrderId(receiveOrderId);

        PurchaseInvoiceResponse response = new PurchaseInvoiceResponse();
        response.setId(invoice.getId());
        response.setCode(invoice.getCode());
        response.setTotalAmount(invoice.getTotalAmount());
        response.setStatus(invoice.getStatus());
        response.setCreatedAt(invoice.getCreatedAt());

        ReceiveOrder ro = invoice.getReceiveOrder();
        if (ro != null) {
            response.setReceiveOrderCode(ro.getCode());

            // Lấy supplier từ PO
            PurchaseOrder po = ro.getPurchaseOrder();
            if (po != null && po.getSupplier() != null) {
                response.setSupplierName(po.getSupplier().getName());
            } else {
                response.setSupplierName(null);
            }
        }

        return response;
    }


}
