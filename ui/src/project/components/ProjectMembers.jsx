import React, {useState} from "react";
import AddMemberModal from "./modal/AddMemberModal";
import {useProjectContext} from "../contexts/ProjectContext";

export default function ProjectMembers() {
    const { members, refreshData } = useProjectContext();

    const [addMemberModalIsOpen, setAddMemberModalIsOpen] = useState(false);

    const handleAddMember = () => {
        setAddMemberModalIsOpen(false);
        refreshData();
    }

    return(
        <div className="container p-3">
            {addMemberModalIsOpen &&
                <AddMemberModal
                    onClose={() => setAddMemberModalIsOpen(false)}
                    onSubmit={handleAddMember}
                />
            }
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h5 className="mb-0">Project Members</h5>
                <button className="btn btn-primary" onClick={() => setAddMemberModalIsOpen(true)}>
                    Add Member
                </button>
            </div>

            {members.length === 0 ? (
                <p className="text-muted">No members added yet.</p>
            ) : (
                <div className="list-group">
                    {members.map((member, index) => (
                        <div
                            key={index}
                            className="list-group-item list-group-item-action d-flex flex-column"
                        >
                            <strong>{member.fullName}</strong>
                            <small className="text-muted">{member.email}</small>
                        </div>
                    ))}
                </div>
            )}
        </div>
    )
}