package com.fetch.receiptprocessor.dao;

import com.fetch.receiptprocessor.model.Item;
import com.fetch.receiptprocessor.model.Receipt;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ReceiptDao {
    private final Jdbi jdbi;

    public ReceiptDao(Jdbi jdbi) { this.jdbi = jdbi; }

    public Optional<UUID> saveReceipt(Receipt receipt) {
        try (Handle handle = jdbi.open()) {
            Optional<UUID> uuidOptional = handle.createUpdate(
                            "INSERT INTO receipts (retailer, purchase_date_time, total, points) " +
                                    "VALUES (:retailer, :purchaseDateTime, :total, :points)")
                    .bind("retailer", receipt.getRetailer())
                    .bind("purchaseDateTime", receipt.getPurchaseDateTime())
                    .bind("total", receipt.getTotal())
                    .bind("points", receipt.getPoints())
                    .executeAndReturnGeneratedKeys("id")
                    .mapTo(UUID.class)
                    .findFirst();

            if (uuidOptional.isPresent()) {
                for (Item item : receipt.getItems()) {
                    handle.createUpdate("INSERT INTO items (receipt_id, short_description, price) " +
                                    "VALUES (:receiptId, :shortDescription, :price)")
                            .bind("receiptId", uuidOptional.get())
                            .bind("shortDescription", item.getShortDescription())
                            .bind("price", Double.parseDouble(item.getPrice()))
                            .execute();
                }
            }
            return uuidOptional;
        }
    }

    public Optional<Receipt> findById(UUID id) {
        try (Handle handle = jdbi.open()) {
            Optional<Receipt> receiptOptional = handle.createQuery(
                            "SELECT id, retailer, purchase_date_time, total, points, items " +
                                    "FROM receipts WHERE id = :id")
                    .bind("id", id)
                    .mapToBean(Receipt.class)
                    .findFirst();

            if (receiptOptional.isPresent()) {
                List<Item> items = handle.createQuery(
                                "SELECT short_description, price " +
                                        "FROM items WHERE receipt_id = :receiptId ")
                        .bind("receiptId", id)
                        .map((rs, ctx) -> Item.builder()
                                .withShortDescription(rs.getString("short_description"))
                                .withPrice(String.valueOf(rs.getBigDecimal("price")))
                                .build())
                        .list();

                receiptOptional.get().setItems(items);
                return receiptOptional;
            }
        }
        return Optional.empty();
    }

    public Optional<Receipt> getPointsById(UUID id) {
        try (Handle handle = jdbi.open()) {
            return handle.createQuery(
                            "SELECT id, points " +
                                    "FROM receipts WHERE id = :id")
                    .bind("id", id)
                    .mapToBean(Receipt.class)
                    .findFirst();
        }
    }
}
