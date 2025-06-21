import React, { useState } from "react";
import TextArea from "../../../common/components/TextArea";
import { ProjectClient } from "../../api/ProjectClient.ts";
import { useParams } from "react-router-dom";
import { useProjectContext } from "../../contexts/ProjectContext";
import { useFormValidation } from "../../hooks/useFormValidation";
import FormField from "../../components/FormField";

export default function CreateTaskModal({ onClose, onSubmit }) {
    const { project } = useProjectContext();
    const { projectId } = useParams();
    const projectClient = ProjectClient.getInstance();
    const [description, setDescription] = useState("");
    const [submitError, setSubmitError] = useState("");
    const [isLoading, setIsLoading] = useState(false);

    const validationRules = {
        title: (value) => value && value.length > 5,
        assigneeId: (value) => !!value,
        priority: (value) => !!value,
        dueDate: (value) => {
            if (!value) return true;
            let selectedDate = new Date(value);
            const today = new Date();
            selectedDate.setHours(0, 0, 0, 0);
            today.setHours(0, 0, 0, 0);
            return selectedDate >= today;
        },
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
            const request = {
                title: formData.title,
                description: description,
                assigneeId: formData.assigneeId,
                priority: formData.priority,
                dueDate: formData.dueDate
            };
            await projectClient.createTask(projectId, request);
            onSubmit();
        } catch (error) {
            setSubmitError(error.message ? error.message : "Failed to create task");
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
                            <h5 className="modal-title">Create Task</h5>
                        </div>
                        <div className="modal-body">
                            <FormField
                                id="title"
                                type="text"
                                name="Title"
                                placeholder="Feed cat"
                                value={formData.title}
                                onChange={value => updateField('title', value)}
                                onBlur={() => showFieldError('title')}
                                isValid={validation.title}
                                showError={showErrors.title}
                                errorMessage="Please enter a title (min 6 chars)"
                                required={true}
                            />
                            <div className="mb-1 text-start">
                                <label className="label form-label ms-2 fw-bold small" htmlFor="assignee">Assignee</label>
                                <select
                                    className="form-select"
                                    id="assignee"
                                    value={formData.assigneeId || ''}
                                    onBlur={() => showFieldError('assigneeId')}
                                    onChange={e => updateField('assigneeId', e.target.value)}
                                >
                                    <option value="">Select Assignee</option>
                                    {project?.members.map((member) => (
                                        <option key={member.id} value={member.id}>{member.fullName}</option>
                                    ))}
                                </select>
                                {showErrors.assigneeId && !validation.assigneeId && (
                                    <span className="text-danger span-warning small">Please select assignee</span>
                                )}
                            </div>
                            <div className="mb-1 text-start">
                                <label className="label form-label ms-2 fw-bold small" htmlFor="Priority">Priority</label>
                                <select
                                    className="form-select"
                                    id="priority"
                                    value={formData.priority || ''}
                                    onBlur={() => showFieldError('priority')}
                                    onChange={e => updateField('priority', e.target.value)}
                                >
                                    <option value="">Select Priority</option>
                                    {project?.taskPriorities.map((priority) => (
                                        <option key={priority.name} value={priority.name}>{priority.name}</option>
                                    ))}
                                </select>
                                {showErrors.priority && !validation.priority && (
                                    <span className="text-danger span-warning small">Please select priority</span>
                                )}
                            </div>
                            <div className="mb-1 text-start">
                                <label className="label form-label ms-2 fw-bold small" htmlFor="dueDate">Due date</label>
                                <input
                                    id="dueDate"
                                    type="date"
                                    className="form-select"
                                    value={formData.dueDate || ''}
                                    onBlur={() => showFieldError('dueDate')}
                                    onChange={e => updateField('dueDate', e.target.value)}
                                />
                                {showErrors.dueDate && !validation.dueDate && (
                                    <span className="text-danger span-warning small">Due date must be future</span>
                                )}
                            </div>
                            <TextArea
                                id="description"
                                name="Description"
                                placeholder="Task description..."
                                value={description}
                                onChange={setDescription}
                                rows={5}
                            />
                        </div>
                        {submitError && (
                            <div className="text-center">
                                <span className="text-danger span-warning small">
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