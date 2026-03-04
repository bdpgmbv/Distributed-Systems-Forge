CREATE TABLE IF NOT EXISTS blocked_accounts (
    account_id VARCHAR(50) PRIMARY KEY,
    reason VARCHAR(255)
);

-- Let's insert a known bad actor so we can test the rejection logic!
INSERT INTO blocked_accounts (account_id, reason)
VALUES ('BAD_GUY_99', 'Known stolen credit card history')
ON CONFLICT DO NOTHING;