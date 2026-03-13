CREATE TABLE IF NOT EXISTS users (
    id UUID NOT NULL,
    telegram_user_id BIGINT NOT NULL,
    username VARCHAR(255),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_telegram_users PRIMARY KEY (id),
    CONSTRAINT uk_telegram_users_telegram_user_id UNIQUE (telegram_user_id)
);

CREATE TABLE IF NOT EXISTS chats (
    id UUID NOT NULL,
    telegram_chat_id BIGINT NOT NULL,
    type VARCHAR(255),
    title VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_chats PRIMARY KEY (id),
    CONSTRAINT uk_chats_telegram_chat_id UNIQUE (telegram_chat_id)
);

CREATE TABLE IF NOT EXISTS players (
    id UUID NOT NULL,
    steam_id BIGINT NOT NULL,
    name VARCHAR(255),
    profile_url VARCHAR(255),
    avatar_url VARCHAR(255),
    account_creation_date BIGINT,
    activity VARCHAR(255),
    is_dota_plus_subscriber BOOLEAN,
    season_rank INTEGER,
    imp INTEGER,
    smurf_flag INTEGER,
    community_visible_state INTEGER,
    is_anonymous BOOLEAN,
    is_stratz_public BOOLEAN,
    match_count INTEGER,
    win_count INTEGER,
    first_match_date BIGINT,
    last_match_date BIGINT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_players PRIMARY KEY (id),
    CONSTRAINT uk_players_steam_id UNIQUE (steam_id)
);

CREATE TABLE IF NOT EXISTS matches (
    id UUID NOT NULL,
    match_id BIGINT NOT NULL,
    duration_seconds INTEGER,
    start_date_time BIGINT,
    end_date_time BIGINT,
    lobby_type VARCHAR(255),
    game_mode VARCHAR(255),
    actual_rank INTEGER,
    radiant_kills INTEGER,
    dire_kills INTEGER,
    analysis_outcome VARCHAR(255),
    bottom_lane_outcome VARCHAR(255),
    mid_lane_outcome VARCHAR(255),
    top_lane_outcome VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_matches PRIMARY KEY (id),
    CONSTRAINT uk_matches_match_id UNIQUE (match_id)
);

CREATE TABLE IF NOT EXISTS chat_player_bindings (
    id UUID NOT NULL,
    telegram_chat_id BIGINT NOT NULL,
    telegram_user_id BIGINT NOT NULL,
    steam_id BIGINT NOT NULL,
    is_primary BOOLEAN NOT NULL,
    notification_settings TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_chat_player_bindings PRIMARY KEY (id),
    CONSTRAINT uk_chat_user_steam UNIQUE (telegram_chat_id, telegram_user_id, steam_id),
    CONSTRAINT fk_cpb_chat FOREIGN KEY (telegram_chat_id) REFERENCES chats (telegram_chat_id),
    CONSTRAINT fk_cpb_user FOREIGN KEY (telegram_user_id) REFERENCES users (telegram_user_id),
    CONSTRAINT fk_cpb_steam FOREIGN KEY (steam_id) REFERENCES players (steam_id)
);

CREATE TABLE IF NOT EXISTS player_match_stats (
    id UUID NOT NULL,
    match_id BIGINT NOT NULL,
    steam_id BIGINT NOT NULL,
    hero_id INTEGER,
    hero_name VARCHAR(255),
    is_victory BOOLEAN,
    is_radiant BOOLEAN,
    kills INTEGER,
    deaths INTEGER,
    assists INTEGER,
    num_last_hits INTEGER,
    num_denies INTEGER,
    gold_per_minute INTEGER,
    experience_per_minute INTEGER,
    hero_damage INTEGER,
    tower_damage INTEGER,
    hero_healing INTEGER,
    lane VARCHAR(255),
    position VARCHAR(255),
    imp INTEGER,
    award VARCHAR(255),
    camp_stack INTEGER,
    courier_kills INTEGER,
    sentry_wards_purchased INTEGER,
    observer_wards_purchased INTEGER,
    sentry_wards_destroyed INTEGER,
    observer_wards_destroyed INTEGER,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_player_match_stats PRIMARY KEY (id),
    CONSTRAINT fk_pms_match FOREIGN KEY (match_id) REFERENCES matches (match_id),
    CONSTRAINT fk_pms_steam FOREIGN KEY (steam_id) REFERENCES players (steam_id),
    CONSTRAINT uk_pms_steam_match UNIQUE (steam_id, match_id)
);

CREATE TABLE IF NOT EXISTS player_rank_history (
    id UUID NOT NULL,
    steam_id BIGINT NOT NULL,
    season_rank INTEGER NOT NULL,
    assigned_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_player_rank_history PRIMARY KEY (id),
    CONSTRAINT fk_prh_steam FOREIGN KEY (steam_id) REFERENCES players (steam_id),
    CONSTRAINT uk_prh_steam_assigned UNIQUE (steam_id, assigned_at)
);

CREATE INDEX IF NOT EXISTS idx_chats_updated ON chats (updated_at);
CREATE INDEX IF NOT EXISTS idx_users_updated ON users (updated_at);
CREATE INDEX IF NOT EXISTS idx_players_updated ON players (updated_at);
CREATE INDEX IF NOT EXISTS idx_players_steam_id ON players (steam_id);
CREATE INDEX IF NOT EXISTS idx_match_start_datetime ON matches (start_date_time);
CREATE INDEX IF NOT EXISTS idx_cpb_chat_user ON chat_player_bindings (telegram_chat_id, telegram_user_id);
CREATE INDEX IF NOT EXISTS idx_cpb_steam ON chat_player_bindings (steam_id);
CREATE INDEX IF NOT EXISTS idx_pms_steam_id ON player_match_stats (steam_id);
CREATE INDEX IF NOT EXISTS idx_pms_match_id ON player_match_stats (match_id);
CREATE INDEX IF NOT EXISTS idx_prh_steam_assigned ON player_rank_history (steam_id, assigned_at);
CREATE INDEX IF NOT EXISTS idx_prh_assigned ON player_rank_history (assigned_at);
