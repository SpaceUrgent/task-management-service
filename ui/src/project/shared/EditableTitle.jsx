import React, { useState } from "react";

export default function EditableTitle({ initialValue, onSave, editable = false }) {
    const [isEditing, setIsEditing] = useState(false);
    const [value, setValue] = useState(initialValue);
    const [submitError, setSubmitError] = useState("");
    const [isLoading, setIsLoading] = useState(false);

    const startEditing = () => {
        setIsEditing(true);
        setSubmitError("");
    };

    const handleChange = (e) => {
        setValue(e.target.value);
        setSubmitError("");
    };

    const handleSaveChanges = async (e) => {
        e.preventDefault();
        if (!value.trim()) {
            setSubmitError("Title cannot be empty");
            return;
        }
        if (value === initialValue) {
            setIsEditing(false);
            return;
        }
        setIsLoading(true);
        setSubmitError("");
        try {
            await onSave(value);
            setIsEditing(false);
        } catch (error) {
            setSubmitError("Failed to save title");
        } finally {
            setIsLoading(false);
        }
    };

    const handleCancelChanges = (e) => {
        e.preventDefault();
        setValue(initialValue);
        setIsEditing(false);
        setSubmitError("");
    };

    if (isEditing) {
        return (
            <div className="d-flex align-items-center w-100 mb-1">
                <input
                    type="text"
                    className={`form-control form-control-sm flex-grow-1 me-2${submitError ? ' is-invalid' : ''}`}
                    value={value}
                    onChange={handleChange}
                    autoFocus
                    disabled={isLoading}
                />
                <div className="d-flex gap-2">
                    <button
                        className="btn btn-outline-secondary btn-sm"
                        onClick={handleSaveChanges}
                        disabled={!value.trim() || value === initialValue || isLoading}
                    >
                        {isLoading ? 'Saving...' : 'Save'}
                    </button>
                    <button
                        className="btn btn-outline-danger btn-sm"
                        onClick={handleCancelChanges}
                        disabled={isLoading}
                    >
                        Cancel
                    </button>
                </div>
                {submitError && (
                    <div className="invalid-feedback d-block">{submitError}</div>
                )}
            </div>
        );
    }

    return (
        <div className="d-flex align-items-center w-100 mb-1">
            <h5 className="ms-2 mb-0">{value}</h5>
            {editable &&
                <button
                    className="btn btn-toolbar btn-sm"
                    onClick={startEditing}
                    aria-label="Edit title"
                    title="Edit title"
                >
                    <img
                        src="/icons/pencil.svg"
                        alt="Edit"
                        width="15"
                        height="15"
                    />
                </button>
            }
        </div>
    );
}