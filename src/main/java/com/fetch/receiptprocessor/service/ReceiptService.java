package com.fetch.receiptprocessor.service;

import com.fetch.receiptprocessor.dao.ReceiptDao;
import com.fetch.receiptprocessor.dto.ProcessReceiptRequest;
import com.fetch.receiptprocessor.model.Item;
import com.fetch.receiptprocessor.model.Receipt;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReceiptService {
    private final ReceiptDao receiptDao;

    public ReceiptService(ReceiptDao receiptDao) { this.receiptDao = receiptDao; }

    public Optional<UUID> saveReceipt(ProcessReceiptRequest request) {
        Receipt receipt = request.toReceipt();
        calculatePoints(receipt);
        return receiptDao.saveReceipt(receipt);
    }

    public Optional<Receipt> findById(UUID id) {
        return receiptDao.findById(id);
    }

    public Optional<Integer> getPointsById(UUID id) {
        return receiptDao.getPointsById(id).map(Receipt::getPoints);
    }

    public void calculatePoints(Receipt receipt) {
        int points = 0;

        // 1 point for every alphanumeric character in the retailer name
        points += receipt.getRetailer().replaceAll("[^A-Za-z0-9]", "").length();

        // 50 points if the total is a round dollar amount with no cents
        if (receipt.getTotal().remainder(new BigDecimal("1")).compareTo(new BigDecimal("0")) == 0) {
            points += 50;
        }

        // 25 points if the total is a multiple of 0.25
        if (receipt.getTotal().remainder(new BigDecimal("0.25")).compareTo(new BigDecimal("0")) == 0) {
            points += 25;
        }

        // 5 points for every 2 items on the receipt
        points += (receipt.getItems().size() / 2) * 5;

        // if the trimmed length of the item description is a multiple of 3,
        // multiply the price by 0.2 and round up to the nearest integer
        for (Item item : receipt.getItems()) {
            if (item.getShortDescription().trim().length() % 3 == 0) {
                points += (int) Math.ceil(Double.parseDouble(item.getPrice()) * 0.2);
            }
        }

        // 6 points if the day in the purchase date is odd
        if(receipt.getPurchaseDateTime().getDayOfMonth() % 2 == 1) {
            points += 6;
        }

        // 10 points if the time of the purchase is after 2:00pm and before 4:00pm
        if(receipt.getPurchaseDateTime().toLocalTime().isAfter(LocalTime.of(14, 0)) &&
                receipt.getPurchaseDateTime().toLocalTime().isBefore(LocalTime.of(16, 0))) {
            points += 10;
        }

        receipt.setPoints(points);
    }
}
