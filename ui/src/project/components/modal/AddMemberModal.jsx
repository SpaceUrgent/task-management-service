import React, {useContext, useEffect, useState} from "react";
import ValidatedInput from "../../../common/components/ValidatedInput";
import AppConstants from "../../../AppConstants.ts";
import {ProjectClient} from "../../api/ProjectClient.ts";
import {ProjectContext} from "../../contexts/ProjectContext";

export default function AddMemberModal({ onClose }) {
    const { project } = useContext(ProjectContext);
    const projectClient = ProjectClient.getInstance();

    const [email, setEmail] = useState("");
    const [emailIsValid, setEmailIsValid] = useState(false);
    const [showEmailError, setShowEmailError] = useState(false);
    const [submitError, setSubmitError] = useState("");

    useEffect(() => {
        setEmailIsValid(email && email.match(AppConstants.VALID_EMAIL_REGEX))
    }, [email])

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!emailIsValid) {
            setShowEmailError(true);
            return;
        }
        try {
            await projectClient.addProjectMember(project.id, email);
            console.log('successfully added!');
            setTimeout(() => {

            }, 10000);
            onClose();
        } catch (error) {
            console.log(error);
            setSubmitError(error.message ? error.message : "Failed to add project member");
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
                            <h5 className="modal-title">Add Member</h5>
                        </div>
                        <div className="modal-body">
                            <ValidatedInput
                                id="email"
                                type="email"
                                name="Email"
                                value={email}
                                onChange={(value) => setEmail(value)}
                                placeholder="username@domain.com"
                                errorMessage="Please enter a valid email"
                                showError={showEmailError}
                                isValid={emailIsValid}
                                required={true}
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
                            <button className="btn btn-secondary" onClick={() => onClose()}>Close</button>
                            <button className="btn btn-primary">Submit</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    )
}