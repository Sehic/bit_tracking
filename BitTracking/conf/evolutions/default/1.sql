# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table post_office (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  address                   varchar(255),
  constraint pk_post_office primary key (id))
;

create table user (
  id                        bigint auto_increment not null,
  first_name                varchar(50),
  last_name                 varchar(50),
  password                  varchar(50),
  email                     varchar(50),
  type_of_user              integer,
  post_office_id            bigint,
  constraint ck_user_type_of_user check (type_of_user in ('3','2','1')),
  constraint pk_user primary key (id))
;

alter table user add constraint fk_user_postOffice_1 foreign key (post_office_id) references post_office (id) on delete restrict on update restrict;
create index ix_user_postOffice_1 on user (post_office_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table post_office;

drop table user;

SET FOREIGN_KEY_CHECKS=1;

