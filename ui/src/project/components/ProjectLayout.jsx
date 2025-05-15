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

    const title = () => {
        if (currentUserRole === "ADMIN" || currentUserRole === "OWNER") {
            return <EditableTitle initialValue={project.title} onSave={handleUpdateProjectTitle}/>
        }
        return <h4 className="m-0">{project?.title}</h4>;
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
            {title()}
            <ul className="nav nav-tabs mt-2">
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