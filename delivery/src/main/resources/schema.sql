create table city
(
    id          bigint  not null
        constraint city_pkey
            primary key,
    arrival     varchar(255),
    destination varchar(255),
    duration    integer not null
);

alter table city
    owner to eventuate;

create table delivery
(
    id             bigint  not null
        constraint delivery_pkey
            primary key,
    delivery_track varchar(255),
    duration       integer not null,
    order_id       varchar(255),
    city_id        bigint
        constraint fk4e0fu5fqs6rb0xn6bw39p6uyl
            references city
);

alter table delivery
    owner to eventuate;

INSERT INTO eventuate.city (id, arrival, destination, duration) VALUES (1, 'Moscow', 'A', 3);
INSERT INTO eventuate.city (id, arrival, destination, duration) VALUES (2, 'Saint-Petersburg', 'A', 2);
INSERT INTO eventuate.city (id, arrival, destination, duration) VALUES (3, 'Voronezh', 'A', 5);
INSERT INTO eventuate.city (id, arrival, destination, duration) VALUES (4, 'Moscow', 'B', 2);
INSERT INTO eventuate.city (id, arrival, destination, duration) VALUES (5, 'Saint-Petersburg', 'B', 3);