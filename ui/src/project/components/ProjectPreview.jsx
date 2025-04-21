import React from "react";
import {Link} from "react-router-dom";

export default function ProjectPreview({ projectPreview }) {
    const ownerFullName = `${projectPreview.owner.firstName} ${projectPreview.owner.lastName}`;

    return (
        <li key={projectPreview.id} className="list-group-item">
            <div className="card col m-2">
                <div className="card-header">
                    <h3>{projectPreview.title}</h3>
                </div>
                <div className="card-body">
                    <div className="row">
                        <div className="col">
                            Owner: {ownerFullName}
                        </div>
                        <div className="col text-end">
                            <Link to={`/projects/${projectPreview.id}`} className="btn btn-sm btn-outline-secondary">
                                View Details
                            </Link>
                        </div>
                    </div>
                </div>
            </div>
        </li>
    )
}