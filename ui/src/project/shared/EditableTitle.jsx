import React, {useState} from "react";

export default function EditableTitle({ initialValue, onSave, editable = false }) {
    const [isEditing, setIsEditing] = useState(false);
    const [ value, setValue ] = useState(initialValue);

    const startEditing = () => {
        setIsEditing(true);
    }

    const handleChange = (e) => {
        const newValue = e.target.value;
        console.log(newValue);
        setValue(newValue);
    }

    const handleSaveChanges = (e) => {
        e.preventDefault();

        onSave(value);
        setIsEditing(false);
    }

    const handleCancelChanges = (e) => {
        e.preventDefault();
        setValue(initialValue);
        setIsEditing(false);
    }

    if (isEditing) {
        return (
            <div className="d-flex align-items-center w-100 mb-1">
                <input
                    type="text"
                    className="form-control form-control-sm flex-grow-1 me-2"
                    value={value}
                    onChange={handleChange}
                    autoFocus
                />
                <div className="d-flex gap-2">
                    <button
                        className="btn btn-outline-secondary btn-sm"
                        onClick={handleSaveChanges}
                    >
                        Save
                    </button>
                    <button
                        className="btn btn-outline-danger btn-sm"
                        onClick={handleCancelChanges}
                    >
                        Cancel
                    </button>
                </div>
            </div>
        )
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
    )
}