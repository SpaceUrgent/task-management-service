import React, { useState } from "react";
import AppConstants from "../../../AppConstants.ts";
import { ProjectClient } from "../../api/ProjectClient.ts";
import { useProjectContext } from "../../contexts/ProjectContext";
import {useForm} from "react-hook-form";
import ValidatedFormInput from "../../../shared/components/ValidatedFormInput";

export default function AddMemberModal({ onClose, onSubmit }) {
    const { project } = useProjectContext();
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
            email: ""
        }
    });

    const handleAddMember = async (data) => {
        setIsLoading(true);
        setSubmitError("");
        try {
            await projectClient.addProjectMember(project.id, data.email);
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
                    <form onSubmit={handleSubmit(handleAddMember)}>
                        <div className="modal-header">
                            <h5 className="modal-title">Add Member</h5>
                        </div>
                        <div className="modal-body">
                            <ValidatedFormInput
                                type="email"
                                name="email"
                                label="Member email"
                                placeholder="member@domain.com"
                                registration={register("email", {
                                    required: "Please enter valid email address",
                                    pattern: {
                                        value: AppConstants.VALID_EMAIL_REGEX,
                                        message: "Please enter valid email address"
                                    }
                                })}
                                errors={errors}
                                touchedFields={touchedFields}
                            />
                        </div>
                        {submitError && (
                            <span className="text-danger span-warning m-3">{submitError}</span>
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