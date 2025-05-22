import React from "react";
import {Outlet, useLocation, useNavigate, useParams} from "react-router-dom";
import {useProjectContext} from "../contexts/ProjectContext";
import EditableTitle from "./EditableTitle";
import {ProjectClient} from "../api/ProjectClient.ts";

export default function ProjectLayout({}) {
    const { projectId } = useParams();
    const { project, currentUserRole, refreshData } = useProjectContext();
    const location = useLocation();
    const navigate = useNavigate();
    const projectClient = ProjectClient.getInstance();

    const subComponentUriPath = () => location.pathname.split("/")[3];

    const isActiveTab = (tabName) => {
        return location.pathname === `/projects/${projectId}/${tabName}`;
    }

    const handleUpdateProjectTitle = async (value) => {
        if (!value || project.title === value) return;
        await projectClient.updateProjectInfo(projectId, {
            title: value,
            description: project.description,
        });
        refreshData();
    }

    return (
        <div className="container-fluid h-100 d-flex flex-column">
            <EditableTitle
                initialValue={project.title}
                onSave={handleUpdateProjectTitle}
                editable={currentUserRole === "Owner"}
            />
            <ul className="nav nav-tabs mt-1">
                <li className="nav-item">
                    <button
                        className={`nav-link ${isActiveTab("profile") || !subComponentUriPath() ? "active" : ""}`}
                        onClick={() => navigate(`/projects/${projectId}/profile`)}
                    >
                        Profile
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