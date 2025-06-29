import React, { useState } from "react";
import TextArea from "../../../shared/components/TextArea";
import { ProjectClient } from "../../api/ProjectClient.ts";
import { useParams } from "react-router-dom";
import { useProjectContext } from "../../contexts/ProjectContext";
import {useForm} from "react-hook-form";
import ValidatedFormInput from "../../../shared/components/ValidatedFormInput";

export default function CreateTaskModal({ onClose, onSubmit }) {
    const { project } = useProjectContext();
    const { projectId } = useParams();
    const projectClient = ProjectClient.getInstance();
    const [description, setDescription] = useState("");
    const [submitError, setSubmitError] = useState("");
    const [isLoading, setIsLoading] = useState(false);

    const {
        register,
        handleSubmit,
        formState: { errors, isValid, touchedFields }
    } = useForm({
        mode: "all",
        defaultValues: {
            title: "",
            assignee: "",
            priority: null,
            dueDate: null,
        }
    });

    const isValidDueDate = (value) => {
        if (!value) return true;
        let selectedDate = new Date(value);
        const today = new Date();
        selectedDate.setHours(0, 0, 0, 0);
        today.setHours(0, 0, 0, 0);
        return selectedDate >= today;
    }

    const handleCreateTask = async (data) => {
        setIsLoading(true);
        setSubmitError("");
        try {
            const request = {
                title: data.title,
                description: description,
                assigneeId: data.assignee,
                priority: data.priority,
                dueDate: data.dueDate
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
                    <form onSubmit={handleSubmit(handleCreateTask)}>
                        <div className="modal-header">
                            <h5 className="modal-title">Create Task</h5>
                        </div>
                        <div className="modal-body">
                            <ValidatedFormInput
                                name="title"
                                label="Title"
                                placeholder="Enter title"
                                registration={register("title", {
                                    required: "Title is required",
                                    validate: value => value?.length >= 6 || "Title must be at least 6 characters",
                                })}
                                errors={errors}
                                touchedFields={touchedFields}
                            />
                            <div className="mb-1 text-start">
                                <label className="label form-label ms-2 fw-bold small" htmlFor="assignee">Assignee</label>
                                <select
                                    className="form-select"
                                    id="assignee"
                                    {...register("assignee", {
                                        required: "Please select an assignee",
                                    })}
                                >
                                    <option value="">Select Assignee</option>
                                    {project?.members.map((member) => (
                                        <option key={member.id} value={member.id}>{member.fullName}</option>
                                    ))}
                                </select>
                                {errors.assignee && touchedFields.assignee && (
                                    <span className="text-danger span-warning small">{errors.assignee.message}</span>
                                )}
                            </div>
                            <div className="mb-1 text-start">
                                <label className="label form-label ms-2 fw-bold small" htmlFor="Priority">Priority</label>
                                <select
                                    className="form-select"
                                    id="priority"
                                    {...register("priority", {
                                        required: "Please select priority",
                                    })}
                                >
                                    <option value="">Select Priority</option>
                                    {project?.taskPriorities.map((priority) => (
                                        <option key={priority.name} value={priority.name}>{priority.name}</option>
                                    ))}
                                </select>
                                {errors.priority && touchedFields.priority && (
                                    <span className="text-danger span-warning small">{errors.priority.message}</span>
                                )}
                            </div>
                            <div className="mb-1 text-start">
                                <label className="label form-label ms-2 fw-bold small" htmlFor="dueDate">Due date</label>
                                <input
                                    id="dueDate"
                                    type="date"
                                    className="form-select"
                                    {...register("dueDate", {
                                        required: false,
                                        validate: value =>  isValidDueDate(value) || "Due date must be future"
                                    })}
                                />
                                {errors.dueDate && touchedFields.dueDate && (
                                    <span className="text-danger span-warning small">{errors.dueDate.message}</span>
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
                            <button className="btn btn-primary" type="submit" disabled={!isValid || isLoading}>
                                {isLoading ? 'Submitting...' : 'Submit'}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
}