import React, {useEffect, useState} from "react";
import ValidatedInput from "../../common/components/ValidatedInput";

export default function ChangePasswordModal({ show, onClose, onChangePassword, errorMessage }) {

    const [currentPassword, setCurrentPassword] = useState('');
    const [currentPasswordIsValid, setCurrentPasswordIsValid] = useState(false);
    const [showCurrentPasswordError, setShowCurrentPasswordError] = useState(false);

    const [password, setPassword] = useState('');
    const [passwordIsValid, setPasswordIsValid] = useState(false);
    const [showPasswordError, setShowPasswordError] = useState(false);

    const [confirmPassword, setConfirmPassword] = useState('');
    const [passwordMatch, setPasswordMatch] = useState(false);
    const [showConfirmPasswordError, setShowConfirmPasswordError] = useState(false);

    useEffect(() => {
        function validPassword(value) {
            return value && value.length >= 8;
        }
        setCurrentPasswordIsValid(validPassword(currentPassword));
        setPasswordIsValid(validPassword(password));
        setPasswordMatch(password === confirmPassword);
    }, [currentPassword, password, confirmPassword]);

    const handleSubmit = () => {
        if (!inputsAreValid()) return;

        onChangePassword(currentPassword, password);
    };

    const inputsAreValid = () => {
        return currentPasswordIsValid
            && passwordIsValid
            && passwordMatch;
    }

    return (
        <div className={`modal ${show ? 'd-block' : 'd-none'}`} tabIndex="-1">
            <div className="modal-dialog modal-dialog-centered">
                <div className="modal-content">
                    <div className="modal-header">
                        <h5 className="modal-title">Change Password</h5>
                        <button type="button" className="btn-close" onClick={onClose}></button>
                    </div>
                    <div className="modal-body">
                        <ValidatedInput
                            id="currentPassword"
                            type="password"
                            name="Current Password"
                            value={currentPassword}
                            onChange={(value) => setCurrentPassword(value)}
                            onBlur={() => setShowCurrentPasswordError(true)}
                            errorMessage="Invalid current password"
                            isValid={currentPasswordIsValid}
                            showError={showCurrentPasswordError}
                            required={true}
                        />
                        <ValidatedInput
                            id="password"
                            type="password"
                            name="Password"
                            value={password}
                            onChange={(value) => setPassword(value)}
                            onBlur={() => setShowPasswordError(false)}
                            errorMessage="Password must contain at least 8 characters"
                            isValid={passwordIsValid}
                            showError={showPasswordError}
                            required={true}
                        />
                        <ValidatedInput
                            id="confirmPassword"
                            type="password"
                            name="Confirm Password"
                            value={confirmPassword}
                            onChange={(value) => setConfirmPassword(value)}
                            onBlur={() => setShowConfirmPasswordError(true)}
                            errorMessage="Confirm doesn't match password"
                            isValid={passwordMatch && password}
                            showError={showConfirmPasswordError}
                            required={true}
                        />
                        <p/>
                        {errorMessage && <span className="text-danger span-warning">{errorMessage}</span>}
                    </div>
                    <div className="modal-footer">
                        <button className="btn btn-secondary" onClick={onClose}>Cancel</button>
                        <button className="btn btn-primary" onClick={handleSubmit}>Change Password</button>
                    </div>
                </div>
            </div>
        </div>
    );
}
