import React, { useState } from "react";
import { useProjectContext } from "../../contexts/ProjectContext";
import { ProjectClient } from "../../api/ProjectClient.ts";
import LabeledSelector from "../../../shared/components/selectors/LabeledSelector";

export default function ChangeProductOwnerModal({ onClose }) {
    const { project } = useProjectContext();
    const projectClient = ProjectClient.getInstance();
    const [selectedOwner, setSelectedOwner] = useState(null);
    const [isConfirmed, setIsConfirmed] = useState(false);
    const [submitError, setSubmitError] = useState(null);
    const [isLoading, setIsLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!selectedOwner || selectedOwner === project.owner.id) {
            onClose();
            return;
        }
        setIsLoading(true);
        setSubmitError(null);
        try {
            await projectClient.updateMemberRole(project.id, selectedOwner, 'Owner');
            onClose();
        } catch (error) {
            setSubmitError('Failed to update owner');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="modal show d-block" tabIndex="-1">
            <div className="modal-dialog modal-dialog-centered">
                <div className="modal-content">
                    <div className="modal-header">
                        <h5 className="modal-title">Change Project Owner</h5>
                        <button type="button" className="btn-close" onClick={onClose}></button>
                    </div>
                    <div className="modal-body">
                        <form onSubmit={handleSubmit}>
                            <div className="mb-3">
                                <LabeledSelector
                                    label="Owner"
                                    value={project.owner.id}
                                    onChange={setSelectedOwner}
                                    options={project?.members.map((member) => ({
                                        value: member.id,
                                        label: member.fullName,
                                    }))}
                                />
                            </div>
                            <div className="form-check mb-3">
                                <input
                                    className="form-check-input"
                                    type="checkbox"
                                    id="confirmOwnership"
                                    checked={isConfirmed}
                                    onChange={(e) => setIsConfirmed(e.target.checked)}
                                />
                                <label className="form-check-label" htmlFor="confirmOwnership">
                                    I understand that this action permanently transfers project ownership.
                                </label>
                            </div>
                            {submitError && (
                                <span className="text-danger span-warning">{submitError}</span>
                            )}
                            <div className="d-flex justify-content-end">
                                <button type="button" className="btn btn-secondary me-2" onClick={onClose} disabled={isLoading}>
                                    Cancel
                                </button>
                                <button
                                    type="submit"
                                    className="btn btn-primary"
                                    disabled={!isConfirmed || isLoading}
                                >
                                    {isLoading ? 'Changing...' : 'Change Owner'}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    );
}