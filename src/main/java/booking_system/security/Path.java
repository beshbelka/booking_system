package booking_system.security;

public class Path {

    public static final String[] PUBLIC = {
            "/css/**",
            "/js/**",
            "/images/**",
            "/error"
    };

    public static final String[] PUBLIC_GET = {
            "/",
            "/about",
            "/auth/login",
            "/auth/registration"
    };

    public static final String[] PUBLIC_POST = {
            "/auth/register",
            "/auth/login"
    };
}
