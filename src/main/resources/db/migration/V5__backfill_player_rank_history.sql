INSERT INTO player_rank_history (id, steam_id, season_rank, assigned_at)
SELECT
    CAST(
        SUBSTRING(MD5('rank-history-backfill-20260314-' || CAST(p.steam_id AS VARCHAR)), 1, 8) || '-' ||
        SUBSTRING(MD5('rank-history-backfill-20260314-' || CAST(p.steam_id AS VARCHAR)), 9, 4) || '-' ||
        SUBSTRING(MD5('rank-history-backfill-20260314-' || CAST(p.steam_id AS VARCHAR)), 13, 4) || '-' ||
        SUBSTRING(MD5('rank-history-backfill-20260314-' || CAST(p.steam_id AS VARCHAR)), 17, 4) || '-' ||
        SUBSTRING(MD5('rank-history-backfill-20260314-' || CAST(p.steam_id AS VARCHAR)), 21, 12)
        AS UUID
    ),
    p.steam_id,
    p.season_rank,
    TIMESTAMP WITH TIME ZONE '2026-03-14 00:01:00+03:00'
FROM players p
WHERE p.season_rank IS NOT NULL
  AND NOT EXISTS (
      SELECT 1
      FROM player_rank_history prh
      WHERE prh.steam_id = p.steam_id
  );