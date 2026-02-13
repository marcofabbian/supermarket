-- Flyway migration: seed supermarket table with initial data (idempotent)

-- Insert a few sample supermarkets. Each INSERT uses a WHERE NOT EXISTS check
-- so the migration is safe to run multiple times.

INSERT INTO supermarket (name, logo_url, created_at)
SELECT 'Aldi Suisse', 'https://s7g10.scene7.com/is/image/aldi/ALDI%20SUISSE%20LOGO-header?$G05-Logo-XL$&fmt=png-alpha', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM supermarket WHERE name = 'Aldi Suisse');

INSERT INTO supermarket (name, logo_url, created_at)
SELECT 'Lidl Suisse', 'https://www.lidl.ch/static/assets/d82111a6-98ba-4c6d-aaba-e21fbe241281-1784400.svg', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM supermarket WHERE name = 'Lidl Suisse');

INSERT INTO supermarket (name, logo_url, created_at)
SELECT 'Migros', 'https://www.migros.ch/assets/images/menu/migrosx.svg', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM supermarket WHERE name = 'Migros');

INSERT INTO supermarket (name, logo_url, created_at)
SELECT 'Coop', 'https://www.coop.ch/_ui/26.1.1.1998/desktop/common/img/masthead/logo/img/coop_logo.svg', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM supermarket WHERE name = 'Coop');

INSERT INTO supermarket (name, logo_url, created_at)
SELECT 'Denner', '', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM supermarket WHERE name = 'Denner');

