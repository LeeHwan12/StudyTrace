import { createContext, useContext, useEffect, useMemo, useState } from "react";
import * as authApi from "../lib/auth"; // login, logout, me 함수가 있는 파일 경로로 맞추기

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);      // 로그인 유저 정보
  const [loading, setLoading] = useState(true); // 초기 me() 체크 중

  // 앱 시작 시: 세션이 살아있으면 user 복구
  useEffect(() => {
    let cancelled = false;

    (async () => {
      try {
        const me = await authApi.me();
        if (!cancelled) setUser(me);
      } catch {
        if (!cancelled) setUser(null);
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();

    return () => {
      cancelled = true;
    };
  }, []);

  // 로그인: /login → 성공하면 /me로 유저 가져와서 세팅
  const login = async ({ username, password }) => {
    await authApi.login({ username, password });
    const me = await authApi.me();
    setUser(me);
    return me;
  };

  // 로그아웃: /logout → 유저 비우기
  const logout = async () => {
    try {
      await authApi.logout();
    } finally {
      setUser(null);
    }
  };

  // 회원가입: /register → 서버가 자동로그인까지 해주므로 /me로 유저 세팅
  const register = async (payload) => {
    await authApi.register(payload);
    const me = await authApi.me();
    setUser(me);
    return me;
  };

  const value = useMemo(
    () => ({
      user,
      loading,
      isAuthenticated: !!user,
      setUser, // 필요하면 외부에서 수동 갱신 가능
      login,
      logout,
      register,
      refreshMe: async () => {
        const me = await authApi.me();
        setUser(me);
        return me;
      },
    }),
    [user, loading]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within <AuthProvider>.");
  return ctx;
}
