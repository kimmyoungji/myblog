CREATE TABLE `users` (
  `user_id` bigint PRIMARY KEY AUTO_INCREMENT,
  `email` varchar(255) UNIQUE NOT NULL,
  `password` varchar(255) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime
);

CREATE TABLE `categories` (
  `category_id` bigint PRIMARY KEY AUTO_INCREMENT,
  `parent_category_id` bigint,
  `name` varchar(100) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime
);

CREATE TABLE `posts` (
  `post_id` bigint PRIMARY KEY AUTO_INCREMENT,
  `category_id` bigint NOT NULL,
  `author_id` bigint NOT NULL,
  `file_group_id` bigint,
  `title` varchar(255) NOT NULL,
  `content` longtext NOT NULL,
  `view_count` int NOT NULL DEFAULT 0,
  `del_yn` char(1) NOT NULL DEFAULT 'Y',
  `created_at` datetime NOT NULL,
  `updated_at` datetime
);

CREATE TABLE `comments` (
  `comment_id` bigint PRIMARY KEY AUTO_INCREMENT,
  `parent_comment_id` bigint,
  `post_id` bigint NOT NULL,
  `author_id` bigint NOT NULL,
  `content` longtext NOT NULL,
  `del_yn` char(1) NOT NULL DEFAULT 'Y',
  `created_at` datetime NOT NULL,
  `updated_at` datetime
);

CREATE TABLE `file_groups` (
  `file_group_id` bigint PRIMARY KEY AUTO_INCREMENT,
  `created_at` datetime NOT NULL
);

CREATE TABLE `files` (
  `file_id` bigint PRIMARY KEY AUTO_INCREMENT,
  `file_group_id` bigint NOT NULL,
  `original_file_name` varchar(255) NOT NULL,
  `saved_file_name` varchar(255) NOT NULL,
  `file_path` varchar(500) NOT NULL,
  `file_size` bigint,
  `file_ext` varchar(30),
  `sort_order` int NOT NULL DEFAULT 0,
  `created_at` datetime NOT NULL
);

CREATE TABLE `roles` (
  `role_id` bigint PRIMARY KEY AUTO_INCREMENT,
  `role_code` varchar(50) UNIQUE NOT NULL,
  `role_name` varchar(100) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime
);

CREATE TABLE `user_roles` (
  `user_role_id` bigint PRIMARY KEY AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  `created_at` datetime NOT NULL
);

CREATE UNIQUE INDEX `user_roles_index_0` ON `user_roles` (`user_id`, `role_id`);

-- ALTER TABLE `categories` ADD FOREIGN KEY (`parent_category_id`) REFERENCES `categories` (`category_id`);

-- ALTER TABLE `posts` ADD FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`);

-- ALTER TABLE `posts` ADD FOREIGN KEY (`author_id`) REFERENCES `users` (`user_id`);

-- ALTER TABLE `posts` ADD FOREIGN KEY (`file_group_id`) REFERENCES `file_groups` (`file_group_id`);

-- ALTER TABLE `comments` ADD FOREIGN KEY (`parent_comment_id`) REFERENCES `comments` (`comment_id`);

-- ALTER TABLE `comments` ADD FOREIGN KEY (`post_id`) REFERENCES `posts` (`post_id`);

-- ALTER TABLE `comments` ADD FOREIGN KEY (`author_id`) REFERENCES `users` (`user_id`);

-- ALTER TABLE `files` ADD FOREIGN KEY (`file_group_id`) REFERENCES `file_groups` (`file_group_id`);

-- ALTER TABLE `user_roles` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

-- ALTER TABLE `user_roles` ADD FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`);