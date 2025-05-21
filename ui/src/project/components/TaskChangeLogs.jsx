import React from "react";

export default function TaskChangeLogs({ task }) {
    const changeLogs = task?.changeLogs || [];

    return (
        <div className="accordion mt-4" id="changeLogsAccordion">
            <div className="accordion-item">
                <h2 className="accordion-header" id="headingLogs">
                    <button
                        className="accordion-button collapsed"
                        type="button"
                        data-bs-toggle="collapse"
                        data-bs-target="#collapseLogs"
                        aria-expanded="false"
                        aria-controls="collapseLogs"
                    >
                        Task Change Logs
                    </button>
                </h2>
                <div
                    id="collapseLogs"
                    className="accordion-collapse collapse"
                    aria-labelledby="headingLogs"
                    data-bs-parent="#changeLogsAccordion"
                >
                    <div className="accordion-body">
                        {changeLogs.length === 0 ? (
                            <p className="text-muted">No changes yet.</p>
                        ) : (
                            <ul className="list-group">
                                {changeLogs.map((log, index) => (
                                    <li key={index} className="list-group-item">
                                        <div className="text-muted">{log.occurredAt}</div>
                                        <div><strong>{log.logMessage}</strong></div>
                                        <div>
                                            <span className="badge bg-secondary me-2">Old: {log.oldValue}</span>
                                            <span className="badge bg-primary">New: {log.newValue}</span>
                                        </div>
                                    </li>
                                ))}
                            </ul>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}