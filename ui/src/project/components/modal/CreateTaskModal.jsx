import React, {useEffect, useState} from "react";
import ValidatedInput from "../../../common/components/ValidatedInput";
import TextArea from "../../../common/components/TextArea";
import {ProjectClient} from "../../api/ProjectClient.ts";

export default function CreateTaskModal({projectId, members = [], onClose, onSubmit}) {
    const projectClient = ProjectClient.getInstance();

    const [title, setTitle] = useState("");
    const [titleIsValid, setTitleIsValid] = useState(false);
    const [showTitleError, setShowTitleError] = useState(false);

    const [assigneeId, setAssigneeId] = useState(null);
    const [assigneeIdIsValid, setAssigneeIdIsValid] = useState(false);
    const [showAssigneeError, setShowAssigneeError] = useState(false);

    const [description, setDescription] = useState("");

    const [submitError, setSubmitError] = useState("");

    useEffect(() => {
        setTitleIsValid(title && title.length > 5)
    }, [title])

    useEffect(() => {
        setAssigneeIdIsValid(!!assigneeId);
    }, [assigneeId]);

    const memberName = (member) => {
        return member ? `${member?.firstName} ${member?.lastName}` : "";
    }

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!titleIsValid || !assigneeIdIsValid) {
            setShowAssigneeError(true);
            setShowTitleError(true);
            return;
        }

        try {
            const request = {
                title: title,
                description: description,
                assigneeId: assigneeId,
            }
            await projectClient.createTask(projectId, request);
            onSubmit();
        } catch (error) {
            setSubmitError(error.message ? error.message : "Failed to create task");
        }
    }

    return(
        <div
            className="modal d-block"
            tabIndex={-1}
            role="dialog"
        >
            <div className="modal-dialog modal-dialog-centered">
                <div className="modal-content">
                    <form onSubmit={handleSubmit}>
                        <div className="modal-header">
                            <h5 className="modal-title">Create Task</h5>
                        </div>
                        <div className="modal-body">
                            <ValidatedInput
                                id="title"
                                type="text"
                                name="Title"
                                placeholder="Feed cat"
                                onChange={(value) => setTitle(value)}
                                isValid={titleIsValid}
                                onBlur={() => setShowTitleError(true)}
                                errorMessage="Please enter title"
                                showError={showTitleError}
                                required={true}
                            />
                            <div className="mb-1 text-start">
                                <label className="label form-label mb-0" htmlFor="assignee">Assignee</label>
                                <select
                                    className="form-select"
                                    onBlur={() => setShowAssigneeError(true)}
                                    onChange={(e) => {
                                        const assigneeId = e.target.value;
                                        console.log('selected assigneeId: ' + assigneeId);
                                        setAssigneeId(assigneeId)
                                    }}
                                >
                                    <option value="">Select Assignee</option>
                                    {
                                        members.map((member) => (
                                            <option key={member.id} value={member.id}>{memberName(member)}</option>
                                        ))
                                    }
                                </select>
                                {showAssigneeError && !assigneeIdIsValid &&
                                    <span className="text-danger span-warning">Please select assignee</span>
                                }
                            </div>
                            <TextArea
                                id="description"
                                name="Description"
                                placeholder="Task description..."
                                value={description}
                                onChange={(value) => setDescription(value)}
                                rows={5}
                            />
                        </div>
                        {submitError &&
                            <div className="text-center">
                                <span className="text-danger span-warning">
                                    <strong>
                                        {submitError}
                                    </strong>
                                </span>
                            </div>
                        }
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