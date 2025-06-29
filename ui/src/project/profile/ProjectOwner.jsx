import React, {useState} from "react";
import {useProjectContext} from "../contexts/ProjectContext";
import ChangeProjectOwnerModal from "./modal/ChangeProjectOwnerModal";
import LabeledValue from "../../shared/components/LabeledValue";

export default function ProjectOwner({ allowChangeOwner = false }) {
    const {project, refreshData} = useProjectContext();

    const [openChangeOwnerModal, setOpenChangeOwnerModal] = useState(false);
    const [newStatusPosition] = useState(null);

    const handleChangeOwner = () => {
        setOpenChangeOwnerModal(true);
        refreshData();
    }

    return(
        <div className="row">
            {openChangeOwnerModal &&
                <ChangeProjectOwnerModal
                    statusPosition={newStatusPosition}
                    onClose={handleChangeOwner}
                />
            }
            <div className="d-flex justify-content-between align-items-center mb-2">
                <h6 className="form-label m-0">Project owner</h6>
                {allowChangeOwner &&
                    <button
                        className="btn btn-outline-secondary btn-sm"
                        onClick={() => setOpenChangeOwnerModal(true)}
                    >
                        Change
                    </button>
                }
            </div>
            <hr/>
            <div className="row">
                <div className="col">
                    <LabeledValue label="Name" value={project.owner?.fullName}/>
                </div>
                <div className="col">
                    <LabeledValue label="Email" value={project.owner?.email}/>
                </div>
            </div>
        </div>
    );
}