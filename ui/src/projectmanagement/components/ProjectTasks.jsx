import React, {useEffect, useState} from "react";
import {ProjectClient} from "../api/ProjectClient.ts";
import CreateTaskModal from "./modal/CreateTaskModal";
import TaskPreviewTable from "./TaskPreviewTable";
import {useProjectContext} from "../contexts/ProjectContext";

export default function ProjectTasks() {
    const { project, members } = useProjectContext();

    const projectClient = ProjectClient.getInstance();

    const [pageSize, setPageSize] = useState(25);
    const [currentPage, setCurrentPage] = useState(1);
    const [sortBy, setSortBy] = useState("createdAt:DESC");
    const [chosenAssigneeId, setChosenAssigneeId] = useState(null);
    const [chosenStatus, setChosenStatus] = useState(null);

    const [tasksPage, setTasksPage] = useState({});
    const [showCreateTaskModal, setShowCreateTaskModal] = useState(false);

    const fetchTaskPage = async () => {
        try {
            const options = {
                size: pageSize,
                page: currentPage,
                sortBy: sortBy,
                assigneeId: chosenAssigneeId,
                status: chosenStatus,
            }
            const data = await projectClient.getTaskPreviews(project?.id, options);
            setTasksPage(data);
        } catch (error) {
        }
    }

    const memberFullName = (member) => {
        if (!member) return "";
        return `${member.firstName} ${member.lastName}`;
    }

    useEffect(() => {
        fetchTaskPage();
    }, [])

    useEffect(() => {
        fetchTaskPage();
    }, [pageSize, currentPage, sortBy, chosenAssigneeId, chosenStatus]);

    return(
        <div className="d-flex flex-column h-100 container py-3">
            {showCreateTaskModal &&
                <CreateTaskModal
                    projectId={project.id}
                    members={members}
                    onClose={() => setShowCreateTaskModal(false)}
                />
            }
            <div className="row g-2 align-items-center mb-3">
                <div className="col-auto">
                    <button className="btn btn-primary" onClick={() => setShowCreateTaskModal(true)}>
                        Add Task
                    </button>
                </div>
                <div className="col-auto">
                    <select className="form-select"
                            onChange={(e) => setSortBy(e.target.value)}
                    >
                        <option value="createdAt:DESC" selected={"createdAt:DESC" === sortBy}>Sort by create time desc</option>
                        <option value="createdAt:ASC" selected={"createdAt:ASC" === sortBy}>Sort by created time asc</option>
                    </select>
                </div>
                <div className="col-auto">
                    <select className="form-select"
                            onChange={(e) => {
                                const value = e.target.value ? e.target.value : null;
                                setChosenStatus(value);
                            }}
                    >
                        <option value="">All</option>
                        {project?.taskStatuses?.map((status) => (
                            <option
                                key={status}
                                value={status}
                                selected={chosenStatus === status}
                            >{status}</option>
                        ))}
                    </select>
                </div>
                <div className="col-auto">
                    <select
                        className="form-select"
                        onChange={(e) => {
                            let assigneeId = e.target.value ? e.target.value : null;
                            console.log('change assigneeId ', assigneeId);
                            setChosenAssigneeId(assigneeId)
                        }}
                    >
                        <option value="" selected={!chosenAssigneeId}>All Assignees</option>
                        {members.map((member) => (
                            <option value={member.id} key={member.id} selected={member.id === chosenAssigneeId}>{memberFullName(member)}</option>
                        ))}
                    </select>
                </div>
                <div className="col-auto">
                    <select
                        className="form-select"
                        onChange={(e) => setPageSize(e.target.value)}
                    >
                        <option value={25} selected={pageSize === 25}>25</option>
                        <option value={50} selected={pageSize === 50}>50</option>
                        <option value={100} selected={pageSize === 100}>100</option>
                    </select>
                </div>
            </div>

            <TaskPreviewTable taskPreviews={tasksPage?.data} />

            <div className="d-flex justify-content-between align-items-center mt-auto pt-3 border-top">
                <button
                    className="btn btn-outline-secondary"
                    disabled={currentPage <= 1}
                    onClick={() => setCurrentPage(currentPage - 1)}
                >
                    Previous
                </button>
                <span>
                    Page {currentPage} of {tasksPage?.totalPages ? tasksPage.totalPages : 1}
                </span>
                <button
                    className="btn btn-outline-secondary"
                    disabled={tasksPage?.totalPages ? currentPage >= tasksPage.totalPages : true}
                    onClick={() => setCurrentPage(currentPage + 1)}
                >
                    Next
                </button>
            </div>
        </div>
    )
}