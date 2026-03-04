CREATE TABLE IF NOT EXISTS inventory (
    product_id VARCHAR(255) PRIMARY KEY,
    stock INT NOT NULL
);
-- Insert a dummy product for testing
INSERT INTO inventory (product_id, stock) VALUES ('some-product-id', 5) ON CONFLICT DO NOTHING;