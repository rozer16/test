public static User getUser(HttpServletRequest req) {
    HttpSession session = req.getSession(false);
    if (session == null) {
        return null;                      // ❌ don't return the session, return null
    }
    // Get the user object from the "user" attribute in the session
    return (User) session.getAttribute("user");
}

public static void login(HttpServletRequest req, HttpServletResponse resp, User user) {
    // Invalidate any existing session
    HttpSession oldSession = req.getSession(false);
    if (oldSession != null) {
        oldSession.invalidate();
    }

    // Create a new session and store the user
    HttpSession session = req.getSession(true);   // note: true to create a new one
    session.setAttribute("user", user);           // store under "user"
}

public static void login(HttpServletRequest req, HttpServletResponse resp, User user) {
    // Invalidate any existing session
    HttpSession oldSession = req.getSession(false);
    if (oldSession != null) {
        oldSession.invalidate();
    }

    // Create a new session and store the user
    HttpSession session = req.getSession(true);   // note: true to create a new one
    session.setAttribute("user", user);           // store under "user"
}

import jakarta.servlet.http.HttpSession;   // or javax.servlet.http.HttpSession
