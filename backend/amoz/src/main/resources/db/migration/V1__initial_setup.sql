CREATE TABLE Company
(
    CompanyID             CHAR(36)     NOT NULL DEFAULT (UUID()) PRIMARY KEY,
    CompanyNumber         VARCHAR(50)  NOT NULL UNIQUE,
    CountryOfRegistration VARCHAR(100) NOT NULL,
    Name                  VARCHAR(100) NOT NULL,
    AddressID             CHAR(36)     NOT NULL UNIQUE,
    Regon                 VARCHAR(14) UNIQUE
);

CREATE TABLE ProductVariant
(
    ProductVariantID CHAR(36) NOT NULL DEFAULT (UUID()) PRIMARY KEY,
    ProductID        CHAR(36) NOT NULL,
    Code             INT      NOT NULL UNIQUE,
    StockID          CHAR(36) NOT NULL,
    DimensionsID     CHAR(36),
    WeightID         CHAR(36),
    ImpactOnPrice    DOUBLE,
    VariantName      VARCHAR(100)
);

CREATE TABLE Weight
(
    WeightID   CHAR(36) NOT NULL      DEFAULT (UUID()) PRIMARY KEY,
    UnitWeight ENUM ('MG', 'G', 'KG') DEFAULT 'KG' NOT NULL,
    Amount     INT      NOT NULL
);

CREATE TABLE Attribute
(
    AttributeID   CHAR(36)    NOT NULL DEFAULT (UUID()) PRIMARY KEY,
    AttributeName VARCHAR(50) NOT NULL
);

CREATE TABLE Dimensions
(
    DimensionsID   CHAR(36) NOT NULL            DEFAULT (UUID()) PRIMARY KEY,
    UnitDimensions ENUM ('MM', 'CM', 'M', 'DM') DEFAULT 'M' NOT NULL,
    Height         DOUBLE,
    Length         DOUBLE,
    Width          DOUBLE
);

CREATE TABLE Person
(
    PersonID    CHAR(36)             NOT NULL DEFAULT (UUID()) PRIMARY KEY,
    Name        VARCHAR(30)          NOT NULL,
    Surname     VARCHAR(30)          NOT NULL,
    DateOfBirth DATE                 NOT NULL,
    Sex         ENUM ('M', 'F', 'O') NOT NULL
);

CREATE TABLE Category
(
    CategoryID       CHAR(36)    NOT NULL DEFAULT (UUID()) PRIMARY KEY,
    Name             VARCHAR(30) NOT NULL,
    CategoryLevel    SMALLINT             DEFAULT 1 NOT NULL,
    ParentCategoryID CHAR(36)
);

CREATE TABLE Customer
(
    CustomerID               CHAR(36)     NOT NULL DEFAULT (UUID()) PRIMARY KEY,
    ContactPersonID          CHAR(36)     NOT NULL,
    NameOnInvoice            VARCHAR(255) NOT NULL,
    DefaultDeliveryAddressID CHAR(36)
);

CREATE TABLE Employee
(
    EmployeeID      CHAR(36) NOT NULL DEFAULT (UUID()) PRIMARY KEY,
    UserID          CHAR(21) NOT NULL,
    ContactPersonID CHAR(36) NOT NULL,
    PersonID        CHAR(36) NOT NULL,
    RoleInCompany   ENUM ('OWNER', 'REGULAR'),
    CompanyID       CHAR(36),
    EmploymentDate  DATE
);

CREATE TABLE Stock
(
    StockID         CHAR(36) NOT NULL DEFAULT (UUID()) PRIMARY KEY,
    AmountAvailable INT      NOT NULL
);

CREATE TABLE `User`
(
    UserID     CHAR(21)                              NOT NULL PRIMARY KEY,
    SystemRole ENUM ('USER', 'ADMIN') DEFAULT 'USER' NOT NULL
);

CREATE TABLE CustomerB2C
(
    CustomerB2CID CHAR(36)        NOT NULL DEFAULT (UUID()) PRIMARY KEY,
    CustomerID    CHAR(36) UNIQUE NOT NULL,
    PersonID      CHAR(36)        NOT NULL UNIQUE
);

CREATE TABLE ProductOrderItem
(
    ProductOrderItemID CHAR(36) NOT NULL DEFAULT (UUID()) PRIMARY KEY,
    ProductVariantID   CHAR(36) NOT NULL,
    ProductOrderID     CHAR(36) NOT NULL,
    UnitPrice          DOUBLE   NOT NULL,
    Amount             INT CHECK (Amount > 0),
    ProductName        VARCHAR(100),
    UNIQUE (ProductVariantID, ProductOrderID)
);

CREATE TABLE Invoice
(
    InvoiceID       CHAR(36) NOT NULL DEFAULT (UUID()) PRIMARY KEY,
    InvoiceNumber   INT      NOT NULL UNIQUE AUTO_INCREMENT,
    AmountOnInvoice DOUBLE   NOT NULL
);

CREATE TABLE ContactPerson
(
    ContactPersonID CHAR(36)    NOT NULL DEFAULT (UUID()) PRIMARY KEY,
    ContactNumber   VARCHAR(20) NOT NULL UNIQUE,
    EmailAddress    VARCHAR(100) UNIQUE
);

CREATE TABLE Product
(
    ProductID            CHAR(36)     NOT NULL DEFAULT (UUID()) PRIMARY KEY,
    CategoryID           CHAR(36)     NOT NULL,
    CompanyID            CHAR(36)     NOT NULL,
    MainProductVariantID CHAR(36)     NOT NULL,
    Name                 VARCHAR(100) NOT NULL,
    Price                DOUBLE       NOT NULL,
    Description          VARCHAR(1000),
    Brand                VARCHAR(50)
);

CREATE TABLE ProductOrder
(
    ProductOrderID CHAR(36) NOT NULL                                            DEFAULT (UUID()) PRIMARY KEY,
    Status         ENUM ('NEW', 'ORDERED', 'SHIPPED', 'DELIVERED', 'CANCELLED') DEFAULT 'NEW' NOT NULL,
    CustomerID     CHAR(36),
    AddressID      CHAR(36),
    InvoiceID	   CHAR(36),
    TrackingNumber VARCHAR(10),
    TimeOfSending  TIME(3),
    TimeOfCreation DATETIME                                                     DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE CustomerB2B
(
    CustomerB2BID    CHAR(36)     NOT NULL DEFAULT (UUID()) PRIMARY KEY,
    CustomerID       CHAR(36)     NOT NULL UNIQUE,
    AddressOnInvoice VARCHAR(255) NOT NULL,
    CompanyNumber    VARCHAR(30)  NOT NULL UNIQUE
);

CREATE TABLE ProductAttribute
(
    ProductAttributeID CHAR(36) NOT NULL DEFAULT (UUID()) PRIMARY KEY,
    ProductID          CHAR(36) NOT NULL,
    AttributeID        CHAR(36) NOT NULL,
    Value              VARCHAR(255),
    UNIQUE (ProductID, AttributeID)
);

CREATE TABLE VariantAttribute
(
    VariantAttributeID CHAR(36) NOT NULL DEFAULT (UUID()) PRIMARY KEY,
    ProductVariantID   CHAR(36) NOT NULL,
    AttributeID        CHAR(36) NOT NULL,
    Value              VARCHAR(255),
    UNIQUE (ProductVariantID, AttributeID)
);

CREATE TABLE Address
(
    AddressID             CHAR(36)     NOT NULL DEFAULT (UUID()) PRIMARY KEY,
    City                  VARCHAR(255),
    Street                VARCHAR(255),
    StreetNumber          VARCHAR(10),
    ApartmentNumber       VARCHAR(10),
    PostalCode            VARCHAR(10),
    AdditionalInformation VARCHAR(255) NOT NULL
);

ALTER TABLE Category
    ADD CONSTRAINT FK_Category_ParentCategoryID FOREIGN KEY (ParentCategoryID) REFERENCES Category (CategoryID);

ALTER TABLE Company
    ADD CONSTRAINT FK_Company_AddressID FOREIGN KEY (AddressID) REFERENCES Address (AddressID);

ALTER TABLE Customer
    ADD CONSTRAINT FK_Customer_ContactPersonID FOREIGN KEY (ContactPersonID) REFERENCES ContactPerson (ContactPersonID);
ALTER TABLE Customer
    ADD CONSTRAINT FK_Customer_DefaultDeliveryAddressID FOREIGN KEY (DefaultDeliveryAddressID) REFERENCES Address (AddressID);

ALTER TABLE CustomerB2B
    ADD CONSTRAINT FK_CustomerB2B_CustomerID FOREIGN KEY (CustomerID) REFERENCES Customer (CustomerID);

ALTER TABLE CustomerB2C
    ADD CONSTRAINT FK_CustomerB2C_CustomerID FOREIGN KEY (CustomerID) REFERENCES Customer (CustomerID);
ALTER TABLE CustomerB2C
    ADD CONSTRAINT FK_CustomerB2C_PersonID FOREIGN KEY (PersonID) REFERENCES Person (PersonID);

ALTER TABLE ProductVariant
    ADD CONSTRAINT FK_ProductVariant_DimensionsID FOREIGN KEY (DimensionsID) REFERENCES Dimensions (DimensionsID);
ALTER TABLE ProductVariant
    ADD CONSTRAINT FK_ProductVariant_StockID FOREIGN KEY (StockID) REFERENCES Stock (StockID);
ALTER TABLE ProductVariant
    ADD CONSTRAINT FK_ProductVariant_WeightID FOREIGN KEY (WeightID) REFERENCES Weight (WeightID);

ALTER TABLE Employee
    ADD CONSTRAINT FK_Employee_CompanyID FOREIGN KEY (CompanyID) REFERENCES Company (CompanyID);
ALTER TABLE Employee
    ADD CONSTRAINT FK_Employee_ContactPersonID FOREIGN KEY (ContactPersonID) REFERENCES ContactPerson (ContactPersonID);
ALTER TABLE Employee
    ADD CONSTRAINT FK_Employee_PersonID FOREIGN KEY (PersonID) REFERENCES Person (PersonID);
ALTER TABLE Employee
    ADD CONSTRAINT FK_Employee_UserID FOREIGN KEY (UserID) REFERENCES `User` (UserID);

ALTER TABLE ProductOrder
    ADD CONSTRAINT FK_ProductOrder_InvoiceID FOREIGN KEY (InvoiceID) REFERENCES Invoice (InvoiceID);

ALTER TABLE Product
    ADD CONSTRAINT FK_Product_CategoryID FOREIGN KEY (CategoryID) REFERENCES Category (CategoryID);
ALTER TABLE Product
    ADD CONSTRAINT FK_Product_CompanyID FOREIGN KEY (CompanyID) REFERENCES Company (CompanyID);
ALTER TABLE Product
    ADD CONSTRAINT FK_Product_MainProductVariantID FOREIGN KEY (MainProductVariantID) REFERENCES ProductVariant (ProductVariantID);

ALTER TABLE ProductAttribute
    ADD CONSTRAINT FK_ProductAttribute_AttributeID FOREIGN KEY (AttributeID) REFERENCES Attribute (AttributeID);
ALTER TABLE ProductAttribute
    ADD CONSTRAINT FK_ProductAttribute_ProductID FOREIGN KEY (ProductID) REFERENCES Product (ProductID);

ALTER TABLE ProductOrder
    ADD CONSTRAINT FK_ProductOrder_CustomerID FOREIGN KEY (CustomerID) REFERENCES Customer (CustomerID);

ALTER TABLE ProductOrderItem
    ADD CONSTRAINT FK_ProductOrderItem_ProductOrderID FOREIGN KEY (ProductOrderID) REFERENCES ProductOrder (ProductOrderID);
ALTER TABLE ProductOrderItem
    ADD CONSTRAINT FK_ProductOrderItem_ProductVariantID FOREIGN KEY (ProductVariantID) REFERENCES ProductVariant (ProductVariantID);

ALTER TABLE VariantAttribute
    ADD CONSTRAINT FK_VariantAttribute_AttributeID FOREIGN KEY (AttributeID) REFERENCES Attribute (AttributeID);
ALTER TABLE VariantAttribute
    ADD CONSTRAINT FK_VariantAttribute_ProductVariantID FOREIGN KEY (ProductVariantID) REFERENCES ProductVariant (ProductVariantID);

DELIMITER //

CREATE TRIGGER TRG_BEFORE_INSERT_CATEGORY
    BEFORE INSERT
    ON Category
    FOR EACH ROW
BEGIN
    IF NEW.ParentCategoryID IS NOT NULL THEN
        SET NEW.CategoryLevel = (SELECT CategoryLevel FROM Category WHERE CategoryID = NEW.ParentCategoryID) + 1;
    ELSE
        SET NEW.CategoryLevel = 1;
    END IF;
END //

DELIMITER ;