import {createContext, useContext, useEffect, useState} from "react";
import {ProjectClient} from "../../project/api/ProjectClient.ts";
import {IAMClient} from "../../iam/api/IAMClient.ts";

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    const [isAuthenticated, setIsAuthenticated] = useState(() => {
        return localStorage.getItem("isAuthenticated") === "true";
    });

    useEffect(() => {
        localStorage.setItem("isAuthenticated", isAuthenticated.toString());
    }, [isAuthenticated]);

    useEffect(() => {
        ProjectClient.getInstance().setOnUnauthorized(logout);
        IAMClient.getInstance().setOnUnauthorized(logout);
    }, [])

    const login = () => {setIsAuthenticated(true)}
    const logout = () => {
        console.log('logout');
        setIsAuthenticated(false)
    }

    return (
        <AuthContext.Provider value={{ isAuthenticated, login, logout }}>
            {children}
        </AuthContext.Provider>
    )
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error("useAuth must be used within an AuthProvider");
    }
    return context;
}