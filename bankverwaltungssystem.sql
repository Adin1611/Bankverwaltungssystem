-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Erstellungszeit: 02. Feb 2024 um 14:57
-- Server-Version: 10.4.28-MariaDB
-- PHP-Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Datenbank: `bankverwaltungssystem`
--

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `girokonto`
--

CREATE TABLE `girokonto` (
  `kontoNr` bigint(20) NOT NULL,
  `kontostand` double NOT NULL,
  `kontoAktiv` tinyint(1) NOT NULL,
  `summeEinzahlungen` double NOT NULL,
  `summeAuszahlungen` double NOT NULL,
  `ueberziehungsLimit` double NOT NULL,
  `negativZinssatz` double NOT NULL,
  `positivZinssatz` double NOT NULL,
  `spesen` double NOT NULL,
  `kid` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Daten für Tabelle `girokonto`
--

INSERT INTO `girokonto` (`kontoNr`, `kontostand`, `kontoAktiv`, `summeEinzahlungen`, `summeAuszahlungen`, `ueberziehungsLimit`, `negativZinssatz`, `positivZinssatz`, `spesen`, `kid`) VALUES
(1, 200, 1, 0, 0, 300, 2, 3, 20, 3),
(2, 200, 1, 0, 0, 100, 2.3, 4.6, 40, 9),
(3, 200, 1, 0, 0, 30, 2, 3, 20, 15),
(4, 500, 1, 0, 0, 30, 2, 3, 20, 16),
(5, 980, 1, 0, 20, 300, 2.3, 4.3, 20, 18),
(6, 2980, 1, 0, 20, 30, 2.3, 1.7, 20, 20),
(7, 2020, 1, 20, 0, 30, 1.2, 1.3, 20, 21),
(9, 200, 1, 0, 0, 20, 2, 5, 30, 4),
(10, 1142.8, 1, 0, 60, 10, 2, 3, 20, 28),
(11, 240, 1, 40, 0, 10, 2.3, 4.3, 30, 29),
(12, 2232.76, 1, 0, 70, 30, 1.2, 1.5, 20, 30),
(13, 1050, 1, 50, 0, 30, 4, 2, 20, 31),
(14, 50286, 1, 50000, 700, 30, 2, 3, 20, 32),
(15, 200, 1, 0, 0, 20, 1, 2, 30, 34),
(18, 2000, 1, 0, 0, 30, 2, 3, 20, 37),
(19, 2193.6000000000004, 1, 500, 320, 30, 2, 3, 20, 38),
(276363534, 200, 1, 0, 0, 30, 1, 2, 20, 47);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `kunde`
--

CREATE TABLE `kunde` (
  `kid` int(11) NOT NULL,
  `name` varchar(30) NOT NULL,
  `ort` varchar(30) NOT NULL,
  `email` varchar(50) NOT NULL,
  `kreWuerdigkeit` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Daten für Tabelle `kunde`
--

INSERT INTO `kunde` (`kid`, `name`, `ort`, `email`, `kreWuerdigkeit`) VALUES
(1, 'adin', 'spillerb', 'adin.sali@outlook.com', 1),
(2, 'd', 's', 's', 1),
(3, 'noah', 'ruthner', 'noah.ruthner@outlook.com', 1),
(4, 'a', 'd', 'a', 1),
(5, 'a', 'd', 'a', 1),
(8, 'Noah', 'Ruthner', 'noah.ruthner@gmail.com', 1),
(14, 'Lukas Hasibar', 'Spillern', 'l.hasibar@gmail.com', 1),
(15, 'adin salihovic', 'spillern', 'adin.sali@outlook.com', 1),
(16, 'Azam Asipi', 'Großweikersdorf', 'azam.asipi@gmail.com', 1),
(17, 'Max Muster', 'Stockerau', 'max.muster@gmail.com', 1),
(18, 'Phillip Plessl', 'Obritz', 'pipo.plessel@gmail.com', 1),
(19, 'Julian Weiler', 'Laa an der Thaya', 'julian.weiler@outlook.com', 1),
(20, 'Florian Winkelhofer', 'Großnondorf', 'f.winkel@gmail.com', 1),
(21, 'Peter Himmelbauer', 'Grafenberg', 'p.himmel@outloojk.com', 1),
(22, 'Benjamin Kadl', 'Patzental', 'b.k@gmail.com', 1),
(23, 'Elias Bergermayer', 'Großkadolz', 'e.b@gmail.com', 1),
(24, 'a', 'b', 'c', 1),
(25, 'a', 'b', 'c', 1),
(26, 'adin salihovic', 'spillern', 'adin.sali@outlook.com', 1),
(27, 'adin salihovic', 'spillern', 'a', 1),
(45, 'martin', 'werenburt', 'm.w@gmail.com', 1),
(46, 'heinz', 'mieler', 'h.m@gmail.com', 1),
(47, 'martin schneider', 'spillerb', 'm.a@outlook.com', 1);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `sparkonto`
--

CREATE TABLE `sparkonto` (
  `kontoNr` bigint(11) NOT NULL,
  `kontostand` int(11) NOT NULL,
  `kontoAktiv` int(11) NOT NULL,
  `summeEinzahlungen` int(11) NOT NULL,
  `summeAuszahlungen` int(11) NOT NULL,
  `zinssatz` int(11) NOT NULL,
  `kid` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Daten für Tabelle `sparkonto`
--

INSERT INTO `sparkonto` (`kontoNr`, `kontostand`, `kontoAktiv`, `summeEinzahlungen`, `summeAuszahlungen`, `zinssatz`, `kid`) VALUES
(1, 150000, 1, 0, 0, 2, 10),
(2, 150000, 1, 0, 0, 2, 14),
(3, 300, 1, 0, 0, 2, 17),
(4, 2000, 1, 0, 0, 2, 19),
(5, 2020, 1, 0, 0, 1, 23),
(6, 3200, 1, 200, 0, 2, 33),
(7, 200, 1, 0, 0, 2, 43);

--
-- Indizes der exportierten Tabellen
--

--
-- Indizes für die Tabelle `girokonto`
--
ALTER TABLE `girokonto`
  ADD PRIMARY KEY (`kontoNr`);

--
-- Indizes für die Tabelle `kunde`
--
ALTER TABLE `kunde`
  ADD PRIMARY KEY (`kid`);

--
-- Indizes für die Tabelle `sparkonto`
--
ALTER TABLE `sparkonto`
  ADD PRIMARY KEY (`kontoNr`);

--
-- AUTO_INCREMENT für exportierte Tabellen
--

--
-- AUTO_INCREMENT für Tabelle `girokonto`
--
ALTER TABLE `girokonto`
  MODIFY `kontoNr` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=82928937379;

--
-- AUTO_INCREMENT für Tabelle `kunde`
--
ALTER TABLE `kunde`
  MODIFY `kid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=48;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
