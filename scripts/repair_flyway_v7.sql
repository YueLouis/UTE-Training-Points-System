-- ============================================
-- FLYWAY V7 REPAIR SCRIPT
-- ============================================
-- Run this on Railway MySQL to fix V7 failed migration
-- Then redeploy app from Railway dashboard

-- Step 1: Check current V7 status
SELECT
    installed_rank,
    version,
    description,
    script,
    success,
    execution_time,
    installed_on
FROM flyway_schema_history
WHERE version = '7';

-- Step 2: Delete V7 failed record
DELETE FROM flyway_schema_history WHERE version = '7';

-- Step 3: Clean up any partial V7 data (if exists)
-- V7 might have inserted some data before failing
DELETE FROM user_org_units WHERE org_unit_id > 0;
DELETE FROM org_units WHERE id > 0;

-- Step 4: Reset AUTO_INCREMENT
ALTER TABLE org_units AUTO_INCREMENT = 1;

-- Step 5: Verify V7 is removed
SELECT COUNT(*) as v7_count FROM flyway_schema_history WHERE version = '7';
-- Should return 0

-- Step 6: Check remaining migrations
SELECT version, description, success, installed_on
FROM flyway_schema_history
ORDER BY installed_rank DESC;

-- ============================================
-- AFTER RUNNING THIS SCRIPT:
-- ============================================
-- 1. Go to Railway Dashboard
-- 2. Click your backend service
-- 3. Click "Redeploy" button (or it will auto-redeploy)
-- 4. Flyway will detect V7 missing and run it again
-- 5. V7 should succeed this time!
-- ============================================

