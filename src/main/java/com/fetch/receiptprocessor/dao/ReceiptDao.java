package com.fetch.receiptprocessor.dao;

import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Repository;

@Repository
public class ReceiptDao {
    private final Jdbi jdbi;

    public ReceiptDao(Jdbi jdbi) { this.jdbi = jdbi; }


}
