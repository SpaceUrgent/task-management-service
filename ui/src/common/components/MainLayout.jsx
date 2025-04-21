import React from "react";
import {Outlet} from "react-router-dom";
import SideNavbar from "./SideNavbar";
import {useAuth} from "../contexts/AuthContext";

export default function MainLayout() {
    const {logout} = useAuth();
    return (
        <div className="container-fluid vh-100 vw-100">
            <div className="row vh-100">
                <SideNavbar onSignOut={logout} />
                <div className="col-md-9 flex-grow-1 overflow-auto pt-2 ps-2">
                    <Outlet/>
                </div>
            </div>
        </div>
    )
}