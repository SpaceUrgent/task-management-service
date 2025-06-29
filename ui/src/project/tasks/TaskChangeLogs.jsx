import React from "react";
import {formatDateTime} from "../../shared/Time";

export default function TaskChangeLogs({
    isExpanded = true, onToggleExpand, changeLogs = []
}) {

    return (
        <div className="accordion" id="changeLogsAccordion">
            <div className="accordion-item">
                <h2 className="accordion-header" id="headingLogs">
                    <button
                        className={`accordion-button ${isExpanded ? '' : 'collapsed'}`}
                        type="button"
                        onClick={onToggleExpand}
                        data-bs-toggle="collapse"
                        aria-expanded={isExpanded}
                        aria-controls="collapseLogs"
                    >
                        Task Change Logs
                    </button>
                </h2>
                <div
                    id="collapseLogs"
                    className={`accordion-collapse collapse ${isExpanded ? "show" : ""}`}
                    aria-labelledby="headingLogs"
                >
                    <div className="accordion-body pt-2 pb-2">
                        <div className="row">
                            <div className="col p-0">
                                {changeLogs.length === 0 ? (
                                    <p className="text-muted">No changes yet.</p>
                                ) : (
                                    <ul className="list-group">
                                        {changeLogs.map((log, index) => (
                                            <li key={index} className="list-group-item">
                                                <div className="text-muted">{formatDateTime(log.occurredAt)}</div>
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
            </div>
        </div>
    );
}