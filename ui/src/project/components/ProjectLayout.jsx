import React from "react";
import {Outlet, useLocation, useNavigate, useParams} from "react-router-dom";
import {useProjectContext} from "../contexts/ProjectContext";

export default function ProjectLayout({}) {
    const { projectId } = useParams();
    const { project } = useProjectContext();
    const location = useLocation();
    const navigate = useNavigate();


    const subComponentUriPath = () => location.pathname.split("/")[3];

    const isActiveTab = (tabName) => {
        return location.pathname === `/projects/${projectId}/${tabName}`;
    }

    return (
        <div className="container-fluid h-100 d-flex flex-column">
            <h5>{project?.title}</h5>
            <ul className="nav nav-tabs mt-3">
                <li className="nav-item">
                    <button
                        className={`nav-link ${isActiveTab("overview") || !subComponentUriPath() ? "active" : ""}`}
                        onClick={() => navigate(`/projects/${projectId}/overview`)}
                    >
                        Overview
                    </button>
                </li>
                <li className="nav-item">
                    <button
                        className={`nav-link ${isActiveTab("tasks") ? "active" : ""}`}
                        onClick={() => navigate(`/projects/${projectId}/tasks`)}
                    >
                        Tasks
                    </button>
                </li>
                <li className="nav-item">
                    <button
                        className={`nav-link ${isActiveTab("members") ? "active" : ""}`}
                        onClick={() => navigate(`/projects/${projectId}/members`)}
                    >
                        Members
                    </button>
                </li>
            </ul>
            <Outlet/>
        </div>
    )
}