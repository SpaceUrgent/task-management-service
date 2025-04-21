import React, {useState} from "react";

export default function EditableDescription({
    initialValue, onSave
}) {

    const [ isEditing, setIsEditing ] = useState(false);
    const [ description, setDescription ] = useState(initialValue);

    const startEditing = () => {
        setIsEditing(true);
    }

    const handleChangeDescription = (e) => {
        setDescription(e.target.value);
    }

    const handleSubmitChanges = (e) => {
        e.preventDefault();
        if (description !== initialValue) {
            onSave(description);
        }
        setIsEditing(false);
    }

    const handleCancelEditing = (e) => {
        e.preventDefault();
        setDescription(initialValue);
        setIsEditing(false);
    }

    return (
        <div className="row mt-3">
            <div className="d-flex justify-content-between align-items-center mb-2">
                <label className="form-label m-0">Description</label>
                {!isEditing ? (
                    <button
                        className="btn btn-outline-secondary btn-sm"
                        onClick={startEditing}
                    >
                        Modify
                    </button>
                ) : (
                    <div className="d-flex gap-2">
                        <button
                            className="btn btn-outline-secondary btn-sm"
                            onClick={handleSubmitChanges}
                        >
                            Save
                        </button>
                        <button
                            className="btn btn-outline-danger btn-sm"
                            onClick={handleCancelEditing}
                        >
                            Cancel
                        </button>
                    </div>
                )}
            </div>
            <textarea
                className="form-control"
                style={{ resize: "none" }}
                value={description}
                rows={8}
                readOnly={!isEditing}
                onChange={handleChangeDescription}
            />
        </div>
    )
}