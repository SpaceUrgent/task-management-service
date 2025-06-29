import React from "react";
import {Link, useLocation} from "react-router-dom";

export default function SideNavbar({ onSignOut }) {
    const location = useLocation();

    function getFirstUriPathPart() {
        return location.pathname.split("/")[1];
    }

    const isActive = (path) => getFirstUriPathPart() === path;

    return (
        <nav className="bg-dark text-white d-flex flex-column flex-shrink-0" style={{ width: '280px', height: '100vh', position: 'sticky', top: 0 }}>
            <div className="d-flex flex-column h-100">
                {/* Header */}
                <div className="text-center mt-2 mb-1">
                    <span className="navbar-brand fs-4">Agile Application</span>
                </div>
                <hr className="my-1"/>
                
                {/* Navigation Links */}
                <div className="flex-grow-1 p-3">
                    <ul className="nav nav-pills flex-column">
                        <li className="nav-item mb-2">
                            <Link
                                className={`nav-link ${isActive("projects") || !getFirstUriPathPart() ? "active" : "text-light"}`}
                                to="/projects"
                            >
                                Projects
                            </Link>
                        </li>
                        <li className="nav-item mb-2">
                            <Link
                                className={`nav-link ${isActive("dashboard") ? "active" : "text-light"}`}
                                to="/dashboard"
                            >
                                Dashboard
                            </Link>
                        </li>
                        <li className="nav-item mb-2">
                            <Link
                                className={`nav-link ${isActive("profile") ? "active" : "text-light"}`}
                                to="/profile"
                            >
                                Profile
                            </Link>
                        </li>
                    </ul>
                </div>

                {/* Sign Out Button */}
                <div className="p-3 mt-auto">
                    <button 
                        type="button" 
                        className="btn btn-outline-danger w-100"
                        onClick={onSignOut}
                    >
                        Sign Out
                    </button>
                </div>
            </div>
        </nav>
    )
}