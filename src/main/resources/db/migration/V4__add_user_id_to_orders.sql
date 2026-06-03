-- Add user_id column to link each order to the user who placed it.
-- Nullable because existing orders (placed before this migration) have no owner.
-- In production you would follow this with:
--   UPDATE orders SET user_id = <default_user_id> WHERE user_id IS NULL;
-- to assign orphaned orders before adding a NOT NULL constraint.
ALTER TABLE orders ADD COLUMN user_id BIGINT;

ALTER TABLE orders
    ADD CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id);
