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
  tracking_num              varchar(255),
  recipient_address         varchar(255),
  destination               varchar(255),
  weight                    double(10,2),
  price                     double(10,2),
  package_type              integer,
  sender_name               varchar(255),
  recipient_name            varchar(255),
  approved                  tinyint(1) default 0,
  seen                      tinyint(1) default 0,
  constraint ck_package_package_type check (package_type in ('4','2','1','3')),
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

create table shipment (
  id                        bigint auto_increment not null,
  post_office_id_id         bigint,
  package_id_id             bigint,
  status                    integer,
  date_created              datetime(6),
  constraint ck_shipment_status check (status in ('5','1','2','4','3')),
  constraint pk_shipment primary key (id))
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


create table package_user (
  package_id                     bigint not null,
  user_id                        bigint not null,
  constraint pk_package_user primary key (package_id, user_id))
;

create table linked_offices (
  post_officeA_id                bigint not null,
  post_officeB_id                bigint not null,
  constraint pk_linked_offices primary key (post_officeA_id, post_officeB_id))
;
alter table image_path add constraint fk_image_path_profilePhoto_1 foreign key (profile_photo_id) references user (id) on delete restrict on update restrict;
create index ix_image_path_profilePhoto_1 on image_path (profile_photo_id);
alter table post_office add constraint fk_post_office_place_2 foreign key (place_id) references location (id) on delete restrict on update restrict;
create index ix_post_office_place_2 on post_office (place_id);
alter table shipment add constraint fk_shipment_postOfficeId_3 foreign key (post_office_id_id) references post_office (id) on delete restrict on update restrict;
create index ix_shipment_postOfficeId_3 on shipment (post_office_id_id);
alter table shipment add constraint fk_shipment_packageId_4 foreign key (package_id_id) references package (id) on delete restrict on update restrict;
create index ix_shipment_packageId_4 on shipment (package_id_id);
alter table user add constraint fk_user_postOffice_5 foreign key (post_office_id) references post_office (id) on delete restrict on update restrict;
create index ix_user_postOffice_5 on user (post_office_id);



alter table package_user add constraint fk_package_user_package_01 foreign key (package_id) references package (id) on delete restrict on update restrict;

alter table package_user add constraint fk_package_user_user_02 foreign key (user_id) references user (id) on delete restrict on update restrict;

alter table linked_offices add constraint fk_linked_offices_post_office_01 foreign key (post_officeA_id) references post_office (id) on delete restrict on update restrict;

alter table linked_offices add constraint fk_linked_offices_post_office_02 foreign key (post_officeB_id) references post_office (id) on delete restrict on update restrict;

# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table image_path;

drop table location;

drop table package;

drop table package_user;

drop table post_office;

drop table linked_offices;

drop table shipment;

drop table user;

SET FOREIGN_KEY_CHECKS=1;

