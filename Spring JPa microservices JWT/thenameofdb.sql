-- phpMyAdmin SQL Dump
-- version 4.7.0
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Nov 13, 2017 at 07:55 PM
-- Server version: 10.1.25-MariaDB
-- PHP Version: 5.6.31

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `thenameofdb`
CREATE DATABASE `thenameofdb`;
--

-- --------------------------------------------------------

--
-- Table structure for table `latest_statistics`
--

CREATE TABLE `latest_statistics` (
  `ID` int(11) NOT NULL,
  `DESCRIPTION` varchar(100) NOT NULL,
  `DATE` date NOT NULL,
  `EMAIL` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `latest_statistics`
--

INSERT INTO `latest_statistics` (`ID`, `DESCRIPTION`, `DATE`, `EMAIL`) VALUES
(1, 'You have 3 high priority todos and 4 low priority todos.', '2017-11-11', 'alex@quicktutorialz.com'),
(2, 'You have <b>0 low priority </b> ToDos and <b>1 high priority</b> ToDos.', '2017-11-11', 'alex@quicktutorialz.com'),
(3, 'You have <b>0 low priority </b> ToDos and <b>1 high priority</b> ToDos.', '2017-11-11', 'alex@quicktutorialz.com'),
(7, 'You have <b>1 low priority </b> ToDos and <b>1 high priority</b> ToDos.', '2017-11-13', 'alex@quicktutorialz.com');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `latest_statistics`
--
ALTER TABLE `latest_statistics`
  ADD PRIMARY KEY (`ID`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `latest_statistics`
--
ALTER TABLE `latest_statistics`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
