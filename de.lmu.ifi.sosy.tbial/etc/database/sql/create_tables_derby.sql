drop table players;
drop table games;
drop table users;

create table users (
	id int generated always as identity primary key,
	name varchar(255) unique not null,
	password varchar(255) not null);
	
create table games (
	id int generated always as identity primary key,
	name varchar(255) unique not null,
	maxPlayers int not null,
	isPrivate boolean not null,
	hash char (128),
	salt char(16),
	check (maxPlayers >= 4 and maxPlayers <= 7));
	
create table players (
	id int generated always as identity,
	userid int references users(id),
	gameid int references games(id),
	ishost boolean not null);