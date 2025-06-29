import React, {useEffect, useState} from "react";
import {Link, useParams, useSearchParams} from "react-router-dom";
import {useProjectContext} from "../contexts/ProjectContext";
import {ProjectClient} from "../api/ProjectClient.ts";
import LoadingSpinner from "../../shared/components/LoadingSpinner";
import Alert from "../../shared/components/Alert";
import LabeledSelector from "../../shared/components/selectors/LabeledSelector";
import EditableTitle from "../shared/EditableTitle";
import EditableDescription from "../shared/EditableDescription";
import DateSelector from "../../shared/components/selectors/DateSelector";
import TaskChangeLogs from "./TaskChangeLogs";
import TaskComments from "./TaskComments";
import {formatDateTime} from "../../shared/Time";
import LabeledValue from "../../shared/components/LabeledValue";

export default function Task() {
    const projectClient = ProjectClient.getInstance();
    const { taskId} = useParams();
    const { project } = useProjectContext();

    const [searchParams, setSearchParams] = useSearchParams();

    const [task, setTask] = useState(null);
    const [ isLoading, setIsLoading ] = useState(false);
    const [ commentsExpanded, setCommentsExpanded ] = useState(searchParams.get("commentsExpanded") === "true");
    const [ changeLogsExpanded, setChangeLogsExpanded ] = useState(searchParams.get("changeLogsExpanded") === "true");

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

    useEffect(() => {
        const newParams = {
            commentsExpanded: commentsExpanded,
            changeLogsExpanded: changeLogsExpanded,
        }
        setSearchParams(newParams)
    }, [commentsExpanded, changeLogsExpanded])

    const handleUpdateTitle = async (value) => {
        if (!value || value === task.title) return;
        await projectClient.updateTask(task.id, {
            title: value,
            description: task.description,
            assigneeId: task.assignee.id,
            dueDate: task.dueDate,
            priority: task.priority,
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
            dueDate: task.dueDate,
            priority: task.priority,
            status: task.status
        })
        fetchTask();
    }

    const handleChangePriority = async (value) => {
        if (!value || value === task.priority) return;
        await projectClient.updateTask(task.id, {
            title: task.title,
            description: task.description,
            assigneeId: task.assignee?.id,
            dueDate: task.dueDate,
            priority: value,
            status: task.status
        })
        fetchTask();
    }

    const handleChangeAssignee = async (value) => {
        value = value === "Unassigned" ? null : value;
        if (value === task.assignee?.id) return;
        await projectClient.assignTask(task.id, value);
        fetchTask();
    }

    const handleChangeStatus = async (value) => {
        if (value === task.status) return;
        await projectClient.updateTaskStatus(task.id, value);
        fetchTask();
    }

    const handleDueDateChange = async (value) => {
        if (value === task.dueDate) return;
        if (value && new Date(value) < new Date()) return;
        await projectClient.updateTask(task.id, {
            title: task.title,
            description: task.description,
            assigneeId: task.assignee?.id,
            dueDate: value,
            priority: task.priority,
            status: task.status
        })
        fetchTask();
    }

    const handleAddComment = async (value) => {
        if (value === task.comment) return;
        await projectClient.addTaskComment(task.id, value);
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
        <div className="container-fluid mt-0">
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
                        <h5 className="flex-grow-1 m-0 pe-2">#{task.number} </h5>
                        <EditableTitle
                            initialValue={task.title}
                            onSave={handleUpdateTitle}
                            editable={true}
                        />
                    </div>
                    <hr/>
                </div>
                <div className="container m-0 p-0">
                    <div className="row mt-3">
                        <div className="col-md-3">
                            <LabeledValue
                                label="Created"
                                value={formatDateTime(task.createdAt)}
                            />
                        </div>
                        <div className="col-md-3">
                            <LabeledValue
                                label="Updated"
                                value={formatDateTime(task.updatedAt)}
                            />
                        </div>
                        <div className="col-md-3">
                            <LabeledValue
                                label="Owner"
                                value={task.owner.fullName}
                            />
                        </div>
                    </div>
                    <div className="row mt-3">
                        <div className="col-md-3">
                            <DateSelector
                                onChange={handleDueDateChange}
                                initialValue={task.dueDate}
                            />
                        </div>
                        <div className="col-md-3">
                            <LabeledSelector
                                label="Priority"
                                value={task.priority}
                                onChange={handleChangePriority}
                                options={project?.taskPriorities.map(priority => ({
                                    value: priority.name, label: priority.name
                                }))}
                            />
                        </div>
                        <div className="col-md-3">
                            <LabeledSelector
                                label="Status"
                                value={task.status}
                                onChange={handleChangeStatus}
                                options={project?.taskStatuses.map(status => ({
                                    value: status.name, label: status.name
                                }))}
                            />
                        </div>
                        <div className="col-md-3">
                            <LabeledSelector
                                label="Assignee"
                                value={task.assignee?.id}
                                onChange={handleChangeAssignee}
                                options={[
                                    { value: null, label: 'Unassigned' },
                                    ...(project?.members.map((member) => (
                                        { value: member.id, label: member.fullName}
                                    )))
                                ]}
                            />
                        </div>
                    </div>
                    <EditableDescription
                        initialValue={task.description}
                        onSave={handleUpdateDescription}
                        allowEdit={true}
                    />
                    <div className="row mt-4">
                        <div className="col p-0">
                            <TaskComments
                                isExpanded={commentsExpanded}
                                onToggleExpand={() => setCommentsExpanded(!commentsExpanded)}
                                comments={task.comments}
                                onAddComment={handleAddComment}
                            />
                        </div>
                    </div>
                    <div className="row mt-2">
                        <div className="col p-0">
                            <TaskChangeLogs
                                isExpanded={changeLogsExpanded}
                                onToggleExpand={() => setChangeLogsExpanded(!changeLogsExpanded)}
                                changeLogs={task?.changeLogs}
                            />
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}