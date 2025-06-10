import React, { useState } from 'react';
import {formatDate} from "../../common/Time";
import {Link} from "react-router-dom";

export default function TaskList({ 
    title,
    tasks,
    pageSize,
    onLoadMore,
    isCollapsible = true,
    initiallyExpanded = true
}) {
    const [isExpanded, setIsExpanded] = useState(initiallyExpanded);
    const [displayCount, setDisplayCount] = useState(pageSize);

    const handleLoadMore = () => {
        setDisplayCount(prev => prev + pageSize);
        onLoadMore(displayCount / pageSize + 1);
    };

    const handleCollapse = () => {
        setIsExpanded(!isExpanded);
        if (!isExpanded) {
            setDisplayCount(pageSize); // Reset to initial state when expanding
        }
    };

    if (!isExpanded) {
        return (
            <div className="card mb-3">
                <div className="card-header d-flex justify-content-between align-items-center">
                    <h7 className="mb-0">{title}</h7>
                    <button 
                        className="btn btn-link"
                        onClick={handleCollapse}
                    >
                        Show
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="card mb-3">
            <div className="card-header d-flex justify-content-between align-items-center">
                <h7 className="mb-0">{title}</h7>
                {isCollapsible && (
                    <button 
                        className="btn btn-link"
                        onClick={handleCollapse}
                    >
                        Hide
                    </button>
                )}
            </div>
            <div className="card-body">
                <div className="list-group">
                    {tasks.slice(0, displayCount).map(task => (
                        <div key={task.id} className="list-group-item">
                            <div className="d-flex w-100 justify-content-sm-between align-items-center mb-1">
                                <h6 className="mb-1">#{task.number} - {task.title}</h6>
                                <button className="btn btn-link btn-sm me-1">
                                    <Link to={`/projects/${task.projectId}/tasks/${task.taskId}`}>
                                        View details
                                    </Link>
                                </button>
                            </div>
                            <div className="row p-0 mb-0">
                                <div className="col-4">
                                    <small className="text-muted">
                                        {task.projectTitle}
                                    </small>
                                </div>
                                <div className="col">
                                    <small className="text-muted">
                                        Created: {formatDate(task.createdAt)}
                                    </small>
                                </div>
                                <div className="col">
                                    {task.dueDate &&
                                        <small className={task.isOverdue ? 'text-danger' : 'text-muted'}>
                                            Due: {task.dueDate}
                                        </small>
                                    }
                                </div>
                                <div className="col">
                                    <small className="text-muted">
                                        Status: {task.status}
                                    </small>
                                </div>
                                <div className="col">
                                    <small className="text-muted">
                                        Priority: {task.priority}
                                    </small>
                                </div>
                                <div className="col-2">
                                    <small className="text-muted">
                                        Assignee: {task.assignee.fullName}
                                    </small>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
                {tasks.length > displayCount && (
                    <div className="d-grid">
                        <button
                            className="btn btn-outline-secondary btn-sm mt-3"
                            onClick={handleLoadMore}
                        >
                            Load More
                        </button>
                    </div>
                )}
            </div>
        </div>
    );
} 