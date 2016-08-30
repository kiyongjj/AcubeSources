/** DATABASE 스키마와 유저, 테이블까지 일괄 생성 */

IF EXISTS (SELECT name FROM master.dbo.sysdatabases WHERE name = N'ndiscdb')
	DROP DATABASE [ndiscdb]
GO

/** 데이터 파일 경로 - 서버 설치 환경에 맞게 수정할 것 */
CREATE DATABASE [ndiscdb]  ON (NAME = N'ndiscdb_Data', FILENAME = N'H:\acube\msdata\ndiscdb_Data.MDF' , SIZE = 200, FILEGROWTH = 10%) LOG ON (NAME = N'ndiscdb_Log', FILENAME = N'H:\acube\msdata\ndiscdb_Log.LDF' , SIZE = 100, FILEGROWTH = 10%)
 COLLATE Korean_Wansung_CI_AS
GO

exec sp_dboption N'ndiscdb', N'autoclose', N'false'
GO

exec sp_dboption N'ndiscdb', N'bulkcopy', N'false'
GO

exec sp_dboption N'ndiscdb', N'trunc. log', N'false'
GO

exec sp_dboption N'ndiscdb', N'torn page detection', N'true'
GO

exec sp_dboption N'ndiscdb', N'read only', N'false'
GO

exec sp_dboption N'ndiscdb', N'dbo use', N'false'
GO

exec sp_dboption N'ndiscdb', N'single', N'false'
GO

exec sp_dboption N'ndiscdb', N'autoshrink', N'false'
GO

exec sp_dboption N'ndiscdb', N'ANSI null default', N'false'
GO

exec sp_dboption N'ndiscdb', N'recursive triggers', N'false'
GO

exec sp_dboption N'ndiscdb', N'ANSI nulls', N'false'
GO

exec sp_dboption N'ndiscdb', N'concat null yields null', N'false'
GO

exec sp_dboption N'ndiscdb', N'cursor close on commit', N'false'
GO

exec sp_dboption N'ndiscdb', N'default to local cursor', N'false'
GO

exec sp_dboption N'ndiscdb', N'quoted identifier', N'false'
GO

exec sp_dboption N'ndiscdb', N'ANSI warnings', N'false'
GO

exec sp_dboption N'ndiscdb', N'auto create statistics', N'true'
GO

exec sp_dboption N'ndiscdb', N'auto update statistics', N'true'
GO

if( (@@microsoftversion / power(2, 24) = 8) and (@@microsoftversion & 0xffff >= 724) )
	exec sp_dboption N'ndiscdb', N'db chaining', N'false'
GO

use [ndiscdb]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_TND_FILE_TND_MEDIA]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[TND_FILE] DROP CONSTRAINT FK_TND_FILE_TND_MEDIA
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_TND_MEDIA_TND_VOLUME]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[TND_MEDIA] DROP CONSTRAINT FK_TND_MEDIA_TND_VOLUME
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[TND_FILE]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[TND_FILE]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[TND_MEDIA]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[TND_MEDIA]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[TND_QUEUE]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[TND_QUEUE]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[TND_STAT]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[TND_STAT]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[TND_VOLUME]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[TND_VOLUME]
GO


if not exists (select * from master.dbo.syslogins where loginname = N'nd_user')
BEGIN
	declare @logindb nvarchar(132), @loginlang nvarchar(132) select @logindb = N'ndiscdb', @loginlang = N'한국어'
	if @logindb is null or not exists (select * from master.dbo.sysdatabases where name = @logindb)
		select @logindb = N'master'
	if @loginlang is null or (not exists (select * from master.dbo.syslanguages where name = @loginlang) and @loginlang <> N'us_english')
		select @loginlang = @@language
	exec sp_addlogin N'nd_user', N'nd000', @logindb, @loginlang
END
GO

if not exists (select * from dbo.sysusers where name = N'nd_user' and uid < 16382)
	EXEC sp_grantdbaccess N'nd_user', N'nd_user'
GO

exec sp_addrolemember N'db_owner', N'nd_user'
GO

CREATE TABLE [dbo].[TND_FILE] (
	[FLE_ID] [varchar] (32) COLLATE Korean_Wansung_CI_AS NOT NULL ,
	[FLE_NAME] [varchar] (255) COLLATE Korean_Wansung_CI_AS NOT NULL ,
	[FLE_SIZE] [numeric](10, 0) NULL ,
	[FLE_CRTDT] [varchar] (14) COLLATE Korean_Wansung_CI_AS NULL ,
	[FLE_MODDT] [varchar] (14) COLLATE Korean_Wansung_CI_AS NULL ,
	[FLE_STATUS] [varchar] (10) COLLATE Korean_Wansung_CI_AS NULL ,
	[FLE_MDID] [numeric](5, 0) NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[TND_MEDIA] (
	[MD_ID] [numeric](5, 0) NOT NULL ,
	[MD_NAME] [varchar] (255) COLLATE Korean_Wansung_CI_AS NOT NULL ,
	[MD_TYPE] [numeric](5, 0) NULL ,
	[MD_PATH] [varchar] (255) COLLATE Korean_Wansung_CI_AS NOT NULL ,
	[MD_CRTDT] [varchar] (14) COLLATE Korean_Wansung_CI_AS NULL ,
	[MD_DESC] [varchar] (255) COLLATE Korean_Wansung_CI_AS NULL ,
	[MD_MAXSIZE] [numeric](10, 0) NULL ,
	[MD_SIZE] [numeric](10, 0) NULL ,
	[MD_VOLID] [numeric](5, 0) NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[TND_QUEUE] (
	[QUE_FLE_ID] [varchar] (32) COLLATE Korean_Wansung_CI_AS NOT NULL ,
	[QUE_TYPE] [varchar] (10) COLLATE Korean_Wansung_CI_AS NOT NULL ,
	[QUE_CRTDT] [varchar] (14) COLLATE Korean_Wansung_CI_AS NULL ,
	[QUE_STATUS] [varchar] (10) COLLATE Korean_Wansung_CI_AS NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[TND_STAT] (
	[STAT_ID] [varchar] (10) COLLATE Korean_Wansung_CI_AS NOT NULL ,
	[STAT_TYPE] [varchar] (10) COLLATE Korean_Wansung_CI_AS NULL ,
	[STAT_CRTDT] [varchar] (14) COLLATE Korean_Wansung_CI_AS NULL ,
	[STAT_VALUE] [varchar] (10) COLLATE Korean_Wansung_CI_AS NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[TND_VOLUME] (
	[VOL_ID] [numeric](5, 0) NOT NULL ,
	[VOL_NAME] [varchar] (255) COLLATE Korean_Wansung_CI_AS NOT NULL ,
	[VOL_ACCESS] [varchar] (10) COLLATE Korean_Wansung_CI_AS NULL ,
	[VOL_CRTDT] [varchar] (14) COLLATE Korean_Wansung_CI_AS NULL ,
	[VOL_DESC] [varchar] (255) COLLATE Korean_Wansung_CI_AS NULL 
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[TND_FILE] WITH NOCHECK ADD 
	CONSTRAINT [PK_TND_FILE] PRIMARY KEY  CLUSTERED 
	(
		[FLE_ID]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[TND_MEDIA] WITH NOCHECK ADD 
	CONSTRAINT [PK_TND_MEDIA] PRIMARY KEY  CLUSTERED 
	(
		[MD_ID]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[TND_QUEUE] WITH NOCHECK ADD 
	CONSTRAINT [PK_TND_QUEUE] PRIMARY KEY  CLUSTERED 
	(
		[QUE_FLE_ID],
		[QUE_TYPE]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[TND_STAT] WITH NOCHECK ADD 
	CONSTRAINT [PK_TND_STAT] PRIMARY KEY  CLUSTERED 
	(
		[STAT_ID]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[TND_VOLUME] WITH NOCHECK ADD 
	CONSTRAINT [PK_TND_VOLUME] PRIMARY KEY  CLUSTERED 
	(
		[VOL_ID]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[TND_FILE] ADD 
	CONSTRAINT [DF__TND_FILE__FLE_ST__173876EA] DEFAULT ('0') FOR [FLE_STATUS]
GO

ALTER TABLE [dbo].[TND_FILE] ADD 
	CONSTRAINT [FK_TND_FILE_TND_MEDIA] FOREIGN KEY 
	(
		[FLE_MDID]
	) REFERENCES [dbo].[TND_MEDIA] (
		[MD_ID]
	)
GO

ALTER TABLE [dbo].[TND_MEDIA] ADD 
	CONSTRAINT [FK_TND_MEDIA_TND_VOLUME] FOREIGN KEY 
	(
		[MD_VOLID]
	) REFERENCES [dbo].[TND_VOLUME] (
		[VOL_ID]
	)
GO


insert into tnd_volume(VOL_ID,VOL_NAME,VOL_ACCESS,VOL_CRTDT,VOL_DESC) values('101', 'VOLUME101', 'RCUD', getdate(), 'VOLUME101')
insert into tnd_media(MD_ID,MD_NAME,MD_TYPE,MD_PATH,MD_CRTDT,MD_DESC,MD_MAXSIZE,MD_SIZE,MD_VOLID) values('101', 'MEDIA101', '1', '/utilap/jbosswas6/MEDIA', getdate(), 'MEDIA101', '10000000000', '0', '101')
