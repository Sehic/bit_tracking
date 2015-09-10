# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table image_path (
  id                        bigint auto_increment not null,
  image_url                 varchar(255),
  profile_photo_id          bigint,
  constraint uq_image_path_profile_photo_id unique (profile_photo_id),
  constraint pk_image_path primary key (id))
;

create table location (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  x                         double,
  y                         double,
  constraint pk_location primary key (id))
;

create table package (
  id                        bigint auto_increment not null,
  post_office_id            bigint,
  destination               varchar(255),
  constraint pk_package primary key (id))
;

create table post_office (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  address                   varchar(255),
  place_id                  bigint,
  constraint uq_post_office_place_id unique (place_id),
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

alter table image_path add constraint fk_image_path_profilePhoto_1 foreign key (profile_photo_id) references user (id) on delete restrict on update restrict;
create index ix_image_path_profilePhoto_1 on image_path (profile_photo_id);
alter table package add constraint fk_package_postOffice_2 foreign key (post_office_id) references post_office (id) on delete restrict on update restrict;
create index ix_package_postOffice_2 on package (post_office_id);
alter table post_office add constraint fk_post_office_place_3 foreign key (place_id) references location (id) on delete restrict on update restrict;
create index ix_post_office_place_3 on post_office (place_id);
alter table user add constraint fk_user_postOffice_4 foreign key (post_office_id) references post_office (id) on delete restrict on update restrict;
create index ix_user_postOffice_4 on user (post_office_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table image_path;

drop table location;

drop table package;

drop table post_office;

drop table user;

SET FOREIGN_KEY_CHECKS=1;

