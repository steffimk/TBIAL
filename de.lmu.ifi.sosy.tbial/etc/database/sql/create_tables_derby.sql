drop table users;

create table users (
	id int generated always as identity primary key,
	name varchar(255) unique not null,
	password varchar(255) not null);