import React from "react";
import {useAuth} from "../shared/contexts/AuthContext";
import {Navigate} from "react-router-dom";

export default function PublicRoute({ children }) {
    const { isAuthenticated } = useAuth();
    return !isAuthenticated ? children : <Navigate to="/projects" replace/>
}