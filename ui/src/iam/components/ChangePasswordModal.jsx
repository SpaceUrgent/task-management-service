import React, { useState, useEffect } from "react";
import FormInput from "../../common/components/FormInput";
import { useFormValidation } from "../../common/hooks/useFormValidation";

export default function ChangePasswordModal({ show, onClose, onChangePassword, errorMessage }) {
    const [isLoading, setIsLoading] = useState(false);
    const [localError, setLocalError] = useState("");

    const validationRules = {
        currentPassword: (value) => value && value.length >= 8,
        password: (value) => value && value.length >= 8,
        confirmPassword: (value, formData) => value && value === formData.password,
    };

    const {
        formData,
        validation,
        showErrors,
        updateField,
        showFieldError,
        isFormValid,
        resetForm
    } = useFormValidation(validationRules);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLocalError("");
        setIsLoading(true);
        try {
            await onChangePassword(formData.currentPassword, formData.password);
            resetForm()
        } catch (err) {
            setLocalError("Failed to change password.");
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className={`modal ${show ? 'd-block' : 'd-none'}`} tabIndex="-1">
            <div className="modal-dialog modal-dialog-centered">
                <div className="modal-content">
                    <form onSubmit={handleSubmit}>
                        <div className="modal-header">
                            <h5 className="modal-title">Change Password</h5>
                            <button type="button" className="btn-close" onClick={onClose}></button>
                        </div>
                        <div className="modal-body">
                            <FormInput
                                id="currentPassword"
                                type="password"
                                name="Current Password"
                                value={formData.currentPassword}
                                onChange={value => updateField('currentPassword', value)}
                                onBlur={() => showFieldError('currentPassword')}
                                errorMessage="Current password must be at least 8 characters"
                                isValid={validation.currentPassword}
                                showError={showErrors.currentPassword}
                                required={true}
                            />
                            <FormInput
                                id="password"
                                type="password"
                                name="Password"
                                value={formData.password}
                                onChange={value => updateField('password', value)}
                                onBlur={() => showFieldError('password')}
                                errorMessage="Password must be at least 8 characters"
                                isValid={validation.password}
                                showError={showErrors.password}
                                required={true}
                            />
                            <FormInput
                                id="confirmPassword"
                                type="password"
                                name="Confirm Password"
                                value={formData.confirmPassword}
                                onChange={value => updateField('confirmPassword', value)}
                                onBlur={() => showFieldError('confirmPassword')}
                                errorMessage="Passwords do not match"
                                isValid={validation.confirmPassword}
                                showError={showErrors.confirmPassword}
                                required={true}
                            />
                            <p/>
                            {(errorMessage || localError) && <span className="text-danger span-warning">{errorMessage || localError}</span>}
                        </div>
                        <div className="modal-footer">
                            <button className="btn btn-secondary" type="button" onClick={onClose} disabled={isLoading}>Cancel</button>
                            <button className="btn btn-primary" type="submit" disabled={!isFormValid() || isLoading}>
                                {isLoading ? 'Changing...' : 'Change Password'}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
}
