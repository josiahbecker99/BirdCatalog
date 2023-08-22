drop database if exists birdDB;
create database birdDB;

use birdDB;

drop table if exists birds;


create table birds (
	birdType varchar(25) not null,
	sex varchar(6) not null,
	dateObserved date not null
);

    
    