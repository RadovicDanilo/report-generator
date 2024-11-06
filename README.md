# softverske-komponente-1

# SQL SCRIPT

jdbc:mysql://127.0.0.1:3306/report?user=root"&"password=

CREATE TABLE employees (
id INT AUTO_INCREMENT PRIMARY KEY,
first_name VARCHAR(50),
last_name VARCHAR(50),
age INT,
salary DECIMAL(10, 2),
city VARCHAR(50)
);

INSERT INTO employees (first_name, last_name, age, salary, city) VALUES
('John', 'Doe', 30, 50000.00, 'New York'),
('Jane', 'Smith', 28, 55000.00, 'Los Angeles'),
('Michael', 'Johnson', 40, 70000.00, 'Chicago'),
('Emily', 'Davis', 35, 62000.00, 'Houston'),
('David', 'Wilson', 45, 75000.00, 'Phoenix'),
('Sarah', 'Martinez', 32, 58000.00, 'San Diego');
 
