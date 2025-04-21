import React, {useEffect, useState} from "react";
import {Link, Navigate, useParams} from "react-router-dom";
import {useProjectContext} from "../contexts/ProjectContext";
import {ProjectClient} from "../api/ProjectClient.ts";

export default function Task() {

    const projectClient = ProjectClient.getInstance();
    const { taskId} = useParams();
    const { project, members } = useProjectContext();

    const [task, setTask] = useState(null);
    const [ isLoading, setIsLoading ] = useState(false);

    const [ titleIsEditing, setTitleIsEditing ] = useState(false);
    const [ title, setTitle ] = useState();

    const [ descriptionIsEditing, setDescriptionIsEditing ] = useState(false);
    const [ description, setDescription ] = useState();

    const fetchTask = async () => {
        setIsLoading(true);
        try {
            const data = await projectClient.getTaskDetails(taskId);
            console.log('data', data);
            setTask(data);
            setDescription(data.description);
        } catch (error) {
            console.log(error);
        } finally {
            setIsLoading(false);
        }
    }

    useEffect(() => {
        fetchTask();
    }, [])

    const editTitle = () => {
        setTitle(task.title);
        setTitleIsEditing(true);
    }

    const dismissEditTitle = () => {
        setTitle(task.title);
        setTitleIsEditing(false);
    }

    const handleTitleChange = (e) => {
        e.preventDefault();
        setTitle(e.target.value);
    }

    const handleUpdateTitle = async (e) => {
        e.preventDefault();
        if (!title || task.title === title) {
            setTitleIsEditing(false);
            return;
        }
        await projectClient.updateTask(task.id, {
            title: title,
            description: task.description,
            assigneeId: task.assignee.id,
            status: task.status
        });
        setTitleIsEditing(false);
        fetchTask();
    }

    const editDescription = () => {
        setDescription(task.description);
        setDescriptionIsEditing(true);
    }

    const dismissEditDescription = () => {
        setDescriptionIsEditing(task.description);
        setDescriptionIsEditing(false);
    }

    const handleChangeDescription = (e) => {
        e.preventDefault();
        setDescription(e.target.value);
    }

    const handleUpdateDescription = async (e) => {
        e.preventDefault();
        if (task.description === description) {
            setDescriptionIsEditing(false);
            return;
        }
        await projectClient.updateTask(task.id, {
            title: task.title,
            description: description,
            assigneeId: task.assignee.id,
            status: task.status
        })
        setDescriptionIsEditing(false);
        fetchTask();
    }

    const handleChangeAssignee = async (e) => {
        e.preventDefault();
        const newAssigneeId = e.target.value;
        if (newAssigneeId === task.assignee.id) return;
        await projectClient.assignTask(task.id, newAssigneeId);
        fetchTask();
    }

    const handleChangeStatus = async (e) => {
        e.preventDefault();
        const newStatus = e.target.value;
        if (newStatus === task.status) return;
        await projectClient.updateTaskStatus(task.id, newStatus);
        fetchTask();
    }

    if (isLoading) {
        return (
            <div>
                Loading...
            </div>
        )
    }

    if (!task) {
        return (
            <span className="span-warning">Task not found</span>
        )
    }

    return (
        <div className="container-fluid mt-2">
            <div className="p-1">
                <div className="row align-items-start">
                    <div className="col-sm ms-3 mb-2">
                        <Link to={`/projects/${project.id}/tasks`} className="text-decoration-none">
                            <span className="text-primary">&larr; All tasks</span>
                        </Link>
                    </div>
                </div>
                <div className="row align-items-center">
                <div className="col d-flex align-items-center ms-3 me-3">
                        <strong className="me-2">#{task.id}</strong>
                        {titleIsEditing ? (
                            <>
                                <input
                                    type="text"
                                    className="form-control form-control-sm flex-grow-1 me-2"
                                    value={title}
                                    onChange={handleTitleChange}
                                    autoFocus
                                />
                                <div className="d-flex gap-2">
                                    <button
                                        className="btn btn-outline-secondary btn-sm"
                                        onClick={handleUpdateTitle}
                                    >
                                        Save
                                    </button>
                                    <button
                                        className="btn btn-outline-danger btn-sm"
                                        onClick={dismissEditTitle}
                                    >
                                        Cancel
                                    </button>
                                </div>
                            </>
                        ) : (
                            <>
                                <h4 className="flex-grow-1 m-0">{task.title}</h4>
                                <button
                                    className="btn btn-outline-secondary btn-sm ms-2"
                                    onClick={editTitle}
                                >
                                    <img
                                        src="/icons/pencil-square.svg"
                                        alt="Edit"
                                        width="18"
                                        height="18"
                                    />
                                </button>
                            </>
                        )}
                    </div>
                </div>
                <hr/>
                <div className="m-3">
                    <div className="row mt-3">
                        <div className="col-md-5">
                            <div className="mb-3">
                                <a>
                                    <strong>Created: </strong>
                                    {task.createdAt}
                                </a>
                            </div>
                        </div>
                        <div className="col-md-5">
                            {task.updatedAt &&
                                <div className="mb-3">
                                    <a>
                                        <strong>Updated:</strong>
                                        {task.updatedAt}
                                    </a>
                                </div>
                            }
                        </div>
                    </div>
                    <div className="row mt-3">
                        <div className="col-md-3">
                            <div className="input-group">
                                <label className="input-group-text" htmlFor="status">Status</label>
                                <select className="form-select" id="status" onChange={handleChangeStatus}>
                                    <option value={task.status}>{task.status}</option>
                                    {project?.taskStatuses.map((status) => (
                                        <option value={status} key={status}>{status}</option>
                                    ))}
                                </select>
                            </div>
                        </div>
                        <div className="col-md-3">
                            <div className="input-group">
                                <label className="input-group-text" htmlFor="assignee">Assignee</label>
                                <select className="form-select" id="assignee" onChange={handleChangeAssignee}>
                                    <option value={task.assignee.id}>{task.assignee.firstName} {task.assignee.firstName}</option>
                                    {members.map((member) => (
                                        <option value={member.id} key={member.id}>{member.firstName} {member.lastName}</option>
                                    ))}
                                </select>
                            </div>
                        </div>
                        <div className="col-md-3">
                            <div className="input-group">
                                <label className="input-group-text" htmlFor="owner">Owner</label>
                                <input className="form-control" value={`${task.owner.firstName} ${task.owner.lastName}`} disabled={true}/>
                            </div>
                        </div>
                    </div>
                    <div className="row mt-3">
                        <div className="d-flex justify-content-between align-items-center mb-2">
                            <label className="form-label m-0">Description</label>
                            {!descriptionIsEditing ? (
                                <button
                                    className="btn btn-outline-secondary btn-sm"
                                    onClick={editDescription}
                                >
                                    Modify
                                </button>
                            ) : (
                                <div className="d-flex gap-2">
                                    <button
                                        className="btn btn-outline-secondary btn-sm"
                                        onClick={handleUpdateDescription}
                                    >
                                        Save
                                    </button>
                                    <button
                                        className="btn btn-outline-danger btn-sm"
                                        onClick={dismissEditDescription}
                                    >
                                        Cancel
                                    </button>
                                </div>
                            )}
                        </div>

                        <textarea
                            className="form-control"
                            style={{ resize: "none" }}
                            value={description}
                            rows={8}
                            readOnly={!descriptionIsEditing}
                            onChange={handleChangeDescription}
                        />
                    </div>
                </div>
            </div>
        </div>
    );
}