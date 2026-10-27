package com.shakeup.backend.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Order(0)
public class SchemaCompatRunner implements ApplicationRunner {

	private final JdbcTemplate jdbc;

	public SchemaCompatRunner(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	@Override
	public void run(ApplicationArguments args) {
		jdbc.execute("ALTER TABLE IF EXISTS workout_plan_items ALTER COLUMN reps_target DROP NOT NULL");
		jdbc.execute("ALTER TABLE IF EXISTS workout_plan_items ALTER COLUMN weight_kg DROP NOT NULL");
		jdbc.execute(
				"ALTER TABLE IF EXISTS workout_plan_items ALTER COLUMN duration_seconds DROP NOT NULL");

		jdbc.execute("ALTER TABLE IF EXISTS exercises DROP CONSTRAINT IF EXISTS exercises_source_check");
		jdbc.execute(
				"ALTER TABLE IF EXISTS exercises ADD CONSTRAINT exercises_source_check "
						+ "CHECK (source IN ('SEED', 'WGER', 'CUSTOM'))");

		jdbc.execute("ALTER TABLE IF EXISTS foods DROP CONSTRAINT IF EXISTS foods_source_check");
		jdbc.execute(
				"ALTER TABLE IF EXISTS foods ADD CONSTRAINT foods_source_check "
						+ "CHECK (source IN ('SEED', 'WGER', 'OFF', 'CUSTOM'))");

		jdbc.execute("ALTER TABLE IF EXISTS foods ADD COLUMN IF NOT EXISTS serving_grams DOUBLE PRECISION");

		jdbc.execute("ALTER TABLE IF EXISTS users ADD COLUMN IF NOT EXISTS coaching_starts_on DATE");
		jdbc.execute("ALTER TABLE IF EXISTS users ADD COLUMN IF NOT EXISTS coaching_ends_on DATE");
	}
}
