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

    return(
        <div className="container p-3">
            <div className="col">
                <div className="row mb-4">
                    <ProjectOwner allowChangeOwner={currentUserRole === "Owner"}/>
                </div>
                <div className="row mb-4">
                    <EditableTaskStatuses/>
                </div>
                <div className="row">
                    <EditableDescription
                        initialValue={project?.description}
                        onSave={handleUpdateDescription}
                        allowEdit={currentUserRole === "Admin" || currentUserRole === "Owner"}
                    />
                </div>
            </div>
        </div>
    )
}