import React from 'react';

export default function TaskSummary({ summary }) {
    return (
        <div className="row g-3 mb-4">
            <div className="col-md-3 col-sm-6">
                <div className="card bg-primary shadow-sm">
                    <div className="card-body p-2">
                        <div className="d-flex justify-content-between align-items-center">
                            <span className="text-white-50">Total Tasks</span>
                            <span className="h4 mb-0 text-white">{summary.total}</span>
                        </div>
                    </div>
                </div>
            </div>
            <div className="col-md-3 col-sm-6">
                <div className="card bg-warning shadow-sm">
                    <div className="card-body p-2">
                        <div className="d-flex justify-content-between align-items-center">
                            <span className="text-white-50">In Progress</span>
                            <span className="h4 mb-0 text-white">{summary.open}</span>
                        </div>
                    </div>
                </div>
            </div>
            <div className="col-md-3 col-sm-6">
                <div className="card bg-danger shadow-sm">
                    <div className="card-body p-2">
                        <div className="d-flex justify-content-between align-items-center">
                            <span className="text-white-50">Overdue</span>
                            <span className="h4 mb-0 text-white">{summary.overdue}</span>
                        </div>
                    </div>
                </div>
            </div>
            <div className="col-md-3 col-sm-6">
                <div className="card bg-success shadow-sm">
                    <div className="card-body p-2">
                        <div className="d-flex justify-content-between align-items-center">
                            <span className="text-white-50">Completed</span>
                            <span className="h4 mb-0 text-white">{summary.closed}</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
} 