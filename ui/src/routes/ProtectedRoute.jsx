import React from "react";
import {useAuth} from "../shared/contexts/AuthContext"
import {Navigate} from "react-router-dom";

export default function ProtectedRoute({ children }) {
    const { isAuthenticated } = useAuth();
    return isAuthenticated ? children : <Navigate to="/login" replace/>
}