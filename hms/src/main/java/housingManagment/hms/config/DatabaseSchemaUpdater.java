package housingManagment.hms.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Executes database schema updates that can't be handled by entity changes
 * alone.
 * Specifically, removes duplicate columns from tables and fixes database
 * constraints.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DatabaseSchemaUpdater {

        private final JdbcTemplate jdbcTemplate;

        @PostConstruct
        public void updateSchema() {
                log.info("Checking and updating database schema");

                try {
                        // Remove duplicate gender column from student table
                        jdbcTemplate.execute("ALTER TABLE user_students DROP COLUMN IF EXISTS gender");
                        log.info("Removed duplicate gender column from user_students table if it existed");

                        // Remove duplicate gender column from teacher table
                        jdbcTemplate.execute("ALTER TABLE user_teachers DROP COLUMN IF EXISTS gender");
                        log.info("Removed duplicate gender column from user_teachers table if it existed");

                        // Fix student role check constraint
                        jdbcTemplate.execute(
                                        "ALTER TABLE user_students DROP CONSTRAINT IF EXISTS user_students_role_check");
                        jdbcTemplate.execute("ALTER TABLE user_students ADD CONSTRAINT user_students_role_check " +
                                        "CHECK (role IN ('BACHELOR_1_Y', 'BACHELOR_2_Y', 'BACHELOR_3_Y', 'BACHELOR_4_Y', "
                                        +
                                        "'MASTER_1_Y', 'MASTER_2_Y', 'DOCTORAL_STUDENT_1_Y', 'DOCTORAL_STUDENT_2_Y', " +
                                        "'DOCTORAL_STUDENT_3_Y', 'DOCTORAL_STUDENT_4_Y', 'EXCHANGE_STUDENT', 'GUEST_STUDENT', "
                                        +
                                        "'MASTER_STUDENT_1_Y', 'MASTER_STUDENT_2_Y', " +
                                        "'BACHELOR_DEGREE_1_Y', 'BACHELOR_DEGREE_2_Y', 'BACHELOR_DEGREE_3_Y', 'BACHELOR_DEGREE_4_Y', 'BACHELOR_DEGREE_5_Y', "
                                        +
                                        "'NUFYP', 'DOCTORAL_STUDENT_5_Y'))");
                        log.info("Fixed student role check constraint");

                        // Fix student school check constraint
                        jdbcTemplate.execute(
                                        "ALTER TABLE user_students DROP CONSTRAINT IF EXISTS user_students_school_check");
                        jdbcTemplate.execute("ALTER TABLE user_students ADD CONSTRAINT user_students_school_check " +
                                        "CHECK (school IN ('SCHOOL_OF_ENGINEERING_AND_DIGITAL_SCIENCES', " +
                                        "'SCHOOL_OF_MEDICINE', 'SCHOOL_OF_SCIENCES_AND_HUMANITIES', " +
                                        "'GRADUATE_SCHOOL_OF_PUBLIC_POLICY', 'GRADUATE_SCHOOL_OF_BUSINESS', " +
                                        "'GRADUATE_SCHOOL_OF_EDUCATION'))");
                        log.info("Fixed student school check constraint");

                        // Fix maintenance role check constraint
                        jdbcTemplate.execute(
                                        "ALTER TABLE user_maintenance DROP CONSTRAINT IF EXISTS user_maintenance_role_check");
                        jdbcTemplate.execute("ALTER TABLE user_maintenance ADD CONSTRAINT user_maintenance_role_check "
                                        +
                                        "CHECK (role IN ('MAINTENANCE_DISPATCHER', 'MAINTENANCE_ENGINEER', 'MAINTENANCE_STAFF'))");
                        log.info("Fixed maintenance role check constraint");

                        // Fix housing management role check constraint
                        jdbcTemplate.execute(
                                        "ALTER TABLE user_housing_management DROP CONSTRAINT IF EXISTS user_housing_management_role_check");
                        jdbcTemplate
                                        .execute("ALTER TABLE user_housing_management ADD CONSTRAINT user_housing_management_role_check "
                                                        +
                                                        "CHECK (role IN ('MANAGER', 'BLOCK_MANAGER'))");
                        log.info("Fixed housing management role check constraint");

                        // Fix DSS role check constraint
                        jdbcTemplate.execute("ALTER TABLE user_d_s_s DROP CONSTRAINT IF EXISTS user_d_s_s_role_check");
                        jdbcTemplate.execute("ALTER TABLE user_d_s_s ADD CONSTRAINT user_d_s_s_role_check " +
                                        "CHECK (role IN ('DSS_MANAGER', 'DSS_ASSISTANT'))");
                        log.info("Fixed DSS role check constraint");
                } catch (Exception e) {
                        log.error("Error updating database schema: {}", e.getMessage(), e);
                        // Don't fail application startup - the application can still function
                        // and will just log errors when trying to save entities
                }
        }
}