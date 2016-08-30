create database if not exists ndisc;
grant all privileges on ndisc.* to nd_user@"%" identified by "nd000";
grant all privileges on ndisc.* to nd_user@localhost identified by "nd000";

-- You may have to explicitly define your hostname in order for things
-- to work correctly.  For example:
-- grant all privileges on stor.* to storuser@host.domain.com identified by "sds000";
