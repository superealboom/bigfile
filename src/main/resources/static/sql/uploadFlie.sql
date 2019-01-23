create table `upload_file` (
    `file_id` varchar(32) not null,
    `file_path` varchar(128) not null comment '文件存储路径',
    `file_size` varchar(32) not null comment '文件大小',
    `file_suffix` varchar(8) not null comment '文件后缀',
    `file_name` varchar(32) not null comment '文件名',
    `file_md5` varchar(32) not null comment '文件md5值',
    `create_time` timestamp default '0000-00-00 00:00:00',
    `update_time` timestamp default now() on update now(),
	`file_status` int not null comment '文件状态',
    primary key (`file_id`)
)comment '文件存储表';