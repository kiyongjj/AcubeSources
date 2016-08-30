CREATE TABLE TND_VOLUME
(
 VOL_ID       NUMBER(5) 
    CONSTRAINT VOLUME_PK PRIMARY KEY
	using index storage
	(initial 500K next 500K pctincrease 0)
	tablespace NDISC_X_TS,
 VOL_NAME     VARCHAR2(255) NOT NULL,
 VOL_ACCESS   VARCHAR2(10),
 VOL_CRTDT    VARCHAR2(14),
 VOL_DESC     VARCHAR2(255)
)
  STORAGE (INITIAL 500K NEXT 500K PCTINCREASE 0 )
  TABLESPACE NDISC_D_TS
  PCTFREE 10
  PCTUSED 80
;

CREATE TABLE TND_MEDIA
(
 MD_ID       NUMBER(5) 
    CONSTRAINT MEDIA_PK PRIMARY KEY
	using index storage
	(initial 500K next 500K pctincrease 0)
	tablespace NDISC_X_TS,
 MD_NAME     VARCHAR2(255) NOT NULL,
 MD_TYPE     NUMBER(5),
 MD_PATH     VARCHAR2(255) NOT NULL,
 MD_CRTDT    VARCHAR2(14),
 MD_DESC     VARCHAR2(255),
 MD_MAXSIZE  NUMBER(12),
 MD_SIZE     NUMBER(12),
 MD_VOLID    NUMBER(5)
    CONSTRAINT MEDIA_VOLID_R REFERENCES TND_VOLUME(VOL_ID) 
)
  STORAGE (INITIAL 500K NEXT 500K PCTINCREASE 0 )
  TABLESPACE NDISC_D_TS
  PCTFREE 10
  PCTUSED 80
;

CREATE TABLE TND_FILE
(
 FLE_ID       VARCHAR2(32)
    CONSTRAINT FILE_PK PRIMARY KEY
	using index storage
	(initial 10M next 5M pctincrease 0)
	tablespace NDISC_X_TS,
 FLE_NAME     VARCHAR2(255) NOT NULL,
 FLE_SIZE     NUMBER(10),
 FLE_CRTDT    VARCHAR2(14),
 FLE_MODDT    VARCHAR2(14),
 FLE_STATUS   VARCHAR2(10) DEFAULT '0',
 FLE_MDID     NUMBER(5)
    CONSTRAINT FILE_MDID_R REFERENCES TND_MEDIA(MD_ID)
) 
  STORAGE (INITIAL 50M NEXT 10M PCTINCREASE 0 )
  TABLESPACE NDISC_D_TS
  PCTFREE 10
  PCTUSED 80
;
   
CREATE TABLE TND_STAT
(
 STAT_ID      VARCHAR2(10)
    CONSTRAINT STAT_PK PRIMARY KEY
	using index storage
	(initial 500K next 500K pctincrease 0)
	tablespace NDISC_X_TS,
 STAT_TYPE    VARCHAR2(10),
 STAT_CRTDT   VARCHAR2(14),
 STAT_VALUE   VARCHAR2(10)
) 
  STORAGE (INITIAL 500K NEXT 500K PCTINCREASE 0 )
  TABLESPACE NDISC_D_TS
  PCTFREE 10
  PCTUSED 80
;

CREATE TABLE TND_QUEUE
(
 QUE_FLE_ID   VARCHAR(32),
 QUE_TYPE     VARCHAR(10),
 QUE_CRTDT    VARCHAR(14),
 QUE_STATUS   VARCHAR(10),
    CONSTRAINT QUEUE_PK PRIMARY KEY(QUE_FLE_ID, QUE_TYPE) 
	using index storage
	(initial 500K next 500K pctincrease 0)
	tablespace NDISC_X_TS
) 
  STORAGE (INITIAL 50M NEXT 10M PCTINCREASE 0 )
  TABLESPACE NDISC_D_TS
  PCTFREE 10
  PCTUSED 80
;

insert into tnd_volume values('101', 'NDISC_VOLUME', 'RCUD', to_char(SYSDATE, 'yyyymmddhh24miss'), 'NDISC_VOLUME');
insert into tnd_media values('101', 'NDISC_MEDIA', '1', 'D:\Acube\MEDIA', to_char(SYSDATE, 'yyyymmddhh24miss'), 'NDISC_MEDIA', '10000000000', '0', '101');