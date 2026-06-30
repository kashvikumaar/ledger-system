CREATE OR REPLACE FUNCTION prevent_entries_modification()
RETURNS TRIGGER AS $$
BEGIN
    RAISE EXCEPTION 'entries table is append-only';
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER no_update_entries
    BEFORE UPDATE ON entries
    FOR EACH ROW
    EXECUTE FUNCTION prevent_entries_modification();

CREATE TRIGGER no_delete_entries
    BEFORE DELETE ON entries
    FOR EACH ROW
    EXECUTE FUNCTION prevent_entries_modification();