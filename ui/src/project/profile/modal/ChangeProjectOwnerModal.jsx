import React, {useState} from "react";
import {useProjectContext} from "../../contexts/ProjectContext";
import {ProjectClient} from "../../api/ProjectClient.ts";
import LabeledSelector from "../../../common/components/selectors/LabeledSelector";

export default function ChangeProductOwnerModal({ onClose }) {
    const { project} = useProjectContext();
    const projectClient = ProjectClient.getInstance();

    const [isConfirmed, setIsConfirmed] = useState(false);
    const [selectedOwnerId, setSelectedOwnerId] = useState(null);
    const [submitError, setSubmitError] = useState(null);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!selectedOwnerId || !isConfirmed) return;
        if (selectedOwnerId === project.owner.id) return;
        try {
            await projectClient.updateMemberRole(project.id, selectedOwnerId, 'Owner');
            onClose();
        } catch (error) {
            setSubmitError('Failed to update owner');
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
                                    value={selectedOwnerId ? selectedOwnerId : project.owner.id}
                                    onChange={setSelectedOwnerId}
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
                            {submitError &&
                                <span className="text-danger span-warning">{submitError}</span>
                            }
                            <div className="d-flex justify-content-end">
                                <button type="button" className="btn btn-secondary me-2" onClick={onClose}>
                                    Cancel
                                </button>
                                <button type="submit" className="btn btn-primary" disabled={!isConfirmed}>
                                    Change Owner
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    );
}