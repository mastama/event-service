-- Schema public (default). Kalau mau schema khusus, ganti public -> core.
-- Tabel "event"
CREATE TABLE IF NOT EXISTS public.event (
    id           UUID         PRIMARY KEY,                       -- diisi app via @PrePersist
    title        TEXT         NOT NULL,
    start_time   TIMESTAMP    NOT NULL,                          -- LocalDateTime -> TIMESTAMP (w/o tz)
    end_time     TIMESTAMP    NOT NULL,
    location     TEXT,
    quota        INTEGER      NOT NULL CHECK (quota >= 0),
    description  TEXT,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),            -- Instant -> TIMESTAMPTZ
    updated_at   TIMESTAMPTZ,
    CONSTRAINT ck_event_time CHECK (end_time > start_time)
    );

-- Tabel peserta
CREATE TABLE IF NOT EXISTS public.event_participant (
    id         UUID        PRIMARY KEY,
    event_id   UUID        NOT NULL,
    warga_nik  VARCHAR(16) NOT NULL CHECK (char_length(warga_nik) = 16),
    CONSTRAINT fk_event_participant_event
    FOREIGN KEY (event_id) REFERENCES public.event(id)
    ON DELETE CASCADE,
    CONSTRAINT uq_event_warga UNIQUE (event_id, warga_nik)
    );

-- Indeks yang berguna
CREATE INDEX IF NOT EXISTS idx_event_start_time ON public.event (start_time);
CREATE INDEX IF NOT EXISTS idx_event_title_lower ON public.event (lower(title));
CREATE INDEX IF NOT EXISTS idx_event_participant_event ON public.event_participant (event_id);
CREATE INDEX IF NOT EXISTS idx_event_participant_warga ON public.event_participant (warga_nik);
