import React, { useEffect, useState} from "react";
import CreateProjectModal from "./modal/CreateProjectModal";
import {ProjectClient} from "../api/ProjectClient.ts";
import ProjectPreview from "./ProjectPreview";
import LoadingSpinner from "../../common/components/LoadingSpinner";
import Alert from "../../common/components/Alert";

export default function AvailableProjects() {
    const projectClient = ProjectClient.getInstance();

    const [projects, setProjects] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [createNewModalIsOpen, setCreateNewModalIsOpen] = useState(false);

    useEffect(() => {
        fetchProjects();
    }, []);

    const fetchProjects = async () => {
        try {
            setLoading(true);
            const data = await projectClient.getAvailableProjects();
            console.log(data);
            setProjects(data);
        } catch (error) {
            console.log(error);
            setError('Failed to load projects.')
        } finally {
            setLoading(false);
        }
    }

    if (loading) {
        return (
            <LoadingSpinner/>
        );
    }
    if (error) {
        return (
            <Alert error={error}/>
        );
    }

    return (
        <section className="container-fluid">
                {createNewModalIsOpen &&
                    <CreateProjectModal
                        onClose={() => setCreateNewModalIsOpen(false)}
                        onSubmit={() => fetchProjects()}
                    />
                }
            <div className="d-flex justify-content-between align-items-center m-3">
                <h5>Available Projects</h5>
                <button className="btn btn-sm btn-primary"
                        onClick={() => setCreateNewModalIsOpen(true)}
                >
                    Create New
                </button>
            </div>
            <hr/>

            {projects.length === 0 ? (
                <p>No projects available.</p>
            ) : (
                <ul className="row row-cols-2 p-0">
                    {projects.map((projectPreview) => (
                        <ProjectPreview projectPreview={projectPreview}/>
                    ))}
                </ul>
            )}
        </section>
    );
};