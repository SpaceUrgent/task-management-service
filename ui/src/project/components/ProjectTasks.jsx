import React, {useEffect, useState} from "react";
import {ProjectClient} from "../api/ProjectClient.ts";
import CreateTaskModal from "./modal/CreateTaskModal";
import TaskPreviewTable from "./TaskPreviewTable";
import {useProjectContext} from "../contexts/ProjectContext";
import PaginationPanel from "./PaginationPanel";
import Selector from "../../common/components/selectors/Selector";
import LoadingSpinner from "../../common/components/LoadingSpinner";
import Alert from "../../common/components/Alert";
import MultiStatusSelector from "./selector/MultiStatusSelector";

export default function ProjectTasks() {
    const { project, members } = useProjectContext();

    const projectClient = ProjectClient.getInstance();

    const [ isLoading , setIsLoading ] = useState(false);
    const [ isError , setIsError ] = useState(false);
    const [pageSize, setPageSize] = useState(25);
    const [currentPage, setCurrentPage] = useState(1);
    const [sortBy, setSortBy] = useState("createdAt:DESC");
    const [chosenAssigneeId, setChosenAssigneeId] = useState(null);
    const [chosenStatuses, setChosenStatuses] = useState([]);

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

    useEffect(() => {
        fetchTaskPage();
    }, [])

    useEffect(() => {
        if (project?.taskStatuses) {
            setChosenStatuses(project.taskStatuses.map(status => status.name));
        }
    }, [project?.taskStatuses]);

    useEffect(() => {
        fetchTaskPage();
        console.log('fetch task page');
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
                            { value: "priority:DESC", label: "High priority first" },
                            { value: "priority:ASC", label: "Low priority first" },
                            // { value: "dueDate:DESC", label: "Due date desc" },
                            // { value: "dueDate:ASC", label: "Due date asc" },
                        ]}
                    />
                </div>
                <div className="col-auto">
                    {/*<Selector*/}
                    {/*    value={chosenStatuses}*/}
                    {/*    onChange={(value) => setChosenStatuses(value)}*/}
                    {/*    options={[*/}
                    {/*        { value: "", label: "All" },*/}
                    {/*        ...project?.taskStatuses*/}
                    {/*            .sort((a, b) => a.position - b.position)*/}
                    {/*            .map(status => ({*/}
                    {/*                value: status.name, label: status.name*/}
                    {/*            }))*/}
                    {/*    ]}*/}
                    {/*/>*/}
                        <MultiStatusSelector
                            statuses={project?.taskStatuses.map(s => s.name) || []}
                            selected={chosenStatuses}
                            onChange={(statuses) =>
                            {
                                console.log('selected: ', statuses);
                                setChosenStatuses(statuses)
                            }}
                        />
                </div>
                <div className="col-auto">
                    <Selector
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