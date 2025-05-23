import React, {useEffect, useState} from "react";
import {ProjectClient} from "../api/ProjectClient.ts";
import CreateTaskModal from "./modal/CreateTaskModal";
import TaskPreviewTable from "./TaskPreviewTable";
import {useProjectContext} from "../contexts/ProjectContext";
import PaginationPanel from "./PaginationPanel";
import Selector from "../../common/components/selectors/Selector";
import LoadingSpinner from "../../common/components/LoadingSpinner";
import Alert from "../../common/components/Alert";

export default function ProjectTasks() {
    const { project, members } = useProjectContext();

    const projectClient = ProjectClient.getInstance();

    const [ isLoading , setIsLoading ] = useState(false);
    const [ isError , setIsError ] = useState(false);
    const [pageSize, setPageSize] = useState(25);
    const [currentPage, setCurrentPage] = useState(1);
    const [sortBy, setSortBy] = useState("createdAt:DESC");
    const [chosenAssigneeId, setChosenAssigneeId] = useState(null);
    const [chosenStatus, setChosenStatus] = useState(null);

    const [tasksPage, setTasksPage] = useState({});
    const [showCreateTaskModal, setShowCreateTaskModal] = useState(false);

    const fetchTaskPage = async () => {
        setIsLoading(true);
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
            setIsError(false);
        } catch (error) {
            setIsError(true);
        } finally {
            setIsLoading(false);
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

    const handleSubmitCreateTask = () => {
        setShowCreateTaskModal(false);
        fetchTaskPage();
    }

    if (isLoading) {
        return <LoadingSpinner/>
    }

    if (isError) {
        return <Alert error="Failed to receive tasks"/>
    }

    return(
        <div className="d-flex flex-column h-100 container py-3">
            {showCreateTaskModal &&
                <CreateTaskModal
                    projectId={project.id}
                    members={members}
                    onClose={() => setShowCreateTaskModal(false)}
                    onSubmit={handleSubmitCreateTask}
                />
            }
            <div className="row g-2 align-items-center mb-3">
                <div className="col-auto">
                    <button className="btn btn-primary" onClick={() => setShowCreateTaskModal(true)}>
                        Add Task
                    </button>
                </div>
                <div className="col-auto">
                    <Selector
                        value={sortBy}
                        onChange={(value) => setSortBy(value)}
                        options={[
                            { value: "createdAt:DESC", label: "Sort by create time desc" },
                            { value: "createdAt:ASC", label: "Sort by create time asc" },
                        ]}
                    />
                </div>
                <div className="col-auto">
                    <Selector
                        value={chosenStatus}
                        onChange={(value) => setChosenStatus(value)}
                        options={[
                            { value: "", label: "All" },
                            ...project?.taskStatuses.map(status => ({
                                value: status, label: status
                            }))
                        ]}
                    />
                </div>
                <div className="col-auto">
                    <Selector
                        value={chosenAssigneeId}
                        onChange={(value) => setChosenAssigneeId(value)}
                        options={[
                            { value: "", label: "All" },
                            ...members.map(member => ({
                                value: member.id,
                                label: member.firstName + " " + member.lastName,
                            }))
                        ]}
                    />
                </div>
                <div className="col-auto">
                    <Selector
                        value={pageSize}
                        onChange={(value) => setPageSize(value)}
                        options={[
                            { value : 25 }, { value : 50 }, { value : 100 }
                        ]}
                    />
                </div>
            </div>

            <TaskPreviewTable taskPreviews={tasksPage?.data} />

            <PaginationPanel
                currentPage={currentPage}
                totalPages={tasksPage?.totalPages}
                onNext={() => setCurrentPage(currentPage + 1)}
                onPrevious={() => setCurrentPage(currentPage - 1)}
            />
        </div>
    )
}