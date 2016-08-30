--NDISC db user 생성
--system 계정으로 실행하여야 함
--
--일반적으로 data tablespace와 index tablespace는 disk content 관련하여
--파일시스템을 물리적으로 분리하는 것으로 권고하고 있다
--tablespace의 size 산정은 해당 사이트 상황에 따라 적절하게 수정되야 함
--oracle 8i 이상 적용
create tablespace NDISC_D_TS
   datafile '/data3/datafile/ndisc/ndisc_d_ts.dbf' size 128M
   autoextend on next 16M maxsize 512M
   extent management local;

create tablespace NDISC_X_TS
   datafile '/data3/datafile/ndisc/ndisc_x_ts.dbf' size 128M
   autoextend on next 16M maxsize 256M
   extent management local;

create user nd_user identified by nd000
   default tablespace NDISC_D_TS
   temporary tablespace temp;
grant connect,resource to nd_user;