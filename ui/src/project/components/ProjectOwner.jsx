import React, {useState} from "react";
import {useProjectContext} from "../contexts/ProjectContext";
import ChangeProductOwnerModal from "./modal/ChangeProductOwnerModal";

export default function ProjectOwner({}) {
    const {project, currentUserRole, refreshData} = useProjectContext();

    const [openChangeOwnerModal, setOpenChangeOwnerModal] = useState(false);
    const [newStatusPosition] = useState(null);

    const handleChangeOwner = () => {
        setOpenChangeOwnerModal(true);
        refreshData();
    }

    return(
        <div className="row">
            {openChangeOwnerModal &&
                <ChangeProductOwnerModal
                    statusPosition={newStatusPosition}
                    onClose={handleChangeOwner}
                />
            }
            <div className="d-flex justify-content-between align-items-center mb-2">
                <h6 className="form-label m-0">Project owner</h6>
                {currentUserRole === "Owner" &&
                    <button
                        className="btn btn-outline-secondary btn-sm"
                        onClick={() => setOpenChangeOwnerModal(true)}
                    >
                        Change
                    </button>
                }
            </div>
            <hr/>
            <div className="row mb-3">
                <div className="col">
                    <a>Name: {project.owner?.fullName}</a>
                </div>
                <div className="col">
                    <a>Email: {project.owner?.email}</a>
                </div>
            </div>
        </div>
    );
}