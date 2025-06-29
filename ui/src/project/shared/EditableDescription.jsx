import React, { useState } from "react";

export default function EditableDescription({
    initialValue, onSave, allowEdit = false
}) {
    const [isEditing, setIsEditing] = useState(false);
    const [description, setDescription] = useState(initialValue);
    const [submitError, setSubmitError] = useState("");
    const [isLoading, setIsLoading] = useState(false);

    const startEditing = () => {
        setIsEditing(true);
        setSubmitError("");
    };

    const handleChangeDescription = (e) => {
        setDescription(e.target.value);
        setSubmitError("");
    };

    const handleSubmitChanges = async (e) => {
        e.preventDefault();
        if (!description.trim()) {
            setSubmitError("Description cannot be empty");
            return;
        }
        if (description === initialValue) {
            setIsEditing(false);
            return;
        }
        setIsLoading(true);
        setSubmitError("");
        try {
            await onSave(description);
            setIsEditing(false);
        } catch (error) {
            setSubmitError("Failed to save description");
        } finally {
            setIsLoading(false);
        }
    };

    const handleCancelEditing = (e) => {
        e.preventDefault();
        setDescription(initialValue);
        setIsEditing(false);
        setSubmitError("");
    };

    const editButtons = () => {
        if (isEditing) {
            return (
                <div className="d-flex gap-2">
                    <button
                        className="btn btn-outline-secondary btn-sm"
                        onClick={handleSubmitChanges}
                        disabled={!description.trim() || description === initialValue || isLoading}
                    >
                        {isLoading ? 'Saving...' : 'Save'}
                    </button>
                    <button
                        className="btn btn-outline-danger btn-sm"
                        onClick={handleCancelEditing}
                        disabled={isLoading}
                    >
                        Cancel
                    </button>
                </div>
            );
        }
        return (
            <button
                className="btn btn-outline-secondary btn-sm"
                style={{ width: "65px" }}
                onClick={startEditing}
            >
                Edit
            </button>
        );
    };

    return (
        <div className="row mt-3">
            <div className="d-flex justify-content-between align-items-center mb-2">
                <h6 className="form-label m-0">Description</h6>
                {allowEdit && editButtons()}
            </div>
            <hr />
            <textarea
                className={`form-control${submitError ? ' is-invalid' : ''}`}
                style={{ resize: "none" }}
                value={description}
                rows={8}
                readOnly={!isEditing}
                onChange={handleChangeDescription}
            />
            {submitError && (
                <div className="invalid-feedback d-block">{submitError}</div>
            )}
        </div>
    );
}