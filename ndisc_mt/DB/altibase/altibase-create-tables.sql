CREATE TABLE TND_VOLUME
(
 VOL_ID       NUMBER(5) NOT NULL, 
 VOL_NAME     VARCHAR2(255) NOT NULL,
 VOL_ACCESS   VARCHAR2(10),
 VOL_CRTDT    VARCHAR2(14),
 VOL_DESC     VARCHAR2(255)
)  TABLESPACE NDISC_D_TS;

ALTER TABLE TND_VOLUME
	ADD CONSTRAINT VOLUME_PK
PRIMARY KEY (VOL_ID) using index tablespace NDISC_X_TS;



CREATE TABLE TND_MEDIA
(
 MD_ID       NUMBER(5) NOT NULL,    
 MD_NAME     VARCHAR2(255) NOT NULL,
 MD_TYPE     NUMBER(5),
 MD_PATH     VARCHAR2(255) NOT NULL,
 MD_CRTDT    VARCHAR2(14),
 MD_DESC     VARCHAR2(255),
 MD_MAXSIZE  NUMBER(12),
 MD_SIZE     NUMBER(12),
 MD_VOLID    NUMBER(5)
    CONSTRAINT MEDIA_VOLID_R REFERENCES TND_VOLUME(VOL_ID) 
)TABLESPACE NDISC_D_TS;

 ALTER TABLE TND_MEDIA
 	ADD CONSTRAINT MEDIA_PK
 PRIMARY KEY (MD_ID) using index tablespace NDISC_X_TS;


CREATE TABLE TND_FILE
(
 FLE_ID       VARCHAR2(32) NOT NULL,	
 FLE_NAME     VARCHAR2(255) NOT NULL,
 FLE_SIZE     NUMBER(10),
 FLE_CRTDT    VARCHAR2(14),
 FLE_MODDT    VARCHAR2(14),
 FLE_STATUS   VARCHAR2(10) DEFAULT '0',
 FLE_MDID     NUMBER(5)
    CONSTRAINT FILE_MDID_R REFERENCES TND_MEDIA(MD_ID)
) TABLESPACE NDISC_D_TS;
  
ALTER TABLE TND_FILE
 	ADD CONSTRAINT FILE_PK
 PRIMARY KEY (FLE_ID) using index tablespace NDISC_X_TS;   
   
   
CREATE TABLE TND_STAT
(
 STAT_ID      VARCHAR2(10) NOT NULL,
 STAT_TYPE    VARCHAR2(10),
 STAT_CRTDT   VARCHAR2(14),
 STAT_VALUE   VARCHAR2(10)
) TABLESPACE NDISC_D_TS;
  
ALTER TABLE TND_STAT
 	ADD CONSTRAINT STAT_PK
PRIMARY KEY (STAT_ID) using index tablespace NDISC_X_TS; 


CREATE TABLE TND_QUEUE
(
 QUE_FLE_ID   VARCHAR(32),
 QUE_TYPE     VARCHAR(10),
 QUE_CRTDT    VARCHAR(14),
 QUE_STATUS   VARCHAR(10)    
) TABLESPACE NDISC_D_TS;

ALTER TABLE TND_QUEUE
 	ADD CONSTRAINT QUEUE_PK
PRIMARY KEY (QUE_FLE_ID, QUE_TYPE) using index tablespace NDISC_X_TS; 


-- insert into tnd_volume values('103', 'NDISC_VOLUME_103', 'RCUD', to_char(SYSDATE, 'yyyymmddhh24miss'), 'NDISC_TEST_VOLUME');
-- insert into tnd_media values('101', 'NDISC_MEDIA_101', '1', '/NDISC_MEDIA/M101', to_char(SYSDATE, 'yyyymmddhh24miss'), 'NDISC_TEST_MEDIA', '1000000000', '0', '103');