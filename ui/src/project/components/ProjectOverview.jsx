import React from "react";
import {useProjectContext} from "../contexts/ProjectContext";

export default function ProjectOverview() {
    const { project } = useProjectContext();

    return(
        <div className="container p-3">
            <div className="mb-3">
                <h5 className="ms-2 mb-2">Project Owner</h5>
                <hr/>
                <p className="ms-1"><strong>Name:</strong> {project.owner?.fullName}</p>
                <p className="ms-1"><strong>Email:</strong> {project.owner?.email}</p>
            </div>
            <div className="mb-3">
                <h5 className="ms-2 mb-2">Description</h5>
                <hr/>
                <div
                    className="flex-grow-1 bg-light text-black p-2 overflow-auto rounded"
                    style={{height: "300px"}}
                >
                    <p>{project?.description}</p>
                </div>
            </div>
        </div>
    )
}