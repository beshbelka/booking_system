package booking_system;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.crypto.SecretKey;
import java.net.ConnectException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "booking_system.repository")
@EnableScheduling
public class BookingSystemApplication {

	private static final String DB_NAME = "bookingsystem_db";
	private static final String USER = "postgres";
	private static final String PASSWORD = "postgres";

	public static void main(String[] args) {
		createDatabase();
		SpringApplication.run(BookingSystemApplication.class, args);
	}

	private static void createDatabase() {
		try {
			Class.forName("org.postgresql.Driver");
			try (Connection conn = DriverManager.getConnection(
					"jdbc:postgresql://localhost:5432/postgres", USER, PASSWORD);
				 Statement stmt = conn.createStatement()) {
				String checkSql = "SELECT 1 FROM pg_database WHERE datname = '" + DB_NAME + "'";
				ResultSet rs = stmt.executeQuery(checkSql);
				if (!rs.next()) {
					String createSql = "CREATE DATABASE \"" + DB_NAME + "\"";
					stmt.executeUpdate(createSql);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
