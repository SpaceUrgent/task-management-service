import React, { useState } from "react";
import { ProjectClient } from "../../api/ProjectClient.ts";
import { useProjectContext } from "../../contexts/ProjectContext";
import {useFormValidation} from "../../../common/hooks/useFormValidation";
import FormInput from "../../../common/components/FormInput";

export default function AddTaskStatusModal({ statusPosition, onClose }) {
    const { project, refreshData } = useProjectContext();
    const projectClient = ProjectClient.getInstance();
    const [submitError, setSubmitError] = useState("");
    const [isLoading, setIsLoading] = useState(false);

    const validationRules = {
        status: (value) => !!value && value.length >= 4,
    };

    const {
        formData,
        validation,
        showErrors,
        updateField,
        showFieldError,
        isFormValid
    } = useFormValidation(validationRules);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!isFormValid()) {
            Object.keys(validation).forEach(field => showFieldError(field));
            return;
        }
        setIsLoading(true);
        setSubmitError("");
        try {
            await projectClient.addTaskStatus(project.id, {
                name: formData.status,
                position: statusPosition
            });
            refreshData();
            onClose();
        } catch (error) {
            setSubmitError(error.message ? error.message : "Failed to add task status");
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="modal d-block" tabIndex={-1} role="dialog">
            <div className="modal-dialog modal-dialog-centered">
                <div className="modal-content">
                    <form onSubmit={handleSubmit}>
                        <div className="modal-header">
                            <h5 className="modal-title">Add task status</h5>
                        </div>
                        <div className="modal-body">
                            <FormInput
                                id="status"
                                type="text"
                                name="Status name"
                                value={formData.status}
                                onChange={value => updateField('status', value)}
                                onBlur={() => showFieldError('status')}
                                isValid={validation.status}
                                showError={showErrors.status}
                                errorMessage="Status must be at least 4 characters"
                                required={true}
                            />
                            {submitError && (
                                <div className="text-center">
                                    <span className="text-danger span-warning">
                                        <strong>{submitError}</strong>
                                    </span>
                                </div>
                            )}
                        </div>
                        <div className="modal-footer">
                            <button className="btn btn-secondary" type="button" onClick={onClose} disabled={isLoading}>Close</button>
                            <button className="btn btn-primary" type="submit" disabled={!isFormValid() || isLoading}>
                                {isLoading ? 'Submitting...' : 'Submit'}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
}