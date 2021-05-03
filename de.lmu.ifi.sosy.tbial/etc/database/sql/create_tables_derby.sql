drop table users ;
drop table games ;

create table users (
	id int generated always as identity primary key,
	name varchar(255) unique not null,
	password varchar(255) not null);
	
create table games (
	id int generated always as identity,
	hostId int references users(id),
	name varchar(255) unique not null,
	maxPlayers int not null,
	isPrivate boolean not null,
	password varchar (255),
	check (maxPlayers >= 4 and maxPlayers <= 7));