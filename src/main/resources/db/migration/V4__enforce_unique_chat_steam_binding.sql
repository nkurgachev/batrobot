DELETE FROM chat_player_bindings
WHERE id IN (
    SELECT id
    FROM (
        SELECT id,
               ROW_NUMBER() OVER (
                   PARTITION BY telegram_chat_id, steam_id
                   ORDER BY created_at ASC, id ASC
               ) AS rn
        FROM chat_player_bindings
    ) ranked
    WHERE ranked.rn > 1
);

ALTER TABLE chat_player_bindings
    ADD CONSTRAINT uk_chat_steam UNIQUE (telegram_chat_id, steam_id);
