import {createContext, useCallback, useContext, useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import {ProjectClient} from "../api/ProjectClient.ts";

export const ProjectContext = createContext({
    currentUserRole: null,
    project: null,
    refreshData: () => {}
});

export const ProjectContextProvider = ({ children }) => {
    const { projectId } = useParams();
    const projectClient = ProjectClient.getInstance();

    const [ currentUserRole, setCurrentUserRole ] = useState(null);
    const [ project, setProject ] = useState(null);
    const [ isLoading, setIsLoading ] = useState(true);

    const fetchProjectData = useCallback(async () => {
        setIsLoading(true);
        try {
            const data = await projectClient.getProjectDetails(projectId);
            setProject(data.projectDetails);
            setCurrentUserRole(data.role);
        } catch (e) {
            console.error(e);
        } finally {
            setIsLoading(false);
        }
    }, [projectId]);

    useEffect(() => {
        fetchProjectData();
    }, [fetchProjectData]);

    if (isLoading) return <div>Loading...</div>;
    if (!project) return <div className="alert alert-danger">Project not found</div>;

    return (
        <ProjectContext.Provider value={{ currentUserRole, project, refreshData: fetchProjectData }}>
            {children}
        </ProjectContext.Provider>
    )
}

export const useProjectContext = () => {
    const context = useContext(ProjectContext);
    if (!context) {
        throw new Error('useProjectContext must be used within project context');
    }
    return context;
};