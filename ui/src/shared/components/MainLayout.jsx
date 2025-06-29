import React from "react";
import {Outlet} from "react-router-dom";
import SideNavbar from "./SideNavbar";
import {useAuth} from "../contexts/AuthContext";

export default function MainLayout() {
    const {logout} = useAuth();
    return (
        <div className="d-flex vh-100">
            <SideNavbar onSignOut={logout} />
            <div className="flex-grow-1 overflow-auto p-3">
                <Outlet/>
            </div>
        </div>
    )
}