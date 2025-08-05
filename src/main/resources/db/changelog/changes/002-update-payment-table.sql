-- payment_status 컬럼 추가
ALTER TABLE payment
    ADD COLUMN payment_status VARCHAR(255) NULL;

-- payment_type 컬럼 추가
ALTER TABLE payment
    ADD COLUMN payment_type VARCHAR(255) NULL;

-- is_instant_payment 컬럼 추가
ALTER TABLE payment
    ADD COLUMN is_instant_payment BOOLEAN NULL DEFAULT TRUE;

-- updated_at 컬럼 추가
ALTER TABLE payment
    ADD COLUMN updated_at TIMESTAMP NULL;