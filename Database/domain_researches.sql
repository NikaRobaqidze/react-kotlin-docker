-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Dec 02, 2023 at 07:01 PM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `domain_researches`
--

-- --------------------------------------------------------

--
-- Table structure for table `history`
--

CREATE TABLE `history` (
  `history_id` varchar(255) NOT NULL,
  `domain` varchar(255) NOT NULL,
  `details` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `execute_time_mils` bigint(20) NOT NULL,
  `date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Dumping data for table `history`
--

INSERT INTO `history` (`history_id`, `domain`, `details`, `execute_time_mils`, `date`) VALUES
('38bb9040-eab7-4490-9131-940c47d91a45-google.com', 'google.com', '[{\"hosts\":[\"2Fencrypted.google.com\",\"2Fsupport.google.com\",\"Encrypted.google.com\",\"accounts.google.com\",\"apis.google.com\",\"encrypted.google.com\",\"myaccount.google.com\",\"ogs.google.com\",\"play.google.com\",\"policies.google.com\",\"support.google.com\"],\"shodan\":[]}]', 12668, '2023-12-02 16:21:47'),
('c670d0fc-a6b8-4e68-8f51-569636f36ef1-agrobest.ge', 'agrobest.ge', '[{\"hosts\":[],\"shodan\":[]}]', 6752, '2023-12-02 16:22:04'),
('ff44dcae-4bb8-4296-b1ea-06bc2e519f78-kali.org', 'kali.org', '[{\"hosts\":[\"2Fdocs.kali.org\",\"2Ftools.kali.org\",\"arm.kali.org\",\"autopkgtest.kali.org\",\"bugs.kali.org\",\"discord.kali.org\",\"docs.kali.org\",\"forums.kali.org\",\"http.kali.org\",\"nethunter.kali.org\",\"old.kali.org\",\"pkg.kali.org\",\"status.kali.org\",\"tools.kali.org\"],\"shodan\":[]}]', 12725, '2023-12-02 16:21:25');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `history`
--
ALTER TABLE `history`
  ADD UNIQUE KEY `history_id` (`history_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
