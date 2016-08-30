--- Create Table
CREATE TABLE nd_user2."TND_FILE"
(
  "FLE_ID" character varying(32) NOT NULL,
  "FLE_NAME" character varying(255) NOT NULL,
  "FLE_SIZE" numeric(10),
  "FLE_CRTDT" character varying(14),
  "FLE_MODDT" character varying(14),
  "FLE_STATUS" character varying(10) DEFAULT 0,
  "FLE_MDID" numeric(5) NOT NULL,
  CONSTRAINT "TND_FILE_pkey" PRIMARY KEY ("FLE_ID")
)
WITH (OIDS=FALSE);
ALTER TABLE nd_user2."TND_FILE" OWNER TO nd_user2;



CREATE TABLE nd_user2."TND_MEDIA"
(
 "MD_ID"       numeric NOT NULL,
 "MD_NAME"     character varying(255) NOT NULL,
 "MD_TYPE"     numeric,
 "MD_PATH"     character varying(255) NOT NULL,
 "MD_CRTDT"    character varying(14),
 "MD_DESC"     character varying(255),
 "MD_MAXSIZE"	 numeric,
 "MD_SIZE"	 numeric, 
 "MD_VOLID"    numeric NOT NULL,
 CONSTRAINT "TND_MEDIA_pkey" PRIMARY KEY ("MD_ID")
)
WITH (OIDS=FALSE);
ALTER TABLE nd_user2."TND_MEDIA" OWNER TO nd_user2;



CREATE TABLE nd_user2."TND_VOLUME"
(
 "VOL_ID"   	  numeric NOT NULL,
 "VOL_NAME"     character varying(255) NOT NULL,
 "VOL_ACCESS"   character varying(10),
 "VOL_CRTDT"    character varying(14),
 "VOL_DESC"     character varying(255),
 CONSTRAINT "TND_VOLUME_pkey" PRIMARY KEY ("VOL_ID")
)
WITH (OIDS=FALSE);
ALTER TABLE nd_user2."TND_VOLUME" OWNER TO nd_user2;



CREATE TABLE nd_user2."TND_QUEUE"
(
 "QUE_FLE_ID"   character varying(32),
 "QUE_TYPE"     character varying(10),
 "QUE_CRTDT"    character varying(14),
 "QUE_STATUS"   character varying(10),
  CONSTRAINT "TND_QUEUE_pkey" PRIMARY KEY ("QUE_FLE_ID","QUE_TYPE")
)
WITH (OIDS=FALSE);
ALTER TABLE nd_user2."TND_QUEUE" OWNER TO nd_user2;



CREATE TABLE nd_user2."TND_STAT"
(
 "STAT_ID"      character varying(10),
 "STAT_TYPE"    character varying(10),
 "STAT_CRTDT"   character varying(14),
 "STAT_VALUE"   character varying(10),
 CONSTRAINT "TND_STAT_pkey" PRIMARY KEY ("STAT_ID")
)
WITH (OIDS=FALSE);
ALTER TABLE nd_user2."TND_STAT" OWNER TO nd_user2;


