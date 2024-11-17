# softverske-komponente-1

# SQL SCRIPT

jdbc:mysql://127.0.0.1:3306/report?user=root"&"password=

CREATE TABLE zaposleni (
id INT AUTO_INCREMENT PRIMARY KEY,
ime VARCHAR(50),
prezime VARCHAR(50),
godine INT,
plata DECIMAL(10, 2),
grad VARCHAR(50)
);

INSERT INTO zaposleni (ime, prezime, godine, plata, grad) VALUES
('Marko', 'Petrović', 30, 50000.00, 'Beograd'),
('Jelena', 'Jovanović', 28, 55000.00, 'Novi Sad'),
('Nikola', 'Nikolić', 40, 70000.00, 'Niš'),
('Milica', 'Stanković', 35, 62000.00, 'Kragujevac'),
('Dejan', 'Mitrović', 45, 75000.00, 'Subotica'),
('Ana', 'Kostić', 32, 58000.00, 'Zrenjanin'),
('Stefan', 'Lazić', 29, 53000.00, 'Čačak'),
('Ivana', 'Savić', 27, 52000.00, 'Šabac'),
('Lazar', 'Popović', 37, 68000.00, 'Kruševac'),
('Dragana', 'Vukić', 33, 60000.00, 'Pirot');
 
