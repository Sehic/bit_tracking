# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table image_path (
  id                        bigint auto_increment not null,
  image_url                 varchar(255),
  profile_photo_id          bigint,
  constraint pk_image_path primary key (id))
;

create table location (
  id                        bigint auto_increment not null,
  x                         double,
  y                         double,
  constraint pk_location primary key (id))
;

create table package (
  id                        bigint auto_increment not null,
  post_office_id            bigint,
  tracking_num              varchar(255),
  destination               varchar(255),
  status                    ENUM('READY_FOR_SHIPING', 'ON_ROUTE', 'OUT_FOR_DELIVERY', 'DELIVERED'),
  constraint ck_package_status check (status in ('READY_FOR_SHIPING','ON_ROUTE','OUT_FOR_DELIVERY','DELIVERED')),
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
  constraint ck_user_type_of_user check (type_of_user in ('3','2','1','4')),
  constraint pk_user primary key (id))
;


create table linked_offices (
  post_officeA_id                bigint not null,
  post_officeB_id                bigint not null,
  constraint pk_linked_offices primary key (post_officeA_id, post_officeB_id))
;
alter table image_path add constraint fk_image_path_profilePhoto_1 foreign key (profile_photo_id) references user (id) on delete restrict on update restrict;
create index ix_image_path_profilePhoto_1 on image_path (profile_photo_id);
alter table package add constraint fk_package_postOffice_2 foreign key (post_office_id) references post_office (id) on delete restrict on update restrict;
create index ix_package_postOffice_2 on package (post_office_id);
alter table post_office add constraint fk_post_office_place_3 foreign key (place_id) references location (id) on delete restrict on update restrict;
create index ix_post_office_place_3 on post_office (place_id);
alter table user add constraint fk_user_postOffice_4 foreign key (post_office_id) references post_office (id) on delete restrict on update restrict;
create index ix_user_postOffice_4 on user (post_office_id);



alter table linked_offices add constraint fk_linked_offices_post_office_01 foreign key (post_officeA_id) references post_office (id) on delete restrict on update restrict;

alter table linked_offices add constraint fk_linked_offices_post_office_02 foreign key (post_officeB_id) references post_office (id) on delete restrict on update restrict;

# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table image_path;

drop table location;

drop table package;

drop table post_office;

drop table linked_offices;

drop table user;

SET FOREIGN_KEY_CHECKS=1;

