import React, { useState } from "react";

export default function ExcludeMemberModal({ member, onClose, onSubmit }) {
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
            setSubmitError("Failed to exclude member");
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="modal show d-block" tabIndex="-1">
            <div className="modal-dialog">
                <div className="modal-content">
                    <div className="modal-header">
                        <h5 className="modal-title">Exclude Member</h5>
                        <button type="button" className="btn-close" onClick={onClose}></button>
                    </div>
                    <div className="modal-body">
                        <form onSubmit={handleSubmit}>
                            <p>
                                Are you sure you want to exclude <strong>{member.fullName}</strong> from this project?
                            </p>
                            <p>This action will:</p>
                            <ul>
                                <li>Remove {member.fullName} from the project</li>
                                <li>Revoke their access to all project tasks and settings</li>
                                <li>Remove their admin privileges (if any)</li>
                            </ul>
                            <p className="text-warning">
                                <strong>Note:</strong> They can only rejoin if you invite them back.
                            </p>
                            <div className="form-check">
                                <input
                                    type="checkbox"
                                    className="form-check-input"
                                    id="confirmExclude"
                                    checked={isConfirmed}
                                    onChange={() => setIsConfirmed(!isConfirmed)}
                                />
                                <label className="form-check-label" htmlFor="confirmExclude">
                                    I understand that {member.fullName} will lose access to this project.
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
                                    {isLoading ? 'Excluding...' : 'Exclude Member'}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    );
} 