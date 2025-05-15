import React, {useState} from "react";
import {useProjectContext} from "../contexts/ProjectContext";
import EditableDescription from "../EditableDescription";
import {ProjectClient} from "../api/ProjectClient.ts";
import EditableTaskStatuses from "./EditableTaskStatuses";

export default function ProjectOverview() {
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
        if (currentUserRole === "ADMIN" || currentUserRole === "OWNER") {
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

    const [taskStatusIsModifying, setTaskStatusIsModifying] = useState(false);

    const modifyButton = () => {
        if (currentUserRole === "ADMIN" || currentUserRole === "OWNER") {
            return (
                <button
                    className="btn btn-outline-secondary btn-sm"
                    onClick={() => setTaskStatusIsModifying(true)}
                >
                    Modify
                </button>
            )
        }
        return <></>;
    }

    const taskStatuses = () => {
        return(
            <ul className="p-0 gap-1 list-group-horizontal d-flex flex-wrap">
                {project.taskStatuses.map((status, index) => (
                    <li className="list-group-item" key={status.name}>
                        <span className="badge bg-primary">
                            {status.name}
                        </span>
                        {index !== project.taskStatuses.length - 1 && (
                            <span className="text-gray-500"> →</span>
                        )}
                    </li>
                ))}
            </ul>
        );
    }

    return(
        <div className="container p-3">
            <div className="col">
                <div className="row">
                    <div className="mb-3">
                        <h6>Project Owner</h6>
                        <hr/>
                        <p>Name: {project.owner?.fullName}</p>
                        <p>Email: {project.owner?.email}</p>
                    </div>
                </div>
                <div className="row">
                    {/*<div className="row">*/}
                    {/*    <div className="d-flex justify-content-between align-items-center mb-2">*/}
                    {/*        <h6 className="form-label m-0">Task statuses</h6>*/}
                    {/*        {modifyButton()}*/}
                    {/*    </div>*/}
                    {/*</div>*/}
                    {/*<hr/>*/}
                    {/*{taskStatuses()}*/}
                    <EditableTaskStatuses/>
                </div>
                <div className="row">
                    {description()}
                </div>
            </div>
        </div>
    )
}