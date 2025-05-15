import React, {useEffect, useState} from "react";
import ValidatedInput from "../../../common/components/ValidatedInput";
import {ProjectClient} from "../../api/ProjectClient.ts";
import {useProjectContext} from "../../contexts/ProjectContext";

export default function AddTaskStatusModal({ statusPosition, onClose }) {

    const { project, refreshData } = useProjectContext();
    const projectClient = ProjectClient.getInstance();
    const [status, setStatus] = useState('');
    const [statusIsValid, setStatusIsValid] = useState(false);
    const [showStatusError, setShowStatusError] = useState(false);

    const [submitError, setSubmitError] = useState('');

    useEffect(() => {
        setStatusIsValid(!!status && status.length >= 4);
    }, [status])

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!statusIsValid) {
            setShowStatusError(true);
            return;
        }

        try {
            await projectClient.addTaskStatus(project.id, {
                name: status,
                position: statusPosition
            });
            refreshData();
            onClose();
        } catch (error) {
            setSubmitError(error.message ? error.message : "Failed to add task status");
        }
    }

    return (
        <div
            className="modal d-block"
            tabIndex={-1}
            role="dialog"
        >
            <div className="modal-dialog modal-dialog-centered">
                <div className="modal-content">
                    <form onSubmit={handleSubmit}>
                        <div className="modal-header">
                            <h5 className="modal-title">Add task status</h5>
                        </div>
                        <div className="modal-body">
                            <ValidatedInput
                                id="status"
                                type="text"
                                name="Status name"
                                onChange={(value) => setStatus(value)}
                                isValid={statusIsValid}
                                onBlur={() => setShowStatusError(true)}
                                errorMessage="Status must be at least 4 characters"
                                showError={showStatusError}
                                required={true}
                            />
                            {submitError &&
                                <div className="text-center">
                                <span className="text-danger span-warning">
                                    <strong>
                                        {submitError}
                                    </strong>
                                </span>
                                </div>
                            }
                        </div>
                        <div className="modal-footer">
                            <button className="btn btn-secondary" onClick={onClose}>Close</button>
                            <button className="btn btn-primary">Submit</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    )
}