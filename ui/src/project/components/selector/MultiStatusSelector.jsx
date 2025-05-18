import React, {useEffect, useRef, useState} from "react";

export default function MultiStatusSelector({ statuses, selected, onChange }) {

    const [tempSelected, setTempSelected] = useState(selected);
    const dropdownRef = useRef(null);

    const toggleStatus = (status) => {
        setTempSelected(prev =>
            prev.includes(status)
                ? prev.filter(s => s !== status)
                : [...prev, status]
        );
    };

    useEffect(() => {
        const dropdownEl = dropdownRef.current;
        if (!dropdownEl) return;

        const handleDropdownHide = () => {
            if (tempSelected.length === 0) {
                onChange(statuses); // select all if none selected
            } else {
                onChange(tempSelected);
            }
        };

        dropdownEl.addEventListener("hide.bs.dropdown", handleDropdownHide);
        return () => {
            dropdownEl.removeEventListener("hide.bs.dropdown", handleDropdownHide);
        };
    }, [tempSelected]);

    useEffect(() => {
        setTempSelected(selected);
    }, [selected]);

    return (
        <div className="btn-group" ref={dropdownRef}>
            <button
                className="btn btn-outline-secondary dropdown-toggle"
                type="button"
                id="statusDropdown"
                data-bs-toggle="dropdown"
                data-bs-auto-close="outside"
                aria-expanded="false"
            >
                {selected.length === statuses.length
                    ? "All Statuses"
                    : selected.length === 0
                        ? "None Selected"
                        : `${selected.length} selected`}
            </button>
            <ul
                className="dropdown-menu p-2"
                aria-labelledby="statusDropdown"
                style={{ minWidth: '250px' }}
            >
                {statuses.map(status => (
                    <li key={status} className="form-check">
                        <input
                            className="form-check-input"
                            type="checkbox"
                            id={`status-${status}`}
                            checked={tempSelected.includes(status)}
                            onChange={() => toggleStatus(status)}
                        />
                        <label className="form-check-label ms-1" htmlFor={`status-${status}`}>
                            {status}
                        </label>
                    </li>
                ))}
            </ul>
        </div>
    );
}