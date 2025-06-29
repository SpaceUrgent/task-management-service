import React, { useState } from "react";

export default function PromoteMemberModal({ onCancel, onSubmit }) {
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
            setSubmitError("Failed to promote member");
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="modal show d-block" tabIndex="-1">
            <div className="modal-dialog">
                <div className="modal-content">
                    <div className="modal-header">
                        <h5 className="modal-title">Confirm Admin Promotion</h5>
                        <button type="button" className="btn-close" onClick={onCancel}></button>
                    </div>
                    <div className="modal-body">
                        <form onSubmit={handleSubmit}>
                            <p>
                                Admins have elevated privileges, including the ability to:
                            </p>
                            <ul>
                                <li>Manage project members</li>
                                <li>Edit project details</li>
                                <li>Access all tasks and settings</li>
                            </ul>
                            <div className="form-check">
                                <input
                                    type="checkbox"
                                    className="form-check-input"
                                    id="agree"
                                    checked={isConfirmed}
                                    onChange={() => setIsConfirmed(!isConfirmed)}
                                />
                                <label className="form-check-label" htmlFor="agree">
                                    I understand that this action gives full admin rights.
                                </label>
                            </div>
                            {submitError && (
                                <div className="text-danger span-warning small mt-2">{submitError}</div>
                            )}
                            <div className="d-flex justify-content-end mt-3">
                                <button type="button" className="btn btn-secondary me-2" onClick={onCancel} disabled={isLoading}>
                                    Cancel
                                </button>
                                <button type="submit" className="btn btn-primary" disabled={!isConfirmed || isLoading}>
                                    {isLoading ? 'Submitting...' : 'Submit'}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    );
}