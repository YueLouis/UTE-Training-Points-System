-- V8__fix_event_mode_enum.sql
-- Fix EventMode enum: ATTENDACE → ATTENDANCE

UPDATE events SET event_mode = 'ATTENDANCE' WHERE event_mode = 'ATTENDACE';

