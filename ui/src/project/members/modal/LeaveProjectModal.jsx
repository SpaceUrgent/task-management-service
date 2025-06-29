import React, { useState } from "react";

export default function LeaveProjectModal({ onClose, onSubmit }) {
    const [isConfirmed, setIsConfirmed] = useState(false);
    const [isLoading, setIsLoading] = useState(false);
    const [submitError, setSubmitError] = useState("");

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!isConfirmed) return;
        setIsLoading(true);
        setSubmitError("");
        try {
            await onSubmit();
        } catch (error) {
            setSubmitError("Failed to leave project");
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="modal show d-block" tabIndex="-1">
            <div className="modal-dialog">
                <div className="modal-content">
                    <div className="modal-header">
                        <h5 className="modal-title">Leave Project</h5>
                        <button type="button" className="btn-close" onClick={onClose}></button>
                    </div>
                    <div className="modal-body">
                        <form onSubmit={handleSubmit}>
                            <p>
                                Are you sure you want to leave this project? This action will:
                            </p>
                            <ul>
                                <li>Remove you from the project</li>
                                <li>Revoke your access to all project tasks and settings</li>
                                <li>Remove your admin privileges (if any)</li>
                            </ul>
                            <p className="text-warning">
                                <strong>Note:</strong> You can only rejoin if another member invites you back.
                            </p>
                            <div className="form-check">
                                <input
                                    type="checkbox"
                                    className="form-check-input"
                                    id="confirmLeave"
                                    checked={isConfirmed}
                                    onChange={() => setIsConfirmed(!isConfirmed)}
                                />
                                <label className="form-check-label" htmlFor="confirmLeave">
                                    I understand that I will lose access to this project.
                                </label>
                            </div>
                            {submitError && (
                                <div className="text-danger span-warning small mt-2">{submitError}</div>
                            )}
                            <div className="d-flex justify-content-end mt-3">
                                <button type="button" className="btn btn-secondary me-2" onClick={onClose} disabled={isLoading}>
                                    Cancel
                                </button>
                                <button type="submit" className="btn btn-danger" disabled={!isConfirmed || isLoading}>
                                    {isLoading ? 'Leaving...' : 'Leave Project'}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    );
} 