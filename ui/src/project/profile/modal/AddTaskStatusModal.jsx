import React, { useState } from "react";
import { ProjectClient } from "../../api/ProjectClient.ts";
import { useProjectContext } from "../../contexts/ProjectContext";
import ValidatedFormInput from "../../../common/components/ValidatedFormInput";
import {useForm} from "react-hook-form";

export default function AddTaskStatusModal({ statusPosition, onClose }) {
    const { project, refreshData } = useProjectContext();
    const projectClient = ProjectClient.getInstance();
    const [submitError, setSubmitError] = useState("");
    const [isLoading, setIsLoading] = useState(false);

    const {
        register,
        handleSubmit,
        formState: { errors, isValid, touchedFields }
    } = useForm({
        mode: "all",
        defaultValues: {
            statusName: ""
        }
    });

    const handleAddTaskStatus = async (data) => {
        setIsLoading(true);
        setSubmitError("");
        try {
            await projectClient.addTaskStatus(project.id, {
                name: data.statusName,
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
                    <form onSubmit={handleSubmit(handleAddTaskStatus)}>
                        <div className="modal-header">
                            <h5 className="modal-title">Add task status</h5>
                        </div>
                        <div className="modal-body">
                            <ValidatedFormInput
                                name="statusName"
                                label="Status name"
                                registration={register("statusName", {
                                    required: "Status name is required",
                                    validate: value => value?.length >= 3 || "Status name must contain at least 3 characters"
                                })}
                                errors={errors}
                                touchedFields={touchedFields}
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