import React, { useState } from "react";
import TextArea from "../../../common/components/TextArea";
import { ProjectClient } from "../../api/ProjectClient.ts";
import {useForm} from "react-hook-form";
import ValidatedFormInput from "../../../common/components/ValidatedFormInput";

export default function CreateProjectModal({ onClose, onSubmit }) {
    const projectClient = ProjectClient.getInstance();
    const [description, setDescription] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const [submitError, setSubmitError] = useState("");

    const {
        register,
        handleSubmit,
        formState: { errors, isValid, touchedFields }
    } = useForm({
        mode: "all",
        defaultValues: {
            title: ""
        }
    });

    const handleCreateProject = async (data) => {
        setIsLoading(true);
        setSubmitError("");
        try {
            await projectClient.createProject({ title: data.title, description });
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
                    <form onSubmit={handleSubmit(handleCreateProject)}>
                        <div className="modal-header">
                            <h5 className="modal-title">Create new project</h5>
                        </div>
                        <div className="modal-body">
                            <ValidatedFormInput
                                name="title"
                                label="Title"
                                type="text"
                                registration={register("title", {
                                    required: "Please enter title"
                                })}
                                errors={errors}
                                touchedFields={touchedFields}
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