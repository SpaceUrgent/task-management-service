import React, { useState } from "react";
import TextArea from "../../../common/components/TextArea";
import { ProjectClient } from "../../api/ProjectClient.ts";
import {useFormValidation} from "../../../common/hooks/useFormValidation";
import FormInput from "../../../common/components/FormInput";

export default function CreateProjectModal({ onClose, onSubmit }) {
    const projectClient = ProjectClient.getInstance();
    const [description, setDescription] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const [submitError, setSubmitError] = useState("");

    const validationRules = {
        title: (value) => value && value.length > 0,
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
            await projectClient.createProject({ title: formData.title, description });
            onClose();
            onSubmit();
        } catch (error) {
            setSubmitError(error.message ? error.message : "Failed to create project");
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
                            <h5 className="modal-title">Create new project</h5>
                        </div>
                        <div className="modal-body">
                            <FormInput
                                id="title"
                                type="text"
                                name="Title"
                                placeholder="Project title"
                                value={formData.title}
                                onChange={value => updateField('title', value)}
                                onBlur={() => showFieldError('title')}
                                isValid={validation.title}
                                showError={showErrors.title}
                                errorMessage="Please enter title"
                                required={true}
                            />
                            <TextArea
                                id="description"
                                name="Description"
                                placeholder="Project description..."
                                value={description}
                                onChange={setDescription}
                                rows={5}
                            />
                        </div>
                        {submitError && (
                            <div className="text-center">
                                <span className="text-danger span-warning small">
                                    {submitError}
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