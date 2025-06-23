import React, { useEffect, useRef, useState } from "react";

export default function LabeledAssigneeMultiValueSelector({ label, members, selectedAssignees, unassigned, onChange }) {
    const [tempSelected, setTempSelected] = useState(selectedAssignees || []);
    const [tempUnassigned, setTempUnassigned] = useState(unassigned || false);
    const dropdownRef = useRef(null);

    // Sync local state with props when they change
    useEffect(() => {
        setTempSelected(selectedAssignees || []);
        setTempUnassigned(unassigned || false);
    }, [selectedAssignees, unassigned]);

    // On dropdown close, commit selection to parent
    useEffect(() => {
        const dropdownEl = dropdownRef.current;
        if (!dropdownEl) return;
        const handleDropdownHide = () => {
            onChange(tempSelected, tempUnassigned);
        };
        dropdownEl.addEventListener("hide.bs.dropdown", handleDropdownHide);
        return () => {
            dropdownEl.removeEventListener("hide.bs.dropdown", handleDropdownHide);
        };
    }, [tempSelected, tempUnassigned, onChange]);

    const toggleAssignee = (id) => {
        setTempSelected(prev =>
            prev.includes(id)
                ? prev.filter(a => a !== id)
                : [...prev, id]
        );
    };

    const toggleUnassigned = () => {
        setTempUnassigned(prev => !prev);
    };

    // Display text
    let displayText = "None selected";
    if (tempUnassigned && tempSelected.length > 0) {
        displayText = `Unassigned + ${tempSelected.length} selected`;
    } else if (tempUnassigned) {
        displayText = "Unassigned";
    } else if (tempSelected.length === members.length) {
        displayText = "All selected";
    } else if (tempSelected.length > 0) {
        displayText = `${tempSelected.length} selected`;
    }

    return (
        <div className="input-group input-group-sm">
            {label && <label className="input-group-text" htmlFor={label}>{label}</label>}
            <div className="btn-group" ref={dropdownRef}>
                <button
                    className="btn btn-sm btn-outline-secondary dropdown-toggle"
                    type="button"
                    id="assigneeDropdown"
                    data-bs-toggle="dropdown"
                    data-bs-auto-close="outside"
                    aria-expanded="false"
                >
                    {displayText}
                </button>
                <ul
                    className="dropdown-menu p-2"
                    aria-labelledby="assigneeDropdown"
                    style={{ minWidth: '250px' }}
                    onClick={e => e.stopPropagation()}
                >
                    <li className="form-check" onClick={e => e.stopPropagation()}>
                        <input
                            className="form-check-input"
                            type="checkbox"
                            id="unassigned"
                            checked={tempUnassigned}
                            onChange={toggleUnassigned}
                            onClick={e => e.stopPropagation()}
                        />
                        <label className="form-check-label ms-1" htmlFor="unassigned">
                            Unassigned
                        </label>
                    </li>
                    {members.map(member => (
                        <li key={member.id} className="form-check" onClick={e => e.stopPropagation()}>
                            <input
                                className="form-check-input"
                                type="checkbox"
                                id={`assignee-${member.id}`}
                                checked={tempSelected.includes(Number(member.id))}
                                onChange={() => toggleAssignee(Number(member.id))}
                                onClick={e => e.stopPropagation()}
                            />
                            <label className="form-check-label ms-1" htmlFor={`assignee-${member.id}`}>
                                {member.fullName}
                            </label>
                        </li>
                    ))}
                </ul>
            </div>
        </div>
    );
}