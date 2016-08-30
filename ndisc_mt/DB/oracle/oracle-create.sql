--NDISC db user ����
--system �������� �����Ͽ��� ��
--
--�Ϲ������� data tablespace�� index tablespace�� disk content �����Ͽ�
--���Ͻý����� ���������� �и��ϴ� ������ �ǰ��ϰ� �ִ�
--tablespace�� size ������ �ش� ����Ʈ ��Ȳ�� ���� �����ϰ� �����Ǿ� ��
--oracle 8i �̻� ����
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