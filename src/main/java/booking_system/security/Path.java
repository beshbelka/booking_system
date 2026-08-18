package booking_system.security;

public class Path {

    public static final String[] PUBLIC = {
            "/css/**",
            "/js/**",
            "/images/**",
            "/error",
            "/favicon.ico"
    };

    public static final String[] PUBLIC_GET = {
            "/",
            "/about",
            "/auth/login",
            "/auth/registration",
            "/seances",
            "/seats",
            "/error"
    };

    public static final String[] PUBLIC_POST = {
            "/auth/register",
            "/auth/login",
            "/book"
    };
}
