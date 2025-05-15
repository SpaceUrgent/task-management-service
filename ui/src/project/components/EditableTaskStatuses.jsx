import React, {useState} from "react";
import {useProjectContext} from "../contexts/ProjectContext";
import AddTaskStatusModal from "./modal/AddTaskStatusModal";
import {ProjectClient} from "../api/ProjectClient.ts";

export default function EditableTaskStatuses() {
    const {project, currentUserRole, refreshData} = useProjectContext();
    const projectClient = ProjectClient.getInstance();

    const [isEditing, setIsEditing] = useState(false);
    const [openAddStatusModal, setOpenAddStatusModal] = useState(false);
    const [newStatusPosition, setNewStatusPosition] = useState(null);

    const [deleteError, setDeleteError] = useState(null);

    const statusBadge = (status, index) => {
        return(
            <li className="list-group-item" key={status.name}>
                <span className="badge bg-primary">
                    {status.name}
                </span>
                {index !== project.taskStatuses.length - 1 && (
                    <span className="text-gray-500"> →</span>
                )}
            </li>
        )
    }

    const handleAddStatus = (position) => {
        setNewStatusPosition(position);
        setOpenAddStatusModal(true);
    }

    const handleDeleteStatus = async (statusName) => {
        try {
            await projectClient.removeTaskStatus(project.id, statusName);
            refreshData();
        } catch (error) {
            setDeleteError(error.message ? error.message : "Failed to delete status");
            setTimeout(() => setDeleteError(null), 5000); 
        }
    }

    const plusButton = (position) => {
        return(
            <button
                type="button"
                className="btn btn-outline-secondary align-items-center justify-content-center p-0"
                style={{
                    width: "1.75rem",
                    height: "1.75rem",
                    borderRadius: "50%",
                    fontSize: "1rem",
                    lineHeight: "1"
                }}
                onClick={() => handleAddStatus(position)}
            >
                +
            </button>
        )
    }

    const editingStatusBadge = (status, index) => {
        return(
            <li className="list-group-item" key={status.name}>
                {index === 0 && plusButton(1)}
                <div
                    className="d-inline-flex align-items-center rounded px-2 py-0 me-2 ms-2 mb-2"
                    style={{
                        backgroundColor: "#0d6efd",
                        color: "white",
                        fontSize: "0.75rem",
                        height: "1.75rem"
                    }}
                >
                    <span className="me-1"><strong>{status.name}</strong></span>
                    <button
                        type="button"
                        className="btn btn-sm btn-close btn-close-white p-0 m-0"
                        aria-label="Remove"
                        onClick={() => handleDeleteStatus(status.name)}
                        style={{
                            width: "1rem",
                            height: "1rem"
                        }}
                    />
                </div>
                {plusButton(index + 2)}
            </li>
        )
    }

    const editingButton = () => {
        if (isEditing) {
            return(
                <button
                    className="btn btn-outline-secondary btn-sm"
                    onClick={() => setIsEditing(false)}
                >
                    Finish
                </button>
            )
        } else {
            return (
                <button
                    className="btn btn-outline-secondary btn-sm"
                    onClick={() => setIsEditing(true)}
                >
                    Edit
                </button>
            )
        }
    }

    return(
        <div className="row">
            {openAddStatusModal &&
                <AddTaskStatusModal
                    statusPosition={newStatusPosition}
                    onClose={() => setOpenAddStatusModal(false)}
                />
            }
            <div className="d-flex justify-content-between align-items-center mb-2">
                <h6 className="form-label m-0">Task statuses</h6>
                {currentUserRole === "ADMIN" || currentUserRole === "OWNER" &&
                    editingButton()
                }
            </div>
            <hr/>
            <ul className="p-0 gap-1 list-group-horizontal d-flex flex-wrap">
                {project.taskStatuses.map((status, index) => (
                    isEditing ? editingStatusBadge(status, index) : statusBadge(status, index)
                ))}
            </ul>
            {deleteError && (
                <div className="alert alert-danger alert-dismissible fade show" role="alert">
                    {deleteError}
                    <button type="button" className="btn-close" onClick={() => setDeleteError(null)} aria-label="Close"></button>
                </div>
            )}
        </div>
    )
}