import React, {useState} from "react";

export default function EditableTitle({
                                          initialValue,
                                          onSave
}) {
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

    return (
        <div className="d-flex align-items-center w-100">
            {isEditing ? (
                <>
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
                </>
            ) : (
                <>
                    <h4 className="flex-grow-1 m-0">{value}</h4>
                    <button
                        className="btn btn-outline-secondary btn-sm ms-2"
                        onClick={startEditing}
                    >
                        <img
                            src="/icons/pencil-square.svg"
                            alt="Edit"
                            width="18"
                            height="18"
                        />
                    </button>
                </>
            )}
        </div>
    );
}