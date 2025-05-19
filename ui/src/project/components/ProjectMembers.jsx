import React, {useState} from "react";
import AddMemberModal from "./modal/AddMemberModal";
import {useProjectContext} from "../contexts/ProjectContext";
import PromoteMemberModal from "./modal/PromoteMemberModal";
import {ProjectClient} from "../api/ProjectClient.ts";

export default function ProjectMembers() {
    const { project, currentUserRole, refreshData } = useProjectContext();
    const projectClient = ProjectClient.getInstance();

    const [addMemberModalIsOpen, setAddMemberModalIsOpen] = useState(false);

    const [promoteToAddMemberModalIsOpen, setPromoteToAddMemberModalIsOpen] = useState(false);
    const [promotedMember, setPromotedMember] = useState(null);

    const [alert, setAlert] = useState(null);

    const handleAddMember = () => {
        setAddMemberModalIsOpen(false);
        refreshData();
    }

    const selectPromotedMember = (member) => {
        setPromotedMember(member);
        setPromoteToAddMemberModalIsOpen(true);
        console.log(member);
    }

    const handlePromoteMember = async () => {
        if (!promotedMember) return;
        try {
            await projectClient.updateMemberRole(project.id, promotedMember.id, 'Admin');
            refreshData();
        } catch (error) {
            setAlert("Failed to update promote member");
            setTimeout(() => setAlert(null), 10000);
        } finally {
            setPromoteToAddMemberModalIsOpen(false);
            setPromotedMember(null);
        }
    }

    const handleRevokeAdmin = async (member) => {
        if (!member) return;
        try {
            await projectClient.updateMemberRole(project.id, member.id, null);
            refreshData();
        } catch (error) {
            setAlert("Failed to revoke admin");
            setTimeout(() => setAlert(null), 10000);
        }
    }

    return(
        <div className="container p-3">
            {alert && (
                <div className="alert alert-danger" role="alert">
                    {alert}
                </div>
            )}
            {addMemberModalIsOpen &&
                <AddMemberModal
                    onClose={() => setAddMemberModalIsOpen(false)}
                    onSubmit={handleAddMember}
                />
            }
            {promoteToAddMemberModalIsOpen &&
                <PromoteMemberModal
                    onClose={() => setPromoteToAddMemberModalIsOpen(false)}
                    onSubmit={handlePromoteMember}
                />
            }
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h5 className="mb-0">Project Members</h5>
                <button className="btn btn-primary" onClick={() => setAddMemberModalIsOpen(true)}>
                    Add Member
                </button>
            </div>

            {project?.members.length === 0 ? (
                <p className="text-muted">No members added yet.</p>
            ) : (
                <div className="list-group">
                    {project?.members.map((member, index) => (
                        <div
                            key={index}
                            className="list-group-item list-group-item-action d-flex justify-content-between align-items-center"
                        >
                            <div className="d-flex flex-column">
                                <div className="d-flex align-items-center">
                                    <strong className="me-2">{member.fullName}</strong>
                                    <span className="badge bg-secondary text-uppercase">
                                        {member.role}
                                    </span>
                                </div>
                                <small className="text-muted">{member.email}</small>
                            </div>
                            {!member.role && currentUserRole === 'OWNER' &&
                                <button
                                    className="btn btn-sm btn-outline-primary"
                                    onClick={() => selectPromotedMember(member)}
                                >
                                    Promote to Admin
                                </button>
                            }
                            {member.role === "ADMIN" && currentUserRole === 'OWNER' &&
                                <button
                                    className="btn btn-sm btn-outline-danger"
                                    onClick={() => handleRevokeAdmin(member)}
                                >
                                    Revoke Admin
                                </button>
                            }
                        </div>
                    ))}
                </div>
            )}
        </div>
    )
}