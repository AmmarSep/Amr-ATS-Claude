-- Check if database and tables exist
\c spring-ats

-- Check if user_group table exists and has data
SELECT * FROM user_group;

-- Check if user_details table exists and has users
SELECT user_id, username, email, is_locked, group_ref FROM user_details;

-- Check if admin user exists
SELECT u.user_id, u.username, u.email, u.is_locked, g.group_name, g.short_group
FROM user_details u
LEFT JOIN user_group g ON u.group_ref = g.group_id
WHERE u.email = 'admin@spring.ats';
