
import { useEffect, useState } from "react";
import { Navigate, Outlet } from "react-router-dom";

/**
 * Protects NLIC Connect pages from
 * unauthenticated visitors.
 *
 * Checks the authenticated session using
 * the Spring Boot backend.
 */
function ProtectedRoute() {
  const [authStatus, setAuthStatus] = useState("checking");

  useEffect(() => {
    const controller = new AbortController();

    async function checkAuthentication() {
      try {
        const response = await fetch(
          "http://localhost:8080/api/auth/me",
          {
            method: "GET",
            credentials: "include",
            signal: controller.signal,
          }
        );

        if (!controller.signal.aborted) {
          setAuthStatus(
            response.ok ? "authenticated" : "unauthenticated"
          );
        }
      } catch (error) {
        if (!controller.signal.aborted) {
          console.error(
            "Authentication check failed:",
            error
          );

          setAuthStatus("unauthenticated");
        }
      }
    }

    checkAuthentication();

    return () => controller.abort();
  }, []);

  // Wait for the backend to verify the session.
  if (authStatus === "checking") {
    return (
      <div
        style={{
          textAlign: "center",
          padding: "80px 20px",
        }}
      >
        <h2>NLIC Connect</h2>
        <p>Verifying your session...</p>
      </div>
    );
  }

  // Redirect unauthenticated visitors.
  if (authStatus === "unauthenticated") {
    return <Navigate to="/login" replace />;
  }

  // Display protected pages for authenticated users.
  return <Outlet />;
}

export default ProtectedRoute;