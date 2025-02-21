CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS receipts;

CREATE TABLE receipts (
                          id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                          retailer VARCHAR(255) NOT NULL,
                          purchase_date_time TIMESTAMP NOT NULL,
                          total DECIMAL(10,2) NOT NULL,
                          points INTEGER NOT NULL
);

CREATE TABLE items (
                       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                       receipt_id UUID NOT NULL REFERENCES receipts(id),
                       short_description TEXT NOT NULL,
                       price DECIMAL(10,2) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_items_receipt_id ON items(receipt_id);
CREATE INDEX IF NOT EXISTS idx_items_short_description ON items(short_description);