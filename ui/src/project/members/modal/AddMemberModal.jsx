import React, { useState } from "react";
import AppConstants from "../../../AppConstants.ts";
import { ProjectClient } from "../../api/ProjectClient.ts";
import { useProjectContext } from "../../contexts/ProjectContext";
import { useFormValidation } from "../../hooks/useFormValidation";
import FormField from "../../components/FormField";

export default function AddMemberModal({ onClose, onSubmit }) {
    const { project } = useProjectContext();
    const projectClient = ProjectClient.getInstance();
    const [submitError, setSubmitError] = useState("");
    const [isLoading, setIsLoading] = useState(false);

    const validationRules = {
        email: (value) => value && value.match(AppConstants.VALID_EMAIL_REGEX),
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
            await projectClient.addProjectMember(project.id, formData.email);
            onSubmit();
        } catch (error) {
            setSubmitError(error.message ? error.message : "Failed to add project member");
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
                            <h5 className="modal-title">Add Member</h5>
                        </div>
                        <div className="modal-body">
                            <FormField
                                id="email"
                                type="email"
                                name="Email"
                                value={formData.email}
                                onChange={value => updateField('email', value)}
                                onBlur={() => showFieldError('email')}
                                placeholder="username@domain.com"
                                errorMessage="Please enter a valid email"
                                showError={showErrors.email}
                                isValid={validation.email}
                                required={true}
                            />
                        </div>
                        {submitError && (
                            <div className="text-center">
                                <span className="text-danger span-warning">
                                    <strong>{submitError}</strong>
                                </span>
                            </div>
                        )}
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