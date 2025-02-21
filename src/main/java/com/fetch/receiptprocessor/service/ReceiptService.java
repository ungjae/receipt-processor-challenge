package com.fetch.receiptprocessor.service;

import com.fetch.receiptprocessor.dao.ReceiptDao;
import org.springframework.stereotype.Service;

@Service
public class ReceiptService {
    private final ReceiptDao receiptDao;

    public ReceiptService(ReceiptDao receiptDao) { this.receiptDao = receiptDao; }

}
