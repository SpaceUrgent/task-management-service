import React, {useEffect, useState} from "react";
import {ProjectClient} from "../api/ProjectClient.ts";
import CreateTaskModal from "./modal/CreateTaskModal";
import TaskPreviewTable from "./TaskPreviewTable";
import {useProjectContext} from "../contexts/ProjectContext";
import PaginationPanel from "./PaginationPanel";
import LoadingSpinner from "../../common/components/LoadingSpinner";
import Alert from "../../common/components/Alert";
import LabeledSelector from "../../common/components/selectors/LabeledSelector";
import LabeledMultiValueSelector from "../../common/components/selectors/LabeledMultiValueSelector";
import {useSearchParams} from "react-router-dom";

export default function ProjectTasks() {
    const { project, members } = useProjectContext();

    const projectClient = ProjectClient.getInstance();

    const [searchParams, setSearchParams] = useSearchParams();

    const getParam = (key, defaultValue) => {
        const value = searchParams.get(key);
        return value !== null ? value : defaultValue;
    }

    const [ isLoading , setIsLoading ] = useState(false);
    const [ isError , setIsError ] = useState(false);
    const [pageSize, setPageSize] = useState(Number(getParam("size", 25)));
    const [currentPage, setCurrentPage] = useState(Number(getParam("page", 1)));
    const [sortBy, setSortBy] = useState(getParam("sortBy", "createdAt:DESC"));
    const [chosenAssigneeId, setChosenAssigneeId] = useState(getParam("assignee", ""));
    const [chosenStatuses, setChosenStatuses] = useState(
        getParam("statuses", "").split(",").filter(Boolean)
    );

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
                status: chosenStatuses,
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

    const updateParams = () => {
        const newParams = {
            size: pageSize,
            page: currentPage,
            sortBy,
            assignee: chosenAssigneeId,
            statuses: chosenStatuses.join(","),
        };
        setSearchParams(newParams);
    };

    useEffect(() => {
        fetchTaskPage();
        updateParams();
    }, [pageSize, currentPage, sortBy, chosenAssigneeId, chosenStatuses]);

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
                    <button className="btn btn-sm btn-primary" onClick={() => setShowCreateTaskModal(true)}>
                        Add Task
                    </button>
                </div>
                <div className="col-auto">
                    <LabeledSelector
                        label="Sort"
                        value={sortBy}
                        onChange={(value) => setSortBy(value)}
                        options={[
                            { value: "createdAt:DESC", label: "Sort by create time desc" },
                            { value: "createdAt:ASC", label: "Sort by create time asc" },
                            { value: "priority:DESC", label: "High priority first" },
                            { value: "priority:ASC", label: "Low priority first" },
                        ]}
                    />
                </div>
                <div className="col-auto">
                    <LabeledMultiValueSelector
                        label="Status"
                        values={project?.taskStatuses.map(s => s.name) || []}
                        selected={chosenStatuses}
                        onChange={(statuses) => setChosenStatuses(statuses)}
                    />
                </div>
                <div className="col-auto">
                    <LabeledSelector
                        label="Assignee"
                        value={chosenAssigneeId}
                        onChange={(value) => setChosenAssigneeId(value)}
                        options={[
                            { value: "", label: "All" },
                            ...project.members.map(member => ({
                                value: member.id,
                                label: member.fullName,
                            }))
                        ]}
                    />
                </div>
                <div className="col-auto">
                    <LabeledSelector
                        label="Size"
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