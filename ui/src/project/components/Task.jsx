import React, {useEffect, useState} from "react";
import {Link, useParams} from "react-router-dom";
import {useProjectContext} from "../contexts/ProjectContext";
import {ProjectClient} from "../api/ProjectClient.ts";
import LoadingSpinner from "../../common/components/LoadingSpinner";
import Alert from "../../common/components/Alert";
import LabeledSelector from "../../common/components/selectors/LabeledSelector";
import EditableTitle from "./EditableTitle";
import EditableDescription from "../EditableDescription";

export default function Task() {

    const projectClient = ProjectClient.getInstance();
    const { taskId} = useParams();
    const { project } = useProjectContext();

    const [task, setTask] = useState(null);
    const [ isLoading, setIsLoading ] = useState(false);

    const fetchTask = async () => {
        setIsLoading(true);
        try {
            const data = await projectClient.getTaskDetails(taskId);
            console.log('data', data);
            setTask(data);
        } catch (error) {
            console.log(error);
        } finally {
            setIsLoading(false);
        }
    }

    useEffect(() => {
        fetchTask();
    }, [])

    const handleUpdateTitle = async (value) => {
        if (!value || value === task.title) return;
        await projectClient.updateTask(task.id, {
            title: value,
            description: task.description,
            assigneeId: task.assignee.id,
            status: task.status
        });
        fetchTask();
    }

    const handleUpdateDescription = async (value) => {
        if (task.description === value) {
            return;
        }
        await projectClient.updateTask(task.id, {
            title: task.title,
            description: value,
            assigneeId: task.assignee.id,
            status: task.status
        })
        fetchTask();
    }

    const handleChangeAssignee = async (value) => {
        if (value === task.assignee.id) return;
        console.log("assignee", value);
        await projectClient.assignTask(task.id, value);
        fetchTask();
    }

    const handleChangeStatus = async (value) => {
        if (value === task.status) return;
        await projectClient.updateTaskStatus(task.id, value);
        fetchTask();
    }

    if (isLoading) {
        return (
            <LoadingSpinner/>
        )
    }

    if (!task) {
        return (
            <Alert error="Task not found"/>
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
                        <h4 className="flex-grow-1 m-0 pe-2">#{task.number} </h4>
                        <EditableTitle
                            initialValue={task.title}
                            onSave={handleUpdateTitle}
                        />
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
                            <LabeledSelector
                                label="Status"
                                value={task.status}
                                onChange={handleChangeStatus}
                                options={project?.taskStatuses.map(status => ({
                                    value: status, label: status
                                }))}
                            />
                        </div>
                        <div className="col-md-3">
                            <LabeledSelector
                                label="Assignee"
                                value={task.assignee.id}
                                onChange={handleChangeAssignee}
                                options={project?.members.map((member) => ({
                                    value: member.id,
                                    label: member.fullName,
                                }))}
                            />
                        </div>
                        <div className="col-md-3">
                            <div className="input-group">
                                <label className="input-group-text" htmlFor="owner">Owner</label>
                                <input className="form-control" value={task.owner?.fullName} disabled={true}/>
                            </div>
                        </div>
                    </div>
                    <EditableDescription
                        initialValue={task.description}
                        onSave={handleUpdateDescription}
                    />
                </div>
            </div>
        </div>
    );
}