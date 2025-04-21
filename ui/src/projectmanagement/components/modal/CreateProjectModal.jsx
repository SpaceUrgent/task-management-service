import React, {useEffect, useState} from "react";
import TextArea from "../../../common/components/TextArea";
import {ProjectClient} from "../../api/ProjectClient.ts";
import ValidatedInput from "../../../common/components/ValidatedInput";

export default function CreateProjectModal({ onClose, onSubmit }) {
    const projectClient = ProjectClient.getInstance();

    const [title, setTitle] = useState("");
    const [titleIsValid, setTitleIsValid] = useState(false);
    const [showTitleError, setShowTitleError] = useState(false);

    const [description, setDescription] = useState("");

    useEffect(() => {
        setTitleIsValid(title.length > 0);
    }, [title])

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!titleIsValid) {
            setShowTitleError(true);
            return;
        }
        console.log(`title: ${title} \ndescription: ${description}`);
        await projectClient.createProject({title, description});
        onClose();
        onSubmit();
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
                            <h5 className="modal-title">Create new project</h5>
                        </div>
                        <div className="modal-body">
                            <ValidatedInput
                                id="title"
                                type="text"
                                name="Title"
                                placeholder="Project title"
                                onChange={(value) => setTitle(value)}
                                isValid={titleIsValid}
                                onBlur={() => setShowTitleError(true)}
                                errorMessage="Please enter title"
                                showError={showTitleError}
                                required={true}
                            />
                            <TextArea
                                id="description"
                                name="Description"
                                placeholder="Project description..."
                                onChange={(value) => setDescription(value)}
                                rows={5}
                            />
                        </div>
                        <div className="modal-footer">
                            <button className="btn btn-secondary" onClick={() => onClose()}>Close</button>
                            <button className="btn btn-primary">Submit</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    )
}