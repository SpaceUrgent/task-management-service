import React, { useState } from "react";
import {useForm} from "react-hook-form";
import ValidatedFormInput from "../../common/components/ValidatedFormInput";

export default function ChangePasswordModal({ show, onClose, onChangePassword, errorMessage }) {
    const [isLoading, setIsLoading] = useState(false);
    const [localError, setLocalError] = useState("");

    const {
        register,
        handleSubmit,
        watch,
        formState: { errors, isValid, touchedFields }
    } = useForm({
        mode: "all",
        defaultValues: {
            currentPassword: "",
            password: "",
            confirmPassword: ""
        }
    });

    const onSubmit = async (data) => {
        setLocalError("");
        setIsLoading(true);
        try {
            await onChangePassword(data.currentPassword, data.password);
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
                    <form onSubmit={handleSubmit(onSubmit)}>
                        <div className="modal-header">
                            <h5 className="modal-title">Change Password</h5>
                            <button type="button" className="btn-close" onClick={onClose}></button>
                        </div>
                        <div className="modal-body">
                            <ValidatedFormInput
                                type="password"
                                name="currentPassword"
                                label="Current Password"
                                registration={register("currentPassword", {
                                    required: "Current password is required",
                                })}
                                errors={errors}
                                touchedFields={touchedFields}
                            />
                            <ValidatedFormInput
                                type="password"
                                name="password"
                                label="New Password"
                                registration={register("password", {
                                    required: "Password must contain at least 8 characters",
                                    minLength: {
                                        value: 8,
                                        message: "Password must contain at least 8 characters"
                                    }
                                })}
                                errors={errors}
                                touchedFields={touchedFields}
                            />
                            <ValidatedFormInput
                                type="password"
                                name="confirmPassword"
                                label="Confirm Password"
                                registration={register("confirmPassword", {
                                    required: "Passwords don't match",
                                    validate: value => value === watch("password") || "Passwords don't match"
                                })}
                                errors={errors}
                                touchedFields={touchedFields}
                            />
                            <p/>
                            {(errorMessage || localError) && <span className="text-danger span-warning">{errorMessage || localError}</span>}
                        </div>
                        <div className="modal-footer">
                            <button className="btn btn-secondary" type="button" onClick={onClose} disabled={isLoading}>Cancel</button>
                            <button className="btn btn-primary" type="submit" disabled={!isValid || isLoading}>
                                {isLoading ? 'Changing...' : 'Change Password'}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
}
