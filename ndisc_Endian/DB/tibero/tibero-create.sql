create tablespace NDISC_D_TS
   datafile '/data3/datafile/ndisc/ndisc_d_ts.tbf' size 128M
   autoextend on next 16M maxsize unlimited
   extent management local;

create tablespace NDISC_X_TS
   datafile '/data3/datafile/ndisc/ndisc_x_ts.tbf' size 128M
   autoextend on next 16M maxsize unlimited
   extent management local;

create user nd_user identified by nd000
   default tablespace NDISC_D_TS
   temporary tablespace temp;
grant connect,resource to nd_user;
