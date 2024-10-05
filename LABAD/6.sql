-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 24, 2023 at 11:11 AM
-- Server version: 10.4.19-MariaDB
-- PHP Version: 8.0.7

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `clminton`
--

-- --------------------------------------------------------

--
-- Table structure for table `carttable`
--

CREATE TABLE `carttable` (
  `UserID` char(5) NOT NULL,
  `ProductID` char(5) NOT NULL,
  `Quantity` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `carttable`
--

INSERT INTO `carttable` (`UserID`, `ProductID`, `Quantity`) VALUES
('UA001', 'PD002', 25),
('UA001', 'PD006', 15),
('UA002', 'PD001', 25),
('UA002', 'PD005', 5),
('UA002', 'PD007', 34);

-- --------------------------------------------------------

--
-- Table structure for table `msproduct`
--

CREATE TABLE `msproduct` (
  `ProductID` char(5) NOT NULL,
  `ProductName` varchar(50) NOT NULL,
  `ProductMerk` varchar(25) NOT NULL,
  `ProductPrice` int(11) NOT NULL,
  `ProductStock` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `msproduct`
--

INSERT INTO `msproduct` (`ProductID`, `ProductName`, `ProductMerk`, `ProductPrice`, `ProductStock`) VALUES
('PD001', 'Astrox 99', 'Yonex', 2000000, 100),
('PD002', 'WindStorm 72', 'Li-Ning', 1500000, 75),
('PD003', 'Astrox 88D', 'Yonex', 2500000, 50),
('PD004', 'Astrox 88S', 'Yonex', 2500000, 50),
('PD005', 'Thruster HMR', 'Victor', 1500000, 100),
('PD006', 'Tectonic 7', 'Li-Ning', 2200000, 15),
('PD007', 'Hypernano', 'Victor', 850000, 100),
('PD008', 'Carbonex', 'Yonex', 1000000, 100);

-- --------------------------------------------------------

--
-- Table structure for table `msuser`
--

CREATE TABLE `msuser` (
  `UserID` char(5) NOT NULL,
  `UserEmail` varchar(50) NOT NULL,
  `UserPassword` varchar(50) NOT NULL,
  `UserAge` int(11) NOT NULL,
  `UserGender` varchar(10) NOT NULL,
  `UserNationality` varchar(25) NOT NULL,
  `UserRole` varchar(5) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `msuser`
--

INSERT INTO `msuser` (`UserID`, `UserEmail`, `UserPassword`, `UserAge`, `UserGender`, `UserNationality`, `UserRole`) VALUES
('UA001', 'admin@gmail.com', 'admin1234', 17, 'Male', 'Indonesia', 'Admin'),
('UA002', 'boodi@gmail.com', 'user1234', 17, 'Male', 'Indonesia', 'User'),
('UA003', 'customer@gmail.com', 'customer1234', 21, 'Female', 'Indonesia', 'User'),
('UA004', 'buyer@gmail.com', 'buyer1234', 33, 'Female', 'Indonesia', 'User'),
('UA005', 'Roodi@gmail.com', 'roodi1234', 76, 'Male', 'Indonesia', 'User');

-- --------------------------------------------------------

--
-- Table structure for table `transactiondetail`
--

CREATE TABLE `transactiondetail` (
  `ProductID` char(5) NOT NULL,
  `TransactionID` char(5) NOT NULL,
  `Quantity` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `transactiondetail`
--

INSERT INTO `transactiondetail` (`ProductID`, `TransactionID`, `Quantity`) VALUES
('PD002', 'TH001', 17),
('PD002', 'TH004', 1),
('PD002', 'TH005', 2),
('PD002', 'TH010', 5),
('PD003', 'TH006', 3),
('PD003', 'TH007', 7),
('PD003', 'TH010', 5),
('PD004', 'TH001', 2),
('PD004', 'TH009', 1),
('PD006', 'TH002', 12),
('PD008', 'TH003', 1),
('PD008', 'TH008', 1);

-- --------------------------------------------------------

--
-- Table structure for table `transactionheader`
--

CREATE TABLE `transactionheader` (
  `TransactionID` char(5) NOT NULL,
  `UserID` char(5) DEFAULT NULL,
  `TransactionDate` date NOT NULL,
  `DeliveryInsurance` tinyint(1) NOT NULL,
  `CourierType` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `transactionheader`
--

INSERT INTO `transactionheader` (`TransactionID`, `UserID`, `TransactionDate`, `DeliveryInsurance`, `CourierType`) VALUES
('TH001', 'UA004', '2023-03-01', 1, 'Nanji Express'),
('TH002', 'UA002', '2019-05-16', 1, 'Gejok'),
('TH003', 'UA005', '2022-09-09', 1, 'Gejok'),
('TH004', 'UA002', '2021-05-03', 0, 'J&E'),
('TH005', 'UA003', '2023-02-08', 1, 'Nanji Express'),
('TH006', 'UA003', '2022-06-23', 0, 'Gejok'),
('TH007', 'UA005', '2022-10-05', 0, 'JET'),
('TH008', 'UA002', '2021-03-15', 1, 'Nanji Express'),
('TH009', 'UA004', '2015-04-09', 1, 'JET'),
('TH010', 'UA002', '2022-02-10', 0, 'JET');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `carttable`
--
ALTER TABLE `carttable`
  ADD PRIMARY KEY (`UserID`,`ProductID`),
  ADD KEY `ProductID` (`ProductID`);

--
-- Indexes for table `msproduct`
--
ALTER TABLE `msproduct`
  ADD PRIMARY KEY (`ProductID`);

--
-- Indexes for table `msuser`
--
ALTER TABLE `msuser`
  ADD PRIMARY KEY (`UserID`);

--
-- Indexes for table `transactiondetail`
--
ALTER TABLE `transactiondetail`
  ADD PRIMARY KEY (`ProductID`,`TransactionID`),
  ADD KEY `TransactionID` (`TransactionID`);

--
-- Indexes for table `transactionheader`
--
ALTER TABLE `transactionheader`
  ADD PRIMARY KEY (`TransactionID`),
  ADD KEY `UserID` (`UserID`);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `carttable`
--
ALTER TABLE `carttable`
  ADD CONSTRAINT `carttable_ibfk_1` FOREIGN KEY (`UserID`) REFERENCES `msuser` (`UserID`),
  ADD CONSTRAINT `carttable_ibfk_2` FOREIGN KEY (`ProductID`) REFERENCES `msproduct` (`ProductID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `transactiondetail`
--
ALTER TABLE `transactiondetail`
  ADD CONSTRAINT `transactiondetail_ibfk_1` FOREIGN KEY (`ProductID`) REFERENCES `msproduct` (`ProductID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `transactiondetail_ibfk_2` FOREIGN KEY (`TransactionID`) REFERENCES `transactionheader` (`TransactionID`);

--
-- Constraints for table `transactionheader`
--
ALTER TABLE `transactionheader`
  ADD CONSTRAINT `transactionheader_ibfk_1` FOREIGN KEY (`UserID`) REFERENCES `msuser` (`UserID`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
