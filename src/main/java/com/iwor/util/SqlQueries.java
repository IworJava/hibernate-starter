package com.iwor.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SqlQueries {

    DROP_ALL_SQL("""
            DROP TABLE IF EXISTS users_chat,
                                 chat,
                                 profile,
                                 users,
                                 company_locale,
                                 company;
            """),

    CREATE_COMPANY_SQL("""
            CREATE TABLE company (
                id SERIAL PRIMARY KEY,
                name VARCHAR(255) NOT NULL UNIQUE
            );
            """),

    CREATE_PROFILE_SQL("""
            CREATE TABLE profile (
                id BIGSERIAL PRIMARY KEY,
                street VARCHAR(255),
                language CHAR(2)
            );
            """),

    CREATE_USERS_SQL("""
            CREATE TABLE users (
                id BIGSERIAL PRIMARY KEY,
                username VARCHAR(128) NOT NULL UNIQUE,
                firstname VARCHAR(128),
                lastname VARCHAR(128),
                birth_date DATE,
                role CHAR,
                info JSONB,
                company_id INT NULL REFERENCES company (id) ON DELETE SET NULL,
                profile_id BIGINT UNIQUE REFERENCES profile (id) ON DELETE CASCADE
            );
            """),

    CREATE_COMPANY_LOCALE_SQL("""
            CREATE TABLE company_locale (
                company_id INT NOT NULL REFERENCES company (id) ON DELETE CASCADE,
                lang CHAR(2) NOT NULL,
                description VARCHAR(255) NOT NULL,
                PRIMARY KEY (company_id, lang)
            );
            """),

    CREATE_CHAT_SQL("""
            CREATE TABLE chat (
                id BIGSERIAL PRIMARY KEY,
                name varchar(128) NOT NULL UNIQUE
            );
            """),

    CREATE_USERS_CHAT_SQL("""
            CREATE TABLE users_chat (
                id BIGSERIAL PRIMARY KEY,
                user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
                chat_id BIGINT NOT NULL REFERENCES chat (id) ON DELETE CASCADE,
                created_at TIMESTAMP NOT NULL,
                created_by VARCHAR(128) NOT NULL,
                UNIQUE (user_id, chat_id)
            );
            """);

    private final String query;
}
