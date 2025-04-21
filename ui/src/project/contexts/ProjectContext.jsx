import {createContext, useCallback, useContext, useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import {ProjectClient} from "../api/ProjectClient.ts";

export const ProjectContext = createContext({
    project: null,
    members: [],
    refreshData: () => {}
});

export const ProjectContextProvider = ({ children }) => {
    const { projectId } = useParams();
    const projectClient = ProjectClient.getInstance();

    const [ project, setProject ] = useState(null);
    const [ members, setMembers ] = useState(null);
    const [ isLoading, setIsLoading ] = useState(true);

    const fetchProjectData = useCallback(async () => {
        setIsLoading(true);
        try {
            const [project, members] = await Promise.all([
                projectClient.getProjectDetails(projectId),
                projectClient.getProjectMembers(projectId),
            ]);
            setProject(project);
            setMembers(members);
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
        <ProjectContext.Provider value={{ project, members, refreshData: fetchProjectData }}>
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