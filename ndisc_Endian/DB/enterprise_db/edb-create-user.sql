-- Add new role 
CREATE ROLE nd_user2 LOGIN
ENCRYPTED PASSWORD 'nd000'
SUPERUSER INHERIT CREATEDB NOCREATEROLE;
