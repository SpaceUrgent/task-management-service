import React from "react";
import {useNavigate, useParams} from "react-router-dom";

export default function TaskPreviewTable({taskPreviews = []}) {
    const { projectId } = useParams();
    const navigate = useNavigate();

    const viewTaskDetails = (taskId) => {
        navigate(`/projects/${projectId}/tasks/${taskId}`);
    }

    return (
        <div className="table-responsive mb-3 flex-grow-1 overflow-auto">
            <table className="table table-hover align-middle">
                <thead className="table-light">
                <tr>
                    <th scope="col">#</th>
                    <th scope="col">Created</th>
                    <th scope="col">Title</th>
                    <th scope="col">Priority</th>
                    <th scope="col">Status</th>
                    <th scope="col">Assignee</th>
                    <th scope="col"/>
                </tr>
                </thead>
                <tbody>
                {taskPreviews.map((task) => (
                    <tr key={task.id}>
                        <td>{task.number}</td>
                        <td>{new Date(task.createdAt).toLocaleDateString()}</td>
                        <td>{task.title}</td>
                        <td>{task.priority}</td>
                        <td>
                            <span className="badge bg-secondary">{task.status}</span>
                        </td>
                        <td>
                            {task.assignee ? task.assignee.fullName : "Unassigned"}
                        </td>
                        <td>
                            <button
                                type="button"
                                className="btn btn-sm btn-outline-primary"
                                onClick={() => viewTaskDetails(task.id)}
                            >
                                View
                            </button>
                        </td>
                    </tr>
                ))}
                {taskPreviews.length === 0 && (
                    <tr>
                        <td colSpan={5} className="text-muted text-center py-3">
                            No tasks found.
                        </td>
                    </tr>
                )}
                </tbody>
            </table>
        </div>
    )
}