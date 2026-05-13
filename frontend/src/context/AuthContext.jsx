import { createContext, useContext, useMemo, useState } from "react";
import { loginUser } from "../api/auth";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(localStorage.getItem("token"));

  async function login(credentials) {
    const response = await loginUser(credentials);
    localStorage.setItem("token", response.token);
    setToken(response.token);
  }

  function logout() {
    localStorage.removeItem("token");
    setToken(null);
  }

  const value = useMemo(() => ({
    token,
    isAuthenticated: !!token,
    login,
    logout,
  }), [token]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  return useContext(AuthContext);
}