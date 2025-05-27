import React, {useState} from "react";

export default function EditableLabeledValue({ label, value, onSave }) {
    const [isEditing, setIsEditing] = useState(false);
    const [editedValue, setEditedValue] = useState(value);

    const handleCancel = () => {
        setEditedValue(value);
        setIsEditing(false);
    };

    const handleSave = () => {
        if (editedValue !== value) {
            onSave(editedValue);
        }
        setIsEditing(false);
    };

    return (
        <div className="input-group input-group-sm">
            <label className="input-group-text" htmlFor={`field-${label}`}>{label}</label>
            <input
                id={`field-${label}`}
                className="form-control"
                value={isEditing ? editedValue : value}
                disabled={!isEditing}
                onChange={(e) => setEditedValue(e.target.value)}
            />
            {!isEditing ? (
                <button
                    className="btn btn-outline-secondary"
                    type="button"
                    onClick={() => setIsEditing(true)}
                >
                    Edit
                </button>
            ) : (
                <>
                    <button className="btn btn-outline-primary" type="button" onClick={handleSave}>
                        Save
                    </button>
                    <button className="btn btn-outline-danger" type="button" onClick={handleCancel}>
                        Cancel
                    </button>
                </>
            )}
        </div>
    );
}