import React from "react";
import {useProjectContext} from "../contexts/ProjectContext";
import EditableDescription from "../EditableDescription";
import {ProjectClient} from "../api/ProjectClient.ts";
import EditableTaskStatuses from "./EditableTaskStatuses";
import ProjectOwner from "./ProjectOwner";

export default function ProjectProfile() {
    const projectClient  = ProjectClient.getInstance();
    const { project, currentUserRole, refreshData } = useProjectContext();

    const handleUpdateDescription = async (value) => {
        if (!value || value === project.description) return;
        await projectClient.updateProjectInfo(project.id, {
            title: project.title,
            description: value
        });
        refreshData();
    }

    const description = () => {
        if (currentUserRole === "Admin" || currentUserRole === "Owner") {
            return <EditableDescription initialValue={project?.description} onSave={handleUpdateDescription}/>
        }

        return(
            <div className="mb-3">
                <h6>Description</h6>
                <hr/>
                <div
                    className="flex-grow-1 text-black p-2 overflow-auto rounded shadow-sm"
                    style={{height: "300px"}}
                >
                    <p>{project?.description}</p>
                </div>
            </div>
        );
    }

    return(
        <div className="container p-3">
            <div className="col">
                <div className="row">
                    <ProjectOwner/>
                </div>
                <div className="row">
                    <EditableTaskStatuses/>
                </div>
                <div className="row">
                    {description()}
                </div>
            </div>
        </div>
    )
}