import React, {useState} from "react";

export default function EditableDescription({
    initialValue, onSave, allowEdit = false
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

    const editButtons = () => {
        if (isEditing) {
            return (
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
            )
        }
        return (
            <button
                className="btn btn-outline-secondary btn-sm"
                style={{width : "65px"}}
                onClick={startEditing}
            >
                Edit
            </button>
        )
    }

    return (
        <div className="row mt-3">
            <div className="d-flex justify-content-between align-items-center mb-2">
                <h6 className="form-label m-0">Description</h6>
                {allowEdit && editButtons()}
            </div>
            <hr/>
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