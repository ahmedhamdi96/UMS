CREATE SCHEMA UMSDB;

CREATE TABLE UMSDB.USERS (
    USER_ID int AUTO_INCREMENT PRIMARY KEY,
	FIRST_NAME varchar(50) NOT NULL,
	LAST_NAME varchar(50) NOT NULL,
    EMAIL varchar(50) NOT NULL,
    PASSWORD varchar(64) NOT NULL,
	ADMIN bit NOT NULL DEFAULT 0,
    ACTIVE bit NOT NULL DEFAULT 1
);

CREATE TABLE UMSDB.GROUPS (
    GROUP_ID int AUTO_INCREMENT PRIMARY KEY,
	NAME varchar(50) NOT NULL,
	DESCRIPTION varchar(500) NOT NULL,
    ACTIVE bit NOT NULL DEFAULT 1
);

CREATE TABLE UMSDB.GROUPS_USERS (
    GROUP_ID int NOT NULL,
    USER_ID int NOT NULL,
    FOREIGN KEY (GROUP_ID) REFERENCES UMSDB.GROUPS(GROUP_ID),
    FOREIGN KEY (USER_ID) REFERENCES UMSDB.USERS(USER_ID),
    PRIMARY KEY (GROUP_ID, USER_ID)
);

INSERT INTO UMSDB.USERS (FIRST_NAME, LAST_NAME, EMAIL , PASSWORD, ADMIN, ACTIVE) VALUES ('Ahmed', 'Ebied', 'ahmedhamdi96@live.com', 'password', 1, 1);
INSERT INTO UMSDB.GROUPS (NAME, DESCRIPTION, ACTIVE) VALUES ('Administrators', 'This is a group for administrators.', 1);
INSERT INTO UMSDB.GROUPS_USERS (GROUP_ID, USER_ID) VALUES (1, 1);

USE UMSDB;

DELIMITER ;;

CREATE TRIGGER users_update BEFORE UPDATE ON UMSDB.USERS FOR EACH ROW
IF OLD.USER_ID <=> 1 THEN
  SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cannot update locked record';
END IF;;

CREATE TRIGGER users_delete BEFORE DELETE ON UMSDB.USERS FOR EACH ROW
IF OLD.USER_ID <=> 1 THEN
  SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cannot delete locked record';
END IF;;

CREATE TRIGGER groups_delete BEFORE DELETE ON UMSDB.GROUPS FOR EACH ROW
IF OLD.GROUP_ID <=> 1 THEN
  SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cannot delete locked record';
END IF;;

CREATE TRIGGER groups_users_delete BEFORE DELETE ON UMSDB.GROUPS_USERS FOR EACH ROW
IF OLD.GROUP_ID <=> 1 AND OLD.USER_ID <=> 1 THEN
  SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cannot delete locked record';
END IF;;

DELIMITER ;