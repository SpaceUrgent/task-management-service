import React, {useState} from "react";
import AddMemberModal from "./modal/AddMemberModal";
import {useProjectContext} from "../contexts/ProjectContext";
import PromoteMemberModal from "./modal/PromoteMemberModal";
import LeaveProjectModal from "./modal/LeaveProjectModal";
import ExcludeMemberModal from "./modal/ExcludeMemberModal";
import {ProjectClient} from "../api/ProjectClient.ts";
import {useNavigate} from "react-router-dom";

export default function ProjectMembers() {
    const { project, currentUserRole, refreshData } = useProjectContext();
    const projectClient = ProjectClient.getInstance();
    const navigate = useNavigate();

    const [addMemberModalIsOpen, setAddMemberModalIsOpen] = useState(false);
    const [leaveProjectModalIsOpen, setLeaveProjectModalIsOpen] = useState(false);
    const [excludeMemberModalIsOpen, setExcludeMemberModalIsOpen] = useState(false);
    const [memberToExclude, setMemberToExclude] = useState(null);

    const [promoteToAddMemberModalIsOpen, setPromoteToAddMemberModalIsOpen] = useState(false);
    const [promotedMember, setPromotedMember] = useState(null);

    const [alert, setAlert] = useState(null);

    const handleAddMember = () => {
        setAddMemberModalIsOpen(false);
        refreshData();
    }

    const handleLeaveProject = async () => {
        try {
            await projectClient.leaveProject(project.id);
            setLeaveProjectModalIsOpen(false);
            // Redirect to projects list after leaving
            navigate('/projects');
        } catch (error) {
            setAlert("Failed to leave project");
            setTimeout(() => setAlert(null), 10000);
        }
    }

    const handleExcludeMember = async () => {
        if (!memberToExclude) return;
        try {
            await projectClient.excludeMember(project.id, memberToExclude.id);
            setExcludeMemberModalIsOpen(false);
            setMemberToExclude(null);
            refreshData();
        } catch (error) {
            setAlert("Failed to exclude member");
            setTimeout(() => setAlert(null), 10000);
        }
    }

    const selectPromotedMember = (member) => {
        setPromotedMember(member);
        setPromoteToAddMemberModalIsOpen(true);
        console.log(member);
    }

    const selectMemberToExclude = (member) => {
        setMemberToExclude(member);
        setExcludeMemberModalIsOpen(true);
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

    // Check if current user can exclude a specific member
    const canExcludeMember = (member) => {
        if (currentUserRole === 'Owner') return true;
        if (currentUserRole === 'Admin' && !member.role && member.role !== 'Owner') return true;
        return false;
    }

    return(
        <div>
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
            {leaveProjectModalIsOpen &&
                <LeaveProjectModal
                    onClose={() => setLeaveProjectModalIsOpen(false)}
                    onSubmit={handleLeaveProject}
                />
            }
            {excludeMemberModalIsOpen && memberToExclude &&
                <ExcludeMemberModal
                    member={memberToExclude}
                    onClose={() => {
                        setExcludeMemberModalIsOpen(false);
                        setMemberToExclude(null);
                    }}
                    onSubmit={handleExcludeMember}
                />
            }
            {promoteToAddMemberModalIsOpen &&
                <PromoteMemberModal
                    onClose={() => setPromoteToAddMemberModalIsOpen(false)}
                    onSubmit={handlePromoteMember}
                />
            }
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h6 className="mb-0">Project Members</h6>
                <div className="d-flex gap-2">
                    <button className="btn btn-sm btn-primary" onClick={() => setAddMemberModalIsOpen(true)}>
                        Add Member
                    </button>
                    {currentUserRole !== 'Owner' && (
                        <button 
                            className="btn btn-sm btn-outline-danger" 
                            onClick={() => setLeaveProjectModalIsOpen(true)}
                        >
                            Leave Project
                        </button>
                    )}
                </div>
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
                                    <span className="badge bg-secondary">
                                        {member.role}
                                    </span>
                                </div>
                                <small className="text-muted">{member.email}</small>
                            </div>
                            <div className="d-flex gap-2">
                                {!member.role && currentUserRole === 'Owner' &&
                                    <button
                                        className="btn btn-sm btn-outline-primary"
                                        onClick={() => selectPromotedMember(member)}
                                    >
                                        Promote to Admin
                                    </button>
                                }
                                {member.role === "Admin" && currentUserRole === 'Owner' &&
                                    <button
                                        className="btn btn-sm btn-outline-danger"
                                        onClick={() => handleRevokeAdmin(member)}
                                    >
                                        Revoke Admin
                                    </button>
                                }
                                {canExcludeMember(member) &&
                                    <button
                                        className="btn btn-sm btn-outline-danger"
                                        onClick={() => selectMemberToExclude(member)}
                                    >
                                        Exclude
                                    </button>
                                }
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    )
}