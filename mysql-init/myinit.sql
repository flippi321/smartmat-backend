CREATE USER 'bjorn'@'%' IDENTIFIED BY 'passord';
GRANT ALL PRIVILEGES ON smartmat.* TO 'bjorn'@'%';
FLUSH PRIVILEGES;
