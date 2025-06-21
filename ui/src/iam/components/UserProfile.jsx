import React, {useEffect, useState} from 'react';
import {IAMClient, IAMClientError} from "../api/IAMClient.ts";
import LabeledValue from "../../common/components/LabeledValue";
import EditableLabeledValue from "../../common/components/EditableLabeledValue";
import LoadingSpinner from "../../common/components/LoadingSpinner";
import Alert from "../../common/components/Alert";
import ChangePasswordModal from "./ChangePasswordModal";
import {c} from "react/compiler-runtime";

export default function UserProfile() {
    const iamClient = IAMClient.getInstance();

    const [isLoading, setIsLoading] = useState(true);
    const [failedLoad, setFailedLoad] = useState(false);
    const [errorMessage, setErrorMessage] = useState(null);
    const [user, setUser] = useState(null);

    const [showPasswordModal, setShowPasswordModal] = useState(false);
    const [updatePasswordError, setUpdatePasswordError] = useState(null);

    useEffect(() => {
        fetchUser();
    }, [])

    const fetchUser = async () => {
        setIsLoading(true);
        try {
            const userProfile = await iamClient.getUserProfile();
            setUser(userProfile);
        } catch (error) {
            setFailedLoad(true);
        } finally {
            setIsLoading(false);
        }
    }

    const handleUpdateFirstName = async (value) => {
        if (!value || value === user.firstName) return;
        try {
            await iamClient.updateUserProfile({
                firstName: value,
                lastName: user.lastName,
            })
            fetchUser();
        } catch (error) {
            setErrorMessage(`Failed to update first name`);
            setTimeout(() => setErrorMessage(null), 5000);
        }
    }

    const handleUpdateLastName = async (value) => {
        if (!value || value === user.lastName) return;
        try {
            await iamClient.updateUserProfile({
                firstName: user.firstName,
                lastName: value,
            })
            fetchUser();
        } catch (error) {
            setErrorMessage(`Failed to update last name`);
            setTimeout(() => setErrorMessage(null), 5000);
        }
    }

    const handleUpdatePassword = async (currentPassword, newPassword) => {
        if (!currentPassword || !newPassword) return;
        try {
            await iamClient.updatePassword({
                currentPassword: currentPassword,
                newPassword: newPassword
            });
            setShowPasswordModal(false);
            fetchUser();
        } catch (error) {
            setUpdatePasswordError(
                error instanceof IAMClientError ? error.message : 'Failed to update password. Try again later'
            )
        }
    }

    if (isLoading) {
        return <LoadingSpinner/>
    }

    if (failedLoad) {
        return <Alert error="Failed to load profile."/>
    }

    return (
        <div className="container-fluid">
            <div className="d-flex justify-content-between align-items-center m-3">
                <h5>Profile</h5>
            </div>
            <hr/>

            <ChangePasswordModal
                show={showPasswordModal}
                onClose={() => setShowPasswordModal(false)}
                onChangePassword={handleUpdatePassword}
                errorMessage={updatePasswordError}
            />

            <div className="row justify-content-center">
                <div className="col-md-4 col-lg-6 col-sm-6">
                    <div className="mb-3">
                        <LabeledValue
                            label="Email"
                            value={user.email}
                        />
                    </div>
                    <div className="mb-3">
                        <EditableLabeledValue
                            label="First Name"
                            value={user.firstName}
                            onSave={handleUpdateFirstName}
                        />
                    </div>
                    <div className="mb-3">
                        <EditableLabeledValue
                            label="Last Name"
                            value={user.lastName}
                            onSave={handleUpdateLastName}
                        />
                    </div>
                    <div className="d-flex justify-content-end">
                        <button
                            type="button"
                            className="btn btn-sm btn-outline-primary"
                            onClick={() => setShowPasswordModal(true)}
                        >
                            Change Password
                        </button>
                    </div>
                </div>
            </div>
            {errorMessage && (
                <div
                    className="position-fixed bottom-0 end-0 p-3"
                    style={{ zIndex: 1050 }}
                >
                    <div className="alert alert-warning alert-dismissible fade show shadow" role="alert">
                        {errorMessage}
                        <button
                            type="button"
                            className="btn-close"
                            onClick={() => setErrorMessage(null)}
                        />
                    </div>
                </div>
            )}
        </div>
    )
}